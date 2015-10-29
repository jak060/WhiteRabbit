package com.whiterabbitt.whiterabbitt;

import android.app.Activity;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.parse.ParseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * This class deals with having utility methods that are necessary for our app
 */
public class Utility {

    // For the debugging purpose
    public final String TAG = this.getClass().getSimpleName();

    // This method hides soft keyboard when editText is out of focus
    public static void hideKeyboard(Activity activity, View view) {
        final InputMethodManager inputMethodManager =(InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.restartInput(view);
        view.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    inputMethodManager.showSoftInput(v, InputMethodManager.SHOW_FORCED);
                } else {
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });
    }

    // This method creates the contact list from users phone
    public static void createContactList(Activity activity, ArrayList<String> myList)
    {
        Cursor cursor = activity.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
                null, null);
        // Save all of contact info into the arrays
        while (cursor.moveToNext()) {
            String name = cursor
                    .getString(cursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = cursor
                    .getString(cursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            myList.add(name + ": " + phoneNumber);
        }
    }

    // This method takes a contact list which has a name and phone number and saves only
    // phone number to the ArrayList
    public static void saveOnlyNumbers(ArrayList<String> src, ArrayList<String> dest) {

        int i = 0;

        // Make sure that the list size is greater than 0
        if(src.size() > 0) {
            while(i < src.size()) {
                String temp = src.get(i);
                temp = temp.substring(temp.lastIndexOf(":") + 2);
                temp = temp.replaceAll("[^0-9]", "");

                Log.v("Utility.java", "In phoneNums: " + temp);

                // In case user has put number one in front of the phone number
                if(temp.length() == 11 && temp.substring(0, 1).equals("1")) {
                    temp = temp.substring(1);
                }

                // To avoid duplicates
                if(!dest.contains(temp)) {
                    dest.add(temp);
                    i ++;
                } else {
                    src.remove(i);
                }
            }
        }

    }

    // This method takes a contact list which has a name and username and saves only
    // username to the ArrayList
    public static void saveOnlyUsernames(ArrayList<String> src, ArrayList<String> dest) {

        int i = 0;

        // Make sure that the list size is greater than 0
        if(src.size() > 0) {
            while(i < src.size()) {
                String temp = src.get(i);
                temp = temp.substring(temp.indexOf("(") + 1, temp.lastIndexOf(")"));

                Log.v("Utility.java", "In usernames: " + temp);

                // To avoid duplicates
                if(!dest.contains(temp)) {
                    dest.add(temp);
                    i ++;
                } else {
                    src.remove(i);
                }
            }
        }

    }

    // This method changes 0001112222 to (000) 111-2222
    // Only to have a better readability
    public static String phoneNumberFormat(String phoneNumber) {

        String finalFormat = phoneNumber;

        if(phoneNumber.length() == 10) {
            String temp = phoneNumber.substring(0, 3);
            String temp2 = phoneNumber.substring(3, 6);
            String temp3 = phoneNumber.substring(6);
            finalFormat = "(" + temp + ") " + temp2 + "-" + temp3;
        }

        // If user has put 1 in front of the phone number, then exclude 1.
        else if(phoneNumber.length() == 11) {
            String temp = phoneNumber.substring(1, 4);
            String temp2 = phoneNumber.substring(4, 7);
            String temp3 = phoneNumber.substring(7);
            finalFormat = "(" + temp + ") " + temp2 + "-" + temp3;
        }

        return finalFormat;
    }

    // This method parses MMM dd, yyyy to yyyy-MM-dd
    // For example, Aug 25, 2015 to 2015-08-25
    public static String parseDate(String time) {
        String inputPattern = "MMM dd, yyyy";
        String outputPattern = "yyyy-MM-dd";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String returnDate = null;

        try {
            date = inputFormat.parse(time);
            returnDate = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.v("Utility.java", "Input date:" + date.toString());
        Log.v("Utility.java", "Output date: " + returnDate);

        return returnDate;
    }

    // This method parses MMM dd, yyyy to yyyy-MM-dd
    // For example, 2015-08-25 to Aug 25, 2015
    public static String parseDate2(String time) {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "MMM dd, yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String returnDate = null;

        try {
            date = inputFormat.parse(time);
            returnDate = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.v("Utility.java", "Input date:" + date.toString());
        Log.v("Utility.java", "Output date: " + returnDate);

        return returnDate;
    }

    // This method parses 12-hour format time to 24-hour format
    // Good for sorting by time
    public static String parseTimeTo24HourFormat(String time) {

        // Just get the hour digit
        String hour = time.substring(0, time.indexOf(":"));
        String state = "AM";

        // If  there is no AM, that means it's PM
        if(time.indexOf("AM") == -1) {
            state = "PM";
        }

        // In the case of PM. . .
        if(state.equals("PM")) {

            // If hour digit == 12, then set it to 0
            // so that when you add 12 to it, it won't be 24, which is wrong
            if(hour.equals("12")) {
                hour = 0 + "";
            }

            // Parse the time to int
            Integer strTimeToInt = Integer.parseInt(hour);

            // Add 12 to the hour digit
            // For example 1:00 PM -> 13:00 PM
            //             5:00 PM -> 17:00 PM
            strTimeToInt = strTimeToInt + 12;

            // Parse the int back to String
            hour = strTimeToInt.toString();
            Log.v("Utility.java", "Current parsed time is: " + hour + time.substring(time.indexOf(":")));
        }

        // In the case of AM. . .
        else {

            // Only corner case is when the time is 12 AM. . . so set it to 0
            if(hour.equals("12")) {
                hour = 0 + "";
            }
            Log.v("Utility.java", "Current parsed time is: " + hour + time.substring(time.indexOf(":")));
        }

        if(hour.length() == 1) {
            hour = 0 + hour;
        }

        // Return the string (It should look like something 12:59 PM)
        return hour + time.substring(time.indexOf(":"));
    }
}
