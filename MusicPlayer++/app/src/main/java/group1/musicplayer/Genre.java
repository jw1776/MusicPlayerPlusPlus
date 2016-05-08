package group1.musicplayer;

import java.util.ArrayList;

/**
 * Created by Jack on 5/7/2016.
 */
public class Genre {

    private String genre;
    private ArrayList<Song> genreSongs;

    public Genre(String inputGenre) {
        genre = inputGenre;
        genreSongs = new ArrayList<Song>();
    }

    public void addSong(Song inputSong) {
        genreSongs.add(inputSong);
    }

    public String getGenre() {
        return genre;
    }

    public ArrayList<Song> getSongs() {
        return genreSongs;
    }

}
