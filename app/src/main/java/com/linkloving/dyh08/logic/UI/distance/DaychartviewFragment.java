package com.linkloving.dyh08.logic.UI.distance;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.linkloving.band.dto.SportRecord;
import com.linkloving.band.ui.DetailChartCountData;
import com.linkloving.dyh08.MyApplication;
import com.linkloving.dyh08.R;
import com.linkloving.dyh08.db.sport.UserDeviceRecord;
import com.linkloving.dyh08.ViewUtils.barchartview.DayBarChartView;
import com.linkloving.dyh08.logic.dto.UserEntity;
import com.linkloving.dyh08.utils.logUtils.MyLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by leo.wang on 2016/8/2.
 */
public class DaychartviewFragment extends Fragment {
    public static final String COLUMN_START_TIME="start_time";
    private static final String TAG = com.linkloving.dyh08.logic.UI.step.DaychartviewFragment.class.getSimpleName();
    private List<DayBarChartView.BarChartItemBean> dayhour = new ArrayList<>();
    private DayBarChartView dayBarChartView;
    private View view;
    private View anchorButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tw_calories_chartview_fragment, container, false);
        dayBarChartView = (DayBarChartView) view.findViewById(R.id.day_barchartView);
        dayBarChartView.setPopupViewType(DayBarChartView.DISTANCE__TYPE);
        anchorButton = view.findViewById(R.id.anchor_button);
        String checkDate = getArguments().getString("checkDate");
        new DaychartviewFragment.DayChartAsynck().execute(checkDate);
        return view;
    }

    private class DayChartAsynck extends AsyncTask<Object, Object, List<DayBarChartView.BarChartItemBean>> {

        private Date parse;
        private ArrayList<Date> stringTimeList;
        ArrayList<SportRecord> sportRecordArrayList = new ArrayList<SportRecord>();
        String user_id = MyApplication.getInstance(getActivity()).getLocalUserInfoProvider().getUser_id() + "";
        Date startdata ;
        Date enddata ;
        private  int stepTotal  = 0  ;
        private ArrayList<Integer> integers;

        @Override
        protected List<DayBarChartView.BarChartItemBean> doInBackground(Object... objects) {
            stringTimeList = new ArrayList<>();
            String time = objects[0].toString()+" 00:00:00.000";
            MyLog.e("time是",time);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            try {
                parse =  simpleDateFormat.parse(time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar instance = Calendar.getInstance();
            instance.setTime(parse);
            Date time1 = instance.getTime();
            stringTimeList.add(time1);
            for (int i = 0; i<24;i++)
            {
                instance.add(Calendar.HOUR_OF_DAY,1);
                Date time2 = instance.getTime();
                stringTimeList.add(time2);
            }
            stepTotal = 0 ;
            integers = new ArrayList<>();
            for (int i=0;i<24;i++){
                int stepnumber = 0 ;
                startdata = stringTimeList.get(i);
                enddata = stringTimeList.get(i+1);
                sportRecordArrayList = UserDeviceRecord.findHistoryChartwithHMS
                        (getActivity(), String.valueOf(user_id), startdata, enddata);
                if (sportRecordArrayList.size() == 0) {
                    stepnumber = 0;
                } else {
                    for (SportRecord sportRecordArray : sportRecordArrayList) {
                        if (sportRecordArray.getState().equals("1")||sportRecordArray.getState().equals("2")||sportRecordArray.getState().equals("3"))
                             stepnumber = Integer.parseInt(sportRecordArray.getDistance()) + stepnumber;
                    }
                    integers.add(i);
                }
                stepTotal = stepTotal+stepnumber ;
                DayBarChartView.BarChartItemBean barChartItemBean = new DayBarChartView.BarChartItemBean(Integer.toString(i+1),stepnumber);
                dayhour.add(barChartItemBean);
            }
            return dayhour;
        }
        protected void onPostExecute(final List<DayBarChartView.BarChartItemBean> dayhour) {
            super.onPostExecute(dayhour);
            DistanceActivity activity = (DistanceActivity) getActivity();
            String stepString = activity.step_number.getText().toString();
            Double stepActivityDouble = Double.parseDouble(stepString);
            int stepActivityInt = (int)(stepActivityDouble * 1000);
            int stepDiff = stepActivityInt - stepTotal;
//            不 大于0 来判断，
            if (stepDiff>2&&integers.size()>0){
                int stepDiffEvery = stepDiff / integers.size();
                for (Integer i : integers){
                    dayhour.get(i).itemValue =dayhour.get(i).itemValue+stepDiffEvery ;
                }
            }
            dayBarChartView.setItems(dayhour);
            dayBarChartView.setDialogListerer(new DayBarChartView.DialogListerer() {
                @Override
                public void showDialog(int i, int x, int y) {
                    String textShowTime = i+":00";
                    dayBarChartView.showPopupWindow(anchorButton,textShowTime, (int) dayhour.get(i).itemValue, x, y);
                }
                @Override
                public void dismissPopupWindow() {
                    dayBarChartView.popupWindow.dismiss();
                }
            });
        }
    }
}
