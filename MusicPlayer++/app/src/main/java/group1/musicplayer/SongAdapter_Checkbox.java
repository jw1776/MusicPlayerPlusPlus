package group1.musicplayer;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import java.util.List;

public class SongAdapter_Checkbox extends ArrayAdapter<Song_Checkbox> {

    private final List<Song_Checkbox> list;
    private final Activity context;
    boolean checkAll_flag = false;
    boolean checkItem_flag = false;

    public SongAdapter_Checkbox(Activity context, List<Song_Checkbox> list) {
        super(context, R.layout.playlist_song, list);
        this.context = context;
        this.list = list;
    }

    static class ViewHolder {
        protected TextView text;
        protected CheckBox checkbox;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            convertView = inflator.inflate(R.layout.playlist_song, null);
            viewHolder = new ViewHolder();
            viewHolder.text = (TextView) convertView.findViewById(R.id.playlist_song_artist);
            viewHolder.checkbox = (CheckBox) convertView.findViewById(R.id.playlist_song_title);
            viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int getPosition = (Integer) buttonView.getTag();  // Here we get the position that we have set for the checkbox using setTag.
                    list.get(getPosition).setSelected(buttonView.isChecked()); // Set the value of checkbox to maintain its state.
                }
            });
            convertView.setTag(viewHolder);
            convertView.setTag(R.id.playlist_song_artist, viewHolder.text);
            convertView.setTag(R.id.playlist_song_title, viewHolder.checkbox);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.checkbox.setTag(position);

        viewHolder.text.setText(list.get(position).getArtist());
        viewHolder.checkbox.setText(list.get(position).getTitle());
        viewHolder.checkbox.setChecked(list.get(position).isSelected());

        return convertView;
    }
}
