package com.whiterabbitt.whiterabbitt;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CustomListViewAdapter extends BaseAdapter{

    private LayoutInflater layoutInflater;
    private ArrayList<InvitationInfoActivity> infoList;
    private Context context;

    private Calendar calendar = Calendar.getInstance();

    StringBuilder updatedMyGeofenceStatus;

    int currPosition;

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

    public void updateList(ArrayList<InvitationInfoActivity> newList) {
        infoList.clear();
        infoList.addAll(newList);
        this.notifyDataSetChanged();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        String prevDate = "";
        currPosition = position;

        // If we haven't initialized this convertView, set it up
        if(convertView == null) {
            convertView = layoutInflater.inflate(R.layout.activity_custom_event_row, null);
            holder = new Holder();
            holder.container = (RelativeLayout) convertView.findViewById(R.id.event_container);
            holder.iconDate = (ImageView) convertView.findViewById(R.id.icon_date);
            holder.iconLocation = (ImageView) convertView.findViewById(R.id.icon_location);
            holder.date = (TextView) convertView.findViewById(R.id.event_row_date);
            holder.time = (TextView) convertView.findViewById(R.id.event_row_time);
            holder.title = (TextView) convertView.findViewById(R.id.event_row_title);
            holder.location = (TextView) convertView.findViewById(R.id.event_row_location);
            holder.status = (TextView) convertView.findViewById(R.id.event_status);
            holder.statusBar = convertView.findViewById(R.id.event_status_bar);
            convertView.setTag(holder);

        }
        // Otherwise, user the existing ones
        else {
            holder = (Holder) convertView.getTag();
        }

        // Make sure that the position is greater than 0 so that it won't go out of index
        if(position > 0) {
            prevDate = infoList.get(position - 1).getDate();
        }

        // If dates are matching, then hide the duplicate
        if(prevDate.equals(infoList.get(position).getDate())) {
            holder.date.setVisibility(View.GONE);
        } else {
            holder.date.setVisibility(View.VISIBLE);
        }

        // Set each text field with the corresponding data
        holder.date.setText(Utility.parseDate2(infoList.get(position).getDate()));
        holder.time.setText(infoList.get(position).getTime() + " / " + (Utility.parseDate2(infoList.get(position).getDate()).toUpperCase()));
        holder.title.setText(infoList.get(position).getTitle());
        holder.location.setText(infoList.get(position).getLocationShort());
        holder.title.setTextColor(holder.title.getTextColors().getDefaultColor());
        holder.time.setTextColor(holder.time.getTextColors().getDefaultColor());
        holder.location.setTextColor(holder.location.getTextColors().getDefaultColor());

        // This is to show indication light for events
        if(infoList.get(position).getNumOfAccepted() > 0) {
            holder.statusBar.setBackground(ContextCompat.getDrawable(context, R.color.event_accepted));
            holder.container.setBackground(ContextCompat.getDrawable(context, R.drawable.border_event_accepted));
            holder.status.setText(context.getResources().getString(R.string.accepted));
            holder.status.setTextColor(context.getResources().getColor(R.color.event_accepted));

            // To see whether geofence has been registered or not
            SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(context);
            Boolean isGeofenceRegistered = prefs.getBoolean(Constants.GEOFENCES_REGISTERED_KEY, false);
            Log.v(TAG, "isGeofenceRegistered? " + isGeofenceRegistered);

            String geofenceFlag = infoList.get(position).getGeofenceFlag();
            String myObjectId = ParseUser.getCurrentUser().getObjectId();

            // This should get F or T
            // F = False, which means user hasn't registered in geofence
            // T = True, which means user has registered in geofence
            String myGeofenceStatus = geofenceFlag.
                    substring(geofenceFlag.indexOf(":", geofenceFlag.indexOf(myObjectId)) + 1,
                            geofenceFlag.indexOf(":", geofenceFlag.indexOf(myObjectId)) + 2);

            // If not registered yet, then. . .
            if(myGeofenceStatus.equals("F")) {

                // Make the geofence flag to be T (true)
                updatedMyGeofenceStatus = new StringBuilder(geofenceFlag);
                updatedMyGeofenceStatus.setCharAt(geofenceFlag.indexOf(":", geofenceFlag.indexOf(myObjectId)) + 1, 'T');

                // Save the updated geofence flag
                ParseQuery<ParseObject> query = ParseQuery.getQuery("invitationInfo");
                query.getInBackground(infoList.get(position).getObjectId(), new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, com.parse.ParseException e) {
                        if (e == null) {
                            parseObject.put("geofenceFlag", updatedMyGeofenceStatus.toString());
                            parseObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(com.parse.ParseException e) {
                                    if(e == null) {

                                        // Intent for the geofence
                                        Intent intent = new Intent(context, GeofenceActivity.class);

                                        // Intent for the reward activity
                                        Intent intent2 = new Intent(context, RewardPreparationService.class);

                                        // Pass latitude and longitude for the geofence
                                        intent.putExtra("lat", infoList.get(currPosition).getLatitude());
                                        intent.putExtra("lng", infoList.get(currPosition).getLongitude());

                                        // Pass objectId of the event to reward activity
                                        intent2.putExtra("objectId", infoList.get(currPosition).getObjectId());

                                        Log.v(TAG, "ObjectId for CustomListViewAdapter: " + infoList.get(currPosition).getObjectId());

                                        String time = infoList.get(currPosition).getTime(); // Time holder
                                        String date = Utility.parseDate2(infoList.get(currPosition).getDate()); // Date holder

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
                                        } catch (ParseException e1) {
                                            e1.printStackTrace();
                                        }

                                        Log.v(TAG, "calendar before: " + calendar.getTimeInMillis());

                                        // We need it to trigger the geofence 10 minutes before the actual event
                                        // But currenly 1 min before the actual event only for debugging purposes
                                        long geofenceTriggerTime = 10 * 1000 * 60;

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
                                    } else {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            e.printStackTrace();
                        }
                    }
                });


            }
        }

        else if(infoList.get(position).getNumOfAccepted() == 0) {
            // Declined case where everyone declined the invitation
            if(infoList.get(position).getNumOfDeclined() >= infoList.get(position).getWith().size() - 1) {
                holder.statusBar.setBackground(ContextCompat.getDrawable(context, R.color.event_declined));
                holder.container.setBackground(ContextCompat.getDrawable(context, R.drawable.border_event_declined));
                holder.iconDate.setImageResource(R.drawable.icon_date_declined);
                holder.iconLocation.setImageResource(R.drawable.icon_location_declined);
                holder.title.setTextColor(context.getResources().getColor(R.color.event_declined));
                holder.time.setTextColor(context.getResources().getColor(R.color.event_declined));
                holder.location.setTextColor(context.getResources().getColor(R.color.event_declined));
                holder.status.setText(context.getResources().getString(R.string.declined));
                holder.status.setTextColor(context.getResources().getColor(R.color.event_declined));
            }

            // Pending case
            else {
                holder.statusBar.setBackground(ContextCompat.getDrawable(context, R.color.event_pending));
                holder.container.setBackground(ContextCompat.getDrawable(context, R.drawable.border_event_pending));
                holder.status.setText(context.getResources().getString(R.string.pending));
                holder.status.setTextColor(context.getResources().getColor(R.color.event_pending));
            }
        }

        return convertView;

    }

    static class Holder {
        RelativeLayout container;
        ImageView iconDate;
        ImageView iconLocation;
        TextView date;
        TextView time;
        TextView title;
        TextView location;
        TextView status;
        View statusBar;
    }

}