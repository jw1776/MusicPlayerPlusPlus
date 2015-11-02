package group1.musicplayer;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Jack on 11/1/2015.
 */
public class Playlist implements Serializable {

    private String title;
    private int _id;
    private ArrayList<Song> playlistSongs;

    public Playlist(String new_title, ArrayList<Song> new_playlistSongs) {
        title = new_title;
        playlistSongs = new_playlistSongs;
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<Song> getPlaylistSongs() {
        return playlistSongs;
    }

    public int getId() {
        return _id;
    }

    public void setId(int new_id) {
        _id = new_id;
    }

    public int getSize() {
        return playlistSongs.size();
    }
}
