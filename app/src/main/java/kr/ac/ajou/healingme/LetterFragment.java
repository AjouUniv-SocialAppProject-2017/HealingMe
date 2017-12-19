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
import android.content.Intent;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
    private  long d1,d2;
    private long days;
    private int Calenderclick;
    private List<Integer> daydifflist=new ArrayList<Integer>();
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
        mCalendar = new GregorianCalendar();
        calendarBtn=(Button)rootView.findViewById(R.id.btn_calendar);
        calendar = Calendar.getInstance();
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
                    letterModel.saveLetter(userkey,key, letterEdit.getText().toString(), color, year, month + 1, day, daydiff, letterYear, letterMonth, letterDate);
                    letterCountModel.saveLetter(userkey,key,lettercount);
                }else{
                    letterModel.saveLetter(userkey,key, letterEdit.getText().toString(), color, year, month + 1, day, daydiff, year, month+1, day);
                    letterCountModel.saveLetter(userkey,key,lettercount);
                }
                Toast.makeText(getContext(),"편지가 전송되었습니다",Toast.LENGTH_LONG).show();
                setAlarm();//이거때문에 그냥 바로 보내지는건가??? 아니면 뭐가 문제인거지
            }
        });

        calendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calenderclick=1;
                DialogFragment dialogfragment = new DatePickerDialogTheme1();
                dialogfragment.show(getActivity().getFragmentManager(), "Theme 1");
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }
    @SuppressLint("ValidFragment")
    public class DatePickerDialogTheme1 extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();

            Day1 = Calendar.getInstance();

            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);

            Day1.set(year, month + 1, day);
            d1 = Day1.getTimeInMillis();

            DatePickerDialog datepickerdialog = new DatePickerDialog(getActivity(),
                    AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, this, year, month, day);

            return datepickerdialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            Day2 = Calendar.getInstance();

            Displaycalendar.setText(year + "년 " + (month + 1) + "월 " + day + "일");
            letterYear = year;
            letterMonth = month + 1;
            letterDate = day;


            Day2.set(letterYear, letterMonth, letterDate);
            d2 = Day2.getTimeInMillis();
            daydiff=(int)(d2-d1)/(1000*60*60*24);
            days = d2 - d1;

            daydifflist.add(daydiff);

        }
    }

    //알람의 설정
    private void setAlarm() {
        Random();
        for(int i=0;i<daydifflist.size();i++) {
            Intent notificationIntent = new Intent(getActivity(), BroadcastD.class);
            notificationIntent.putExtra("notificationId", daydifflist.get(i)); //전달할 값
            getDaydiff = daydifflist.get(i);
        }
            Intent intent = new Intent(getActivity(), BroadcastD.class);//알람이 발생했을 경우, BradcastD에게 방송을 해주기 위해서 명시적으로 알려줍니다.
            PendingIntent sender = PendingIntent.getBroadcast(getActivity(), getDaydiff, intent, 0);
            mManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + (d2 - d1), sender);//알람 예약

    }

    public void Random() {

        Random r = new Random(); //객체생성
        id = r.nextInt(100) + 1;
        /*nextInt(9) = 0~99까지 100개의 숫자중 랜덤으로 하나를 뽑아
         변수 a에 넣는다는 의미로 1~100의 숫자에서 하나를 뽑기위해 추출된 값에서 +1을 해준다*/

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
