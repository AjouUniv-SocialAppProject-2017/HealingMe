package kr.ac.ajou.healingme;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class MusicFragment extends Fragment {

    public static ArrayList<MusicData> musicDatas=new ArrayList<>();//어디서든~~~~
    private TextView music_title,music_singer,music_album;
    private ImageView music_image;
    private Bitmap bitmap;
    private Button btn_random;
    private int r;
    private Random rr = new Random();
    private MusicData randomMusic;
    private String title,singer,album;

    public MusicFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_music_fragment, container, false);
        music_title=(TextView)view.findViewById(R.id.music_title);
        music_singer=(TextView)view.findViewById(R.id.music_singer);
        music_album=(TextView)view.findViewById(R.id.music_album);
        music_image=(ImageView)view.findViewById(R.id.music_image);
        btn_random=(Button)view.findViewById(R.id.btn_randomMusic);

        int filelist=R.raw.music;

        try {
            InputStream in = getResources().openRawResource(filelist);
            if (in != null) {
                InputStreamReader stream = new InputStreamReader(in, "euc-kr");//읽어오기
                BufferedReader buffer = new BufferedReader(stream);
                String read;
                while ((read = buffer.readLine()) != null) {//한줄씩 읽늕다
                    String[] musicdataArr = read.split(",");
                    MusicData musicData = new MusicData(musicdataArr[0], musicdataArr[1], musicdataArr[2],musicdataArr[3]);
                    musicDatas.add(musicData);
                }
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Random();
        btn_random.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random();

            }
        });

        return view;
    }

    private void Random(){
        r = rr.nextInt(musicDatas.size()-1);

        randomMusic = musicDatas.get(r);
        title=randomMusic.getTitle();
        singer=randomMusic.getSinger();
        album=randomMusic.getAlbum();

        music_title.setText(title);
        music_singer.setText(singer);
        music_album.setText(album);

        Thread mThread = new Thread(){
            public void run(){
                try{
                    URL url= new URL(randomMusic.getImage());
                    HttpURLConnection connection=(HttpURLConnection)url.openConnection();
                    connection.setDoInput(true);//서버로 부터 응답 수신
                    connection.connect();

                    InputStream inputStream=connection.getInputStream();
                    bitmap= BitmapFactory.decodeStream(inputStream);

                }catch (MalformedURLException e){
                    e.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        };
        mThread.start();

        try {
            mThread.join();
            music_image.setImageBitmap(bitmap);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
