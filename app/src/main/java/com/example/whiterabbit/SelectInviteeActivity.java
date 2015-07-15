package com.example.whiterabbit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class SelectInviteeActivity extends AppCompatActivity {

    ListView invitees;
    private static ArrayList<String> contacts = new ArrayList<String>();
    private static ArrayList<String> phoneNums = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_invitee);

        contacts.clear();
        phoneNums.clear();

        invitees = (ListView) findViewById(R.id.invitees);
        Button submit = (Button) findViewById(R.id.submit);

        Utility.createContactList(this, contacts);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, contacts);
        invitees.setAdapter(arrayAdapter);
        invitees.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CheckedTextView checkedTextView = (CheckedTextView) view;
                String selected = ((TextView) view).getText().toString();

                if (((CheckedTextView) view).isChecked() == false) {
                    if (phoneNums.contains(selected) == false)
                        phoneNums.add(selected);
                    //phoneNums.remove(selected);

                } else {
                    phoneNums.remove(selected);
                }

                checkedTextView.setChecked(!checkedTextView.isChecked());
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
