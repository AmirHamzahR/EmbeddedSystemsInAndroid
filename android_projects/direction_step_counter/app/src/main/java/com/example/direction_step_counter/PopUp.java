package com.example.direction_step_counter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class PopUp extends AppCompatActivity {

    ListView totalStepsList;
    int[] stepCounter = new int[8];
    private String[] directions = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*0.8),(int)(height*0.8));

        totalStepsList = (ListView) findViewById(R.id.totalStepsList);
        Intent intent = getIntent();
        stepCounter = intent.getIntArrayExtra(MainActivity.TOTAL_STEPS);
        Log.d("123456",String.valueOf(stepCounter));
        updateStepData();
    }
    public void updateStepData() {
        List<String> stepData = new ArrayList<>();
        for (int i = 0; i < stepCounter.length; i++) {
            stepData.add(directions[i] + ": " + stepCounter[i]);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stepData);
        totalStepsList.setAdapter(adapter);
    }

}