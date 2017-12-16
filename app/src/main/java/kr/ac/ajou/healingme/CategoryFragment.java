package kr.ac.ajou.healingme;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by janghanna on 2017. 12. 10..
 */

public class CategoryFragment extends Fragment {
    private CategoryModel model = new CategoryModel();
    private List<Category> categories = new ArrayList<>();

    private RecyclerView categoryRecyclerView;
    private FloatingActionButton addCategotyButton;

    private static final int PICK_FROM_ALBUM = 100;
    private Uri currentImageUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_category, container, false);

        categoryRecyclerView = (RecyclerView) rootView.findViewById(R.id.category_recyclerview);
        addCategotyButton = (FloatingActionButton) rootView.findViewById(R.id.add_category_button);
        addCategotyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText title = new EditText(rootView.getContext());
                AlertDialog.Builder ad = new AlertDialog.Builder(rootView.getContext());
                ad.setTitle("카테고리 이름을 입력하세요");
                ad.setView(title);
                ad.setPositiveButton("추가", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (title.getText().length() > 0) {
                            if (currentImageUri != null) {
                                try {
                                    ContentResolver resolver = getActivity().getContentResolver();
                                    InputStream is = resolver.openInputStream(currentImageUri);
                                    currentImageUri = null;

                                    model.uploadImage(is, new OnUploadImageListener() {
                                        @Override
                                        public void onSuccess(String url) {
                                            model.saveCategoryWithImage(title.getText().toString(), url);
                                        }

                                        @Override
                                        public void onFail() {
                                            //...
                                        }
                                    });

                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }

                            } else {
                                model.saveCategory(title.getText().toString());
                            }
                        }

                    }
                });
                ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                ad.setNeutralButton("이미지", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, PICK_FROM_ALBUM);
                    }
                });
                ad.show();
            }
        });

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity().getBaseContext(), 2);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        categoryRecyclerView.setLayoutManager(layoutManager);

        categoryRecyclerView.setAdapter(new RecyclerView.Adapter<CategoryHolder>() {

            @Override
            public CategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                View view = layoutInflater.inflate(R.layout.recycler_item_category, parent, false);
                return new CategoryHolder(view);
            }

            @Override
            public void onBindViewHolder(final CategoryHolder holder, int position) {
                holder.setCategoryName(model.getCategory(position));
                holder.setImage(model.getImageURL(position));

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PostingListFragment postingListFragment = new PostingListFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("category", holder.getCategoryName());
                        postingListFragment.setArguments(bundle);

                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragTransaction = fragmentManager.beginTransaction();
                        fragTransaction.replace(R.id.container_body, postingListFragment);
                        fragTransaction.addToBackStack(null);
                        fragTransaction.commit();
                    }
                });
            }

            @Override
            public int getItemCount() {
                return categories.size();
            }
        });

        model.setOnCategoryChangedListener(new OnCategoryChangedListener() {
            @Override
            public void onDataChanged(List<Category> newcategories) {
                categories = newcategories;
                categoryRecyclerView.getAdapter().notifyDataSetChanged();
            }
        });

        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK)
            return;

        if (requestCode == PICK_FROM_ALBUM) {
            currentImageUri = data.getData();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_posting) {
            Intent intent = new Intent(getActivity().getBaseContext(), SearchActivity.class);
            System.out.println("eeeeeeee"+categories.size());
            intent.putParcelableArrayListExtra("categorylist", (ArrayList<? extends Parcelable>) categories);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    class CategoryHolder extends RecyclerView.ViewHolder {
        private TextView categoryName;
        private ImageView categoryImage;

        public CategoryHolder(View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.category_name);
            categoryImage = itemView.findViewById(R.id.category_image);
        }

        public String getCategoryName() {
            return categoryName.getText().toString();
        }

        public void setCategoryName(String name) {
            categoryName.setText(name);
        }

        public void setImage(String imageUrl) {
            Glide.with(CategoryFragment.this)
                    .load(imageUrl)
                    .into(categoryImage);
        }
    }

}

