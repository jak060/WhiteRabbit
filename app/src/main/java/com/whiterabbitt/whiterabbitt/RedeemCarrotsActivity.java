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
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * This class deals with redeeming user's carrots
 */
public class RedeemCarrotsActivity extends AppCompatActivity{

    private static final String ERROR_INVALID_NUMBER = "Number cannot be zero";
    private static final String ERROR_TOO_MANY_CARROTS = "Donating too many carrots!";

    TextView current_carrots_label; // To hold the current number of carrots that user has
    TextView carrots_to_donate_label; // To hold the number of carrots that user wants to donate
    TextView amount_of_donate_label; // To hold the amount of $ to donate
    Button plus_button; // This button increments the amount of $ to donate
    Button minus_button; // This button decrements the amount of $ to donate
    Integer amount_counter = 0; // To count the amount of $ to donate
    Integer carrot_counter = 0; // To count the number of carrots that user wants to donate
    Integer num_of_current_carrots = 0;// To count the number of carrots that user currently has

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem_carrots);

        // Initialization of variables
        current_carrots_label = (TextView) findViewById(R.id.current_num_carrots);
        carrots_to_donate_label = (TextView) findViewById(R.id.num_carrots);
        amount_of_donate_label = (TextView) findViewById(R.id.amount);
        plus_button = (Button) findViewById(R.id.plus_button);
        minus_button = (Button) findViewById(R.id.minus_button);

        Intent intent = getIntent();

        // Set the initial values
        num_of_current_carrots = intent.getIntExtra("carrots", 0);
        current_carrots_label.setText(num_of_current_carrots.toString());
        carrots_to_donate_label.setText("0");
        amount_of_donate_label.setText("$0");

        // When the user clicks plus button. . .
        plus_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                carrots_to_donate_label.setError(null);

                // Increment the amount by 1
                amount_counter ++;

                // Increment the carrots by 1000
                carrot_counter = carrot_counter + 1000;

                // If the number of carrots you have is greater than or equal to the
                // number of carrots you want to donate, set the text color to be green
                if((num_of_current_carrots - carrot_counter) >= 0) {
                    carrots_to_donate_label.setTextColor(getResources().getColor(R.color.event_accepted));
                }

                // Otherwise, set it to red
                else {
                    carrots_to_donate_label.setTextColor(Color.parseColor("#CC0000"));
                }

                // Set those updated numbers to each TextView
                carrots_to_donate_label.setText(carrot_counter.toString());
                amount_of_donate_label.setText("$" + amount_counter.toString());
            }
        });

        // When the user clicks minus button. . .
        minus_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                carrots_to_donate_label.setError(null);

                // Decrement the amount by 1
                amount_counter --;

                // Decrement the carrots by 1000
                carrot_counter = carrot_counter - 1000;

                // To block the case that it goes to the negative number
                if(amount_counter < 0) {
                    amount_counter = 0;
                }
                if(carrot_counter < 0) {
                    carrot_counter = 0;
                }

                // If the number of carrots you have is greater than
                // number of carrots you want to donate, set the text color to be green
                if((num_of_current_carrots - carrot_counter) > 0 && carrot_counter != 0) {
                    carrots_to_donate_label.setTextColor(getResources().getColor(R.color.event_accepted));
                }

                // Else if the number of carrots you have is equal to the number of carrots
                // you want to donate, set the text color to be black
                else if (carrot_counter == 0) {
                    carrots_to_donate_label.setTextColor(getResources().getColor(R.color.event_won));
                }

                // Otherwise, set it to red
                else {
                    carrots_to_donate_label.setTextColor(Color.parseColor("#CC0000"));
                }

                // Set those updated numbers to each TextView
                carrots_to_donate_label.setText(carrot_counter.toString());
                amount_of_donate_label.setText("$" + amount_counter.toString());
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_donation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_donate) {
            if((num_of_current_carrots - carrot_counter) > 0 && carrot_counter != 0) {
                updateParseUser();
            }

            else if(carrot_counter == 0) {
                carrots_to_donate_label.setError(ERROR_INVALID_NUMBER);
            }

            else{
                carrots_to_donate_label.setError(ERROR_TOO_MANY_CARROTS);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // This method updates user's number of carrots and donation amount
    private void updateParseUser() {

        final ProgressDialog dialog = new ProgressDialog(RedeemCarrotsActivity.this);
        dialog.setTitle("Thank You For Your Kindness");
        dialog.setMessage("Processing Donation. . .");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        ParseUser.getCurrentUser().fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    final Integer updated_number_of_carrots = num_of_current_carrots - carrot_counter;
                    ParseUser.getCurrentUser().put("carrots", updated_number_of_carrots);
                    final Integer prevAmount = (Integer) ParseUser.getCurrentUser().get("donationPoints");
                    final Integer currentAmount = prevAmount + amount_counter;
                    ParseUser.getCurrentUser().put("donationPoints", currentAmount);

                    ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null) {
                                dialog.dismiss();
                                Intent intent = new Intent(getApplicationContext(), DonationSuccessActivity.class);
                                intent.putExtra("donatedAmount", amount_counter);
                                intent.putExtra("updatedNumberOfCarrots", updated_number_of_carrots);
                                intent.putExtra("currentDonationPoints", currentAmount);
                                intent.putExtra("level", (int) ParseUser.getCurrentUser().get("rankPoints"));
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
