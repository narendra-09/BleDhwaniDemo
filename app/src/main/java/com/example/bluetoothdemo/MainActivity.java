package com.example.bluetoothdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.bluetoothdemo.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{
    private static final String TAG = "MainActivity";
    private ActivityMainBinding mainBinding;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private ScanCallback scanCallback;
    private BluetoothLeScanner leScanner;
    private boolean scanning;
    private final int ACTION_REQUEST_ENABLE = 100;
    private static final int scan_period = 15000;
    private BluetoothDevice bluetoothDevice;
    private ArrayList<BluetoothDevice> bluetoothDevices;
    private final int REQUEST_CODE_PERMISSIONS = 10;
    private final String[] REQUIRED_PERMISSIONS = new String[]{
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.BLUETOOTH_ADMIN",
            "android.permission.BLUETOOTH"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        checkingPermissions();
        bluetoothDevices = new ArrayList<>();
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE not supported", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "BLE supported", Toast.LENGTH_SHORT).show();
        }
        setUpBle();
        scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                    Toast.makeText(MainActivity.this, ""+result.getDevice().getName(), Toast.LENGTH_SHORT).show();
                }
        };
        mainBinding.connectBt.setOnClickListener(v -> enableBle());
        mainBinding.scanBt.setOnClickListener(v -> {
            if(bluetoothAdapter.isEnabled()){
                scan();
            }
        });

    }

    private void checkingPermissions() {
        if (allPermissionsGranted()) {
            Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    private boolean allPermissionsGranted() {

        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void setUpBle() {
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        leScanner = bluetoothAdapter.getBluetoothLeScanner();
    }

    private void enableBle() {
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, ACTION_REQUEST_ENABLE);
        }
    }

    private void scan() {
        Handler handler = new Handler(Looper.getMainLooper());
        if (!scanning && leScanner!=null) {
            handler.postDelayed(() -> {
                scanning = false;
                leScanner.stopScan(scanCallback);
               }, scan_period);
            scanning = true;
            leScanner.startScan(scanCallback);
        } else {
            scanning = false;
            assert leScanner != null;
            leScanner.stopScan(scanCallback);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == ACTION_REQUEST_ENABLE && data != null) {
            Toast.makeText(this, "Bluetooth Turned On", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                Toast.makeText(this, "granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }
}