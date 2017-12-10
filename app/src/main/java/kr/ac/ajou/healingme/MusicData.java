package kr.ac.ajou.healingme;

/**
 * Created by Son on 2017-12-07.
 */

public class MusicData {

    private String image;
    private String title;
    private String singer;
    private String album;

    public MusicData(){}

    public MusicData(String image, String title, String singer, String album) {
        this.image = image;
        this.title = title;
        this.singer = singer;
        this.album = album;
    }

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

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }
}
