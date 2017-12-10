package kr.ac.ajou.healingme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GetLetterActivity extends ActionBarActivity {

    private LetterModel letterModel;
    public static RecyclerView recyclerView;
    private Button backBtn;
    private View rootView;
    private List<LettersData> lettersDataList = new ArrayList<>();
    private int id;
    private Toolbar mToolbar;
    private OnLetterChangedListener onLetterChangedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_letter);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        String title= getString(R.string.title_getletter);
        getSupportActionBar().setTitle("");
        backBtn = (Button) findViewById(R.id.backbtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


        Intent intent = getIntent();
        if (intent != null) {
            // extract the extra-data in the Notification
            id = intent.getIntExtra("notificationId", 0);
            Log.e("들어갔니", id + "");
        }
        recyclerView = (RecyclerView) findViewById(R.id.recycleview);

        letterModel = new LetterModel();


        //recyclerview
            LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
            recyclerView.setLayoutManager(layoutManager);

            if (Objects.equals(id,LetterModel.daydiffLetterModel)) {
                Log.e("편지받아라",id+"");
                Log.e("편지받았니",LetterModel.daydiffLetterModel+")");
                recyclerView.setAdapter(new RecyclerView.Adapter<LetterHolder>() {
                    @Override
                    public LetterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                        View view = layoutInflater.inflate(R.layout.letter_listview_custom, parent, false);
                        return new LetterHolder(view);
                    }

                    @Override
                    public void onBindViewHolder(LetterHolder holder, final int position) {
                        Log.e("lettersDataList", lettersDataList.get(position) + "");
                        holder.setText(lettersDataList.get(position));
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(GetLetterActivity.this, GetLetterDetailActivity.class);

                                Bundle args = new Bundle();
                                String msg = lettersDataList.get(position).getMessage();
                                String key = lettersDataList.get(position).getKey();
                                int year = lettersDataList.get(position).getYear();
                                int month = lettersDataList.get(position).getMonth();
                                int date = lettersDataList.get(position).getDate();

                                String color = lettersDataList.get(position).getColor();

                  /*      args.putString("msg", msg);
                        args.putString("key",key);
                        args.putString("color",color);
                        args.putInt("year",year);
                        args.putInt("month",month);
                        args.putInt("date",date);*/
                                intent.putExtra("msg", msg);
                                intent.putExtra("key", key);
                                intent.putExtra("color", color);
                                intent.putExtra("year", year);
                                intent.putExtra("month", month);
                                intent.putExtra("date", date);
                                // intent.putExtras(args);

                                Log.e("msg", intent.toString());
                                startActivity(intent);

                            }
                        });
                    }

                    @Override
                    public int getItemCount() {
                        return lettersDataList.size();
                    }


                });

                letterModel.setOnLetterChangedListener(new OnLetterChangedListener() {
                    @Override
                    public void onDataChanged(List<LettersData> items) {
                        lettersDataList = items;
                        recyclerView.getAdapter().notifyDataSetChanged();
                    }
                });
            }
        }
    }

class LetterHolder extends RecyclerView.ViewHolder {
    private ImageView letter_image;
    private TextView letter_name;
    private TextView letter_date;

    public LetterHolder(View itemView) {
        super(itemView);
        letter_image = (ImageView) itemView.findViewById(R.id.iv_img);
        letter_name = (TextView) itemView.findViewById(R.id.nameTxt);
        letter_date = (TextView) itemView.findViewById(R.id.dateTxt);
    }

    public void setText(LettersData lettersData) {
        Log.e("letterMsg", lettersData.getMessage());
        letter_image.setImageResource(R.drawable.postbox);
        letter_name.setText(lettersData.getMessage());
        letter_date.setText(lettersData.getYear() + "/" + lettersData.getMonth() + "/" + lettersData.getDate());

    }
}

