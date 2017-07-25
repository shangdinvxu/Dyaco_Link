package com.linkloving.dyh08.logic.UI.calories;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.linkloving.band.dto.SportRecord;
import com.linkloving.band.sleep.DLPSportData;
import com.linkloving.band.sleep.SleepDataHelper;
import com.linkloving.band.ui.DatasProcessHelper;
import com.linkloving.band.ui.DetailChartCountData;
import com.linkloving.dyh08.MyApplication;
import com.linkloving.dyh08.R;
import com.linkloving.dyh08.db.sport.UserDeviceRecord;
import com.linkloving.dyh08.ViewUtils.barchartview.DayBarChartView;
import com.linkloving.dyh08.logic.dto.UserEntity;
import com.linkloving.dyh08.utils.ToolKits;
import com.linkloving.dyh08.utils.logUtils.MyLog;
import com.linkloving.utils.TimeZoneHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by leo.wang on 2016/8/2.
 */
public class DaychartviewFragment extends Fragment {
    public static final String COLUMN_START_TIME = "start_time";
    private static final String TAG = DaychartviewFragment.class.getSimpleName();
    private List<DayBarChartView.BarChartItemBean> dayhour = new ArrayList<>();
    private DayBarChartView dayBarChartView;
    private View view;
    private View anchorButton;
    int calorieseveryday;
    private int calorieseveryHour;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tw_calories_chartview_fragment, container, false);
        ToolKits toolKits = new ToolKits();
        this.calorieseveryday = toolKits.getCalories(getActivity());
        MyLog.e("calories", calorieseveryday + "calories");
        dayBarChartView = (DayBarChartView) view.findViewById(R.id.day_barchartView);
        dayBarChartView.setPopupViewType(DayBarChartView.CALORIES);
        anchorButton = view.findViewById(R.id.anchor_button);
        String checkDate = getArguments().getString("checkDate");
        new DayChartAsynck().execute(checkDate);
        return view;
    }

    private class DayChartAsynck extends AsyncTask<Object, Object, List<DayBarChartView.BarChartItemBean>> {

        private Date parse;
        private ArrayList<Date> stringTimeList;
        ArrayList<SportRecord> sportRecordArrayList = new ArrayList<SportRecord>();
        String user_id = MyApplication.getInstance(getActivity()).getLocalUserInfoProvider().getUser_id() + "";
        Date startdata;
        Date enddata;
        private DetailChartCountData count;
        UserEntity userEntity = MyApplication.getInstance(getActivity()).getLocalUserInfoProvider();
        private SimpleDateFormat hh = new SimpleDateFormat("HH", Locale.getDefault());
        private int caloriesTotal = 0 ;
        private int howNow;

        @Override
        protected List<DayBarChartView.BarChartItemBean> doInBackground(Object... objects) {
            calorieseveryHour = calorieseveryday / 24;
            MyLog.e("calories", calorieseveryHour + "calorieseveryHour");
            stringTimeList = new ArrayList<>();
            String time = objects[0].toString() + " 00:00:00.000";
            MyLog.e("time是", time);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            try {
                parse = simpleDateFormat.parse(time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar instance = Calendar.getInstance();
            instance.setTime(parse);
            Date time1 = instance.getTime();
            stringTimeList.add(time1);
            for (int i = 0; i < 24; i++) {
                instance.add(Calendar.HOUR_OF_DAY, 1);
                Date time2 = instance.getTime();
                stringTimeList.add(time2);
            }
            caloriesTotal=0;
            for (int i = 0; i < 24; i++) {
                int calValue = 0;
                
                startdata = stringTimeList.get(i);
                enddata = stringTimeList.get(i + 1);
                sportRecordArrayList = UserDeviceRecord.findHistoryChartwithHMS
                        (getActivity(), String.valueOf(user_id), startdata, enddata);
                if (sportRecordArrayList.size() == 0) {
                    calValue = 0;
                } else {
                    for (SportRecord sportRecordArray : sportRecordArrayList) {
                        if (sportRecordArray.getState().equals("1") || sportRecordArray.getState().equals("2") || sportRecordArray.getState().equals("3"))
                            calValue = ToolKits.calculateCalories(Integer.parseInt(sportRecordArray.getDistance()),
                                    Integer.parseInt(sportRecordArray.getDuration()),
                                    userEntity.getUserBase().getUser_weight()) + calValue;
                    }
                }
                List<DLPSportData> srs = SleepDataHelper.querySleepDatas2(sportRecordArrayList);
                String startDateLocal = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(time1);
                try {
                    count = DatasProcessHelper.countSportData(srs, startDateLocal);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                int walkCal = ToolKits.calculateCalories(Float.parseFloat(String.valueOf(count.walking_distance)),
                        (int) count.walking_duration * 60, userEntity.getUserBase().getUser_weight());
                int runCal = ToolKits.calculateCalories(Float.parseFloat(String.valueOf(count.runing_distance)), (int) count.runing_duation * 60, userEntity.getUserBase().getUser_weight());

                /*********************/
                Date dateToday = new Date();

                if (ToolKits.compareDate(dateToday, parse)) {
                    String dateTodayFormat = sdf.format(dateToday);
                    String dateFormat = sdf.format(parse);
                    String[] dateFormatSplit = dateTodayFormat.split(" ");
                    String[] dateSplit = dateFormat.split(" ");
                    MyLog.e(TAG, "1---" + dateSplit[0] + "2--" + dateFormatSplit[0]);
                    if (dateSplit[0].equals(dateFormatSplit[0])) {
                        MyLog.e(TAG, "日期等于今天");
                        String HH = hh.format(dateToday);
                        howNow = Integer.parseInt(HH);
                        if (i  < howNow) {
                            MyLog.e(TAG, "小时之前");
                            calValue = calorieseveryHour + walkCal + runCal;
                        } else {
                            calValue = walkCal + runCal;
                            MyLog.e(TAG, "小时之后");
                        }
                    } else {
                        MyLog.e(TAG, "日期在今天之前");
                        calValue = walkCal + runCal + calorieseveryHour;
                    }
                } else {
                    MyLog.e(TAG, "日期在今天之后");
                }
                /*********************/
                MyLog.e(TAG, "calValue" + calValue);
                DayBarChartView.BarChartItemBean barChartItemBean = new DayBarChartView.BarChartItemBean(Integer.toString(i + 1), calValue);
                dayhour.add(barChartItemBean);
                caloriesTotal=caloriesTotal+calValue ;
            }
            return dayhour;
        }

        protected void onPostExecute(final List<DayBarChartView.BarChartItemBean> dayhour) {
            super.onPostExecute(dayhour);
            CaloriesActivity activity = (CaloriesActivity) getActivity();
            CharSequence text = activity.step_number.getText();
            int activityStep = Integer.parseInt(text.toString());
            int stepDiff = activityStep-caloriesTotal;
            float itemValue = dayhour.get(howNow).itemValue;
            dayhour.get(howNow).itemValue=itemValue+stepDiff;
            dayBarChartView.setItems(dayhour);
            dayBarChartView.setDialogListerer(new DayBarChartView.DialogListerer() {
                @Override
                public void showDialog(int i, int x, int y) {
                    String textShowTime = i + ":00";
                    dayBarChartView.showPopupWindow(anchorButton, textShowTime, (int) dayhour.get(i).itemValue, x, y);
                }

                @Override
                public void dismissPopupWindow() {
                    dayBarChartView.popupWindow.dismiss();
                }
            });
        }
    }

}

