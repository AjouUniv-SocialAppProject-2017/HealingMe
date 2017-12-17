package kr.ac.ajou.healingme;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by janghanna on 2017. 12. 10..
 */

public class PostingListFragment extends Fragment {

    private String categoryName;

    private PostingModel model;
    private UserModel userModel;
    private List<Posting> postings = new ArrayList<>();

    private RecyclerView postingRecyclerView;
    private FloatingActionButton addPostingButton;

    private DatabaseReference likeRef;
    private boolean like = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_posting, container, false);

        categoryName = getArguments().getString("category");
        ((MainActivity) getActivity()).setActionBarTitle(categoryName);
        System.out.println(categoryName);

        model = new PostingModel(categoryName);
        userModel = new UserModel();
        likeRef = FirebaseDatabase.getInstance().getReference("likes");

        postingRecyclerView = (RecyclerView) rootView.findViewById(R.id.posting_recyclerview);
        addPostingButton = (FloatingActionButton) rootView.findViewById(R.id.add_posting_button);
        addPostingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddPostingFragment addPostingFragment = new AddPostingFragment();
                Bundle bundle = new Bundle();
                bundle.putString("category", categoryName);
                addPostingFragment.setArguments(bundle);

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragTransaction = fragmentManager.beginTransaction();
                fragTransaction.replace(R.id.container_body, addPostingFragment);
                fragTransaction.addToBackStack(null);
                fragTransaction.commit();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        postingRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        postingRecyclerView.setLayoutManager(layoutManager);

        postingRecyclerView.setAdapter(new RecyclerView.Adapter<PostingHolder>() {
            @Override
            public PostingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                View view = layoutInflater.inflate(R.layout.recycler_item_posting, parent, false);
                return new PostingHolder(view);
            }

            @Override
            public void onBindViewHolder(PostingHolder holder, final int position) {
                String title = postings.get(position).getTitle();
                String timestamp = postings.get(position).getTimestamp();
                String imageUrl = postings.get(position).getImageURL();
                holder.setText(title, timestamp);
                holder.setImage(imageUrl);

                holder.setLike(postings.get(position).getId());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PostingInfoFragment postingInfoFragment = new PostingInfoFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("posting", postings.get(position));
                        postingInfoFragment.setArguments(bundle);

                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragTransaction = fragmentManager.beginTransaction();
                        fragTransaction.replace(R.id.container_body, postingInfoFragment);
                        fragTransaction.addToBackStack(null);
                        fragTransaction.commit();
                    }
                });

                holder.postingCheckButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        like = true;

                        likeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (like) {
                                    if (dataSnapshot.child(userModel.getUserKey()).hasChild(model.getPostingId(position))) {
                                        likeRef.child(userModel.getUserKey()).child(model.getPostingId(position)).removeValue();
                                        like = false;

                                    } else {
                                        likeRef.child(userModel.getUserKey()).child(model.getPostingId(position)).setValue("like");
                                        like = false;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });
            }

            @Override
            public int getItemCount() {
                return postings.size();
            }
        });

        model.setOnPostingChangedListener(new OnPostingChangedListener() {
            @Override
            public void onDataChanged(List<Posting> newpostings) {
                postings = newpostings;
                postingRecyclerView.getAdapter().notifyDataSetChanged();
            }
        });

        return rootView;
    }


    private class PostingHolder extends RecyclerView.ViewHolder {
        private TextView postingTitleView;
        private TextView postingDateView;
        private CheckBox postingCheckButton;
        private ImageView postingImageView;

        private DatabaseReference likeRef;
        private FirebaseAuth auth;

        public PostingHolder(View itemView) {
            super(itemView);

            postingTitleView = itemView.findViewById(R.id.posting_title);
            postingDateView = itemView.findViewById(R.id.posting_day);
            postingCheckButton = itemView.findViewById(R.id.posting_check_button);
            postingImageView = itemView.findViewById(R.id.posting_image);

            likeRef = FirebaseDatabase.getInstance().getReference("likes");
            auth = FirebaseAuth.getInstance();
        }

        public void setText(String title, String timestamp) {
            postingTitleView.setText(title);
            postingDateView.setText(timestamp);
        }

        public void setLike(final String postingKey) {
            likeRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(auth.getCurrentUser().getUid()).hasChild(postingKey)) {
                        postingCheckButton.setChecked(true);
                    } else {
                        postingCheckButton.setChecked(false);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void setImage(String imageUrl) {
            int visibility = imageUrl.isEmpty() ? View.GONE : View.VISIBLE;
            postingImageView.setVisibility(visibility);

            Glide.with(PostingListFragment.this)
                    .load(imageUrl)
                    .into(postingImageView);
        }
    }

}
