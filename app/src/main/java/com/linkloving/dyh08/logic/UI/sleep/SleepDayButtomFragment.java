package com.linkloving.dyh08.logic.UI.sleep;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.linkloving.dyh08.R;

/**
 * Created by Daniel.Xu on 2016/8/24.
 */
public class SleepDayButtomFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tw_sleep_day_buttom, container, false);
        return view ;
    }
}
