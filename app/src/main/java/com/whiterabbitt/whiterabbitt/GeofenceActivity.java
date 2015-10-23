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

package com.whiterabbitt.whiterabbitt;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
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

    private String objectId;

    public GeofenceActivity() {
        super("geofence-activity");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent (Intent intent) {

        // Clear the list before using it
        GEOFENCE_LOCATIONS.clear();

        // Get the latitude and longitude from the last intent
        double lat = intent.getExtras().getDouble("lat");
        double lng = intent.getExtras().getDouble("lng");

        // Get the objectId of the current event
        objectId = intent.getAction();

        Log.v(TAG, "GEOFENCE LAT: " + lat);
        Log.v(TAG, "GEOFENCE LNG: " + lng);
        Log.v(TAG, "OBJECTID: " + objectId);

        // Make a LatLng object with the passed latitude and longitude
        LatLng currentLocation = new LatLng(lat, lng);

        geofenceList = new ArrayList<Geofence>();
        geofencePendingIntent = null;

        sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        geofencesAdded = sharedPreferences.getBoolean(Constants.GEOFENCES_ADDED_KEY, false);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Constants.GEOFENCES_REGISTERED_KEY, true);
        editor.commit();

        // Put the location to the HashMap
        GEOFENCE_LOCATIONS.put(objectId, currentLocation);

        populateGeofenceList();

        buildGoogleApiClient();

    }

    // Builds a GoogleApiClient. Uses the {@code #addApi} method to request the LocationServices API.
    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    // Runs when a GoogleApiClient object successfully connects.
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

    // This method dynamically create geofences based on the user's chosen location.
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


    // Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
    // Also specifies how the geofence notifications are initially triggered.
    private GeofencingRequest getGeofecingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);

        return builder.build();
    }


    // Adds geofences, which sets alerts to be notified when the device enters or exits one of the
    // specified geofences. Handles the success or failure results returned by addGeofences().
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

    // Removes geofences, which stops further notifications when the device enters or exits
    // previously registered geofences.
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

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent getGeofencePendingIntent() {
        if(geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        if(objectId != null) {
            intent.setAction(objectId);
        }
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void onResult(Status status) {
        if(status.isSuccess()) {
            geofencesAdded = !geofencesAdded;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Constants.GEOFENCES_ADDED_KEY, geofencesAdded);
            editor.commit();

        } else {
            Log.e(TAG, "Error in onResult: " + status.getStatusCode());
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(Constants.GEOFENCES_REGISTERED_KEY, false);
            editor.commit();
        }
    }

}