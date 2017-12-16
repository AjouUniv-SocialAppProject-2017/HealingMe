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

public class BookFragment extends Fragment {

    public static ArrayList<BookData> bookDatas=new ArrayList<>();//어디서든~~~~
    private TextView book_title,book_publisher;
    private ImageView book_image;
    private Bitmap bitmap;
    private Button btn_random;
    private int r;
    private Random rr = new Random();
    private BookData randomBook;
    private String title,author,publisher;

    public BookFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_book, container, false);
        book_title=(TextView)view.findViewById(R.id.book_title);
        book_publisher=(TextView)view.findViewById(R.id.book_publisher);
        book_image=(ImageView)view.findViewById(R.id.book_image);
        btn_random=(Button)view.findViewById(R.id.btn_randomBook);

        int filelist=R.raw.book;

//        for(int i=0;i<9;i++) {//splash ㄱㄱ
        try {
            InputStream in = getResources().openRawResource(filelist);
            if (in != null) {
                InputStreamReader stream = new InputStreamReader(in, "euc-kr");//읽어오기
                BufferedReader buffer = new BufferedReader(stream);
                String read;
                while ((read = buffer.readLine()) != null) {//한줄씩 읽늕다
                    String[] bookdataArr = read.split(",");
                    BookData bookData = new BookData(bookdataArr[0], bookdataArr[1], bookdataArr[2],bookdataArr[3]);
                    bookDatas.add(bookData);
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
        r = rr.nextInt(bookDatas.size()-1);

        randomBook = bookDatas.get(r);
        title=randomBook.getTitle();
        author=randomBook.getAuthor();
        publisher=randomBook.getPublisher();

        book_title.setText(title+" ("+author+")");
        book_publisher.setText("출판사"+" - "+publisher);

        Thread mThread = new Thread(){
            public void run(){
                try{
                    URL url= new URL(randomBook.getImage());
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
            book_image.setImageBitmap(bitmap);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
