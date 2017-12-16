package kr.ac.ajou.healingme;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class GetLetterDetailActivity extends AppCompatActivity {
    private String msg, color, key,userkey;
    private int year, month, date, point;
    private View rootView;
    private TextView textView, dateTextView;
    private Button deleteBtn, backBtn;
    private LetterModel letterModel;
    private LettersData lettersData;
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_letter_detail);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");


        msg = getIntent().getStringExtra("msg");
        userkey=getIntent().getStringExtra("userkey");
        key = getIntent().getStringExtra("key");
        color = getIntent().getStringExtra("color");
        year = getIntent().getIntExtra("year",1);
        month = getIntent().getIntExtra("month",1);
        date = getIntent().getIntExtra("date",1);


        backBtn = (Button) findViewById(R.id.backbtn);
        textView = (TextView) findViewById(R.id.get_message);
        dateTextView = (TextView) findViewById(R.id.date);
        deleteBtn = (Button) findViewById(R.id.deletebtn);
        textView.getText();
        dateTextView.getText();
        textView.setText(msg);
        dateTextView.setText(year + "/" + (month+1) + "/" + date);

        letterModel = new LetterModel();


        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("keys", key);
                letterModel.deleteLetter(userkey,key);
                Toast.makeText(getApplicationContext(),"편지가 삭제되었습니다",Toast.LENGTH_LONG).show();

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),GetLetterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        colorchange();

    }

    private void colorchange() {
        if (Objects.equals(color, "blue")) {
            textView.setBackgroundResource(R.drawable.blueletter);
        } else if (Objects.equals(color, "green")) {
            textView.setBackgroundResource(R.drawable.greenletter);
        } else if (Objects.equals(color, "orange")) {
            textView.setBackgroundResource(R.drawable.orangeletter);
        } else if (Objects.equals(color, "yellow")) {
            textView.setBackgroundResource(R.drawable.yellowletter);
        } else if (Objects.equals(color, "red")) {
            textView.setBackgroundResource(R.drawable.redletter);
        } else if (Objects.equals(color, "purple")) {
            textView.setBackgroundResource(R.drawable.purpleletter);
        } else {
            textView.setBackgroundResource(R.drawable.basicletter);
        }
    }
}
