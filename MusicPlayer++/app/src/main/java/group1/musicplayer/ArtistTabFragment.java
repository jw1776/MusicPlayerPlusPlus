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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ArtistTabFragment extends Fragment {

    private Context context;
    private ArrayList<Song> songList;
    private List<String> artistList;
    private ListView artistView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.artist_tab_layout, container, false);

        songList = MainActivity.getSongArray();
        artistList = new ArrayList<>();

        //add all artists, including duplicates, to artistList
        for(int i = 0; i < songList.size(); i++){
            artistList.add(songList.get(i).getArtist());
        }

        //add the artists to a hash set, which will not allow duplicates
        Set<String> hs = new HashSet<>();
        hs.addAll(artistList);
        //clear artistList and fill it with the contents of the hash set
        artistList.clear();
        artistList.addAll(hs);

        // remove null strings
        for(int i=0; i<artistList.size(); i++){
            if(artistList.get(i) == null){
                artistList.remove(i);
            }
        }

        Collections.sort(artistList, String.CASE_INSENSITIVE_ORDER); // sort alphabetically, ignoring case

        artistView = (ListView) rootView.findViewById(R.id.artist_list); //get a reference to the ListView created in song_tab_layout
        ArtistAdapter theAdapter = new ArtistAdapter(context,(ArrayList<String>) artistList);
        artistView.setAdapter(theAdapter); //pass the ListView object the appropriate adapter

        return rootView;
    }
}
