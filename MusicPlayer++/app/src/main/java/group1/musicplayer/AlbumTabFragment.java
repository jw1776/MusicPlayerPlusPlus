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

public class AlbumTabFragment extends Fragment {

    private static Context context;
    private static ArrayList<Album> albumArray;
    private static ArrayList<Song> currentSongList;
    private static ArrayList<Song> contextArray;
    private static String currentAlbum;
    private static ListView albumView;
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
        albumArray = MainActivity.getAlbumArray();
        contextArray = new ArrayList<Song>();
        View rootView = inflater.inflate(R.layout.album_tab_layout, container, false);

        albumView = (ListView) rootView.findViewById(R.id.album_list); //get a reference to the ListView created in album_tab_layout.xml

        backButton = (Button) rootView.findViewById(R.id.back_button_albumTab);
        header = (TextView) rootView.findViewById(R.id.header_albumTab);
        headerLayout = (LinearLayout) rootView.findViewById(R.id.header_layout_albumTab);

        switch(mode){
            case 0: //Show albums
                //hide back button and header
                if(headerLayout.getVisibility() != View.GONE){
                    headerLayout.setVisibility(View.GONE);
                }

                AlbumAdapter_AlbumTab theAdapter = new AlbumAdapter_AlbumTab(context, albumArray);
                albumView.setAdapter(theAdapter);
                break;
            case 1: //Show songs
                //show header and back button
                if(headerLayout.getVisibility() != View.VISIBLE){
                    headerLayout.setVisibility(View.VISIBLE);
                }

                header.setText(currentAlbum);

                SongAdapter_AlbumTab songAdapter = new SongAdapter_AlbumTab(context, currentSongList);
                albumView.setAdapter(songAdapter);
                break;

            default:
                break;
        }

        return rootView;
    }

    public static void backButtonPressed(){

        // Go back to album list
        if(headerLayout.getVisibility() != View.GONE){
            headerLayout.setVisibility(View.GONE);
        }

        AlbumAdapter_AlbumTab theAdapter = new AlbumAdapter_AlbumTab(context, albumArray);
        albumView.setAdapter(theAdapter);

        mode--;
        if(mode < 0){
            mode = 0;
        }

    }

    public static void showAlbumSongs(int index) {
        mode = 1;
        if(headerLayout.getVisibility() != View.VISIBLE){
            headerLayout.setVisibility(View.VISIBLE);
        }

        currentAlbum = albumArray.get(index).getTitle();
        currentSongList = albumArray.get(index).getSongs();
        header.setText(currentAlbum);

        SongAdapter_AlbumTab songAdapter = new SongAdapter_AlbumTab(context, currentSongList);
        albumView.setAdapter(songAdapter);
    }

    public static void updateContextArray(){ //called whenever a song is clicked in this fragment
        contextArray = currentSongList;
    }

    public static ArrayList<Song> getContextArray(){
        return contextArray;
    }
}
