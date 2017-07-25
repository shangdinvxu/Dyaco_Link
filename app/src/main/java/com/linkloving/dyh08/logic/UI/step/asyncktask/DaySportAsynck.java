package com.linkloving.dyh08.logic.UI.step.asyncktask;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import com.linkloving.band.dto.DaySynopic;
import com.linkloving.dyh08.ViewUtils.drawAngle.DrawArc;
import com.linkloving.dyh08.prefrences.PreferencesToolkits;
import com.linkloving.dyh08.utils.CommonUtils;
import com.linkloving.dyh08.utils.DbDataUtils;
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

    Object object ;
    DrawArc drawArc ;
    TextView textView ;
    Context context ;
    TextView stepNumber ;
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
    }

    //耗时操作
    @Override
    protected DaySynopic doInBackground(Object... params) {
        String data = (String) params[0];
        MyLog.e("params", data);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = null;
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
        //走路 步数
        int walkStep = (int) (CommonUtils.getScaledDoubleValue(Double.valueOf(daySynopic.getWork_step()), 0));
        //跑步 步数
        int runStep = (int) (CommonUtils.getScaledDoubleValue(Double.valueOf(daySynopic.getRun_step()), 0));
        int step = walkStep + runStep;
        int stepGoal = Integer.parseInt(PreferencesToolkits.getGoalInfo(context, PreferencesToolkits.KEY_GOAL_STEP));
        float stepPercent  = (float)step/stepGoal ;
        //此时去更新UI
        stepNumber.setText(Integer.toString(step));
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

