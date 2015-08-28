package com.example.whiterabbit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class CreateAccountActivity extends AppCompatActivity {

    private static final String ERROR_EMPTY_FIELD = "Field cannot be left blank.";
    private static final String ERROR_AGREE_BOX = "You must agree to the terms to create an account.";

    // For the debugging purpose
    public final String TAG = this.getClass().getSimpleName();

    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText password;
    private EditText phoneNumber;

    private TextView agreeBoxReqMsg;

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
        Utility.hideKeyboard(this, phoneNumber);

        // When the user clicks it, go to the main page
        createBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(firstName.getText().length() == 0) {
                    firstName.setError(ERROR_EMPTY_FIELD);
                }

                if(lastName.getText().length() == 0) {
                    lastName.setError(ERROR_EMPTY_FIELD);
                }

                if(email.getText().length() == 0) {
                    email.setError(ERROR_EMPTY_FIELD);
                }

                if(password.getText().length() == 0) {
                    password.setError(ERROR_EMPTY_FIELD);
                }

                if(phoneNumber.getText().length() == 0) {
                    phoneNumber.setError(ERROR_EMPTY_FIELD);
                }

                if(!agreeTerm.isChecked()) {
                    agreeBoxReqMsg.setText(ERROR_AGREE_BOX);
                } else {
                    agreeBoxReqMsg.setText("");
                }

                // Makes sure that the user has completed everything
                if(firstName.getText().length() > 0 &&
                        lastName.getText().length() > 0 &&
                        email.getText().length() > 0 &&
                        password.getText().length() > 0 &&
                        phoneNumber.getText().length() > 0 &&
                        agreeTerm.isChecked()) {

                    final ProgressDialog dialog = new ProgressDialog(CreateAccountActivity.this);
                    dialog.setTitle("Thank You For Your Patience");
                    dialog.setMessage("Creating An Account. . .");
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();

                    // Set up a new user
                    ParseUser user = new ParseUser();
                    user.setUsername(email.getText().toString());
                    user.setPassword(password.getText().toString());
                    user.put("firstName", firstName.getText().toString());
                    user.put("lastName", lastName.getText().toString());
                    user.put("phoneNumber", phoneNumber.getText().toString());
                    user.put("carrots", 50);
                    user.put("rankPoints", 0);
                    user.put("attempts", 0);

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
                                installation.put("user", email.getText().toString());
                                installation.put("userName", ParseUser.getCurrentUser());
                                // TODO: REMOVE ALL SPECIAL CHARS FROM THE PHONE NUMBER
                                String testPhoneNumber = (String) ParseUser.getCurrentUser().get("phoneNumber");
                                Log.v(TAG, "Phone Number At CREATE_ACCOUNT_CORRECT: " + phoneNumber.getText().toString());
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

}