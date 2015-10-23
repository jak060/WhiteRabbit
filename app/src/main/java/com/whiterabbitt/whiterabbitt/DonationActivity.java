package com.whiterabbitt.whiterabbitt;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * This class deals with getting donations from the user
 */

public class DonationActivity extends AppCompatActivity {

    private static final String ERROR_INVALID_NUMBER = "Number cannot be zero";
    private static final String ERROR_TOO_MUCH_CARROT = "Donating too many carrot!";

    Integer currentNumOfCarrots = 0; // To hold the current number of carrots of the user
    Integer numOfCarrots = 0; // To hold the number of carrots that user chooses
    EditText numCarrotsView; // To display the number of carrots that user chooses
    Integer amount = 0; // To hold the total amount of donation money

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation);

        // Remove drop shadow from action bar on Lollipop
        getSupportActionBar().setElevation(0);

        Intent intent = getIntent();

        // Get the current number for carrots of the user from the last activity
        currentNumOfCarrots = (Integer) intent.getExtras().get("carrots");

        // To display the total amount of donation money
        final TextView amountView = (TextView) findViewById(R.id.amount);
        numCarrotsView = (EditText) findViewById(R.id.num_carrots);

        // To display the current number of carrots of the user
        final TextView showCurrentNumOfCarrots = (TextView) findViewById(R.id.current_num_carrots);
        showCurrentNumOfCarrots.setText(currentNumOfCarrots.toString());

        // When the user types the number of carrots that he/she wants to donate. . .
        numCarrotsView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // Disable the error message if there is any error
                numCarrotsView.setError(null);

                // Convert the string to integer
                numOfCarrots = convertStringToInt(numCarrotsView.getText().toString());
                int dollarsPerCarrot = 1;

                // Calculate the total amount of donation money
                amount = calculateAmount(numOfCarrots, dollarsPerCarrot);

                // Finally, display the total amount of donation money
                amountView.setText(String.format("$%d", amount));
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
            if(numOfCarrots <= 0) {
                numCarrotsView.setError(ERROR_INVALID_NUMBER);
            }

            if(currentNumOfCarrots < numOfCarrots) {
                numCarrotsView.setError(ERROR_TOO_MUCH_CARROT);
            }

            if(currentNumOfCarrots >= numOfCarrots && numOfCarrots > 0){
                updateParseUser();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // This method converts string to integer
    private int convertStringToInt(String string) {
        int value;

        if(string.equals("")) {
            value = 0;
        }
        else {
            value = Integer.parseInt(string);
        }

        return value;
    }

    // This method calculates the total amount of the donation money
    private int calculateAmount(int numCarrots, int dollarsPerCarrot) {
        return numCarrots * dollarsPerCarrot;
    }

    // This method updates user's number of carrots and donation amount
    private void updateParseUser() {

        final ProgressDialog dialog = new ProgressDialog(DonationActivity.this);
        dialog.setTitle("Thank You For Your Kindness");
        dialog.setMessage("Processing Donation. . .");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        ParseUser.getCurrentUser().fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    final Integer prevNumOfCarrots = currentNumOfCarrots;
                    currentNumOfCarrots = currentNumOfCarrots - numOfCarrots;
                    ParseUser.getCurrentUser().put("carrots", currentNumOfCarrots);
                    final Integer prevAmount = (Integer) ParseUser.getCurrentUser().get("donationPoints");
                    final Integer currentAmount = prevAmount + amount;
                    ParseUser.getCurrentUser().put("donationPoints", currentAmount);

                    ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null) {
                                dialog.dismiss();
                                Intent intent = new Intent(getApplicationContext(), DonationSuccessActivity.class);
                                intent.putExtra("prevCarrots", prevNumOfCarrots);
                                intent.putExtra("prevDonationPoints", prevAmount);
                                intent.putExtra("currentCarrots", currentNumOfCarrots);
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
