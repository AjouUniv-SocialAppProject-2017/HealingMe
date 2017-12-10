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

/**
 * Created by Son on 2017-11-01.
 */


public class MovieFragment extends Fragment {

    public static ArrayList<MovieData> movieDatas=new ArrayList<>();//어디서든~~~~
    private TextView movie_title,movie_release,movie_rating;
    private ImageView movie_image;
    private Bitmap bitmap;
    private Button btn_random;
    private int r;
    private Random rr = new Random();
    private MovieData randomMovie;
    private String title,relase,rating;

    public MovieFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_movie, container, false);
        movie_title=(TextView)view.findViewById(R.id.movie_title);
        movie_rating=(TextView)view.findViewById(R.id.movie_rate);
        movie_release=(TextView)view.findViewById(R.id.movie_relase);
        movie_image=(ImageView)view.findViewById(R.id.movie_image);
        btn_random=(Button)view.findViewById(R.id.btn_randomMovie);

        int filelist=R.raw.movie;


    try {
            InputStream in = getResources().openRawResource(filelist);
            if (in != null) {
                InputStreamReader stream = new InputStreamReader(in, "euc-kr");//읽어오기
                BufferedReader buffer = new BufferedReader(stream);

                String read;
                while ((read = buffer.readLine()) != null) {//한줄씩 읽늕다
                    String[] moviedataArr = read.split(",");
                    MovieData movieData = new MovieData(moviedataArr[0], moviedataArr[1], moviedataArr[2],moviedataArr[3]);
                    movieDatas.add(movieData);
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
        r = rr.nextInt(movieDatas.size()-1);

        randomMovie = movieDatas.get(r);
        title=randomMovie.getTitle();
        relase=randomMovie.getReleaseYear();
        rating=randomMovie.getRating();


        movie_title.setText(title);
        movie_release.setText(relase);
        movie_rating.setText(rating);

        Thread mThread = new Thread(){
            public void run(){
                try{
                    URL url= new URL(randomMovie.getImage());
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
            movie_image.setImageBitmap(bitmap);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

}
