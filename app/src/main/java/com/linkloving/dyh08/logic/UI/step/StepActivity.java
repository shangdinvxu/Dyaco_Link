package com.linkloving.dyh08.logic.UI.step;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.linkloving.dyh08.R;
import com.linkloving.dyh08.basic.toolbar.ToolBarActivity;
import com.linkloving.dyh08.logic.UI.step.WheelView.GoalWheelView;
import com.linkloving.dyh08.logic.UI.step.asyncktask.DaySportAsynck;
import com.linkloving.dyh08.logic.UI.step.asyncktask.MonthSportAsynck;
import com.linkloving.dyh08.logic.UI.step.asyncktask.WeekSportAsynckTask;
import com.linkloving.dyh08.logic.UI.step.asyncktask.YearSportAsynck;
import com.linkloving.dyh08.ViewUtils.drawAngle.DrawArc;
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
public class StepActivity extends ToolBarActivity{
    private static final String TAG = StepActivity.class.getSimpleName();
    /**VIEW下方四个切换按钮*/
    @InjectView(R.id.step_button_day)
    RadioButton step_button_day_im;
    @InjectView(R.id.step_button_week)
    RadioButton step_button_week_im;
    @InjectView(R.id.step_button_month)
    RadioButton step_button_month_im;
    @InjectView(R.id.step_button_year)
    RadioButton step_button_year_im;
    @InjectView(R.id.step_tv_date)
     TextView step_tv_date ;
    @InjectView(R.id.step_DrawArc_angle)
    DrawArc stepCircleView ;
    @InjectView(R.id.step_mount)
    TextView step_number ;
    @InjectView(R.id.goal_txt)
    AppCompatTextView goalTv;
    @InjectView(R.id.steps_textView)
    TextView stepTV ;
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
        setContentView(R.layout.tw_step_activity);
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
        rightLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<String> stepGoalList = creatStepGoalList();
                LayoutInflater inflater = getLayoutInflater();
                final View layout = inflater.inflate(R.layout.goalwheelstep, (LinearLayout) findViewById(R.id.goal_view));
                final GoalWheelView wheelView = (GoalWheelView)layout.findViewById(R.id.goal_wheelView);
                final Button okbtn = (Button)layout.findViewById(R.id.okBtn);
                final Button cancelBtn = (Button)layout.findViewById(R.id.cancelBtn);
                String stepGoal = PreferencesToolkits.getGoalInfo(StepActivity.this,PreferencesToolkits.KEY_GOAL_STEP);
                wheelView.setSeletion(getIndexFromGoalList(stepGoalList,stepGoal));
                wheelView.setOffset(1);
                wheelView.setItems(stepGoalList);
                alertDialog = new AlertDialog.Builder(StepActivity.this)
                        .setView(layout).show();
                alertDialog.show();
                okbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e(TAG, "wheelView.getSeletedItem():" + wheelView.getSeletedItem());
                        PreferencesToolkits.setGoalInfo(StepActivity.this, PreferencesToolkits.KEY_GOAL_STEP, wheelView.getSeletedItem());
                        updataGoalView();
                        switch (selectionIndex){
                            case 0:
                                new DaySportAsynck(null,step_number,stepCircleView,step_tv_date,StepActivity.this).execute(flushDate);
                                break;
                            case 1:
                                new WeekSportAsynckTask(null,step_number,stepCircleView,step_tv_date,StepActivity.this).execute(flushWeek);
                                break;
                            case 2:
                                new MonthSportAsynck(null,step_number, stepCircleView, step_tv_date, StepActivity.this).execute(flushMonth);
                                break;
                            case 3:
                                new YearSportAsynck(null,step_number,stepCircleView,step_tv_date,StepActivity.this).execute(flushYear);
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



    /**
     * 构建步数目标集合
     * @return
     */
    private List<String> creatStepGoalList() {
        List<String> list =new ArrayList<String>();
        //要求步数目标为2000~~100000
        int startGoal = 10000;
        int overGoal = 100000;
        for(int i =startGoal;i<=overGoal;i+=1000 ){
            list.add(i+"");
        }
        return list;
    }

    private int getIndexFromGoalList(List<String> goalList,String goal) {
        int i = 0;
        for(;i<goalList.size();i++){
            if(goalList.get(i).equals(goal)){
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
        String stepGoal = PreferencesToolkits.getGoalInfo(this,PreferencesToolkits.KEY_GOAL_STEP);
        goalTv.setText(stepGoal);
        goalTv.setTextColor(Color.rgb(255,196,0));
    }

    @Override
    protected void getIntentforActivity() {
    }

    /**选择Fragment*/
    private void setSelection(int index){
        restartBotton();
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
//        hideFragments(transaction);
        switch (index){
            case 0:
                selectionIndex =0 ;
                stepTV.setText(getString(R.string.steps));
                step_button_day_im.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.d_on_72px, 0, 0);
                new DaySportAsynck(flushDate,step_number,stepCircleView,step_tv_date,StepActivity.this).execute(flushDate);
                    dayViewFragment = new DayViewFragment();
                    dayViewFragment.setDataChangeListener(new IDataChangeListener() {
                        @Override
                        public void onDataChange(String data) {
                            MyLog.e(TAG,"日界面点击的日期是:"+data);
                            flushDate = data;
                            new DaySportAsynck(null,step_number,stepCircleView,step_tv_date,StepActivity.this).execute(flushDate);
                        }
                    });
                transaction.replace(R.id.step_middle,dayViewFragment);
                break;
            case 1:
                selectionIndex =1 ;
                stepTV.setText(R.string.avgsteps);
                step_button_week_im.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.w_on_72px, 0, 0);
                new WeekSportAsynckTask(null,step_number,stepCircleView,step_tv_date,StepActivity.this).execute(flushWeek);
                weekViewFragment = new WeekViewFragment();
                weekViewFragment.setDataChangeListener(new IDataChangeListener() {
                        @Override
                        public void onDataChange(String data) {
                            MyLog.e(TAG,"周界面点击的日期是:"+data);
                            flushWeek = data;
                            new WeekSportAsynckTask(null,step_number,stepCircleView,step_tv_date,StepActivity.this).execute(flushWeek);
                        }
                    });
                transaction.replace(R.id.step_middle,weekViewFragment);
                break;
            case 2:
                selectionIndex =2 ;
                stepTV.setText(R.string.avgsteps);
                step_button_month_im.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.m_on_72px, 0, 0);
                new MonthSportAsynck(null,step_number, stepCircleView, step_tv_date, StepActivity.this).execute(flushMonth);
                monthViewFragment  = new MonthViewFragment();
                monthViewFragment.setDataChangeListener(new IDataChangeListener() {
                        @Override
                        public void onDataChange(String data) {
                            MyLog.e(TAG, "月界面点击的月是:" + data);
                            flushMonth = data;
                            new MonthSportAsynck(null,step_number, stepCircleView, step_tv_date, StepActivity.this).execute(flushMonth);
                        }
                    });
                transaction.replace(R.id.step_middle, monthViewFragment);
                break;
            case 3:
                selectionIndex =3 ;
                stepTV.setText(R.string.avgsteps);
                step_button_year_im.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.y_on_72px, 0, 0);
                yearViewFragment = new YearViewFragment();
                flushYear = CommonUtils.getFlushYear();
                new YearSportAsynck(null,step_number,stepCircleView,step_tv_date,StepActivity.this).execute(flushYear);
                yearViewFragment.setDataChangeListener(new IDataChangeListener() {
                        @Override
                        public void onDataChange(String data) {
                            MyLog.e(TAG,"年界面点击的年份是:"+data);
                            new YearSportAsynck(null,step_number,stepCircleView,step_tv_date,StepActivity.this).execute(data);
                        }
                    });
                transaction.replace(R.id.step_middle, yearViewFragment);
                break;
        }
        transaction.commit();
    }
    /**切换Fragment时候 重置按钮图片*/
    private void restartBotton() {
        step_button_day_im.setCompoundDrawablesRelativeWithIntrinsicBounds(0,R.mipmap.d_off_72px,0,0);
        step_button_week_im.setCompoundDrawablesRelativeWithIntrinsicBounds(0,R.mipmap.w_off_72px,0,0);
        step_button_month_im.setCompoundDrawablesRelativeWithIntrinsicBounds(0,R.mipmap.m_off_72px,0,0);
        step_button_year_im.setCompoundDrawablesRelativeWithIntrinsicBounds(0,R.mipmap.y_off_72px,0,0);
    }

    /**日数据浏览点击事件*/
    public void onDayViewClicked(View view){ setSelection(0); }
    /**周数据浏览点击事件*/
    public void onWeekViewClicked(View view){ setSelection(1); }
    /**月数据浏览点击事件*/
    public void onMonthViewClicked(View view){ setSelection(2); }
    /**年数据浏览点击事件*/
    public void onYearViewClicked(View view){ setSelection(3); }
    @Override
    protected void initView() {
    }
    @Override
    protected void initListeners() {
    }
}
