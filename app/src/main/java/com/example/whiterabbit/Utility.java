package com.example.whiterabbit;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class Utility {

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
}
