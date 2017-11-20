package kr.ac.ajou.healingme;

/**
 * Created by Son on 2017-10-02.
 */

public class LetterData {
    private String userName;
    private String message;
    private int year;
    private int month;
    private int date;
    private String color;
    private long diffDay;
    private int id;



    public LetterData(){}


    public LetterData(String userName, String message, int year, int month, int date, String color, long diffDay, int id) {
        this.userName = userName;
        this.message = message;
        this.year = year;
        this.month = month;
        this.date = date;
        this.color = color;
        this.diffDay = diffDay;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getDiffDay() {
        return diffDay;
    }

    public void setDiffDay(int diffDay) {
        this.diffDay = diffDay;
    }



    public String getColor() {
        return color;
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

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public void setColor(String color) {
        this.color = color;
    }





    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLetter() {
        return message;
    }

    public void setLetter(String letter) {
        this.message = letter;
    }


}
