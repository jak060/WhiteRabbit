package com.example.whiterabbit;

import android.app.Activity;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;

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
        for(int i = 0; i < src.size(); i++) {
            String temp = src.get(i);
            temp = temp.substring(temp.indexOf(":") + 2);
            temp = temp.replaceAll("[^0-9]", "");

          //  Log.v(TAG, "In phoneNums: " + temp);

            dest.add(temp);
        }
    }

    // This method changes 0001112222 to (000)111-2222
    // Only to have a better readability
    public static String phoneNumberFormat(String phoneNumber) {
        String temp = phoneNumber.substring(0, 3);
        String temp2 = phoneNumber.substring(3, 6);
        String temp3 = phoneNumber.substring(6);
        String finalFormat = "(" + temp + ")" + temp2 + "-" + temp3;
        return finalFormat;
    }
}
