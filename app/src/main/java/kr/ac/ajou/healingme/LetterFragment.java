package kr.ac.ajou.healingme;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@RequiresApi(api = Build.VERSION_CODES.N)
public class LetterFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private LetterModel letterModel;
    private LetterCountModel letterCountModel;

    private EditText letterEdit;
    private Button button,calendarBtn;
    private TextView Displaycalendar;

    private String color,key,userkey;
    private int year, month, day;

    public static int id,daydiff,getDaydiff;
    private int letterYear,letterMonth, letterDate;

    private Calendar calendar = Calendar.getInstance();

    private AlarmManager mManager;
    private GregorianCalendar mCalendar;
    private Calendar Day1;
    private Calendar Day2;
    private String resDaydiff;

    private long diffdays;
    private int Calenderclick;
    private int lettercount=0;



    public LetterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_letters, container, false);

        letterEdit = (EditText) rootView.findViewById(R.id.edit_message);
        button = (Button) rootView.findViewById(R.id.btn_send);

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        Day1=new GregorianCalendar(year,month,day);

        calendarBtn=(Button)rootView.findViewById(R.id.btn_calendar);

        Displaycalendar=(TextView)rootView.findViewById(R.id.calendarTxt);
        Displaycalendar.setText(year+"년 "+(month+1)+"월 "+day+"일");


        rootView.findViewById(R.id.btn_red).setOnClickListener(this);
        rootView.findViewById(R.id.btn_green).setOnClickListener(this);
        rootView.findViewById(R.id.btn_blue).setOnClickListener(this);
        rootView.findViewById(R.id.btn_orange).setOnClickListener(this);
        rootView.findViewById(R.id.btn_purple).setOnClickListener(this);
        rootView.findViewById(R.id.btn_yellow).setOnClickListener(this);
        rootView.findViewById(R.id.btn_basic).setOnClickListener(this);

        mManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);


        letterModel = new LetterModel();
        letterCountModel=new LetterCountModel();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Objects.equals(Calenderclick,1)) {
                    letterModel.saveLetter(userkey,key, letterEdit.getText().toString(), color, year, month + 1, day, daydiff, letterYear, letterMonth+1, letterDate);
                    letterCountModel.saveLetter(userkey,key,lettercount);
                }else{
                    letterModel.saveLetter(userkey,key, letterEdit.getText().toString(), color, year, month + 1, day, daydiff, year, month+1, day);
                    letterCountModel.saveLetter(userkey,key,lettercount);
                }

                Toast.makeText(getActivity(),"편지가 전송되었습니다",Toast.LENGTH_LONG).show();
                setAlarm();//이거때문에 그냥 바로 보내지는건가??? 아니면 뭐가 문제인거지
            }
        });

        calendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calenderclick=1;
                create();
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }


    //저장 Custom dialog생성
    private void create() {
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.datepicker, null);

        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setView(dialogView);
        ad.setPositiveButton("추가", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                DatePicker datepicker = (DatePicker) dialogView.findViewById(R.id.date);
                letterYear = datepicker.getYear();
                letterMonth = datepicker.getMonth();
                letterDate = datepicker.getDayOfMonth();

                Day2=new GregorianCalendar(letterYear,letterMonth,letterDate);


                Displaycalendar.setText(letterYear+"년 "+(letterMonth+1)+"월 "+letterDate+"일");

                diffdays=Day2.getTimeInMillis()-Day1.getTimeInMillis();

                if(diffdays<0){
                    diffdays=diffdays*-1;
                    resDaydiff=Long.toString(diffdays/(1000*60*60*24));
                    daydiff= Integer.parseInt(resDaydiff);
                    Log.e("diff",resDaydiff);

                }else{
                    resDaydiff=Long.toString(diffdays/(1000*60*60*24));
                    daydiff= Integer.parseInt(resDaydiff);
                    Log.e("diff2",resDaydiff);

                }


            }
        });
        ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        ad.show();
    }


    //알람의 설정
    private void setAlarm() {
        Random();

            Intent intent = new Intent(getActivity(), BroadcastD.class);//알람이 발생했을 경우, BradcastD에게 방송을 해주기 위해서 명시적으로 알려줍니다.
            PendingIntent sender = PendingIntent.getBroadcast(getActivity(), id, intent, 0);
            mManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + diffdays, sender);//알람 예약

    }

    public void Random() {

        Random r = new Random(); //객체생성
        id = r.nextInt(1000) + 1;
        System.out.println("랜덤으로 뽑힌 숫자는 " + id + "입니다."); //a에 저장된 값을 화면에 출력
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_blue) {
            letterEdit.setBackgroundResource(R.drawable.blueletter);
            color = "blue";
        } else if (view.getId() == R.id.btn_green) {
            letterEdit.setBackgroundResource(R.drawable.greenletter);
            color = "green";
        } else if (view.getId() == R.id.btn_yellow) {
            letterEdit.setBackgroundResource(R.drawable.yellowletter);
            color = "yellow";
        } else if (view.getId() == R.id.btn_purple) {
            letterEdit.setBackgroundResource(R.drawable.purpleletter);
            color = "purple";
        } else if (view.getId() == R.id.btn_red) {
            letterEdit.setBackgroundResource(R.drawable.redletter);
            color = "red";
        } else if (view.getId() == R.id.btn_orange) {
            letterEdit.setBackgroundResource(R.drawable.orangeletter);
            color = "orange";
        } else {
            letterEdit.setBackgroundResource(R.drawable.basicletter);
            color = "basic";
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }




}
