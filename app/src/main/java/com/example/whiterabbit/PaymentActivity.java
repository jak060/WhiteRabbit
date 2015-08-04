package com.example.whiterabbit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * This class handles the payment related activities
 */
public class PaymentActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        ImageView paypalImage = (ImageView) findViewById(R.id.imageView);
        paypalImage.setImageResource(R.drawable.paypal_image);

        Button loginBtn = (Button) findViewById(R.id.button7);

        EditText e1 = (EditText) findViewById(R.id.editText);
        EditText e2 = (EditText) findViewById(R.id.editText7);

        Utility.hideKeyboard(this, e1);

        Utility.hideKeyboard(this, e2);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Survey1Activity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        return;
    }
}
