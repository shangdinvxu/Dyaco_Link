package com.linkloving.dyh08.logic.UI.distance;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.linkloving.band.dto.DaySynopic;
import com.linkloving.dyh08.R;
import com.linkloving.dyh08.ViewUtils.barchartview.BarChartView;
import com.linkloving.dyh08.ViewUtils.barchartview.MonthBarChartView;
import com.linkloving.dyh08.utils.CommonUtils;
import com.linkloving.dyh08.utils.DbDataUtils;
import com.linkloving.dyh08.utils.ToolKits;
import com.linkloving.dyh08.utils.logUtils.MyLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by leo.wang on 2016/8/2.
 */
public class MonthchartviewFragment extends Fragment {
    private static final String TAG = MonthchartviewFragment.class.getSimpleName();

    MonthBarChartView barChartView ;
    private View view;
    private MonthBarChartView.BarChartItemBean[] items;
    private List<MonthBarChartView.BarChartItemBean> month;
    private View anchorButton;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tw_month_barchartview_fragment, container, false);
         barChartView = (MonthBarChartView) view.findViewById(R.id.month_barchartView);
        barChartView.setPopupViewType(BarChartView.DISTANCE__TYPE);
        anchorButton = view.findViewById(R.id.anchor_button);
        String date = getArguments().getString("monthDate");
        new MonthChartAsynckTask().execute(date);
        return view ;
    }
    public  class MonthChartAsynckTask extends AsyncTask<Object, Object, List<DaySynopic>> {
        private  final String[] PLANETS = new String[]{"January", "February", "March", "April", "May", "June", "July",
                "August", "September", "October", "November", "December"};
        private Date dateWithI;
        private String firstDayOfMonth;
        private String lastDayOfMonth;
        private int year;
        private String[] splitdata;
        private String[] split = {"","",""};
        private Date parseDate;
        String param ;

        protected List<DaySynopic> doInBackground(Object... params) {

            param = params[0].toString();
            //            如果没有的话则说明选择的是今天的,手动给他一个日期
            MyLog.e("param1",param);
            if (param == null || param.length() <= 0){
                Date time = Calendar.getInstance().getTime();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
                String todayParam = simpleDateFormat.format(time);
                param = todayParam ;
            }
            MyLog.e("param1",param);
            String[] split = param.split("-");
            String year = split[0];
            String month = split[1];
            firstDayOfMonth = CommonUtils.getFirstDayOfMonth(Integer.parseInt(year), Integer.parseInt(month));
            lastDayOfMonth = CommonUtils.getLastDayOfMonth(Integer.parseInt(year), Integer.parseInt(month));
            MyLog.e("firstDayOfMonth","firstDayOfMonth"+ firstDayOfMonth +"_______"+ lastDayOfMonth);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd ");
            return DbDataUtils.findMonthDatainSql(getActivity(), simpleDateFormat, firstDayOfMonth, lastDayOfMonth);
        }
        @Override
        protected void onPostExecute(List<DaySynopic> daySynopics) {
            super.onPostExecute(daySynopics);
            int step = 0;
            month = new ArrayList<>();
            Calendar rightNow = Calendar.getInstance();
            SimpleDateFormat sim = new SimpleDateFormat("MM/dd");
            for(DaySynopic daySynopic:daySynopics){
                int walkDistance = (int) (CommonUtils.getScaledDoubleValue(Double.valueOf(daySynopic.getWork_distance()), 0));
                //跑步 里程
                int runDistance = (int) (CommonUtils.getScaledDoubleValue(Double.valueOf(daySynopic.getRun_distance()), 0));
                int distance = walkDistance + runDistance;

                String date = daySynopic.getData_date() ;
                try {
                    parseDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String format = sim.format(parseDate);
                MonthBarChartView.BarChartItemBean barChartItemBean = new MonthBarChartView.BarChartItemBean(format, distance);
                MyLog.e(TAG, barChartItemBean.itemType.toString() + "=======" + Integer.toString((int) barChartItemBean.itemValue));
                month.add(barChartItemBean);
            }
            int monthMount = ToolKits.getMonthMount(parseDate);
            int leftSize = monthMount - month.size();
            for (int i = 0 ;i<leftSize;i++){
                Calendar instance = Calendar.getInstance();
                instance.setTime(parseDate);
                instance.add(Calendar.DAY_OF_YEAR,1+i);
                Date time = instance.getTime();
                String format = sim.format(time);
                month.add(new MonthBarChartView.BarChartItemBean(format,0));
            }
            barChartView.setItems(month);
            barChartView.setDialogListerer(new MonthBarChartView.DialogListerer() {
                @Override
                public void showDialog(int i, int x, int y) {
                    barChartView.showPopupWindow(anchorButton, month.get(i).itemType,(int) month.get(i).itemValue, x, y);
                }
                @Override
                public void dismissPopupWindow() {
                    barChartView.popupWindow.dismiss();
                }
            });
        }
    }

}
