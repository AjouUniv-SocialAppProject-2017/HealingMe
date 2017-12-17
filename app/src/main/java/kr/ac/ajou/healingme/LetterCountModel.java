package kr.ac.ajou.healingme;

import android.icu.util.Calendar;
import android.util.Log;
import android.widget.Toast;

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
 * Created by Son on 2017-12-14.
 */

class LetterCountData{

    private int lettercount;
    private String key;
    private String userkey;

    public LetterCountData(){}

    public LetterCountData(String userkey,String key,int lettercount) {
        this.userkey=userkey;
        this.key=key;
        this.lettercount = lettercount;
    }

    public int getLettercount() {
        return lettercount;
    }

    public void setLettercount(int lettercount) {
        this.lettercount = lettercount;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUserkey() {
        return userkey;
    }

    public void setUserkey(String userkey) {
        this.userkey = userkey;
    }
}
interface OnLetterCountChangedListener {
    void onDataChanged(List<LetterCountData> letterCountData);
}


public class LetterCountModel {

        private List<LetterCountData> letterCountDataList = new ArrayList<>();
        private OnLetterCountChangedListener onLetterCountChangedListener;

        private DatabaseReference letterCountRef;
        private FirebaseDatabase database = FirebaseDatabase.getInstance();

        private FirebaseAuth auth = FirebaseAuth.getInstance();
        private FirebaseUser user;
    private String userKey;


    public void setOnLetterCountChangedListener(final OnLetterCountChangedListener listener) {
        this.onLetterCountChangedListener = listener;
    }

    public LetterCountModel() {
        user = auth.getCurrentUser();
        userKey = user.getUid();

        letterCountRef=database.getReference("messageCount");
        letterCountRef.child(userKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                letterCountDataList = new ArrayList<>();

                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot e : children) {
                    LetterCountData lettersCountData = e.getValue(LetterCountData.class);
                    letterCountDataList.add(lettersCountData);
                }


                //데이터 날짜 후에 수정하는거??여기서??
                if (onLetterCountChangedListener != null) {
                    onLetterCountChangedListener.onDataChanged(letterCountDataList);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    public void saveLetter(String userKey2,String key,int letterCount){
        user = auth.getCurrentUser();
        userKey = user.getUid();

        DatabaseReference databaseReference=letterCountRef.child(userKey).push();
        key=databaseReference.getKey();
        databaseReference.setValue(new LetterCountData(userKey,key,letterCount));

    }





}
