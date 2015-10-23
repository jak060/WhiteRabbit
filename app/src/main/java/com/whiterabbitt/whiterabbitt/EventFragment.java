package com.whiterabbitt.whiterabbitt;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * This fragment deals with handling and displaying current events
 */

public class EventFragment extends Fragment {
    private String title;
    private int pageNum;
    private ListView eventList;

    // For the debugging purpose
    public final String TAG = this.getClass().getSimpleName();

    private ArrayList <InvitationInfoActivity> infoList = new ArrayList<InvitationInfoActivity>();

    public static EventFragment newInstance(String title, int pageNum) {

        EventFragment eventFragment = new EventFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("pageNumber", pageNum);
        bundle.putString("pageTitle", title);

        eventFragment.setArguments(bundle);

        return eventFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNum = getArguments().getInt("pageNumber", 0);
        title = getArguments().getString("pageTitle");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_main, viewGroup, false);

        eventList = (ListView) view.findViewById(R.id.listView);
        //parseTimeUpdate();

        // Make sure to clear the list before using it
        infoList.clear();

        ArrayList<String> myEventList = new ArrayList<String>((ArrayList) ParseUser.getCurrentUser().get("eventList"));
        for(int i = 0; i < myEventList.size(); i++) {
            Log.v(TAG, "Here are my list of event obejctIds:" + myEventList.get(i));
        }

        // Make a query to the database to retrieve invitation information
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("invitationInfo");
        query.whereContainedIn("objectId", myEventList);
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
                        //Log.v(TAG, "Current State: " + invitationInfoActivity.getState());
                        Log.v(TAG, "ObjectId in EventFragment: " + list.get(i).getObjectId());
                        infoList.add(invitationInfoActivity);
                    }

                    // Set up the adapter
                    eventList.setAdapter(new CustomListViewAdapter(view.getContext(), infoList));

                    // Make each row clickable
                    eventList.setClickable(true);

                    // When the user clicks an item. . .
                    eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                            InvitationInfoActivity invitationInfoActivity = (InvitationInfoActivity) eventList.getItemAtPosition(position);

                            // Check if the selected event is pending
                            if (invitationInfoActivity.getNumOfAccepted() == 0 && infoList.get(position).getNumOfDeclined() < infoList.get(position).getWith().size() - 1) {

                                // To check that the user is not a host so that the user can responds to the invitation
                                if (!ParseUser.getCurrentUser().getObjectId().equals(invitationInfoActivity.getHostId())) {
                                    Intent intent = new Intent(getActivity(), RespondInvitationActivity.class);

                                    String invitationInfo = "Title: " + invitationInfoActivity.getTitle() + "\n" + "Time: " + invitationInfoActivity.getTime() + "\n" + "Date: " +
                                            Utility.parseDate2(invitationInfoActivity.getDate()) + "\n" + "Location: " + invitationInfoActivity.getLocation() + "\n" + "From - "
                                            + invitationInfoActivity.getWith().get(invitationInfoActivity.getWith().size() - 1)
                                            + "\n" + "Rewards: " + invitationInfoActivity.getCarrot();

                                    String contact = invitationInfoActivity.getWith().get(invitationInfoActivity.getWith().size() - 1);
                                    String name = contact.substring(0, contact.lastIndexOf(":"));
                                    String phoneNum = contact.substring(contact.lastIndexOf(":") + 2);
                                    phoneNum = phoneNum.replaceAll("[^0-9]", "");

                                    intent.putExtra("phoneNumber", phoneNum);
                                    intent.putExtra("objectId", invitationInfoActivity.getObjectId());
                                    intent.putExtra("name", name);
                                    intent.putExtra("title", invitationInfoActivity.getTitle());
                                    intent.putExtra("time", invitationInfoActivity.getTime());
                                    intent.putExtra("date", Utility.parseDate2(invitationInfoActivity.getDate()));
                                    intent.putExtra("location", invitationInfoActivity.getLocation());
                                    intent.putExtra("reward", invitationInfoActivity.getCarrot());

                                    startActivity(intent);
                                }

                                // If the user is a host, then just display the message how many people have responded
                                else {
                                    Integer numOfResponded = invitationInfoActivity.getNumOfAccepted() + invitationInfoActivity.getNumOfDeclined();
                                    Integer totalNumOfInvitees = invitationInfoActivity.getWith().size() - 1;
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setMessage(numOfResponded.toString() + " invitee(s)" + " has responded out of " + totalNumOfInvitees + " invitee(s)")
                                            .setTitle(R.string.dialog_title)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            });
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }


                            }

                            // Check if selected event is accepted
                            else if (infoList.get(position).getNumOfAccepted() > 0) {
                                Intent intent = new Intent(getActivity(), EventDetailActivity.class);
                                intent.putExtra("title", invitationInfoActivity.getTitle());
                                intent.putExtra("time", invitationInfoActivity.getTime());
                                intent.putExtra("date", invitationInfoActivity.getDate());
                                intent.putExtra("location", invitationInfoActivity.getLocation());
                                intent.putExtra("reward", invitationInfoActivity.getCarrot());
                                intent.putExtra("latitude", invitationInfoActivity.getLatitude());
                                intent.putExtra("longitude", invitationInfoActivity.getLongitude());
                                intent.putStringArrayListExtra("participants", invitationInfoActivity.getWith());
                                startActivity(intent);
                            }
                        }

                    });

                } else {
                    Log.d(TAG, "Error: " + e.getMessage());
                }
            }
        });

        return view;
    }
/*
    public void parseTimeUpdate() {
        // Make a query to the database to retrieve invitation information
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("invitationInfo");
        query.addAscendingOrder("parsedTime");
        query.setLimit(20);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                for(int i = 0; i < objects.size(); i ++) {
                    String temp = (String) objects.get(i).get("parsedTime");
                    String temp1 = Utility.parseTimeTo24HourFormat(temp);
                    objects.get(i).put("parsedTime", temp1);
                    objects.get(i).saveInBackground();
                }
            }
        });
    }*/
}
