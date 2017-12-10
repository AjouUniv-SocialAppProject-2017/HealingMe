package kr.ac.ajou.healingme;

/**
 * Created by Son on 2017-11-01.
 */

public class BookData {
    private String image;
    private String title;
    private String author;
    private String publisher;

    public BookData(){}

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public BookData(String image, String title, String author, String publisher) {
        this.image = image;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
    }
}
