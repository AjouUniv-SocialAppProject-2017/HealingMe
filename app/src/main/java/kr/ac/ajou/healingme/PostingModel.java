package kr.ac.ajou.healingme;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by janghanna on 2017. 11. 7..
 */

class Posting implements Serializable {
    String id;
    String category;
    String title;
    final String userId;
    String timestamp;
    String content;
    String imageURL;

    public Posting() {
        this("", "", "", "", "", "", "");
    }

    public Posting(String id, String category, String userId, String title, String content, String timestamp) {
        this.id = id;
        this.category = category;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
    }

    public Posting(String id, String category, String userId, String title, String content, String timestamp, String imageURL) {
        this.id = id;
        this.category = category;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.imageURL = imageURL;
    }

    private static String timestamp() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.KOREA);
        return dateFormat.format(date);
    }

    public static Posting newPosting(String id, String category, String userId, String title, String content) {
        return new Posting(id, category, userId, title, content, timestamp());
    }

    public static Posting newPostingWithImage(String id, String category, String userId, String title, String content, String imageURL) {
        return new Posting(id, category, userId, title, content, timestamp(), imageURL);
    }

    public String getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getUserId() {
        return userId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getContent() {
        return content;
    }

    public String getImageURL() {
        return imageURL;
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
    private StorageReference storageReference;
    private FirebaseUser user;
    private String userKey;

    private UserModel umodel = new UserModel();

    public void setOnPostingChangedListener(OnPostingChangedListener listener) {
        this.onPostingChangedListener = listener;
    }

    public PostingModel(String category) {
        user = auth.getCurrentUser();
        userKey = user.getUid();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReferenceFromUrl("gs://social-app-programming.appspot.com").child("postingImages");

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

                System.out.println(postings.size());

                if (onPostingChangedListener != null) {
                    onPostingChangedListener.onDataChanged(postings);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println(databaseError.getMessage());
            }
        });

    }

    private String generateTempFilename() {
        return new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
    }

    public void uploadImage(InputStream is, final OnUploadImageListener listener) {
        UploadTask task = storageReference.child(generateTempFilename()).putStream(is);
        task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String imageUrl = taskSnapshot.getDownloadUrl().toString();
                listener.onSuccess(imageUrl);
            }
        });
    }

    public void writePostings(String category, String title, String content) {
        DatabaseReference childRef = postingRef.child(category).push();
        childRef.setValue(Posting.newPosting(childRef.getKey(), category, umodel.getUserId(), title, content));
    }

    public void writePostingsWithImage(String category, String title, String content, String imageUrl) {
        DatabaseReference childRef = postingRef.child(category).push();
        childRef.setValue(Posting.newPostingWithImage(childRef.getKey(), category, umodel.getUserId(), title, content, imageUrl));
    }

    public String getTitle(int position) {
        System.out.println("Aaaa"+postings.get(position).title);
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

    public String getPostingId(int position) {
        return postings.get(position).id;
    }

    public String getImageURL(int position) {
        return postings.get(position).imageURL;
    }

}
