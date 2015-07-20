package com.example.whiterabbit;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CreateEventActivity extends AppCompatActivity {

    // For the debugging purpose
    public final String TAG = this.getClass().getSimpleName();

    ArrayList<String> friendList = new ArrayList<String>();
    ArrayList<String> phoneNumbers = new ArrayList<String>();

    static TextView showTime;
    static TextView showDate;
    TextView showLocation;
    EditText title;
    TextView showInvitees;
    public ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        friendList.clear();
        phoneNumbers.clear();

        // Initialization of variables
        Button timeBtn = (Button) findViewById(R.id.chooseTimeBtn);
        Button dateBtn = (Button) findViewById(R.id.chooseDateBtn);
        Button mapBtn = (Button) findViewById(R.id.showMapBtn);
        Button inviteBtn = (Button) findViewById(R.id.inviteBtn);
        Button sendBtn = (Button) findViewById(R.id.sendBtn);
        showTime = (TextView) findViewById(R.id.showTime);
        showDate = (TextView) findViewById(R.id.showDate);
        title = (EditText) findViewById(R.id.title);
        showLocation = (TextView) findViewById(R.id.chosenLocationText);

        showInvitees = (TextView) findViewById(R.id.inviteesTextView);

        // When user clicks "Choose Time" button, show the time picker
        timeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogFragment = new TimePickerFragment();
                dialogFragment.show(getFragmentManager(), "timePicker");
            }
        });

        // When user clicks "Choose Date" button, show the date picker
        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogFragment = new DatePickerFragment();
                dialogFragment.show(getFragmentManager(), "datePicker");
            }
        });

        // When user clicks "Choose Location" button, show the map
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        // When user clicks "Choose Location" button, show the map
        inviteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SelectInviteeActivity.class);
                startActivityForResult(intent, 2);
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog = new ProgressDialog(CreateEventActivity.this);
                dialog.setTitle("Thank You For Your Patience :)");
                dialog.setMessage("Sending This Invitation. . .");
                dialog.show();
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);


                ParseObject invitationInfo = new ParseObject("invitationInfo");
                invitationInfo.put("title", title.getText().toString());
                invitationInfo.put("time", showTime.getText().toString());
                invitationInfo.put("data", showDate.getText().toString());
                invitationInfo.put("Location", showLocation.getText().toString());
                invitationInfo.put("invitees", friendList);

                invitationInfo.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        dialog.dismiss();

                        if (e != null) {
                            Toast.makeText(getApplicationContext(), "Error saving: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();

                        } else {

                            // Start the new activity
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            //intent.putExtra("image", filePath);
                            startActivity(intent);
                        }
                    }
                });

                Utility.saveOnlyNumbers(friendList, phoneNumbers);
                // Create our Installation query
                Log.v(TAG, phoneNumbers.get(0));
                if(phoneNumbers.isEmpty() == false) {
                    // Send push notifications to users
                    for(int i = 0; i < phoneNumbers.size(); i ++) {
                        ParseQuery pushQuery = ParseInstallation.getQuery();
                        pushQuery.whereEqualTo("user", phoneNumbers.get(i));

                        // Send push notification to query
                        ParsePush push = new ParsePush();
                        push.setQuery(pushQuery); // Set our Installation query
                        push.setMessage("Hello World!");
                        push.sendInBackground();
                    }
                }

            }
        });

    }

    // This method brings a user-typed address and displays that address
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                showLocation.setText(data.getStringExtra("address"));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

            String state = "AM";

            // To make time more easy to understand
            if(hour == 0) {
                hour = 12;
            }

            if(hour > 12) {
                hour = hour - 12;
                state = "PM";

            }
            showTime.setText(hour + ":" + min + " " + state);

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
            showDate.setText(month + "/" + day + "/" + year);
        }
    }
}


