package group1.musicplayer;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;

import android.net.Uri;
import android.content.ContentResolver;
import android.database.Cursor;
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

/*
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import fm.last.musicbrainz.data.model.Artist;*/

public class MainActivity extends Activity implements MediaPlayerControl {

    private static ArrayList<Song> songList, audioList;
    private static ArrayList<Playlist> playlistArray;
    private static ArrayList<Artist> artistArray;
    private static ArrayList<Album> albumArray;
    private ArrayList<Song> searchList;
    private ArrayList<Integer> searchIndex;
    private boolean searching;
    private String searchTerm;
    private ListView songView;
    private LinearLayout controller_layout;
    private static TextView nowPlayingText;
    private MusicService musicServiceObject;
    private Intent playIntent;
    private boolean musicBound = false; //Keeps track of whether or not MainActivity is bound to the MusicService
    private MusicController controller;
    private boolean paused = false; //true if activity is in onPause state
    private boolean playbackPaused = false;
    private static boolean userAction = false;
    private AlertDialog.Builder dialogBuilder;
    private static int lastKnownDuration = 0;
    private static int lastKnownPosition = 0;
    private final Random random = new Random();
    private boolean shuffleOn = false;
    private ArrayList<Integer> shuffleList = new ArrayList<Integer>();
    private String hourValue = "00";
    private String minuteValue = "00";

    ActionBar.Tab songTab, artistTab, albumTab, playlistTab;
    Fragment songTabFragment = new SongTabFragment();
    Fragment artistTabFragment = new ArtistTabFragment();
    Fragment albumTabFragment = new AlbumTabFragment();
    Fragment playlistTabFragment = new PlaylistTabFragment();

    //http://www.androidhive.info/2014/07/android-speech-to-text-tutorial/
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

        populateArtistArray();
        populateAlbumArray();

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

        controller_layout = (LinearLayout)findViewById(R.id.controller_layout);
        nowPlayingText = (TextView)findViewById(R.id.nowplaying);
        setController(); //initializes the MediaController
    }

    private void setAllTabListeners(){
        //Set tab listeners
        songTab.setTabListener(new TabListener(songTabFragment));
        artistTab.setTabListener(new TabListener(artistTabFragment));
        albumTab.setTabListener(new TabListener(albumTabFragment));
        playlistTab.setTabListener(new TabListener(playlistTabFragment));
    }

    private void sortSongsByTitle(){

        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareToIgnoreCase(b.getTitle());
            }
        });
    }

    private void sortArtistsByTitle(){

        Collections.sort(artistArray, new Comparator<Artist>() {
            public int compare(Artist a, Artist b) {
                if (a.getTitle() == null || b.getTitle() == null) { //if either of the artist titles are null
                    return 0; //return 0, which indicates that the artist are equal
                }
                else {
                    return a.getTitle().compareToIgnoreCase(b.getTitle()); //otherwise compare as normal
                }
            }
        });
    }

    private void sortAlbumsByTitle(){

        Collections.sort(albumArray, new Comparator<Album>() {
            public int compare(Album a, Album b) {
                if (a.getTitle() == null || b.getTitle() == null) { //if either of the artist titles are null
                    return 0; //return 0, which indicates that the artist are equal
                }
                else {
                    return a.getTitle().compareToIgnoreCase(b.getTitle()); //otherwise compare as normal
                }
            }
        });
    }

    private void populateArtistArray(){
        //populate artistArray from the songList
        for(int i = 0; i < songList.size(); i++){
            String thisArtist = songList.get(i).getArtist();
            boolean artistFound = false;

            for(int j = 0; j < artistArray.size(); j++){
                if(artistArray.get(j).getTitle().equalsIgnoreCase(thisArtist)){     //if the artist already exists in our array
                    artistFound = true;
                    artistArray.get(j).addSong(songList.get(i));        //add this song to the artist's array of songs
                    break;
                }
            }// inner for
            if(!artistFound){   //if the artist was not found in the array
                Artist newArtist = new Artist(thisArtist);
                newArtist.addSong(songList.get(i));
                artistArray.add(newArtist);
            }
        }// outer for
        sortArtistsByTitle();
    }

    private void populateAlbumArray(){
        //populate albumArray from the songList
        for(int i = 0; i < songList.size(); i++){
            String thisAlbum = songList.get(i).getAlbum();
            long thisAlbumID = songList.get(i).getAlbumId();
            String thisAlbumArtist = songList.get(i).getArtist();
            boolean albumFound = false;

            for(int j = 0; j < albumArray.size(); j++){
                if(albumArray.get(j).getId() == thisAlbumID){     //if the album already exists in our array
                    albumFound = true;
                    albumArray.get(j).addSong(songList.get(i));        //add this song to the album's array of songs
                    break;
                }
            }// inner for
            if(!albumFound){   //if the album was not found in the array
                Album newAlbum = new Album(thisAlbum, thisAlbumID, thisAlbumArtist);
                newAlbum.addSong(songList.get(i));
                albumArray.add(newAlbum);
            }
        }// outer for
        sortAlbumsByTitle();
    }

    private ServiceConnection musicConnection = new ServiceConnection(){ //connect to service, create ServiceConnection object

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicBinder binder = (MusicBinder)service;// binder variable = passed "service" variable
            musicServiceObject = binder.getService(); //get service
            musicServiceObject.fillList(songList); //Pass the array of songs to the MusicService object
            musicBound = true;

        }// end onServiceConnected method

        @Override
        public void onServiceDisconnected(ComponentName name){
            musicBound = false;
        }
    };

    // Broadcast receiver to determine when music player has been prepared
    private BroadcastReceiver onPrepareReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent i) {
            // When music player has been prepared, show controller
            if(userAction){
                controller.show(0);
                controller_layout.setVisibility(View.VISIBLE); //show the media controller after a song has been chosen
            }
        }
    };

    @Override
    protected void onStart(){
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, MusicService.class);

            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
            //start MusicService. This will initiate the code in onServiceConnected()
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        paused = true;
    }

    @Override
    protected void onResume(){
        super.onResume();

        // Set up receiver for media player onPrepared broadcast
        LocalBroadcastManager.getInstance(this).registerReceiver(onPrepareReceiver,
                new IntentFilter("MEDIA_PLAYER_PREPARED"));

        if(paused){
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
    protected void onStop(){
        controller.hide();
        super.onStop();
    }

    @Override
    protected void onDestroy(){
        stopService(playIntent);
        musicServiceObject=null;
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){ //handles end/shuffle buttons
        switch(item.getItemId()) {
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

        }//end switch
        return super.onOptionsItemSelected(item);
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
                        System.out.println("voice place\n.\n.");
                        break;
                }
                return true;
                //nicer settings example: http://www.androidhive.info/2011/09/how-to-create-android-menus/
            }
        });
        settingsMenu.show();
    }

    private void toggleShuffle(){
        //toggle on or off the shuffle button
        if(shuffleOn) shuffleOn = false;
        else shuffleOn = true;
    }

    //randomly shuffles the next song to play
    private void shuffleSong(){

        if(shuffleOn){//randomly shuffle the next song
            int num = random.nextInt(songList.size());
            shuffleList.add(num);//keep a list of the randomly generated songs
            musicServiceObject.setSong(num);
        }
        else{//set the next song to play in the list
            //pos is at the end, so set the next song to the first song
            if(musicServiceObject.getPosn() == songList.size() - 1){
                musicServiceObject.setSong(0);
            }
            else{
                musicServiceObject.setSong(musicServiceObject.getPosn() + 1);
            }
        }
    }

    //display the activity to add additional files, like recordings
    private void beginAudioActivity(){

        Intent i = new Intent(this, Audio.class);
        i.putExtra("filteredAudioList", audioList);
        startActivityForResult(i, 1);
    }

    //add the new songs to the default list
    private void addAdditionalSongs(ArrayList<Song> additionalSongs){

       // System.out.println("\n*\n*\n**********************BEFORE THE SIZE OF THE SONG LIST IS: " + songList.size());
        for(int i = 0; i < additionalSongs.size(); i++) {
            songList.add(additionalSongs.get(i));
        }
       // System.out.println("\n*\n*\n**********************AFTER THE SIZE OF THE SONG LIST IS: " + songList.size());
    }

    private void removeDuplicates(){//remove duplicate songs based on artist and title

        HashMap<String, Song> map = new HashMap<String, Song>();

        //add all of the song items to a map, to remove the duplicates
        for(int i = 0; i < songList.size(); i++) {
            if(!map.containsKey(songList.get(i).toString()))//toString is the artist and title
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

    private void addListToMap(ArrayList<Song> list, HashMap<String, Song> map){

        //add all of the song items to a map, to remove the duplicates
        for(int i = 0; i < list.size(); i++) {
            if(!map.containsKey(list.get(i).toString()))//toString is the artist and title
                map.put(list.get(i).toString(), list.get(i));
        }
    }

    private void removeNonsenseFromMap(ArrayList<Song> list, HashMap<String, Song> map){

        for(int i = 0; i < list.size(); i++){
            if(map.containsKey(list.get(i).toString())) {
                map.remove(list.get(i).toString());
            }
        }
    }

    private void printList(ArrayList<Song> list){//for debugging

        for(int i = 0; i < list.size(); i++)
            System.out.println(list.get(i).toString() + " Album: " + list.get(i).getAlbum());
    }

    //create and populate a list of Song objects for the passed in lists, such as
    //songList, ringtones, notifications, and everything
    private void createList(ContentResolver musicResolver, Uri musicUri, String[] musicContents,
                            String selection, ArrayList<Song> list){

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

    public static ArrayList<Song> getSongArray(){ //for use in SongTabFragment
        return songList;
    }

    public static ArrayList<Artist> getArtistArray() {
        return artistArray;
    }

    public static ArrayList<Album> getAlbumArray(){
        return albumArray;
    }

    public void songPicked(View view){ //executes when an item in the ListView is clicked. Defined in xml
        userAction = true;
        musicServiceObject.setSong(Integer.parseInt(view.getTag().toString()));
        musicServiceObject.playSong();
        //if(playbackPaused){
        //    setController();
        //    playbackPaused = false;
        //}
        //controller.show(0);
    }

    public void artistPicked(View view){
        int artistPosition = (int) view.getTag();
        ArtistTabFragment.showAlbums(artistArray.get(artistPosition).getAlbums());
    }

    public void albumPicked_artistTab(View view){
        int albumPosition = (int) view.getTag();
        ArtistTabFragment.showSongs(albumPosition);
    }

    public void songPicked_artistTab(View view){
        userAction = true;

        long song_id = (long) view.getTag();
        for(int i= 0; i < songList.size(); i++){
            if(song_id == songList.get(i).getID()){
                musicServiceObject.setSong(i);
            }
        }
        musicServiceObject.playSong();
    }

    public void backButton_artistTab(View view){
        ArtistTabFragment.backButtonPressed();
    }

    public static void setNowPlayingText(Song songNowPlaying){
        nowPlayingText.setText(songNowPlaying.getTitle() + " - " + songNowPlaying.getArtist());
    }

    // Methods below this point handle the MediaController
    private void setController(){
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

    protected void playNext(){ //called above by the Controller's onClick methods

        if(shuffleOn){
            shuffleSong();
            musicServiceObject.playSong();
        }
        else
            musicServiceObject.playNext();
        //if(playbackPaused){
        //    setController();
        //    playbackPaused = false;
        //}
        //controller.show(0);

    }

    protected void playPrev() {
        musicServiceObject.playPrev();
        //if(playbackPaused){
        //    setController();
        //    playbackPaused = false;
        //}
        //controller.show(0);
    }

    private void search(){
        //start the custom dialog box which is actually a new activity called SearchDialogBox
        Intent i = new Intent(getApplicationContext(), SearchDialogBox.class);
        i.putParcelableArrayListExtra("song_list", songList);
        startActivityForResult(i,1);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            /*if(requestCode == 3){//keep track of the timer values
                //the values for the timer
                String hourValue = data.getStringExtra("hourValue");
                String minuteValue = data.getStringExtra("minuteValue");

                //find the values from the timer if the user had set it earlier
                if (hourValue != null) { this.hourValue = hourValue; }
                if (minuteValue != null) { this.minuteValue = minuteValue; }
            }*/

          //  else {
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
            //}
        }

        if (requestCode == 1 && resultCode == 1 && data != null) {
            musicServiceObject.setSong(data.getIntExtra("searchChoice",-1));
            musicServiceObject.playSong();
            //if(playbackPaused){
            //    setController();
            //    playbackPaused = false;
            //}
            //controller.show(0);
        }
    }

    private ArrayList<Song> findAdditionalSongs(ArrayList<String> additionalListString){

        ArrayList<Song> additionalSongs = new ArrayList<Song>();

        for(int i = 0; i < additionalListString.size(); i++){
            for(int j = 0; j < audioList.size(); j++){
                if(audioList.get(j).toString().equals(additionalListString.get(i))){
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
        if(musicServiceObject!=null && musicBound && musicServiceObject.isPng()){ //make sure MusicService is instantiated and bound to this activity
            lastKnownDuration = musicServiceObject.getDur();
            return lastKnownDuration;

        } else return lastKnownDuration;
    }

    @Override
    public int getCurrentPosition() {
        if(musicServiceObject!=null && musicBound && musicServiceObject.isPng()){
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
        if(musicServiceObject!=null && musicBound){
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
        Intent i = new Intent(getApplicationContext(),CreatePlaylistActivity.class);
        i.putParcelableArrayListExtra("song_list",songList);
        startActivityForResult(i, 1);
    }

    public void playlistPicked(View v) {
        int playlistPosition = (int) v.getTag();
        PlaylistTabFragment.showPlaylistSongs(playlistPosition);
    }

    public void backButton_playlistTab (View v) {
        PlaylistTabFragment.backButtonPressed();
    }

    public void backButton_albumTab (View v){
        AlbumTabFragment.backButtonPressed();
    }

    public void albumPicked_albumTab (View v){
        int albumPosition = (int) v.getTag();
        AlbumTabFragment.showAlbumSongs(albumPosition);
    }

    public void songPicked_albumTab (View v){
        userAction = true;

        long song_id = (long) v.getTag();
        for(int i= 0; i < songList.size(); i++){
            if(song_id == songList.get(i).getID()){
                musicServiceObject.setSong(i);
            }
        }
        musicServiceObject.playSong();
    }
}