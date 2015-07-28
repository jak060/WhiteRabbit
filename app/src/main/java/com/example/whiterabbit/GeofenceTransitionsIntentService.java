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

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

public class GeofenceTransitionsIntentService extends IntentService{

    // For the debugging purpose
    protected final String TAG = this.getClass().getSimpleName();

    public GeofenceTransitionsIntentService() {
        super("geofence-transitions-service");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if(geofencingEvent.hasError()) {
            Log.e(TAG, "Error From onHandleIntent(GeofencingEvent).");
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            String geofenceTransitionDetails = getGeofenceTransitionDetails(
                    this,
                    geofenceTransition,
                    triggeringGeofences
            );

            sendNotification(geofenceTransitionDetails);
            Log.v(TAG, geofenceTransitionDetails);

        } else {
            Log.e(TAG, "Geofence transition error: invalid transition type"  + geofenceTransition);
        }
    }

    private String getGeofenceTransitionDetails(
            Context context,
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        ArrayList triggeringGeofencesIdsList = new ArrayList();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }

    private void sendNotification(String notificationDetails) {
        Intent notificationIntent = new Intent(getApplicationContext(), GeofenceActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        stackBuilder.addParentStack(GeofenceActivity.class);

        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle(notificationDetails)
                .setContentText("Click notification to return to app")
                .setContentIntent(notificationPendingIntent);

        builder.setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());

    }

    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "Entered";
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "Exited";
            default:
                return "Unknown Transition";
        }
    }

}
