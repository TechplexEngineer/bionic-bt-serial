package org.team4909.scouting;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;


import androidx.activity.result.ActivityResult;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.PermissionState;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.ActivityCallback;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;


import org.json.JSONException;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CapacitorPlugin(
        name = "BluetoothSerial",
        permissions = {
                @Permission(
                        alias = "ACCESS_COARSE_LOCATION",
                        strings = {Manifest.permission.ACCESS_COARSE_LOCATION}
                )
        }
)
public class BluetoothSerialPlugin extends Plugin {

    // Debugging
    private static final String TAG = "BluetoothSerialPlugin";

    private PluginCall mListenCallback = null;

    // The Handler that gets information back from the BluetoothSerialService
    // Original code used handler for the because it was talking to the UI.
    // Consider replacing with normal callbacks
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {

         public void handleMessage(Message msg) {

             switch (msg.what) {
                 case BluetoothSerial.MESSAGE_READ_RAW: {
                     JSObject ret = new JSObject();
                     ret.put("bytes", msg.obj);
                     notifyListeners("rawData", ret);
                     break;
                 }
                case BluetoothSerial.MESSAGE_CONNECTED_TO_DEVICE: {
                    BluetoothDevice device = (BluetoothDevice) msg.obj;
                    Log.i(TAG, "connected to " + device.getAddress());

                    notifyListeners("connected", deviceToJSON(device));
                    if (mListenCallback != null) {
                        mListenCallback.resolve(deviceToJSON(device));
                    }
                    break;
                }
                case BluetoothSerial.MESSAGE_CONNECTION_FAILED: {
                    BluetoothDevice device = (BluetoothDevice) msg.obj;

                    notifyListeners("connectionFailed", deviceToJSON(device));

                    Log.i(TAG, "Connection Failed " + device.getAddress());
                    break;
                }
                case BluetoothSerial.MESSAGE_CONNECTION_LOST: {
                    BluetoothDevice device = (BluetoothDevice) msg.obj;

                    notifyListeners("connectionLost", deviceToJSON(device));

                    Log.i(TAG, "Connection Lost " +  device.getAddress());
                    break;
                }
             }
         }
    };

    private final BluetoothSerial implementation = new BluetoothSerial(mHandler);

    /**
     * Get a list of paired devices
     * @result  { result: {name:string, address:string, class?:number}[] }
     */
    @PluginMethod
    public void getBondedDevices(PluginCall call) {
        JSObject ret = new JSObject();
        Set<BluetoothDevice> bondedDevices = implementation.getBondedDevices();
        JSArray deviceList = new JSArray();

        for (BluetoothDevice device : bondedDevices) {
            deviceList.put(deviceToJSON(device));
        }

        ret.put("result", deviceList);
        call.resolve(ret);
    }
    private JSObject deviceToJSON(BluetoothDevice device) {
        JSObject json = new JSObject();
        json.put("name", device.getName());
        json.put("macAddress", device.getAddress());
        if (device.getBluetoothClass() != null) {
            json.put("class", device.getBluetoothClass().getDeviceClass());
        }
        return json;
    }

    /**
     * Start listening for incoming connections
     * @input (null, callback: ListenCallback)
     */
    @PluginMethod(returnType = PluginMethod.RETURN_CALLBACK)
    public void startListening(PluginCall call){
        mListenCallback = call;
        mListenCallback.setKeepAlive(true);
        implementation.startListening();
    }

    /**
     * Stop listening for incoming connections
     */
    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void stopListening(PluginCall call){
        if (mListenCallback != null) {
            bridge.releaseCall(mListenCallback);
        }
        mListenCallback = null;
        implementation.disconnectAll();
        call.resolve();
    }

    /**
     * Check if the listening thread is running
     * @result {result:boolean}
     */
    @PluginMethod
    public void isListening(PluginCall call){
        JSObject ret = new JSObject();
        ret.put("result", implementation.isListening());
        call.resolve(ret);
    }

    /**
     * Connect to the specified macAddress
     * @input (options:{ macAddress:string })
     */
    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void connect(PluginCall call){
        String macAddress = call.getString("macAddress");
        if (macAddress == null) {
            call.reject("macAddress is required");
        }
        BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(macAddress);

        if (device != null) {
            implementation.connect(device);
            call.resolve();
        } else {
            call.reject("Could not connect to " + macAddress);
        }
    }

    /**
     * Promise results in true if device is connected
     * @result {result: boolean}
     */
    @PluginMethod
    public void isConnected(PluginCall call){
        String address = call.getString("macAddress");
        if (address == null) {
            call.reject("macAddress is required");
            return;
        }
        JSObject res = new JSObject();
        res.put("result", implementation.isConnected(address));
        call.resolve(res);
    }

    /**
     * Get a list of connected bluetooth devices
     * @result { result: {name:string, address:string, class?:number}[] }
     */
    @PluginMethod
    public void getConnectedDevices(PluginCall call){
        JSObject res = new JSObject();
        JSArray devices = new JSArray();

        Set<BluetoothDevice> paired = implementation.getBondedDevices();
        Set<String> connected = implementation.getConnectedDevices();
        for (String macAddress : connected) {
            for (BluetoothDevice dev : paired) {
                if (dev.getAddress().equals(macAddress)) {
                    devices.put(deviceToJSON(dev));
                    break; // found the one we need, can skip the rest of the inner loop
                }
            }
        }

        res.put("result", devices);
        call.resolve(res);
    }

    /**
     * Disconnect from the paired device
     * @input (options: { macAddress:string })
     * @result  { result: boolean }
     */
    @PluginMethod
    public void disconnect(PluginCall call) {
        String macAddress = call.getString("macAddress");
        if (macAddress == null) {
            call.reject("mac address is required");
            return;
        }
        JSObject ret = new JSObject();
        ret.put("result", implementation.disconnect(macAddress));
        call.resolve(ret);
    }

    /**
     * Disconnect from all connected devices
     */
    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void disconnectAll(PluginCall call){
        implementation.disconnectAll();
        call.resolve();
    }

    public boolean isMACValid(String mac) {
        // If the string is empty
        // return false
        if (mac == null) {
            return false;
        }

        String regex = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})|([0-9a-fA-F]{4}\\.[0-9a-fA-F]{4}\\.[0-9a-fA-F]{4})$";
        Pattern p = Pattern.compile(regex);

        Matcher m = p.matcher(mac);
        return m.matches();
    }

    /**
     * Disconnect from the paired device
     * @input (options: { macAddress:string, data:UInt8Array })
     * @result  { result:boolean }
     */
    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void write(PluginCall call){
        String macAddress = call.getString("macAddress");
        if (!isMACValid(macAddress)) {
            call.reject("macAddress is required");
            return;
        }

        JSArray data = call.getArray("data");
        if (data.length() < 1) {
            call.reject("array of data is required");
            return;
        }

        try {
            // idea from: https://stackoverflow.com/a/3176237/429544
            List<Byte> byteList = data.toList();
            Byte[] byteArr = byteList.toArray(new Byte[0]);
            byte[] toWrite = toPrimitive(byteArr);

            JSObject ret = new JSObject();
            ret.put("result", implementation.write(macAddress, toWrite));
            call.resolve(ret);
        } catch (JSONException e) {
            call.reject("array of data is required");
        }
    }

    /**
     * Converts an array of object Bytes to primitives.
     * <p>
     * This method returns {@code null} for a {@code null} input array.
     * </p>
     *
     * @param array  a {@code Byte} array, may be {@code null}
     * @return a {@code byte} array, {@code null} if null array input
     * @throws NullPointerException if an array element is {@code null}
     * @src https://github.com/apache/commons-lang/blob/ecf744f6c6be31efe892b6d4c57ad9f39bf280a8/src/main/java/org/apache/commons/lang3/ArrayUtils.java#L9305-L9327
     */
    public static byte[] toPrimitive(final Byte[] array) {
        final byte[] EMPTY_BYTE_ARRAY = {};
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return EMPTY_BYTE_ARRAY;
        }
        final byte[] result = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].byteValue();
        }
        return result;
    }


    /**
     * Check if the bluetooth adapter is enabled
     * @result { result:boolean }
     */
    @PluginMethod
    public void isEnabled(PluginCall call){
        JSObject res = new JSObject();
        res.put("result", BluetoothAdapter.getDefaultAdapter().isEnabled());
        call.resolve(res);
    }

    /**
     * Request that the user enable the bluetooth adapter if its not already enabled
     */
    @PluginMethod
    public void enableAdapter(PluginCall call){
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(call, intent, "enableBluetoothResult");
    }

    @ActivityCallback
    private void enableBluetoothResult(PluginCall call, ActivityResult result) {
        if (call == null) {
            return;
        }

        if (Activity.RESULT_OK == result.getResultCode()) {
            call.resolve();
            return;
        }
        call.reject("User declined to enable bluetooth");
    }

    /**
     * Disable the bluetooth adapter. Does not promt the user.
     */
    @PluginMethod
    public void disableAdapter(PluginCall call){
        BluetoothAdapter.getDefaultAdapter().disable();
        call.resolve();
    }

    /**
     * Show the Bluetooth settings menu for the user.
     */
    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void showBluetoothSettings(PluginCall call){
        Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
        getActivity().startActivity(intent);
        call.resolve();
    }

    /**
     * Discovery lasts for about 12 seconds.
     * @input (null, callback: DiscoveryCallback
     */
    @PluginMethod(returnType = PluginMethod.RETURN_CALLBACK)
    public void startDiscovery(PluginCall call){
        if (getPermissionState("ACCESS_COARSE_LOCATION") != PermissionState.GRANTED) {
            requestPermissionForAlias("ACCESS_COARSE_LOCATION", call, "discoveryPermsCallback");
            return;
        }

        call.setKeepAlive(true);
        discoverDevices(call);
    }
    @PermissionCallback
    private void discoveryPermsCallback(PluginCall call) {
        if (getPermissionState("ACCESS_COARSE_LOCATION") == PermissionState.GRANTED) {
            discoverDevices(call);
        } else {
            call.reject("Permission is required to start discovery");
        }
    }
    private void discoverDevices(PluginCall call) {
        JSObject ret = new JSObject();
                    ret.put("starting", true);
                    notifyListeners("discoveryState", ret);

        final BroadcastReceiver discoverReceiver = new BroadcastReceiver() {

            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    if (call != null) {
                        JSObject dev = deviceToJSON(device);
                        notifyListeners("discovered", dev);
                        call.resolve(dev);
                    }

                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    JSObject ret = new JSObject();
                    ret.put("completed", true);
                    notifyListeners("discoveryState", ret);
                }
            }
        };

        Activity activity = getActivity();
        activity.registerReceiver(discoverReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        activity.registerReceiver(discoverReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
        BluetoothAdapter.getDefaultAdapter().startDiscovery();
    }

    /**
     * Stops any running discovery process.
     */
    @PluginMethod
    public void cancelDiscovery(PluginCall call){
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
        call.resolve();
    }

    /**
     * Sets the name of the bluetooth adapter. Name is what paired devices will see when they connect.
     * @input {name: string}
     */
    @PluginMethod
    public void setName(PluginCall call){
        String newName = call.getString("name");
        if (newName == null || newName.length() < 1) {
            call.reject("name is required");
            return;
        }
        BluetoothAdapter.getDefaultAdapter().setName(newName);
        call.resolve();
    }

    /**
     * Gets the name of the bluetooth adapter.
     * @result { result:string }
     */
    @PluginMethod
    public void getName(PluginCall call){
        JSObject ret = new JSObject();
        ret.put("result", BluetoothAdapter.getDefaultAdapter().getName());
        call.resolve(ret);
    }

    /**
     * Ensure bluetooth is enabled and the device is discoverable to remote scanners.
     * Default durationSec is 120 is not provided. Max is 300 seconds.
     */
    @PluginMethod
    public void setDiscoverable(PluginCall call){
        Integer discoverableDuration = 120;
        try {
            discoverableDuration = call.getInt("durationSec", 120);
        } catch(Exception e) {
            Log.d(TAG, "using default duration "+discoverableDuration);
        }
        Intent discoverIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, discoverableDuration);
        getActivity().startActivity(discoverIntent);
        call.resolve();
    }
}
