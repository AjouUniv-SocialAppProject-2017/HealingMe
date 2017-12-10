package kr.ac.ajou.healingme;

/**
 * Created by Son on 2017-10-30.
 */

public class MovieData {

    private String image;
    private String rating;
    private String releaseYear;
    private String title;

    public MovieData(){}
    public MovieData(String image, String rating, String releaseYear, String title) {
        this.image = image;
        this.rating = rating;
        this.releaseYear = releaseYear;
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(String releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
