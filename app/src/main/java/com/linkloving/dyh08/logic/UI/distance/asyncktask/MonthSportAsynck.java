package com.linkloving.dyh08.logic.UI.distance.asyncktask;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import com.linkloving.band.dto.DaySynopic;
import com.linkloving.dyh08.ViewUtils.drawAngle.DrawArc;
import com.linkloving.dyh08.prefrences.PreferencesToolkits;
import com.linkloving.dyh08.utils.CommonUtils;
import com.linkloving.dyh08.utils.DbDataUtils;
import com.linkloving.dyh08.utils.ToolKits;
import com.linkloving.dyh08.utils.logUtils.MyLog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Daniel.Xu on 2016/8/8.
 */
public class MonthSportAsynck extends AsyncTask<Object, Object, List<DaySynopic>> {
    private static final String TAG = MonthSportAsynck.class.getSimpleName();
    Object object;
    DrawArc drawArc;
    TextView textView;
    Context context;
    TextView stepNumber ;
    private int localSettingUnitInfo;

    private static final String[] PLANETS = new String[]{"January", "February", "March", "April", "May", "June", "July",
            "August", "September", "October", "November", "December"};
    private Date dateWithI;
    private int month;
    private String firstDayOfMonth;
    private String lastDayOfMonth;
    private int year;
    private String[] splitdata;
    private String[] split = {"","",""};
    private Double distanceDB;


    String param ;

    /**
     * @param object   用于传Asynck执行的对象
     * @param drawArc  画中心圆
     * @param textView 更新日期
     * @param context  上下文对象
     */
    public MonthSportAsynck(Object object,  TextView stepNumber ,DrawArc drawArc, TextView textView, Context context) {
        this.stepNumber = stepNumber ;
        this.object = object;
        this.drawArc = drawArc;
        this.textView = textView;
        this.context = context;
    }
    @Override
    protected List<DaySynopic> doInBackground(Object... params) {
        localSettingUnitInfo = PreferencesToolkits.getLocalSettingUnitInfo(context);
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
        int step = 0;
        int daynumber = 0;
        int stepnumber= 0 ;
        for(DaySynopic daySynopic:daySynopics){
            daynumber +=1 ;
            //走路 步数
            MyLog.e(TAG,daySynopic.toString());
            //走路 步数
            int walkDistance = (int) (CommonUtils.getScaledDoubleValue(Double.valueOf(daySynopic.getWork_distance()), 0));
            //跑步 里程
            int runDistance = (int) (CommonUtils.getScaledDoubleValue(Double.valueOf(daySynopic.getRun_distance()), 0));
            int distance = walkDistance + runDistance;
            stepnumber = stepnumber+distance  ;
            MyLog.e(TAG,stepnumber+"======"+daynumber);
        }
        if (daynumber == 0){
            daynumber =1 ;
        }
        stepnumber = stepnumber /daynumber ;
        int stepGoal = Integer.parseInt(PreferencesToolkits.getGoalInfo(context, PreferencesToolkits.KEY_GOAL_DISTANCE));
        float stepPercent  = (float)stepnumber/(stepGoal*1000) ;
        //此时去更新UI
        double i = (double)stepnumber / 1000;
        if (i==0){
            distanceDB = 0.0 ;
        }else{
            if (localSettingUnitInfo!= ToolKits.UNIT_GONG){
                i = i*0.6214 ;
            }
            distanceDB = (Math.round(i * 100 + 0.5) / 100.0);
        }

        stepNumber.setText(Double.toString(distanceDB));
        drawArc.setPercent(stepPercent);
          textView.setText(param);
    }
}
