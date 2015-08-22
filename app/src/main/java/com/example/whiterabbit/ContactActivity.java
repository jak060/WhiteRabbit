package com.example.whiterabbit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        overridePendingTransition(R.anim.slide_up, R.anim.no_change);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.no_change, R.anim.slide_down);
    }

}
