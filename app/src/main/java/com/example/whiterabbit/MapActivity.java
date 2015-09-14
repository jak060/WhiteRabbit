package com.example.whiterabbit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import com.google.android.gms.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
 * This class deals with the Google Maps v2 w/ Geocoder
 */

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    EditText locationText; // To search the location
    ImageButton locationBtn; // To search the location
    LocationManager mlocationManager;
    Location globalLocation;
    double lat;
    double lng;
    GoogleMap googleMap;

    // For the debugging purpose
    public final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Initialization
        locationText = (EditText) findViewById(R.id.locationText);
        locationBtn = (ImageButton) findViewById(R.id.locationBtn);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap map) {

        googleMap = map;

        // When the user starts the map, start at the current position of the user
           mlocationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // If we can find any location provider that meets our criteria,
        // then set it up
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        Log.v(TAG, "Best proviver: " + bestProvider);
        globalLocation = locationManager.getLastKnownLocation(bestProvider);
        googleMap.setMyLocationEnabled(true);

        // If we found one, use it as the current location
        if(globalLocation != null) {
            onLocationChanged(globalLocation);
        }

        /*
        locationManager.requestLocationUpdates(bestProvider, 20000, 0, new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                LatLng startLocation = new LatLng(latitude, longitude);
                // This makes the button when the user clicks, brings the
                // map to the current position
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLocation, 13));
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        });*/


        // When the user long clicks the map, add the marker
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {

                // Hide the soft keyboard so that the user can see the map
                hideKeyboard(locationText);

                // Clear the map
                googleMap.clear();

                // Add a marker to the user-typed address
                Marker marker = map.addMarker(new MarkerOptions()
                        .title("Location Found:")
                        .snippet("Click HERE To Choose This Location :)")
                        .position(latLng));

                marker.showInfoWindow();

                List<Address> addressList = null;
                Address address = null;

                final StringBuilder strAddress = new StringBuilder();

                // Geocoder converts actual longitude and latitude to the String address
                Geocoder geocoder = new Geocoder(getApplicationContext());

                try {
                    // This gets the user-typed address and save it into the StringBuilder
                    addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    address = addressList.get(0);
                    for(int i = 0; i < address.getMaxAddressLineIndex(); i ++) {
                        strAddress.append(address.getAddressLine(i)).append("\n");
                    }

                    Toast.makeText(getApplicationContext(), strAddress, Toast.LENGTH_LONG).show();
                } catch(IOException e) {
                    e.printStackTrace();
                }

                // When the user clicks the message, go back to the previous activity with
                // passing the user-typed address
                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Intent intent = new Intent(getApplicationContext(), CreateEventActivity.class);
                        intent.putExtra("address", strAddress.toString());
                        intent.putExtra("lat", latLng.latitude);
                        intent.putExtra("lng", latLng.longitude);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });


            }
        });

        // When user clicks the button, call the method which locates the address that user has typed
        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(locationText.getText().toString().length() > 0) {
                    locate(googleMap);
                }

            }
        });

        // When user clicks the search button, call the method which locates the address that user has typed
        locationText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if(locationText.getText().toString().length() > 0) {
                        locate(googleMap);
                    }
                    return true;
                }
                return false;
            }
        });

    }

    // A method that returns the last known location
    private Location getLastKnownLocation() {
        mlocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mlocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Log.v(TAG, "Location Providers: " + provider);
            Location l = mlocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    public void locate(final GoogleMap map) {

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
            if(addressList.size() > 0) {
                address = addressList.get(0);

                for(int i = 0; i < address.getMaxAddressLineIndex(); i ++) {
                    strAddress.append(address.getAddressLine(i)).append("\n");
                }

                Toast.makeText(this, strAddress, Toast.LENGTH_LONG).show();
            }


        } catch(IOException e) {
            e.printStackTrace();
        }


        if(address != null) {
            // Get the latitude and longitude of the user-typed address
            lat = address.getLatitude();
            lng = address.getLongitude();
            Log.v(TAG, "lat: " + lat);
            Log.v(TAG, "lng: " + lng);


            LatLng currentLocation = new LatLng(lat, lng);;

            // Move the map to the user-typed address
            map.setMyLocationEnabled(true);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 13));

            // Add a marker to the user-typed address
            Marker marker = map.addMarker(new MarkerOptions()
                    .title("Location Found:")
                    .snippet("Click HERE To Choose This Location :)")
                    .position(currentLocation));

            marker.showInfoWindow();

            // When the user clicks the message, go back to the previous activity with
            // passing the user-typed address
            map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                @Override
                public void onInfoWindowClick(Marker marker) {
                    Intent intent = new Intent(getApplicationContext(), CreateEventActivity.class);
                    intent.putExtra("address", strAddress.toString());
                    intent.putExtra("lat", lat);
                    intent.putExtra("lng", lng);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        } else {
            new AlertDialog.Builder(MapActivity.this)
                    .setTitle("Location Not Found.")
                    .setMessage("Please Try Again.")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).setIcon(android.R.drawable.ic_dialog_alert).show();
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng startLocation = new LatLng(latitude, longitude);
        // This makes the button when the user clicks, brings the
        // map to the current position
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLocation, 13));
    }

    // A method which hides the soft keyboard
    private void hideKeyboard(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}
