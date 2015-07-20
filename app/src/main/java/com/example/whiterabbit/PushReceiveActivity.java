package com.example.whiterabbit;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;

// This class handles
public class PushReceiveActivity extends ParsePushBroadcastReceiver {

    // For the debugging purpose
    public final String TAG = this.getClass().getSimpleName();

    @Override
    public void onPushOpen(Context context, Intent intent) {
        Log.v(TAG, "Push Notification Clicked!");
        Intent myIntent = new Intent(context, RespondInvitationActivity.class);
        myIntent.putExtras(intent.getExtras());
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(myIntent);
    }

}
