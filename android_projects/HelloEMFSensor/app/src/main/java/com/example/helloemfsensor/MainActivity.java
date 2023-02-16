package com.example.helloemfsensor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    // get access to sensors
    private Sensor mMagneticField;
    // represent a sensor

    // Set TextView for sensor value
    private TextView mag_x;
    private TextView mag_y;
    private TextView mag_z;
    private TextView mag_h;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find view from layout file
        mag_x = (TextView) findViewById(R.id.emf_Xaxis);
        mag_y = (TextView) findViewById(R.id.emf_Yaxis);
        mag_z = (TextView) findViewById(R.id.emf_Zaxis);
        mag_h = (TextView) findViewById(R.id.emf_magnetic_field);

        // Get an instance of SensorManager for accessing sensors
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Determine a default sensor type, in this case is magnetometer
        mMagneticField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    private double h;
    @Override
    public void onSensorChanged(SensorEvent sensorEvent){
        // Calculate the total magnetic field (x^2 + y^2 + z^2 = emf)
        h = Math.sqrt(sensorEvent.values[0] * sensorEvent.values[0] + sensorEvent.values[1] * sensorEvent.values[1] + sensorEvent.values[2] * sensorEvent.values[2]);
        mag_x.setText("mag_Xaxis:" + sensorEvent.values[0]);
        mag_y.setText("mag_Yaxis:" + sensorEvent.values[1]);
        mag_z.setText("mag_Zaxis:" + sensorEvent.values[2]);
        mag_h.setText("magnetic_field:" + h);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i){

    }

    @Override
    protected void onResume() {
        super.onResume();
        // register the sensor when user returns to the activity
        mSensorManager.registerListener(this,mMagneticField,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // disable the sensor
        mSensorManager.unregisterListener(this);
    }

}