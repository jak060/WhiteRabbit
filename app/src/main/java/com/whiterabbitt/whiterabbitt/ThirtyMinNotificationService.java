package com.whiterabbitt.whiterabbitt;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

/**
 * This class sends a friendly reminder 30 minutes before the actual event
 */
public class ThirtyMinNotificationService extends IntentService {

    private static final String NOTIFICATION_TITLE = "Hurry!";
    private static final String NOTIFICATION_CONTENT  = "You have 30 min to be on time for the next event";

    public ThirtyMinNotificationService() {
        super("thirty-min-notification-service");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        sendNotification(NOTIFICATION_TITLE);
    }

    // This method sets up the necessary things to display the push notification
    private void sendNotification(String notificationDetails) {
        Intent notificationIntent = new Intent(Constants.NOTIFICATION_CLICKED);
        notificationIntent.putExtra("requestCode", 4);
        PendingIntent notificationPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 4, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setSmallIcon(R.mipmap.wr_logo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.wr_logo))
                .setContentTitle(notificationDetails)
                .setContentText(NOTIFICATION_CONTENT)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000})
                .setContentIntent(notificationPendingIntent);

        builder.setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());

    }
}
