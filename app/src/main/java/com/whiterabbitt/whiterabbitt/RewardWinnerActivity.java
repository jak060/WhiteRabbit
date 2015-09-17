package com.whiterabbitt.whiterabbitt;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * This class deals with displaying and handling reward page for winners
 */
public class RewardWinnerActivity extends AppCompatActivity{

    // For the debugging purpose
    public final String TAG = this.getClass().getSimpleName();
    String objectId = "";
    ProgressDialog dialog;

    TextView prevCarrotView;
    TextView prevRankView;
    TextView currCarrotView;
    TextView currRankView;
    TextView resultTextView;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_winner);

        Intent intent = getIntent();

        if(intent != null) {
            objectId = intent.getExtras().getString("objectId");
            Log.v(TAG, "ObjectId For Winner: " + objectId);
        }

        // Initializing Views
        prevCarrotView = (TextView) findViewById(R.id.carrots_value);
        prevRankView = (TextView) findViewById(R.id.rank_value);
        currCarrotView = (TextView) findViewById(R.id.carrots_value2);
        currRankView = (TextView) findViewById(R.id.rank_value2);
        resultTextView = (TextView) findViewById(R.id.resultText);

        // Update the parse
        updateParse();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reward_winner_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_close) {
            if(intent != null) {

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

                startActivity(intent);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

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

        if(intent != null) {
            startActivity(intent);
        }

    }

    private void updateParse() {
        // First of all show the progress dialog to make the user wait
        dialog = new ProgressDialog(RewardWinnerActivity.this);
        dialog.setTitle("Thank You Using Our App :)");
        dialog.setMessage("Bringing out the result. . .");
        dialog.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        // ObjectId is needed for unsubscribing an user from the event
        ParseQuery<ParseObject> invitationInfo = ParseQuery.getQuery("invitationInfo");
        invitationInfo.getInBackground(objectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {

                    // Add the number of carrots, which user has predefined
                    String stringCarrots = (String) parseObject.get("carrots");
                    String numberOfCarrots = (String) stringCarrots.substring(0, 2);
                    Integer totalCarrots = (Integer) ParseUser.getCurrentUser().get("carrots");
                    prevCarrotView.setText(totalCarrots.toString());

                    totalCarrots = totalCarrots + Integer.parseInt(numberOfCarrots);
                    currCarrotView.setText(totalCarrots.toString());

                    resultTextView.setText("You have earned " + Integer.parseInt(numberOfCarrots) + " carrots!");

                    // Increment the total number of attempts
                    Integer numOfAttempts = (Integer) ParseUser.getCurrentUser().get("attempts");
                    numOfAttempts = numOfAttempts + 1;

                    // Increment the rank points
                    Integer rankPoints = (Integer) ParseUser.getCurrentUser().get("rankPoints");
                    prevRankView.setText(rankPoints.toString());

                    rankPoints = rankPoints + 1;
                    currRankView.setText(rankPoints.toString());

                    // Update the number of carrots
                    ParseUser.getCurrentUser().put("carrots", totalCarrots);

                    // Update the total number of attempts
                    ParseUser.getCurrentUser().put("attempts", numOfAttempts);

                    Log.v(TAG, "numOfAttempts" + numOfAttempts);

                    // Update the total rank points
                    // Update the total number of attempts
                    ParseUser.getCurrentUser().put("rankPoints", rankPoints);

                    // Save
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
                                intent = new Intent(getApplicationContext(), MainActivity.class);

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
