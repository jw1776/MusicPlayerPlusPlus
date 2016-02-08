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
    private ListView songView;
    private AlertDialog.Builder dialogBuilder;
    private Button doneButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_playlist);
        songList = getIntent().getParcelableArrayListExtra("song_list");
        songView = (ListView) findViewById(R.id.createPlaylist_ListView);
        doneButton = (Button) findViewById(R.id.playlistDoneButton);
        SongAdapter_CreatePlaylist theAdapter = new SongAdapter_CreatePlaylist(this, songList);
        songView.setAdapter(theAdapter);
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
