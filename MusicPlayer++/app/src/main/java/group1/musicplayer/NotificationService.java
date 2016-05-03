package group1.musicplayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.View;
import android.widget.RemoteViews;

import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * Created by shawn on 4/24/2016.
 */

public class NotificationService extends Service {

    private String songTitle, songAlbum, songArtist;
    private Long albumId = 0L;
    private Intent intent;
    private RemoteViews bigViews;
    Notification status;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null) {
            System.out.println("***notifcation tray intent is NULL FUCK!");
        }

        else {
            //pass info back to MAIN to know what button was pressed
            Intent broadcastNotification = new Intent();
            broadcastNotification.setAction("NOTIFICATIONS_READY");

             //receive info from the musicService when a song is changed
            registerReceiver(songChangedReceiver, new IntentFilter("SONG_CHANGED"));

            this.intent = intent;

            if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
                showNotification();
              //  Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
            }
            else if (intent.getAction().equals(Constants.ACTION.PREV_ACTION)) {
                broadcastNotification.putExtra("prevPressed", true);
            }
            else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION)) {
                broadcastNotification.putExtra("playPressed", true);
            }
            else if (intent.getAction().equals(Constants.ACTION.NEXT_ACTION)) {
                broadcastNotification.putExtra("nextPressed", true);
            }
            else if (intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION)) {
                broadcastNotification.putExtra("exitPressed", true);
                stopForeground(true);
                stopSelf();
            }
            getApplicationContext().sendBroadcast(broadcastNotification);
        }
        return START_STICKY;
    }

    private BroadcastReceiver songChangedReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent){

            if(intent.getAction().equals("SONG_CHANGED")){//grab song info that was changed in musicService
                System.out.println("SONG CHANDED im in service*****");
                songTitle = intent.getStringExtra("songTitle");
                songAlbum = intent.getStringExtra("album");
                songArtist = intent.getStringExtra("artist");
                albumId = intent.getLongExtra("albumId", 0L);
                showNotification();
            }
        }
    };

    private void showNotification() {

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
               getAlbumArt());

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Intent previousIntent = new Intent(this, NotificationService.class);
        previousIntent.setAction(Constants.ACTION.PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,previousIntent, 0);

        Intent playIntent = new Intent(this, NotificationService.class);
        playIntent.setAction(Constants.ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0, playIntent, 0);

        Intent nextIntent = new Intent(this, NotificationService.class);
        nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0, nextIntent, 0);

        Intent closeIntent = new Intent(this, NotificationService.class);
        closeIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
        PendingIntent pcloseIntent = PendingIntent.getService(this, 0, closeIntent, 0);

        bigViews.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);

        bigViews.setImageViewResource(R.id.status_bar_play, R.drawable.apollo_holo_dark_pause);

     //   bigViews.setTextViewTextSize(R.id.status_bar_track_name, TypedValue.COMPLEX_UNIT_SP, Float.parseFloat("20"));
        bigViews.setTextViewText(R.id.status_bar_track_name, songTitle);
        bigViews.setTextViewText(R.id.status_bar_artist_name, songArtist);
        bigViews.setTextViewText(R.id.status_bar_album_name, songAlbum);

        status = new Notification.Builder(this).build();
        status.contentView = bigViews;
        status.bigContentView = bigViews;
        status.flags = Notification.FLAG_ONGOING_EVENT;
        status.icon = R.mipmap.ic_launcher;
        status.contentIntent = pendingIntent;
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, status);
    }

    private Bitmap getAlbumArt(){

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
        }
        return bitmap;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}