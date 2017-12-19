package kr.ac.ajou.healingme;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@RequiresApi(api = Build.VERSION_CODES.N)
public class GetLetterActivity extends AppCompatActivity {

    private LetterModel letterModel;

    public static RecyclerView recyclerView;
    private View rootView;
    private List<LettersData> lettersDataList = new ArrayList<>();

    private OnLetterChangedListener onLetterChangedListener;
    private Calendar calendar = Calendar.getInstance();
    private int year,month,day;
    private int daydiff;
    private AlarmManager mManager;
    public static int id;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_letter);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("나만의 우체통");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        mManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        Intent intent = getIntent();

        recyclerView = (RecyclerView) findViewById(R.id.recycleview);

        letterModel = new LetterModel();

        //recyclerview
            LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
            recyclerView.setLayoutManager(layoutManager);

                recyclerView.setAdapter(new RecyclerView.Adapter<LetterHolder>() {
                    @Override
                    public LetterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                        View view = layoutInflater.inflate(R.layout.letter_listview_custom, parent, false);
                        return new LetterHolder(view);
                    }

                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onBindViewHolder(LetterHolder holder, final int position) {
                        Log.e("lettersDataList", lettersDataList.get(position).getDaydiff() + "");
                        letterModel.updateLetter(lettersDataList.get(position).getUserKey(),lettersDataList.get(position).getKey(),lettersDataList.get(position).getMessage(), lettersDataList.get(position).getColor(), year, month + 1, day, daydiff, lettersDataList.get(position).getLetteryear(), lettersDataList.get(position).getLettermonth(), lettersDataList.get(position).getLetterdate());

                     if(lettersDataList.get(position).getDaydiff() <= 0) {
                           holder.setText(lettersDataList.get(position));

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(GetLetterActivity.this, GetLetterDetailActivity.class);
                                    String msg = lettersDataList.get(position).getMessage();
                                    String userkey=lettersDataList.get(position).getUserKey();
                                    String key = lettersDataList.get(position).getKey();
                                    int year = lettersDataList.get(position).getYear();
                                    int month = lettersDataList.get(position).getMonth();
                                    int date = lettersDataList.get(position).getDate();

                                    String color = lettersDataList.get(position).getColor();

                                    intent.putExtra("msg", msg);
                                    intent.putExtra("userkey",userkey);
                                    intent.putExtra("key", key);
                                    intent.putExtra("color", color);
                                    intent.putExtra("year", year);
                                    intent.putExtra("month", month);
                                    intent.putExtra("date", date);

                                    Log.e("userkey",userkey);
                                    startActivity(intent);

                                }
                            });
                        }else{
                            holder.Text(lettersDataList.get(position));
                        }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home: {
                onBackPressed();

                return true;
            }

        }
        return super.onOptionsItemSelected(item);
    }

}


@RequiresApi(api = Build.VERSION_CODES.N)
class LetterHolder extends RecyclerView.ViewHolder {
    public static ImageView letter_image;
    private TextView letter_name;
    private TextView letter_date;
    private int year,month,day;
    private Calendar calendar = Calendar.getInstance();
    public static int change;


    @RequiresApi(api = Build.VERSION_CODES.N)
    public LetterHolder(View itemView) {
        super(itemView);

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        letter_image = (ImageView) itemView.findViewById(R.id.iv_img);
        letter_name = (TextView) itemView.findViewById(R.id.nameTxt);
        letter_date = (TextView) itemView.findViewById(R.id.dateTxt);
    }

    public void setText(LettersData lettersData) {
        letter_image.setImageResource(R.drawable.postbox);
        letter_name.setText(lettersData.getMessage());
        letter_date.setText(lettersData.getLetteryear() + "/" + lettersData.getLettermonth() + "/" + lettersData.getLetterdate());
    }

    public void Text(LettersData lettersData){
        letter_image.setImageResource(R.drawable.closemailbox);
        letter_name.setText("편지가 도착하지 않았습니다");
        letter_date.setText("D-"+lettersData.getDaydiff());
    }



}



