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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by janghanna on 2017. 12. 10..
 */

public class PostingListFragment extends Fragment {

    private String categoryName;

    private PostingModel model;
    private List<Posting> postings = new ArrayList<>();

    private RecyclerView postingRecyclerView;
    private FloatingActionButton addPostingButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_posting, container, false);

        categoryName = getArguments().getString("category");

//        TextView actionbarText = (TextView) rootView.findViewById(R.id.custom_actionbar_text);
//        actionbarText.setText(categoryName);
        model = new PostingModel(categoryName);

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
                String title = model.getTitle(position);
                String timestamp = model.getTimestamp(position);
                holder.setText(title, timestamp);

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

    class PostingHolder extends RecyclerView.ViewHolder {
        private TextView chatTitleView;
        private TextView chatDateView;
        private CheckBox postingCheckButton;

        public PostingHolder(View itemView) {
            super(itemView);

            chatTitleView = itemView.findViewById(R.id.posting_title);
            chatDateView = itemView.findViewById(R.id.posting_day);

        }

        public void setText(String title, String timestamp) {
            chatTitleView.setText(title);
            chatDateView.setText(timestamp);
        }
    }

}
