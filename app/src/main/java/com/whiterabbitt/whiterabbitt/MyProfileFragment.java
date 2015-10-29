package com.whiterabbitt.whiterabbitt;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.text.DecimalFormat;

/**
 * This fragment deals with handling and displaying my profile
 */
public class MyProfileFragment extends Fragment {
    private String title;
    private int pageNum;

    TextView nameHolder; // To hold the name of the user
    TextView phoneNumberHolder; // To hold the phone number of the user
    TextView usernameHolder; // To hold the user name of the user
    TextView carrotsHolder; // To hold the current # of carrots that user has
    TextView rankPointsHolder; // To hold the current rank points of the user
    TextView punctualityHolder; // To hold the punctuality of the user
    TextView donationHolder; // To hold the donation amount that user has donated

    ProgressBar progressBar;

    Integer numOfCarrots = 0;

    // For the debugging purpose
    public final String TAG = this.getClass().getSimpleName();

    public static MyProfileFragment newInstance(String title, int pageNum) {

        MyProfileFragment myProfileFragment = new MyProfileFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("pageNumber", pageNum);
        bundle.putString("pageTitle", title);

        myProfileFragment.setArguments(bundle);
        return myProfileFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNum = getArguments().getInt("pageNumber", 2);
        title = getArguments().getString("pageTitle");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_profile, viewGroup, false);

        // Initialize TextViews
        nameHolder = (TextView) view.findViewById(R.id.name);
        phoneNumberHolder = (TextView) view.findViewById(R.id.phone_number);
        usernameHolder = (TextView) view.findViewById(R.id.email);
        carrotsHolder = (TextView) view.findViewById(R.id.carrots_value);
        rankPointsHolder = (TextView) view.findViewById(R.id.level_value);
        punctualityHolder = (TextView) view.findViewById(R.id.punctuality);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        donationHolder = (TextView) view.findViewById(R.id.donation_value);

        // This should brings and sets necessary information of the user
        fetchUserDateFromParse();

        // If user clicks the donation carrot button, then start that activity
        Button btnDonateCarrots = (Button) view.findViewById(R.id.btn_donate_carrots);
        btnDonateCarrots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DonationActivity.class);
                intent.putExtra("carrots", numOfCarrots);
                startActivity(intent);
            }
        });

        // If user clicks the redeem carrot button, then start that activity
        Button btnRedeemCarrots = (Button) view.findViewById(R.id.btn_redeem_carrots);
        btnRedeemCarrots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RedeemCarrotsActivity.class);
                intent.putExtra("carrots", numOfCarrots);
                startActivity(intent);
            }
        });

        return view;
    }

    // This method brings necessary information of the user from the database, and
    // sets those information into the right text fields
    private void fetchUserDateFromParse() {
        ParseUser.getCurrentUser().fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {

                    // Set up each TextView field
                    nameHolder.setText(parseObject.get("firstName") + " " + parseObject.get("lastName"));
                    phoneNumberHolder.setText(Utility.phoneNumberFormat((String) parseObject.get("phoneNumber")));
                    usernameHolder.setText((String) parseObject.get("username"));
                    rankPointsHolder.setText((parseObject.get("rankPoints")).toString());
                    numOfCarrots = (Integer) parseObject.get("carrots");
                    carrotsHolder.setText(parseObject.get("carrots").toString());
                    donationHolder.setText("$" + parseObject.get("donationPoints").toString());

                    // This is to calculate the punctuality
                    double punc = (((Integer) parseObject.get("rankPoints") * 1.0) / (Integer) parseObject.get("attempts")) * 100;

                    // To take care of NaN case
                    if(Double.isNaN(punc)) {
                        punc = 0.0;
                    }

                    // To format the decimal number to be something like 100.00
                    DecimalFormat decimalFormat = new DecimalFormat("###.##");

                    // Set up the punctuality
                    Log.v(TAG, "Punctuality: " + decimalFormat.format(punc) + "%");
                    punctualityHolder.setText("Punctuality: " + decimalFormat.format(punc) + "%");
                    progressBar.setProgress((int) punc);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
