package com.example.whiterabbit;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

// This class handles all of push-notification-receive-related activities
public class PushReceiveActivity extends ParsePushBroadcastReceiver {

    // For the debugging purpose
    public final String TAG = this.getClass().getSimpleName();

    // This method handles the case when user opens the notification
    @Override
    public void onPushOpen(Context context, Intent intent) {
        Log.v(TAG, "Push Notification Clicked!");
        Intent myIntent;

        try {
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));

            // If the json string was "You have an invitation!!!", then to go the
            // RespondInvitationActivity.class
            if(json.getString("alert").equals("You have an invitation!!!")) {
                myIntent = new Intent(context, RespondInvitationActivity.class);
            }

            // Otherwise, go to main
            else {
                myIntent = new Intent(context, MainActivity.class);
            }

            // For the testing purposes
            String test = json.getString("alert");
            Log.v(TAG, "DO I HAVE AN: " + test);

            // Pass these invitation information to the RespondInvitationActivity.class
            if(json.getString("alert").equals("You have an invitation!!!")) {
                String invitationInfo = "Title: " + json.getString("title") + "\n" + "Time: " + json.getString("time") + "\n" + "Date: " +
                        json.getString("date") + "\n" + "Location: " + json.getString("location") + "\n" + "From - " + json.getString("fromName") +
                        ": " + Utility.phoneNumberFormat(json.getString("fromNumber"));
                myIntent.putExtra("phoneNumber", json.getString("fromNumber"));
                myIntent.putExtra("name", json.getString("fromName"));
                myIntent.putExtra("info", invitationInfo);
                myIntent.putExtra("objectId", json.getString("objectId"));
            }

            // This this flag so that we can start the new activity
            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Start the new activity
            context.startActivity(myIntent);
        } catch(JSONException e) {
            e.printStackTrace();
        }

    }

}
