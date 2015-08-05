package com.example.whiterabbit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * This class is for the user testing
 */
public class UserTestingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_testing);

        Button earlyBtn = (Button) findViewById(R.id.button);
        Button lateBtn = (Button) findViewById(R.id.button2);
 //       Intent intent = getIntent();
//        final String objectId = intent.getExtras().getString("objectId");

        earlyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserTestingResultActivity.class);
                intent.putExtra("result", "On time? you get these messages:\n\n" + "1. You're the winner\n\n" +
                        "2. Your gift card from “John”(late comer) has arrived in your email.\n\n" +
                        "3. Enjoy your reward!\n\n" + "4. Please try other case OR press ok for survey");
                //intent.putExtra("objectId", objectId);
                startActivity(intent);
            }
        });

        lateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserTestingResultActivity.class);
                intent.putExtra("result", "Late? you get these messages:\n\n 1. Sorry, just missed a chance to win!\n\n" +
                        "2. Starbuck gift has been sent your friends\n\n" +
                        "3. The payment receipt is ready in your email\n\n" +
                        "4. Please try other case OR press ok for survey");
                //intent.putExtra("objectId", objectId);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        return;
    }
}
