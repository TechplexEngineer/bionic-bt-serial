package org.team4909.scouting;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;



import java.util.Set;

@CapacitorPlugin(name = "BluetoothSerial")
public class BluetoothSerialPlugin extends Plugin {

    // Debugging
    private static final String TAG = "BluetoothSerialPlugin";
    private static final boolean D = true;

    StringBuffer buffer = new StringBuffer();

    // The Handler that gets information back from the BluetoothSerialService
    // Original code used handler for the because it was talking to the UI.
    // Consider replacing with normal callbacks
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {

         public void handleMessage(Message msg) {
             switch (msg.what) {
                 case BluetoothSerial.MESSAGE_READ:
                    buffer.append((String)msg.obj);

//                    if (dataAvailableCallback != null) {
//                        sendDataToSubscriber();
//                    }

                    break;
                 case BluetoothSerial.MESSAGE_READ_RAW:
//                    if (rawDataAvailableCallback != null) {
//                        byte[] bytes = (byte[]) msg.obj;
//                        sendRawDataToSubscriber(bytes);
//                    }
                    break;
                 case BluetoothSerial.MESSAGE_STATE_CHANGE:

                    if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothSerial.STATE_CONNECTED:
                            Log.i(TAG, "BluetoothSerial.STATE_CONNECTED");
//                            notifyConnectionSuccess();
                            break;
                        case BluetoothSerial.STATE_CONNECTING:
                            Log.i(TAG, "BluetoothSerial.STATE_CONNECTING");
                            break;
                        case BluetoothSerial.STATE_LISTEN:
                            Log.i(TAG, "BluetoothSerial.STATE_LISTEN");
                            break;
                        case BluetoothSerial.STATE_NONE:
                            Log.i(TAG, "BluetoothSerial.STATE_NONE");
                            break;
                    }
                    break;
                case BluetoothSerial.MESSAGE_WRITE:
                    //  byte[] writeBuf = (byte[]) msg.obj;
                    //  String writeMessage = new String(writeBuf);
                    //  Log.i(TAG, "Wrote: " + writeMessage);
                    break;
                case BluetoothSerial.MESSAGE_DEVICE_NAME:
                    Log.i(TAG, msg.getData().getString(BluetoothSerial.DEVICE_NAME));
                    break;
                case BluetoothSerial.MESSAGE_TOAST:
                    String message = msg.getData().getString(BluetoothSerial.TOAST);
//                    BluetoothSerial.notifyConnectionLost(message);
                    break;
             }
         }
    };

    private BluetoothSerial implementation = new BluetoothSerial(mHandler);

    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", implementation.echo(value));
        call.resolve(ret);
    }

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


}
