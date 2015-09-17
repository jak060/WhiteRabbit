package com.whiterabbitt.whiterabbitt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DonationSuccessActivity extends AppCompatActivity {
    Integer prevCarrots;
    Integer prevAmount;
    Integer currCarrots;
    Integer currAmount;

    TextView prevCarrotView;
    TextView prevAmountView;
    TextView currCarrotView;
    TextView currAmountView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_success);

        // Initializing Views
        prevCarrotView = (TextView) findViewById(R.id.carrots_value);
        prevAmountView = (TextView) findViewById(R.id.donation_value);
        currCarrotView = (TextView) findViewById(R.id.carrots_value2);
        currAmountView = (TextView) findViewById(R.id.donation_points2);

        Intent intent = getIntent();

        prevCarrots = (Integer) intent.getExtras().get("prevCarrots");
        prevAmount = (Integer) intent.getExtras().get("prevDonationPoints");
        currCarrots = (Integer) intent.getExtras().get("currentCarrots");
        currAmount = (Integer) intent.getExtras().get("currentDonationPoints");

        prevCarrotView.setText(prevCarrots.toString());
        prevAmountView.setText("$"+prevAmount.toString());
        currCarrotView.setText(currCarrots.toString());
        currAmountView.setText("$"+currAmount.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_donation_success, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_close) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        return;
    }
}
