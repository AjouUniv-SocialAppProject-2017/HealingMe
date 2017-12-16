package kr.ac.ajou.healingme;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

class User {
    String name;
    String id;

    public User() {}

    public User(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
}

interface OnUserChangedListener {
    void onDataChanged(List<User> users);
}

public class UserModel {
    private String email;
    private String userId;

    public void setEmail(String email) {
        this.email = email;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    private static class Singleton {
        private static final UserModel INSTANCE = new UserModel();
    }

    public static UserModel getInstance() {
        return Singleton.INSTANCE;
    }

    public void login(String email, String password, final OnLoginCompleteListener listener) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                listener.onSuccess();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onFail();
            }
        });
    }

    private DatabaseReference userRef;
    private List<User> users = new ArrayList<>();
    private OnUserChangedListener onUserChangedListener;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user;
    private String userKey;

    public UserModel() {
        user = auth.getCurrentUser();
        userKey = user.getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        userRef = database.getReference("users");

        userRef.child(userKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users = new ArrayList<>();

                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot e : children) {
                    User user = e.getValue(User.class);
                    users.add(user);
                    setUserId(user.id);
                }
                if (onUserChangedListener != null) {
                    onUserChangedListener.onDataChanged(users);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void saveUser(String name, String id) {
        DatabaseReference ref = userRef.child(userKey).push();
        ref.setValue(new User(name, id));
    }

    public String getUserId() {
        return userId;
    }

    public String getUserKey() {
        return userKey;
    }
}
