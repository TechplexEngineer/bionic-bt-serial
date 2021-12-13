package org.team4909.scouting;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;


/**
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread that listens for
 * incoming connections, a thread for connecting with a device, and a
 * thread for performing data transmissions when connected.
 *
 * This code was based on the Android SDK BluetoothChat Sample
 * $ANDROID_SDK/samples/android-17/BluetoothChat
 * https://github.com/android/connectivity-samples/tree/main/BluetoothChat
 */
public class BluetoothSerial {

    // Debugging
    private static final String TAG = "BluetoothSerial";
    private static final boolean D = true;

    // Message types sent from the BluetoothSerial to the Handler
    public static final int MESSAGE_CONNECTED_TO_DEVICE_NAME = 4;
    public static final int MESSAGE_Event = 5;
    public static final int MESSAGE_READ_RAW = 6;

    // Name for the SDP record when creating server socket
    private static final String SDP_NAME = "BionicScoutSecure";

    // Well known SPP(Serial Port Profile) UUID
    private static final UUID UUID_SPP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Member fields
    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private AcceptThread mAcceptThread; // listen for incoming connections
    private final ConcurrentHashMap<String, ConnectedThread> connections; // support multiple established connections
    private ConnectThread mConnectThread; // we only support attempting to make one connection at a time


    private AtomicBoolean isListening;

    /**
     * Constructor. Prepares a new BluetoothSerial session.
     * @param handler  A Handler to send messages back to the UI Activity
     */
    public BluetoothSerial(Handler handler) {
        isListening.set(false);
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mHandler = handler;
        connections = new ConcurrentHashMap<>();
        isListening = new AtomicBoolean(false);
    }

    // ========================================================================
    // Listening (Accept Incoming Connections) as a server
    // ========================================================================

    /**
     * Start an AcceptThread to begin a session in listening (server) mode.
     */
    public synchronized void startListening() {
        if (D) Log.d(TAG, "startListening");

        // Start the thread to listen on a BluetoothServerSocket
        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }
    }

    /**
     * Stop accepting new connections. Leave existing connections intact.
     */
    public synchronized void stopListening() {
        if (D) Log.d(TAG, "stopListening");

        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }
        isListening.set(false);
    }

    /**
     * @return true if listening for and accepting incoming connections
     */
    public boolean isListening() {
        return isListening.get();
    }

    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class AcceptThread extends Thread {
        // The local server socket
        private final BluetoothServerSocket mmServerSocket;
        private boolean shouldListen = true;

        public AcceptThread() {
            isListening.set(true);
            BluetoothServerSocket tmp = null;

            // Create a new listening server socket
            try {
                tmp = mAdapter.listenUsingRfcommWithServiceRecord(SDP_NAME, UUID_SPP);
            } catch (IOException e) {
                Log.e(TAG, "Socket listen() failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            // this shouldn't happen, but let's handle the case that it does
            if (mmServerSocket == null) {
                isListening.set(false);
                Log.e(TAG, "BT Not Listening, failed to start listening.");
                return;
            }
            if (D) Log.d(TAG, "Socket BEGIN mAcceptThread" + this);
            setName("AcceptThread");

            BluetoothSocket socket;

            // Listen to the server socket
            while (shouldListen) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "Socket accept() failed", e);
                    break;
                }

                if (socket == null) {
                    isListening.set(false);
                    Log.e(TAG, "Socket was closed.");
                    continue;
                }

                // If a connection was accepted
                // Situation normal. Start the connected thread.
                connected(socket, socket.getRemoteDevice());
            }
            if (D) Log.i(TAG, "END mAcceptThread, socket");
        }

        public void cancel() {
            if (D) Log.d(TAG, "Socket cancel " + this);
            shouldListen = false;
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Socket close() of server failed", e);
            }
        }
    }

    // ========================================================================
    // Start an outgoing connection to a different server
    // ========================================================================

    /**
     * Start the ConnectThread to initiate a connection to a remote device that is listening.
     * @param device  The BluetoothDevice to connect
     */
    public synchronized void connect(BluetoothDevice device) {
        if (D) Log.d(TAG, "connect to: " + device);

        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
    }

    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;

            // Get a BluetoothSocket for a connection with the given BluetoothDevice
            try {
                tmp = device.createRfcommSocketToServiceRecord(UUID_SPP);
            } catch (IOException e) {
                Log.e(TAG, "Socket create() failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectThread Socket");
            setName("ConnectThread");

            // Always cancel discovery because it will slow down a connection
            mAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a successful connection or an exception
                Log.i(TAG,"Connecting to socket...");
                mmSocket.connect();
                Log.i(TAG,"Connected");
            } catch (IOException e) {
                Log.e(TAG, e.toString());
                Log.e(TAG, "Couldn't establish a Bluetooth connection.");

                // Send a failure message back to the Activity
                mHandler.obtainMessage(BluetoothSerial.MESSAGE_Event, "Unable to connect to device").sendToTarget();

                // Start the service over to restart listening mode
                BluetoothSerial.this.startListening();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothSerial.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    // ========================================================================
    //
    // ========================================================================

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     * @param socket  The BluetoothSocket on which the connection was made
     * @param device  The BluetoothDevice that has been connected
     */
    private synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        if (D) Log.d(TAG, "connected, Socket");

        // Start the thread to manage the connection and perform transmissions
        ConnectedThread connectedThread = new ConnectedThread(socket);
        connectedThread.start();
        connections.put(device.getAddress(), connectedThread);

        mHandler.obtainMessage(MESSAGE_CONNECTED_TO_DEVICE_NAME, device.getName()).sendToTarget();
    }

    // ========================================================================
    // Manage a connection
    // ========================================================================

    /**
     * Write to the connected device.
     * This should not be called after Stop(), behavior undefined.
     *
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(String address, byte[] out) {
        ConnectedThread r = connections.get(address);
        if (r != null) {
            r.write(out);
        }
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        private boolean isCancelled = false;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytesRead;

            // Keep listening to the InputStream while connected
            while (!isCancelled) {
                try {
                    // Read from the InputStream
                    bytesRead = mmInStream.read(buffer);

                    // Send the raw bytestream to handler
                    // We make a copy because the full array can have extra data at the end
                    // when / if we read less than its size.
                    if (bytesRead > 0) {
                        mHandler.obtainMessage(BluetoothSerial.MESSAGE_READ_RAW, buffer).sendToTarget();
                    }

                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);

                    // Send a failure message back to the Activity
                    mHandler.obtainMessage(BluetoothSerial.MESSAGE_Event, "Device connection was lost").sendToTarget();

                    // Start the service over to restart listening mode
                    BluetoothSerial.this.startListening();
                    break;
                }
            }
            // mark self for deletion @todo
        }

        /**
         * Write to the connected OutStream.
         * @param buffer  The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            isCancelled = true;
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    /**
     * Stop all threads
     */
    public synchronized void stop() {
        if (D) Log.d(TAG, "stop");

        // stop accepting new connections
        stopListening();

        // cancel any in progress connections
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // close all current connections
        if (connections.size() > 0) {
            for (Map.Entry<String, ConnectedThread> connection : connections.entrySet()) {
                connection.getValue().cancel();
            }// this seems like it could be buggy if an accept thread is started concurrently with this stop
            connections.clear();
        }
    }

    public boolean isConnected(String address) {
        return connections.containsKey(address);
    }

    /**
     * Accessor for list of paired (bonded) devices.
     * May return devices that are out of range, or have bluetooth turned off.
     *
     * @return a set of bluetooth devices the current device is paired with.
     */
    public Set<BluetoothDevice> getBondedDevices() {
        return mAdapter.getBondedDevices();
    }

}
