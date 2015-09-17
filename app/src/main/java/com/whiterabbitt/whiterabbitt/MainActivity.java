package com.whiterabbitt.whiterabbitt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    FragmentPageAdapter fragmentPageAdapter;
    ViewPager viewPager;

    // For the debugging purpose
    public final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_container);

        viewPager = (ViewPager) findViewById(R.id.pager);
        fragmentPageAdapter= new FragmentPageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(fragmentPageAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_invitations) {
            Intent intent = new Intent(getApplicationContext(), InvitationsActivity.class);
            startActivity(intent);
            return true;
        }
        else if(id == R.id.action_create_event) {
            Intent intent = new Intent(getApplicationContext(), CreateEventActivity.class);
            startActivity(intent);
            return true;
        }
        else if(id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        viewPager.setAdapter(fragmentPageAdapter);
    }

    @Override
    public void onBackPressed() {
        return;
    }
}
