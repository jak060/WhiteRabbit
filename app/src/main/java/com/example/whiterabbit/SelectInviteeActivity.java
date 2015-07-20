package com.example.whiterabbit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;

public class SelectInviteeActivity extends AppCompatActivity {

    ListView invitees;
    private static ArrayList<String> contacts = new ArrayList<String>();
    private static ArrayList<String> phoneNums = new ArrayList<String>();
    // For the debugging purpose
    public final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_invitee);

        contacts.clear();
        phoneNums.clear();

        invitees = (ListView) findViewById(R.id.invitees);
        Button submit = (Button) findViewById(R.id.submit);

        //Utility.createContactList(this, contacts);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Set<String> set = prefs.getStringSet("friendList", null);

        contacts = new ArrayList<String>(set);

        final SparseBooleanArray selectedItem = new SparseBooleanArray();

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
                    //Log.v(TAG, "hello");
                } else {
                    selectedItem.delete(i);
                    invitees.setItemChecked(i, false);
                    phoneNums.remove(selected);
                    //Log.v(TAG, "world");
                }
/*
                String selected = (String) adapterView.getItemAtPosition(i);

                if (((CheckedTextView) view).isChecked() == false) {
                    if (phoneNums.contains(selected) == false)
                        phoneNums.add(selected);
                    //phoneNums.remove(selected);

                } else {
                    phoneNums.remove(selected);
                }

                checkedTextView.setChecked(!checkedTextView.isChecked());*/
            }
        });

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
