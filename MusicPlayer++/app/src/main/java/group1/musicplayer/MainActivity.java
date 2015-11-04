package group1.musicplayer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import android.net.Uri;
import android.content.ContentResolver;
import android.database.Cursor;
import android.widget.EditText;
import android.widget.ListView;
import android.os.IBinder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.View;
import group1.musicplayer.MusicService.MusicBinder;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.Toast;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

public class MainActivity extends Activity implements MediaPlayerControl {

    private static ArrayList<Song> songList, audioList;
    private static ArrayList<Playlist> playlistArray;
    private ArrayList<Song> searchList;
    private ArrayList<Integer> searchIndex;
    private boolean searching;
    private String searchTerm;
    private ListView songView;
    private MusicService musicServiceObject;
    private Intent playIntent;
    private boolean musicBound = false; //Keeps track of whether or not MainActivity is bound to the MusicService
    private MusicController controller;
    private boolean paused = false; //true if activity is in onPause state
    private boolean playbackPaused = false;
    private AlertDialog.Builder dialogBuilder;

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
        getSongList(); //fill the array with all songs
        removeDuplicates();
        sortSongsByTitle();

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(false); //hide icon
        actionBar.setDisplayShowTitleEnabled(true); //show title
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        //Create and set the names for each tab
        songTab = actionBar.newTab().setText("Songs");
        artistTab = actionBar.newTab().setText("Artists");
        albumTab = actionBar.newTab().setText("Albums");
        playlistTab = actionBar.newTab().setText("Playlists");

        //Set tab listeners
        songTab.setTabListener(new TabListener(songTabFragment));
        artistTab.setTabListener(new TabListener(artistTabFragment));
        albumTab.setTabListener(new TabListener(albumTabFragment));
        playlistTab.setTabListener(new TabListener(playlistTabFragment));

        //Add tabs to action bar
        actionBar.addTab(songTab);
        actionBar.addTab(artistTab);
        actionBar.addTab(albumTab);
        actionBar.addTab(playlistTab);

        setController(); //initializes the MediaController
    }

    private void sortSongsByTitle(){

        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });

        //   SongAdapter theAdapter = new SongAdapter(this, songList);
        //    songView.setAdapter(theAdapter); //pass the ListView object the appropriate adapter
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
        if(paused){
            setController(); //needed for controller to display upon returning to the app
            paused = false;
        }
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
                break;
            case R.id.action_end:
                stopService(playIntent);
                musicServiceObject = null;
                System.exit(0);
                break;
            case R.id.action_search:
                this.search();
                break;
            case R.id.folder_button://the user wants to browse for additional audio files
                beginAudioActivity();
                break;

        }//end switch
        return super.onOptionsItemSelected(item);
    }

    private void beginAudioActivity(){

        Intent i = new Intent(this, Audio.class);
        i.putExtra("filteredAudioList", audioList);
        startActivityForResult(i, 1);
    }

    //add the new songs to the default list
    private void addAdditionalSongs(ArrayList<Song> additionalSongs){

        for(int i = 0; i < additionalSongs.size(); i++)
            songList.add(additionalSongs.get(i));
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

        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] musicContents = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION
        };

        createList(musicResolver, musicUri, musicContents, " != 0", songList);
        createList(musicResolver, musicUri, musicContents, " == 0", audioList);
        //left off here: add the audioList to the audio serach thing

        System.out.println("THE ENTIRE SONG LIST: ");
        for(int i = 0; i < songList.size(); i++)
            System.out.println(songList.get(i).toString());
        System.out.println("THE ENTIRE audio LIST: ");
        for(int i = 0; i < audioList.size(); i++)
            System.out.println(audioList.get(i).toString());

    }// end getSongList() function

    //create a list of Song objects for actual song files vs audio files (voice recordings and stuff)
    private void createList(ContentResolver musicResolver, Uri musicUri, String[] musicContents,
                            String type, ArrayList<Song> list){

        //the type determines if its a song or not. if != 0 its an actual song, if == 0 its not a song
        String selection = MediaStore.Audio.Media.IS_MUSIC + type;

        if(!type.equals(" == 0")) {//its not an actual music file
            musicContents = null;
        }

        Cursor musicCursor = musicResolver.query(
                musicUri,
                musicContents,
                selection,//selection for music only, null for everything
                null,
                null);

        if (musicCursor != null && musicCursor.moveToFirst()) {

            int titleColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST);

            do { //add the Song objects to the appropriate list: songList or audioList
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                list.add(new Song(thisId, thisTitle, thisArtist));
            }
            while (musicCursor.moveToNext()); //while there are still items left
        }
    }


    public static ArrayList<Song> getSongArray(){ //for use in SongTabFragment
        return songList;
    }

    public void songPicked(View view){ //executes when an item in the ListView is clicked. Defined in xml
        musicServiceObject.setSong(Integer.parseInt(view.getTag().toString()));
        musicServiceObject.playSong();
        if(playbackPaused){
            setController();
            playbackPaused = false;
        }
        controller.show(0);
    }

    public void artistPicked(View view){

    }
    // Methods below this point handle the MediaController
    private void setController(){
        controller = new MusicController(this);

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
        controller.setAnchorView(findViewById(R.id.activity_main));
        controller.setEnabled(true);
    }

    protected void playNext(){ //called above by the Controller's onClick methods
        musicServiceObject.playNext();
        if(playbackPaused){
            setController();
            playbackPaused = false;
        }
        controller.show(0);
    }

    protected void playPrev() {
        musicServiceObject.playPrev();
        if(playbackPaused){
            setController();
            playbackPaused = false;
        }
        controller.show(0);
    }

    private void search(){
        searchList = new ArrayList<Song>();
        searchIndex = new ArrayList<Integer>();
        dialogBuilder = new AlertDialog.Builder(this);
        searchTerm = "";
        searching = false;
        final EditText textInput = new EditText(this);
        dialogBuilder.setTitle("Find a Song");
        dialogBuilder.setMessage("Artist, Song, etc.");
        dialogBuilder.setView(textInput);
        dialogBuilder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                searchTerm += textInput.getText().toString();
                for(int i = 0; i<songList.size();i++){
                    Song current = songList.get(i);
                    if(current.getTitle().toLowerCase().contains(searchTerm.toLowerCase()) ||
                            current.getArtist().toLowerCase().contains(searchTerm.toLowerCase())){
                        Log.d("stuff", current.getTitle());
                        searchList.add(current);
                        searchIndex.add(i);
                    }
                }
                Intent i = new Intent(getApplicationContext(),Search.class);
                i.putParcelableArrayListExtra("search_results",searchList);
                i.putIntegerArrayListExtra("search_index",searchIndex);
                startActivityForResult(i, 1);
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                searchTerm = "";
            }
        });

        AlertDialog searchBox = dialogBuilder.create();
        searchBox.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            ArrayList<String> audioListString = data.getStringArrayListExtra("additionalSongs");

            //find the Song objects that where added
            ArrayList<Song> additionalSongs = findAdditionalSongs(audioListString);
            if(additionalSongs != null) {
                addAdditionalSongs(additionalSongs);//add the additional songs to the main list
                sortSongsByTitle();
              //  System.out.println("\n.\n.\n.UPDATING THE SCREEEEEEEEEEEEEEEEN");
                songTabFragment = new SongTabFragment();//update the list on the screen
                songTab.setTabListener(new TabListener(songTabFragment));
            }
        }

        if (requestCode == 1 && resultCode == 1 && data != null) {
            musicServiceObject.setSong(data.getIntExtra("searchChoice",-1));
            musicServiceObject.playSong();
            if(playbackPaused){
                setController();
                playbackPaused = false;
            }
            controller.show(0);
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
            return musicServiceObject.getDur();
        } else return 0;
    }

    @Override
    public int getCurrentPosition() {
        if(musicServiceObject!=null && musicBound && musicServiceObject.isPng()){
            return musicServiceObject.getPosn();
        } else return 0;
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

}


