/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.whiterabbit;

import android.app.Activity;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.GeofencingApi;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class deals with geofence-related activities
 */

public class GeofenceActivity extends IntentService implements
        ConnectionCallbacks, OnConnectionFailedListener, ResultCallback<Status> {

    // For the debugging purpose
    protected final String TAG = this.getClass().getSimpleName();

    protected GoogleApiClient googleApiClient;

    protected ArrayList<Geofence> geofenceList;

    private boolean geofencesAdded;

    private PendingIntent geofencePendingIntent;

    private SharedPreferences sharedPreferences;

    public static final HashMap<String, LatLng> GEOFENCE_LOCATIONS = new HashMap<String, LatLng>();

    public GeofenceActivity() {
        super("geofence-activity");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Make sure to empty the list before using it

        //setContentView(R.layout.activity_geofence);
    }

    @Override
    protected void onHandleIntent (Intent intent) {
        GEOFENCE_LOCATIONS.clear();
        //Intent intent = getIntent();

            double lat = intent.getExtras().getDouble("lat");
            double lng = intent.getExtras().getDouble("lng");

            Log.v(TAG, "GEOFENCE LAT: " + lat);
            Log.v(TAG, "GEOFENCE LNG: " + lng);

            LatLng currentLocation = new LatLng(lat, lng);

            geofenceList = new ArrayList<Geofence>();
            geofencePendingIntent = null;
            sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
            geofencesAdded = sharedPreferences.getBoolean(Constants.GEOFENCES_ADDED_KEY, false);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(Constants.GEOFENCES_REGISTERED_KEY, true);
            editor.commit();

            GEOFENCE_LOCATIONS.put("WhiteRabbit", currentLocation);

            populateGeofenceList();

            buildGoogleApiClient();

    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.v(TAG, "Connected to GoogleApiClient");
        addGeofencesHandler();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.v(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.v(TAG, "Connection suspended");
    }

    public void populateGeofenceList() {
        for(Map.Entry<String, LatLng> entry : GEOFENCE_LOCATIONS.entrySet()) {
            geofenceList.add(new Geofence.Builder()
                    .setRequestId(entry.getKey())
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
                            Constants.GEOFENCE_RADIUS_IN_METERS
                    )
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
        }

    }

    private GeofencingRequest getGeofecingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);

        return builder.build();
    }

    public void addGeofencesHandler() {
        if(!googleApiClient.isConnected()) {
            Toast.makeText(this, "Not Connected!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    googleApiClient,
                    getGeofecingRequest(),
                    getGeofencePendingIntent()
            ).setResultCallback(this);
        } catch (SecurityException securityException) {
            logSecurityException(securityException);
        }
    }

    public void removeGeofencesHandler() {
        if(!googleApiClient.isConnected()) {
            Toast.makeText(this, "Not Connected!", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            LocationServices.GeofencingApi.removeGeofences(
                    googleApiClient,
                    getGeofencePendingIntent()
            ).setResultCallback(this);
        } catch (SecurityException securityException) {
            logSecurityException(securityException);
        }
    }

    private void logSecurityException(SecurityException securityException) {
        Log.e(TAG, "Invalid location permission. " +
            "You need to user ACCESS_FINE_LOCATION with geofences", securityException);
    }

    private PendingIntent getGeofencePendingIntent() {
        if(geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);

        return PendingIntent.getService(this, 0 , intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void onResult(Status status) {
        if(status.isSuccess()) {
            geofencesAdded = !geofencesAdded;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Constants.GEOFENCES_ADDED_KEY, geofencesAdded);
            editor.commit();

            if(geofencesAdded) {
                Toast.makeText(this, "Geofence is added.", Toast.LENGTH_SHORT).show();
                //removeGeofencesHandler();
            } else {
                Toast.makeText(this, "Geofenc is removed.", Toast.LENGTH_SHORT).show();
            }

        } else {
            Log.e(TAG, "Error in onResult: " + status.getStatusCode());
        }
    }

}