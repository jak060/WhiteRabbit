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
                intent.putExtra("result", "Congratulation! You're the white rabbitt! Yay :D\n\n" +
                        "You just got an email with a gift card from your friend!\n\n" +
                        "You're a step away to be golden rabbitt :)");
                //intent.putExtra("objectId", objectId);
                startActivity(intent);
            }
        });

        lateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserTestingResultActivity.class);
                intent.putExtra("result", "Sorry, just missed a chance to win! Maybe next time :)\n\n" +
                        "Your receipt is ready in your email!");
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
