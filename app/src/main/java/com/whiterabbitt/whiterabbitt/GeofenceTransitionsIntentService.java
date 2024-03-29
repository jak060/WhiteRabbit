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
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

/**
 * This service deals with issuing notification based on user's status with gefence
 */

public class GeofenceTransitionsIntentService extends IntentService{

    // For the debugging purpose
    protected final String TAG = this.getClass().getSimpleName();

    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public GeofenceTransitionsIntentService() {
        super("geofence-transitions-service");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Handles incoming intents.
     * @param intent sent by Location Services. This Intent is provided to Location
     *               Services (inside a PendingIntent) when addGeofences() is called.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if(geofencingEvent.hasError()) {
            Log.e(TAG, "Error From onHandleIntent(GeofencingEvent).");
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            String geofenceTransitionDetails = getGeofenceTransitionDetails(
                    this,
                    geofenceTransition,
                    triggeringGeofences
            );

            // Send notification and log the transition details.
            sendNotification(geofenceTransitionDetails);
            Log.v(TAG, geofenceTransitionDetails);

            if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                Log.v(TAG, "I've entered!");
                // This is to share this flag with other activities
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = prefs.edit();

                // This is winning condition when the result comes
                editor.putBoolean(Constants.GEOFENCES_TRANSITION_INTENT_ENTERED_KEY, true);
                editor.commit();
            } else if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                Log.v(TAG, "I've exited!");
                // This is to share this flag with other activities
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = prefs.edit();

                // This is losing condition when the result comes
                editor.putBoolean(Constants.GEOFENCES_TRANSITION_INTENT_ENTERED_KEY, false);
                editor.commit();
            }

        } else {
            Log.e(TAG, "Geofence transition error: invalid transition type" + geofenceTransition);
        }
    }

    /**
     * Gets transition details and returns them as a formatted string.
     *
     * @param context               The app context.
     * @param geofenceTransition    The ID of the geofence transition.
     * @param triggeringGeofences   The geofence(s) triggered.
     * @return                      The transition details formatted as String.
     */
    private String getGeofenceTransitionDetails(
            Context context,
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        return geofenceTransitionString;
    }

    /**
     * Posts a notification in the notification bar when a transition is detected.
     */
    private void sendNotification(String notificationDetails) {
        Intent notificationIntent = new Intent(Constants.NOTIFICATION_CLICKED);
        notificationIntent.putExtra("requestCode", 3);
        PendingIntent notificationPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 3, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setSmallIcon(R.mipmap.wr_logo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.wr_logo))
                .setContentTitle(notificationDetails)
                .setContentText("Click notification to return to app")
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000})
                .setContentIntent(notificationPendingIntent);


        builder.setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }

    /**
     * Maps geofence transition types to their human-readable equivalents.
     *
     * @param transitionType    A transition type constant defined in Geofence
     * @return                  A String indicating the type of transition
     */
    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "You've arrived!";
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "You just left the destination.";
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                return "Dwelling...";
            default:
                return "Unknown Transition";
        }
    }

}
