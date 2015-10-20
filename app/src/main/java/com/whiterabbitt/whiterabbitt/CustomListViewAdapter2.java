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

import com.parse.ParseUser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CustomListViewAdapter2 extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private ArrayList<InvitationInfoActivity> infoList;
    private Context context;

    // For the debugging purpose
    public final String TAG = this.getClass().getSimpleName();

    public CustomListViewAdapter2(Context context, ArrayList<InvitationInfoActivity> infoList) {
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
        String prevDate = "";

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

        // This is to case of won event
        if(infoList.get(position).getWinners().contains(ParseUser.getCurrentUser().getObjectId())) {
            holder.statusBar.setBackground(ContextCompat.getDrawable(context, R.color.event_won));
            holder.container.setBackground(ContextCompat.getDrawable(context, R.drawable.border_event_won));
            holder.status.setText(context.getResources().getString(R.string.won));
            holder.status.setTextColor(context.getResources().getColor(R.color.event_won));
            holder.title.setTextColor(context.getResources().getColor(R.color.default_color));
            holder.time.setTextColor(context.getResources().getColor(R.color.event_won));
            holder.location.setTextColor(context.getResources().getColor(R.color.event_won));
            holder.iconDate.setImageResource(R.drawable.icon_date);
            holder.iconLocation.setImageResource(R.drawable.icon_location);
        }

        // Lost event case
        else {
            holder.statusBar.setBackground(ContextCompat.getDrawable(context, R.color.event_declined));
            holder.container.setBackground(ContextCompat.getDrawable(context, R.drawable.border_event_declined));
            holder.iconDate.setImageResource(R.drawable.icon_date_declined);
            holder.iconLocation.setImageResource(R.drawable.icon_location_declined);
            holder.title.setTextColor(context.getResources().getColor(R.color.event_declined));
            holder.time.setTextColor(context.getResources().getColor(R.color.event_declined));
            holder.location.setTextColor(context.getResources().getColor(R.color.event_declined));
            holder.status.setText(context.getResources().getString(R.string.lost));
            holder.status.setTextColor(context.getResources().getColor(R.color.event_declined));
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
