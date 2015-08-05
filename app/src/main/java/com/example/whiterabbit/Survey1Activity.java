package com.example.whiterabbit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

/**
 * This class deals with the survey 1
 */
public class Survey1Activity extends AppCompatActivity{

    String answerForA = "";
    String answerForB = "";
    String answerForC = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey1);

        final RadioGroup questionA = (RadioGroup) findViewById(R.id.radioGroup1);

        final RadioGroup questionB = (RadioGroup) findViewById(R.id.radioGroup2);

        final RadioGroup questionC = (RadioGroup) findViewById(R.id.radioGroup3);

        Button submitBtn = (Button) findViewById(R.id.button4);

        questionA.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton)findViewById(checkedId);
                answerForA = rb.getText().toString();
            }

        });

        questionB.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) findViewById(checkedId);
                answerForB = rb.getText().toString();
            }

        });

        questionC.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) findViewById(checkedId);
                answerForC = rb.getText().toString();
            }

        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ProgressDialog dialog = new ProgressDialog(Survey1Activity.this);
                dialog.setTitle("Thank You For Your Feedback");
                dialog.setMessage("Recording Answers. . .");
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                ParseObject survey1 = new ParseObject("survey1");
                survey1.put("answerA", answerForA);
                survey1.put("answerB", answerForB);
                survey1.put("answerC", answerForC);

                survey1.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        dialog.dismiss();
                        if(e == null) {
                            Intent intent = new Intent(getApplicationContext(), UserTestingActivity.class);
                            startActivity(intent);
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

    }

    @Override
    public void onBackPressed() {
        return;
    }

}
