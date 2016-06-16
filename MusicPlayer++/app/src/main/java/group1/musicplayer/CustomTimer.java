package group1.musicplayer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CustomTimer extends Activity {

    private Intent intent;
    private Button timerButton;
    private EditText hourValue, minuteValue;
    private Context appContext;
    private Intent service;
    private IntentFilter intentfilter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_timer);

        intent = getIntent();
        service =  new Intent(getBaseContext(), CustomTimerService.class);
        hourValue = (EditText) findViewById(R.id.hour_value);
        minuteValue = (EditText) findViewById(R.id.minute_value);
        timerButton = (Button) findViewById(R.id.start_timer_button);

        appContext = getApplicationContext();

        setUpHourValue();
        setUpMinuteValue();
        setUpTimerButton();

        intentfilter = new IntentFilter();
        intentfilter.addAction("LOCATION_REACHED");
        registerReceiver(broadcastReceiver, intentfilter);
    }

    //used to grab the timer values from CustomTimerService
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent){

            if(intent.getAction().equals("LOCATION_REACHED")){

                //grab hour and min from CustomeTimerService
                String hour = intent.getStringExtra("hourValue");
                String min = intent.getStringExtra("minuteValue");

                if(min != null && hour != null){

                    //add a 0 to the front of a single digit number, just to make it look nice
                    if(hour.length() == 1){ hour = "0" + hour;}
                    if(min.length() == 1){ min = "0" + min;}

                    //alert the user that the app is going to shut down soon if the timer is <= 3 sec
//                    if(Integer.parseInt(hour) == 0 && Integer.parseInt(min) <= 3){
//                        Toast.makeText(appContext, "Timer up. Existing MusicPlayer++...", Toast.LENGTH_SHORT).show();
//                    }
                    hourValue.setText(hour);
                    minuteValue.setText(min);
                }
                else{
                    System.out.println("hour or min is null*********************************");
                }
            }
        }
    };

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

                    Toast.makeText(getApplicationContext(), "Starting timer for " + hourTime + " hour(s) and " +
                            minuteTime + " minute(s).", Toast.LENGTH_SHORT).show();

                    service.putExtra("hour_time", hourTime);
                    service.putExtra("minute_time", minuteTime);
                    service.putExtra("timer_intent", intent);
                    startService(service);
                }
                else{//the user entered all zeros for the values so kill/reset the timer
                    Toast.makeText(appContext, "RESETING TIMER TO ZERO", Toast.LENGTH_SHORT).show();
                    stopService(service);//left off here, trying to kill service thread after timer reset
                }

                finish();//go back to the previous page
            }
        });
    }
    @Override
    protected void onStop(){

        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
        super.onStop();
    }
}