package kr.ac.ajou.healingme;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by janghanna on 2017. 11. 28..
 */

public class ChatListFragment extends Fragment {
    private ChatGroupModel model = new ChatGroupModel();
    private List<String> groupNames = new ArrayList<>();

    private RecyclerView groupRecyclerView;
    private FloatingActionButton addFolderButton;

    private DatabaseReference userRef;

    private String userList = "";
    private int userNum;
    private List<String> users = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_chat_list, container, false);

        userRef = FirebaseDatabase.getInstance().getReference("chats");

        groupRecyclerView = (RecyclerView) rootView.findViewById(R.id.chat_group_recyclerview);
        addFolderButton = (FloatingActionButton) rootView.findViewById(R.id.add_chat_button);
        addFolderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText title = new EditText(rootView.getContext());
                AlertDialog.Builder ad = new AlertDialog.Builder(rootView.getContext());
                ad.setTitle("채팅방 이름을 입력하세요");
                ad.setView(title); //추가 버튼 클릭시 폴더이름 적는 dialog 생성
                ad.setPositiveButton("추가", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (title.getText().length() > 0) {
                            model.saveGroup(title.getText().toString());
                        }
                        else {
                            Toast.makeText(((AppCompatActivity)getActivity()).getApplicationContext(), "채팅방 이름이 입력되지 않았습니다", Toast.LENGTH_LONG).show();
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
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(((AppCompatActivity)getActivity()).getBaseContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        groupRecyclerView.addItemDecoration(new DividerItemDecoration(rootView.getContext(), LinearLayoutManager.VERTICAL));
        groupRecyclerView.setLayoutManager(layoutManager);

        groupRecyclerView.setAdapter(new RecyclerView.Adapter<ChatGroupHolder>() {
            @Override
            public ChatGroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                View view = layoutInflater.inflate(R.layout.recycler_item_chat_list, parent, false);
                return new ChatGroupHolder(view);
            }

            @Override
            public void onBindViewHolder(final ChatGroupHolder holder, final int position) {
                holder.setGroupName(model.getGroup(position));
                holder.setUsers(model.getGroup(position));

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ChatFragment chatFragment = new ChatFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("chat_group", holder.getGroupName());
                        chatFragment.setArguments(bundle);

                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragTransaction = fragmentManager.beginTransaction();
                        fragTransaction.replace(R.id.container_body, chatFragment);
                        fragTransaction.addToBackStack(null);
                        fragTransaction.commit();

                    }
                });


            }

            @Override
            public int getItemCount() {
                return groupNames.size();
            }
        });

        model.setOnGroupChangedListener(new OnGroupChangedListener() {
            @Override
            public void onDataChanged(List<String> groups) {
                groupNames = groups;
                groupRecyclerView.getAdapter().notifyDataSetChanged();
            }
        });

        return rootView;


    }

    class ChatGroupHolder extends RecyclerView.ViewHolder {
        private TextView chat_group;
        private TextView chat_users;
        private TextView chat_userNum;

        public ChatGroupHolder(View itemView) {
            super(itemView);

            chat_group = (TextView) itemView.findViewById(R.id.chat_group_name);
            chat_users = itemView.findViewById(R.id.chat_group_people);
            chat_userNum = itemView.findViewById(R.id.chat_people_num);
        }

        public void setGroupName(String name) {
            chat_group.setText(name);
        }

        public String getGroupName() {
            return chat_group.getText().toString();
        }

        public void setUsers(final String chatGroup) {

            userRef.child(chatGroup).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<String> newUsers = new ArrayList<>();
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                    for (DataSnapshot e : children) {
                        Chat user = e.getValue(Chat.class);
                        if (!users.contains(user.userId)) {
                            users.add(user.userId);

                            if (users.size()==1) {
                                userList += user.userId;
                            } else {
                                userList += ("/" + user.userId);
                            }
//                            userList += (" " + user.userId);

                        }
                        userNum = users.size();

                        chat_users.setText(userList + " 참여중..");
                        chat_userNum.setText(String.valueOf(userNum));
                    }
                    users = newUsers;
                    userList = "";
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    }

}

