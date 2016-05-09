package group1.musicplayer;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.RelativeLayout;

/**
 * Created by LukeJr on 9/26/2015.
 */
public class SongAdapter extends BaseAdapter {

    private ArrayList<Song> songArray;
    private LayoutInflater songInf;

    public SongAdapter(Context c, ArrayList<Song> grabbedSongArray){
        songArray = grabbedSongArray;
        songInf = LayoutInflater.from(c);
    }
    //Adapter code
    public void setSongs(ArrayList<Song> songList) {
        songArray = songList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return songArray.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RelativeLayout listLayout = (RelativeLayout)songInf.inflate(R.layout.song, parent, false);
        //layout for each individual song in the list. Uses song.xml
        TextView songView = (TextView)listLayout.findViewById(R.id.song_title);
        TextView artistView = (TextView)listLayout.findViewById(R.id.song_artist);
        TextView durationView = (TextView)listLayout.findViewById(R.id.song_duration);

        Song currentSong = songArray.get(position);

        //Highlight the currently playing song
        if(MainActivity.getMusicServiceObject() != null && MainActivity.getUserAction()){
            if (currentSong.getID() == MainActivity.getMusicServiceObject().getSongId()) {
                songView.setTextColor(Color.parseColor("#34B5E5"));
            }
        }

        songView.setText(currentSong.getTitle()); //pass data to textView objects in each list item
        artistView.setText(currentSong.getArtist());
        durationView.setText(currentSong.getDuration());

        listLayout.setTag(position); //use the song's position in list as a tag
        return listLayout;
    }

}
