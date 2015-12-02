package group1.musicplayer;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AlbumAdapter extends BaseAdapter {

    private ArrayList<Album> albumArray;
    private LayoutInflater albumInf;

    public AlbumAdapter(Context c, ArrayList<Album> newAlbumArray){
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
        LinearLayout listLayout = (LinearLayout)albumInf.inflate(R.layout.album, parent, false);
        //layout for each individual album in the list uses album.xml
        TextView titleView = (TextView)listLayout.findViewById(R.id.album_title);

        String albumTitle = albumArray.get(position).getTitle();
        titleView.setText(albumTitle);

        listLayout.setTag(position); //use the album position as a tag

        return listLayout;
    }
}
