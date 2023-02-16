package com.example.motionsensor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // get access to sensors
    private SensorManager mSensorManager;

    // represent a sensor
    private Sensor Accelerometer;

    // TextViews to display data
    private TextView acc_x;
    private TextView acc_y;
    private TextView acc_z;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        acc_x = (TextView) findViewById(R.id.acc_Xaxis);
        acc_y = (TextView) findViewById(R.id.acc_Yaxis);
        acc_z = (TextView) findViewById(R.id.acc_Zaxis);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    // Change the sensor state if it change
    private double h;
    final float alpha = (float) 0.8; // alpha value should be changed
    private float gravity [] = new float[3];
    // This code below creates a simple filter for an accelerometer
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        // In this example, alpha is calculated as t / (t+dT),
        // where t is the low-pass filter's time-constant and
        // dT is the event delivery rate.

        // Isolate the force of gravity with the low-pass filter.
        gravity[0] = alpha * gravity[0] + (1 - alpha) * sensorEvent.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * sensorEvent.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * sensorEvent.values[2];

        // Remove the gravity contribution with the high-pass filter.
        float linear_acceleration [] = new float[3];
        linear_acceleration[0] = sensorEvent.values[0] - gravity[0];
        linear_acceleration[1] = sensorEvent.values[1] - gravity[1];
        linear_acceleration[2] = sensorEvent.values[2] - gravity[2];

        if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            acc_x.setText("acc_Xaxis:" + linear_acceleration[0]);
            acc_y.setText("acc_Yaxis:" + linear_acceleration[1]);
            acc_z.setText("acc_Zaxis:" + linear_acceleration[2]);
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
        mSensorManager.registerListener(this,Accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
    }
}