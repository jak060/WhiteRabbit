package com.example.whiterabbit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * This class deals with displaying and handling reward page for late comers
 */
public class RewardLoserActivity extends AppCompatActivity{

    // For the debugging purpose
    public final String TAG = this.getClass().getSimpleName();
    String objectId = "";
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_loser);

        Button okButton = (Button) findViewById(R.id.button);

        Intent intent = getIntent();

        if(intent != null) {
            objectId = intent.getExtras().getString("objectId");
            Log.v(TAG, "ObjectId For Loser: " + objectId);
        }

        okButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // First of all show the progress dialog to make the user wait
                dialog = new ProgressDialog(RewardLoserActivity.this);
                dialog.setTitle("Thank You Using Our App :)");
                dialog.setMessage("Going Back To Main. . .");
                dialog.show();
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(Constants.GEOFENCES_REGISTERED_KEY, false);
                editor.commit();

                SharedPreferences prefs2 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor2 = prefs2.edit();
                editor2.putBoolean(Constants.GEOFENCES_TRANSITION_INTENT_ENTERED_KEY, false);
                editor2.commit();

                ParseQuery<ParseObject> invitationInfo = ParseQuery.getQuery("invitationInfo");
                invitationInfo.getInBackground(objectId, new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {
                        if (e == null) {
                            String temp = (String) parseObject.get("ownerID");
                            String myID = ParseUser.getCurrentUser().getObjectId();
                            temp = temp.replace(myID, "");
                            parseObject.put("ownerID", temp);

                            parseObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {

                                    dialog.dismiss();

                                    if (e == null) {
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                    } else {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }

                        else {
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
