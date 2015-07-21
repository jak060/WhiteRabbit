package com.example.whiterabbit;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class EventFragment extends Fragment {
    private String title;
    private int pageNum;

    private static String indicator;

    // For the debugging purpose
    public final String TAG = this.getClass().getSimpleName();

    private ArrayList <InvitationInfoActivity> infoList = new ArrayList<InvitationInfoActivity>();

    public static EventFragment newInstance(String title, int pageNum, String indicator) {

        EventFragment eventFragment = new EventFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("pageNumber", pageNum);
        bundle.putString("pageTitle", title);
        bundle.putString("indicator", indicator);

        eventFragment.setArguments(bundle);
        return eventFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNum = getArguments().getInt("pageNumber", 0);
        title = getArguments().getString("pageTitle");
        indicator = getArguments().getString("indicator");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_main, viewGroup, false);

        final ListView eventList = (ListView) view.findViewById(R.id.listView);

        infoList.clear();

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("invitationInfo");
        query.whereEqualTo("owner", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {

                    for (int i = 0; i < list.size(); i++) {
                        InvitationInfoActivity invitationInfoActivity = new InvitationInfoActivity();
                        invitationInfoActivity.setTitle((String) list.get(i).get("title"));
                        invitationInfoActivity.setTime((String) list.get(i).get("time"));
                        invitationInfoActivity.setDate((String) list.get(i).get("date"));
                        invitationInfoActivity.setLocation((String) list.get(i).get("location"));
                        invitationInfoActivity.setWith((ArrayList)list.get(i).get("invitees"));
                        infoList.add(invitationInfoActivity);
                    }

                    eventList.setAdapter(new CustomListViewAdapter(view.getContext(), infoList, indicator));

                } else {
                    Log.d(TAG, "Error: " + e.getMessage());
                }
            }
        });
        return view;
    }
}
