package com.linkloving.dyh08.logic.UI.calories;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.linkloving.band.dto.DaySynopic;
import com.linkloving.dyh08.MyApplication;
import com.linkloving.dyh08.R;
import com.linkloving.dyh08.ViewUtils.barchartview.BarChartView;
import com.linkloving.dyh08.ViewUtils.barchartview.WeekBarChartView;
import com.linkloving.dyh08.logic.dto.UserEntity;
import com.linkloving.dyh08.utils.CommonUtils;
import com.linkloving.dyh08.utils.DbDataUtils;
import com.linkloving.dyh08.utils.ToolKits;
import com.linkloving.dyh08.utils.logUtils.MyLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by leo.wang on 2016/8/2.
 */
public class WeekchartviewFragment extends Fragment {
    private static final String TAG = WeekchartviewFragment.class.getSimpleName();

    private WeekBarChartView barChartView;
    private WeekBarChartView.BarChartItemBean[] items;
    private View view;
    private List<WeekBarChartView.BarChartItemBean> week = new ArrayList<>();
    private UserEntity userEntity;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private View anchorButton;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tw_week_barchartview_fragment, container, false);
        userEntity = MyApplication.getInstance(getActivity()).getLocalUserInfoProvider();
        barChartView = (WeekBarChartView) view.findViewById(R.id.week_barchartView);
        barChartView.setPopupViewType(BarChartView.CALORIES_TYPE);
        anchorButton = view.findViewById(R.id.anchor_button);
        String date = getArguments().getString("date");
        MyLog.e(TAG,date);
        new WeekChartAsynckTask().execute(date);
        return view;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
    public  class WeekChartAsynckTask extends AsyncTask<Object, Object, List<DaySynopic>> {
        String startData; //周开始时间
        String endData;   //周结束时间
        Date endDatesdf;
        Date startDatesdf ;
        String startDateformat;
        private String endDateMd;
        private String endDateformat;
        private Date parseDate;

        Date mondayOfThisWeek =null;
        Date sundayofThisWeek=null ;

        protected List<DaySynopic> doInBackground(Object... params) {
            MyLog.e("params", params[0].toString());
            String dateStr = params[0].toString();
            SimpleDateFormat sdf =  new SimpleDateFormat("yyyy/MM/dd");
//            SimpleDateFormat sdfWithSlashYear = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
//            SimpleDateFormat sdfWithSlashNoYear = new SimpleDateFormat("MM/dd", Locale.getDefault());
//            取出来的天数加7天
            try {
                Date parse = sdf.parse(dateStr);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(parse);
                calendar.add(Calendar.DAY_OF_YEAR, 7);
                Date time = calendar.getTime();
                 dateStr = sdf.format(time);
            } catch (ParseException e) {
                e.printStackTrace();
            }


            try {
                mondayOfThisWeek = ToolKits.getFirstSundayOfThisWeek(sdf.parse(dateStr));
                sundayofThisWeek = ToolKits.getStaurdayofThisWeek(sdf.parse(dateStr));
            } catch (ParseException e) {
                e.printStackTrace();
            }
//            如果没有的话则说明选择的是今天的,手动给他一个日期
        if (mondayOfThisWeek==null){
            Date mondayOfThisWeekToday = ToolKits.getFirstSundayOfThisWeek(Calendar.getInstance().getTime());
            Date sundayofThisWeekToday = ToolKits.getStaurdayofThisWeek(Calendar.getInstance().getTime());
            mondayOfThisWeek =mondayOfThisWeekToday ;
            sundayofThisWeek = sundayofThisWeekToday ;
        }
            String monDayStr = sdf.format(mondayOfThisWeek);
            MyLog.e(TAG, monDayStr+"monDayStr");
            String sunDayStr = sdf.format(sundayofThisWeek);
//            String monday = sdfWithSlashYear.format(mondayOfThisWeek);
//            String sunday = sdfWithSlashNoYear.format(sundayofThisWeek);

            return DbDataUtils.findWeekDatainSql(getActivity(), sdf, sunDayStr, sunDayStr);
        }
        @Override
        protected void onPostExecute(List<DaySynopic> daySynopics) {
            super.onPostExecute(daySynopics);
            float step = 0;
            SimpleDateFormat sim = new SimpleDateFormat("MM/dd");
            for(DaySynopic daySynopic:daySynopics){

                Log.e(TAG, "----daySynopic---" + daySynopic.toString());
                //走路 分钟
                double walktime = CommonUtils.getScaledDoubleValue(Double.valueOf(daySynopic.getWork_duration()), 1);
                //跑步 分钟
                double runtime = CommonUtils.getScaledDoubleValue(Double.valueOf(daySynopic.getRun_duration()), 1);

                int walkCal = ToolKits.calculateCalories(Float.parseFloat(daySynopic.getWork_distance()), (int) walktime * 60, userEntity.getUserBase().getUser_weight());
                int runCal = ToolKits.calculateCalories(Float.parseFloat(daySynopic.getRun_distance()), (int) runtime * 60, userEntity.getUserBase().getUser_weight());
                int calValue = walkCal + runCal;
                String date = daySynopic.getData_date() ;
                try {
                    parseDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String format = sim.format(parseDate);
                Date date1 = new Date();
                String todayString = simpleDateFormat.format(date1);
                if (!todayString.equals(date)){
                    ToolKits toolKits = new ToolKits();
                    calValue = calValue + toolKits.getCalories(getActivity());
                }
                WeekBarChartView.BarChartItemBean barChartItemBean = new WeekBarChartView.BarChartItemBean(format, calValue);
                week.add(barChartItemBean);
            }
            int leftSize = 7 - week.size();
            for (int i = 0 ;i<leftSize;i++){
                Calendar instance = Calendar.getInstance();
                instance.setTime(parseDate);
                instance.add(Calendar.DAY_OF_YEAR,1+i);
                Date time = instance.getTime();
                String format = sim.format(time);
                week.add(new WeekBarChartView.BarChartItemBean(format,0));
            }
            barChartView.setItems(week);
            barChartView.setDialogListerer(new WeekBarChartView.DialogListerer() {
                @Override
                public void showDialog(int i, int x, int y) {
                    barChartView.showPopupWindow(anchorButton,week.get(i).itemType, (int) week.get(i).itemValue, x, y);
                }
                @Override
                public void dismissPopupWindow() {
                    barChartView.popupWindow.dismiss();
                }
            });
        }
    }
}
