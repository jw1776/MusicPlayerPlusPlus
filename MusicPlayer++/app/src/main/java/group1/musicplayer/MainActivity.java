package group1.musicplayer;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.jar.Manifest;

import android.net.Uri;
import android.content.ContentResolver;
import android.database.Cursor;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.os.IBinder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.View;

//import fm.last.musicbrainz.data.dao.ArtistDao;
import group1.musicplayer.MusicService.MusicBinder;

import android.widget.MediaController.MediaPlayerControl;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

/*
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import fm.last.musicbrainz.data.model.Artist;*/

public class MainActivity extends Activity implements MediaPlayerControl {

    private static ArrayList<Song> songList, audioList;
    private static ArrayList<Artist> artistArray;
    private static ArrayList<Album> albumArray;
    private LinearLayout controller_layout;
    private static TextView nowPlayingText;
    private MusicService musicServiceObject;
    private Intent playIntent;
    private boolean musicBound = false; //Keeps track of whether or not MainActivity is bound to the MusicService
    private MusicController controller;
    private boolean paused = false; //true if activity is in onPause state
    private boolean playbackPaused = false;
    private static boolean userAction = false;
    private static int lastKnownDuration = 0;
    private static int lastKnownPosition = 0;
    private final Random random = new Random();
    private boolean shuffleOn = false;
    private ArrayList<Integer> shuffleList;

    private int shufflePos = 0;
    private String URL = "";

    private final int REQ_CODE_VIDEO_PLAYER = 20;
    private final int REQ_CODE_SPEECH_INPUT = 3;

    ActionBar.Tab songTab, artistTab, albumTab, playlistTab;
    Fragment songTabFragment = new SongTabFragment();
    Fragment artistTabFragment = new ArtistTabFragment();
    Fragment albumTabFragment = new AlbumTabFragment();
    Fragment playlistTabFragment = new PlaylistTabFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songList = new ArrayList<Song>();
        audioList = new ArrayList<Song>();
        artistArray = new ArrayList<Artist>();
        albumArray = new ArrayList<Album>();

        getSongList(); //fill the array with all songs
        removeDuplicates();
        sortSongsByTitle();

        populateAlbumArray();
        populateArtistArray();

        //For testing purposes, do not remove
        /*
        for(int p = 0; p < artistArray.size(); p++){
            System.out.println("ARTIST: " + artistArray.get(p).getTitle());
            for(int i = 0; i < artistArray.get(p).getAlbums().size(); i++){
                System.out.println("ALBUM: " + artistArray.get(p).getAlbums().get(i).getTitle());
                for (int j = 0; j < artistArray.get(p).getAlbums().get(i).getSongs().size(); j++){
                    System.out.println("SONG: " + artistArray.get(p).getAlbums().get(i).getSongs().get(j).getTitle());
                }
            }
        }
        */

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(true); //hide icon
        actionBar.setDisplayShowTitleEnabled(false); //show title
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        //Create and set the names for each tab
        songTab = actionBar.newTab().setText("Songs");
        artistTab = actionBar.newTab().setText("Artists");
        albumTab = actionBar.newTab().setText("Albums");
        playlistTab = actionBar.newTab().setText("Playlists");

        setAllTabListeners();

        //Add tabs to action bar
        actionBar.addTab(songTab);
        actionBar.addTab(artistTab);
        actionBar.addTab(albumTab);
        actionBar.addTab(playlistTab);

     /*   @Component
        public class ArtistHandler {//https://github.com/lastfm/musicbrainz-data

            private final ArtistDao artistDao;

            @Autowired
            public ArtistHandler(ArtistDao artistDao) {
                this.artistDao = artistDao;
            }

            @Transactional
            public void process(int id) {
                Artist artist = artistDao.getById(id);
                // ...
            }

            https://github.com/lastfm/musicbrainz-data
        }*/

        controller_layout = (LinearLayout) findViewById(R.id.controller_layout);
        nowPlayingText = (TextView) findViewById(R.id.nowplaying);
        setController(); //initializes the MediaController
    }

    private void setAllTabListeners() {
        //Set tab listeners
        songTab.setTabListener(new TabListener(songTabFragment));
        artistTab.setTabListener(new TabListener(artistTabFragment));
        albumTab.setTabListener(new TabListener(albumTabFragment));
        playlistTab.setTabListener(new TabListener(playlistTabFragment));
    }

    private void sortSongsByTitle() {

        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareToIgnoreCase(b.getTitle());
            }
        });
    }

    private void sortArtistsByTitle() {

        Collections.sort(artistArray, new Comparator<Artist>() {
            public int compare(Artist a, Artist b) {
                if (a.getTitle() == null || b.getTitle() == null) { //if either of the artist titles are null
                    return 0; //return 0, which indicates that the artist are equal
                } else {
                    return a.getTitle().compareToIgnoreCase(b.getTitle()); //otherwise compare as normal
                }
            }
        });
    }

    private void sortAlbumsByTitle() {

        Collections.sort(albumArray, new Comparator<Album>() {
            public int compare(Album a, Album b) {
                if (a.getTitle() == null || b.getTitle() == null) { //if either of the artist titles are null
                    return 0; //return 0, which indicates that the artist are equal
                } else {
                    return a.getTitle().compareToIgnoreCase(b.getTitle()); //otherwise compare as normal
                }
            }
        });
    }

    private void populateArtistArray() {
        //populate artistArray from the songList
        for (int i = 0; i < songList.size(); i++) {
            String thisArtist = songList.get(i).getArtist();
            boolean artistFound = false;

            for (int j = 0; j < artistArray.size(); j++) {
                if (artistArray.get(j).getTitle().equalsIgnoreCase(thisArtist)) {     //if the artist already exists in our array
                    artistFound = true;
                    artistArray.get(j).addSong(songList.get(i));        //add this song to the artist's array of songs
                    break;
                }
            }// inner for
            if (!artistFound) {   //if the artist was not found in the array
                Artist newArtist = new Artist(thisArtist);
                newArtist.addSong(songList.get(i));
                artistArray.add(newArtist);
            }
        }// outer for
        sortArtistsByTitle();
    }

    private void populateAlbumArray() {
        //populate albumArray from the songList
        for (int i = 0; i < songList.size(); i++) {
            String thisAlbum = songList.get(i).getAlbum();
            long thisAlbumID = songList.get(i).getAlbumId();
            String thisAlbumArtist = songList.get(i).getArtist();
            boolean albumFound = false;

            for (int j = 0; j < albumArray.size(); j++) {
                if (albumArray.get(j).getId() == thisAlbumID) {     //if the album already exists in our array
                    albumFound = true;
                    albumArray.get(j).addSong(songList.get(i));        //add this song to the album's array of songs
                    break;
                }
            }// inner for
            if (!albumFound) {   //if the album was not found in the array
                Uri sArtworkUri = Uri
                        .parse("content://media/external/audio/albumart");
                Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, thisAlbumID);

                System.out.println("ALBUM ART URI: " + albumArtUri.toString());
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(
                            this.getContentResolver(), albumArtUri);
                    //bitmap = Bitmap.createScaledBitmap(bitmap, 30, 30, true);

                } catch (FileNotFoundException exception) {
                    exception.printStackTrace();
                    bitmap = BitmapFactory.decodeResource(this.getResources(),
                            R.drawable.default_album);
                } catch (IOException e) {

                    e.printStackTrace();
                }

                Album newAlbum = new Album(thisAlbum, thisAlbumID, thisAlbumArtist, bitmap);
                newAlbum.addSong(songList.get(i));
                albumArray.add(newAlbum);
            }
        }// outer for
        sortAlbumsByTitle();
    }

    private ServiceConnection musicConnection = new ServiceConnection() { //connect to service, create ServiceConnection object

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicBinder binder = (MusicBinder) service;// binder variable = passed "service" variable
            musicServiceObject = binder.getService(); //get service
            musicServiceObject.fillList(songList); //Pass the array of songs to the MusicService object
            musicBound = true;

        }// end onServiceConnected method

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    // Broadcast receiver to determine when music player has been prepared
    private BroadcastReceiver onPrepareReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent i) {
            // When music player has been prepared, show controller
            if (userAction) {
                controller.show(0);
                controller_layout.setVisibility(View.VISIBLE); //show the media controller after a song has been chosen
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);

            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);

            startNotificationService();
            //start MusicService. This will initiate the code in onServiceConnected()
        }
    }

    private void startNotificationService(){

        Intent serviceIntent = new Intent(MainActivity.this, NotificationService.class);
        serviceIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
        startService(serviceIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        paused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Set up receiver for media player onPrepared broadcast
        LocalBroadcastManager.getInstance(this).registerReceiver(onPrepareReceiver,
                new IntentFilter("MEDIA_PLAYER_PREPARED"));

        if (paused) {
            setController(); //needed for controller to display upon returning to the app
            paused = false;
        }
        findViewById(R.id.activity_main).post(new Runnable() { //defer showing the controller until all lifecycle methods are called
            public void run() {

                if (userAction) {
                    controller.show(0);
                    controller_layout.setVisibility(View.VISIBLE); //show the media controller after a song has been chosen
                }
            }
        });
    }

    @Override
    protected void onStop() {
        controller.hide();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        musicServiceObject = null;
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //handles end/shuffle buttons
        switch (item.getItemId()) {
            case R.id.action_shuffle:
                //shuffle
                toggleShuffle();
                shuffleSong();
                break;
            case R.id.action_end:
                stopService(playIntent);
                musicServiceObject = null;
                System.exit(0);
                break;
            case R.id.action_search:
                this.search();
                break;
            case R.id.settings_button:
                displaySettingsOption();
                break;
            case R.id.voice_button:
                promptSpeechInput();
                break;
            case R.id.video_button:
                if (isNetworkAvailable()) {
                    startVideo();
                } else {
                    Toast.makeText(getApplicationContext(), "ERROR: Please connect to WIFI or enable"
                            + " mobile data.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.lyrics:
                if (isNetworkAvailable()) {
                    if(!userAction){
                        Toast.makeText(getApplicationContext(), "ERROR: Please select a song first to "
                                + " display lyrics.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        //   searchLyrics();
                        geniusSearch();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "ERROR: Please connect to WIFI or enable"
                            + " mobile data.", Toast.LENGTH_SHORT).show();
                }
                break;

        }//end switch
        return super.onOptionsItemSelected(item);
    }

    private void searchLyrics(){
        String songTitle = musicServiceObject.getSongTitle();
        if(songTitle.contains(" ")){
            songTitle = songTitle.replace(' ', '+');
        }
        String songArtist = musicServiceObject.getSongArtist();
        if(songArtist.contains(" ")){
            songArtist = songArtist.replace(' ', '+');
        }

        String song = "http://api.lyricsnmusic.com/songs?api_key=53f9fa63b88b03a07cd32892ae23ee&artist=" + songArtist +
                "&track=" + songTitle;
        // call AsynTask to perform network operation on separate thread
        new HttpAsyncTask().execute(song);
    }

    public String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            String viewable = "false", instrumental = "false";
            //Toast.makeText(getBaseContext(), "Received!", Toast.LENGTH_LONG).show();
            try {
                JSONArray jsonArray = new JSONArray(result);
                String str = "";
                str = jsonArray.getJSONObject(0).getString("url");
                viewable = jsonArray.getJSONObject(0).getString("viewable");
                instrumental = jsonArray.getJSONObject(0).getString("instrumental");

                System.out.println("*****************" + viewable);

                URL = str;
                System.out.println(URL);
                if(instrumental == "true"){
                    Toast.makeText(getBaseContext(), "Song is an instrumental", Toast.LENGTH_LONG).show();
                    return;
                }
                if (URL != "" && viewable == "true") {
                    Intent i = new Intent(getApplicationContext(), SearchLyrics.class);
                    i.putExtra("url", URL);
                    startActivity(i);
                }
                else{
                    geniusSearch();
                }
            } catch (JSONException e) {
                Log.e("MYAPP", "unexpected JSON exception", e);
                //     Toast.makeText(getBaseContext(), "Lyrics do not exist on LyricsnMusic, opening up Genius.com", Toast.LENGTH_LONG).show();
                geniusSearch();
            }
        }
    }

    private void geniusSearch(){
        String songTitle = musicServiceObject.getSongTitle();
        if(songTitle.contains(" ")){
            songTitle = songTitle.replace(' ', '-');
        }
        String songArtist = musicServiceObject.getSongArtist();
        if(songArtist.contains(" ")){
            songArtist = songArtist.replace(' ', '-');
        }
        URL = ("http://genius.com/" + songArtist + "-" + songTitle + "-lyrics");
        System.out.println("********" + URL);
        Intent i = new Intent(getApplicationContext(), SearchLyrics.class);
        i.putExtra("url", URL);
        startActivity(i);
    }

    /*display the settings to the user
    * they may either: add additional files, create a timer, edit voice control, etc*/
    private void displaySettingsOption() {

        View settingsView = findViewById(R.id.settings_button);
        PopupMenu settingsMenu = new PopupMenu(MainActivity.this, settingsView);
        settingsMenu.getMenuInflater().inflate(R.menu.settings_menu, settingsMenu.getMenu());
        settingsMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.add_icon:
                        beginAudioActivity();
                        break;

                    case R.id.timer_icon:
                        Intent timerActivity = new Intent(MainActivity.this, CustomTimer.class);
                        // timerActivity.putExtra("minuteValue", minuteValue);
                        // timerActivity.putExtra("hourValue", hourValue);
                        // startActivityForResult(timerActivity, 3);
                        startActivity(timerActivity);
                        break;

                    case R.id.voice_icon:
                        voiceSettings();
                        break;
                }
                return true;
                //nicer settings example: http://www.androidhive.info/2011/09/how-to-create-android-menus/
            }
        });
        settingsMenu.show();
    }

    /**
     * Showing google speech input dialog
     */
    private void promptSpeechInput() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Say a command like:\n- a song name \n- play next song \n- play previous song ");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Your device does not support speech to text!",
                    Toast.LENGTH_LONG).show();
        }
    }

    //grabs the voice to text value the user said (the song) and plays the song
    private void playRecognizedSong(String song) {

        //search the song list for the song that the user said
        for (int i = 0; i < songList.size(); i++) {
            //ignore any special characters in the original title of the song
            if (songList.get(i).getTitle().replaceAll("\\p{Punct}", "").equalsIgnoreCase(song.replaceAll("\\p{Punct}", ""))) {
                musicServiceObject.setSong(i);
                musicServiceObject.playSong();
                return;
            }
        }
        //the song they want to play does not exist OR it did not get translated correctly
        Toast.makeText(getApplicationContext(), "Could not find song: " + song, Toast.LENGTH_SHORT).show();
    }

    /*Allow the user to change the voice commands and enable the voice
    * recognition to be listening to you constantly, disabling the need
    * to press the voice button to do a voice command.*/
    private void voiceSettings(){

    }
    //check if the user has access to internet or not
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

//    @Override
//    public void onBackPressed() {
//        if(webView != null){
//            Intent goBack = new Intent(this, MainActivity.class);
//            startActivity(goBack);
//        } else {
//            finish();
//        }
//    }

    //launches the youtube player for the playing song
    private void startVideo() {

        Intent i = new Intent(MainActivity.this, YoutubeSearch.class);

        if (playbackPaused || isPlaying()) {
            //search for a video for the current song and begin playing it
            i.putExtra("currentSong", getCurrentSong());
            pause();
        }
        startActivityForResult(i, REQ_CODE_VIDEO_PLAYER);
    }

    //returns the title and artist of the current song playing
    //if no song is currently playing, returns null
    private String getCurrentSong() {

        if (musicServiceObject.getSongPosition() != -1) {
            // System.out.println("*******current playing song val is: " + musicServiceObject.getSongPosition());
            return musicServiceObject.getSongArray().get(musicServiceObject.getSongPosition()).toString();
        }
        return null;
    }

    private void toggleShuffle() {
        //toggle on or off the shuffle button
        shufflePos = 0;
        shuffleList = new ArrayList<Integer>();
        shuffleList.add(musicServiceObject.getSongPosition());
        if (shuffleOn) {
            shuffleOn = false;
            Toast.makeText(getApplicationContext(), "Shuffle toggled off.", Toast.LENGTH_SHORT).show();
        } else {
            shuffleOn = true;
            Toast.makeText(getApplicationContext(), "Shuffle toggled on.", Toast.LENGTH_SHORT).show();
        }
    }

    //randomly shuffles the next song to play
    private void shuffleSong() {

//        if (shuffleOn) {//randomly shuffle the next song
        int num = random.nextInt(musicServiceObject.getSongArray().size());
        shuffleList.add(num);//keep a list of the randomly generated songs
//        } else {//set the next song to play in the list
//            //pos is at the end, so set the next song to the first song
//            if (musicServiceObject.getSongPosition() == musicServiceObject.getSongArray().size() - 1) {
//                musicServiceObject.setSong(0);
//            } else {
//                musicServiceObject.setSong(musicServiceObject.getSongPosition() + 1);
//            }
//        }
    }

    //display the activity to add additional files, like recordings
    private void beginAudioActivity() {

        Intent i = new Intent(this, Audio.class);
        i.putExtra("filteredAudioList", audioList);
        startActivityForResult(i, 1);
    }

    //add the new songs to the default list
    private void addAdditionalSongs(ArrayList<Song> additionalSongs) {

        // System.out.println("\n*\n*\n**********************BEFORE THE SIZE OF THE SONG LIST IS: " + songList.size());
        for (int i = 0; i < additionalSongs.size(); i++) {
            songList.add(additionalSongs.get(i));
        }
        // System.out.println("\n*\n*\n**********************AFTER THE SIZE OF THE SONG LIST IS: " + songList.size());
    }

    private void removeDuplicates() {//remove duplicate songs based on artist and title

        HashMap<String, Song> map = new HashMap<String, Song>();

        //add all of the song items to a map, to remove the duplicates
        for (int i = 0; i < songList.size(); i++) {
            if (!map.containsKey(songList.get(i).toString()))//toString is the artist and title
                map.put(songList.get(i).toString(), songList.get(i));
        }
        //override the previous values of the list with the hashmap
        songList = new ArrayList<Song>(map.values());
    }

    public void getSongList() {

        ContentResolver musicResolver = getContentResolver();//resolve the contents of the phone
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;//grab the location of the audio

        String[] musicContents = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ALBUM_ID
        };

        ArrayList<Song> ringtones = new ArrayList<Song>();
        ArrayList<Song> notifications = new ArrayList<Song>();

        //create 4 separate lists of: songs only, ringtones only, notifications only, list of everything
        createList(musicResolver, musicUri, musicContents, MediaStore.Audio.Media.IS_MUSIC + " != 0", songList);
        createList(musicResolver, musicUri, musicContents, MediaStore.Audio.Media.IS_RINGTONE + " != 0", ringtones);
        createList(musicResolver, musicUri, musicContents, MediaStore.Audio.Media.IS_NOTIFICATION + " != 0", notifications);
        createList(musicResolver, musicUri, musicContents, null, audioList);//default audio consists of everything
        //MediaStore.Audio.Media.IS_PODCAST != 0

        HashMap<String, Song> map = new HashMap<String, Song>();
        addListToMap(audioList, map);//add everything to the map
        //remove the songs, ringtones and notifications from the map; leaving the map with recordings
        removeNonsenseFromMap(songList, map);
        removeNonsenseFromMap(ringtones, map);
        removeNonsenseFromMap(notifications, map);

        //left off here. add shit to the additional if it also contains the key word unknown for title
        audioList = new ArrayList<Song>(map.values());//populate the audioList with the map contents
    }

    private void addListToMap(ArrayList<Song> list, HashMap<String, Song> map) {

        //add all of the song items to a map, to remove the duplicates
        for (int i = 0; i < list.size(); i++) {
            if (!map.containsKey(list.get(i).toString()))//toString is the artist and title
                map.put(list.get(i).toString(), list.get(i));
        }
    }

    private void removeNonsenseFromMap(ArrayList<Song> list, HashMap<String, Song> map) {

        for (int i = 0; i < list.size(); i++) {
            if (map.containsKey(list.get(i).toString())) {
                map.remove(list.get(i).toString());
            }
        }
    }

    private void printList(ArrayList<Song> list) {//for debugging

        for (int i = 0; i < list.size(); i++)
            System.out.println(list.get(i).toString() + " Album: " + list.get(i).getAlbum());
    }

    //create and populate a list of Song objects for the passed in lists, such as
    //songList, ringtones, notifications, and everything
    private void createList(ContentResolver musicResolver, Uri musicUri, String[] musicContents,
                            String selection, ArrayList<Song> list) {

        Cursor musicCursor = musicResolver.query(
                musicUri,//the location of the media
                musicContents,//the attributes of the song, such as artist, title, album, etc
                selection,//the type of audio file, such as songs, ringtones, and notifications
                null,
                null);

        if (musicCursor != null && musicCursor.moveToFirst()) {//at least one audio file exits

            //the columns for each song data
            int titleColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST);
            int albumColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ALBUM);
            int albumIdColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ALBUM_ID);

            do { //add the Song objects to the list by moving the cursor to each data column
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisAlbum = musicCursor.getString(albumColumn);
                long thisAlbumId = musicCursor.getLong(albumIdColumn);

                list.add(new Song(thisId, thisTitle, thisArtist, thisAlbum, thisAlbumId));
            }
            while (musicCursor.moveToNext()); //while there are still items left
        }
    }

    public static ArrayList<Song> getSongArray() { //for use in SongTabFragment
        return songList;
    }

    public static ArrayList<Artist> getArtistArray() {
        return artistArray;
    }

    public static ArrayList<Album> getAlbumArray() {
        return albumArray;
    }

    public void songPicked(View view) { //executes when an item in SongTabFragment's ListView is clicked. Defined in xml
        userAction = true;

        musicServiceObject.fillList(songList);
        musicServiceObject.setSong(Integer.parseInt(view.getTag().toString()));
        musicServiceObject.playSong();
        //if(playbackPaused){
        //    setController();
        //    playbackPaused = false;
        //}
        //controller.show(0);
    }

    public void artistPicked(View view) {
        int artistPosition = (int) view.getTag();
        ArtistTabFragment.showAlbums(artistArray.get(artistPosition).getAlbums());
    }

    public void albumPicked_artistTab(View view) {
        int albumPosition = (int) view.getTag();
        ArtistTabFragment.showSongs(albumPosition);
    }

    public void songPicked_artistTab(View view) {
        userAction = true;
        ArtistTabFragment.updateContextArray();

        musicServiceObject.fillList(ArtistTabFragment.getContextArray());
        musicServiceObject.setSong(Integer.parseInt(view.getTag().toString()));
        musicServiceObject.playSong();
    }

    public void backButton_artistTab(View view) {
        ArtistTabFragment.backButtonPressed();
    }

    public static void setNowPlayingText(Song songNowPlaying) {
        nowPlayingText.setText(songNowPlaying.getTitle() + " - " + songNowPlaying.getArtist());
    }

    // Methods below this point handle the MediaController
    private void setController() {
        if (controller == null) controller = new MusicController(this);

        //controller = new MusicController(this);

        controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });

        controller.setMediaPlayer(this);
        controller.setAnchorView(findViewById(R.id.controller_layout));
        controller.setEnabled(true);
    }

    protected void playNext() { //called above by the Controller's onClick methods

        if (shuffleOn) {
            shufflePos++;
            shuffleSong();
            musicServiceObject.setSong(shuffleList.get(shufflePos));
            musicServiceObject.playSong();
        } else
            musicServiceObject.playNext();
        //if(playbackPaused){
        //    setController();
        //    playbackPaused = false;
        //}
        //controller.show(0);

    }

    protected void playPrev() {
        if (shuffleOn) {
            shufflePos--;
            //Reset Song to the end of the Shuffle Song ArrayList
            if (shufflePos < 0) {
                shufflePos = shuffleList.size() - 1;
            }

            musicServiceObject.setSong(shuffleList.get(shufflePos));
            musicServiceObject.playSong();

        } else
            musicServiceObject.playPrev();
        //if(playbackPaused){
        //    setController();
        //    playbackPaused = false;
        //}
        //controller.show(0);
    }

    private void search() {
        //start the custom dialog box which is actually a new activity called SearchDialogBox
        Intent i = new Intent(getApplicationContext(), SearchDialogBox.class);
        i.putParcelableArrayListExtra("song_list", songList);
        startActivityForResult(i, 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQ_CODE_VIDEO_PLAYER && playbackPaused){
            musicServiceObject.go();//resume the player when returned from video player
        }

        if (resultCode == RESULT_OK) {

            System.out.println("**returned from another activity");
            if (requestCode == REQ_CODE_SPEECH_INPUT) {
                //the user used voice to text and some values were created
                ArrayList<String> voiceItems = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                //check the first in the list and see what they said

                if (voiceItems.get(0).equalsIgnoreCase("Play next song")) {
                    playNext();
                } else if (voiceItems.get(0).equalsIgnoreCase("Play previous song")) {
                    playPrev();
                } else {
                    playRecognizedSong(voiceItems.get(0));
                }
                userAction = true;
                //for testing
                // for (int i = 0; i < voiceItems.size(); i++) {
                //     System.out.println("Voice item " + i + ": " + voiceItems.get(i));
                // }
            } else {
                ArrayList<String> audioListString = data.getStringArrayListExtra("additionalSongs");

                //find the Song objects that were added
                ArrayList<Song> additionalSongs = findAdditionalSongs(audioListString);
                if (additionalSongs != null) {
                    addAdditionalSongs(additionalSongs);//add the additional songs to the main list
                    sortSongsByTitle();
                    //  SongTabFragment songFragment = (SongTabFragment) getFragmentManager().findFragmentById(R.id.main_full);
                    //  songFragment.updateAdapterArray(songList);

                    //left off here
                    songTabFragment = new SongTabFragment();//update the list on the screen
                    setAllTabListeners();
                }
            }
        }

        if (requestCode == 1 && resultCode == 1 && data != null) {
            musicServiceObject.setSong(data.getIntExtra("searchChoice", -1));
            musicServiceObject.playSong();
            //if(playbackPaused){
            //    setController();
            //    playbackPaused = false;
            //}
            //controller.show(0);
        }
    }

    private ArrayList<Song> findAdditionalSongs(ArrayList<String> additionalListString) {

        ArrayList<Song> additionalSongs = new ArrayList<Song>();

        for (int i = 0; i < additionalListString.size(); i++) {
            for (int j = 0; j < audioList.size(); j++) {
                if (audioList.get(j).toString().equals(additionalListString.get(i))) {
                    // System.out.println("additional found: " + audioList.get(i).toString());
                    additionalSongs.add(audioList.remove(j));
                    break;
                }
            }
        }
        return additionalSongs;
    }

    public static void setUserAction() {
        userAction = true;
    }

    public static boolean getUserAction() {
        return userAction;
    }

    //MediaPlayerControl interface methods
    @Override
    public void start() {
        musicServiceObject.go();
    }

    @Override
    public void pause() {
        playbackPaused = true;
        musicServiceObject.pausePlayer();
    }

    @Override
    public int getDuration() {
        if (musicServiceObject != null && musicBound && musicServiceObject.isPng()) { //make sure MusicService is instantiated and bound to this activity
            lastKnownDuration = musicServiceObject.getDur();
            return lastKnownDuration;

        } else return lastKnownDuration;
    }

    @Override
    public int getCurrentPosition() {
        if (musicServiceObject != null && musicBound && musicServiceObject.isPng()) {
            lastKnownPosition = musicServiceObject.getPosn();
            return lastKnownPosition;
        } else return lastKnownPosition;
    }

    @Override
    public void seekTo(int pos) {
        musicServiceObject.seek(pos);
    }

    @Override
    public boolean isPlaying() {
        if (musicServiceObject != null && musicBound) {
            return musicServiceObject.isPng();
        } else return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    public void addPlaylistClick(View v) {
        Intent i = new Intent(getApplicationContext(), CreatePlaylistActivity.class);
        i.putParcelableArrayListExtra("song_list", songList);
        startActivityForResult(i, 1);
    }

    public void playlistPicked(View v) {
        int playlistPosition = (int) v.getTag();
        PlaylistTabFragment.showPlaylistSongs(playlistPosition);
    }

    public void backButton_playlistTab(View v) {
        PlaylistTabFragment.backButtonPressed();
    }

    public void backButton_albumTab(View v) {
        AlbumTabFragment.backButtonPressed();
    }

    public void albumPicked_albumTab(View v) {
        int albumPosition = (int) v.getTag();
        AlbumTabFragment.showAlbumSongs(albumPosition);
    }

    public void songPicked_albumTab(View view) {
        userAction = true;
        AlbumTabFragment.updateContextArray();

        musicServiceObject.fillList(AlbumTabFragment.getContextArray());
        musicServiceObject.setSong(Integer.parseInt(view.getTag().toString()));
        musicServiceObject.playSong();
    }

    public void songPicked_playlistTab(View view) {
        userAction = true;
        PlaylistTabFragment.updateContextArray();
        Log.e("DEBUG", "songPicked_playlistTab RUNNINGNOW");
        musicServiceObject.fillList(PlaylistTabFragment.getContextArray());
        musicServiceObject.setSong(Integer.parseInt(view.getTag().toString()));
        musicServiceObject.playSong();
    }

}