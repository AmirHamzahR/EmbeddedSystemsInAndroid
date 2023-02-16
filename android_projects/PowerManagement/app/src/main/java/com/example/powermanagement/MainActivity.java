package com.example.powermanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private IntentFilter ifilter;
    public TextView TV;

    // Battery level
    private int BatteryL;
    // Battery voltage
    private int BatteryV;
    // Battery temperature
    private double BatteryT;
    // Battery technology
    private String BatteryTe;
    // Battery Status
    private String BatteryStatus;
    // Battery Health
    private String BatteryHealth;
    // Battery Plugged
    private String BatteryPlugged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Display the text view
        TV = (TextView) findViewById(R.id.TV);

        // Define the intent filter for battery
        ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mBatInforReceiver,ifilter);
    }

    private BroadcastReceiver mBatInforReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent){
            String action = intent.getAction();
            // Retrieve data from intent
            if(Intent.ACTION_BATTERY_CHANGED.equals(action)){
                BatteryL = intent.getIntExtra("level", 0);
                BatteryV = intent.getIntExtra("voltage", 0);
                BatteryT = intent.getIntExtra("temperature", 0);
                BatteryTe = intent.getStringExtra("technology");
            }
            switch (intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN)){
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    BatteryStatus = "Charging";
                    break;
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                    BatteryStatus = "Discharging";
                    break;
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                    BatteryStatus = "Not Charging";
                    break;
                case BatteryManager.BATTERY_STATUS_FULL:
                    BatteryStatus = "Fully Charged";
                    break;
                case BatteryManager.BATTERY_STATUS_UNKNOWN:
                    BatteryStatus = "Unknown Status";
                    break;
            }
            switch (intent.getIntExtra("health", BatteryManager.BATTERY_STATUS_UNKNOWN)){
                case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                    BatteryHealth = "Unknown Status";
                    break;
                case BatteryManager.BATTERY_HEALTH_GOOD:
                    BatteryHealth = "Good Status";
                    break;
                case BatteryManager.BATTERY_HEALTH_DEAD:
                    BatteryHealth = "Dead Status";
                    break;
                case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                    BatteryHealth = "Over Voltage";
                    break;
                case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                    BatteryHealth = "Overheat";
                    break;
            }
            switch (intent.getIntExtra("plugged",0)){
                case BatteryManager.BATTERY_PLUGGED_AC:
                    BatteryPlugged = "Plugged to AC";
                    break;
                case BatteryManager.BATTERY_PLUGGED_USB:
                    BatteryPlugged = "Plugged to USB";
                    break;
                case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                    BatteryPlugged = "Plugged to Wireless";
                    break;
                default:
                    BatteryPlugged = "-----";
            }
            TV.setText("Battery level: " + BatteryL + "%" + "\n" + "\n" +
                    "Battery Status: " + BatteryStatus + "\n" + "\n" +
                    "Battery Plugged: " + BatteryPlugged + "\n" + "\n" +
                    "Battery Health: " + BatteryHealth + "\n" + "\n" +
                    "Battery Voltage: " + (BatteryV/1000) + "V" + "\n" + "\n" +
                    "Battery Temperature: " + (BatteryT*0.1) + "^C" + "\n" + "\n" +
                    "Battery Technology: " + BatteryTe);
        }
    };

}