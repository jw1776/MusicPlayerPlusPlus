package group1.musicplayer;

/**
 * Created by LukeJr on 10/18/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.app.Fragment;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class SongTabFragment extends Fragment {

    private ListView songView;
    private Context context;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.song_tab_layout, container, false);

        songView = (ListView) rootView.findViewById(R.id.song_list); //get a reference to the ListView created in song_tab_layout
        SongAdapter theAdapter = new SongAdapter(context, MainActivity.getSongArray());
        songView.setAdapter(theAdapter); //pass the ListView object the appropriate adapter

        registerForContextMenu(songView);

        return rootView;
    }

    public void updateAdapterArray(ArrayList<Song> songList) {
        ((SongAdapter) songView.getAdapter()).setSongs(songList);
    }

    @Override
    public void onResume(){
        Log.e("DEBUG", "onResume of LoginFragment");

        SongAdapter theAdapter = new SongAdapter(context, MainActivity.getSongArray());
        songView.setAdapter(theAdapter); //pass the ListView object the appropriate adapter

        super.onResume();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.editSong:
                //editSong(info.id); //
                return true;
            case R.id.deleteSong:
                //deleteSong(info.id);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

}