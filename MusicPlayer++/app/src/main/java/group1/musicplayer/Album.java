package group1.musicplayer;

import android.graphics.Bitmap;
import java.util.ArrayList;

/**
 * Created by Jack on 11/22/2015.
 */
public class Album {
    private String title;
    private String artist;
    private Bitmap coverArt;
    private ArrayList<Song> albumSongs;

    public void Album (String albumTitle, String albumArtist, Bitmap albumCoverArt) {
        title = albumTitle;
        artist = albumArtist;
        coverArt = albumCoverArt;
        albumSongs = new ArrayList<Song>();
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public Bitmap getCoverArt() {
        return coverArt;
    }

    public ArrayList<Song> getSongs() {
        return albumSongs;
    }

    public void addSong(Song newSong) {
        albumSongs.add(newSong);
    }
}
