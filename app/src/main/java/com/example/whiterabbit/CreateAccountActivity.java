package com.example.whiterabbit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class CreateAccountActivity extends AppCompatActivity {

    // For the debugging purpose
    public final String TAG = this.getClass().getSimpleName();

    // To convert user inputs(EditText) to String
    public String textField1 = "";
    public String textField2 = "";
    public String textField3 = "";
    public String textField4 = "";
    public String textField5 = "";

    // To check whether textFields(user inputs) are empty
    public boolean isEmpty1 = true;
    public boolean isEmpty2 = true;
    public boolean isEmpty3 = true;
    public boolean isEmpty4 = true;
    public boolean isEmpty5 = true;

    // To check whether the user has checked the checkbox for the agreeing terms
    public boolean isChecked = false;

    public EditText firstName;
    public EditText lastName;
    public EditText email;
    public EditText password;
    public EditText phoneNumber;

    // To let user know that they have to meet minimum requirements to create an account
    public TextView req1;
    public TextView req2;
    public TextView req3;
    public TextView req4;
    public TextView req5;
    public TextView req6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // Button initialization for Create An Account
        Button createBtn = (Button) findViewById(R.id.createAccountBtn);

        // Initialization of text fields
        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        phoneNumber = (EditText) findViewById(R.id.phoneNumber);

        req1 = (TextView) findViewById(R.id.reqField1);
        req2 = (TextView) findViewById(R.id.reqField2);
        req3 = (TextView) findViewById(R.id.reqField3);
        req4 = (TextView) findViewById(R.id.reqField4);
        req5 = (TextView) findViewById(R.id.reqField5);
        req6 = (TextView) findViewById(R.id.reqField6);

        final CheckBox agreeTerm = (CheckBox) findViewById(R.id.agreeBox);

        // Hides the keyboard when the user touches something other then the edit text fields
        Utility.hideKeyboard(this, firstName);
        Utility.hideKeyboard(this, lastName);
        Utility.hideKeyboard(this, email);
        Utility.hideKeyboard(this, password);
        Utility.hideKeyboard(this, phoneNumber);

        // When the user clicks it, go to the main page
        createBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                textField1 = firstName.getText().toString();
                textField2 = lastName.getText().toString();
                textField3 = email.getText().toString();
                textField4 = password.getText().toString();
                textField5 = phoneNumber.getText().toString();

                // To check whether each input is empty or not
                if(textField1.length() > 0) {
                    isEmpty1 = false;
                    req1.setText("");
                } else {
                    isEmpty1 = true;
                    req1.setText("Required Field!");
                }

                if(textField2.length() > 0) {
                    isEmpty2 = false;
                    req2.setText("");
                } else {
                    isEmpty2 = true;
                    req2.setText("Required Field!");
                }
                if(textField3.length() > 0) {
                    isEmpty3 = false;
                    req3.setText("");
                } else {
                    isEmpty3 = true;
                    req3.setText("Required Field!");
                }

                if(textField4.length() > 0) {
                    isEmpty4 = false;
                    req4.setText("");
                } else {
                    isEmpty4 = true;
                    req4.setText("Required Field!");
                }

                if(textField5.length() > 0) {
                    isEmpty5 = false;
                    req5.setText("");
                } else {
                    isEmpty5 = true;
                    req5.setText("Required Field!");
                }

                // If the return value is false, then it indicates that the user hasn't checked
                // the checkbox
                if(agreeTerm.isChecked() == false) {
                    isChecked = false;
                    req6.setText("Required Field!");
                } else {
                    isChecked = true;
                    req6.setText("");
                }

                Log.v(TAG, "isEmpty1:" + isEmpty1);
                Log.v(TAG, "isEmpty2:" + isEmpty2);
                Log.v(TAG, "isEmpty3:" + isEmpty3);
                Log.v(TAG, "isEmpty4:" + isEmpty4);
                Log.v(TAG, "isEmpty5:" + isEmpty5);
                Log.v(TAG, "isChecked:" + isChecked);

                // Makes sure that the user has filled each text field & checkbox & radio button
                if(isEmpty1 == false && isEmpty2 == false && isEmpty3 == false && isEmpty4 == false
                        && isEmpty5 == false && isChecked == true) {

                    final ProgressDialog dialog = new ProgressDialog(CreateAccountActivity.this);
                    dialog.setTitle("Thank You For Your Patience");
                    dialog.setMessage("Creating An Account. . .");
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();

                    // Set up a new user
                    ParseUser user = new ParseUser();
                    user.setUsername(textField3);
                    user.setPassword(textField4);
                    user.put("firstName", textField1);
                    user.put("lastName", textField2);
                    user.put("phoneNumber", textField5);

                    // Sign up in the background
                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            dialog.dismiss();

                            if(e != null) {
                                // Display a error message when creating account fails
                                //Toast.makeText(CreateAccountActivity.this, "Error from saving a user", Toast.LENGTH_SHORT).show();
                                Log.v(TAG, "Error:" + e.getMessage() + ", " + e.getCode());

                                // 202 means a username is already taken
                                if(e.getCode() == 202) {
                                    Toast.makeText(CreateAccountActivity.this, "Username already taken!", Toast.LENGTH_LONG).show();
                                    email.setText("");
                                }

                            }

                            else {

                                ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                                installation.put("user", textField5);
                                installation.put("userName", ParseUser.getCurrentUser());
                                // TODO: REMOVE ALL SPECIAL CHARS FROM THE PHONE NUMBER
                                String testPhoneNumber = (String) ParseUser.getCurrentUser().get("phoneNumber");
                                Log.v(TAG, "Phone Number At CREATE_ACCOUNT_CORRECT: " + textField5);
                                Log.v(TAG, "Phone Number At CREATE_ACCOUNT_TEST: " + testPhoneNumber);
                                installation.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        Log.v(TAG, "REGISTERED SUCCESSFULLY !!!!!");
                                    }
                                });

                                // User created the account successfully, so go to the next activity
                                Toast.makeText(CreateAccountActivity.this, "Registered Successfully :)",
                                        Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                }
            }
        });
    }

    // A method that checks the length of the passed string and set whether it's empty or not.
    // And then it sets the corresponding text message
    public void checkLength(String str, TextView textView, boolean isEmpty) {
        if(str.length() != 0) {
            isEmpty = false;
            textView.setText("");
        } else {
            isEmpty = true;
            textView.setText("Required Field!");
        }
    }

}