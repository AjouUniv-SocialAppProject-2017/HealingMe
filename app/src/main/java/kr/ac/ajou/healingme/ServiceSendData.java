package kr.ac.ajou.healingme;

/**
 * Created by Son on 2017-11-09.
 */

public class ServiceSendData {
    private String context;
    private String userName;


    private String title;
    private int year,month,day;
    public ServiceSendData(){};



    public ServiceSendData(String userName, String title,String context, int year, int month, int day) {

        this.userName = userName;
        this.title=title;
        this.context = context;
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
