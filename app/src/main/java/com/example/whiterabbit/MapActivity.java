package com.example.whiterabbit;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;


/**
 * This class deals with the Google Maps v2 w/ Geocoder
 */

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    EditText locationText; // To search the location
    ImageButton locationBtn; // To search the location

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Initialization
        locationText = (EditText) findViewById(R.id.locationText);
        locationBtn = (ImageButton) findViewById(R.id.locationBtn);

        // Display the map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {

        final GoogleMap tempMap = map;

        // When the user starts the map, start at the current position of the user
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        Location location = locationManager.getLastKnownLocation(locationManager
                .getBestProvider(criteria, false));
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng startLocation = new LatLng(latitude, longitude);
        map.setMyLocationEnabled(true); // This makes the button when the user clicks, brings the
                                        // map to the current position
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(startLocation, 13));

        // When user clicks the button, call the method which locates the address that user has typed
        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locate(tempMap);
            }
        });

    }

    public void locate(GoogleMap map) {

        // Hide the soft keyboard so that the user can see the map
        hideKeyboard(locationText);

        // It's for clearing markers each time user searches
        map.clear();

        // Convert the address that user has typed to String
        String location = locationText.getText().toString();

        List<Address> addressList = null;
        Address address = null;

        final StringBuilder strAddress = new StringBuilder();

        // Geocoder converts String address to the actual longitude and latitude
        Geocoder geocoder = new Geocoder(this);

        try {
            // This gets the user-typed address and save it into the StringBuilder
            addressList = geocoder.getFromLocationName(location, 1);
            address = addressList.get(0);
            for(int i = 0; i < address.getMaxAddressLineIndex(); i ++) {
                strAddress.append(address.getAddressLine(i)).append("\n");
            }

            Toast.makeText(this, strAddress, Toast.LENGTH_LONG).show();
        } catch(IOException e) {
            e.printStackTrace();
        }

        // Get the latitude and longitude of the user-typed address
        double lat = address.getLatitude();
        double lng = address.getLongitude();

        LatLng currentLocation = new LatLng(lat, lng);

        // Move the map to the user-typed address
        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 13));

        // Add a marker to the user-typed address
        map.addMarker(new MarkerOptions()
                .title("Location Found:")
                .snippet("Click HERE To Choose This Location :)")
                .position(currentLocation));

        // When the user clicks the marker, show the message
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return false;
            }
        });

        // When the user clicks the message, go back to the previous activty with
        // passing the user-typed address
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(getApplicationContext(), CreateEventActivity.class);
                intent.putExtra("address", strAddress.toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    // A method which hides the soft keyboard
    private void hideKeyboard(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}
