package com.linkloving.dyh08.logic.UI.sleep;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.linkloving.band.dto.DaySynopic;
import com.linkloving.dyh08.R;
import com.linkloving.dyh08.ViewUtils.drawAngle.DrawArc;
import com.linkloving.dyh08.logic.UI.sleep.calendar.MonthDateView;
import com.linkloving.dyh08.utils.CommonUtils;
import com.linkloving.dyh08.utils.DbDataUtils;
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
public class MonthCalendarFragment extends Fragment {
    private static final String TAG = MonthCalendarFragment.class.getSimpleName();
    private int type = 0 ;
    public ImageView left_btn ;
    public ImageView right_btn;

    private com.linkloving.dyh08.logic.UI.sleep.IDataChangeListener dataChangeListener;
    private View view ;
    public String date;
    private MonthDateView monthDateView;
    private List<Integer> safeList;
    private List<Integer> atentionList;
    private List<Integer> dangerList;
    private String firstDayOfMonth;
    private String lastDayOfMonth;
    private Date parse;

    public void setDataChangeListener(com.linkloving.dyh08.logic.UI.sleep.IDataChangeListener dataChangeListener){
        this.dataChangeListener = dataChangeListener;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tw_sleep_month_calendar, container, false);
        ButterKnife.inject(this, view);
        left_btn = (ImageView) view.findViewById(R.id.step_calendar_iv_left);
        right_btn = (ImageView) view.findViewById(R.id.step_calendar_iv_right);
        monthDateView = (MonthDateView) view.findViewById(R.id.monthDateView);
        String date = getArguments().getString("monthDate");
////        不用异步.不然list加载不到
        setSleepList(date);
        monthDateView.setDaysSafeList(safeList);
        monthDateView.setDaysAtentionList(atentionList);
        monthDateView.setDaysDangerList(dangerList);
        monthDateView.setDateClick(new MonthDateView.DateClick() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClickOnDate() {
                //拼接日期字符串（可以自己定义）
                if (monthDateView.getmSelDay() == 0) {
                    return;
                }
                String checkDate = monthDateView.getmSelYear() + "-" + monthDateView.getmSelMonth() + "-" + monthDateView.getmSelDay();
                MyLog.e(TAG, checkDate + "checkDate");
                dataChangeListener.onDataChange(checkDate);

                MyLog.e(TAG, "checkDate" + checkDate);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    parse = simpleDateFormat.parse(checkDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar instance = Calendar.getInstance();
                instance.setTime(parse);
                instance.add(Calendar.MONTH, -1);
                Date time = instance.getTime();
                SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat("yyyy-MM");
                String format = simpleDateFormatTime.format(time);
//                String sleepListDate = monthDateView.getmSelYear() + "-" + monthDateView.getmSelMonth() ;
                setSleepList(format);
                if (type == 0) {
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    DaychartviewFragment daychartviewFragment = new DaychartviewFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("date", checkDate);
                    daychartviewFragment.setArguments(bundle);
                    transaction.replace(R.id.step_middle, daychartviewFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
//加载下面的buttom
                    SleepActivity activity = (SleepActivity) getActivity();
                    activity.setFragmentReplaceTag(4);

                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    SleepDayButtomFragment sleepDayButtomFragment = new SleepDayButtomFragment();
                    fragmentTransaction.replace(R.id.tw_sleep_buttomfragment, sleepDayButtomFragment).commit();

                }
            }
        });
        left_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type= 1 ;
                monthDateView.onLeftClick();
                type = 0 ;

            }
        });
        right_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                type = 1;
                monthDateView.onRightClick();
                type = 0;
            }
        });

        return view;
    }
    private void setSleepList(String date){
        String[] split = date.split("-");
        String year = split[0];
        String month = split[1];
        firstDayOfMonth = CommonUtils.getFirstDayOfMonth(Integer.parseInt(year), Integer.parseInt(month) );
        lastDayOfMonth = CommonUtils.getLastDayOfMonth(Integer.parseInt(year), Integer.parseInt(month));
        MyLog.e("firstDayOfMonth","firstDayOfMonth"+ firstDayOfMonth +"_______"+ lastDayOfMonth);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd ");
        List<DaySynopic> daySynopics = DbDataUtils.findMonthDatainSql(getActivity(), simpleDateFormat, firstDayOfMonth, lastDayOfMonth);
        safeList = new ArrayList<Integer>();
        atentionList = new ArrayList<Integer>();
        dangerList = new ArrayList<Integer>();
        int daynumber = 0;
        double deepSleepHour = 0 ;
        for(DaySynopic daySynopic:daySynopics){
            daynumber +=1 ;
            //浅睡 小时
            double lightSleepHour = CommonUtils.getScaledDoubleValue(Double.valueOf(daySynopic.getSleepMinute()), 1);
            //深睡 小时
            deepSleepHour = CommonUtils.getScaledDoubleValue(Double.valueOf(daySynopic.getDeepSleepMiute()), 1);
            MyLog.e(TAG,"deepSleepHour="+deepSleepHour);
            if (deepSleepHour>5){
                safeList.add(daynumber);
                MyLog.e(TAG, daySynopic.getData_date() + "safelist的日期");
            }else if (deepSleepHour>=2){
                atentionList.add(daynumber);
            }else if (deepSleepHour>0){
                dangerList.add(daynumber);
                MyLog.e(TAG, daySynopic.getData_date() + "dangerList的日期deepSleepHour"+deepSleepHour);
            }
        }
        monthDateView.setDaysSafeList(safeList);
        monthDateView.setDaysAtentionList(atentionList);
        monthDateView.setDaysDangerList(dangerList);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



}
