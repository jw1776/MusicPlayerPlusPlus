package group1.musicplayer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import android.content.ContentUris;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.app.Service;
import android.app.Notification;
import android.app.PendingIntent;
import android.view.View;
import android.widget.RemoteViews;


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
    private Notification.Builder builder;

    private RemoteViews bigViews;
    Notification status;

    public void onCreate(){
        super.onCreate();

        songPosition = 0; //initialize position
        player = new MediaPlayer();
        builder = new Notification.Builder(this);
      //  setupBuilderActions();
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

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//
//        if (intent == null) {
//            System.out.println("***notifcation tray intent is NULL FUCK!");
//        }
//
//        else {
//            //pass info back to MAIN to know what button was pressed
//            Intent broadcastNotification = new Intent();
//            broadcastNotification.setAction("NOTIFICATIONS_READY");
//
//            //receive info from the musicService when a song is changed
//          //  registerReceiver(songChangedReceiver, new IntentFilter("SONG_CHANGED"));
//
//           // this.intent = intent;
//
//            if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
//                showNotification(-1);
//                //  Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
//            }
//            else if (intent.getAction().equals(Constants.ACTION.PREV_ACTION)) {
//                broadcastNotification.putExtra("prevPressed", true);
//            }
//            else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION)) {
//                broadcastNotification.putExtra("playPressed", true);
//            }
//            else if (intent.getAction().equals(Constants.ACTION.NEXT_ACTION)) {
//                broadcastNotification.putExtra("nextPressed", true);
//            }
//            else if (intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION)) {
//                broadcastNotification.putExtra("exitPressed", true);
//                stopForeground(true);
//                stopSelf();
//            }
//            getApplicationContext().sendBroadcast(broadcastNotification);
//        }
//        return START_STICKY;
//    }

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

        System.out.println("Passing song back to main***");
        //pass the current song info back to main
        Intent songChanged = new Intent("SONG_CHANGED");
        songChanged.putExtra("songTitle", songTitle);
        songChanged.putExtra("artist", songArtist);
        songChanged.putExtra("album", playSong.getAlbum());
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
        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendInt); //takes user back to MainActivity when selecting notification
        builder.setSmallIcon(R.drawable.play);
        builder.setTicker(songTitle);
        builder.setOngoing(true);
        builder.setContentTitle(songTitle);
        builder.setContentText(songArtist);
        builder.setSubText(songAlbum);

        Notification not = builder.build();
        startForeground(NOTIFY_ID, not);

        // Broadcast intent to activity to let it know the media player has been prepared
        Intent onPreparedIntent = new Intent("MEDIA_PLAYER_PREPARED");
        LocalBroadcastManager.getInstance(this).sendBroadcast(onPreparedIntent);
    }

    private void showNotification(long albumId) {

        // Using RemoteViews to bind custom layouts into Notification
//        RemoteViews views = new RemoteViews(getPackageName(),
//                R.layout.status_bar);
        bigViews = new RemoteViews(getPackageName(), R.layout.status_bar_expanded);

        // showing default album image
//        views.setViewVisibility(R.id.status_bar_icon, View.VISIBLE);
//        views.setViewVisibility(R.id.status_bar_album_art, View.GONE);
        //   bigViews.setViewVisibility(R.id.status_bar_icon, View.VISIBLE);
        bigViews.setViewVisibility(R.id.status_bar_album_art, View.VISIBLE);
        bigViews.setImageViewBitmap(R.id.status_bar_album_art,
                getAlbumArt(albumId));

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Intent previousIntent = new Intent(this, NotificationService.class);
        previousIntent.setAction(Constants.ACTION.PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
                previousIntent, 0);

        Intent playIntent = new Intent(this, NotificationService.class);
        playIntent.setAction(Constants.ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                playIntent, 0);

        Intent nextIntent = new Intent(this, NotificationService.class);
        nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0,
                nextIntent, 0);

        Intent closeIntent = new Intent(this, NotificationService.class);
        closeIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
        PendingIntent pcloseIntent = PendingIntent.getService(this, 0,
                closeIntent, 0);

        //  views.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);

        // views.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);

        //  views.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent);

        // views.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);

        //  views.setImageViewResource(R.id.status_bar_play,
        //         R.drawable.apollo_holo_dark_pause);
        bigViews.setImageViewResource(R.id.status_bar_play,
                R.drawable.apollo_holo_dark_pause);



        // views.setTextViewText(R.id.status_bar_artist_name, "Artist Name");
        //   bigViews.setTextViewTextSize(R.id.status_bar_track_name, TypedValue.COMPLEX_UNIT_SP, Float.parseFloat("20"));
        bigViews.setTextViewText(R.id.status_bar_track_name, songTitle);
        bigViews.setTextViewText(R.id.status_bar_artist_name, songArtist);
        bigViews.setTextViewText(R.id.status_bar_album_name, songAlbum);

        System.out.printf("***%s - %s - %s\n", songTitle, songArtist, songAlbum);
        status = new Notification.Builder(this).build();
        // status.contentView = views;
        status.contentView = bigViews;
        status.bigContentView = bigViews;
        status.flags = Notification.FLAG_ONGOING_EVENT;
        status.icon = R.mipmap.ic_launcher;
        status.contentIntent = pendingIntent;
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, status);
    }

    private Bitmap getAlbumArt(long albumId){

        Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri albumArtUri = ContentUris.withAppendedId(artworkUri, albumId);

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), albumArtUri);
        } catch (FileNotFoundException exception) {
            System.out.println("***ALBUM ART NOT FOUND. CREATING DEFAULT...");
            bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.default_album);
        } catch (IOException e) {
            e.printStackTrace();
            bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.default_album);
        }
        return bitmap;
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
