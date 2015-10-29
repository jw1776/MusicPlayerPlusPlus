package group1.musicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

public class Search extends ActionBarActivity{
    private ArrayList<Song> searchList;
    private ArrayList<Integer> searchIndex;
    private ListView songView;
    private MusicController controller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onPause();
        searchList = getIntent().getParcelableArrayListExtra("search_results");
        searchIndex = getIntent().getIntegerArrayListExtra("search_index");
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
                System.exit(0);
                break;
        }//end switch
        return super.onOptionsItemSelected(item);
    }
    public void songPicked(View view){ //executes when an item in the ListView is clicked. Defined in xml
        int picked = searchIndex.get(Integer.parseInt(view.getTag().toString()));
        Intent output = new Intent();
        output.putExtra("searchChoice", picked);
        setResult(1, output);
        finish();
    }
   
}
