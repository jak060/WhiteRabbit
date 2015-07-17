package com.example.whiterabbit;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContactListFragment extends Fragment {
    private String title;
    private int pageNum;

    // For the debugging purpose
    public final String TAG = this.getClass().getSimpleName();

    ListView friends;
    private static ArrayList<String> contacts = new ArrayList<String>();
    private static ArrayList<String> phoneNums = new ArrayList<String>();
    private static HashMap<String, String> lookUpTable = new HashMap<String, String>();
    private static ArrayList<String> finalFriendList = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;

    public static ContactListFragment newInstance(String title, int pageNum) {

        ContactListFragment contactListFragment = new ContactListFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("pageNumber", pageNum);
        bundle.putString("pageTitle", title);

        contactListFragment.setArguments(bundle);
        return contactListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNum = getArguments().getInt("pageNumber", 1);
        title = getArguments().getString("pageTitle");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_contact_list, viewGroup, false);

        friends = (ListView) view.findViewById(R.id.friends);
        Utility.createContactList(getActivity(), contacts);
        arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, finalFriendList);

        saveOnlyNumbers(contacts, phoneNums);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    // The query was successful.
                    for (ParseUser user : objects) {
                        String username = user.getUsername();
                        String phoneNum = user.get("phoneNumber").toString();
                        phoneNum = phoneNum.replaceAll("[^0-9]", "");
                        Log.v(TAG, "In lookUpTables: " + phoneNum);
                        lookUpTable.put(phoneNum, username);

                    }

                    for (int i = 0; i < phoneNums.size(); i++) {
                        Log.v(TAG, "HashMap Working!?");
                        if (lookUpTable.containsKey(phoneNums.get(i))) {
                            Log.v(TAG, "HashMap Working!");
                            finalFriendList.add(contacts.get(i));
                        }
                    }

                    friends.setAdapter(arrayAdapter);
                } else {
                    // Something went wrong.
                    e.printStackTrace();
                }
            }
        });
//        Log.v(TAG, lookUpTable.get(phoneNums.get(5))
       // friends.setAdapter(arrayAdapter);
        return view;
    }

    public void saveOnlyNumbers(ArrayList<String> src, ArrayList<String> dest) {
        for(int i = 0; i < src.size(); i++) {
            String temp = src.get(i);
            temp = temp.substring(temp.indexOf(":") + 2);
            temp = temp.replaceAll("[^0-9]", "");

            Log.v(TAG, "In phoneNums: " + temp);

            dest.add(temp);
        }
    }
}
