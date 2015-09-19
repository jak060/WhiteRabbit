package com.whiterabbitt.whiterabbitt;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Remove drop shadow from action bar on Lollipop
        getSupportActionBar().setElevation(0);
    }

}
