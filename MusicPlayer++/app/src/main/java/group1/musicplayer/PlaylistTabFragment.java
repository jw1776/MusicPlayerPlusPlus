package group1.musicplayer;

/**
 * Created by LukeJr on 10/18/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Fragment;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class PlaylistTabFragment extends Fragment {

    private static Context context;
    private static ListView playlistView;
    private static ArrayList<Playlist> playlistsArray = new ArrayList<Playlist>();
    private static ArrayList<Song> currentSongList;
    private static ArrayList<Song> contextArray;
    private static String currentPlaylist;
    private static Button backButton;
    private static Button addPlaylistButton;
    private static TextView header;
    private static LinearLayout headerLayout; //contains both the header and the back button
    private static int mode = 0;  //0 when viewing playlists, 1 when viewing songs

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //playlistsArray = new ArrayList<Playlist>();
        contextArray = new ArrayList<Song>();
        DBHandler db = new DBHandler(context, null, null, 1);
        if (db.databaseExists(context, "mpp_data.db")) {    //check if the database exists
            playlistsArray = db.pullPlaylists();

        }

        View rootView = inflater.inflate(R.layout.playlist_tab_layout, container, false);
        playlistView = (ListView) rootView.findViewById(R.id.playlist_ListView); //get a reference to the ListView created in playlist_tab_layout.xml

        addPlaylistButton = (Button) rootView.findViewById(R.id.add_playlist_button);
        backButton = (Button) rootView.findViewById(R.id.playlist_back_button);
        header = (TextView) rootView.findViewById(R.id.playlist_header);
        headerLayout = (LinearLayout) rootView.findViewById(R.id.playlist_header_layout);

        switch(mode){
            case 0: //Show playlists
                //hide back button and header
                if(headerLayout.getVisibility() != View.GONE){
                    headerLayout.setVisibility(View.GONE);
                }
                if(addPlaylistButton.getVisibility() != View.VISIBLE){
                    addPlaylistButton.setVisibility(View.VISIBLE);
                }

                PlaylistAdapter theAdapter = new PlaylistAdapter(context, playlistsArray);
                playlistView.setAdapter(theAdapter); //pass the ListView object the appropriate adapter
                break;
            case 1: //Show songs
                //show header and back button
                if(headerLayout.getVisibility() != View.VISIBLE){
                    headerLayout.setVisibility(View.VISIBLE);
                }
                if(addPlaylistButton.getVisibility() != View.GONE){
                    addPlaylistButton.setVisibility(View.GONE);
                }
                header.setText(currentPlaylist);

                SongAdapter_PlaylistTab songAdapter = new SongAdapter_PlaylistTab(context, currentSongList);
                playlistView.setAdapter(songAdapter);
                break;

            default:
                break;
        }

        return rootView;
    }

    @Override
    public void onResume(){
        //Log.e("DEBUG", "onResume of PlaylistTabFragment");

        if(mode == 0){
            DBHandler db = new DBHandler(context, null, null, 1);
            if (db.databaseExists(context, "mpp_data.db")) {    //check if the database exists
                playlistsArray = db.pullPlaylists();
            }
            PlaylistAdapter theAdapter = new PlaylistAdapter(context, playlistsArray);
            playlistView.setAdapter(theAdapter); //pass the ListView object the appropriate adapter
        }

        super.onResume();
    }

    public static void backButtonPressed(){

            // Go back to playlists list
            if(headerLayout.getVisibility() != View.GONE){
                headerLayout.setVisibility(View.GONE);
            }
            if(addPlaylistButton.getVisibility() != View.VISIBLE){
            addPlaylistButton.setVisibility(View.VISIBLE);
            }

            PlaylistAdapter theAdapter = new PlaylistAdapter(context, playlistsArray);
            playlistView.setAdapter(theAdapter); //pass the ListView object the appropriate adapter

            mode--;
            if(mode < 0){
                mode = 0;
            }

    }

    public static void showPlaylistSongs(int index) {
        mode = 1;
        if(headerLayout.getVisibility() != View.VISIBLE){
            headerLayout.setVisibility(View.VISIBLE);
        }
        if(addPlaylistButton.getVisibility() != View.GONE){
            addPlaylistButton.setVisibility(View.GONE);
        }

        currentPlaylist = playlistsArray.get(index).getTitle();
        currentSongList = playlistsArray.get(index).getPlaylistSongs();
        header.setText(currentPlaylist);

        SongAdapter_PlaylistTab songAdapter = new SongAdapter_PlaylistTab(context, currentSongList);
        playlistView.setAdapter(songAdapter);
    }

    public static void updateContextArray(){ //called whenever a song is clicked in this fragment
        contextArray = currentSongList;
    }

    public static ArrayList<Song> getContextArray(){
        return contextArray;
    }
}
