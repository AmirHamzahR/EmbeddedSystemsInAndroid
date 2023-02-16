package com.example.diffsensorsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class GyroscopeSensor extends AppCompatActivity implements SensorEventListener {
    // get access to sensors
    private SensorManager mSensorManager;

    // represent a sensor
    private Sensor Gyroscope;

    // TextViews to display data
    private TextView gyr_x;
    private TextView gyr_y;
    private TextView gyr_z;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gyroscope_sensor);

        Intent intent = getIntent();

        gyr_x = (TextView) findViewById(R.id.gyr_Xaxis);
        gyr_y = (TextView) findViewById(R.id.gyr_Yaxis);
        gyr_z = (TextView) findViewById(R.id.gyr_Zaxis);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Gyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z = sensorEvent.values[2];
        if(sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gyr_x.setText("gyr_Xaxis:" + x);
            gyr_y.setText("gyr_Yaxis:" + y);
            gyr_z.setText("gyr_Zaxis:" + z);
        }
    }

    // Change the sensor state if the accuracy changes
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    // Pause the sensor
    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    // Resume the sensor with a slight delay for safety measures
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this,Gyroscope,SensorManager.SENSOR_DELAY_NORMAL);
    }
}