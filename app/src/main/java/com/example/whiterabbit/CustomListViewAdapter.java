package com.example.whiterabbit;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CustomListViewAdapter extends BaseAdapter{

    private LayoutInflater layoutInflater;
    private ArrayList<InvitationInfoActivity> infoList;
    private Context context;

    private Calendar calendar = Calendar.getInstance();;


    // For the debugging purpose
    public final String TAG = this.getClass().getSimpleName();

    public CustomListViewAdapter(Context context, ArrayList<InvitationInfoActivity> infoList) {
        this.infoList = infoList;
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
    }

    public int getCount() {
        return infoList.size();
    }

    public Object getItem(int i) {
        return infoList.get(i);
    }

    public long getItemId(int i) {
        return i;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        String finalString = "";

        // If we haven't initialized this convertView, set it up
        if(convertView == null) {
            convertView = layoutInflater.inflate(R.layout.activity_custom_event_row, null);
            holder = new Holder();
            holder.date = (TextView) convertView.findViewById(R.id.event_row_date);
            holder.time = (TextView) convertView.findViewById(R.id.event_row_time);
            holder.title = (TextView) convertView.findViewById(R.id.event_row_title);
            holder.location = (TextView) convertView.findViewById(R.id.event_row_location);
            holder.light = (Button) convertView.findViewById((R.id.signalLight));
            holder.status = (TextView) convertView.findViewById(R.id.status);
            convertView.setTag(holder);

        }
        // Otherwise, user the existing ones
        else {
            holder = (Holder) convertView.getTag();
        }

        holder.date.setText(infoList.get(position).getDate());
        holder.time.setText(infoList.get(position).getTime());
        holder.title.setText(infoList.get(position).getTitle());
        holder.location.setText(infoList.get(position).getLocation());

        // This is to show indication light for events
        if(infoList.get(position).getState() == 0) {
            holder.light.setBackgroundColor(Color.parseColor("#00FF00")); // Green light
            holder.status.setText("Accepted");

            // To see whether geofence has been registered or not
            SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(context);
            Boolean isGeofenceRegistered = prefs.getBoolean(Constants.GEOFENCES_REGISTERED_KEY, false);
            Log.v(TAG, "isGeofenceRegistered? " + isGeofenceRegistered);

            // If not registered yet, then. . .
            if(!isGeofenceRegistered) {

                // Intent for the geofence
                Intent intent = new Intent(context, GeofenceActivity.class);

                // Intent for the reward activity
                Intent intent2 = new Intent(context, RewardPreparationService.class);

                // Pass latitude and longitude for the geofence
                intent.putExtra("lat", infoList.get(position).getLatitude());
                intent.putExtra("lng", infoList.get(position).getLongitude());

                // Pass objectId of the event to reward activity
                intent2.putExtra("objectId", infoList.get(position).getObjectId());

                Log.v(TAG, "ObjectId for CustomListViewAdapter: " + infoList.get(position).getObjectId());

                String time = infoList.get(position).getTime(); // Time holder
                String date = infoList.get(position).getDate(); // Date holder

                // Combine the date and time
                String dateTime = date + " " + time;

                Log.v(TAG, "dateTime: " + dateTime.toString());

                // The format for the date and time (ex. Aug 25, 2015 11:50 PM)
                DateFormat inFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm aa");

                Date myDate = null;

                try {
                    // Convert the string format of date to actual Date object
                    myDate = inFormat.parse(dateTime);
                    Log.v(TAG, "myDate: " + myDate.toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Log.v(TAG, "calendar before: " + calendar.getTimeInMillis());

                // We need it to trigger the geofence 10 minutes before the actual event
                // But currenly 1 min before the actual event only for debugging purposes
                long geofenceTriggerTime = 1 * 1000 * 60;

                // Set the date to the calendar
                calendar.setTime(myDate);

                Log.v(TAG, "calendar after: " + calendar.getTimeInMillis());

                // Schedule the geofence activity at 10 mins before the actualy event
                PendingIntent pendingIntent = PendingIntent.getService(context, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - geofenceTriggerTime, pendingIntent);

                // Schedule the reward activity at the actual event time
                PendingIntent pendingIntent2 = PendingIntent.getService(context, 2, intent2, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager alarmManager2 = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmManager2.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent2);
            }
        } else if((infoList.get(position).getState() > 0) && (infoList.get(position).getState() < infoList.get(position).getWith().size())) {
            holder.light.setBackgroundColor(Color.parseColor("#F7D358")); // Yellow light
            holder.status.setText("Pending");
        } else {
            holder.light.setBackgroundColor(Color.parseColor("#FF0000")); // Red light
            holder.status.setText("Declined");
        }




        return convertView;

    }

    static class Holder {
        TextView date;
        TextView time;
        TextView title;
        TextView location;
        Button light;
        TextView status;
    }

}
