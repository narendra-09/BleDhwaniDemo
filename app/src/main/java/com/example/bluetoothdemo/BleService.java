package com.example.bluetoothdemo;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class BleService extends Service {
    private static final String TAG = "BleService";

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothManager bluetoothManager;
    private BluetoothGatt bluetoothGatt;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: Service running");
    }

    public BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
           if(newState == BluetoothProfile.STATE_CONNECTED){
               Log.d(TAG, "onConnectionStateChange: Connected");
           }
           if(newState == BluetoothProfile.STATE_CONNECTING){
               Log.d(TAG, "onConnectionStateChange: Connecting");
           }
           if(newState == BluetoothProfile.STATE_DISCONNECTING){
               Log.d(TAG, "onConnectionStateChange: DisConnecting");
           }
           if(newState == BluetoothProfile.STATE_DISCONNECTED){
               Log.d(TAG, "onConnectionStateChange: DisConnected");
           }

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
        }
    };

    private final LocalBinder localBinder = new LocalBinder();
    public class LocalBinder extends Binder {
        BleService getService(){
            return BleService.this;
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return localBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    private void close() {
        if(bluetoothGatt == null){
            return;
        }
        bluetoothGatt.close();
        bluetoothGatt = null;
    }

    public boolean initialize(){
        if(bluetoothManager == null){
            bluetoothManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
            if(bluetoothManager == null){
                Log.d(TAG, "initialize: Failed");
                return false;
            }
        }
        bluetoothAdapter = bluetoothManager.getAdapter();
        if(bluetoothAdapter == null){
            Log.d(TAG, "initialize: Ble Adapter Failed");
            return false;
        }
        return true;
    }

    public boolean connect(final String address){
        if(bluetoothAdapter == null || address == null){
            Log.d(TAG, "connect: Failed");
            return false;
        }
        final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        bluetoothGatt = device.connectGatt(BleService.this,false,bluetoothGattCallback);
        return true;
    }

    public void disConnect(){
        if(bluetoothAdapter == null || bluetoothGatt == null){
            Log.d(TAG, "disConnect: Adapter Not Initialized");
        }
        bluetoothGatt.disconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy : Service Destroyed");
    }
}
