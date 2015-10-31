package com.whiterabbitt.whiterabbitt;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

/**
 * This class deals with redeeming user's carrots
 */
public class RedeemCarrotsActivity extends AppCompatActivity{

    private static final String ERROR_INVALID_NUMBER = "Number cannot be zero";
    private static final String ERROR_TOO_MANY_CARROTS = "Donating too many carrots!";
    private static final String ERROR_EMPTY_FIELD = "Field cannot be left blank.";

    // For the debugging purpose
    public final String TAG = this.getClass().getSimpleName();

    TextView current_carrots_label; // To hold the current number of carrots that user has
    TextView carrots_to_use_label; // To hold the number of carrots that user wants to donate
    TextView amount_of_redeem_label; // To hold the amount of $ to donate
    TextView people_label; // To hold the label of the receiver of the gift card
    TextView receiver; // To hold the actual receiver of the gift card
    Button plus_button; // This button increments the amount of $ to donate
    Button minus_button; // This button decrements the amount of $ to donate
    Integer amount_counter = 0; // To count the amount of $ to donate
    Integer carrot_counter = 0; // To count the number of carrots that user wants to donate
    Integer num_of_current_carrots = 0;// To count the number of carrots that user currently has
    ArrayList<String> friendList = new ArrayList<String>();
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem_carrots);

        // Initialization of variables
        current_carrots_label = (TextView) findViewById(R.id.current_num_carrots);
        carrots_to_use_label = (TextView) findViewById(R.id.num_carrots);
        amount_of_redeem_label = (TextView) findViewById(R.id.amount);
        people_label = (TextView) findViewById(R.id.people_label);
        receiver = (TextView) findViewById(R.id.inviteesTextView);
        plus_button = (Button) findViewById(R.id.plus_button);
        minus_button = (Button) findViewById(R.id.minus_button);

        Intent intent = getIntent();

        // Set the initial values
        num_of_current_carrots = intent.getIntExtra("carrots", 0);
        current_carrots_label.setText(num_of_current_carrots.toString());
        carrots_to_use_label.setText("0");
        amount_of_redeem_label.setText("$0");

        // When the user clicks plus button. . .
        plus_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                carrots_to_use_label.setError(null);

                // Increment the amount by 1
                amount_counter++;

                // Increment the carrots by 1000
                carrot_counter = carrot_counter + 500;

                // If the number of carrots you have is greater than or equal to the
                // number of carrots you want to donate, set the text color to be green
                if ((num_of_current_carrots - carrot_counter) >= 0) {
                    carrots_to_use_label.setTextColor(getResources().getColor(R.color.event_accepted));
                }

                // Otherwise, set it to red
                else {
                    carrots_to_use_label.setTextColor(Color.parseColor("#CC0000"));
                }

                // Set those updated numbers to each TextView
                carrots_to_use_label.setText(carrot_counter.toString());
                amount_of_redeem_label.setText("$" + amount_counter.toString());
            }
        });

        // When the user clicks minus button. . .
        minus_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                carrots_to_use_label.setError(null);

                // Decrement the amount by 1
                amount_counter--;

                // Decrement the carrots by 1000
                carrot_counter = carrot_counter - 500;

                // To block the case that it goes to the negative number
                if (amount_counter < 0) {
                    amount_counter = 0;
                }
                if (carrot_counter < 0) {
                    carrot_counter = 0;
                }

                // If the number of carrots you have is greater than
                // number of carrots you want to donate, set the text color to be green
                if ((num_of_current_carrots - carrot_counter) > 0 && carrot_counter != 0) {
                    carrots_to_use_label.setTextColor(getResources().getColor(R.color.event_accepted));
                }

                // Else if the number of carrots you have is equal to the number of carrots
                // you want to donate, set the text color to be black
                else if (carrot_counter == 0) {
                    carrots_to_use_label.setTextColor(getResources().getColor(R.color.event_won));
                }

                // Otherwise, set it to red
                else {
                    carrots_to_use_label.setTextColor(Color.parseColor("#CC0000"));
                }

                // Set those updated numbers to each TextView
                carrots_to_use_label.setText(carrot_counter.toString());
                amount_of_redeem_label.setText("$" + amount_counter.toString());
            }
        });

        // When the People TextView is clicked, show the friends list
        people_label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelectInviteeActivity.class);
                intent.setAction("Single");
                startActivityForResult(intent, 1);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_redeem_carrots, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_donate) {
            // If the receiver field is empty, then set the error
            if(receiver.getText().length() <= 0) {
                people_label.setError(ERROR_EMPTY_FIELD);
            }

            // If the carrots to use is 0, then set the error
            if(carrot_counter == 0) {
                carrots_to_use_label.setError(ERROR_INVALID_NUMBER);
            }

            // Else if carrots to use is greater than the number of carrots you have, set the error
            else if((num_of_current_carrots - carrot_counter) < 0){
                carrots_to_use_label.setError(ERROR_TOO_MANY_CARROTS);
            }

            // If all of these conditions are met, then proceed to saving date to the database
            if((num_of_current_carrots - carrot_counter) > 0 && carrot_counter != 0 && receiver.getText().length() > 0) {
                saveGiftCardTransactionToParse();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // This method brings a user-typed address and displays that address
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1) {
            if(resultCode == RESULT_OK) {
                people_label.setError(null);
                StringBuilder temp = new StringBuilder();
                friendList = data.getStringArrayListExtra("invitees");

                for(int i = 0; i < friendList.size(); i++) {
                    temp = temp.append(friendList.get(i) + "\n");
                }
                people_label.setText("");
                receiver.setText("To: " + temp);
            }
        }
    }

    // This method saves sender, receiver, and the amount of the gift card to the database
    private void saveGiftCardTransactionToParse() {

        final ProgressDialog dialog = new ProgressDialog(RedeemCarrotsActivity.this);
        dialog.setTitle("Thank You For Your Kindness");
        dialog.setMessage("Processing Donation. . .");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        ParseUser.getCurrentUser().put("carrots", num_of_current_carrots - carrot_counter);
        ParseUser.getCurrentUser().saveInBackground();

        String receiverInfo = receiver.getText().toString();

        // Get only phone number
        username = receiverInfo.substring(receiverInfo.indexOf("(") + 1, receiverInfo.lastIndexOf(")"));

        Log.v(TAG, "Username for receiver: " + username);

        // Search the database that matches the receiver's phone number
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", username);
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    // Put these gift card transaction information into Parse
                    final ParseObject giftCardInfo = new ParseObject("giftCardInfo");
                    giftCardInfo.put("sender", ParseUser.getCurrentUser());
                    giftCardInfo.put("amount", amount_counter);
                    giftCardInfo.put("receiver", object.getObjectId());

                    // Send push notification back to the sender, who has sent the invitation
                    ParsePush push = new ParsePush();
                    JSONObject data = new JSONObject();
                    try {
                        data.put("title", "Wow! You got a gift!");
                        data.put("alert", "What a happy day :)");

                    } catch(JSONException e1) {
                        e.printStackTrace();
                    }

                    // Use this query to get the sender's information
                    ParseQuery pushQuery = ParseInstallation.getQuery();
                    pushQuery.whereEqualTo("user", username);
                    push.setQuery(pushQuery); // Set our Installation query
                    push.setData(data);
                    push.sendInBackground();

                    giftCardInfo.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null) {
                                dialog.dismiss();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            } else {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    e.printStackTrace();
                }
            }
        });


    }

}
