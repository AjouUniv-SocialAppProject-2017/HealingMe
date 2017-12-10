package kr.ac.ajou.healingme;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by janghanna on 2017. 11. 7..
 */

class Posting {
    String title;
    final String userId;
    String timestamp;
    String content;

    public Posting() {
        this("", "", "", "");
    }

    public Posting (String userId, String title, String content, String timestamp) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
    }

    private static String timestamp() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.KOREA);
        return dateFormat.format(date);
    }

    public static Posting newPosting(String userId, String title, String content) {
        return new Posting(userId, title, content, timestamp());
    }
}

interface OnPostingChangedListener {
    void onDataChanged(List<Posting> postings);
}

public class PostingModel {
    private DatabaseReference postingRef;
    private List<Posting> postings = new ArrayList<>();
    private OnPostingChangedListener onPostingChangedListener;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user;
    private String userKey;

    private UserModel umodel = new UserModel();

    public void setOnPostingChangedListener(OnPostingChangedListener listener) {
        this.onPostingChangedListener = listener;
    }

    public PostingModel(String category) {
        user = auth.getCurrentUser();
        userKey = user.getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        this.postingRef = database.getReference("postings");
        this.postingRef.child(category).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Posting> newPostings = new ArrayList<Posting>();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                for (DataSnapshot e : children) {
                    Posting posting = e.getValue(Posting.class);
                    newPostings.add(posting);
                }
                postings = newPostings;

                if(onPostingChangedListener != null) {
                    onPostingChangedListener.onDataChanged(postings);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println(databaseError.getMessage());
            }
        });

    }


    public void writePostings(String category, String title, String content) {
        DatabaseReference childRef = postingRef.child(category).push();
        childRef.setValue(Posting.newPosting(umodel.getUserId(), title, content));
    }


    public String getTitle(int position) {
        return postings.get(position).title;
    }

    public String getContent(int position) {
        return postings.get(position).content;
    }

    public String getUserId(int position) {
        return postings.get(position).userId;
    }

    public String getTimestamp(int position) {
        return postings.get(position).timestamp;
    }


}