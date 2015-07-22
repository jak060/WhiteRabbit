package com.example.whiterabbit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.ParseException;

public class IndexActivity extends AppCompatActivity {

    // For the debugging purpose
    public final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "jkDPPMT3HKgNoIEqdxC0B75X4mts3sLl5aAUHfQu", "n5g1cmRCcy1whnp6TxmJXa4I4Y84D7SfUnHrkqgU");
        ParseInstallation.getCurrentInstallation().saveInBackground();

        /* For the test purposes */
        ////ParseObject testObject = new ParseObject("TestObject");
        ////testObject.put("foo", "bar");
        ////testObject.saveInBackground();

        // Button initialization for Create An Account
        Button createBtn = (Button) findViewById(R.id.createBtn);

        // Button initialization for Logging in
        Button loginBtn = (Button) findViewById(R.id.loginBtn);

        if(ParseUser.getCurrentUser() != null) {
            Log.v(TAG, "I'm already logged in!");
            ParseInstallation.getCurrentInstallation().put("userName", ParseUser.getCurrentUser());
            ParseInstallation.getCurrentInstallation().put("user", ParseUser.getCurrentUser().get("phoneNumber"));
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
/*
        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
