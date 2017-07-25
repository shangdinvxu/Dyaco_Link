package com.linkloving.dyh08.logic.UI.distance;

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
import com.linkloving.dyh08.ViewUtils.barchartview.YearBarChartView;
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
    private YearBarChartView barChartView;
    private YearBarChartView.BarChartItemBean[] items;
    private View view ;
    private ProgressBar progressbarYear;
    private View anchorButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tw_year_barchartview_fragment, container, false);
        barChartView = (YearBarChartView) view.findViewById(R.id.year_barchartView);
        barChartView.setPopupViewType(BarChartView.DISTANCE_AVG_TYPE);
        anchorButton = view.findViewById(R.id.anchorButton);
        progressbarYear = (ProgressBar) view.findViewById(R.id.year_progressbar);
        String date = getArguments().getString("yearDate");
        progressbarYear.setVisibility(View.VISIBLE);
        new YearChartAsynckTask().execute(date);

        return view ;
    }

    public  class YearChartAsynckTask extends AsyncTask<Object, Object, List<YearBarChartView.BarChartItemBean>> {
        String startData; //周开始时间
        String endData;   //周结束时间
        Date endDatesdf;
        Date startDatesdf ;
        String startDateformat;
        private String endDateMd;
        private String endDateformat;
        Context context ;
        private List<YearBarChartView.BarChartItemBean> barChartItemBeans;
        private String substring;

        protected List<YearBarChartView.BarChartItemBean> doInBackground(Object... params) {
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
            barChartItemBeans = new ArrayList<>();
            for( int i=0 ;i<=month ;i++){
                if (month==0){
                    value=1;
                }else {
                    value = i/month ;
                }
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
                    int walkDistance = (int) (CommonUtils.getScaledDoubleValue(Double.valueOf(monthData.getWork_distance()), 0));
                    //跑步 里程
                    int runDistance = (int) (CommonUtils.getScaledDoubleValue(Double.valueOf(monthData.getRun_distance()), 0));
                    int distance = walkDistance + runDistance;
                    stepSum+=distance ;
                }
                monthAverageNumber = stepSum /daySum ;
                substring = String.valueOf(year).substring(2);
                String s1 = Integer.toString(i+1);
                if (s1.length()<2){
                    s1= "0"+s1 ;
                }
                YearBarChartView.BarChartItemBean barChartItemBean = new YearBarChartView.BarChartItemBean(substring +"/"+s1, monthAverageNumber);
                barChartItemBeans.add(barChartItemBean);
            }
            int size = barChartItemBeans.size();
            int yearLeft = 12-barChartItemBeans.size() ;
            for (int i = 0 ;i<yearLeft;i++){
                barChartItemBeans.add(new YearBarChartView.BarChartItemBean(substring +"/"+(size+i+1),0));
            }
            publishProgress(value);
            return barChartItemBeans;
        }
        @Override
        protected void onPostExecute(final List<YearBarChartView.BarChartItemBean> daySynopics) {
            super.onPostExecute(daySynopics);
            progressbarYear.setVisibility(View.GONE);
            barChartView.setItems(daySynopics);
            barChartView.setDialogListerer(new YearBarChartView.DialogListerer() {
                @Override
                public void showDialog(int i, int x, int y) {
                    String itemType = barChartItemBeans.get(i).itemType;
                    String[] split = itemType.split("/");
                    int i1 = Integer.parseInt(split[1]);
                    String finalString = split[0]+"/"+(i1+1);
                    barChartView.showPopupWindow(anchorButton, finalString,(int) daySynopics.get(i).itemValue, x, y);
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
