package com.whiterabbitt.whiterabbitt;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This fragment deals with bringing and showing friends as a list view
 */

public class ContactListFragment extends Fragment {
    private String title;
    private int pageNum;

    // For the debugging purpose
    public final String TAG = this.getClass().getSimpleName();

    // A listView
    ListView friends;

    // A list that has names and phone numbers
    private static ArrayList<String> contacts = new ArrayList<String>();

    // A list that has only phone numbers (no other characters)
    private static ArrayList<String> phoneNums = new ArrayList<String>();

    // A list that has users from the database
    private static HashMap<String, String> lookUpTable = new HashMap<String, String>();

    // A final list that contains friends list
    private static ArrayList<String> finalFriendList = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;

    // A method that creates a new instance of this class
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

        // Clear all the lists
        contacts.clear();
        phoneNums.clear();
        lookUpTable.clear();
        finalFriendList.clear();

        // Save a contact list into the ArrayList
        Utility.createContactList(getActivity(), contacts);

        // For debugging purposes. . . Getting contact list from the phone
        for(int i = 0; i < contacts.size(); i ++) {
            Log.v(TAG, "My Contact List Number " + i + ": " + contacts.get(i));
        }

        // Initialization of arrayAdapter which is needed for listing items
        arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, finalFriendList);

        // Save only numbers into the ArrayList
        Utility.saveOnlyNumbers(contacts, phoneNums);

        // Check whether those phone numbers are in the database
        checkPhoneNumbersFromDB();

        // When the user selects one of his/her friends. . .
        friends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final ProgressDialog dialog = new ProgressDialog(getActivity());
                dialog.setTitle("Thank You For Your Patience");
                dialog.setMessage("Bringing Friend's Information. . .");
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                Log.v(TAG, "ListView item: " + friends.getItemAtPosition(position));

                // This string only saves the number to check from the database to see
                // if that phone number exists. . .
                String temp = (String) friends.getItemAtPosition(position);
                temp = temp.substring(temp.indexOf("(") + 1, temp.lastIndexOf(")"));

                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.whereEqualTo("username", temp);

                // Find that user from the database
                query.findInBackground(new FindCallback<ParseUser>() {
                    public void done(List<ParseUser> objects, ParseException e) {
                        if (e == null) {
                            dialog.dismiss();
                            // Pass necessary information to the detail page
                            Intent intent = new Intent(getActivity(), ContactActivity.class);
                            intent.putExtra("firstName", (String) objects.get(0).get("firstName"));
                            intent.putExtra("lastName", (String) objects.get(0).get("lastName"));
                            intent.putExtra("phoneNumber", (String) objects.get(0).get("phoneNumber"));
                            intent.putExtra("username", (String) objects.get(0).get("username"));
                            intent.putExtra("carrots", (Integer) objects.get(0).get("carrots"));
                            intent.putExtra("rankPoints", (Integer) objects.get(0).get("rankPoints"));
                            intent.putExtra("donationPoints", (Integer) objects.get(0).get("donationPoints"));

                            // Start the activity
                            startActivity(intent);
                        } else {
                            e.printStackTrace();
                        }
                    }
                });


            }
        });

        return view;
    }

    // This method checks database to see whether certain phone numbers are in there
    public void checkPhoneNumbersFromDB() {
        // A parse query which brings users from the database
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    // The query was successful.
                    for (ParseUser user : objects) {

                        // Save the user name and the phone numbers from the database to the look-up table
                        String username = user.getUsername();
                        String phoneNum = user.get("phoneNumber").toString();
                        String firstName = user.get("firstName").toString();
                        String lastName = user.get("lastName").toString();
                        phoneNum = phoneNum.replaceAll("[^0-9]", "");

                        Log.v(TAG, "In lookUpTables: " + phoneNum);

                        // It would look like Jacob Lee(JacobLG3)
                        String formattedUser = firstName + " " + lastName + " (" + username + ")";

                        if(!username.equals(ParseUser.getCurrentUser().get("username"))) {
                            lookUpTable.put(phoneNum, formattedUser);
                        }


                    }

                    // Check whether those phone numbers from user's contact exist
                    // in our database
                    for (int i = 0; i < phoneNums.size(); i++) {
                        //Log.v(TAG, "HashMap Working!?");
                        // If yes, put it into the finalFriendList
                        if (lookUpTable.containsKey(phoneNums.get(i))) {
                            //Log.v(TAG, "HashMap Working!");
                            finalFriendList.add(lookUpTable.get(phoneNums.get(i)));
                        }
                    }

                    // This is to share this friends list with other activities
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    SharedPreferences.Editor editor = prefs.edit();

                    //Set the values
                    Set<String> set = new HashSet<String>();
                    set.addAll(finalFriendList);
                    editor.putStringSet("friendList", set);
                    editor.commit();

                    // List those items
                    friends.setAdapter(arrayAdapter);
                } else {
                    // Something went wrong.
                    e.printStackTrace();
                }
            }
        });
    }
}
