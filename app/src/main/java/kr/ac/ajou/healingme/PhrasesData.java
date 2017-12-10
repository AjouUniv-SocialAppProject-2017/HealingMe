package kr.ac.ajou.healingme;

/**
 * Created by Son on 2017-11-01.
 */

public class PhrasesData {

    private String context;
    private String author;


    public PhrasesData(){}

    public PhrasesData(String context, String author) {

        this.context = context;
        this.author = author;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getAurthor() {
        return author;
    }

    public void setAurthor(String author) {
        this.author = author;
    }



}
