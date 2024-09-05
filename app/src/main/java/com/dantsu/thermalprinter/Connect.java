package com.dantsu.thermalprinter;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.dantsu.escposprinter.connection.DeviceConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;

import java.util.HashMap;
import java.util.Iterator;



public class Connect {
    public static BluetoothConnection getBluetoothDevice(Context context,String macAddress){
        try {
            final BluetoothConnection[] bluetoothDevicesList = (new MyBluetoothPrintersConnections()).getList();
            System.out.println("_macAddress_ 1:"+macAddress);
            if (bluetoothDevicesList != null) {
                for (BluetoothConnection device : bluetoothDevicesList) {
                    System.out.println("_macAddress_ if:"+device.getDevice().getAddress()+"-"+macAddress);
                    if(device.getDevice().getAddress().equals(macAddress.trim())){
                        return  device;
                    }
                }
            }
            System.out.println("_macAddress_ 2:");
        }catch (SecurityException e){

        }catch (Exception e){

        }
        return null;
    }

    public static UsbDevice getUSBDevice(Context context, String deviceId) {
        UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);

        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();

        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        System.out.println("_deviceId_ 1:"+deviceId);
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            String vDeviceId = device.getProductId() + "-" + device.getVendorId();
            if(vDeviceId.equals(deviceId)){

                return device;
            }
        }
        System.out.println("_deviceId_ 2:");
        return null;
    }


}
