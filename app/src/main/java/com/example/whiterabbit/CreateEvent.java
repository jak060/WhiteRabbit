package com.example.whiterabbit;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class CreateEvent extends AppCompatActivity {

    // For the debugging purpose
    public final String TAG = this.getClass().getSimpleName();

    static TextView showTime;
    static TextView showDate;
    TextView showLocation;
    EditText title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);

        // Initialization of variables
        Button timeBtn = (Button) findViewById(R.id.chooseTimeBtn);
        Button dateBtn = (Button) findViewById(R.id.chooseDateBtn);
        Button mapBtn = (Button) findViewById(R.id.showMapBtn);

        showTime = (TextView) findViewById(R.id.showTime);
        showDate = (TextView) findViewById(R.id.showDate);
        title = (EditText) findViewById(R.id.title);
        showLocation = (TextView) findViewById(R.id.chosenLocationText);

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

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityMap.class);
                startActivityForResult(intent, 1);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                showLocation.setText(data.getStringExtra("address"));
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


