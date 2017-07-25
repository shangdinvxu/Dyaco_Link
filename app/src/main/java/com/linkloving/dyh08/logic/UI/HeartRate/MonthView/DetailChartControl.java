package com.linkloving.dyh08.logic.UI.HeartRate.MonthView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.linkloving.band.dto.SportRecord;
import com.linkloving.band.ui.BRDetailData;
import com.linkloving.dyh08.R;
import com.linkloving.dyh08.ViewUtils.barchartview.ScreenUtils;
import com.linkloving.dyh08.logic.UI.HeartRate.GreendaoUtils;
import com.linkloving.dyh08.logic.UI.sleep.chartview.ChartParameter;
import com.linkloving.dyh08.logic.UI.sleep.chartview.DetailBitmapCreator;
import com.linkloving.dyh08.utils.logUtils.MyLog;
import com.zhy.autolayout.AutoRelativeLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import Trace.GreenDao.heartrate;

/**
 * Created by leo.wang on 2016/4/12.
 */
public class DetailChartControl extends RelativeLayout {
    private final String TAG = DetailChartControl.class.getSimpleName();
    //有两个textview,用来显示开始和结束时间
    ///还有一个 LinearLayout
    ImageView dataView;
    ImageView lineView;
    FrameLayout linearLayout;
//    TextView timetv;

    Context context;
    int imageWidth;
    int imageHeight;
    /** 每个时间片对应像素宽度（px/片) */
    float xScale;
    float yScale;
    /** // 每个像素xxx分钟 */
    float xlineScale;
    //图表日期
    Date firstSundayOfThisMonth;
    /** 图片生成器 */
    DetailBitmapCreator detailBitmapCreator2;
    // !!!!!!!!!!!!
    private List<BRDetailData> detailDatas = new ArrayList<BRDetailData>();

    private List<SportRecord> sportRecords=new ArrayList<>();
    private int screenW,screenH ;
    public PopupWindow popupWindow = new PopupWindow();
    private String nowtimeString;

    int dayindexNow;
    String timeNow;
    private long nowtime;
    private FrameLayout framelayout;
    private int i = 0 ;
    private TextView timeView;
    private TextView avgView;
    private TextView maxView;
    private View popupView;
    private GreendaoUtils greendaoUtils;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public DetailChartControl(Context context) {
        super(context);
        InitDetailChartControl(context);
    }
    public DetailChartControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        InitDetailChartControl(context);
    }
    public DetailChartControl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        InitDetailChartControl(context);
    }
    /**
     * 初始化图表
     *
     * @param context
     */
    private void InitDetailChartControl(Context context)
    {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         screenW = ScreenUtils.getScreenW(context);
       screenH = ScreenUtils.getScreenH(context);
        View view = inflater.inflate(R.layout.tw_heartrate_chat, this);
        framelayout = (FrameLayout) view.findViewById(R.id.chat);
        LayoutParams layoutParams = (LayoutParams) framelayout.getLayoutParams();
        layoutParams.topMargin = (int) (screenH*0.05);
        layoutParams.leftMargin= (int) (screenW *0.198);
        layoutParams.width = (int) (screenW *0.724);
        layoutParams.height = (int) (screenH*0.43);
        MyLog.e(TAG, "-----topMargin+"+layoutParams.topMargin+"-----leftMargin+"+layoutParams.leftMargin+"-----width+"+layoutParams.width+"-----height+"+layoutParams.height);
        framelayout.setLayoutParams(layoutParams);
        InitView();
        getViewHigh();
        popupView = LayoutInflater.from(getContext()).inflate(R.layout.tw_heartrate_popupwindow, null);
        timeView = (TextView) popupView.findViewById(R.id.popuptime);
        avgView = (TextView) popupView.findViewById(R.id.avg);
        maxView = (TextView) popupView.findViewById(R.id.max);
        greendaoUtils = new GreendaoUtils(context);
    }
    private void InitView()
    {
        this.dataView = (ImageView) findViewById(R.id.sleep_chat1);
        linearLayout= (FrameLayout) findViewById(R.id.chat);
        lineView = (ImageView) findViewById(R.id.line_chat);
//        timetv = (TextView) findViewById(R.id.time);
        linearLayout.setOnTouchListener(new OnTouchListenerImpl());
    }
    /**
     * 点击柱状图出提示框
     *
     * @param x
     */
    public void showPopupWindow(View view, String number, int x, int y) {
        timeView.setText(number);
        popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setClippingEnabled(true);
/*//        有参数的话，就是一view的左下角进行偏移，xoff正的向左，负的向右.
//        View stepAcitvityLayout = findViewById(R.id.step_activity_layout);*/
        popupWindow.showAsDropDown(view, x, y);
        MyLog.e("点击", "调用了popupwindow");
    }

    private class OnTouchListenerImpl implements OnTouchListener{
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            MyLog.e(TAG,"onTouch X:"+event.getX());
            int point = -1 ;
            if(event.getX()> 0 && event.getX()<dataView.getWidth()) {
                for (int i = 0; i < 31; i++) {
                    if ((screenW * (i * 0.024 - 0.01)) < event.getX() && event.getX() < (screenW * (0.01 + i * 0.024))) {
                        point = i;
                        long time = firstSundayOfThisMonth.getTime();
                        /**要注意都是long 类型的*/
                        time = time + 86400000l * i;
                        Date date = new Date(time);
                        MyLog.e(TAG,"time"+time);
                        String format = simpleDateFormat.format(date);
                        List<heartrate> heartrates = searRecord(time);
                        if (heartrates.size() == 0) {
                            MyLog.e(TAG, "heartrates.size()为0");
                            maxView.setText("0");
                            avgView.setText("0");
                        } else {
                            MyLog.e(TAG, "heartrates.size()不为0");
                            int avg = 0 ;
                            int max = 0 ;
                            for (heartrate h :heartrates){
                                avg = h.getAvg()+avg ;
                                max = h.getMax()+max ;
                            }
                            maxView.setText(max/heartrates.size()+ "");
                            avgView.setText(avg/heartrates.size() + "");
                        }
                        timeView.setText(format);
                        MyLog.e(TAG, "时间是+" + format);
                        MyLog.e(TAG, "point是" + point);
                    }
                }
                moveLineViewWithFinger(lineView,event.getX());
            }
            if (event.getAction()==MotionEvent.ACTION_UP){
                i=0;
            }
            return true;
        }

        private List<heartrate> searRecord(long nowtime) {
            List<heartrate> heartrates = greendaoUtils.searchOneDay(nowtime,nowtime+86400000);
            return heartrates;
        }
    }

    private void moveTimeViewWithFinger(View view, float rawX) {
        MyLog.e(TAG,"view.getLeft()："+view.getLeft());
        MyLog.e(TAG,"view.getRight()："+view.getRight());
        MyLog.e(TAG,"rawX："+rawX);
        if(lineView.getLeft()<view.getWidth()/2){
            AutoRelativeLayout.LayoutParams layoutParams = (AutoRelativeLayout.LayoutParams) view.getLayoutParams();
            layoutParams.leftMargin = 0;
            view.setLayoutParams(layoutParams);
            return;
        }
        if((dataView.getWidth()-lineView.getRight())<view.getWidth()/2 ){
            AutoRelativeLayout.LayoutParams layoutParams = (AutoRelativeLayout.LayoutParams) view.getLayoutParams();
            layoutParams.rightMargin = 0;
            view.setLayoutParams(layoutParams);
            return;
        }
        AutoRelativeLayout.LayoutParams layoutParams = (AutoRelativeLayout.LayoutParams) view.getLayoutParams();
        layoutParams.leftMargin = (int) rawX - view.getWidth() / 2;
//        layoutParams.leftMargin = (int) (rawX- screenW*0.15);
        view.setLayoutParams(layoutParams);


    }
    public void showFirstPopupWindow() {
//        View popupView = LayoutInflater.from(getContext()).inflate(R.layout.tw_heartrate_popupwindow, null);
//        TextView time_popupwindow = (TextView) popupView.findViewById(R.id.popuptime);
        popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setClippingEnabled(false);
/*//        有参数的话，就是一view的左下角进行偏移，xoff正的向左，负的向右.
//        View stepAcitvityLayout = findViewById(R.id.step_activity_layout);*/
        popupWindow.showAsDropDown(framelayout, 20, 20);
        MyLog.e("点击", "调用了popupwindow");
    }

    /**
     * 设置View的布局属性，使得view随着手指移动 注意：view所在的布局必须使用RelativeLayout 而且不得设置居中等样式
     *
     * @param view
     * @param rawX
     */
    private void moveLineViewWithFinger(View view, float rawX) {
        AutoRelativeLayout.LayoutParams layoutParams = (AutoRelativeLayout.LayoutParams) view.getLayoutParams();
        layoutParams.leftMargin = (int) rawX - view.getWidth() / 2;
//        layoutParams.leftMargin = (int) (rawX- screenW*0.15);
        view.setLayoutParams(layoutParams);
        if (i ==0 ){
            showFirstPopupWindow();
            i++;
        }else{
            popupWindow.update((int) rawX+(int)(screenW*0.027), (int) (screenH*0.37),-1,-1,false);
        }
    }

    private void initValues()
    {
        initXScale();
        detailBitmapCreator2 = new DetailBitmapCreator(context);
        detailBitmapCreator2.initChartParameter(new ChartParameter(xScale, yScale, imageWidth, imageHeight));
    }
    private void initXScale()
    {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;//用屏幕的宽度
        imageWidth =dataView.getWidth();
        xScale = (float) imageWidth / (120*24f);// 24小时一屏,120是一个小时的时间片(30s一片)  每个时间片所占的像素
        xlineScale = (60*24f) / (float) imageWidth;// 每个像素xxx分钟
        yScale = imageHeight / 80;
    }
        private void  getViewHigh(){
        final ViewTreeObserver vto = linearLayout.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                imageHeight= linearLayout.getMeasuredHeight();
                int width = DetailChartControl.this.getMeasuredWidth();
                initValues();
                MyLog.i("linearLayout获取到的高度:" + imageHeight+"   "+width);
                return true;
            }
        });
    }
    /**
     * 添加图表新条目（分为 左边添加 和 右边添加 两种）
     * @param
     * @return
     */
    private void AsyncAddDetailChart()
    {
        new AsyncTask<Void,Void,List<Bitmap>>(){
            @Override
            protected List<Bitmap> doInBackground(Void... params) {
                List<Bitmap> bitmaps=new ArrayList<Bitmap>();
                Bitmap bitmapChart2=detailBitmapCreator2.drawDetailChart(detailDatas,dayindexNow);
                bitmaps.add(bitmapChart2);
                return bitmaps;
            }

            @Override
            protected void onPostExecute(List<Bitmap> bitmaps) {
                dataView.setImageBitmap(bitmaps.get(0));
            }
        }.execute();
    }

    public void initDayIndex( Date date)
    {
        dataView.setImageBitmap(null);
        firstSundayOfThisMonth =date;


    }






}
