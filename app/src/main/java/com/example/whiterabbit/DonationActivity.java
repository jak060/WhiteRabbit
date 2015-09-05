package com.example.whiterabbit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

public class DonationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation);

        final TextView amountView = (TextView) findViewById(R.id.amount);
        final EditText numCarrotsView = (EditText) findViewById(R.id.num_carrots);
        final EditText dollarsPerCarrotView = (EditText) findViewById(R.id.dollars_per_carrot);

        numCarrotsView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int numCarrots = convertStringToInt(numCarrotsView.getText().toString());
                int dollarsPerCarrot = convertStringToInt(dollarsPerCarrotView.getText().toString());
                int amount = calculateAmount(numCarrots, dollarsPerCarrot);

                amountView.setText(String.format("$%d", amount));
            }
        });

        dollarsPerCarrotView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int numCarrots = convertStringToInt(numCarrotsView.getText().toString());
                int dollarsPerCarrot = convertStringToInt(dollarsPerCarrotView.getText().toString());
                int amount = calculateAmount(numCarrots, dollarsPerCarrot);

                amountView.setText(String.format("$%d", amount));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_donation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_donate) {
            Intent intent = new Intent(getApplicationContext(), DonationSuccessActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private int convertStringToInt(String string) {
        int value;

        if(string.equals("")) {
            value = 0;
        }
        else {
            value = Integer.parseInt(string);
        }

        return value;
    }

    private int calculateAmount(int numCarrots, int dollarsPerCarrot) {
        return numCarrots * dollarsPerCarrot;
    }
}
