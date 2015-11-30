package group1.musicplayer;

import java.util.ArrayList;

/**
 * Created by Jack on 11/22/2015.
 */
public class Artist {
    private String title;
    private ArrayList<Album> artistAlbums;
    private ArrayList<Song> artistSongs;

    public Artist(String artistTitle) {
        title = artistTitle;
        artistAlbums = new ArrayList<Album>();
        artistSongs = new ArrayList<Song>();
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<Album> getAlbums(){
        return artistAlbums;
    }

    public ArrayList<Song> getSongs(){
        return artistSongs;
    }

    public void addAlbum(Album newAlbum) {
        artistAlbums.add(newAlbum);
    }

    public void addSong(Song newSong) {
        boolean songFound = false;
        boolean albumFound = false;
        long thisID = newSong.getID();
        long thisAlbumID = newSong.getAlbumId();

        for(int i = 0; i < artistSongs.size(); i++){
            if (artistSongs.get(i).getID() == thisID){  //if the song already exists in the artistSongs array, do nothing
                songFound = true;
                break;
            }
        }// end for

        if(!songFound){     //if the song was not found, add it
            artistSongs.add(newSong);
        }

        for(int i = 0; i < artistAlbums.size(); i++){
            if(artistAlbums.get(i).getId() == thisAlbumID){     //if the album already exists
                albumFound = true;
                if(!songFound){
                    artistAlbums.get(i).addSong(newSong);
                }

                break;
            }
        }

        if(!albumFound){    //if the album does not already exist
            Album newAlbum = new Album(newSong.getAlbum(), newSong.getAlbumId(), newSong.getArtist());
            newAlbum.addSong(newSong);
            artistAlbums.add(newAlbum);
        }

    }
}
