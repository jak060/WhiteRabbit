package com.example.whiterabbit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

/**
 * This class is for displaying result of the user testing
 */
public class UserTestingResultActivity extends AppCompatActivity{

    String textfield = "";
    //String objectId = "";
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_testing_result);

        Intent intent = getIntent();
        String displayTxt = intent.getExtras().getString("result");
        //objectId = intent.getExtras().getString("objectId");

        TextView textView = (TextView) findViewById(R.id.textView5);

        textView.setText(displayTxt);

        Button submitBtn = (Button) findViewById(R.id.button3);
        Button otherCase = (Button) findViewById(R.id.button6);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), UserTestingActivity.class);
                startActivity(intent);
            }
        });

        otherCase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Survey2Activity.class);
                startActivity(intent);
            }
        });



    }
}
