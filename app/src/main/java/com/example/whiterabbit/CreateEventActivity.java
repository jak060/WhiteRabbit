package com.example.whiterabbit;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CreateEventActivity extends AppCompatActivity {

    // For the debugging purpose
    public final String TAG = this.getClass().getSimpleName();

    ArrayList<String> friendList = new ArrayList<String>();
    ArrayList<String> phoneNumbers = new ArrayList<String>();

    EditText title;

    TextView timeLabel;
    TextView dateLabel;
    TextView locationLabel;
    TextView peopleLabel;

    static TextView showTime;
    static TextView showDate;

    TextView showLocation;
    TextView showInvitees;

    public ProgressDialog dialog;

    double lat;
    double lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        // Clearing these lists before using them
        friendList.clear();
        phoneNumbers.clear();

        // Initialization of variables
        title = (EditText) findViewById(R.id.title);

        timeLabel = (TextView) findViewById(R.id.time_label);
        dateLabel = (TextView) findViewById(R.id.date_label);
        locationLabel = (TextView) findViewById(R.id.location_label);
        peopleLabel = (TextView) findViewById(R.id.people_label);

        showTime = (TextView) findViewById(R.id.showTime);
        showTime.setText(java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT).format(new Date()));

        showDate = (TextView) findViewById(R.id.showDate);
        showDate.setText(java.text.DateFormat.getDateInstance().format(new Date()));

        showLocation = (TextView) findViewById(R.id.chosenLocationText);
        showInvitees = (TextView) findViewById(R.id.inviteesTextView);

        Button sendBtn = (Button) findViewById(R.id.sendBtn);

        // When the Time TextView is clicked, show the time picker
        timeLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogFragment = new TimePickerFragment();
                dialogFragment.show(getFragmentManager(), "timePicker");
            }
        });

        // When the Date TextView is clicked, show the date picker
        dateLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogFragment = new DatePickerFragment();
                dialogFragment.show(getFragmentManager(), "datePicker");
            }
        });

        // When the Location TextView is clicked, show the google map v2
        locationLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        // When the People TextView is clicked, show the friends list
        peopleLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SelectInviteeActivity.class);
                startActivityForResult(intent, 2);
            }
        });

        // If the user clicks send button. . .
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // First of all show the progress dialog to make the user wait
                dialog = new ProgressDialog(CreateEventActivity.this);
                dialog.setTitle("Thank You For Your Patience :)");
                dialog.setMessage("Sending This Invitation. . .");
                dialog.show();
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);

                // Put these invitation information into Parse
                final ParseObject invitationInfo = new ParseObject("invitationInfo");
                invitationInfo.put("title", title.getText().toString());
                invitationInfo.put("time", showTime.getText().toString());
                invitationInfo.put("date", showDate.getText().toString());
                invitationInfo.put("location", showLocation.getText().toString());
                // This format looks like Jacob Kim: (111) 222-3333
                String myself = ParseUser.getCurrentUser().get("firstName") + ": " + Utility.phoneNumberFormat((String) ParseUser.getCurrentUser().get("phoneNumber"));
                friendList.add(myself);
                invitationInfo.put("invitees", friendList);
                invitationInfo.put("ownerID", ParseUser.getCurrentUser().getObjectId());
                invitationInfo.put("stateNum", friendList.size() - 1);

                // Needed to save them in db for the geofence
                invitationInfo.put("lat", lat);
                invitationInfo.put("lng", lng);

                // Save those info into the Parse
                invitationInfo.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        dialog.dismiss();

                        // Error case
                        if (e != null) {
                            Toast.makeText(getApplicationContext(), "Error saving: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            // Format looks like (123)456-7890 => 1234567890 saving only numbers
                            Utility.saveOnlyNumbers(friendList, phoneNumbers);

                            // For the testing purposes
                            Log.v(TAG, phoneNumbers.get(0));

                            // Create our Installation query
                            if (phoneNumbers.isEmpty() == false) {
                                // Send push notifications to users
                                for (int i = 0; i < phoneNumbers.size(); i++) {
                                    // If that user exists, send the push notification
                                    if(!phoneNumbers.get(i).equals(ParseUser.getCurrentUser().get("phoneNumber"))) {
                                        ParseQuery pushQuery = ParseInstallation.getQuery();
                                        pushQuery.whereEqualTo("user", phoneNumbers.get(i));

                                        // Send push notification to query
                                        ParsePush push = new ParsePush();

                                        // Using JSON to send more information along with the push notification
                                        JSONObject data = new JSONObject();
                                        try {
                                            data.put("alert", "You have an invitation!!!");
                                            data.put("title", title.getText().toString());
                                            data.put("time", showTime.getText().toString());
                                            data.put("date", showDate.getText().toString());
                                            data.put("location", showLocation.getText().toString());
                                            data.put("fromName", ParseUser.getCurrentUser().get("firstName"));
                                            data.put("fromNumber", ParseUser.getCurrentUser().get("phoneNumber"));
                                            Log.v(TAG, "MY OBJECT ID IS: " + invitationInfo.getObjectId());
                                            data.put("objectId", invitationInfo.getObjectId());

                                        } catch (JSONException e1) {
                                            e1.printStackTrace();
                                        }
                                        push.setQuery(pushQuery); // Set our Installation query
                                        push.setData(data);
                                        push.sendInBackground();
                                    }

                                }
                            }

                            // Start the new activity
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    }
                });



            }
        });

    }

    // This method brings a user-typed address and displays that address
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                showLocation.setText(data.getStringExtra("address"));
                lat = data.getDoubleExtra("lat", 0.0000);
                lng = data.getDoubleExtra("lng", 0.0000);
                Log.v(TAG, "LAT: " + lat);
                Log.v(TAG, "LNG: " + lng);

            }
        } else if(requestCode == 2) {
            if(resultCode == RESULT_OK) {

                StringBuilder temp = new StringBuilder();
                friendList = data.getStringArrayListExtra("invitees");

                for(int i = 0; i < friendList.size(); i++) {
                    temp = temp.append(friendList.get(i) + "\n");
                }
                showInvitees.setText(temp);
            }
        }
    }

    /**
     * This class extends DialogFragment which sets the user-defined time and returns it
     */
    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        // For the debugging purpose
        public final String TAG = this.getClass().getSimpleName();

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Set default time as a current time
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int min = c.get(Calendar.MINUTE);

            // Create a new TimePicker dialog and returns it
            return new TimePickerDialog(getActivity(), this, hour, min, DateFormat.is24HourFormat(getActivity()));

        }

        public void onTimeSet(TimePicker view, int hour, int min) {

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, min);
            Date date = calendar.getTime();

            showTime.setText(java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT).format(date));

        }

    }

    /**
     * This class extends DialogFragment which sets the user-defined date and returns it
     */
    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Set default date as a current date
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new DatePicker dialog and returns it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            Date date = calendar.getTime();

            showDate.setText(java.text.DateFormat.getDateInstance().format(date));
        }
    }
}


