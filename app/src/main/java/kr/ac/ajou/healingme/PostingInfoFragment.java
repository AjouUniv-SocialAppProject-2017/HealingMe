package kr.ac.ajou.healingme;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by janghanna on 2017. 12. 10..
 */

public class PostingInfoFragment extends Fragment {
    private TextView user;
    private TextView date;
    private TextView content;
    private RecyclerView commentRecyclerView;
    private EditText commentEdit;
    private Button commentButton;
    private ConstraintLayout layout;

    private Posting posting;
    private String categoryName;

    private CommentModel model;
    private UserModel userModel;
    private List<Comment> comments = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_posting_info, container, false);

        posting = (Posting) getArguments().getSerializable("posting");
        ((MainActivity) getActivity()).setActionBarTitle(posting.getTitle());
        categoryName = posting.getCategory();
        model = new CommentModel(posting.getId());
        userModel = new UserModel();

        user = (TextView) rootView.findViewById(R.id.posting_user_text);
        date = (TextView) rootView.findViewById(R.id.posting_date_text);
        content = rootView.findViewById(R.id.posting_info_content_text);
        layout = rootView.findViewById(R.id.posting_info_layout);

        user.setText(posting.getUserId());
        date.setText(posting.getTimestamp());
        content.setText(posting.getContent());

        commentEdit = rootView.findViewById(R.id.comment_edit);

        commentButton = rootView.findViewById(R.id.comment_button);
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                model.writeComment(posting.getId(), commentEdit.getText().toString());
                commentEdit.setText("");
            }
        });

        commentRecyclerView = rootView.findViewById(R.id.comment_recyclerview);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        commentRecyclerView.addItemDecoration(new DividerItemDecoration(rootView.getContext(), LinearLayoutManager.VERTICAL));
        commentRecyclerView.setLayoutManager(layoutManager);
        commentRecyclerView.setAdapter(new RecyclerView.Adapter<CommentHolder>() {
            @Override
            public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                View view = layoutInflater.inflate(R.layout.recycler_item_comment, parent, false);
                return new CommentHolder(view);
            }

            @Override
            public void onBindViewHolder(CommentHolder holder, final int position) {
                String user = model.getUserId(position);
                String content = model.getContent(position);
                String timestamp = model.getTimestamp(position);
                holder.setText(user, content, timestamp);

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        AlertDialog.Builder ad = new AlertDialog.Builder(layout.getContext());
                        ad.setTitle("댓글을 삭제하시겠습니까?");
                        ad.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (model.getUserId(position).equals(userModel.getUserId())) {
                                    model.deleteComment(posting.getId(), model.getId(position));
                                } else {
                                    Toast.makeText(rootView.getContext(), "다른 사용자의 댓글은 삭제할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        ad.show();

                        return true;
                    }
                });

            }

            @Override
            public int getItemCount() {
                return comments.size();
            }
        });


        model.setOnCommentChangedListener(new OnCommentChangedListener() {
            @Override
            public void onDataChanged(List<Comment> newcomments) {
                comments = newcomments;
                commentRecyclerView.getAdapter().notifyDataSetChanged();
                int count = commentRecyclerView.getAdapter().getItemCount();
                commentRecyclerView.scrollToPosition(count - 1);
            }
        });

        return rootView;

    }

    class CommentHolder extends RecyclerView.ViewHolder {
        private TextView userIdText;
        private TextView contentText;
        private TextView dateText;

        public CommentHolder(View itemView) {
            super(itemView);

            userIdText = itemView.findViewById(R.id.comment_user_text);
            contentText = itemView.findViewById(R.id.comment_content_text);
            dateText = itemView.findViewById(R.id.comment_date_text);
        }

        public void setText(String userId, String content, String timestamp) {
            userIdText.setText(userId);
            contentText.setText(content);
            dateText.setText(timestamp);
        }
    }

}
