package com.linkloving.dyh08.utils;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Daniel.Xu on 2016/9/12.
 */
public class TimeUtils {
    private int i = 0 ;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1 :
                i = i+1;
//                simpleDateFormat.
            }
        }
    };
    private  Timer timer ;
    public void setStartTime()
    {
        TimerTask task = new TimerTask() {
            public void run() {
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        };
        timer = new Timer(true);
         timer.schedule(task,1000, 1000);
    }

    public void setStopTime(){
        timer.cancel();
    }
}

