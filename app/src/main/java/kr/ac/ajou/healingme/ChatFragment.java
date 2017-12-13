package kr.ac.ajou.healingme;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.FileNotFoundException;
import java.io.InputStream;

import static android.app.Activity.RESULT_OK;

/**
 * Created by janghanna on 2017. 11. 28..
 */

public class ChatFragment extends Fragment {
    private static final int PICK_FROM_ALBUM = 100;

    String chatGroupName;

    private ChatModel cmodel;
    private UserModel umodel;
    private Uri currentImageUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_chat, container, false);

        chatGroupName = getArguments().getString("chat_group");
        cmodel = new ChatModel(chatGroupName);
        umodel = new UserModel();

        AlertDialog.Builder ad = new AlertDialog.Builder(rootView.getContext());
        ad.setTitle("주의");
        ad.setMessage("나쁜말 금지!");
        ad.show();

        final EditText chatEdit = (EditText) rootView.findViewById(R.id.chat_edit);

        Button chatButton = (Button) rootView.findViewById(R.id.chat_button);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String message = chatEdit.getText().toString();
                if (message.length() > 0) {
                    if (currentImageUri != null) {
                        try {
                            ContentResolver resolver = ((AppCompatActivity)getActivity()).getContentResolver();
                            InputStream is = resolver.openInputStream(currentImageUri);
                            currentImageUri = null;

                            cmodel.uploadImage(is, new OnUploadImageListener() {
                                @Override
                                public void onSuccess(String url) {
                                    cmodel.sendMessageWithImage(chatGroupName, message, url);
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
                        cmodel.sendMessage(chatGroupName, message);
                    }
                }
                chatEdit.setText("");
            }
        });

        Button photoButton = (Button) rootView.findViewById(R.id.photo_button);
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_FROM_ALBUM);
            }
        });

        final RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.chat_recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(((AppCompatActivity)getActivity()).getBaseContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(new RecyclerView.Adapter<ChatFragment.ChatHolder>() {
            @Override
            public ChatFragment.ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                View view = layoutInflater.inflate(R.layout.recycler_item_chat, parent, false);
                return new ChatFragment.ChatHolder(view);
            }

            @Override
            public void onBindViewHolder(ChatFragment.ChatHolder holder, int position) {
                String user = cmodel.getUserId(position);
                String message = cmodel.getMessage(position);
                String timestamp = cmodel.getTimestamp(position);

                if (umodel.getUserId().equals(cmodel.getUserId(position))) {
                    holder.setTextRight(user, message, timestamp);
                } else {
                    holder.setText(user, message, timestamp);
                }

                String imageUrl = cmodel.getImageURL(position);
                holder.setImage(imageUrl);


            }

            @Override
            public int getItemCount() {
                return cmodel.getMessageCount();
            }
        });

        cmodel.setOnDataChangedListener(new OnDataChangedListener() {
            @Override
            public void onDataChanged() {
                recyclerView.getAdapter().notifyDataSetChanged();
                int count = recyclerView.getAdapter().getItemCount();
                recyclerView.scrollToPosition(count - 1);
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

    class ChatHolder extends RecyclerView.ViewHolder {

        private TextView userView;
        private TextView textView;
        private ImageView imageView;
        private TextView timestampView;
        private LinearLayout layout;

        public ChatHolder(View itemView) {
            super(itemView);

            userView = itemView.findViewById(R.id.chat_user_id);
            textView = (TextView) itemView.findViewById(R.id.chat_text_view);
            imageView = (ImageView) itemView.findViewById(R.id.chat_image_view);
            timestampView = (TextView) itemView.findViewById(R.id.chat_timestamp_view);
            layout = itemView.findViewById(R.id.chat_item_layout);
        }

        public void setText(String user, String text, String timestamp) {
            layout.setGravity(Gravity.LEFT);

            userView.setText(user);
            textView.setText(text);
            textView.setBackground(getContext().getResources().getDrawable(R.drawable.bg_msg_from));
            timestampView.setText(timestamp);
        }

        public void setTextRight(String user, String text, String timestamp) {
            layout.setGravity(Gravity.RIGHT);

            userView.setText(user);
            textView.setText(text);
            textView.setBackground(getContext().getResources().getDrawable(R.drawable.bg_msg_to));
            timestampView.setText(timestamp);
        }

        // 공통성과 가변성의 분리 - 변하는 것과 변하지 않는 것은 분리되어야 한다.
        public void setImage(String imageUrl) {
            int visibility = imageUrl.isEmpty() ? View.GONE : View.VISIBLE;
            imageView.setVisibility(visibility);

            Glide.with(ChatFragment.this)
                    .load(imageUrl)
                    .into(imageView);
        }
    }

}

