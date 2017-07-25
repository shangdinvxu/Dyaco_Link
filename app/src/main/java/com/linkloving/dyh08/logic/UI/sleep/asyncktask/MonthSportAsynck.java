package com.linkloving.dyh08.logic.UI.sleep.asyncktask;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import com.linkloving.band.dto.DaySynopic;
import com.linkloving.dyh08.ViewUtils.drawAngle.DrawArc;
import com.linkloving.dyh08.prefrences.PreferencesToolkits;
import com.linkloving.dyh08.utils.CommonUtils;
import com.linkloving.dyh08.utils.DateSwitcher;
import com.linkloving.dyh08.utils.DbDataUtils;
import com.linkloving.dyh08.utils.logUtils.MyLog;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.List;

/**
 * Created by Daniel.Xu on 2016/8/8.
 */
public class MonthSportAsynck extends AsyncTask<Object, Object, List<DaySynopic>> {
    private static final String TAG = MonthSportAsynck.class.getSimpleName();
    Object object;
    DrawArc drawArc;
    TextView stepNumber ;
    private String sleepHourString;
    private String sleepMinuteString;

    private static final String[] PLANETS = new String[]{"January", "February", "March", "April", "May", "June", "July",
            "August", "September", "October", "November", "December"};
    private Date dateWithI;
    private int month;
    private String firstDayOfMonth;
    private String lastDayOfMonth;
    private int year;
    private String[] splitdata;
    private String[] split = {"","",""};
    String param ;
    String datasdf;
    TextView sleepHourTv ;
    TextView sleepMinuteTv ;
    TextView sleepDeepSleep ;
    TextView sleepLightSleep ;
    TextView textView ;
    Context context ;
    /**
     * @param textView 更新日期
     * @param context  上下文对象
     */
    public MonthSportAsynck(TextView sleepHourTv,TextView sleepMinuteTv,TextView sleepDeepSleep,TextView sleepLightSleep,
                            TextView textView, Context context) {
        this.sleepHourTv = sleepHourTv ;
        this.sleepMinuteTv = sleepMinuteTv ;
        this.sleepDeepSleep = sleepDeepSleep ;
        this.sleepLightSleep = sleepLightSleep ;
        this.textView = textView ;
        this.context = context ;
    }
    @Override
    protected List<DaySynopic> doInBackground(Object... params) {
        param = params[0].toString();
        String[] split = param.split("-");
        String year = split[0];
        String month = split[1];
        firstDayOfMonth = CommonUtils.getFirstDayOfMonth(Integer.parseInt(year), Integer.parseInt(month)-1);
        lastDayOfMonth = CommonUtils.getLastDayOfMonth(Integer.parseInt(year), Integer.parseInt(month)-1);
        MyLog.e("firstDayOfMonth","firstDayOfMonth"+ firstDayOfMonth +"_______"+ lastDayOfMonth);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd ");
        return DbDataUtils.findMonthDatainSql(context, simpleDateFormat, firstDayOfMonth, lastDayOfMonth);
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
        if (daynumber==0){
            deephour =0 ;
            lighthour = 0 ;
        }else{
        deephour = deephour /daynumber ;
        lighthour = lighthour /daynumber ;
        }
        int sleepHour = (int) (CommonUtils.getScaledDoubleValue(deephour, 1) + CommonUtils.getScaledDoubleValue(lighthour, 1));
        int  sleepMinute = (int) ((CommonUtils.getScaledDoubleValue(deephour,1)+CommonUtils.getScaledDoubleValue(lighthour,1)-sleepHour)*60);
        MyLog.e(TAG, "sleepHour" + sleepHour + "sleepMinute" + sleepMinute);
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
        sleepDeepSleep.setText(CommonUtils.getScaledDoubleValue(deephour, 1)+"");
        sleepLightSleep.setText(CommonUtils.getScaledDoubleValue(lighthour, 1)+"");
          textView.setText(param);
    }
}
