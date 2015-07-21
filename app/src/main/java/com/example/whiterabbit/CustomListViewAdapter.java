package com.example.whiterabbit;

import android.content.Context;
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
    private String indicator = "";

    public CustomListViewAdapter(Context context, ArrayList<InvitationInfoActivity> infoList, String indicator) {
        this.infoList = infoList;
        layoutInflater = LayoutInflater.from(context);
        this.indicator = indicator;
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
        if(convertView == null) {
            convertView = layoutInflater.inflate(R.layout.activity_custom_event_row, null);
            holder = new Holder();
            holder.information = (TextView) convertView.findViewById(R.id.event);
            holder.light = (Button) convertView.findViewById((R.id.signalLight));
            convertView.setTag(holder);

        } else {
            holder = (Holder) convertView.getTag();
        }

        StringBuilder tempStringBuilder = new StringBuilder();

        for(int i = 0; i < infoList.get(position).getWith().size(); i++) {
            tempStringBuilder = tempStringBuilder.append(infoList.get(position).getWith().get(i) + ", ");

        }
        if(tempStringBuilder.length() > 0) {
            finalString = tempStringBuilder.substring(0, tempStringBuilder.lastIndexOf(","));
        }

        String temp = "Title: " + infoList.get(position).getTitle() + "\n" + "When: " + infoList.get(position).getTime() + ", " +
                infoList.get(position).getDate() + "\n" + "Where: " + infoList.get(position).getLocation() + "W/ " + finalString;
        holder.information.setText(temp);

        holder.light.setBackgroundColor(Color.parseColor("#F7D358"));
        return convertView;

    }

    static class Holder {
        TextView information;
        Button light;
    }

}
