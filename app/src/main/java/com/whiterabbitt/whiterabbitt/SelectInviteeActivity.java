package com.whiterabbitt.whiterabbitt;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Set;

// This class handles the case when user select invitees in CreateEventActivity page
public class SelectInviteeActivity extends AppCompatActivity {

    ListView invitees;

    // To hold contacts
    private static ArrayList<String> contacts = new ArrayList<String>();

    // To hold only phone numbers
    private static ArrayList<String> phoneNums = new ArrayList<String>();

    // For the debugging purpose
    public final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_invitee);

        // Make sure to clear lists before using them
        contacts.clear();
        phoneNums.clear();

        invitees = (ListView) findViewById(R.id.invitees);
        Button submit = (Button) findViewById(R.id.submit);

        //Utility.createContactList(this, contacts);

        // Use this shared friendList from ContactListFragment
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Set<String> set = prefs.getStringSet("friendList", null);

        // And copy it to the ArrayList
        contacts = new ArrayList<String>(set);

        final SparseBooleanArray selectedItem = new SparseBooleanArray();

        // These lines of code handles the case with checkbox
        // If checkbox is selected => put that phone number into the ArrayList
        // Else => remove it or do nothing if that phone number hasn't been selected before
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, contacts);
        invitees.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        invitees.setAdapter(arrayAdapter);
        invitees.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String selected = (String) adapterView.getItemAtPosition(i);

                if (!selectedItem.get(i)) {
                    invitees.setItemChecked(i, true);
                    selectedItem.put(i, true);

                    if (phoneNums.contains(selected) == false)
                        phoneNums.add(selected);

                } else {
                    selectedItem.delete(i);
                    invitees.setItemChecked(i, false);
                    phoneNums.remove(selected);

                }

            }
        });

        // Put this result back to the CreateEventActivity
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phoneNums.size() != 0) {
                    Intent intent = new Intent(getApplicationContext(), CreateEventActivity.class);
                    intent.putStringArrayListExtra("invitees", phoneNums);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    new AlertDialog.Builder(SelectInviteeActivity.this)
                            .setTitle("Hmmm...")
                            .setMessage("Please Select At Least One Contact")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).setIcon(android.R.drawable.ic_dialog_alert).show();
                }
            }
        });

    }
}
