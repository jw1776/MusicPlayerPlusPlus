package group1.musicplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CustomTimer extends Activity {

    private final Handler handler = new Handler();

    private Intent intent;
    private Button timerButton;
    private EditText hourValue, minuteValue;

    private long startTime = 0L;
    private long timeInMilliseconds = 0L;
    private long timeSwapBuff = 0L;
    private long updatedTime = 0L;
    private Intent service;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_timer);

        intent = getIntent();
        service =  new Intent(getBaseContext(), CustomTimerService.class);
        hourValue = (EditText) findViewById(R.id.hour_value);
        minuteValue = (EditText) findViewById(R.id.minute_value);
        timerButton = (Button) findViewById(R.id.start_timer_button);

        //avoid grabbing values from hour and minute if they were not passed for some reason
        if(intent.getStringExtra("hourValue") != null)
            hourValue.setText(getIntent().getStringExtra("hourValue"));
        if(intent.getStringExtra("minuteValue") != null)
            minuteValue.setText(getIntent().getStringExtra("minuteValue"));

        setUpHourValue();
        setUpMinuteValue();
        setUpTimerButton();
    }

    private void setUpHourValue(){

        hourValue.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                hourValue.setText(v.getText().toString());
                return false;
            }
        });
    }

    private void setUpMinuteValue(){

        minuteValue.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                minuteValue.setText(v.getText().toString());
                return false;
            }
        });
    }

    private void setUpTimerButton(){

        timerButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                String hourTime = hourValue.getText().toString();
                String minuteTime = minuteValue.getText().toString();

                //only start the timer if it is not zero
                if (Integer.parseInt(hourTime) != 0 || Integer.parseInt(minuteTime) != 0) {
                    System.out.println("the first edit values are " + hourTime + " : " + minuteTime);

                    service.putExtra("hour_time", hourTime);
                    service.putExtra("minute_time", minuteTime);
                    service.putExtra("timer_intent", intent);
                    startService(service);

                    //startTime = SystemClock.uptimeMillis();
                   // handler.postDelayed(updateTimerThread, 0);
                    System.out.println("passing service values back to customer timer ");
                    System.out.println(service.getStringExtra("minuteValue") + "*******************");
                  //  hourValue.setText(service.getStringExtra("hourValue"));
                 //   minuteValue.setText(service.getStringExtra("minuteValue"));
                    //send the time values back to main
                    //this does not constantly update the values, it only updates it once
                    intent.putExtra("hourValue", hourTime);
                    intent.putExtra("minuteValue", minuteTime);
                    setResult(RESULT_OK, intent);
                }
                else{
                    Toast.makeText(getApplicationContext(), "RESETING TIMER TO ZERO", Toast.LENGTH_SHORT).show();
                    stopService(service);
                }

                finish();//go back to the previous page
            }
        });
    }

    private Intent getOuterClassIntent(){
        return intent;
    }

//    private Runnable updateTimerThread = new Runnable() {
//
//        //update the time on the screen and constantly pass the values back to main
//        public void run() {
//
//            String hourTime = hourValue.getText().toString();
//            String minuteTime = minuteValue.getText().toString();
//
//            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
//
//            updatedTime = timeSwapBuff + timeInMilliseconds;
//
//            int secs = (int) (updatedTime / 1000);
//            int mins = secs / 60;
//            int hour = mins / 60;
//            secs = secs % 60;
//
//            //update the time on screen
//            hourValue.setText(Integer.toString(Integer.parseInt(hourTime) - hour));
//            minuteValue.setText(Integer.toString(Integer.parseInt(minuteTime) - secs));
//
//            System.out.println("updating time in runnable*************");
//            System.out.println(hourValue.getText().toString()+ " : " + minuteValue.getText().toString());
//
//            //pass the values back to main
//            getOuterClassIntent().putExtra("hourValue", hourTime);
//            getOuterClassIntent().putExtra("minuteValue", minuteTime);
//            setResult(RESULT_OK, getOuterClassIntent());
//
//            handler.postDelayed(this, 0);
//        }
//    };
}