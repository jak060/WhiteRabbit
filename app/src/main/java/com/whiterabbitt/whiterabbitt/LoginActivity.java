package com.whiterabbitt.whiterabbitt;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class LoginActivity extends AppCompatActivity {

    private static final String ERROR_EMPTY_FIELD = "Field cannot be left blank.";

    // For the debugging purpose
    public final String TAG = this.getClass().getSimpleName();

    // To convert user inputs(EditText) to String
    public String textField1 = "";
    public String textField2 = "";

    // To check whether textFields(user inputs) are empty
    public boolean isEmpty1 = true;
    public boolean isEmpty2 = true;

    // To let user know that they have to meet minimum requirements to create an account
    public TextView req1;
    public TextView req2;

    EditText email;
    EditText password;

    // To check the validity of user
    public boolean userMatch = false;

    public ProgressDialog dialog;

    boolean retVal = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Button initialization for Logging in
        Button loginBtn = (Button) findViewById(R.id.loginBtn);

        // Initialization of email and password text fields
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

        // Button initialization for Creating an Account
        Button createAccount = (Button) findViewById(R.id.createAccountBtn);

        // Hides the keyboard when the user touches something other then the edit text fields
        Utility.hideKeyboard(this, email);
        Utility.hideKeyboard(this, password);


        // When the user clicks it, go to the main page
        loginBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Convert username field to String
                textField1 = email.getText().toString().trim();

                // Convert password field to String
                textField2 = password.getText().toString();

                // To check whether the input is empty or not
                // To check whether the input is empty or not
                if (email.getText().length() == 0) {
                    email.setError(ERROR_EMPTY_FIELD);
                    isEmpty1 = true;
                } else {
                    isEmpty1 = false;
                }

                if (password.getText().length() == 0) {
                    password.setError(ERROR_EMPTY_FIELD);
                    isEmpty2 = true;
                } else {
                    isEmpty2 = false;
                }

                // Makes sure that each field is not empty
                if (isEmpty1 == false && isEmpty2 == false) {

                    // Create a Progress dialog to buy some time to talk to the server
                    dialog = new ProgressDialog(LoginActivity.this);
                    dialog.setTitle("Thank You For Your Patience");
                    dialog.setMessage("Logging In. . .");
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();

                    Log.v(TAG, "Username: " + textField1);
                    Log.v(TAG, "Password " + textField2);

                    ParseUser.logInInBackground(textField1, textField2, new LogInCallback() {
                        @Override
                        public void done(ParseUser parseUser, ParseException e) {
                            dialog.dismiss();

                            // If log in fails, display a alert dialog
                            if (e != null) {
                                new AlertDialog.Builder(LoginActivity.this)
                                        .setTitle("Error!")
                                        .setMessage("Username or Passoword doesn't match! Please try again!")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        }).setIcon(android.R.drawable.ic_dialog_alert).show();
                            }

                            // If log in succeeds, then go to the next activity
                            else {
                                Toast.makeText(LoginActivity.this, "Logged In Successfully :)",
                                        Toast.LENGTH_LONG).show();

                                ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                                installation.put("user", ParseUser.getCurrentUser().get("phoneNumber"));
                                installation.put("userName", ParseUser.getCurrentUser());
                                installation.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(intent);
                                        } else {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }

                        }
                    });

                }
            }
        });

        // When the user clicks it, go to the Create Account page
        createAccount.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateAccountActivity.class);
                startActivity(intent);
            }
        });
    }
}