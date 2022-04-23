package com.app.trackingapp;

import android.app.Activity;
import android.widget.TextView;
import java.util.TimerTask;

public class Timer {
    private java.util.Timer T;
    private TextView tv;
    private int seconds;
    private Activity activity;

    public Timer(Activity activity, TextView tv) {
        this.activity = activity;
        this.tv = tv;
    }

    private void startTimer() {
        T.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
               activity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        int hours = seconds / 3600;
                        int mins = (seconds % 3600) / 60;
                        int secs = seconds % 60;

                        String tf = String.format("%02d:%02d:%02d", hours, mins, secs);
                        tv.setText("Time " + tf);
                        seconds++;
                    }
                });
            }
        }, 1000, 1000);
    }

    public void initTimer() {
        this.T = new java.util.Timer();
        this.seconds = 0;
        startTimer();
    }

    public void stopTimer() {
        this.T.cancel();
    }
}
