package com.example.whiterabbit;

import android.app.Application;

import com.parse.Parse;

/**
 * This class extends Application to initialize parse.
 */
public class MyApplication extends Application {
    public void onCreate() {
        super.onCreate();
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "jkDPPMT3HKgNoIEqdxC0B75X4mts3sLl5aAUHfQu", "n5g1cmRCcy1whnp6TxmJXa4I4Y84D7SfUnHrkqgU");
    }
}
