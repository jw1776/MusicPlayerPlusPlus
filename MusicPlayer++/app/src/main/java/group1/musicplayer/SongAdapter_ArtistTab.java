package group1.musicplayer;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class SongAdapter_ArtistTab extends BaseAdapter {

    private ArrayList<Song> songArray;
    private LayoutInflater songInf;

    public SongAdapter_ArtistTab(Context c, ArrayList<Song> newSongArray){
        songArray = newSongArray;
        songInf = LayoutInflater.from(c);
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
        RelativeLayout listLayout = (RelativeLayout)songInf.inflate(R.layout.song_artisttab, parent, false);
        //layout for each individual song in the list. Uses song_artisttab.xml
        TextView titleView = (TextView)listLayout.findViewById(R.id.song_title);
        TextView durationView = (TextView)listLayout.findViewById(R.id.song_duration_artistTab);

        Song currentSong = songArray.get(position);

        titleView.setText(currentSong.getTitle()); //pass data to textView objects in each list item
        durationView.setText(currentSong.getDuration());

        listLayout.setTag(position); //use the song's position as a tag
        return listLayout;
    }
}
