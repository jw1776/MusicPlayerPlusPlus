package group1.musicplayer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class CreatePlaylistActivity extends Activity {
    private ArrayList<Song> songList;
    private ArrayList<Song_Checkbox> checkboxList;
    private ListView songView;
    private AlertDialog.Builder dialogBuilder;
    private Button doneButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_playlist);
        songList = getIntent().getParcelableArrayListExtra("song_list");
        convertSongList();
        songView = (ListView) findViewById(R.id.createPlaylist_ListView);
        doneButton = (Button) findViewById(R.id.playlistDoneButton);
        SongAdapter_Checkbox theAdapter = new SongAdapter_Checkbox(this, checkboxList);
        songView.setAdapter(theAdapter);
    }

    public void convertSongList() {
        checkboxList = new ArrayList<Song_Checkbox>();

        for(int i = 0; i < songList.size(); i++) {
            Song_Checkbox convert_me = new Song_Checkbox(songList.get(i).getID(), songList.get(i).getTitle(), songList.get(i).getArtist(), songList.get(i).getAlbum(), songList.get(i).getAlbumId(), songList.get(i).getDuration());
            checkboxList.add(convert_me);
        }
    }

    public void playlistSongChecked(View v) {

    }

    public void doneButtonClick(View v) {
        dialogBuilder = new AlertDialog.Builder(this);
        final EditText textInput = new EditText(this);
        dialogBuilder.setTitle("Playlist Name");
        dialogBuilder.setView(textInput);
        dialogBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ArrayList<Song> temp = new ArrayList<Song>();
                for (int i = 0; i < checkboxList.size(); i++) {
                    if (checkboxList.get(i).isSelected()) {
                        Song convert_me = new Song(checkboxList.get(i).getID(), checkboxList.get(i).getTitle(), checkboxList.get(i).getArtist(), checkboxList.get(i).getAlbum(), checkboxList.get(i).getAlbumId(), checkboxList.get(i).getDuration());
                        temp.add(convert_me);
                    }
                }
                String playlistTitle = "";
                playlistTitle += textInput.getText().toString();
                Playlist p = new Playlist(playlistTitle, temp);
                DBHandler db = new DBHandler(CreatePlaylistActivity.this, null, null, 1);
                db.addPlaylist(p);
                finish();
            }
        });

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        AlertDialog playlistDoneDialog = dialogBuilder.create();
        playlistDoneDialog.show();
    }
}
