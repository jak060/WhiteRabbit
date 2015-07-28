package com.example.whiterabbit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// This class shows when user opens the app from the notification
public class RespondInvitationActivity  extends AppCompatActivity{

    String combined; // To combine Strings

    // For the debugging purpose
    public final String TAG = this.getClass().getSimpleName();

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respond_invitation);
        Intent intent = getIntent();
        final String phoneNumber;
        final String objectId;
        final String name;
        String temp = ""; // To hold the phoneNumber
        String temp2 = ""; // To hold the objectId
        String temp3 = ""; // To hold the name


        if(intent != null) {
            TextView invitationInfo = (TextView) findViewById(R.id.textView2);
            invitationInfo.setText(intent.getStringExtra("info"));
            temp = intent.getStringExtra("phoneNumber");
            temp2 = intent.getStringExtra("objectId");
            temp3 = intent.getStringExtra("name");
        }

        Button acceptBtn = (Button) findViewById(R.id.acceptBtn);
        Button declineBtn = (Button) findViewById(R.id.declineBtn);

        phoneNumber = temp;
        objectId = temp2;
        name = temp3;

        // Combine name with the phone number
        combined = name + ": " + phoneNumber;

        // Handle the case when the user accepts the invitation
        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // First of all show the progress dialog to make the user wait
                dialog = new ProgressDialog(RespondInvitationActivity.this);
                dialog.setTitle("Thank You For Your Patience :)");
                dialog.setMessage("Sending Back Your Response.");
                dialog.show();
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);

                // Use this query to get the sender's information
                ParseQuery pushQuery = ParseInstallation.getQuery();
                pushQuery.whereEqualTo("user", phoneNumber);

                // Send push notification back to the sender, who has sent the invitation
                ParsePush push = new ParsePush();
                JSONObject data = new JSONObject();
                try {
                    data.put("title", "From: " + ParseUser.getCurrentUser().get("firstName"));
                    data.put("alert", "Accepted :)");

                } catch(JSONException e) {
                    e.printStackTrace();
                }
                push.setQuery(pushQuery); // Set our Installation query
                push.setData(data);
                push.sendInBackground();

                // This is to change the invitation information
                ParseQuery<ParseObject> invitationInfo = ParseQuery.getQuery("invitationInfo");
                invitationInfo.getInBackground(objectId, new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {

                        dialog.dismiss();

                        if (e == null) {
                            // stateNum - 1 is to change the indicator light in the main event page
                            parseObject.put("stateNum", ((Integer) parseObject.get("stateNum")) - 1);

                            // This is to subscribe this user the the invitation so that this user can display the received invitation on his main event page
                            parseObject.put("ownerID", parseObject.get("ownerID") + ":" + ParseUser.getCurrentUser().getObjectId());
                            parseObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e == null) {
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Log.e(TAG, "Error saving updated invitation info to parse (when user clicked Accept Button)");
                                        e.printStackTrace();
                                    }

                                }
                            });
                        } else {
                            Log.e(TAG, "Error getting invitation info from parse (when user clicked Accept Button)");
                            e.printStackTrace();
                        }

                    }
                });


            }
        });

        // Handle the case when the user declines the invitation
        declineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // First of all show the progress dialog to make the user wait
                dialog = new ProgressDialog(RespondInvitationActivity.this);
                dialog.setTitle("Thank You For Your Patience :)");
                dialog.setMessage("Sending Back Your Response.");
                dialog.show();
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);

                // Use this query to get the sender's information
                ParseQuery pushQuery = ParseInstallation.getQuery();
                pushQuery.whereEqualTo("user", phoneNumber);

                // Send push notification back to the sender, who has sent the invitation
                ParsePush push = new ParsePush();
                JSONObject data = new JSONObject();
                try {
                    data.put("title", "From: " + ParseUser.getCurrentUser().get("firstName"));
                    data.put("alert", "Declined :(");

                } catch(JSONException e) {
                    e.printStackTrace();
                }
                push.setQuery(pushQuery); // Set our Installation query
                push.setData(data);
                push.sendInBackground();

                // This is to change the invitation information
                ParseQuery<ParseObject> invitationInfo = ParseQuery.getQuery("invitationInfo");
                invitationInfo.getInBackground(objectId, new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {

                        dialog.dismiss();

                        if(e == null) {
                            // stateNum is to change the indicator light to the red light in the main event page
                            parseObject.put("stateNum", ((Integer) parseObject.get("stateNum")) + ((ArrayList) parseObject.get("invitees")).size());
                            parseObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e == null) {
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Log.e(TAG, "Error saving updated invitation info to parse (when user clicked Decline Button)");
                                        e.printStackTrace();
                                    }

                                }
                            });
                        } else {
                            Log.e(TAG, "Error getting invitation info from parse (when user clicked Decline Button)");
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
    }
}
