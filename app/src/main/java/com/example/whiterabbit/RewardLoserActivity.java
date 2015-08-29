package com.example.whiterabbit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

        Intent intent = getIntent();

        if(intent != null) {
            objectId = intent.getExtras().getString("objectId");
            Log.v(TAG, "ObjectId For Loser: " + objectId);
        }

        // Reset the boolean value of GEOFENCES_REGISTERED_KEY
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Constants.GEOFENCES_REGISTERED_KEY, false);
        editor.commit();

        // Reset the boolean value of GEOFENCES_TRANSITION_INTENT_ENTERED_KEY
        SharedPreferences prefs2 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor2 = prefs2.edit();
        editor2.putBoolean(Constants.GEOFENCES_TRANSITION_INTENT_ENTERED_KEY, false);
        editor2.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reward_loser_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_close) {
            updateParse();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        updateParse();
    }

    private void updateParse() {
        // First of all show the progress dialog to make the user wait
        dialog = new ProgressDialog(RewardLoserActivity.this);
        dialog.setTitle("Thank You Using Our App :)");
        dialog.setMessage("Going Back To Main. . .");
        dialog.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        // ObjectId is needed for unsubscribing an user from the event
        ParseQuery<ParseObject> invitationInfo = ParseQuery.getQuery("invitationInfo");
        invitationInfo.getInBackground(objectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {

                    // Subtract the number of carrots, which user has predefined
                    String stringCarrots = (String) parseObject.get("carrots");
                    String numberOfCarrots = (String) stringCarrots.substring(0, 1);
                    Integer totalCarrots = (Integer) ParseUser.getCurrentUser().get("carrots");
                    totalCarrots = totalCarrots - Integer.parseInt(numberOfCarrots);

                    // Increment the total number of attempts
                    Integer numOfAttempts = (Integer) ParseUser.getCurrentUser().get("attempts");
                    numOfAttempts = numOfAttempts + 1;

                    // Update the number of carrots
                    ParseUser.getCurrentUser().put("carrots", totalCarrots);

                    // Update the total number of attempts
                    ParseUser.getCurrentUser().put("attempts", numOfAttempts);
                    ParseUser.getCurrentUser().saveInBackground();

                    // Unsubscribe the current user from the event
                    String temp = (String) parseObject.get("ownerID");
                    String myID = ParseUser.getCurrentUser().getObjectId();
                    temp = temp.replace(myID, "");
                    parseObject.put("ownerID", temp);

                    // Save the change back to the db
                    parseObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {

                            // Dismiss the progress dialog
                            dialog.dismiss();

                            if (e == null) {
                                // If no error, then start the main activity
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
}
