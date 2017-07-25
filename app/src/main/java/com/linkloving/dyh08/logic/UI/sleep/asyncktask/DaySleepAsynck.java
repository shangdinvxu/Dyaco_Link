package com.linkloving.dyh08.logic.UI.sleep.asyncktask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.linkloving.band.dto.SportRecord;
import com.linkloving.band.ui.BRDetailData;
import com.linkloving.band.ui.DatasProcessHelper;
import com.linkloving.dyh08.db.sport.UserDeviceRecord;
import com.linkloving.dyh08.logic.UI.sleep.chartview.DetailChartControl;
import com.linkloving.dyh08.logic.dto.UserEntity;
import com.linkloving.dyh08.utils.ToolKits;
import com.linkloving.dyh08.utils.logUtils.MyLog;
import com.linkloving.dyh08.utils.manager.AsyncTaskManger;
import com.linkloving.utils.TimeZoneHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/8/12.
 */
public class DaySleepAsynck extends AsyncTask<Object, Object, List<BRDetailData>> {
    private static final String TAG = DaySleepAsynck.class.getSimpleName();

    private Context context ;
    private ProgressDialog proDialog;

    private UserEntity userEntity;
    private DetailChartControl detailChart;
    private String sqlDate ;   // 2014-06-17

    /**
     * @param context
     * @param detailChart 图形view
     * @param proDialog   载入数据的dialog
     * @param userEntity  本地用户类
     */
    public DaySleepAsynck(Context context, DetailChartControl detailChart, ProgressDialog proDialog, UserEntity userEntity) {
        this.context = context;
        this.detailChart = detailChart;
        this.proDialog = proDialog;
        this.userEntity = userEntity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        proDialog.show();
    }

    @Override
    protected List<BRDetailData> doInBackground(Object... params) {
        sqlDate = (String) params[0];
        ArrayList<SportRecord> src= UserDeviceRecord.findHistoryChart(context, String.valueOf(userEntity.getUser_id()), sqlDate, sqlDate, true);
        List<BRDetailData> detailDatas = DatasProcessHelper.parseSR2BR(src);
        for (BRDetailData brDetailData:detailDatas){
            MyLog.e("DaySleepAsynck",brDetailData.toString());
        }
        return detailDatas;
    }

    @Override
    protected void onPostExecute(List<BRDetailData> brDetailDatas) {
        super.onPostExecute(brDetailDatas);
        SimpleDateFormat sim = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD);
        Date dateChart = null;
        try {
            dateChart = sim.parse(sqlDate);
        } catch (ParseException e) {
            return;
        }
        //时间偏移量
        int offset = TimeZoneHelper.getTimeZoneOffsetSecond();
        int dayindex= (int) ((dateChart.getTime()/1000+offset)/(30L)/2880);
        detailChart.initDayIndex(brDetailDatas,dayindex,dateChart);
        //UI层操作完毕后 dismiss进度条
        proDialog.dismiss();
        AsyncTaskManger.getAsyncTaskManger().removeAsyncTask(this);
    }
}
