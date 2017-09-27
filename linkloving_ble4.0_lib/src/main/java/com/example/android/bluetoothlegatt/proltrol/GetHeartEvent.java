package com.example.android.bluetoothlegatt.proltrol;

/**
 * Created by Daniel.Xu on 2017/5/22.
 */

public class GetHeartEvent {
    public GetHeartEvent(int total ,int left) {
        this.total = total;
        this.left = left ;
    }
    int total ;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    int  left ;
}
