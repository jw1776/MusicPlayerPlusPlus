package group1.musicplayer;

import android.content.ContentUris;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

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

            ArrayList<Album> albumArray = MainActivity.getAlbumArray();

                    Bitmap albumArt = null;

            for (int i = 0; i < albumArray.size(); i++) {
                if(albumArray.get(i).getId() == newSong.getAlbumId()) {
                    albumArt = albumArray.get(i).getCoverArt();
                }
            }

            Album newAlbum = new Album(newSong.getAlbum(), newSong.getAlbumId(), newSong.getArtist(), albumArt);
            newAlbum.addSong(newSong);
            artistAlbums.add(newAlbum);
        }



    }

    public int getAlbumCount(){
        return artistAlbums.size();
    }

    public int getSongCount(){
        return artistSongs.size();
    }
}
