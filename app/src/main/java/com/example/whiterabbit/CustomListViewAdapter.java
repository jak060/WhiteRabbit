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
            holder.information = (TextView) convertView.findViewById(R.id.event);
            holder.light = (Button) convertView.findViewById((R.id.signalLight));
            holder.status = (TextView) convertView.findViewById(R.id.status);
            convertView.setTag(holder);

        }

        // Otherwise, user the existing ones
        else {
            holder = (Holder) convertView.getTag();
        }


        // To hold the final string of invitees
        StringBuilder tempStringBuilder = new StringBuilder();

        for(int i = 0; i < infoList.get(position).getWith().size(); i++) {
            tempStringBuilder = tempStringBuilder.append(infoList.get(position).getWith().get(i) + ", ");

        }

        // final string of all invitees without the last comma
        if(tempStringBuilder.length() > 0) {
            finalString = tempStringBuilder.substring(0, tempStringBuilder.lastIndexOf(","));
        }

        // Set up the string of all the invitation information
        String temp = "Title: " + infoList.get(position).getTitle() + "\n" + "When: " + infoList.get(position).getTime() + ", " +
                infoList.get(position).getDate() + "\n" + "Where: " + infoList.get(position).getLocation() + "Participants:\n" + finalString;
        holder.information.setText(temp);

        // This is to show indication light for events
        if(infoList.get(position).getState() == 0) {
            holder.light.setBackgroundColor(Color.parseColor("#00FF00")); // Green light
            holder.status.setText("Accepted");


            SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(context);

            Boolean isGeofenceRegistered = prefs.getBoolean(Constants.GEOFENCES_REGISTERED_KEY, false);

            Log.v(TAG, "isGeofenceRegistered? " + isGeofenceRegistered);
            // Start the geofence
            //Intent intent = new Intent(context, GeofenceActivity.class);
            //intent.putExtra("lat", infoList.get(position).getLatitude());
            //intent.putExtra("lng", infoList.get(position).getLongitude());

            //context.startActivity(intent);
            if(!isGeofenceRegistered) {
                Intent intent = new Intent(context, GeofenceActivity.class);
                Intent intent2 = new Intent(context, RewardPreparationService.class);
                //intent.putExtra("time", infoList.get(currPosition).getTime());
                //intent.putExtra("date", infoList.get(currPosition).getDate());
                intent.putExtra("lat", infoList.get(position).getLatitude());
                intent.putExtra("lng", infoList.get(position).getLongitude());

                intent2.putExtra("objectId", infoList.get(position).getObjectId());

                Log.v(TAG, "ObjectId for CustomListViewAdapter: " + infoList.get(position).getObjectId());

                String time = infoList.get(position).getTime();

                String date = infoList.get(position).getDate();

                String dateTime = date + " " + time;

                Log.v(TAG, "dateTime: " + dateTime.toString());

                DateFormat inFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm aa");

                Date myDate = null;

                try {
                    myDate = inFormat.parse(dateTime);
                    Log.v(TAG, "myDate: " + myDate.toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Log.v(TAG, "calendar before: " + calendar.getTimeInMillis());

                // We need it to trigger the geofence 10 minutes before the actual event
                long geofenceTriggerTime = 5 * 1000 * 60;

                calendar.setTime(myDate);

                Log.v(TAG, "calendar after: " + calendar.getTimeInMillis());

                PendingIntent pendingIntent = PendingIntent.getService(context, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - geofenceTriggerTime, pendingIntent);

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
        TextView information;
        Button light;
        TextView status;
    }

}
