package kr.ac.ajou.healingme;

import android.graphics.drawable.Drawable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Son on 2017-11-29.
 */

class LettersData {

    private String message, key;
    private String color;
    private int year, month, date,daydiff;
    private Drawable icon;

    public LettersData() {
    }


    public LettersData(String key, String message, String color, int year, int month, int date,int daydiff) {
        this.key = key;
        this.message = message;
        this.color = color;
        this.year = year;
        this.month = month;
        this.date = date;
        this.daydiff=daydiff;
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
}


interface OnLetterChangedListener {
    void onDataChanged(List<LettersData> letters);
}


public class LetterModel {
    private DatabaseReference letterRef;
    private List<LettersData> lettersDataList = new ArrayList<>();
    private OnLetterChangedListener onLetterChangedListener;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static int daydiffLetterModel;
 /* private List<LettersData> lettersData=new ArrayList<>();
    private FirebaseDatabase database=FirebaseDatabase.getInstance();
    private DatabaseReference reference=database.getReference("message");
    public static OnLetterChangedListener onLetterChangedListener;*/

    private String userKey;

    public void setOnLetterChangedListener(final OnLetterChangedListener listener) {
        this.onLetterChangedListener = listener;
    }

    public LetterModel() {

        letterRef=database.getReference();
        letterRef.child("message").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lettersDataList = new ArrayList<>();

                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot e : children) {
                    LettersData lettersData = e.getValue(LettersData.class);
                    lettersDataList.add(lettersData);
                    daydiffLetterModel=lettersData.getDaydiff();
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



    public void saveLetter(String key,String message, String color, int year, int month, int date ,int daydiff){

        DatabaseReference databaseReference=letterRef.child("message").push();
        key=databaseReference.getKey();
        databaseReference.setValue(new LettersData(key,message,color,year,month,date,daydiff));
    }

    public void deleteLetter(String key){
        letterRef.child("message").child(key).removeValue();
    }



}
