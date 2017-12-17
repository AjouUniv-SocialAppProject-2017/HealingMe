package kr.ac.ajou.healingme;

import android.app.Activity;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class CustomServiceActivity extends Fragment implements TextWatcher {
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

    public CustomServiceActivity() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_custom_service, container, false);

        user = auth.getCurrentUser();
        userKey = user.getUid();

        txtTitleServiceCenter=(EditText)rootView.findViewById(R.id.customtitle);
        txtServiceCenter = (EditText) rootView.findViewById(R.id.edit_service);
        txt_length = (TextView) rootView.findViewById(R.id.review_length);

        btn_send = (Button) rootView.findViewById(R.id.btn_send);

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
                Toast.makeText(getActivity(), "보냈습니다", Toast.LENGTH_LONG).show();
            }
        });

        // Inflate the layout for this fragment
        return rootView;
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
            Toast.makeText(getContext(), "글은 150자 까지 입력 가능합니다.", Toast.LENGTH_SHORT).show();
        }
        txt_length.setText(s.length() + " / 150자");
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
