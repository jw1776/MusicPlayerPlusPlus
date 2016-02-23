package group1.musicplayer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Handler.Callback;

import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by shawn on 2/21/2016.
 */
public class CustomTimerService extends Service {

    private final Handler handler = new Handler();
    private Intent timerIntent;
    private Context context;
//    private long startTime = 0L;
//    private long timeInMilliseconds = 0L;
//    private long timeSwapBuff = 0L;
//    private long updatedTime = 0L;

    private int hourTime = 0, minuteTime = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.

        context = this;
        timerIntent = intent;

        if(timerIntent != null){

            if(timerIntent.getStringExtra("hour_time") != null) {
                hourTime = Integer.parseInt(timerIntent.getStringExtra("hour_time"));
            }
            if(timerIntent.getStringExtra("minute_time") != null){
                minuteTime = Integer.parseInt(timerIntent.getStringExtra("minute_time"));
            }

         //   startTime = SystemClock.uptimeMillis();
            Toast.makeText(this, "Starting timer for " + hourTime + " hour(s) and " +
                    + minuteTime + " minute(s).", Toast.LENGTH_SHORT).show();
            handler.postDelayed(updateTimerThread, 0);
        }
        return START_STICKY;
    }

    private Intent getOuterClassIntent(){
        return timerIntent;
    }

    private Runnable updateTimerThread = new Runnable() {

        //update the time on the screen and constantly pass the values back to main
        public void run() {

            new CountDownTimer(((hourTime * 60 + minuteTime) * 1000), 1000) {//total time, interval

                //MINUTES FOR NOW ACT LIKE SECONDS AND HOUR ACTS LIKE MINUTES, JUST FOR TESTING****

                private long minTimer = minuteTime * 1000;
                private long hourTimer = hourTime * 1000;

                public void onTick(long millisUntilFinished) {
                     //   System.out.println(hourTime + " : " + minuteTime);
                    System.out.println(millisUntilFinished + " left****************");

                    //update the amount of minutes when it reaches 0 but there are still hours left
//                    if(minTimer == 0 && hourTimer != 0){
//                        minTimer = 5900;
//                        hourTimer --;
//                    }
//                    else{
//                        minTimer--;
//                    }
                    if(millisUntilFinished <= 3000 && millisUntilFinished >= 1000){
//                        new Runnable(){
//                            @Override
//                            public void run() {//doesnt show up, prob cuz problem with main thread or something
//                                Toast.makeText(context, "Timer up. Existing MusicPlayer++...", Toast.LENGTH_LONG).show();
//                            }
//                        };
                    }
        //            System.out.println(hourTime + " : " + minuteTime);

                    //pass the values back to main
                    getOuterClassIntent().putExtra("hourValue", Long.toString(millisUntilFinished));
                    getOuterClassIntent().putExtra("minuteValue", Integer.toString(minuteTime));
                }

                public void onFinish() {
                    //not showing toast....
                   // Toast.makeText(context, "Timer up. Existing MusicPlayer++...", Toast.LENGTH_LONG).show();
                    handler.removeCallbacks(updateTimerThread);
                    System.exit(0);//this is not that great, in the end call onFinish on all of the activities
                }
            }.start();
            //update the time on screen
//           hourTime = Integer.toString(Integer.parseInt(hourTime) - hour);
//           minuteTime = Integer.toString(Integer.parseInt(minuteTime) - min);
//
//            System.out.println("updating time in runnable*************");
//            System.out.println(hourTime + " : " + minuteTime);
//
//            //pass the values back to main
//            getOuterClassIntent().putExtra("hourValue", hourTime);
//            getOuterClassIntent().putExtra("minuteValue", minuteTime);

            handler.postDelayed(this, 0);
        }
    };

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
        super.onDestroy();

    }
}

/*

    private Runnable updateTimerThread = new Runnable() {

        //update the time on the screen and constantly pass the values back to main
        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            int hour = mins / 60;
            secs = secs % 60;

            //update the time on screen
            hourTime = Integer.toString(Integer.parseInt(hourTime) - hour);
            minuteTime = Integer.toString(Integer.parseInt(minuteTime) - secs);

            System.out.println("updating time in runnable*************");
            System.out.println(hourTime + " : " + minuteTime);

            //pass the values back to main
            getOuterClassIntent().putExtra("hourValue", hourTime);
            getOuterClassIntent().putExtra("minuteValue", minuteTime);

            handler.postDelayed(this, 0);
        }
    };*
 */
