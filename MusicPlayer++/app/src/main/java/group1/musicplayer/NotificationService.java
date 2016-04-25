package group1.musicplayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Created by shawn on 4/24/2016.
 */

public class NotificationService extends Service {

    Notification status;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        System.out.println("in on starttt***");

        if (intent == null) {
            System.out.println("***notifcation tray intent is NULL FUCK!");
        }

        else {
            System.out.println("***notifcation tray intent is NOT null :D!");

            if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
                showNotification();
                Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();

            } else if (intent.getAction().equals(Constants.ACTION.PREV_ACTION)) {
                Toast.makeText(this, "Clicked Previous", Toast.LENGTH_SHORT).show();
                System.out.println("Clicked Previous");
            } else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION)) {
                Toast.makeText(this, "Clicked Play", Toast.LENGTH_SHORT).show();
                System.out.println("Clicked Play");
            } else if (intent.getAction().equals(Constants.ACTION.NEXT_ACTION)) {
                Toast.makeText(this, "Clicked Next", Toast.LENGTH_SHORT).show();
                System.out.println("Clicked Next");
            } else if (intent.getAction().equals(
                    Constants.ACTION.STOPFOREGROUND_ACTION)) {
                System.out.println("Received Stop Foreground Intent");
                Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show();
                stopForeground(true);
                stopSelf();
            }
        }


        return START_STICKY;
    }

    private void showNotification() {
        System.out.println("***im showNotificaiton");

        // Using RemoteViews to bind custom layouts into Notification
        RemoteViews views = new RemoteViews(getPackageName(),
                R.layout.status_bar);
        RemoteViews bigViews = new RemoteViews(getPackageName(),
                R.layout.status_bar_expanded);

        // showing default album image
        views.setViewVisibility(R.id.status_bar_icon, View.VISIBLE);
        views.setViewVisibility(R.id.status_bar_album_art, View.GONE);
        bigViews.setImageViewBitmap(R.id.status_bar_album_art,
                Constants.getDefaultAlbumArt(this));

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

        views.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);

        views.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);

        views.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent);

        views.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);

        views.setImageViewResource(R.id.status_bar_play,
                R.drawable.apollo_holo_dark_pause);
        bigViews.setImageViewResource(R.id.status_bar_play,
                R.drawable.apollo_holo_dark_pause);

        views.setTextViewText(R.id.status_bar_track_name, "Song Title");
        bigViews.setTextViewText(R.id.status_bar_track_name, "Song Title");

        views.setTextViewText(R.id.status_bar_artist_name, "Artist Name");
        bigViews.setTextViewText(R.id.status_bar_artist_name, "Artist Name");

        bigViews.setTextViewText(R.id.status_bar_album_name, "Album Name");

        status = new Notification.Builder(this).build();
        status.contentView = views;
        status.bigContentView = bigViews;
        status.flags = Notification.FLAG_ONGOING_EVENT;
        status.icon = R.drawable.ic_launcher;
        status.contentIntent = pendingIntent;
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, status);
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