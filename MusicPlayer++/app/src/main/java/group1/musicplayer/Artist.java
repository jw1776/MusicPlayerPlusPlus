package group1.musicplayer;

import java.util.ArrayList;

/**
 * Created by Jack on 11/22/2015.
 */
public class Artist {
    private String title;
    private ArrayList<Album> artistAlbums;
    private ArrayList<Song> artistSongs;

    public void Artist(String artistTitle) {
        title = artistTitle;
        artistAlbums = new ArrayList<Album>();
        artistSongs = new ArrayList<Song>();
    }

    public String getTitle() {
        return title;
    }

    public void addAlbum(Album newAlbum) {
        artistAlbums.add(newAlbum);
    }

    public void addSong(Song newSong) {
        artistSongs.add(newSong);
    }
}
