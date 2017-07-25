package com.linkloving.dyh08.logic.UI.calories.asyncktask;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import com.linkloving.band.dto.DaySynopic;
import com.linkloving.dyh08.MyApplication;
import com.linkloving.dyh08.ViewUtils.drawAngle.DrawArc;
import com.linkloving.dyh08.logic.dto.UserEntity;
import com.linkloving.dyh08.prefrences.PreferencesToolkits;
import com.linkloving.dyh08.utils.CommonUtils;
import com.linkloving.dyh08.utils.DbDataUtils;
import com.linkloving.dyh08.utils.ToolKits;
import com.linkloving.dyh08.utils.logUtils.MyLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 天数据查询 并且更新到饼状图UI
 */
public class DaySportAsynck extends AsyncTask<Object, Object, DaySynopic> {
    String datasdf;
    private static  final String TAG =DaySportAsynck.class.getSimpleName() ;
    Object object ;
    DrawArc drawArc ;
    TextView textView ;
    Context context ;
    TextView stepNumber ;
    int calorieseveryday ;
    private UserEntity userEntity;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    Date date = null;
    /**
     *
     * @param object  用于传Asynck执行的对象
     * @param drawArc   画中心圆
     * @param textView  更新日期
     * @param context   上下文对象
     */

    public DaySportAsynck(Object object, TextView stepNumber ,DrawArc drawArc, TextView textView, Context context){
        this.stepNumber = stepNumber ;
        this.object = object ;
        this.drawArc = drawArc ;
        this.textView = textView ;
        this.context = context ;
        ToolKits toolKits = new ToolKits();
         this.calorieseveryday = toolKits.getCalories(context);

    }

    //耗时操作
    @Override
    protected DaySynopic doInBackground(Object... params) {
        userEntity = MyApplication.getInstance(context).getLocalUserInfoProvider();
        String data = (String) params[0];
        MyLog.e("params", data);
        try {
            date = sdf.parse(data);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        datasdf = sdf.format(date);
//        MyLog.e(TAG, "datasdf:" + sdf.format(date));
        return DbDataUtils.findDayDatainSql(context, sdf.format(date), sdf.format(date));
    }

    //取到的某日DaySynopic形如：[data_date=2016-04-14,data_date2=null,time_zone=480,record_id=null,user_id=null,run_duration=1.0,run_step=68.0,run_distance=98.0
    // ,create_time=null,work_duration=178.0,work_step=6965.0,work_distance=5074.0,sleepMinute=2.0916666984558105,deepSleepMiute=1.25 gotoBedTime=1460645100 getupTime=1460657160]
    @Override
    protected void onPostExecute(DaySynopic daySynopic) {
        super.onPostExecute(daySynopic);
        //走路 分钟
        double walktime = CommonUtils.getScaledDoubleValue(Double.valueOf(daySynopic.getWork_duration()), 1);
        //跑步 分钟
        double runtime = CommonUtils.getScaledDoubleValue(Double.valueOf(daySynopic.getRun_duration()), 1);

        int walkCal = ToolKits.calculateCalories(Float.parseFloat(daySynopic.getWork_distance()), (int) walktime * 60, userEntity.getUserBase().getUser_weight());
        int runCal = ToolKits.calculateCalories(Float.parseFloat(daySynopic.getRun_distance()), (int) runtime * 60, userEntity.getUserBase().getUser_weight());
        Date dateToday = new Date();
        int calValue = 0 ;
        if (ToolKits.compareDate(dateToday,date)){
//            calValue = walkCal + runCal+calorieseveryday;
            String dateTodayFormat = sdf.format(dateToday);
            String dateFormat = sdf.format(date);
            String[] dateFormatSplit = dateTodayFormat.split(" ");
            String[] dateSplit = dateFormat.split(" ");
            MyLog.e(TAG,"1---"+dateSplit[0]+"2--"+dateFormatSplit[0]);
            if (dateSplit[0].equals(dateFormatSplit[0])){
                MyLog.e(TAG,"日期等于今天");
                SimpleDateFormat hh = new SimpleDateFormat("HH", Locale.getDefault());
                String HH = hh.format(dateToday);
                SimpleDateFormat mm = new SimpleDateFormat("mm", Locale.getDefault());
                String MM = mm.format(dateToday);
                int i = calorieseveryday * (Integer.parseInt(HH) * 60 + Integer.parseInt(MM)) / 1440;
                calValue = i+walkCal+runCal ;
            }else{
                MyLog.e(TAG,"日期在今天之前");
                calValue = walkCal + runCal+calorieseveryday;
            }
        }else{
            MyLog.e(TAG,"日期在今天之后");
         /*   String dateTodayFormat = sdf.format(dateToday);
            String dateFormat = sdf.format(date);
            String[] dateFormatSplit = dateTodayFormat.split(" ");
            String[] dateSplit = dateFormat.split(" ");
            if (dateSplit[0].equals(dateFormatSplit[0])){
                MyLog.e(TAG,"日期等于今天");
                SimpleDateFormat hh = new SimpleDateFormat("HH", Locale.getDefault());
                String HH = hh.format(dateToday);
                SimpleDateFormat mm = new SimpleDateFormat("mm", Locale.getDefault());
                String MM = mm.format(dateToday);
                int i = calorieseveryday * (Integer.parseInt(HH) * 60 + Integer.parseInt(MM)) / 1440;
                calValue = i+walkCal+runCal ;
            }else{
                MyLog.e(TAG,"日期在今天之后");
                calValue =walkCal+runCal ;
            }*/
            calValue =walkCal+runCal ;
        }

        int stepGoal = Integer.parseInt(PreferencesToolkits.getGoalInfo(context, PreferencesToolkits.KEY_GOAL_CAL));
        float stepPercent  = (float)calValue/stepGoal ;
        //此时去更新UI
        stepNumber.setText(Integer.toString(calValue));
        drawArc.setPercent(stepPercent);
        textView.setText(datasdf);
//            int walkDistance = (int) (CommonUtils.getScaledDoubleValue(Double.valueOf(daySynopic.getWork_distance()), 0));//走路 里程
//            int runDistance = (int) (CommonUtils.getScaledDoubleValue(Double.valueOf(daySynopic.getRun_distance()), 0));//跑步 里程
//            int distance = walkDistance + runDistance;
//            double qianleephour = CommonUtils.getScaledDoubleValue(Double.valueOf(daySynopic.getSleepMinute()), 1);//浅睡 小时
//            double deepleephour = CommonUtils.getScaledDoubleValue(Double.valueOf(daySynopic.getDeepSleepMiute()), 1);//深睡 小时
//            double sleeptime = CommonUtils.getScaledDoubleValue(qianleephour + deepleephour, 1);
//            double walktime = CommonUtils.getScaledDoubleValue(Double.valueOf(daySynopic.getWork_duration()), 1);//走路 分钟
//            double runtime = CommonUtils.getScaledDoubleValue(Double.valueOf(daySynopic.getRun_duration()), 1);//跑步 分钟
//            double worktime = CommonUtils.getScaledDoubleValue(walktime + runtime, 1);
//            int walkcal = ToolKits.calculateCalories(Float.parseFloat(daySynopic.getWork_distance()), (int) walktime * 60, userEntity.getUserBase().getUser_weight());
//            int runcal = ToolKits.calculateCalories(Float.parseFloat(daySynopic.getRun_distance()), (int) runtime * 60, userEntity.getUserBase().getUser_weight());
    }
}

