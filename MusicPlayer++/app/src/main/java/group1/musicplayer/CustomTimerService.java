package group1.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;

import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.widget.Toast;

/**
 * Created by shawn on 2/21/2016.
 */
public class CustomTimerService extends Service {

 //   private final Handler handler = new Handler();
    private int hourTime = 0, minuteTime = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //grab the values from CustomTimer and display it and start the count down
    @Override
    public int onStartCommand(Intent timerIntent, int flags, int startId) {

        if (timerIntent != null) {

            if (timerIntent.getStringExtra("hour_time") != null) {
                hourTime = Integer.parseInt(timerIntent.getStringExtra("hour_time"));
            }
            if (timerIntent.getStringExtra("minute_time") != null) {
                minuteTime = Integer.parseInt(timerIntent.getStringExtra("minute_time"));
            }

            //handler.postDelayed(updateTimerThread, 0);//start the thread

            //update the time on the screen and constantly pass the values back to CustomTimer
            new MoreAccurateTimer(((hourTime * 60 + minuteTime) * 1000), 1000) {//total time, interval

                //MINUTES FOR NOW ACT LIKE SECONDS AND HOUR ACTS LIKE MINUTES, JUST FOR TESTING****

                public void onTick(long millisUntilFinished) {

                    System.out.println(millisUntilFinished + " left****************");

                    long min = (millisUntilFinished / 1000) % 60;//this is actually sec, but used for testing for now...
                    long hour = (millisUntilFinished / 1000) / 60;

                    //constantly pass the minute and hour values back to CustomTimer
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction("LOCATION_REACHED");
                    broadcastIntent.putExtra("minuteValue", Long.toString(min));
                    broadcastIntent.putExtra("hourValue", Long.toString(hour));
                    getApplicationContext().sendBroadcast(broadcastIntent);
                }

                public void onFinish() {
                    System.exit(0);//this is not that great, in the end call onFinish on all of the activities
                }
            }.start();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
        //handler.removeCallbacks(updateTimerThread);
        super.onDestroy();
    }
}

//source for following code:
//http://stackoverflow.com/questions/12762272/android-countdowntimer-additional-milliseconds-delay-between-ticks
// http://grepcode.com/file_/repository.grepcode.com/java/ext/com.google.android/android/4.1.1_r1/android/os/CountDownTimer.java/?v=source
abstract class MoreAccurateTimer  {

    /**
     * Millis since epoch when alarm should stop.
     */
    private final long mMillisInFuture;

    /**
     * The interval in millis that the user receives callbacks
     */
    private final long mCountdownInterval;

    private long mStopTimeInFuture;

    private long mNextTime;

    /**
     * @param millisInFuture The number of millis in the future from the call
     *   to {@link #start()} until the countdown is done and {@link #onFinish()}
     *   is called.
     * @param countDownInterval The interval along the way to receive
     *   {@link #onTick(long)} callbacks.
     */
    public MoreAccurateTimer(long millisInFuture, long countDownInterval) {
        mMillisInFuture = millisInFuture;
        mCountdownInterval = countDownInterval;
    }

    /**
     * Cancel the countdown.
     */
    public final void cancel() {
        mHandler.removeMessages(MSG);
    }

    /**
     * Start the countdown.
     */
    public synchronized final MoreAccurateTimer start() {
        if (mMillisInFuture <= 0) {
            onFinish();
            return this;
        }

        mNextTime = SystemClock.uptimeMillis();
        mStopTimeInFuture = mNextTime + mMillisInFuture;

        mNextTime += mCountdownInterval;
        mHandler.sendMessageAtTime(mHandler.obtainMessage(MSG), mNextTime);
        return this;
    }


    /**
     * Callback fired on regular interval.
     * @param millisUntilFinished The amount of time until finished.
     */
    public abstract void onTick(long millisUntilFinished);

    /**
     * Callback fired when the time is up.
     */
    public abstract void onFinish();

    private static final int MSG = 1;

    // handles counting down
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            synchronized (MoreAccurateTimer.this) {
                final long millisLeft = mStopTimeInFuture - SystemClock.uptimeMillis();

                if (millisLeft <= 0) {
                    onFinish();
                } else {
                    onTick(millisLeft);

                    // Calculate next tick by adding the countdown interval from the original start time
                    // If user's onTick() took too long, skip the intervals that were already missed
                    long currentTime = SystemClock.uptimeMillis();
                    do {
                        mNextTime += mCountdownInterval;
                    } while (currentTime > mNextTime);

                    // Make sure this interval doesn't exceed the stop time
                    if(mNextTime < mStopTimeInFuture)
                        sendMessageAtTime(obtainMessage(MSG), mNextTime);
                    else
                        sendMessageAtTime(obtainMessage(MSG), mStopTimeInFuture);
                }
            }
        }
    };
}