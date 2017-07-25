package com.linkloving.dyh08.logic.UI.sleep;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.linkloving.band.dto.DaySynopic;
import com.linkloving.dyh08.R;
import com.linkloving.dyh08.ViewUtils.barchartview.BarChartView;
import com.linkloving.dyh08.utils.CommonUtils;
import com.linkloving.dyh08.utils.DbDataUtils;
import com.linkloving.dyh08.utils.logUtils.MyLog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by leo.wang on 2016/8/2.
 */
public class YearchartviewFragment extends Fragment {
    private static final String TAG = YearchartviewFragment.class.getSimpleName();
    private BarChartView barChartView;
    private BarChartView.BarChartItemBean[] items;
    private View view ;
    private ProgressBar progressbarYear;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tw_year_barchartview_fragment, container, false);
        barChartView = (BarChartView) view.findViewById(R.id.year_barchartView);
        barChartView.setPopupViewType(BarChartView.STEP_AVG_TYPE);
        progressbarYear = (ProgressBar) view.findViewById(R.id.year_progressbar);
        String date = getArguments().getString("yearDate");
        progressbarYear.setVisibility(View.VISIBLE);
        new YearChartAsynckTask().execute(date);

        return view ;
    }

    public  class YearChartAsynckTask extends AsyncTask<Object, Object, List<BarChartView.BarChartItemBean>> {
        String startData; //周开始时间
        String endData;   //周结束时间
        Date endDatesdf;
        Date startDatesdf ;
        String startDateformat;
        private String endDateMd;
        private String endDateformat;
        Context context ;

        protected List<BarChartView.BarChartItemBean> doInBackground(Object... params) {
//            wheelview ,需要加一.指针偏移小1 ;
            int value = 0 ;
            int year;
            String s = params[0].toString().trim();
            MyLog.e(TAG,s+"");
            year = Integer.parseInt(s)+1;
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdfStandard = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            int month = calendar.get(Calendar.MONTH);
//        MyLog.e(TAG,Integer.toString(month));
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            List<BarChartView.BarChartItemBean> barChartItemBeans = new ArrayList<>();
            for( int i=0 ;i<=month ;i++){
                value = i/month ;
                calendar.set(year,i,day);
                Date time = calendar.getTime();
                String dateFormat = sdfStandard.format(time);
                List<DaySynopic> monthDatainSql = DbDataUtils.findMonthDatainSql(context, sdfStandard, dateFormat, dateFormat);
                int stepSum = 0 ;
                int daySum = 0;
                float monthAverageNumber = 0 ;
                for (DaySynopic monthData :monthDatainSql) {
                    daySum ++ ;
                    //走路 步数
                    int walkStep = (int) (CommonUtils.getScaledDoubleValue(Double.valueOf(monthData.getWork_step()), 0));
                    //跑步 步数
                    int runStep = (int) (CommonUtils.getScaledDoubleValue(Double.valueOf(monthData.getRun_step()), 0));
                    int step = walkStep +runStep ;
                    stepSum+=step ;
                }
                monthAverageNumber = stepSum /daySum ;
                BarChartView.BarChartItemBean barChartItemBean = new BarChartView.BarChartItemBean(Integer.toString(i+1), monthAverageNumber);
                barChartItemBeans.add(barChartItemBean);
            }
            publishProgress(value);
            return barChartItemBeans;
        }
        @Override
        protected void onPostExecute(final List<BarChartView.BarChartItemBean> daySynopics) {
            super.onPostExecute(daySynopics);
            progressbarYear.setVisibility(View.GONE);
            barChartView.setItems(daySynopics);
            barChartView.setDialogListerer(new BarChartView.DialogListerer() {
                @Override
                public void showDialog(int i, int x, int y) {
//                    barChartView.showPopupWindow(view, (int) daySynopics.get(i).itemValue, x, y);
                }
                @Override
                public void dismissPopupWindow() {
                    barChartView.popupWindow.dismiss();
                }
            });
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
            progressbarYear.setProgress((Integer) values[0]);
        }
    }



}
