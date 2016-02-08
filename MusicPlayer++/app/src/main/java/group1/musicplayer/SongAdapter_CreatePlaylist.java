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
 * Created by Jack on 2/7/2016.
 */
public class SongAdapter_CreatePlaylist extends BaseAdapter {

    private ArrayList<Song> songArray;
    private LayoutInflater songInf;

    public SongAdapter_CreatePlaylist(Context c, ArrayList<Song> grabbedSongArray){
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
        LinearLayout listLayout = (LinearLayout)songInf.inflate(R.layout.playlist_song, parent, false);
        //layout for each individual song in the list. Uses playlist_song.xml
        TextView songView = (TextView)listLayout.findViewById(R.id.playlist_song_title);
        TextView artistView = (TextView)listLayout.findViewById(R.id.playlist_song_artist);

        Song currentSong = songArray.get(position);

        songView.setText(currentSong.getTitle()); //pass data to textView objects in each list item
        artistView.setText(currentSong.getArtist());

        listLayout.setTag(position); //use the song's position in list as a tag
        return listLayout;
    }
}
