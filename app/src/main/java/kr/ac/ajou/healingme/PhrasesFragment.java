package kr.ac.ajou.healingme;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Son on 2017-11-01.
 */

public class PhrasesFragment extends Fragment {
    public static ArrayList<PhrasesData> phrasesDatas=new ArrayList<>();//어디서든~~~~
    private TextView phrases_context,phrases_author;
    private Button btn_random;
    private int r;
    private Random rr = new Random();
    private PhrasesData randomPhrases;
    private String context,author;

    public PhrasesFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_phrases, container, false);
        phrases_context=(TextView)view.findViewById(R.id.phrases_context);
        phrases_author=(TextView)view.findViewById(R.id.phrases_author);

        btn_random=(Button)view.findViewById(R.id.btn_randomPhrases);

        int filelist=R.raw.context;




//        for(int i=0;i<9;i++) {//splash ㄱㄱ
        try {

            InputStream in = getResources().openRawResource(filelist);
            if (in != null) {

                InputStreamReader stream = new InputStreamReader(in, "euc-kr");//읽어오기
                BufferedReader buffer = new BufferedReader(stream);

                String read;
                while ((read = buffer.readLine()) != null) {//한줄씩 읽늕다
                    String[] phrasesdataArr = read.split(",");
                    PhrasesData phrasesData = new PhrasesData(phrasesdataArr[0], phrasesdataArr[1]);
                    phrasesDatas.add(phrasesData);
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
        r = rr.nextInt(phrasesDatas.size()-1);

        randomPhrases = phrasesDatas.get(r);
        context=randomPhrases.getContext();
        author=randomPhrases.getAurthor();

        phrases_context.setText(context);
        phrases_author.setText(author);

    }
}
