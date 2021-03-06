package group1.musicplayer;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by LukeJr on 11/1/2015.
 */
public class ArtistAdapter extends BaseAdapter {

    private ArrayList<Artist> artistArray;
    private LayoutInflater artistInf;

    public ArtistAdapter(Context c, ArrayList<Artist> grabbedArtistArray){
        artistArray = grabbedArtistArray;
        artistInf = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return artistArray.size();
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
        LinearLayout listLayout = (LinearLayout)artistInf.inflate(R.layout.artist, parent, false);
        //layout for each individual artist in the list uses artist.xml
        TextView nameView = (TextView)listLayout.findViewById(R.id.artist_name);
        TextView countView = (TextView) listLayout.findViewById(R.id.artist_count);

        String thisArtist = artistArray.get(position).getTitle();
        nameView.setText(thisArtist); //pass artist name string to textView objects in each list item

        String albumString = new String("");
        if(artistArray.get(position).getAlbumCount() == 1){
            albumString = " album, ";
        }else{
            albumString = " albums, ";
        }

        String songString = new String("");
        if(artistArray.get(position).getSongCount() == 1){
            songString = " song";
        }else{
            songString = " songs";
        }

        String thisCount = new String(artistArray.get(position).getAlbumCount() + albumString + artistArray.get(position).getSongCount() + songString);
        countView.setText(thisCount);

        listLayout.setTag(position); //use the artists position as a tag

        return listLayout;
    }
}
