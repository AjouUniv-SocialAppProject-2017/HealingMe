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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by janghanna on 2017. 10. 29..
 */

class Chat {
    final String message;
    final String timestamp;
    final String imageURL;
    final String userId;

    public Chat() {
        this("", "", "", "");
    }

    public Chat(String userId, String message, String timestamp) {
        this(userId, message, timestamp, "");
    }

    public Chat(String userId, String message, String timestamp, String imageURL) {
        this.userId = userId;
        this.message = message;
        this.timestamp = timestamp;
        this.imageURL = imageURL;
    }

    public static Chat newChat(String userId, String message) {
        return new Chat(userId, message, timestamp());
    }

    public static Chat newChatWithImage(String userId, String message, String imageUrl) {
        return new Chat(userId, message, timestamp(), imageUrl);
    }

    private static String timestamp() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("a h:mm", Locale.KOREA);
        return dateFormat.format(date);
    }

}

public class ChatModel {
    private DatabaseReference chatRef;
    private List<Chat> chats = new ArrayList<>();
    private List<String> users = new ArrayList<>();
    private OnDataChangedListener onDataChangedListener;

    private StorageReference storageReference;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user;
    private String userKey;

    private UserModel umodel = new UserModel();

    public void setOnDataChangedListener(OnDataChangedListener listener) {
        this.onDataChangedListener = listener;
    }

    public ChatModel(String group) {
        user = auth.getCurrentUser();
        userKey = user.getUid();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReferenceFromUrl("gs://social-app-programming.appspot.com").child("chatImages");


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        this.chatRef = database.getReference("chats");
        this.chatRef.child(group).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Chat> newChats = new ArrayList<Chat>();

                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                for (DataSnapshot e : children) {
                    Chat chat = e.getValue(Chat.class);
                    newChats.add(chat);

                }

                chats = newChats;

                if (onDataChangedListener != null) {
                    onDataChangedListener.onDataChanged();
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


    public void sendMessage(String group, String message) {
        DatabaseReference childRef = chatRef.child(group).push();
        childRef.setValue(Chat.newChat(umodel.getUserId(), message));
    }

    public void sendMessageWithImage(String group, String message, String imageUrl) {
        DatabaseReference childRef = chatRef.child(group).push();
        childRef.setValue(Chat.newChatWithImage(umodel.getUserId(), message, imageUrl));
    }

    public String getMessage(int position) {
        return chats.get(position).message;
    }

    public String getImageURL(int position) {
        return chats.get(position).imageURL;
    }

    public String getTimestamp(int position) {
        return chats.get(position).timestamp;
    }

    public int getMessageCount() {
        return chats.size();
    }

    public String getUserId(int position) {
        return chats.get(position).userId;
    }

//    public int getUsersNum() {
//        return users.size();
//    }
//
//    public String getUserList() {
//        String userList = "";
//        for (int i = 0; i < users.size(); i++) {
//            userList.concat(users.get(i) + " ");
//        }
//        return userList;
//    }


}
