package com.whiterabbitt.whiterabbitt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * This class deals with bringing and showing friend's information on the page
 */

public class ContactActivity extends AppCompatActivity {

    TextView nameHolder; // To hold the name of the user
    TextView phoneNumberHolder; // To hold the phone number of the user
    TextView usernameHolder; // To hold the user name of the user
    TextView carrotsHolder; // To hold the # of carrots that user currently has
    TextView rankPointsHolder; // To hold the current rank points of the user
    TextView donationHolder; // To hold the amount of donation money that he has donated

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        // This animates page to slide up as if it's a new pop-up
        overridePendingTransition(R.anim.slide_up, R.anim.no_change);

        // Initialize TextViews
        nameHolder = (TextView) findViewById(R.id.name);
        phoneNumberHolder = (TextView) findViewById(R.id.phone_number);
        usernameHolder = (TextView) findViewById(R.id.email);
        carrotsHolder = (TextView) findViewById(R.id.carrots_value);
        rankPointsHolder = (TextView) findViewById(R.id.level_value);
        donationHolder = (TextView) findViewById(R.id.donation_value);

        // Get chosen user's details from the last activity
        Intent intent = getIntent();
        String firstName = (String) intent.getExtras().get("firstName");
        String lastName = (String) intent.getExtras().get("lastName");
        String phoneNumber = (String) intent.getExtras().get("phoneNumber");
        phoneNumber = Utility.phoneNumberFormat(phoneNumber);
        String username = (String) intent.getExtras().get("username");
        Integer carrots = (Integer) intent.getExtras().get("carrots");
        Integer rankPoints = (Integer) intent.getExtras().get("rankPoints");
        Integer donationPoints = (Integer) intent.getExtras().get("donationPoints");

        // Set TextViews with details of the selected user
        nameHolder.setText(firstName + " " + lastName);
        phoneNumberHolder.setText(phoneNumber);
        usernameHolder.setText(username);
        carrotsHolder.setText(carrots.toString());
        rankPointsHolder.setText(rankPoints.toString());
        donationHolder.setText("$" + donationPoints.toString());

    }

    // When user presses back button. . .
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // This animates page to slide down
        overridePendingTransition(R.anim.no_change, R.anim.slide_down);
    }

}
