package com.example.whiterabbit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// This class shows when user opens the app from the notification
public class RespondInvitationActivity  extends AppCompatActivity{
    ArrayList<String> newList = new ArrayList<String>();
    String combined;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respond_invitation);
        Intent intent = getIntent();
        final String phoneNumber;
        final String objectId;
        final String name;
        String temp = "";
        String temp2 = "";
        String temp3 = "";


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

        combined = name + ": " + phoneNumber;

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseQuery pushQuery = ParseInstallation.getQuery();
                pushQuery.whereEqualTo("user", phoneNumber);
                final InvitationInfoActivity invitationInfoActivity = new InvitationInfoActivity();
                // Send push notification to query
                ParsePush push = new ParsePush();
                JSONObject data = new JSONObject();
                try {
                    data.put("alert", "Accepted!");
                    data.put("indicator", "Accept");

                } catch(JSONException e) {
                    e.printStackTrace();
                }
                push.setQuery(pushQuery); // Set our Installation query
                push.setData(data);
                push.sendInBackground();

                ParseQuery<ParseObject> invitationInfo = ParseQuery.getQuery("invitationInfo");
                invitationInfo.whereEqualTo("objectId", objectId);
                invitationInfo.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                        //InvitationInfoActivity invitationInfoActivity = new InvitationInfoActivity();
                        invitationInfoActivity.setTitle((String) list.get(0).get("title"));
                        invitationInfoActivity.setTime((String) list.get(0).get("time"));
                        invitationInfoActivity.setDate((String) list.get(0).get("date"));
                        invitationInfoActivity.setLocation((String) list.get(0).get("location"));
                        invitationInfoActivity.setWith((ArrayList) list.get(0).get("invitees"));
                        newList = (ArrayList) list.get(0).get("invitees");

                        for(int i = 0; i < newList.size(); i++) {
                            newList.remove(0);
                        }

                        newList.add(combined);
                        ParseObject invitationInfomation = new ParseObject("invitationInfo");
                        invitationInfomation.put("title", invitationInfoActivity.getTitle());
                        invitationInfomation.put("time", invitationInfoActivity.getTime());
                        invitationInfomation.put("date", invitationInfoActivity.getDate());
                        invitationInfomation.put("location", invitationInfoActivity.getLocation());
                        invitationInfomation.put("invitees", newList);
                        invitationInfomation.put("owner", ParseUser.getCurrentUser());

                        invitationInfomation.saveInBackground();

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                });


            }
        });

        declineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
