package com.example.whiterabbit;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * This class prepares everything that is necessary to start the reward page
 */
public class RewardPreparationService  extends IntentService{
    // For the debugging purpose
    protected final String TAG = this.getClass().getSimpleName();

    public RewardPreparationService() {
        super("reward-preparation-service");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        // A boolean avalue to check whether the user has entered the geofence or not
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean arrived = prefs.getBoolean(Constants.GEOFENCES_TRANSITION_INTENT_ENTERED_KEY, false);

        // To pass objectId to corresponding reward page
        String objectId = intent.getExtras().getString("objectId");

        Log.v(TAG, "Did he arrive? " + arrived);

        // If he has entered the geofence, then go to the winner page
        if(arrived == true) {
            Intent newIntent = new Intent(this, RewardWinnerActivity.class);
            newIntent.putExtra("objectId", objectId);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(newIntent);
        }

        // Else, then go to the loser page
        else {
            Intent newIntent = new Intent(this, RewardLoserActivity.class);
            newIntent.putExtra("objectId", objectId);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(newIntent);
        }


    }
}
