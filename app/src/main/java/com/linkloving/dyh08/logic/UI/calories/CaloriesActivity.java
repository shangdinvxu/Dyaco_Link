package com.linkloving.dyh08.logic.UI.calories;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.linkloving.dyh08.R;
import com.linkloving.dyh08.ViewUtils.drawAngle.DrawArc;
import com.linkloving.dyh08.basic.toolbar.ToolBarActivity;
import com.linkloving.dyh08.logic.UI.calories.WheelView.GoalWheelView;
import com.linkloving.dyh08.logic.UI.calories.asyncktask.DaySportAsynck;
import com.linkloving.dyh08.logic.UI.calories.asyncktask.MonthSportAsynck;
import com.linkloving.dyh08.logic.UI.calories.asyncktask.WeekSportAsynckTask;
import com.linkloving.dyh08.logic.UI.calories.asyncktask.YearSportAsynck;
import com.linkloving.dyh08.prefrences.PreferencesToolkits;
import com.linkloving.dyh08.utils.CommonUtils;
import com.linkloving.dyh08.utils.logUtils.MyLog;
import com.linkloving.dyh08.utils.sportUtils.DateSwitcher;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by leo.wang on 2016/7/27.
 */
public class CaloriesActivity extends ToolBarActivity {
    private static final String TAG = CaloriesActivity.class.getSimpleName();
    /**
     * VIEW下方四个切换按钮
     */
    @InjectView(R.id.step_button_day)
    RadioButton step_button_day_im;
    @InjectView(R.id.step_button_week)
    RadioButton step_button_week_im;
    @InjectView(R.id.step_button_month)
    RadioButton step_button_month_im;
    @InjectView(R.id.step_button_year)
    RadioButton step_button_year_im;
    @InjectView(R.id.step_tv_date)
    TextView step_tv_date;
    @InjectView(R.id.step_DrawArc_angle)
    DrawArc stepCircleView;
    @InjectView(R.id.step_mount)
    TextView step_number;
    @InjectView(R.id.calories_textView)
    TextView caloriesTV;
    @InjectView(R.id.goal_txt)
    AppCompatTextView goalTv;
    private DayViewFragment dayViewFragment;
    private WeekViewFragment weekViewFragment;
    private MonthViewFragment monthViewFragment;
    private YearViewFragment yearViewFragment;

    AlertDialog alertDialog;

    DateSwitcher weekSwitcher;

    String flushDate = CommonUtils.getFlushDate();
    String flushWeek = CommonUtils.getFlushWeek();
    String flushMonth = CommonUtils.getFlushMonth();
    private String flushYear;
    private int selectionIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tw_calories_activity);
        ButterKnife.inject(this);
        HideButtonRight(false);
        weekSwitcher = new DateSwitcher(DateSwitcher.PeriodSwitchType.week);
        /*设置进入到STEP界面默认的fragment*/
        selectionIndex = 0;
        setSelection(selectionIndex);
        //更新灰色小字日期
        updataData();
        updataGoalView();
        //获取右上角的点击控件
        LinearLayout rightLayout = getRightButton();
        TextView ree = (TextView) findViewById(R.id.ree);
        ree.setVisibility(View.VISIBLE);
        AppCompatImageView gogalImage = (AppCompatImageView) findViewById(R.id.gogalImage);
        gogalImage.setVisibility(View.GONE);

        rightLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<String> stepGoalList = creatStepGoalList();
                LayoutInflater inflater = getLayoutInflater();
                final View layout = inflater.inflate(R.layout.caloriesgoalwheel, (LinearLayout) findViewById(R.id.goal_view));
                final GoalWheelView wheelView = (GoalWheelView) layout.findViewById(R.id.goal_wheelView);
                final Button okbtn = (Button) layout.findViewById(R.id.okBtn);
                final Button cancelBtn = (Button) layout.findViewById(R.id.cancelBtn);
                String stepGoal = PreferencesToolkits.getGoalInfo(CaloriesActivity.this, PreferencesToolkits.KEY_GOAL_CAL);
                wheelView.setSeletion(getIndexFromGoalList(stepGoalList, stepGoal));
                wheelView.setOffset(1);
                wheelView.setItems(stepGoalList);
                alertDialog = new AlertDialog.Builder(CaloriesActivity.this)
                        .setView(layout).show();
                alertDialog.show();
                okbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e(TAG, "wheelView.getSeletedItem():" + wheelView.getSeletedItem());
                        PreferencesToolkits.setGoalInfo(CaloriesActivity.this, PreferencesToolkits.KEY_GOAL_CAL, wheelView.getSeletedItem());
                        updataGoalView();
                        switch (selectionIndex) {
                            case 0:
                                new DaySportAsynck(null, step_number, stepCircleView, step_tv_date, CaloriesActivity.this).execute(flushDate);
                                break;
                            case 1:
                                new WeekSportAsynckTask(null, step_number, stepCircleView, step_tv_date, CaloriesActivity.this).execute(flushWeek);
                                break;
                            case 2:
                                new MonthSportAsynck(null, step_number, stepCircleView, step_tv_date, CaloriesActivity.this).execute(flushMonth);
                                break;
                            case 3:
                                new YearSportAsynck(null, step_number, stepCircleView, step_tv_date, CaloriesActivity.this).execute(flushYear);
                                break;
                        }


                        alertDialog.dismiss();
                    }
                });
                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /**
     * 构建步数目标集合
     *
     * @return
     */
    private List<String> creatStepGoalList() {
        List<String> list = new ArrayList<String>();
        //要求步数目标为2000~~100000
        int startGoal = 1000;
        int overGoal = 9900;
        for (int i = startGoal; i <= overGoal; i += 100) {
            list.add(i + "");
        }
        return list;
    }

    private int getIndexFromGoalList(List<String> goalList, String goal) {
        int i = 0;
        for (; i < goalList.size(); i++) {
            if (goalList.get(i).equals(goal)) {
                return i;
            }
        }
        return i;
    }

    private void updataData() {
        step_tv_date.setText(CommonUtils.getDateWithStr());
    }

    private void updataGoalView() {
        //从本地抓取目标值 并且设置到view上
        String stepGoal = PreferencesToolkits.getGoalInfo(this, PreferencesToolkits.KEY_GOAL_CAL);
        goalTv.setText(stepGoal);
        goalTv.setTextColor(Color.rgb(255, 196, 0));
    }

    @Override
    protected void getIntentforActivity() {
    }


    /**
     * 选择Fragment
     */
    private void setSelection(int index) {
        restartBotton();
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
//        hideFragments(transaction);
        switch (index) {
            case 0:
                selectionIndex = 0;
                caloriesTV.setText(R.string.kcal);
                step_button_day_im.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.d_on_72px, 0, 0);
                new DaySportAsynck(flushDate, step_number, stepCircleView, step_tv_date, CaloriesActivity.this).execute(flushDate);
                dayViewFragment = new DayViewFragment();
                dayViewFragment.setDataChangeListener(new IDataChangeListener() {
                    @Override
                    public void onDataChange(String data) {
                        MyLog.e(TAG, "日界面点击的日期是:" + data);
                        flushDate = data;
                        new DaySportAsynck(null, step_number, stepCircleView, step_tv_date, CaloriesActivity.this).execute(flushDate);
                    }
                });
                transaction.replace(R.id.step_middle, dayViewFragment);
                break;
            case 1:
                selectionIndex = 1;
                caloriesTV.setText(R.string.avgkcal);
                step_button_week_im.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.w_on_72px, 0, 0);
                new WeekSportAsynckTask(null, step_number, stepCircleView, step_tv_date, CaloriesActivity.this).execute(flushWeek);
                weekViewFragment = new WeekViewFragment();
                weekViewFragment.setDataChangeListener(new IDataChangeListener() {
                    @Override
                    public void onDataChange(String data) {
                        MyLog.e(TAG, "周界面点击的日期是:" + data);
                        flushWeek = data;
                        new WeekSportAsynckTask(null, step_number, stepCircleView, step_tv_date, CaloriesActivity.this).execute(flushWeek);
                    }
                });
                transaction.replace(R.id.step_middle, weekViewFragment);
                break;
            case 2:
                selectionIndex = 2;
                caloriesTV.setText(R.string.avgkcal);
                step_button_month_im.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.m_on_72px, 0, 0);
                new MonthSportAsynck(null, step_number, stepCircleView, step_tv_date, CaloriesActivity.this).execute(flushMonth);
                monthViewFragment = new MonthViewFragment();
                monthViewFragment.setDataChangeListener(new IDataChangeListener() {
                    @Override
                    public void onDataChange(String data) {
                        MyLog.e(TAG, "月界面点击的月是:" + data);
                        flushMonth = data;
                        new MonthSportAsynck(null, step_number, stepCircleView, step_tv_date, CaloriesActivity.this).execute(flushMonth);
                    }
                });
                transaction.replace(R.id.step_middle, monthViewFragment);
                break;
            case 3:
                selectionIndex = 3;
                caloriesTV.setText(R.string.avgkcal);
                step_button_year_im.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.y_on_72px, 0, 0);
                yearViewFragment = new YearViewFragment();
                flushYear = CommonUtils.getFlushYear();
                new YearSportAsynck(null, step_number, stepCircleView, step_tv_date, CaloriesActivity.this).execute(flushYear);
                yearViewFragment.setDataChangeListener(new IDataChangeListener() {
                    @Override
                    public void onDataChange(String data) {
                        MyLog.e(TAG, "年界面点击的年份是:" + data);
                        new YearSportAsynck(null, step_number, stepCircleView, step_tv_date, CaloriesActivity.this).execute(data);
                    }
                });
                transaction.replace(R.id.step_middle, yearViewFragment);
                break;
        }
        transaction.commit();
    }

    /**
     * 切换Fragment时候 重置按钮图片
     */
    private void restartBotton() {
        step_button_day_im.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.d_off_72px, 0, 0);
        step_button_week_im.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.w_off_72px, 0, 0);
        step_button_month_im.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.m_off_72px, 0, 0);
        step_button_year_im.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.y_off_72px, 0, 0);
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
}
