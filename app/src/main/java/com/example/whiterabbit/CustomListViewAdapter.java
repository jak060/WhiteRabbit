package com.example.whiterabbit;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomListViewAdapter extends BaseAdapter{

    private LayoutInflater layoutInflater;
    private ArrayList<InvitationInfoActivity> infoList;
    private Context context;

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

            // Start the geofence
            //Intent intent = new Intent(context, GeofenceActivity.class);
            //intent.putExtra("lat", infoList.get(position).getLatitude());
            //intent.putExtra("lng", infoList.get(position).getLongitude());

            //context.startActivity(intent);

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
