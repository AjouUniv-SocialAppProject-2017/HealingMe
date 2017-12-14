package kr.ac.ajou.healingme;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by janghanna on 2017. 12. 13..
 */
class Like {
    final String postingId;
    boolean like;

    public Like() {
        this("", false);
    }

    public Like(String postingId, boolean like) {
        this.postingId = postingId;
        this.like = like;
    }

    public static Like newLike(String posingId, boolean like) {
        return new Like(posingId, like);
    }

    public void setLike(boolean like) {
        this.like = like;
    }
}

interface OnLikeChangedListener {
    void onLikeChanged(boolean like);
}

public class LikeModel {
    private DatabaseReference likeRef;
    private OnLikeChangedListener onLikeChangedListener;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user;
    private String userKey;
    private String postingKey;
    private boolean like;


    public void setOnLikeChangedListener(OnLikeChangedListener listener, String postingKey) {
        this.onLikeChangedListener = listener;
        this.postingKey = postingKey;
    }

    public LikeModel(final String postingKey) {
        user = auth.getCurrentUser();
        userKey = user.getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        likeRef = database.getReference("likes");

        likeRef.child(userKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (like) {
                    if (dataSnapshot.child(userKey).hasChild(postingKey)) {
                        likeRef.child(userKey).child(postingKey).removeValue();
                        like = true;
                    }
                } else {
                    System.out.println("AAAA"+postingKey);
                    likeRef.child(userKey).child(postingKey).setValue(Like.newLike(postingKey, like));
                    like = false;
                }


                if (onLikeChangedListener != null) {
                    onLikeChangedListener.onLikeChanged(like);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void doLike(String postingKey, boolean like) {
        if (like) {
            likeRef.child(userKey).child(postingKey).setValue(Like.newLike(postingKey, like));
        } else {
            System.out.println("삭제삭제");
            likeRef.child(userKey).child(postingKey).removeValue();
        }
    }


}
