package com.example.diffsensorsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /** Called when the user tops the Motion sensors button */
    public void sendMotion(View view){
        Intent intent = new Intent(this, MotionSensor.class);
        startActivity(intent);
    }

    /** Called when the user tops the Gyro sensors button */
    public void sendGyro(View view){
        Intent intent = new Intent(this, GyroscopeSensor.class);
        startActivity(intent);
    }
}