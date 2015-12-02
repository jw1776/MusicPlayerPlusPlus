package group1.musicplayer;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
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
        LinearLayout listLayout = (LinearLayout)songInf.inflate(R.layout.song_artisttab, parent, false);
        //layout for each individual song in the list. Uses song_artisttab.xml
        TextView titleView = (TextView)listLayout.findViewById(R.id.song_title);

        Song currentSong = songArray.get(position);

        titleView.setText(currentSong.getTitle()); //pass data to textView objects in each list item

        listLayout.setTag(currentSong.getID()); //use the song's ID as a tag
        return listLayout;
    }
}
