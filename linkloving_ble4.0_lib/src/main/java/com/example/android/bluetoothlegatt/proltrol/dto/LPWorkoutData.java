package com.example.android.bluetoothlegatt.proltrol.dto;

/**
 * Created by Daniel.Xu on 2016/10/17.
 */

public class LPWorkoutData {
    public long getStarttime() {
        return starttime;
    }

    public void setStarttime(long starttime) {
        this.starttime = starttime;
    }

    public long getEndtime() {
        return endtime;
    }

    public void setEndtime(long endtime) {
        this.endtime = endtime;
    }

    public LPWorkoutData() {
    }

    public LPWorkoutData(long endtime, long starttime) {
        this.endtime = endtime;
        this.starttime = starttime;
    }

    @Override
    public String toString() {
        return "LPWorkoutData{" +
                "endtime=" + endtime +
                ", starttime=" + starttime +
                '}';
    }

    private long starttime ;
    private  long endtime ;

}
