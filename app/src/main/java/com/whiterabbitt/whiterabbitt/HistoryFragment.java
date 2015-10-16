package com.whiterabbitt.whiterabbitt;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * This fragment deals with the listing out past events using the ListView
 */
public class HistoryFragment extends Fragment{

    private String title;
    private int pageNum;
    private ListView eventList;

    // For the debugging purpose
    public final String TAG = this.getClass().getSimpleName();

    private ArrayList<InvitationInfoActivity> infoList = new ArrayList<InvitationInfoActivity>();

    public static HistoryFragment newInstance(String title, int pageNum) {

        HistoryFragment historyFragment = new HistoryFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("pageNumber", pageNum);
        bundle.putString("pageTitle", title);

        historyFragment.setArguments(bundle);

        return historyFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNum = getArguments().getInt("pageNumber", 3);
        title = getArguments().getString("pageTitle");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_history_fragment, viewGroup, false);

        eventList = (ListView) view.findViewById(R.id.listView);

        // Make sure to clear the list before using it
        infoList.clear();

        ArrayList<String> myEventHistory = new ArrayList<String>((ArrayList) ParseUser.getCurrentUser().get("eventHistory"));

        // Make a query to the database to retrieve invitation information
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("invitationInfo");
        query.whereContainedIn("objectId", myEventHistory);
        query.addAscendingOrder("date");
        query.addAscendingOrder("parsedTime");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {

                    // Save all corresponding info to each field to set up the adapter
                    for (int i = 0; i < list.size(); i++) {
                        InvitationInfoActivity invitationInfoActivity = new InvitationInfoActivity();
                        invitationInfoActivity.setTitle((String) list.get(i).get("title"));
                        invitationInfoActivity.setTime((String) list.get(i).get("time"));
                        invitationInfoActivity.setDate((String) list.get(i).get("date"));
                        invitationInfoActivity.setLocation((String) list.get(i).get("location"));
                        invitationInfoActivity.setWith((ArrayList) list.get(i).get("invitees"));
                        invitationInfoActivity.setNumOfAccepted((Integer) list.get(i).get("accepted"));
                        invitationInfoActivity.setNumOfDeclined((Integer) list.get(i).get("declined"));
                        invitationInfoActivity.setCarrot((String) list.get(i).get("carrots"));
                        invitationInfoActivity.setLatitude((Double) list.get(i).get("lat"));
                        invitationInfoActivity.setLongitude((Double) list.get(i).get("lng"));
                        invitationInfoActivity.setHostId((String) list.get(i).get("hostID"));
                        invitationInfoActivity.setObjectId(list.get(i).getObjectId());
                        invitationInfoActivity.setGeofenceFlag((String) list.get(i).get("geofenceFlag"));
                        invitationInfoActivity.setWinners((ArrayList) list.get(i).get("winners"));
                        invitationInfoActivity.setLosers((ArrayList) list.get(i).get("losers"));
                        //Log.v(TAG, "Current State: " + invitationInfoActivity.getState());
                        Log.v(TAG, "ObjectId in HistoryFragment: " + list.get(i).getObjectId());
                        infoList.add(invitationInfoActivity);
                    }

                    // Set up the adapter
                    eventList.setAdapter(new CustomListViewAdapter2(view.getContext(), infoList));


                } else {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }
}
