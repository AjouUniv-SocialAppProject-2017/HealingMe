package kr.ac.ajou.healingme;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.content.Context;
import android.icu.util.GregorianCalendar;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Objects;
import java.util.Random;

public class GetLetterActivity extends ActionBarActivity {
    private TextView Text,dateTxt;
    private Button button;
    private String userName,fcmToken,msg,color,id;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    private NotificationManager mNotification;
    private AlarmManager mManager;
    private GregorianCalendar mCalendar;
    private TextView letter;
    private int date,month,year;
    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_letter);


        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        String title= getString(R.string.title_getletter);
        getSupportActionBar().setTitle(title);


        Text=(TextView)findViewById(R.id.get_message);
        userName = "user" + new Random().nextInt(10000);
        fcmToken= FirebaseInstanceId.getInstance().getToken();
        letter=(TextView)findViewById(R.id.get_message);

        dateTxt=(TextView)findViewById(R.id.date);

        mNotification = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        mManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        mCalendar = new GregorianCalendar();


        Bring();


    }

    private void Bring() {

        Bundle extras = getIntent().getExtras();
        id = String.valueOf(extras.getInt("notificationId"));

        databaseReference.child("message").child(id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                LetterData letterData=dataSnapshot.getValue(LetterData.class);
                Toast.makeText(getApplicationContext(),id,Toast.LENGTH_LONG).show();
                msg = letterData.getLetter();
                color = letterData.getColor();

                year=letterData.getYear();
                month=letterData.getMonth();
                date=letterData.getDate();

                colorchange();

                Text.setText(msg);
                dateTxt.setText(year+"/"+month+"/"+date);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void colorchange(){
        if(Objects.equals(color, "blue")){
            letter.setBackgroundResource(R.drawable.blueletter);
        }else if(Objects.equals(color, "green")){
            letter.setBackgroundResource(R.drawable.greenletter);
        }else if(Objects.equals(color, "orange")){
            letter.setBackgroundResource(R.drawable.orangeletter);
        }else if(Objects.equals(color, "yellow")){
            letter.setBackgroundResource(R.drawable.yellowletter);
        }else if(Objects.equals(color, "red")){
            letter.setBackgroundResource(R.drawable.redletter);
        }else if(Objects.equals(color, "purple")){
            letter.setBackgroundResource(R.drawable.purpleletter);
        }else{
            letter.setBackgroundResource(R.drawable.basicletter);
        }
    }
}
