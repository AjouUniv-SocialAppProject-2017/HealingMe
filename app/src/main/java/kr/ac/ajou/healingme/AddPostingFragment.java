package kr.ac.ajou.healingme;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.io.FileNotFoundException;
import java.io.InputStream;

import static android.app.Activity.RESULT_OK;

/**
 * Created by janghanna on 2017. 12. 10..
 */

public class AddPostingFragment extends Fragment {
    private static final int PICK_FROM_ALBUM = 100;
    private Uri currentImageUri;

    private String categoryName;

    private EditText title;
    private EditText content;

    private PostingModel postingModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_add_posting, container, false);
        categoryName = getArguments().getString("category");
        postingModel = new PostingModel(categoryName);
        title = (EditText) rootView.findViewById(R.id.posting_title_edit);
        content = (EditText) rootView.findViewById(R.id.posting_content_edit);

        Button postButton = (Button) rootView.findViewById(R.id.post_button);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (title.getText().toString().length() > 0 && content.getText().toString().length() > 0) {
                    if (currentImageUri != null) {
                        try {
                            ContentResolver resolver = getActivity().getContentResolver();
                            InputStream is = resolver.openInputStream(currentImageUri);
                            currentImageUri = null;

                            postingModel.uploadImage(is, new OnUploadImageListener() {
                                @Override
                                public void onSuccess(String url) {
                                    postingModel.writePostingsWithImage(categoryName, title.getText().toString(), content.getText().toString(), url);
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
                        postingModel.writePostings(categoryName, title.getText().toString(), content.getText().toString());
                    }
                }

                PostingListFragment postingListFragment = new PostingListFragment();
                Bundle bundle = new Bundle();
                bundle.putString("category", categoryName);
                postingListFragment.setArguments(bundle);

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragTransaction = fragmentManager.beginTransaction();
                fragTransaction.add(R.id.container_body, postingListFragment);
                fragTransaction.addToBackStack(null);
                fragTransaction.commit();
                
            }
        });

        Button imageButton = rootView.findViewById(R.id.posting_image_button);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_FROM_ALBUM);
            }
        });

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

}
