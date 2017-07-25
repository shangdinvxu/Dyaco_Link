package com.linkloving.dyh08.logic.UI.sleep.asyncktask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.linkloving.band.dto.DaySynopic;
import com.linkloving.dyh08.ViewUtils.drawAngle.DrawArc;
import com.linkloving.dyh08.utils.CommonUtils;
import com.linkloving.dyh08.utils.DbDataUtils;
import com.linkloving.dyh08.utils.ToolKits;
import com.linkloving.dyh08.utils.logUtils.MyLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 周数据查询 并且更新到饼状图UI
 */
 public  class WeekSportAsynckTask extends AsyncTask<Object, Object, List<DaySynopic>> {
    private static final String TAG = WeekSportAsynckTask.class.getSimpleName();
    String startData; //周开始时间
    String endData;   //周结束时间

    String startDateformat;
    String endDateformat;
    String endDateMd;
    Object object ;
    DrawArc drawArc ;
    TextView stepNumber ;
    String datasdf;
    TextView sleepHourTv ;
    TextView sleepMinuteTv ;
    TextView sleepDeepSleep ;
    TextView sleepLightSleep ;
    TextView textView ;
    Context context ;
    private String sleepHourString;
    private String sleepMinuteString;


    /**
     *
     * @param textView  更新日期
     * @param context   上下文对象
     */

    public WeekSportAsynckTask(TextView sleepHourTv,TextView sleepMinuteTv,TextView sleepDeepSleep,TextView sleepLightSleep,
                               TextView textView, Context context){
        this.sleepHourTv = sleepHourTv ;
        this.sleepMinuteTv = sleepMinuteTv ;
        this.sleepDeepSleep = sleepDeepSleep ;
        this.sleepLightSleep = sleepLightSleep ;
        this.textView = textView ;
        this.context = context ;
    }

    //耗时操作
    @Override
    protected List<DaySynopic> doInBackground(Object... params) {

        String dateStr = params[0].toString();
        Log.e(TAG, "dateStr-------" + dateStr);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        Date mondayOfThisWeek = null;
        Date sundayofThisWeek = null;
        try {
            mondayOfThisWeek= ToolKits.getFirstSundayOfThisWeek(sdf.parse(dateStr));
            sundayofThisWeek = ToolKits.getStaurdayofThisWeek(sdf.parse(dateStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat simYearMonth = new SimpleDateFormat("MM/dd");
         startData = sdf.format(mondayOfThisWeek);
         endData = simYearMonth.format(sundayofThisWeek);


        SimpleDateFormat sdfStandard = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        startDateformat = sdfStandard.format(mondayOfThisWeek);
        endDateformat = sdfStandard.format(sundayofThisWeek);
        Log.e(TAG, startDateformat + "-------" + "endDateformat" + endDateformat);
        return DbDataUtils.findWeekDatainSql(context, sdfStandard, endDateformat, endDateformat);
    }

    @Override
    protected void onPostExecute(List<DaySynopic> daySynopics) {
        super.onPostExecute(daySynopics);
        int daynumber = 0;
        double deepSleepHour = 0 ;
        double lighthour = 0 ;
        double deephour = 0 ;
        for(DaySynopic daySynopic:daySynopics){
            daynumber +=1 ;
            //浅睡 小时
            double lightSleepHour = CommonUtils.getScaledDoubleValue(Double.valueOf(daySynopic.getSleepMinute()), 1);
            //深睡 小时
            deepSleepHour = CommonUtils.getScaledDoubleValue(Double.valueOf(daySynopic.getDeepSleepMiute()), 1);
            double sleepTime = CommonUtils.getScaledDoubleValue(lightSleepHour + deepSleepHour, 1);
            lighthour = lightSleepHour +lightSleepHour ;
            deephour = deephour +deepSleepHour ;
        }
        deephour = deephour /daynumber ;
        lighthour = lighthour /daynumber ;

        int sleepHour = (int) (deephour+lighthour);
        int  sleepMinute = (int) ((deephour+lighthour-sleepHour)*60);
        MyLog.e(TAG,"sleepHour"+sleepHour+"sleepMinute"+sleepMinute);
        //       时间显示前面做加0操作
        if(sleepHour<10){
            sleepHourString = "0"+sleepHour;
        }else{
            sleepHourString = sleepHour+"" ;
        }
        if(sleepMinute<10){
            sleepMinuteString = "0"+sleepMinute;
        }else{
            sleepMinuteString = sleepMinute+"";
        }
        sleepHourTv.setText(sleepHourString+"");
        sleepMinuteTv.setText(sleepMinuteString+"");
        sleepDeepSleep.setText(CommonUtils.getScaledDoubleValue(deephour,1)+"");
        sleepLightSleep.setText(CommonUtils.getScaledDoubleValue(lighthour,1)+"");
        textView.setText(startData+" - "+endData);
    }
}

