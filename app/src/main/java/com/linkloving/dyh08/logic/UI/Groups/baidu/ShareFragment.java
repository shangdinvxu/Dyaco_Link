package com.linkloving.dyh08.logic.UI.Groups.baidu;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.linkloving.dyh08.R;

/**
 * Created by Daniel.Xu on 2016/8/22.
 */
public class ShareFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tw_share_fragment, container, false);
        return view;
    }
}
