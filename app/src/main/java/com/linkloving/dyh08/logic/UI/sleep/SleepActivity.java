package com.linkloving.dyh08.logic.UI.sleep;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.trace.TraceLocation;
import com.linkloving.dyh08.R;
import com.linkloving.dyh08.ViewUtils.calendar.MonthDateView;
import com.linkloving.dyh08.basic.toolbar.ToolBarActivity;
import com.linkloving.dyh08.logic.UI.main.PortalActivity;
import com.linkloving.dyh08.logic.UI.sleep.asyncktask.DaySportAsynck;
import com.linkloving.dyh08.logic.UI.sleep.asyncktask.MonthSportAsynck;
import com.linkloving.dyh08.logic.UI.sleep.asyncktask.WeekSportAsynckTask;
import com.linkloving.dyh08.utils.CommonUtils;
import com.linkloving.dyh08.utils.TypefaceUtils;
import com.linkloving.dyh08.utils.logUtils.MyLog;
import com.linkloving.dyh08.utils.sportUtils.DateSwitcher;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by leo.wang on 2016/7/27.
 */
public class SleepActivity extends ToolBarActivity {
    private static final String TAG = SleepActivity.class.getSimpleName();
    /**
     * VIEW下方四个切换按钮
     */
    @InjectView(R.id.step_tv_date)
    TextView step_tv_date;

    @InjectView(R.id.goal_txt)
    AppCompatTextView goalTv;
    @InjectView(R.id.sleep_hour_tv)
    TextView sleepHourTv;
    @InjectView(R.id.sleep_minute_tv)
    TextView sleepMinuteTv;
    @InjectView(R.id.sleep_lightSleep)
    TextView sleepLightSleep;
    @InjectView(R.id.sleep_DeepSleep)
    TextView sleepDeepSleep;
    @InjectView(R.id.step_middle)
    RelativeLayout stepMiddle;
    @InjectView(R.id.sleep_button_day)
    RadioButton sleepButtonDay;
    @InjectView(R.id.sleep_button_week)
    RadioButton sleepButtonWeek;
    @InjectView(R.id.sleep_button_month)
    RadioButton sleepButtonMonth;
    @InjectView(R.id.step_activity_layout)
    LinearLayout stepActivityLayout;
    @InjectView(R.id.tw_sleep_buttomfragment)
    RelativeLayout sleepButtonRL ;
    @InjectView(R.id.tw_sleep_buttom)
    RadioGroup sleepButtom;
    private DayViewFragment dayViewFragment;
    private WeekViewFragment weekViewFragment;
    private MonthViewFragment monthViewFragment;

    private int fragmentReplaceTag = 0 ;

    public int getDayButtomTag() {
        return dayButtomTag;
    }
    public void setDayButtomTag(int dayButtomTag) {
        this.dayButtomTag = dayButtomTag;
    }

    private int  dayButtomTag = 0 ;



    DateSwitcher weekSwitcher;

    String flushDate = CommonUtils.getFlushDate();
    String flushWeek = CommonUtils.getFlushWeek();
    String flushMonth = CommonUtils.getFlushMonth();
    private String flushYear;
    private int selectionIndex;
    private FragmentTransaction transaction;

    public void setFragmentReplaceTag(int tag){
        this.fragmentReplaceTag = tag ;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tw_sleep_activity);
        ButterKnife.inject(this);
        weekSwitcher = new DateSwitcher(DateSwitcher.PeriodSwitchType.week);
        /*设置进入到STEP界面默认的fragment*/
        selectionIndex = 0;
        //更新灰色小字日期
        updataData();
        setSelection(selectionIndex);
//        设置字体
        TypefaceUtils.setNumberType(SleepActivity.this, sleepHourTv);
        TypefaceUtils.setNumberType(SleepActivity.this,sleepMinuteTv);
        TypefaceUtils.setNumberType(SleepActivity.this,sleepLightSleep);
        TypefaceUtils.setNumberType(SleepActivity.this,sleepDeepSleep);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        switch (fragmentReplaceTag){
            case 1:
                new DaySportAsynck(sleepHourTv,sleepMinuteTv,sleepDeepSleep,sleepLightSleep, step_tv_date, SleepActivity.this).execute(flushDate);
                    sleepButtom.setVisibility(View.VISIBLE);
                    sleepButtom.bringToFront();
               fragmentTransaction.show(dayViewFragment);
                setFragmentReplaceTag(0);
            break;
            case 2:
                new WeekSportAsynckTask(sleepHourTv,sleepMinuteTv,sleepDeepSleep,sleepLightSleep, step_tv_date,  SleepActivity.this).execute(flushWeek);
                sleepButtom.setVisibility(View.VISIBLE);
                sleepButtom.bringToFront();
                fragmentTransaction.show(weekViewFragment);
                setFragmentReplaceTag(0);
                break;
            case 3:
                new MonthSportAsynck(sleepHourTv,sleepMinuteTv,sleepDeepSleep,sleepLightSleep, step_tv_date, SleepActivity.this).execute(flushMonth);
                sleepButtom.setVisibility(View.VISIBLE);
                sleepButtom.bringToFront();
                fragmentTransaction.show(monthViewFragment);
                setFragmentReplaceTag(0);
                break;
            case 4:
                SleepCalendarButtomFragment sleepCalendarButtomFragment = new SleepCalendarButtomFragment();
                fragmentTransaction.show(sleepCalendarButtomFragment);
                fragmentTransaction.replace(R.id.tw_sleep_buttomfragment,sleepCalendarButtomFragment).commit();
                setFragmentReplaceTag(3);
                break;
            case 0 :
                Intent intent = new Intent(SleepActivity.this, PortalActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);

    }

    private void updataData() {
        step_tv_date.setText(CommonUtils.getDateWithStr());
    }

    @Override
    protected void getIntentforActivity() {
    }
public void videGone(){
    sleepButtom.setVisibility(View.GONE);
}
    /**
     * 选择Fragment
     */
    private void setSelection(int index) {
        restartBotton();
        FragmentManager manager = getFragmentManager();
        transaction = manager.beginTransaction();
//        hideFragments(transaction);
        switch (index) {
            case 0:
                selectionIndex = 0;
                sleepButtonDay.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.d_on_72px, 0, 0);
                dayViewFragment = new DayViewFragment();
                flushDate = CommonUtils.getFlushDate();
                new DaySportAsynck(sleepHourTv,sleepMinuteTv,sleepDeepSleep,sleepLightSleep, step_tv_date, SleepActivity.this).execute(flushDate);
                dayViewFragment.setDataChangeListener(new IDataChangeListener() {
                    @Override
                    public void onDataChange(String data) {
                        MyLog.e(TAG, "日界面点击的日期是:" + data);
                        new DaySportAsynck(sleepHourTv,sleepMinuteTv,sleepDeepSleep,sleepLightSleep, step_tv_date, SleepActivity.this).execute(data);
//                        if (dayButtomTag==1) {
//                            sleepButtom.setVisibility(View.GONE);
//                        }
                        fragmentReplaceTag = 1 ;
                    }
                });
                transaction.replace(R.id.step_middle, dayViewFragment);

                break;
            case 1:
                selectionIndex = 1;
                sleepButtonWeek.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.w_on_72px, 0, 0);
                weekViewFragment = new WeekViewFragment();
                new WeekSportAsynckTask(sleepHourTv,sleepMinuteTv,sleepDeepSleep,sleepLightSleep, step_tv_date,  SleepActivity.this).execute(flushWeek);
                weekViewFragment.setDataChangeListener(new IDataChangeListener() {
                    @Override
                    public void onDataChange(String data) {
                        MyLog.e(TAG, "周界面点击的日期是:" + data);
                        new WeekSportAsynckTask(sleepHourTv, sleepMinuteTv, sleepDeepSleep, sleepLightSleep, step_tv_date, SleepActivity.this).execute(data);
                    }
                });
                transaction.replace(R.id.step_middle, weekViewFragment);
                transaction.addToBackStack(null);
                weekViewFragment.setSleepNextListener(new IClickNextlistener() {
                    @Override
                    public void nextFragment() {
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        SleepWeekButtomFragment sleepWeekButtomFragment = new SleepWeekButtomFragment();
                        fragmentTransaction.replace(R.id.tw_sleep_buttomfragment,sleepWeekButtomFragment);
                        sleepButtom.setVisibility(View.GONE);
                        fragmentReplaceTag = 2 ;
                                fragmentTransaction.commit();
                    }
                });
                break;
            case 2:
                selectionIndex = 2;
                sleepButtonMonth.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.m_on_72px, 0, 0);
                monthViewFragment = new MonthViewFragment();
                new MonthSportAsynck(sleepHourTv,sleepMinuteTv,sleepDeepSleep,sleepLightSleep, step_tv_date, SleepActivity.this).execute(flushMonth);
                monthViewFragment.setDataChangeListener(new IDataChangeListener() {
                    @Override
                    public void onDataChange(String data) {
                        MyLog.e(TAG, "月界面点击的月是:" + data);
                        new MonthSportAsynck(sleepHourTv, sleepMinuteTv, sleepDeepSleep, sleepLightSleep, step_tv_date, SleepActivity.this).execute(data);

                    }
                });
                transaction.replace(R.id.step_middle, monthViewFragment);
                monthViewFragment.setClickNextListener(new IClickNextlistener() {
                    @Override
                    public void nextFragment() {
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        SleepWeekButtomFragment sleepWeekButtomFragment = new SleepWeekButtomFragment();
                        fragmentTransaction.replace(R.id.tw_sleep_buttomfragment,sleepWeekButtomFragment);
                        sleepButtom.setVisibility(View.GONE);
                        fragmentReplaceTag = 3 ;
                        fragmentTransaction.commit();
                    }
                });
                break;
            case 3:
//                selectionIndex = 3;
//                step_button_year_im.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.y_on_72px, 0, 0);
//                yearViewFragment = new YearViewFragment();
//                flushYear = CommonUtils.getFlushYear();
//                new YearSportAsynck(sleepHourTv,sleepMinuteTv,sleepDeepSleep,sleepLightSleep, step_tv_date, SleepActivity.this).execute(flushYear);
//                yearViewFragment.setDataChangeListener(new IDataChangeListener() {
//                    @Override
//                    public void onDataChange(String data) {
//                        MyLog.e(TAG, "年界面点击的年份是:" + data);
//                        new YearSportAsynck(sleepHourTv,sleepMinuteTv,sleepDeepSleep,sleepLightSleep, step_tv_date, SleepActivity.this).execute(flushYear);
//                    }
//                });
//                transaction.replace(R.id.step_middle, yearViewFragment);
                break;
        }
        transaction.commit();
    }

    /**
     * 切换Fragment时候 重置按钮图片
     */
    private void restartBotton() {
        sleepButtonDay.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.d_off_72px, 0, 0);
        sleepButtonWeek.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.w_off_72px, 0, 0);
        sleepButtonMonth.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.m_off_72px, 0, 0);
//        step_button_year_im.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.y_off_72px, 0, 0);
    }

    /**
     * 日数据浏览点击事件
     */
    public void onDayViewClicked(View view) {
        setSelection(0);
    }

    /**
     * 周数据浏览点击事件
     */
    public void onWeekViewClicked(View view) {
        setSelection(1);
    }

    /**
     * 月数据浏览点击事件
     */
    public void onMonthViewClicked(View view) {
        setSelection(2);
    }

    /**
     * 年数据浏览点击事件
     */
    public void onYearViewClicked(View view) {
        setSelection(3);
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initListeners() {
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
