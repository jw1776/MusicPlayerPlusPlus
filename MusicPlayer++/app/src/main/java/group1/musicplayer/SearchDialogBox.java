package group1.musicplayer;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.view.View.*;
import android.widget.RadioButton;

import java.util.ArrayList;

public class SearchDialogBox extends Activity implements OnClickListener {
    private ArrayList<Song> songList;
    int choice = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_dialog_box);
        //set listeners for the multiple buttons
        Button search = (Button) findViewById(R.id.Search_Button);
        search.setOnClickListener(this);
        Button back = (Button) findViewById(R.id.Back_Search);
        back.setOnClickListener(this);
        //tack the song list from the main activity
        songList = getIntent().getParcelableArrayListExtra("song_list");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //if the search button is pressed
            case R.id.Search_Button:
                //take the search term pass it to the search activity
                EditText searchTerm = (EditText) findViewById(R.id.search_term);
                Intent i = new Intent(getApplicationContext(), Search.class);
                Log.d("Stuff", searchTerm.getText().toString());
                i.putExtra("search_term", searchTerm.getText().toString());
                //pass the choice of search field to search activity
                i.putExtra("choice", choice);
                i.putParcelableArrayListExtra("song_list", songList);
                startActivityForResult(i, 1);
                break;

            case R.id.Back_Search:
                Log.d("EXIT","GOING BACK BACK BACK BACK");
                finish();
                break;

            default:
                break;
        }
    }
    //radio buttons act as search fields
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        //choice is set to 1 for artist
        //2 for album, 3 for song title and
        //4 for Any
        switch(view.getId()) {
            case R.id.Artist:
                if (checked)
                    choice = 1;
                    break;
            case R.id.Album:
                if (checked)
                    choice = 2;
                    break;
            case R.id.Song:
                if (checked)
                    choice = 3;
                    break;
            case R.id.Any:
                if (checked)
                    choice = 0;
                    break;
        }
    }
    //get the result from the search activity that can be passed back up to the main activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data != null){
            Intent output = new Intent();
            output.putExtra("searchChoice",data.getIntExtra("searchChoice",-1));
            setResult(1, output);
            finish();
        }

    }
}
