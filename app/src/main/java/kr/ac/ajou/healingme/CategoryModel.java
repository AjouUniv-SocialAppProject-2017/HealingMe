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
 * Created by janghanna on 2017. 11. 7..
 */

class Category {
    final String category;
    final String imageURL;

    public Category() {
        this("", "");
    }

    public Category(String category) {
        this(category, "");
    }

    public Category(String category, String imageURL) {
        this.category = category;
        this.imageURL = imageURL;
    }

    public static Category newCategory(String category) {
        return new Category(category);
    }

    public static Category newCategoryWithImage(String category, String imageUrl) {
        return new Category(category, imageUrl);
    }
}

interface OnCategoryChangedListener {
    void onDataChanged(List<Category> categories);
}

public class CategoryModel {

    private DatabaseReference categoryRef;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private StorageReference storageReference;
    private FirebaseUser user;
    private String userKey;

    private List<Category> categories = new ArrayList<>();
    public List<String> categorykeys = new ArrayList<>();

    public CategoryModel() {
        user = auth.getCurrentUser();
        userKey = user.getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        categoryRef = database.getReference("categories");

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReferenceFromUrl("gs://social-app-programming.appspot.com").child("categoryImages");
    }

    public void setOnCategoryChangedListener(final OnCategoryChangedListener listener) {
        categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                categories = new ArrayList<>();

                Iterable<DataSnapshot> groups = dataSnapshot.getChildren();
                for (DataSnapshot e : groups) {
                    Category category = e.getValue(Category.class);
                    categories.add(category);
                    //TODO.삭제.5
                    categorykeys.add(e.getKey());
                }

                if (listener != null) {
                    listener.onDataChanged(categories);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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

    public void saveCategory(String category) {
        categoryRef.push().setValue(Category.newCategory(category));
    }

    public void saveCategoryWithImage(String category, String imageUrl) {
        categoryRef.push().setValue(Category.newCategoryWithImage(category, imageUrl));
    }

    public String getCategory(int position) {
        return categories.get(position).category;
    }

    public String getImageURL(int position) {
        return categories.get(position).imageURL;
    }


}
