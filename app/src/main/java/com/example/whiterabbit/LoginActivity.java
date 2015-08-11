package com.example.whiterabbit;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

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
        Button loginBtn = (Button) findViewById(R.id.loginBtn2);

        // Initialization of email and password text fields
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

        req1 = (TextView) findViewById(R.id.reqField1);
        req2 = (TextView) findViewById(R.id.reqField2);

        // Button initialization for Creating an Account
        ImageButton createAccount = (ImageButton) findViewById(R.id.createAccountBtn);

        // Button initialization for Forgot Password?
        ImageButton forgotPass = (ImageButton) findViewById(R.id.passwordBtn);

        // Hides the keyboard when the user touches something other then the edit text fields
        Utility.hideKeyboard(this, email);
        Utility.hideKeyboard(this, password);


        // When the user clicks it, go to the main page
        loginBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Convert username field to String
                textField1 = email.getText().toString();

                // Convert password field to String
                textField2 = password.getText().toString();

                // To check whether the input is empty or not
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

                // Makes sure that each field is not empty
                if(isEmpty1 == false && isEmpty2 == false) {

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
                            if(e != null) {
                                new AlertDialog.Builder(LoginActivity.this)
                                        .setTitle("Error!")
                                        .setMessage("Username or Passoword doesn't match! Please try again!")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener(){
                                            public void onClick(DialogInterface dialog, int which){

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
                                installation.saveInBackground();

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
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