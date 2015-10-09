package com.whiterabbitt.whiterabbitt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * This class handles the case when the user opens up push notifications
 */
public class OnPushHandlingBroadcastReceiver extends BroadcastReceiver{

    // For the debugging purpose
    protected final String TAG = this.getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.v(TAG, "AM I EVEN HERE?");
        Log.v(TAG, "My Action IS: " + intent.getAction());
        Log.v(TAG, "My Request Code is: " + intent.getExtras().getInt("requestCode"));
        // This case is where the user has clicked the notification
        if(intent.getAction().equals(Constants.NOTIFICATION_CLICKED)) {
            if(intent.getExtras().getInt("requestCode") == 3) {
                Log.v(TAG, "I'm from Geofence");
                //Intent myIntent = new Intent(context, EventDetailActivity.class);
                //context.startActivity(myIntent);
            }

            if(intent.getExtras().getInt("requestCode") == 4) {
                Log.v(TAG, "I'm from Thirty Min");
                //Intent myIntent = new Intent(context, EventDetailActivity.class);
                //context.startActivity(myIntent);
            }
        }

        // This case is where the user has dismissed the notification
        else if (intent.getAction().equals(Constants.NOTIFICATION_DISMISSED)){

        }
    }

}
