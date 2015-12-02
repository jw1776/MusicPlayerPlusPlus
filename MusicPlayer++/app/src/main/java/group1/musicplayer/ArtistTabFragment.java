package group1.musicplayer;

/**
 * Created by LukeJr on 10/18/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Fragment;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ArtistTabFragment extends Fragment {

    private static Context context;
    private static ArrayList<Artist> artistList;
    private static ArrayList<Album> currentAlbumList;
    private static ArrayList<Song> currentSongList;
    private static String currentArtist;
    private static String currentAlbum;
    private static ListView artistView;
    private static Button backButton;
    private static TextView header;
    private static LinearLayout headerLayout; //contains both the header and the back button
    private static int mode = 0;  //0 when viewing artists, 1 when viewing albums, 2 when viewing songs

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.artist_tab_layout, container, false);

        artistList = MainActivity.getArtistArray();
        artistView = (ListView) rootView.findViewById(R.id.artist_list); //get a reference to the ListView created in artist_tab_layout
        backButton = (Button) rootView.findViewById(R.id.back_button);
        header = (TextView) rootView.findViewById(R.id.header);
        headerLayout = (LinearLayout) rootView.findViewById(R.id.header_layout);

        switch(mode){
            case 0: //Show artists
                //hide back button and header
                if(headerLayout.getVisibility() != View.GONE){
                    headerLayout.setVisibility(View.GONE);
                }
                ArtistAdapter artistAdapter = new ArtistAdapter(context, artistList);
                artistView.setAdapter(artistAdapter); //pass the ListView object the appropriate adapter
                break;
            case 1: //Show albums
                //show header and back button
                if(headerLayout.getVisibility() != View.VISIBLE){
                    headerLayout.setVisibility(View.VISIBLE);
                }
                header.setText(currentArtist);
                AlbumAdapter albumAdapter = new AlbumAdapter(context, currentAlbumList);
                artistView.setAdapter(albumAdapter);
                break;
            case 2: //Show songs
                if(headerLayout.getVisibility() != View.VISIBLE){
                    headerLayout.setVisibility(View.VISIBLE);
                }
                header.setText(currentAlbum);
                SongAdapter_ArtistTab songAdapter = new SongAdapter_ArtistTab(context, currentSongList);
                artistView.setAdapter(songAdapter);
                break;
            default:
                break;
        }


        return rootView;
    }

    public static void showAlbums(ArrayList<Album> albumArrayList){
        mode = 1;
        if(headerLayout.getVisibility() != View.VISIBLE){
            headerLayout.setVisibility(View.VISIBLE);
        }
        currentAlbumList = albumArrayList;
        currentArtist = currentAlbumList.get(0).getArtist();
        header.setText(currentArtist);

        AlbumAdapter theAdapter = new AlbumAdapter(context, currentAlbumList);
        artistView.setAdapter(theAdapter);

    }

    public static void showSongs(int albumPosition){
        mode = 2;
        if(headerLayout.getVisibility() != View.VISIBLE){
            headerLayout.setVisibility(View.VISIBLE);
        }
        currentAlbum = currentAlbumList.get(albumPosition).getTitle();
        currentSongList = currentAlbumList.get(albumPosition).getSongs();
        header.setText(currentAlbum);

        SongAdapter_ArtistTab songAdapter = new SongAdapter_ArtistTab(context, currentSongList);
        artistView.setAdapter(songAdapter);
    }

    public static void backButtonPressed(){
        if(mode == 1){
            // Go back to artists list
            if(headerLayout.getVisibility() != View.GONE){
                headerLayout.setVisibility(View.GONE);
            }
            ArtistAdapter artistAdapter = new ArtistAdapter(context, artistList);
            artistView.setAdapter(artistAdapter); //pass the ListView object the appropriate adapter
        }else
        if(mode == 2){
            //Go back to albums list
            if(headerLayout.getVisibility() != View.VISIBLE){
                headerLayout.setVisibility(View.VISIBLE);
            }
            header.setText(currentArtist);
            AlbumAdapter albumAdapter = new AlbumAdapter(context, currentAlbumList);
            artistView.setAdapter(albumAdapter);
        }
        mode--;
        if(mode < 0){
            mode = 0;
        }

    }
}
