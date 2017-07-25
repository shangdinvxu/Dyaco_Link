package com.linkloving.dyh08.logic.UI.sleep;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.linkloving.band.ui.BRDetailData;
import com.linkloving.dyh08.MyApplication;
import com.linkloving.dyh08.R;
import com.linkloving.dyh08.logic.UI.sleep.asyncktask.DaySleepAsynck;
import com.linkloving.dyh08.logic.UI.sleep.chartview.DetailChartControl;
import com.linkloving.dyh08.utils.manager.AsyncTaskManger;

import java.util.List;

/**
 * Created by leo.wang on 2016/8/2.
 */
public class DaychartviewFragment extends Fragment {
    private static final String TAG = DaychartviewFragment.class.getSimpleName();

    private DetailChartControl detailChartControl;
    private ProgressDialog progressDialog;

    /** 当前正在运行中的数据加载异步线程(放全局的目的是尽量控制当前只有一个在运行，防止用户恶意切换导致OOM) */
    private AsyncTask<Object, Object, List<BRDetailData>> dayDataAsync = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tw_day_barchartview_fragment, container, false);
        detailChartControl = (DetailChartControl)view.findViewById(R.id.activity_detailChartView1);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.summarizing_data));
        progressDialog.setCanceledOnTouchOutside(false);
        String date = getArguments().getString("date");
        DaySleepAsynck sleepAsynck = new DaySleepAsynck(getActivity(),detailChartControl,progressDialog, MyApplication.getInstance(getActivity()).getLocalUserInfoProvider());
        if (dayDataAsync != null){
            AsyncTaskManger.getAsyncTaskManger().removeAsyncTask(sleepAsynck, true);
        }
        AsyncTaskManger.getAsyncTaskManger().addAsyncTask(dayDataAsync = sleepAsynck);
        sleepAsynck.execute(date);
        return view ;

    }
}
