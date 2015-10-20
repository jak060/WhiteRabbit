package com.whiterabbitt.whiterabbitt;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class CreateEventActivity extends AppCompatActivity {

    private static final String ERROR_EMPTY_FIELD = "Field cannot be left blank.";

    // For the debugging purpose
    public final String TAG = this.getClass().getSimpleName();

    ArrayList<String> friendList = new ArrayList<String>();
    ArrayList<String> phoneNumbers = new ArrayList<String>();

    EditText title;

    TextView timeLabel;
    TextView dateLabel;
    TextView locationLabel;
    TextView peopleLabel;
    TextView errorMsg;

    static TextView showTime;
    static TextView showDate;

    TextView showLocation;
    TextView showInvitees;

    public ProgressDialog dialog;

    RadioGroup carrots;
    String numberOfCarrots = "0 carrot";

    double lat;
    double lng;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_event_send) {

            // Make sure that each field is not empty
            if(title.getText().length() <= 0) {
                title.setError(ERROR_EMPTY_FIELD);
            }
            if(showLocation.getText().length() <= 0) {
                showLocation.setError(ERROR_EMPTY_FIELD);
            }
            if(showInvitees.getText().length() <= 0) {
                showInvitees.setError(ERROR_EMPTY_FIELD);
            }
            if(numberOfCarrots.equals("0 carrot")) {
                errorMsg.setText("Please Select One");
            }
            if(title.getText().length() > 0 && showLocation.getText().length() > 0 && showInvitees.getText().length() > 0) {
                sendInvitation();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        // Remove drop shadow from action bar on Lollipop
        getSupportActionBar().setElevation(0);

        // Clearing these lists before using them
        friendList.clear();
        phoneNumbers.clear();

        // Initialization of variables
        title = (EditText) findViewById(R.id.title);

        timeLabel = (TextView) findViewById(R.id.time_label);
        dateLabel = (TextView) findViewById(R.id.date_label);
        locationLabel = (TextView) findViewById(R.id.location_label);
        peopleLabel = (TextView) findViewById(R.id.people_label);
        errorMsg = (TextView) findViewById(R.id.errorMsg);
        showTime = (TextView) findViewById(R.id.showTime);
        showTime.setText(java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT).format(new Date()));

        showDate = (TextView) findViewById(R.id.showDate);
        showDate.setText(java.text.DateFormat.getDateInstance().format(new Date()));

        showLocation = (TextView) findViewById(R.id.chosenLocationText);
        showInvitees = (TextView) findViewById(R.id.inviteesTextView);

        carrots = (RadioGroup) findViewById(R.id.radio_group_wager);

        disableErrorWhenUserTypes(title);

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

        carrots.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) findViewById(checkedId);
                numberOfCarrots = rb.getText().toString();
                errorMsg.setText("");
            }
        });


    }

    // This method brings a user-typed address and displays that address
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                showLocation.setError(null);
                showLocation.setText(data.getStringExtra("address"));
                lat = data.getDoubleExtra("lat", 0.0000);
                lng = data.getDoubleExtra("lng", 0.0000);
                Log.v(TAG, "LAT: " + lat);
                Log.v(TAG, "LNG: " + lng);

            }
        } else if(requestCode == 2) {
            if(resultCode == RESULT_OK) {

                showInvitees.setError(null);
                StringBuilder temp = new StringBuilder();
                friendList = data.getStringArrayListExtra("invitees");

                for(int i = 0; i < friendList.size(); i++) {
                    temp = temp.append(friendList.get(i) + "\n");
                }
                showInvitees.setText(temp);
            }
        }
    }

    private void sendInvitation() {
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

        // These are needed to perform a sorting later when we fetch events
        String time = Utility.parseTimeTo24HourFormat(showTime.getText().toString());
        Log.v(TAG, "Time that will sort later: " + time);
        invitationInfo.put("parsedTime", time);
        String date = Utility.parseDate(showDate.getText().toString());
        invitationInfo.put("date", date);

        invitationInfo.put("time", showTime.getText().toString());
        invitationInfo.put("location", showLocation.getText().toString());
        // This format looks like Jacob Kim: (111) 222-3333
        String myself = ParseUser.getCurrentUser().get("firstName") + ": " + Utility.phoneNumberFormat((String) ParseUser.getCurrentUser().get("phoneNumber"));
        friendList.add(myself);
        invitationInfo.put("invitees", friendList);
        final String myObjectId = ParseUser.getCurrentUser().getObjectId();
        invitationInfo.put("ownerID", myObjectId); // To hold IDs of all of the users who are involved in this event
        invitationInfo.put("accepted", 0);
        invitationInfo.put("declined", 0);
        invitationInfo.put("hostID", myObjectId);

        // Needed to save them in db for the geofence
        invitationInfo.put("lat", lat);
        invitationInfo.put("lng", lng);

        // Save the number of carrots
        invitationInfo.put("carrots", numberOfCarrots);

        invitationInfo.put("winners", new ArrayList<String>());
        invitationInfo.put("losers", new ArrayList<String>());

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
                    ParseUser.getCurrentUser().addUnique("eventList", invitationInfo.getObjectId());
                    ParseUser.getCurrentUser().saveInBackground();

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
                                    data.put("alert", "Let's get together :)");
                                    data.put("title", title.getText().toString());
                                    data.put("time", showTime.getText().toString());
                                    data.put("date", showDate.getText().toString());
                                    data.put("location", showLocation.getText().toString());
                                    data.put("fromName", ParseUser.getCurrentUser().get("firstName"));
                                    data.put("fromNumber", ParseUser.getCurrentUser().get("phoneNumber"));
                                    Log.v(TAG, "MY OBJECT ID IS: " + invitationInfo.getObjectId());
                                    data.put("objectId", invitationInfo.getObjectId());
                                    data.put("carrots", numberOfCarrots);

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

    // This method is to diable the error message when the user tries to re-type each field
    public void disableErrorWhenUserTypes(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editText.getText().length() > 0) {
                    editText.setError(null);
                }
            }
        });
    }
}


