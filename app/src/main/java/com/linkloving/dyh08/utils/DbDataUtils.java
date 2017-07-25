package com.linkloving.dyh08.utils;

import android.app.Activity;
import android.content.Context;

import com.linkloving.band.dto.DaySynopic;
import com.linkloving.dyh08.MyApplication;
import com.linkloving.dyh08.db.summary.DaySynopicTable;
import com.linkloving.dyh08.logic.UI.step.StepActivity;
import com.linkloving.dyh08.logic.dto.UserEntity;
import com.linkloving.dyh08.utils.logUtils.MyLog;
import com.linkloving.dyh08.utils.sportUtils.SportDataHelper;
import com.linkloving.utils.TimeZoneHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Daniel.Xu on 2016/8/8.
 */
public class DbDataUtils {

    private static final String TAG = CommonUtils.class.getSimpleName();
    /**
     * 从数据库查询汇总数据流程（多日）
     * @param startData
     * @param endData
     * @return
     */
    public static List<DaySynopic> findWeekDatainSql(Context context, SimpleDateFormat simpleDateFormat, String startData, String endData){
        List<DaySynopic> mDaySynopicArrayList;
        Date startDatesdf = null;
        try {
             startDatesdf = simpleDateFormat.parse(startData);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        UserEntity userEntity = MyApplication.getInstance(context).getLocalUserInfoProvider();
        //去汇总数据表中查询数据
//        mDaySynopicArrayList = DaySynopicTable.findDaySynopicRange(context, userEntity.getUser_id() + "", startData, endData, String.valueOf(TimeZoneHelper.getTimeZoneOffsetMinute()));
//        MyLog.e(TAG, startData + "到" + endData + "的汇总数据是:" + mDaySynopicArrayList.toString());
        mDaySynopicArrayList = ToolKits.getFindWeekData((Activity) context,startDatesdf,userEntity);
        return mDaySynopicArrayList;
    }

    public static List<DaySynopic> findMonthDatainSql(Context context, SimpleDateFormat simpleDateFormat ,String startData, String endData){
        List<DaySynopic> mDaySynopicArrayList;
        Date startDatesdf = null;
        try {
            startDatesdf = simpleDateFormat.parse(startData);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        UserEntity userEntity = MyApplication.getInstance(context).getLocalUserInfoProvider();
        //去汇总数据表中查询数据
//        mDaySynopicArrayList = DaySynopicTable.findDaySynopicRange(context, userEntity.getUser_id() + "", startData, endData, String.valueOf(TimeZoneHelper.getTimeZoneOffsetMinute()));
//        MyLog.e(TAG, startData + "到" + endData + "的汇总数据是:" + mDaySynopicArrayList.toString());
        mDaySynopicArrayList = ToolKits.getFindMonthData((Activity) context,startDatesdf,userEntity);
        return mDaySynopicArrayList;
    }

    /**
     * 数据库查询汇总数据流程（多日）
     * @param context
     * @param startData
     * @param endData
     * @return
     */
    public static DaySynopic findDayDatainSql(Context context, String startData, String endData){
        DaySynopic mDaySynopic;
        UserEntity userEntity = MyApplication.getInstance(context).getLocalUserInfoProvider();
        //去汇总数据表中查询数据
        ArrayList<DaySynopic> mDaySynopicArrayList = DaySynopicTable.findDaySynopicRange(context, userEntity.getUser_id() + "", startData, endData, String.valueOf(TimeZoneHelper.getTimeZoneOffsetMinute()));
        MyLog.e(TAG, startData+"到"+endData+"的汇总数据是:" + mDaySynopicArrayList.toString());
        // 在判断一次,如果得到集合是空,
        // 意味着汇总数据表中没有这一天的数据,
        // 那我就去明细表里去查询并且汇总数据
        if (mDaySynopicArrayList.size() == 0) {
            mDaySynopic = SportDataHelper.offlineReadMultiDaySleepDataToServer(context, startData, endData); //去明细表里去查询并且汇总数据
            MyLog.e(TAG, "daySynopic:" + mDaySynopic.toString());
            DaySynopicTable.saveToSqliteAsync(context, mDaySynopicArrayList, userEntity.getUser_id() + "");  //计算完汇总数据后 存储到汇总表里面哦
        } else {
            mDaySynopic = mDaySynopicArrayList.get(0);  //直接取出来
        }
        return mDaySynopic;
    }
}
