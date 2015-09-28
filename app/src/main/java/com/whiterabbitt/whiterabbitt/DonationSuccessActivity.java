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
    Integer level;

    TextView headerView;
    TextView currCarrotView;
    TextView currAmountView;
    TextView levelView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_success);

        // Remove drop shadow from action bar on Lollipop
        getSupportActionBar().setElevation(0);

        // Initializing Views
        headerView = (TextView) findViewById(R.id.header);
        currCarrotView = (TextView) findViewById(R.id.carrots_value);
        currAmountView = (TextView) findViewById(R.id.donation_value);
        levelView = (TextView) findViewById(R.id.level_value);

        Intent intent = getIntent();

        // TODO: Probably want to change this to only save / retrieve amount donated.
        prevCarrots = (Integer) intent.getExtras().get("prevCarrots");
        prevAmount = (Integer) intent.getExtras().get("prevDonationPoints");
        
        currCarrots = (Integer) intent.getExtras().get("currentCarrots");
        currAmount = (Integer) intent.getExtras().get("currentDonationPoints");

        level = (Integer) intent.getExtras().get("level");

        headerView.setText(String.format("You donated $%d.", currAmount - prevAmount));
        currCarrotView.setText(currCarrots.toString());
        currAmountView.setText("$"+currAmount.toString());
        levelView.setText(level.toString());
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
