package group1.musicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by LukeJr on 2/21/2016.
 */
public class SongAdapter_AlbumTab extends BaseAdapter {

    private ArrayList<Song> songArray;
    private LayoutInflater songInf;

    public SongAdapter_AlbumTab(Context c, ArrayList<Song> newSongArray){
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
        LinearLayout listLayout = (LinearLayout)songInf.inflate(R.layout.song_albumtab, parent, false);
        //layout for each individual song in the list. Uses song_albumtab.xml
        TextView titleView = (TextView)listLayout.findViewById(R.id.song_title_albumTab);

        Song currentSong = songArray.get(position);

        titleView.setText(currentSong.getTitle()); //pass data to textView objects in each list item

        listLayout.setTag(position); //use the song's position as a tag
        return listLayout;
    }
}