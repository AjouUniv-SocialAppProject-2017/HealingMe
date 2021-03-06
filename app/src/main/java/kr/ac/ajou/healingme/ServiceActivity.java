package kr.ac.ajou.healingme;

import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ServiceActivity extends AppCompatActivity implements TextWatcher {
    private EditText txtServiceCenter,txtTitleServiceCenter;
    private TextView txt_length;
    private Button btn_send;
    private Calendar Day1;
    private int year, month, day;
    private String userName;
    private int letterYear, letterMonth, letterDate;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user;
    private String userKey;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("고객센터");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        user = auth.getCurrentUser();
        userKey = user.getUid();

        txtTitleServiceCenter=(EditText)findViewById(R.id.customtitle);
        txtServiceCenter = (EditText) findViewById(R.id.edit_service);
        txt_length = (TextView) findViewById(R.id.review_length);

        btn_send = (Button) findViewById(R.id.btn_send);

        txtServiceCenter.addTextChangedListener(this);

        Calendar calendar = Calendar.getInstance();

        Day1 = Calendar.getInstance();

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServiceSendData serviceSendData = new ServiceSendData(userKey, txtTitleServiceCenter.getText().toString(),txtServiceCenter.getText().toString(), year, month + 1, day);  // 유저 이름과 메세지로 chatData 만들기
                databaseReference.child("serviceCenter").child(userKey).push().setValue(serviceSendData);  // 기본 database 하위 message라는 child에 chatData를 list로 만들기
                Toast.makeText(getApplicationContext(), "보냈습니다", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (s.length() >= 150) {
            Toast.makeText(getApplicationContext(), "글은 150자 까지 입력 가능합니다.", Toast.LENGTH_SHORT).show();
        }
        txt_length.setText(s.length() + " / 150자");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home: {
                onBackPressed();

                return true;
            }

        }
        return super.onOptionsItemSelected(item);
    }

}
