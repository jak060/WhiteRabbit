package com.example.whiterabbit;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

// This class handles
public class PushReceiveActivity extends ParsePushBroadcastReceiver {

    // For the debugging purpose
    public final String TAG = this.getClass().getSimpleName();

    @Override
    public void onPushReceive(Context context, Intent intent) {
        Log.v(TAG, "Push Notification Clicked!");
        Intent myIntent;

        try {
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));

            if(json.getString("alert").equals("You have an invitation!!!")) {
                myIntent = new Intent(context, RespondInvitationActivity.class);
            } else {
                myIntent = new Intent(context, MainActivity.class);
            }

            String test = json.getString("alert");
            Log.v(TAG, "DO I HAVE AN: " + test);

            if(json.getString("alert").equals("You have an invitation!!!")) {
                String invitationInfo = "Title: " + json.getString("title") + "\n" + "Time: " + json.getString("time") + "\n" + "Date: " +
                        json.getString("date") + "\n" + "Location: " + json.getString("location") + "\n" + "From - " + json.getString("fromName") +
                        ": " + Utility.phoneNumberFormat(json.getString("fromNumber"));
                myIntent.putExtra("phoneNumber", json.getString("fromNumber"));
                myIntent.putExtra("name", json.getString("fromName"));
                myIntent.putExtra("info", invitationInfo);
                myIntent.putExtra("objectId", json.getString("objectId"));
            } else {
                myIntent.putExtra("light", json.getString("indicator"));
            }

            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(myIntent);
        } catch(JSONException e) {
            e.printStackTrace();
        }

    }

}
