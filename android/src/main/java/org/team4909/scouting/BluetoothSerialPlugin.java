package org.team4909.scouting;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
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
    private static final boolean D = true;

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
                case BluetoothSerial.MESSAGE_CONNECTED_TO_DEVICE_NAME:
                    String address = (String)msg.obj;
                    Log.i(TAG, "connected to " + address);
                    ret.put("address", address);
                    notifyListeners("connected", ret);
                    break;
                case BluetoothSerial.MESSAGE_Event:
                    String msgStr = (String)msg.obj;
                    Log.i(TAG, "Event message " + msgStr);
//                    BluetoothSerial.notifyConnectionLost(message);
                    break;
             }
         }
    };

    private BluetoothSerial implementation = new BluetoothSerial(mHandler);

    /**
     * Get a list of paired devices
     * {devices: {name:string, id:string, address:string, class?:number}[]}
     * @param call
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
        json.put("address", device.getAddress());
        json.put("id", device.getAddress());
        if (device.getBluetoothClass() != null) {
            json.put("class", device.getBluetoothClass().getDeviceClass());
        }
        return json;
    }

    /**
     * Start listening for incomming connections
     * @param call
     */
    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void listen(PluginCall call){
        //@todo need callback?
        implementation.startListening();
        call.resolve();
    }

    /**
     * Stop listening for incoming connections
     * @param call
     */
    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void stopListen(PluginCall call){
        implementation.stop();
        call.resolve();
    }


    /**
     * Connect to the specified macAddress
     * @param call
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
     * @param call
     */
    @PluginMethod
    public void isConnected(PluginCall call){
        String address = call.getString("address");
        if (address == null) {
            call.reject("address is required");
            return;
        }
        JSObject res = new JSObject();
        res.put("result", implementation.isConnected(address));
        call.resolve(res);
    }

    /**
     * Disconnect from connected peer
     * @param call
     */
    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void disconnect(PluginCall call){
        implementation.stop();
        call.resolve();
    }

    @PluginMethod
    public void isEnabled(PluginCall call){
        JSObject res = new JSObject();
        res.put("result", BluetoothAdapter.getDefaultAdapter().isEnabled());
        call.resolve(res);
    }

    @PluginMethod
    public void enable(PluginCall call){
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
    public void showBluetoothSettings(PluginCall call){
        Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
        getActivity().startActivity(intent);
        call.resolve();
    }

    @PluginMethod //@todo make this take a callback
    public void startDiscovery(PluginCall call){
        if (getPermissionState("ACCESS_COARSE_LOCATION") != PermissionState.GRANTED) {
            requestPermissionForAlias("ACCESS_COARSE_LOCATION", call, "discoveryPermsCallback");
            return;
        }
        discoverDevices(call);
    }
    @PermissionCallback
    private void discoveryPermsCallback(PluginCall call) {
        if (getPermissionState("camera") == PermissionState.GRANTED) {
            discoverDevices(call);
        } else {
            call.reject("Permission is required to start discovery");
        }
    }
    private void discoverDevices(PluginCall call) {

        call.reject("Not Implemented");

//        final CallbackContext ddc = deviceDiscoveredCallback;
//
//        final BroadcastReceiver discoverReceiver = new BroadcastReceiver() {
//
//            private JSONArray unpairedDevices = new JSONArray();
//
//            public void onReceive(Context context, Intent intent) {
//                String action = intent.getAction();
//                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                    try {
//                    	JSONObject o = deviceToJSON(device);
//                        unpairedDevices.put(o);
//                        if (ddc != null) {
//                            PluginResult res = new PluginResult(PluginResult.Status.OK, o);
//                            res.setKeepCallback(true);
//                            ddc.sendPluginResult(res);
//                        }
//                    } catch (JSONException e) {
//                        // This shouldn't happen, log and ignore
//                        Log.e(TAG, "Problem converting device to JSON", e);
//                    }
//                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
//                    callbackContext.success(unpairedDevices);
//                    cordova.getActivity().unregisterReceiver(this);
//                }
//            }
//        };
//
//        Activity activity = cordova.getActivity();
//        activity.registerReceiver(discoverReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
//        activity.registerReceiver(discoverReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
//        bluetoothAdapter.startDiscovery();
    }

    @PluginMethod
    public void cancelDiscovery(PluginCall call){
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
        call.resolve();
    }

    /**
     * Change the name of this device
     * @param call
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
            discoverableDuration = call.getInt("duration", 120);
        } catch(Exception e) {
            Log.d(TAG, "using default duration "+discoverableDuration);
        }
        Intent discoverIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, discoverableDuration);
        getActivity().startActivity(discoverIntent);
        call.resolve();
    }




}
