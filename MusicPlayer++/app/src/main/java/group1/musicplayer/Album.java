package group1.musicplayer;

import android.graphics.Bitmap;
import java.util.ArrayList;

/**
 * Created by Jack on 11/22/2015.
 */
public class Album {
    private String title;
    private long id;
    private String artist;
    private Bitmap coverArt;
    private ArrayList<Song> albumSongs;

    public Album (String albumTitle, long albumId, String albumArtist) {
        title = albumTitle;
        id = albumId;
        artist = albumArtist;
        albumSongs = new ArrayList<Song>();
    }

    public String getTitle() {
        return title;
    }

    public long getId(){
        return id;
    }

    public String getArtist() {
        return artist;
    }

    public Bitmap getCoverArt() {
        return coverArt;
    }

    public void setCoverArt(Bitmap art){
        coverArt = art;
    }

    public ArrayList<Song> getSongs() {
        return albumSongs;
    }

    public void addSong(Song newSong) {
        albumSongs.add(newSong);
    }
}
