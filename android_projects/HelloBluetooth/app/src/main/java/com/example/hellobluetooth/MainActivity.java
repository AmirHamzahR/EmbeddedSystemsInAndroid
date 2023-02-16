package com.example.hellobluetooth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ID_READ_WRITE_PERMISSION = 99;

    // Declaring the variable to be acesses in the methods and classes
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private Set<BluetoothDevice> pairedDevices;
    Button b1,b2,b3,b4;
    ListView lv;
    String devices[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        askBluePermissions();

        b1 = (Button) findViewById(R.id.button);
        b2 = (Button) findViewById(R.id.button2);
        b3 = (Button) findViewById(R.id.button3);
        b4 = (Button) findViewById(R.id.button4);
        lv = (ListView) findViewById(R.id.listView);


        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.enable();
            }
        }

    }

    private void askBluePermissions(){
        if(android.os.Build.VERSION.SDK_INT >= 23){
            // Check if we have read/write permission
            int blueAccessPermission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH);
            int blueAdminPermission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_ADMIN);
            int blueAdvertPermission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_ADVERTISE);
            int blueConnectPermission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT);
            int coarseLocationPermission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
            int fineLocationPermission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);

            if (blueAccessPermission != PackageManager.PERMISSION_GRANTED ||
                    blueAdminPermission!= PackageManager.PERMISSION_GRANTED ||
                    blueAdvertPermission!= PackageManager.PERMISSION_GRANTED ||
                    blueConnectPermission!= PackageManager.PERMISSION_GRANTED ||
                    coarseLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    fineLocationPermission != PackageManager.PERMISSION_GRANTED){
                // If don't have permission so prompt the user.
                this.requestPermissions(
                        new String[]{android.Manifest.permission.BLUETOOTH,
                                android.Manifest.permission.BLUETOOTH_ADMIN,
                                android.Manifest.permission.BLUETOOTH_ADVERTISE,
                                android.Manifest.permission.BLUETOOTH_CONNECT,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_ID_READ_WRITE_PERMISSION
                );
                return;
            }
        }
    }

    // When you have the request results
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_ID_READ_WRITE_PERMISSION: {
                // Note: If request in cancelled, the result arrays are empty.
                // Permissions granted (read/write/camera).
                if(grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED
                        && grantResults[3] == PackageManager.PERMISSION_GRANTED
                        && grantResults[4] == PackageManager.PERMISSION_GRANTED
                        && grantResults[5] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"Permission granted!", Toast.LENGTH_LONG).show();
                }
                // Cancelled or denied
                else{
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    public void list(View v){
        pairedDevices = mBluetoothAdapter.getBondedDevices();

        ArrayList list = new ArrayList();
        for(BluetoothDevice bt: pairedDevices){
            list.add(bt.getName() + "," + bt.getAddress());
        }

        Toast.makeText(getApplicationContext(), "Showing Paired Devices", Toast.LENGTH_SHORT).show();

        final ArrayAdapter adapter = new ArrayAdapter (this, android.R.layout.simple_list_item_1, list);
        lv.setAdapter(adapter);
    }

    // Code to turn on bluetooth
    public void on(View v){
        if(!mBluetoothAdapter.isEnabled()){
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn,0);
            Toast.makeText(getApplicationContext(),"Turned on", Toast.LENGTH_LONG).show();
        } else{
            Toast.makeText(getApplicationContext(), "Already on", Toast.LENGTH_LONG).show();
        }
    }

    // Code to turn off bluetooth
    public void off(View v){
        mBluetoothAdapter.disable();
        Toast.makeText(getApplicationContext(),"Turned off", Toast.LENGTH_LONG).show();
    }

    // Code make the bluetooth to be discovered?
    public void visible(View v){
        Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(getVisible,0);
    }
}