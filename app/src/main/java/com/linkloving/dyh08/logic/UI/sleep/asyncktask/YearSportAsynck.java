//package com.linkloving.dyh08.logic.UI.sleep.asyncktask;
//
//import android.content.Context;
//import android.os.AsyncTask;
//import android.widget.TextView;
//
//import com.linkloving.band.dto.DaySynopic;
//import com.linkloving.dyh08.ViewUtils.drawAngle.DrawArc;
//import com.linkloving.dyh08.prefrences.PreferencesToolkits;
//import com.linkloving.dyh08.utils.CommonUtils;
//import com.linkloving.dyh08.utils.DbDataUtils;
//import com.linkloving.dyh08.utils.logUtils.MyLog;
//
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//import java.util.Locale;
//
///**
// * Created by Daniel.Xu on 2016/8/8.
// */
//public class YearSportAsynck extends AsyncTask<Object, Object, Integer> {
//    private static final String TAG = YearSportAsynck.class.getSimpleName();
//    Object object;
//    DrawArc drawArc;
//    private int year;
//    TextView stepNumber ;
//
//    TextView sleepHourTv ;
//    TextView sleepMinuteTv ;
//    TextView sleepDeepSleep ;
//    TextView sleepLightSleep ;
//    TextView textView ;
//    Context context ;
//    /**
//     * @param textView 更新日期
//     * @param context  上下文对象
//     */
//    public YearSportAsynck(TextView sleepHourTv,TextView sleepMinuteTv,TextView sleepDeepSleep,TextView sleepLightSleep,
//                           TextView textView, Context context) {
//        this.sleepHourTv = sleepHourTv ;
//        this.sleepMinuteTv = sleepMinuteTv ;
//        this.sleepDeepSleep = sleepDeepSleep ;
//        this.sleepLightSleep = sleepLightSleep ;
//        this.textView = textView ;
//        this.context = context ;
//    }
//
//    @Override
//    protected Integer doInBackground(Object... params) {
//        String s = params[0].toString().trim();
//        year = Integer.parseInt(s);
//        Calendar calendar = Calendar.getInstance();
//        SimpleDateFormat sdfStandard = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//        int month = calendar.get(Calendar.MONTH);
////        MyLog.e(TAG,Integer.toString(month));
//        int day = calendar.get(Calendar.DAY_OF_MONTH);
//        int daySum = 0;
//        int stepSum = 0 ;
//        for( int i=0 ;i<=month ;i++){
//            calendar.set(year,i,day);
//            Date time = calendar.getTime();
//            String dateFormat = sdfStandard.format(time);
//            List<DaySynopic> monthDatainSql = DbDataUtils.findMonthDatainSql(context, sdfStandard, dateFormat, dateFormat);
//            for (DaySynopic monthData :monthDatainSql) {
//                daySum ++ ;
//                //走路 步数
//                int walkStep = (int) (CommonUtils.getScaledDoubleValue(Double.valueOf(monthData.getWork_step()), 0));
//                //跑步 步数
//                int runStep = (int) (CommonUtils.getScaledDoubleValue(Double.valueOf(monthData.getRun_step()), 0));
//                int step = walkStep +runStep ;
//                stepSum+=step ;
//            }}
//        MyLog.e(TAG,stepSum+"step"+daySum);
//        return stepSum/daySum;
//    }
//    @Override
//    protected void onPostExecute( Integer averageStepnumber) {
//        super.onPostExecute(averageStepnumber);
//        deephour = deephour /daynumber ;
//        lighthour = lighthour /daynumber ;
//
//        int sleepHour = (int) (deephour+lighthour);
//        double sleepMinute = (deephour+lighthour-sleepHour)*60;
//        sleepHourTv.setText(sleepHour+"");
//        sleepMinuteTv.setText(sleepMinute+"");
//        sleepDeepSleep.setText(deepSleepHour+"");
//        sleepLightSleep.setText(lighthour+"");
//        textView.setText(year+"");
//    }
//
//
//}
