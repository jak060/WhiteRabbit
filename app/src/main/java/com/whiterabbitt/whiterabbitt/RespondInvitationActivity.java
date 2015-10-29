package com.whiterabbitt.whiterabbitt;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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

// This class shows when user opens the app from the notification
public class RespondInvitationActivity  extends AppCompatActivity{

    String combined; // To combine Strings

    // For the debugging purpose
    public final String TAG = this.getClass().getSimpleName();

    ProgressDialog dialog;

    TextView title;
    TextView time_label;
    TextView location_label;
    TextView from_label;
    TextView reward_label;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respond_invitation);

        title = (TextView) findViewById(R.id.title);
        time_label = (TextView) findViewById(R.id.time_label);
        location_label = (TextView) findViewById(R.id.location_label);
        from_label = (TextView) findViewById(R.id.from_label);
        reward_label = (TextView) findViewById(R.id.reward_label);

        Intent intent = getIntent();
        final String username;
        final String objectId;
        String temp = ""; // To hold the username
        String temp2 = ""; // To hold the objectId

        if(intent != null) {
            temp = intent.getStringExtra("username");
            temp2 = intent.getStringExtra("objectId");

            // Get all the necessary fields from the previous activity
            title.setText((String) intent.getExtras().get("title"));
            String time = (String) intent.getExtras().get("time");
            String date = (String) intent.getExtras().get("date");

            time_label.setText(time + " / " + date.toUpperCase());
            location_label.setText(((String) intent.getExtras().get("location")).replaceAll("\n", ", "));
            from_label.setText("From: " + intent.getExtras().get("name"));
            reward_label.setText("Reward: " + intent.getExtras().get("reward"));
        }

        Button acceptBtn = (Button) findViewById(R.id.acceptBtn);
        Button declineBtn = (Button) findViewById(R.id.declineBtn);

        username = temp;
        objectId = temp2;

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
                pushQuery.whereEqualTo("user", username);

                // Send push notification back to the sender, who has sent the invitation
                ParsePush push = new ParsePush();
                JSONObject data = new JSONObject();
                try {
                    data.put("title", ParseUser.getCurrentUser().get("firstName") + " replies...");
                    data.put("alert", "I would love to meet up with you :)");

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

                        if (e == null) {
                            // stateNum - 1 is to change the indicator light in the main event page
                            parseObject.put("accepted", ((Integer) parseObject.get("accepted")) + 1);

                            // This is to subscribe this user for the invitation so that this user can display the received invitation on his main event page
                            parseObject.put("ownerID", parseObject.get("ownerID") + ":" + ParseUser.getCurrentUser().getObjectId());

                            ParseUser.getCurrentUser().addUnique("eventList", objectId);
                            ParseUser.getCurrentUser().saveInBackground();

                            parseObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {

                                    dialog.dismiss();

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
                pushQuery.whereEqualTo("user", username);

                // Send push notification back to the sender, who has sent the invitation
                ParsePush push = new ParsePush();
                JSONObject data = new JSONObject();
                try {
                    data.put("title", ParseUser.getCurrentUser().get("firstName") + " replies...");
                    data.put("alert", "Sorry! Definitely next time :(");

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
                            parseObject.put("declined", ((Integer) parseObject.get("declined")) + 1);

                            // Unsubscribe the current user from the event
                            String temp = (String) parseObject.get("ownerID");
                            String myID = ParseUser.getCurrentUser().getObjectId();
                            temp = temp.replace(myID, "");
                            parseObject.put("ownerID", temp);

                            parseObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
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

    @Override
    public void onBackPressed() {
        return;
    }
}
