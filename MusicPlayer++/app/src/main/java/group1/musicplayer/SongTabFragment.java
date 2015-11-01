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
import android.widget.ListView;

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

        return rootView;
    }

}