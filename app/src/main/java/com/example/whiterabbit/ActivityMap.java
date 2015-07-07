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

public class ActivityMap extends FragmentActivity implements OnMapReadyCallback {

    EditText locationText;
    ImageButton locationBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        locationText = (EditText) findViewById(R.id.locationText);
        locationBtn = (ImageButton) findViewById(R.id.locationBtn);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {

        final GoogleMap tempMap = map;

        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();

        Location location = locationManager.getLastKnownLocation(locationManager
                .getBestProvider(criteria, false));

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        LatLng startLocation = new LatLng(latitude, longitude);

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(startLocation, 13));


        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locate(tempMap);
            }
        });

    }

    public void locate(GoogleMap map) {

        hideKeyboard(locationText);
        map.clear();

        String location = locationText.getText().toString();

        List<Address> addressList = null;
        Address address = null;
        Marker myMarker = null;

        final StringBuilder strAddress = new StringBuilder();

        Geocoder geocoder = new Geocoder(this);
        try {
            addressList = geocoder.getFromLocationName(location, 1);
            address = addressList.get(0);
            for(int i = 0; i < address.getMaxAddressLineIndex(); i ++) {
                strAddress.append(address.getAddressLine(i)).append("\n");
            }

            Toast.makeText(this, strAddress, Toast.LENGTH_LONG).show();
        } catch(IOException e) {
            e.printStackTrace();
        }

        double lat = address.getLatitude();
        double lng = address.getLongitude();

        LatLng currentLocation = new LatLng(lat, lng);

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 13));

        map.addMarker(new MarkerOptions()
                .title("Location Found:")
                .snippet("Click HERE To Choose This Location :)")
                .position(currentLocation));

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return false;
            }
        });

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(getApplicationContext(), CreateEvent.class);
                intent.putExtra("address", strAddress.toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void hideKeyboard(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}
