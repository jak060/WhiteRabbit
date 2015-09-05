package com.example.whiterabbit;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.DecimalFormat;
import java.util.List;

public class MyProfileFragment extends Fragment {
    private String title;
    private int pageNum;

    TextView nameHolder;
    TextView phoneNumberHolder;
    TextView usernameHolder;
    TextView carrotsHolder;
    TextView rankPointsHolder;
    TextView punctualityHolder;

    ProgressBar progressBar;

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
        View view = inflater.inflate(R.layout.activity_my_profile, viewGroup, false);

        // Initialize TextViews
        nameHolder = (TextView) view.findViewById(R.id.name);
        phoneNumberHolder = (TextView) view.findViewById(R.id.phone_number);
        usernameHolder = (TextView) view.findViewById(R.id.email);
        carrotsHolder = (TextView) view.findViewById(R.id.carrots_value);
        rankPointsHolder = (TextView) view.findViewById(R.id.rank_value);
        punctualityHolder = (TextView) view.findViewById(R.id.punctuality);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        ParseUser.getCurrentUser().fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    phoneNumberHolder.setText(Utility.phoneNumberFormat((String) parseObject.get("phoneNumber")));
                    usernameHolder.setText((String) parseObject.get("username"));
                    rankPointsHolder.setText((parseObject.get("rankPoints")).toString());
                    carrotsHolder.setText(parseObject.get("carrots").toString());
                    double punc = (((Integer) parseObject.get("rankPoints") * 1.0) / (Integer) parseObject.get("attempts")) * 100;

                    DecimalFormat decimalFormat = new DecimalFormat("###.##");

                    Log.v(TAG, "Punctuality: " + decimalFormat.format(punc) + "%");
                    punctualityHolder.setText("Punctuality: " + decimalFormat.format(punc) + "%");
                    progressBar.setProgress((int) punc);
                } else {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }
}
