package group1.musicplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by shawn on 3/26/2016.
 */

public class YoutubeSearch extends Activity {

    private EditText searchInput;
    private ListView videosFound;
    private Handler handler;
    private List<VideoItem> searchResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_search);

        searchInput = (EditText)findViewById(R.id.search_input);
        videosFound = (ListView)findViewById(R.id.videos_found);

        handler = new Handler();

        String song = getIntent().getStringExtra("currentSong");
        if(song != null){
            searchInput.setText(song);
           // searchOnYoutube(song);
        }

        searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    searchOnYoutube(v.getText().toString());
                    return false;
                }
                return true;
            }
        });

        setVideosFoundListener();
    }

    //populate the player with the videos that match the users song
    private void searchOnYoutube(final String keywords){

        new Thread(){
            public void run(){
                YoutubeConnector yc = new YoutubeConnector(YoutubeSearch.this);
                searchResults = yc.findVideos(keywords);

                handler.post(new Runnable(){
                    public void run(){
                        updateVideosFound();
                    }
                });
            }
        }.start();
    }

    //updates the view with each video item found, including the thumbnail, title, description, etc
    private void updateVideosFound(){

        ArrayAdapter<VideoItem> videoAdapter = new ArrayAdapter<VideoItem>(getApplicationContext(), R.layout.activity_video_item, searchResults){
            @Override
            public View getView(int position, View view, ViewGroup parent) {
                if(view == null){
                    view = getLayoutInflater().inflate(R.layout.activity_video_item, parent, false);
                }
                ImageView thumbnail = (ImageView)view.findViewById(R.id.video_thumbnail);
                TextView title = (TextView)view.findViewById(R.id.video_title);
                TextView description = (TextView)view.findViewById(R.id.video_description);

                VideoItem searchResult = searchResults.get(position);

                //set the thumbnail, title and description from the videoitem to the view
                Picasso.with(getApplicationContext()).load(searchResult.getThumbnailURL()).into(thumbnail);
                title.setText(searchResult.getTitle());
                description.setText(searchResult.getDescription());
                return view;
            }
        };
        videosFound.setAdapter(videoAdapter);
    }

    private void setVideosFoundListener(){

        videosFound.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
                Intent intent = new Intent(getApplicationContext(), YoutubePlayer.class);
                intent.putExtra("VIDEO_ID", searchResults.get(pos).getId());
                startActivity(intent);
            }
        });
    }
}