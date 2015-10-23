package com.whiterabbitt.whiterabbitt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import java.util.ArrayList;

/**
 * This class deals with displaying necessary contents for the index page
 */
public class IndexActivity extends AppCompatActivity {

    // For the debugging purpose
    public final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        // Button initialization for Create An Account
        Button createBtn = (Button) findViewById(R.id.createBtn);

        // Button initialization for Logging in
        Button loginBtn = (Button) findViewById(R.id.loginBtn);

        // This is the case when the user is already logged in
        if(ParseUser.getCurrentUser() != null) {
            Log.v(TAG, "I'm already logged in!");

            // This sets up the phone number and the user to the ParseInstallation
            // so that you can receive notification from parse
            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
            if(installation.get("phoneNumber") == null) {
                Log.v(TAG, "I'm setting up the phone number and user name!");
                installation.put("user", ParseUser.getCurrentUser().get("phoneNumber"));
                installation.put("userName", ParseUser.getCurrentUser());
                installation.saveInBackground();
            }

            // These are necessary to set up the new columns from the parse for users who used to
            // not have these fields
            if(ParseUser.getCurrentUser().get("eventList") == null) {
                Log.v(TAG, "I'm setting up the eventList!");
                ParseUser.getCurrentUser().put("eventList", new ArrayList<String>());
                ParseUser.getCurrentUser().saveInBackground();
            }
            if(ParseUser.getCurrentUser().get("eventHistory") == null) {
                Log.v(TAG, "I'm setting up the eventHistory!");
                ParseUser.getCurrentUser().put("eventHistory", new ArrayList<String>());
                ParseUser.getCurrentUser().saveInBackground();
            }

            // Start the main activity
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }

        // When the user clicks it, go to the Create Account page
        createBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateAccountActivity.class);
                startActivity(intent);
            }
        });

        // When the user clicks it, go to the Login Page
        loginBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
