package kr.ac.ajou.healingme;

import android.graphics.drawable.Drawable;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Son on 2017-11-29.
 */

class LettersData {

    private String message, key;
    private String color;
    private int year, month, date,daydiff;
    private int letteryear,lettermonth,letterdate;
    private String userKey;
    private Drawable icon;

    public LettersData() {
    }


    public LettersData(String userKey,String key, String message, String color, int year, int month, int date,int daydiff,int letteryear,int lettermonth,int letterdate) {
        this.userKey=userKey;
        this.key = key;
        this.message = message;
        this.color = color;
        this.year = year;
        this.month = month;
        this.date = date;
        this.daydiff=daydiff;
        this.letteryear=letteryear;
        this.lettermonth=lettermonth;
        this.letterdate=letterdate;
    }

    public LettersData(String message, String color, int year, int month, int date, Drawable icon) {
        this.message = message;
        this.color = color;
        this.year = year;
        this.month = month;
        this.date = date;
        this.icon = icon;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }


    public int getDaydiff() {
        return daydiff;
    }

    public void setDaydiff(int daydiff) {
        this.daydiff = daydiff;
    }

    public int getLetteryear() {
        return letteryear;
    }

    public void setLetteryear(int letteryear) {
        this.letteryear = letteryear;
    }

    public int getLettermonth() {
        return lettermonth;
    }

    public void setLettermonth(int lettermonth) {
        this.lettermonth = lettermonth;
    }

    public int getLetterdate() {
        return letterdate;
    }

    public void setLetterdate(int letterdate) {
        this.letterdate = letterdate;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }
}


interface OnLetterChangedListener {
    void onDataChanged(List<LettersData> letters);
}


@RequiresApi(api = Build.VERSION_CODES.N)
public class LetterModel {
    private List<LettersData> lettersDataList = new ArrayList<>();
    private OnLetterChangedListener onLetterChangedListener;

    private DatabaseReference letterRef;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    private Calendar calendar = Calendar.getInstance();
    private int year,month,day;
    private String resDaydiff;
    private Calendar Day1;
    private Calendar Day2;
    private  long diffdays;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user;
    private String userKey;

    public void setOnLetterChangedListener(final OnLetterChangedListener listener) {
        this.onLetterChangedListener = listener;
    }

    public LetterModel() {
        user = auth.getCurrentUser();
        userKey = user.getUid();

        letterRef=database.getReference("message");
        letterRef.child(userKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lettersDataList = new ArrayList<>();

                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot e : children) {
                    LettersData lettersData = e.getValue(LettersData.class);
                    lettersDataList.add(lettersData);
                }

                        //데이터 날짜 후에 수정하는거??여기서??
                        if (onLetterChangedListener != null) {
                            onLetterChangedListener.onDataChanged(lettersDataList);
                        }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    public void saveLetter(String userKey2,String key,String message, String color, int year, int month, int date ,int daydiff,int letteryear,int lettermonth,int letterdate){
        user = auth.getCurrentUser();
        userKey = user.getUid();

        DatabaseReference databaseReference=letterRef.child(userKey).push();
        key=databaseReference.getKey();
        databaseReference.setValue(new LettersData(userKey,key,message,color,year,month,date,daydiff,letteryear,lettermonth,letterdate));
    }

    public void deleteLetter(String userKey2,String key){
        letterRef.child(userKey).child(key).removeValue();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updateLetter(String userKey2, String key, String message, String color, int years, int months, int date , int daydiff, int letteryear, int lettermonth, int letterdate) {


        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        Day1=new GregorianCalendar(year,month,day);
        Day2=new GregorianCalendar(letteryear,lettermonth-1,letterdate);
        Log.e("letterModelYEARE",letteryear+"");
        Log.e("letterModelMONTH",lettermonth+"");
        Log.e("letterModelDATE",letterdate+"");

        diffdays=Day2.getTimeInMillis()-Day1.getTimeInMillis();

        if(diffdays<0){
            resDaydiff=Long.toString(diffdays/(1000*60*60*24));
            daydiff= Integer.parseInt(resDaydiff);
        }else{
            resDaydiff=Long.toString(diffdays/(1000*60*60*24));
            daydiff= Integer.parseInt(resDaydiff);
        }

        letterRef.child(userKey).child(key).setValue(new LettersData(userKey2,key,message,color,years,months,date,daydiff,letteryear,lettermonth,letterdate));
    }

}
