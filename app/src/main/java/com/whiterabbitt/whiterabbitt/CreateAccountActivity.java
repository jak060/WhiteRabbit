package com.whiterabbitt.whiterabbitt;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.ArrayList;

public class CreateAccountActivity extends AppCompatActivity {

    private static final String ERROR_EMPTY_FIELD = "Field cannot be left blank.";
    private static final String ERROR_AGREE_BOX = "You must agree to the terms to create an account.";
    private static final String ERROR_PASSWORD_MISMATCH = "Passwords do not match.";

    // For the debugging purpose
    public final String TAG = this.getClass().getSimpleName();

    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText password;
    private EditText passwordConfirm;
    private EditText phoneNumber;

    private TextView agreeBoxReqMsg;

    private String phoneNum = "";

    private ArrayList<String> eventList = new ArrayList<String>();

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
        passwordConfirm = (EditText) findViewById(R.id.passwordConfirm);
        phoneNumber = (EditText) findViewById(R.id.phoneNumber);

        agreeBoxReqMsg = (TextView) findViewById(R.id.agreeBoxReqMsg);

        final CheckBox agreeTerm = (CheckBox) findViewById(R.id.agreeBox);
        agreeTerm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (agreeTerm.isChecked()) {
                    agreeBoxReqMsg.setText("");
                }
            }
        });

        // Hides the keyboard when the user touches something other then the edit text fields
        Utility.hideKeyboard(this, firstName);
        Utility.hideKeyboard(this, lastName);
        Utility.hideKeyboard(this, email);
        Utility.hideKeyboard(this, password);
        Utility.hideKeyboard(this, passwordConfirm);
        Utility.hideKeyboard(this, phoneNumber);

        disableErrorWhenUserTypes(firstName);
        disableErrorWhenUserTypes(lastName);
        disableErrorWhenUserTypes(email);
        disableErrorWhenUserTypes(phoneNumber);

        phoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        // When the user clicks it, go to the main page
        createBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (firstName.getText().length() == 0) {
                    firstName.setError(ERROR_EMPTY_FIELD);
                }

                if (lastName.getText().length() == 0) {
                    lastName.setError(ERROR_EMPTY_FIELD);
                }

                if (email.getText().length() == 0) {
                    email.setError(ERROR_EMPTY_FIELD);
                }

                if (password.getText().length() == 0) {
                    password.setError(ERROR_EMPTY_FIELD);
                }

                if (passwordConfirm.getText().length() == 0) {
                    passwordConfirm.setError(ERROR_EMPTY_FIELD);
                }

                if (phoneNumber.getText().length() == 0) {
                    phoneNumber.setError(ERROR_EMPTY_FIELD);
                }

                if (!agreeTerm.isChecked()) {
                    agreeBoxReqMsg.setText(ERROR_AGREE_BOX);
                } else {
                    agreeBoxReqMsg.setText("");
                }

                // Makes sure that the user has completed everything
                if (firstName.getText().length() > 0 &&
                        lastName.getText().length() > 0 &&
                        email.getText().length() > 0 &&
                        password.getText().length() > 0 &&
                        passwordConfirm.getText().length() > 0 &&
                        phoneNumber.getText().length() > 0 &&
                        agreeTerm.isChecked()) {

                    // Make sure that the user has typed the password correctly
                    if (password.getText().toString().equals(passwordConfirm.getText().toString())) {

                        final ProgressDialog dialog = new ProgressDialog(CreateAccountActivity.this);
                        dialog.setTitle("Thank You For Your Patience");
                        dialog.setMessage("Creating An Account. . .");
                        dialog.setCancelable(false);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();

                        // Set up a new user
                        ParseUser user = new ParseUser();
                        user.setUsername(email.getText().toString().trim());
                        user.setPassword(password.getText().toString());
                        user.put("firstName", firstName.getText().toString().trim());
                        user.put("lastName", lastName.getText().toString().trim());

                        phoneNum = phoneNumber.getText().toString().replaceAll("[^0-9]", "");

                        if(phoneNum.length() == 11 && phoneNum.substring(0, 1).equals("1")) {
                            phoneNum = phoneNum.substring(1);
                        }

                        user.put("phoneNumber", phoneNum.trim());
                        user.put("carrots", 30);
                        user.put("rankPoints", 0);
                        user.put("attempts", 0);
                        user.put("donationPoints", 0);
                        user.put("eventList", eventList);

                        // Sign up in the background
                        user.signUpInBackground(new SignUpCallback() {
                            @Override
                            public void done(ParseException e) {
                                dialog.dismiss();

                                if (e != null) {
                                    // Display a error message when creating account fails
                                    //Toast.makeText(CreateAccountActivity.this, "Error from saving a user", Toast.LENGTH_SHORT).show();
                                    Log.v(TAG, "Error:" + e.getMessage() + ", " + e.getCode());

                                    // 202 means a username is already taken
                                    if (e.getCode() == 202) {
                                        Toast.makeText(CreateAccountActivity.this, "Username already taken!", Toast.LENGTH_LONG).show();
                                        email.setText("");
                                    }
                                } else {
                                    ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                                    installation.put("user", phoneNum);
                                    installation.put("userName", ParseUser.getCurrentUser());
                                    // TODO: REMOVE ALL SPECIAL CHARS FROM THE PHONE NUMBER
                                    String testPhoneNumber = (String) ParseUser.getCurrentUser().get("phoneNumber");
                                    //Log.v(TAG, "Phone Number At CREATE_ACCOUNT_CORRECT: " + phoneNumber.getText().toString());
                                    //Log.v(TAG, "Phone Number At CREATE_ACCOUNT_TEST: " + testPhoneNumber);
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

                    // If the passwords don't match, then display the corresponding error message
                    else {
                        password.setText("");
                        passwordConfirm.setText("");
                        password.setError(ERROR_PASSWORD_MISMATCH);
                        passwordConfirm.setError(ERROR_PASSWORD_MISMATCH);
                    }

                }
            }
        });
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