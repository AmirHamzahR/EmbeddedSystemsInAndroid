package com.example.direction_step_counter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    public static final String TOTAL_STEPS = "step_counter";

    // Sensors that are used
    private SensorManager sm;
    private Sensor aSensor;
    private Sensor mSensor;
    private Sensor stepDSensor;
    private Sensor stepCSensor;
    private Sensor gyroSensor;

    // Layout Views
    ListView listView;
    TextView tv;
    TextView directionText;
    TextView magnetoQuality;
    ImageButton resetbutton;
    ImageButton popup;
    private ImageView cNeedle;
    public String direction = null;
    private TextView textViewStepCounter;

    // Compass Variables
    public float newDegree = 0;
    private boolean accelerometerTest = false;
    private boolean magnetometerTest = false;
    float[] accelerometerValues = new float[3];
    float[] magneticFieldValues = new float[3];

    // Magnetometer quality
    float[] gValues = new float[3];
    float[] mValues = new float[3];

    // Step Counter Variables
    // Accelerometer for step count
    private long lastStepTime = 0;
    private long debounceTime = 200; // 200 milliseconds
    // Step sensors for step count
    float x_old = 0;
    int[] stepCounterCount = new int[8];
    boolean stepSensorsExist = false;
    boolean useAccelerometer = false;



    // Complete this method
    public void initialization() {
        // Initialize the sensors
        sm = (SensorManager) getSystemService((Context.SENSOR_SERVICE));
        mSensor = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        aSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroSensor = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        if((sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) && (sm.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null)){
            stepCSensor = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            stepDSensor = sm.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            stepSensorsExist = true;
            sm.registerListener(this, stepDSensor,SensorManager.SENSOR_DELAY_FASTEST);
            sm.registerListener(this, stepCSensor,SensorManager.SENSOR_DELAY_FASTEST);
            Toast.makeText(getApplicationContext(),"Using step sensors",Toast.LENGTH_SHORT).show();
        }
        else{
            useAccelerometer = true;
            Toast.makeText(getApplicationContext(),"Using accelerometer for steps",Toast.LENGTH_SHORT).show();
        }

        sm.registerListener(this,mSensor,SensorManager.SENSOR_DELAY_GAME); // Resuming EMF sensor
        sm.registerListener(this,aSensor,SensorManager.SENSOR_DELAY_GAME); // Resuming accelerometer sensor
        sm.registerListener(this,gyroSensor,SensorManager.SENSOR_DELAY_GAME); // Resuming accelerometer sensor


        // Initialise the compass displays
        tv = (TextView) findViewById(R.id.tv);
        cNeedle = findViewById(R.id.cNeedle);
        directionText = (TextView) findViewById(R.id.directionText);

        // Initialise the step-count displays
        textViewStepCounter = findViewById(R.id.textViewStepCounter);
        resetbutton = findViewById(R.id.button2);
        popup = (ImageButton) findViewById(R.id.popup);

        // Initialise the magnetometer quality
        magnetoQuality = findViewById(R.id.magnetoQuality);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stepSensorChecks();
        initialization();
        calculateOrientation();
    }

    public void stepSensorChecks(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
            //ask for permission
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, Sensor.TYPE_STEP_COUNTER);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.BODY_SENSORS}, Sensor.TYPE_GYROSCOPE);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    // Complete this method
    // To calculate and convert the data from both sensors when phone is rotated
    public void calculateOrientation() {
        float[] values = new float[3]; // Stores orientation values (0 - azimuth, 1 - pitch, 2 - roll)
        float[] Rotate = new float[9];
        float[] filteredValues = new float[3];
        int i = 0;

        if (accelerometerTest && magnetometerTest) {
            SensorManager.getRotationMatrix(Rotate, null, accelerometerValues, magneticFieldValues);
            SensorManager.getOrientation(Rotate, values);

            filteredValues = lowPF(filteredValues, values, (float) 1);
            float degree = (float) (Math.toDegrees(filteredValues[0]));
            if (degree < 0) {
                degree += 360;
            }


            if(degree >= 348 || degree < 12){
                direction = "N";
                i = 0;
            }
            else if(degree >= 12 && degree < 78){
                direction = "NE";
                i = 1;
            }
            else if(degree >= 78 && degree < 102){
                direction = "E";
                i = 2;
            }
            else if(degree >= 102 && degree < 168){
                direction = "SE";
                i = 3;
            }
            else if(degree >= 168 && degree < 192){
                direction = "S";
                i = 4;
            }
            else if(degree >= 192 && degree < 258){
                direction = "SW";
                i = 5;
            }
            else if(degree >= 258 && degree < 282){
                direction = "W";
                i = 6;
            }
            else if(degree >= 282 && degree < 348){
                direction = "NW";
                i = 7;
            }

            tv.setText(String.format("%.1f°", degree));
            directionText.setText(direction);
            rotatingCompass(degree);
        }
//        if(useAccelerometer) {
//            // Check if the linear acceleration in the y-axis exceeds the threshold
//            if (magnitudeDelta > 6 && magnitudeDelta < 20 && accelerometerValues[1] > 0) {
//                directionalSteps();
//            }
//        }
        textViewStepCounter.setText(getString(R.string.step_counter, stepCounterCount[i],direction));
    }

    public void rotatingCompass(float degree){
        RotateAnimation animation = new RotateAnimation((float)newDegree, (float)-degree,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(1000);  // Decreased the duration to 100ms
        animation.setInterpolator(new LinearInterpolator());  // Added linear interpolator
        animation.setFillAfter(true);
        cNeedle.startAnimation(animation);
        newDegree = -degree;
    }

    // This code below creates a simple filter for an accelerometer
    private float magnitudeDelta = 0f;
    private float magnitudePrevious = 0f;
    float x_new = 0;
    @Override
    public void onSensorChanged(SensorEvent event) {
        switch(event.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
                // For compass
                accelerometerValues = lowPF(accelerometerValues, event.values,(float) 0.08);
                accelerometerTest = true;
                // For step count
//                float temp_mag = 0f;
//                float magnitude;
//                for(int i = 0; i < 3; i++){
//                    temp_mag += accelerometerValues[i]*accelerometerValues[i];
//                }
//                magnitude = (float) Math.sqrt(temp_mag);
//                magnitudeDelta = magnitude - magnitudePrevious;
//                magnitudePrevious = magnitudeDelta;
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                // For compass
                magneticFieldValues = lowPF(magneticFieldValues, event.values, (float) 0.08); // default 0.08
                mValues = lowPF(mValues, event.values, (float) 0.06);
                magnetometerTest = true;
                // For magnetometer quality
                break;
            case Sensor.TYPE_GYROSCOPE:
                gValues = lowPF(gValues, event.values, (float) 0.06);
                break;
            case Sensor.TYPE_STEP_COUNTER:
                x_new = event.values[0];
                float stepIncrease = x_new - x_old;
                directionalSteps((int) stepIncrease);
                x_old = x_new;
                break;
        }
        magnetometerQuality();
        calculateOrientation(); // This is called at the end of the method to recalculate orientation of the compass
    }

    public void magnetometerQuality(){
        long startTime = System.currentTimeMillis();
        long endTime = startTime + (3 * 1000); // 2 seconds in milliseconds

        List<Float> mBearings = new ArrayList<>();
        List<Float> gBearings = new ArrayList<>();

        // Check if the current time is less than the end time
        if (System.currentTimeMillis() < endTime) {
            // Calculate the bearing of magnetometer
            float mPitch = (float) Math.atan2(mValues[1], mValues[2]);
            float mRoll = (float) Math.atan2(-mValues[0], Math.sqrt(mValues[1] * mValues[1] + mValues[2] * mValues[2]));
            float mYaw = (float) Math.atan2(mValues[1], mValues[0]);
            float mBearing = (float) Math.toDegrees(Math.atan2(Math.sin(mYaw) * Math.cos(mPitch), Math.cos(mYaw) * Math.cos(mRoll) - Math.sin(mRoll) * Math.sin(mPitch) * Math.sin(mYaw)));
            if (mBearing < 0) {
                mBearing += 360;
            }
            mBearings.add(mBearing);
            // Calculate the bearing of gyroscope
            float gBearing = (float) Math.toDegrees(Math.atan2(gValues[1], gValues[0]));

            if (gBearing < 0) {
                gBearing += 360;
            }
            gBearings.add(gBearing);
        }
        // Calculate the average or maximum relative change in bearing
        float mAvgBearing = 0;
        float gAvgBearing = 0;
        for (float mBearing : mBearings) {
            mAvgBearing += mBearing;
        }
        mAvgBearing /= mBearings.size();

        for (float gBearing : gBearings) {
            gAvgBearing += gBearing;
        }
        gAvgBearing /= gBearings.size();

        float accuracyPercentage = 100 * (1 - Math.abs(mAvgBearing - gAvgBearing) / gAvgBearing);
        int color;
        String Mquality;
        if (accuracyPercentage >= 66) {
            color = Color.BLACK;
            Mquality = "GOOD";
        } else if (accuracyPercentage >= 33) {
            color = Color.YELLOW;
            Mquality = "OKAY";
        } else {
            color = Color.RED;
            Mquality = "BAD";
        }
        magnetoQuality.setText(Mquality);
        magnetoQuality.setTextColor(color);
    }

    public void directionalSteps(int stepCount){
        if (direction != null) {
            switch (direction) {
                case "N":
                    stepCounterCount[0] += stepCount;
                    break;
                case "NE":
                    stepCounterCount[1] += stepCount;
                    break;
                case "E":
                    stepCounterCount[2] += stepCount;
                    break;
                case "SE":
                    stepCounterCount[3] += stepCount;
                    break;
                case "S":
                    stepCounterCount[4] += stepCount;
                    break;
                case "SW":
                    stepCounterCount[5] += stepCount;
                    break;
                case "W":
                    stepCounterCount[6] += stepCount;
                    break;
                case "NW":
                    stepCounterCount[7] += stepCount;
                    break;
            }
        }
    }

    public void resetButtonOnClick(View view){
        int[] zero_array = new int[8];
        stepCounterCount = zero_array;
    }

    public void popUpOnClick(View view){
        Intent intent = new Intent(MainActivity.this, PopUp.class);
        intent.putExtra(TOTAL_STEPS, stepCounterCount);
        startActivity(intent);
    }

    public float[] lowPF(float output[], float input[], float alpha){
        for(int i = 0; i < input.length; ++i){
            output[i] = output[i] + alpha * (input[i] - output[i]);
        }
        return output;
    }

    // Complete this method
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // Complete this method
    @Override
    protected void onPause() {
        super.onPause();
        if(stepSensorsExist) {
            sm.unregisterListener(this, stepDSensor);
            sm.unregisterListener(this, stepCSensor);
        }
        sm.unregisterListener(this, gyroSensor);
        sm.unregisterListener(this,mSensor);
        sm.unregisterListener(this,aSensor);

    }

    // Complete this method
    @Override
    protected void onResume() {
        super.onResume();
        if(stepSensorsExist) {
            sm.registerListener(this, stepDSensor,SensorManager.SENSOR_DELAY_FASTEST);
            sm.registerListener(this, stepCSensor,SensorManager.SENSOR_DELAY_FASTEST);
        }
        sm.registerListener(this, gyroSensor,SensorManager.SENSOR_DELAY_GAME);
        sm.registerListener(this,mSensor,SensorManager.SENSOR_DELAY_GAME); // Resuming EMF sensor
        sm.registerListener(this,aSensor,SensorManager.SENSOR_DELAY_GAME); // Resuming accelerometer sensor
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(stepSensorsExist) {
            sm.unregisterListener(this, stepDSensor);
            sm.unregisterListener(this, stepCSensor);
        }
        sm.unregisterListener(this, gyroSensor);
        sm.unregisterListener(this,mSensor);
        sm.unregisterListener(this,aSensor);

    }
}