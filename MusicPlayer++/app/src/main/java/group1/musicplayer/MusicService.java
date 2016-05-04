package group1.musicplayer;

import java.util.ArrayList;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.app.Service;

/**
 * Created by LukeJr on 9/26/2015.
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    private MediaPlayer player;
    private ArrayList<Song> songArray;
    private int songPosition;
    private final IBinder musicBind = new MusicBinder();
    private String songTitle = "";
    private String songArtist = "";
    private String songAlbum = "";
    private static final int NOTIFY_ID = 1;
    //private Notification.Builder builder;


    public void onCreate(){
        super.onCreate();

        songPosition = 0; //initialize position
        player = new MediaPlayer();
       // builder = new Notification.Builder(this);
        initMusicPlayer();
    }

    public void initMusicPlayer(){ //method to set the properties of the music player
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        //lets playback continue even when the device is idle
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
        //this MusicService class will act as the listener for playback completion, errors, etc
    }

    public void fillList(ArrayList<Song> passedArray){
        songArray = passedArray;
    }

    public class MusicBinder extends Binder { //Binder CLASS
        MusicService getService() {
            return MusicService.this;
        }
    }// end Binder class

    public void playSong(){
        player.reset();
        Song playSong = songArray.get(songPosition);
        MainActivity.setNowPlayingText(playSong);
        songTitle = playSong.getTitle();
        songArtist = playSong.getArtist();
        songAlbum = playSong.getAlbum();
        long currentSong = playSong.getID();
        Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currentSong);

        System.out.println("Passing song to notification serv***");
        //pass the current song info notifcation seriv
        Intent songChanged = new Intent("SONG_CHANGED");
        songChanged.putExtra("songTitle", songTitle);
        songChanged.putExtra("artist", songArtist);
        songChanged.putExtra("album", songAlbum);
        songChanged.putExtra("albumId", playSong.getAlbumId());
        getApplication().sendBroadcast(songChanged);

        try{
            player.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        player.prepareAsync();
    }

    public void playSongByID(long id){
        player.reset();
        Song playSong = songArray.get(songPosition);
        MainActivity.setNowPlayingText(playSong);
        songTitle = playSong.getTitle();
        songArtist = playSong.getArtist();
        long currentSong = playSong.getID();
        Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currentSong);

        try{
            player.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        player.prepareAsync();
    }

    @Override
    public void onPrepared(MediaPlayer mp){
        mp.start();

//        Intent notIntent = new Intent(this, MainActivity.class);
//        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendInt = PendingIntent.getActivity(this, 0, notIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        builder.setContentIntent(pendInt); //takes user back to MainActivity when selecting notification
//        builder.setSmallIcon(R.drawable.play);
//        builder.setTicker(songTitle);
//        builder.setOngoing(true);
//        builder.setContentTitle(songTitle);
//        builder.setContentText(songArtist);
//        builder.setSubText(songAlbum);
//
//        Notification not = builder.build();
//        startForeground(NOTIFY_ID, not);

        // Broadcast intent to activity to let it know the media player has been prepared
        Intent onPreparedIntent = new Intent("MEDIA_PLAYER_PREPARED");
        LocalBroadcastManager.getInstance(this).sendBroadcast(onPreparedIntent);
    }

    public void setSong(int songIndex){
        songPosition = songIndex;
    }

    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }

    @Override
    //executes when a track finishes playing, the user chooses a new track, or the user skips to the next/prev track
    public void onCompletion(MediaPlayer mp) {
        if(player.getCurrentPosition()>0){//If the track finished normally
            mp.reset();
            playNext(); //go to the next song
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    @Override
    public IBinder onBind(Intent intent){
        return musicBind;
    }

    @Override
    public void onDestroy(){
        stopForeground(true); //stops notification when the service is destroyed
    }

    public void playPrev(){
        songPosition--;
        if(songPosition < 0) songPosition=songArray.size()-1; //wrap around
        playSong();
    }

    public void playNext(){
            songPosition++;
            if (songPosition >= songArray.size()) songPosition = 0; //wrap around
            playSong();
    }

// Methods below parallel MediaController actions
    public int getPosn(){
        return player.getCurrentPosition();
    }

    public int getDur(){
        return player.getDuration();
    }

    public boolean isPng(){ //is playing
        return player.isPlaying();
    }

    public void pausePlayer(){
        player.pause();
    }

    public void seek(int posn){
        player.seekTo(posn);
    }

    public void go(){
        player.start();
    }

    public ArrayList<Song> getSongArray(){
        return songArray;
    }

    public int getSongPosition() { return songPosition;}

    public String getSongTitle() { return songTitle; }

    public String getSongArtist() { return songArtist; }

}