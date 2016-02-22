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
public class AlbumAdapter_AlbumTab extends BaseAdapter {

    private ArrayList<Album> albumArray;
    private LayoutInflater albumInf;

    public AlbumAdapter_AlbumTab(Context c, ArrayList<Album> newAlbumArray){
        albumArray = newAlbumArray;
        albumInf = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return albumArray.size();
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
        LinearLayout listLayout = (LinearLayout)albumInf.inflate(R.layout.album_tab_album, parent, false);
        //layout for each individual album in the list uses album_tab_album.xml
        TextView titleView = (TextView)listLayout.findViewById(R.id.album_title_albumTab);
        TextView artistView = (TextView)listLayout.findViewById(R.id.album_artist_albumTab);

        String albumTitle = albumArray.get(position).getTitle();
        String albumArtist = albumArray.get(position).getArtist();
        titleView.setText(albumTitle);
        artistView.setText(albumArtist);

        listLayout.setTag(position); //use the album position as a tag

        return listLayout;
    }
}

