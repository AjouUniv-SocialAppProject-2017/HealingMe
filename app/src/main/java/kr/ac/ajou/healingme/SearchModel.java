package kr.ac.ajou.healingme;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by janghanna on 2017. 12. 13..
 */
interface OnSearchListener {
    void onLoad(List<Posting> postings);
}

public class SearchModel {
    private DatabaseReference searchRef;
    private List<Posting> postings = new ArrayList<>();

    public SearchModel() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        searchRef = database.getReference("postings");

    }

    public void setOnSearchListener(final OnSearchListener listener, String category, final String word) {
        searchRef.child(category).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postings = new ArrayList<>();
                Iterable<DataSnapshot> searchList = dataSnapshot.getChildren();
                for (DataSnapshot e : searchList) {
                    Posting posting = e.getValue(Posting.class);
                    System.out.println(posting.getContent());

                    if (posting.getContent().contains(word) || posting.getTitle().contains(word)) {
                        System.out.println("포함됨!");
                        postings.add(posting);
                    }

                    if (listener != null) {
                        listener.onLoad(postings);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println(databaseError.getMessage());
            }
        });
    }

    public String getTitle(int position) {
        return postings.get(position).title;
    }
    public String getTimestamp(int position) {
        return postings.get(position).timestamp;
    }



}
