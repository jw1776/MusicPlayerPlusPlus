package group1.musicplayer;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

public class Search extends Activity {
    private ArrayList<Song> songList;
    private ArrayList<Song> searchList;
    private String searchTerm;
    private int choice;
    private ArrayList<Integer> searchIndex;
    private ListView songView;
    private MusicController controller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        choice = -1;
        searchIndex = new ArrayList<Integer>();
        searchList = new ArrayList<Song>();
        songList = getIntent().getParcelableArrayListExtra("song_list");
        searchTerm = getIntent().getStringExtra("search_term");
        if(choice ==-1){
            for(int i = 0; i<songList.size();i++){
                Song current = songList.get(i);
                if((current.getTitle() != null &&current.getTitle().toLowerCase().contains(searchTerm.toLowerCase()))||
                        (current.getArtist()!=null &&current.getArtist().toLowerCase().contains(searchTerm.toLowerCase()))){
                    Log.d("stuff", current.getTitle());
                    searchList.add(current);
                    searchIndex.add(i);
                }
            }
        }
        else if(choice == 1){
            for(int i = 0; i<songList.size();i++){
                Song current = songList.get(i);
                if(current != null &&current.getArtist().toLowerCase().contains(searchTerm.toLowerCase())){
                    Log.d("stuff", current.getArtist());
                    searchList.add(current);
                    searchIndex.add(i);
                }
            }
        }

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(false); //hide icon
        super.onCreate(savedInstanceState);
        super.onPause();

        setContentView(R.layout.activity_search);
        songView = (ListView) findViewById(R.id.search_list);

        SongAdapter theAdapter = new SongAdapter(this, searchList);
        songView.setAdapter(theAdapter);
    }


    @Override
    protected void onStart(){
        super.onStart();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){ //handles end/shuffle buttons
        switch(item.getItemId()) {
            case R.id.action_shuffle:
                //shuffle
                break;

            case R.id.back:
                finish();
                break;
        }//end switch
        return super.onOptionsItemSelected(item);
    }
    public void songPicked(View view){ //executes when an item in the ListView is clicked. Defined in xml
        MainActivity.setUserAction();
        int picked = searchIndex.get(Integer.parseInt(view.getTag().toString()));
        Intent output = new Intent();
        output.putExtra("searchChoice", picked);
        setResult(1, output);
        finish();
    }

}
