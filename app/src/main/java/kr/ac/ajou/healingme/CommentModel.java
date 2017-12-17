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
 * Created by janghanna on 2017. 12. 10..
 */
class Comment {
//    final String category;
//    final String postingKey;
    final String commentId;
    final String userId;
    final String content;
    final String timestamp;

    public Comment() {
        this("","", "", "");
    }

    public Comment(String commentId,String userId, String content, String timestamp) {
//        this.category = category;
//        this.postingKey = postingKey;
        this.commentId = commentId;
        this.userId = userId;
        this.content = content;
        this.timestamp = timestamp;
    }

    private static String timestamp() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.KOREA);
        return dateFormat.format(date);
    }

    public static Comment newComment(String commentId, String userId, String content) {
        return new Comment(commentId, userId,content, timestamp());
    }

    public String getUserId() {
        return userId;
    }

    public String getContent() {
        return content;
    }

    public String getTimestamp() {
        return timestamp;
    }
}

interface OnCommentChangedListener {
    void onDataChanged(List<Comment> comments);
}

public class CommentModel {
    private DatabaseReference commentRef;
    private List<Comment> comments = new ArrayList<>();
    private OnCommentChangedListener onCommentChangedListener;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user;
    private String userKey;

    private UserModel umodel = new UserModel();

    public void setOnCommentChangedListener(OnCommentChangedListener listener) {
        this.onCommentChangedListener = listener;
    }

    public CommentModel(String postingKey) {
        user = auth.getCurrentUser();
        userKey = user.getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        this.commentRef = database.getReference("comments");

        this.commentRef.child(postingKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Comment> newComments = new ArrayList<Comment>();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                for (DataSnapshot e : children) {
                    Comment comment = e.getValue(Comment.class);
                    newComments.add(comment);
                }
                comments = newComments;

                if(onCommentChangedListener != null) {
                    onCommentChangedListener.onDataChanged(comments);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println(databaseError.getMessage());
            }
        });

    }

    public void writeComment(String postingKey, String content) {
        DatabaseReference childRef = commentRef.child(postingKey).push();
        childRef.setValue(Comment.newComment(childRef.getKey(), umodel.getUserId(), content));
    }

    public void deleteComment(String postingKey, String commentId) {
        commentRef.child(postingKey).child(commentId).removeValue();
    }

    public String getId(int position) {
        return comments.get(position).commentId;
    }

    public String getContent(int position) {
        return comments.get(position).content;
    }

    public String getUserId(int position) {
        return comments.get(position).userId;
    }

    public String getTimestamp(int position) {
        return comments.get(position).timestamp;
    }

}
