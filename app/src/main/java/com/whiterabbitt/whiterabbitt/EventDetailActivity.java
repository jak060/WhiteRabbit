package com.whiterabbitt.whiterabbitt;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * This class displays details of the selected event
 */
public class EventDetailActivity extends FragmentActivity implements OnMapReadyCallback {

    // For the debugging purpose
    public final String TAG = this.getClass().getSimpleName();

    TextView title;
    TextView time_label;
    TextView location_label;
    TextView reward_label;
    double lat;
    double lng;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> participants = new ArrayList<String>();
    // A listView
    ListView friends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        overridePendingTransition(R.anim.slide_up, R.anim.no_change);

        friends = (ListView) findViewById(R.id.participant_list);

        participants.clear();

        title = (TextView) findViewById(R.id.title);
        Log.v(TAG, "TITLE COLORCODE:" + title.getTextColors().getDefaultColor());
        time_label = (TextView) findViewById(R.id.time_label);
        Log.v(TAG, "TIME COLORCODE:" + time_label.getTextColors().getDefaultColor());
        location_label = (TextView) findViewById(R.id.location_label);
        reward_label = (TextView) findViewById(R.id.reward_label);



        Intent intent = getIntent();

        if(intent != null) {
            title.setText((String) intent.getExtras().get("title"));
            String time = (String) intent.getExtras().get("time");
            String date = (String) intent.getExtras().get("date");
            time_label.setText(time + " / " + (Utility.parseDate2(date).toUpperCase()));
            location_label.setText((String) intent.getExtras().get("location"));
            reward_label.setText((String) intent.getExtras().get("reward"));
            lat =  intent.getExtras().getDouble("latitude");
            lng =  intent.getExtras().getDouble("longitude");
            participants = intent.getStringArrayListExtra("participants");
        }

        // Initialization of arrayAdapter which is needed for listing items
        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.activity_custom_textview, participants);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        friends.setAdapter(arrayAdapter);


    }

    @Override
    public void onMapReady(GoogleMap map) {

        LatLng currentLocation = new LatLng(lat, lng);

        if(currentLocation != null) {
            // Move the map to the predefined address
            map.setMyLocationEnabled(true);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 13));

            // Add a marker to the user-typed address
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(currentLocation));

            Circle circle = map.addCircle(new CircleOptions()
                    .center(currentLocation)
                    .radius(1609 / 5)
                    .strokeColor(Color.GRAY)
                    .strokeWidth(2)
                    .fillColor(Color.LTGRAY));

        }
    }

}
