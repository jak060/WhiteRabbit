package com.example.whiterabbit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ContactActivity extends AppCompatActivity {

    TextView firstNameHolder;
    TextView lastNameHolder;
    TextView phoneNumberHolder;
    TextView usernameHolder;
    TextView carrotsHolder;
    TextView rankPointsHolder;
    TextView donationHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        overridePendingTransition(R.anim.slide_up, R.anim.no_change);

        // Initialize TextViews
        firstNameHolder = (TextView) findViewById(R.id.first_name);
        lastNameHolder = (TextView) findViewById(R.id.last_name);
        phoneNumberHolder = (TextView) findViewById(R.id.phone_number);
        usernameHolder = (TextView) findViewById(R.id.email);
        carrotsHolder = (TextView) findViewById(R.id.carrots_value);
        rankPointsHolder = (TextView) findViewById(R.id.rank_value);
        donationHolder = (TextView) findViewById(R.id.donation_points);

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
        firstNameHolder.setText(firstName);
        lastNameHolder.setText(lastName);
        phoneNumberHolder.setText(phoneNumber);
        usernameHolder.setText(username);
        carrotsHolder.setText(carrots.toString());
        rankPointsHolder.setText(rankPoints.toString());
        donationHolder.setText("$" + donationPoints.toString());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.no_change, R.anim.slide_down);
    }

}
