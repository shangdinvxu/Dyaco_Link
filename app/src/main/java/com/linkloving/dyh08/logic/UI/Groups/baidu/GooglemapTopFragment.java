package com.linkloving.dyh08.logic.UI.Groups.baidu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.SupportMapFragment;
import com.linkloving.dyh08.R;

/**
 * Created by Daniel.Xu on 2017/4/7.
 */

public class GooglemapTopFragment extends SupportMapFragment {
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View view = layoutInflater.inflate(R.layout.fragment_google_share_map, null);
        return view;
    }
}
