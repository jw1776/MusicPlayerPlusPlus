package group1.musicplayer;

/**
 * Created by Jack on 2/13/2016.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class PlaylistAdapter extends BaseAdapter {

    private ArrayList<Playlist> playlistArray;
    private LayoutInflater playlistInflater;

    public PlaylistAdapter(Context c, ArrayList<Playlist> new_playlistArray){
        playlistArray = new_playlistArray;
        playlistInflater = LayoutInflater.from(c);
    }


    @Override
    public int getCount() {
        return playlistArray.size();
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
        LinearLayout listLayout = (LinearLayout)playlistInflater.inflate(R.layout.playlist, parent, false);
        //layout for each individual playlist in the list. Uses playlist.xml
        TextView playlistView = (TextView)listLayout.findViewById(R.id.playlist_name);
        TextView countView = (TextView)listLayout.findViewById(R.id.playlist_count);

        Playlist currentPlaylist = playlistArray.get(position);
        playlistView.setText(currentPlaylist.getTitle()); //pass data to textView objects in each list item

        String songString = new String("");
        if(playlistArray.get(position).getPlaylistSongs().size() == 1){
            songString = " song";
        }else{
            songString = " songs";
        }
        countView.setText(playlistArray.get(position).getPlaylistSongs().size() + songString);

        listLayout.setTag(position); //use the playlist's position in list as a tag
        return listLayout;
    }
}

