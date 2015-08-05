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
 * This class handles survey2
 */
public class Survey2Activity extends AppCompatActivity {

    String answerForA = "";
    String answerForB = "";
    String answerForC = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey2);

        final RadioGroup questionA = (RadioGroup) findViewById(R.id.radioGroup1);
        questionA.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) findViewById(checkedId);
                answerForA = rb.getText().toString();
            }

        });

        final RadioGroup questionB = (RadioGroup) findViewById(R.id.radioGroup2);
        questionB.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) findViewById(checkedId);
                answerForB = rb.getText().toString();
            }

        });

        final EditText questionC = (EditText) findViewById(R.id.editText6);

        Button submitBtn = (Button) findViewById(R.id.button5);

        Utility.hideKeyboard(this, questionC);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(questionC.getText().toString().length() > 0) {
                    answerForC = questionC.getText().toString();
                }

                final ProgressDialog dialog = new ProgressDialog(Survey2Activity.this);
                dialog.setTitle("Thank You For Your Feedback");
                dialog.setMessage("Recording Answers. . .");
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                ParseObject survey1 = new ParseObject("survey2");
                survey1.put("answerA", answerForA);
                survey1.put("answerB", answerForB);
                survey1.put("answerC", answerForC);

                survey1.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        dialog.dismiss();
                        if(e == null) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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
