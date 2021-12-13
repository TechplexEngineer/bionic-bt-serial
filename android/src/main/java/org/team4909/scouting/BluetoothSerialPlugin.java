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


import java.nio.charset.StandardCharsets;
import java.util.Set;

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

             JSObject ret = new JSObject();

             switch (msg.what) {
                 case BluetoothSerial.MESSAGE_READ_RAW:
                     ret.put("bytes", msg.obj);
                     notifyListeners("data", ret);
                    break;
                case BluetoothSerial.MESSAGE_CONNECTED_TO_DEVICE:
                    BluetoothDevice device = (BluetoothDevice)msg.obj;
                    Log.i(TAG, "connected to " + device.getAddress());

                    notifyListeners("connected", deviceToJSON(device));
                    if (mListenCallback != null) {
                        ret = new JSObject();
                        ret.put("connected", deviceToJSON(device));
                        mListenCallback.resolve(ret);
                    }
                    break;
                case BluetoothSerial.MESSAGE_CONNECTION_FAILED:
                    String msgStr = (String)msg.obj;
                    Log.i(TAG, "Event message " + msgStr);

                    notifyListeners("connectionFailed", ret);
                    break;
                case BluetoothSerial.MESSAGE_CONNECTION_LOST:
                    String msgLost = (String)msg.obj;
                    Log.i(TAG, "Event message " + msgLost);

                    notifyListeners("connectionLost", ret);
                    break;
             }
         }
    };

    private final BluetoothSerial implementation = new BluetoothSerial(mHandler);

    /**
     * Get a list of paired devices
     * {devices: {name:string, id:string, address:string, class?:number}[]}
     */
    @PluginMethod
    public void getBondedDevices(PluginCall call) {
        JSObject ret = new JSObject();
        Set<BluetoothDevice> bondedDevices = implementation.getBondedDevices();
        JSArray deviceList = new JSArray();

        for (BluetoothDevice device : bondedDevices) {
            deviceList.put(deviceToJSON(device));
        }

        ret.put("devices", deviceList);
        call.resolve(ret);
    }
    private JSObject deviceToJSON(BluetoothDevice device) {
        JSObject json = new JSObject();
        json.put("name", device.getName());
        json.put("macAddress", device.getAddress());
        json.put("id", device.getAddress());
        if (device.getBluetoothClass() != null) {
            json.put("class", device.getBluetoothClass().getDeviceClass());
        }
        return json;
    }

    /**
     * Start listening for incoming connections
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
        implementation.stop();
        call.resolve();
    }

    @PluginMethod
    public void isListening(PluginCall call){
        JSObject ret = new JSObject();
        ret.put("result", implementation.isListening());
        call.resolve(ret);
    }

    /**
     * Connect to the specified macAddress
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

    @PluginMethod
    public void getConnectedDevices(PluginCall call){
        JSObject res = new JSObject();
        res.put("result", implementation.getConnectedDevices());
        call.resolve(res);
    }

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
     * Disconnect from connected peer
     */
    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void disconnectAll(PluginCall call){
        implementation.stop();
        call.resolve();
    }

    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void write(PluginCall call){
        String macAddress = call.getString("macAddress");
        byte[] data = call.getObject("data").toString().getBytes(StandardCharsets.UTF_8);

        JSObject ret = new JSObject();
        ret.put("result", implementation.write(macAddress, data));
        call.resolve(ret);
    }


    @PluginMethod
    public void isEnabled(PluginCall call){
        JSObject res = new JSObject();
        res.put("result", BluetoothAdapter.getDefaultAdapter().isEnabled());
        call.resolve(res);
    }

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

    @PluginMethod
    public void disableAdapter(PluginCall call){
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISABLE);
        startActivityForResult(call, intent, "disableBluetoothResult");
    }

    @ActivityCallback
    private void disableBluetoothResult(PluginCall call, ActivityResult result) {
        if (call == null) {
            return;
        }

        if (Activity.RESULT_OK == result.getResultCode()) {
            call.resolve();
            return;
        }
        call.reject("User declined to disable bluetooth");
    }

    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void showBluetoothSettings(PluginCall call){
        Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
        getActivity().startActivity(intent);
        call.resolve();
    }

    /**
     * Discovery lasts for about 12 seconds.
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
                    notifyListeners("discovery", ret);

        final BroadcastReceiver discoverReceiver = new BroadcastReceiver() {

            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    if (call != null) {
                        JSObject ret = new JSObject();
                        JSObject dev = deviceToJSON(device);
                        ret.put("device", dev);
                        call.resolve(ret);

                        ret.put("device", dev);
                        notifyListeners("discovery", ret);
                    }

                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    JSObject ret = new JSObject();
                    ret.put("completed", true);
                    notifyListeners("discovery", ret);
                }
            }
        };

        Activity activity = getActivity();
        activity.registerReceiver(discoverReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        activity.registerReceiver(discoverReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
        BluetoothAdapter.getDefaultAdapter().startDiscovery();
    }

    @PluginMethod
    public void cancelDiscovery(PluginCall call){
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
        call.resolve();
    }

    /**
     * Change the name of this device
     */
    @PluginMethod
    public void setName(PluginCall call){
        String newName = call.getString("name");
        BluetoothAdapter.getDefaultAdapter().setName(newName);
        call.resolve();
    }

    @PluginMethod
    public void getName(PluginCall call){
        JSObject ret = new JSObject();
        ret.put("name", BluetoothAdapter.getDefaultAdapter().getName());
        call.resolve(ret);
    }

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
