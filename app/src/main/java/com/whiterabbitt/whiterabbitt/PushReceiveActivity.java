package com.whiterabbitt.whiterabbitt;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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
            if(json.getString("alert").equals("Let's get together :)")) {
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
            if(json.getString("alert").equals("Let's get together :)")) {
                String invitationInfo = "Title: " + json.getString("title") + "\n" + "Time: " + json.getString("time") + "\n" + "Date: " +
                        json.getString("date") + "\n" + "Location: " + json.getString("location") + "\n" + "From - " + json.getString("fromName") +
                        ": " + Utility.phoneNumberFormat(json.getString("fromNumber")) + "\n" + "Rewards: " + json.getString("carrots");

                myIntent.putExtra("phoneNumber", json.getString("fromNumber"));
                myIntent.putExtra("info", invitationInfo);
                myIntent.putExtra("objectId", json.getString("objectId"));
                myIntent.putExtra("name", json.getString("fromName"));
                myIntent.putExtra("title", json.getString("title"));
                myIntent.putExtra("time", json.getString("time"));
                myIntent.putExtra("date", json.getString("date"));
                myIntent.putExtra("location", json.getString("location"));
                myIntent.putExtra("reward", json.getString("carrots"));
            }

            // This this flag so that we can start the new activity
            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Start the new activity
            context.startActivity(myIntent);
        } catch(JSONException e) {
            e.printStackTrace();
        }

    }

    // A case where the user dismisses the push notification
    @Override
    public void onPushDismiss(Context context, Intent intent) {

        try {
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));

            // If the json string was "You have an invitation!!!", then . . .
            if(json.getString("alert").equals("Let's get together :)")) {

                final String objectId = json.getString("objectId");
                // This is to change the invitation information
                ParseQuery<ParseObject> invitationInfo = ParseQuery.getQuery("invitationInfo");
                invitationInfo.getInBackground(objectId, new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {
                        if(e == null) {
                            // This is to subscribe this user the the invitation so that this user can display the received invitation on his main event page
                            ParseUser.getCurrentUser().add("eventList", objectId);
                            ParseUser.getCurrentUser().saveInBackground();

                        } else {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }

    }

}
