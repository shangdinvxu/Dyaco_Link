package com.linkloving.dyh08.ViewUtils.barchartview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.linkloving.dyh08.R;
import com.linkloving.dyh08.logic.UI.calories.barchartview.ScreenUtils;
import com.linkloving.dyh08.logic.UI.calories.barchartview.Utility;
import com.linkloving.dyh08.utils.DateSwitcher;
import com.linkloving.dyh08.utils.logUtils.MyLog;

import java.util.ArrayList;
import java.util.List;


/**
 * 自定义组件：条形统计图
 * Created by hanj on 14-12-30.
 */
public class WeekBarChartView extends View {
    private static final String TAG = WeekBarChartView.class.getSimpleName();
    private int screenW, screenH, screenHight;
    private List<WeekBarChartView.BarChartItemBean> mItems = new ArrayList<WeekBarChartView.BarChartItemBean>();
    //max value in mItems.
    private float maxValue;
    //max height of the bar
    private int maxHeight;
    /* private int[] mBarColors = new int[]{Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.MAGENTA, Color.CYAN};*/
    private int[] mBarColors = new int[]{Color.rgb(220, 220, 221)};
    public static final int STEP__TYPE = 1;
    public static final int STEP_AVG_TYPE = 2;
    public static final int CALORIES_TYPE = 3;
    public static final int CALORIES_AVG_TYPE = 4;
    public static final int DISTANCE__TYPE = 5;
    public static final int DISTANCE_AVG_TYPE = 6;
    public static final int CALORIES = 7;
    private Paint barPaint, linePaint, textPaint, barTextpaint;
    public Rect barRect, leftWhiteRect, rightWhiteRect;
    private Path textPath;
    private View popupView;
    private int leftMargin, topMargin, smallMargin;
    //the width of one bar item
    private int barItemWidth;
    //the spacing between two bar items.
    private int barSpace;
    //the width of the line.
    private int lineStrokeWidth;
    private  View showView ;

    /**
     * The x-position of y-index and the y-position of the x-index..
     */
    private float x_index_startY, y_index_startX;
    public PopupWindow popupWindow = new PopupWindow();

    private Bitmap arrowBmp;
    private Rect x_index_arrowRect, y_index_arrowRect;
    private boolean touchDown ;
    //    加载dialogview对象
    private static final int BG_COLOR = Color.parseColor("#ffffff");
    public DialogListerer mdialogListerer;
    private int popupViewWidth;
    private TextView timeView;
    private TextView step_number_textview;
    private  View anchorView ;

    public void setDialogListerer(DialogListerer DialogListerer) {
        this.mdialogListerer = DialogListerer;
    }

    public interface DialogListerer {
        void showDialog(int number, int x, int y);

        void dismissPopupWindow();
    }

    public WeekBarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    private void init(Context context) {


        screenW = ScreenUtils.getScreenW(context);
        y_index_startX = (float) (screenW*0.1);
//        因为获取的是设备屏幕的高度,所以我乘以了0.5
        screenHight = ScreenUtils.getScreenH(context);
        screenH = (int) (ScreenUtils.getScreenH(context) * 0.5);
        leftMargin = ScreenUtils.dp2px(context, 16);
        topMargin = ScreenUtils.dp2px(context, 40);
        smallMargin = ScreenUtils.dp2px(context, 6);

        barPaint = new Paint();
        barPaint.setColor(mBarColors[0]);
        barTextpaint = new Paint();
        linePaint = new Paint();
        lineStrokeWidth = ScreenUtils.dp2px(context, 1);
        linePaint.setStrokeWidth(lineStrokeWidth);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(7);
        barRect = new Rect(0, 0, 0, 0);
        textPath = new Path();

        leftWhiteRect = new Rect(0, 0, 0, screenH);
        rightWhiteRect = new Rect(screenW - leftMargin, 0, screenW, screenH);

        arrowBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.arrow_up);

    }

    //标记是否已经获取过状态拉的高度
    private boolean statusHeightHasGet;

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!statusHeightHasGet) {
            subStatusBarHeight();
            statusHeightHasGet = true;
        }

        //draw background
        canvas.drawColor(BG_COLOR);

        //bounds
        checkLeftMoving();

        textPaint.setTextSize(ScreenUtils.dp2px(getContext(), 12));
        for (int i = 0; i < mItems.size(); i++) {
            barRect.bottom = (int) (screenH * 0.53);
            barRect.left =((int) y_index_startX + barItemWidth * i + barSpace * (i + 1));
//            对值做个判断,如果为0的话设置一个高度,不然会很高 ;
            if (mItems.get(i).itemValue == 0) {
                barRect.top =  barRect.bottom-3 ;
            } else {
                barRect.top =  barRect.bottom-(int) (maxHeight * ( mItems.get(i).itemValue / maxValue));
            }
            barRect.right = barRect.left + barItemWidth;
            barPaint.setColor(Color.rgb(220, 220, 221));
            canvas.drawRect(barRect, barPaint);
            canvas.drawText(DateSwitcher.intWeekDateToString(i + 1),barRect.left,barRect.bottom+30 ,textPaint);
            if (i == iCheck) {
                barPaint.setColor(Color.rgb(251, 195, 0));
                canvas.drawRect(barRect, barPaint);
                barTextpaint.setColor(Color.WHITE);
                barTextpaint.setTextSize(30);
            }

        }

    }

    //    为PopipWindow设置一个type
    public int Popuptype;

    public void setPopupViewType(int type) {
        this.Popuptype = type;
    }


    /**
     * 点击柱状图出提示框
     *
     * @param x
     */
    public void showPopupWindow(View view,String text , int number, int x, int y) {
        switch (Popuptype) {
            case STEP__TYPE:
                popupView = LayoutInflater.from(getContext()).inflate(R.layout.tw_step_popupwiew, null);
                break;
            case STEP_AVG_TYPE:
                popupView = LayoutInflater.from(getContext()).inflate(R.layout.tw_step_avg_popupwiew, null);
                break;
            case CALORIES_TYPE:
                popupView = LayoutInflater.from(getContext()).inflate(R.layout.tw_calories_total_popupwiew, null);
                break;
            case CALORIES:
                popupView = LayoutInflater.from(getContext()).inflate(R.layout.tw_calories_total_popupwiew, null);
                break;
            case CALORIES_AVG_TYPE:
                popupView = LayoutInflater.from(getContext()).inflate(R.layout.tw_calories_avg_popupwiew, null);
                break;
            case DISTANCE__TYPE:
                popupView = LayoutInflater.from(getContext()).inflate(R.layout.tw_distance_popupwiew, null);
                break;
            case DISTANCE_AVG_TYPE:
                popupView = LayoutInflater.from(getContext()).inflate(R.layout.tw_distance_avg_popupwiew, null);
                break;
        }
        step_number_textview = (TextView) popupView.findViewById(R.id.dialog_number);
        step_number_textview.setText(number + "");
        popupViewWidth = popupView.getWidth();
        popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        timeView = (TextView) popupView.findViewById(R.id.time);
        timeView.setText(text);
        popupWindow.setTouchable(true);
        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setClippingEnabled(false);
        anchorView  = view ;
        popupWindow.showAsDropDown(view, x, y, Gravity.BOTTOM);
        MyLog.e(TAG,"touchDown++++++++++"+touchDown);
        showView = view ;
        MyLog.e("点击", "调用了popupwindow");

    }

    /**
     * i 用来传递点击的item,初始化为-1,不然就会默认点击第一个;
     */
    private int iCheck = -1;
    public float leftMoving;
    private float lastPointX;
    private float movingLeftThisTime = 0.0f;

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        int type = event.getAction();

        switch (type) {
            case MotionEvent.ACTION_DOWN:
                lastPointX = event.getRawX();
                int x = (int) event.getX();
                int y = (int) event.getY();
                if(popupWindow!=null||popupWindow.isShowing()){
                    mdialogListerer.dismissPopupWindow();
                }

                for (int i = 0; i < mItems.size(); i++) {
                    barRect.left = barItemWidth * i + barSpace * (i + 1) ;
                    int left = barRect.left;
//                    -60方便点击
                    if (mItems.get(i).itemValue == 0) {
                        barRect.top =  barRect.bottom-3 ;
                    } else {
                        barRect.top =  barRect.bottom-(int) (maxHeight * (1.0f - mItems.get(i).itemValue / maxValue));
                    }
                    int top = (int) (barRect.top-screenH);
                    barRect.right = barRect.left + barItemWidth;
                    int right = barRect.right;
                    touchDown = true ;
                    if (x > left && x < right && y<barRect.bottom&&y>barRect.top-screenH) {
                        iCheck = i;
                        x = (int) (barRect.left - screenW * 0.1);
                        y = (int) (-screenH + barRect.top + 0.078 * screenHight);
                        mdialogListerer.showDialog(i, (int) (x-screenW*0.13), y);
                        popupWindow.update(anchorView,(int) (lastPointX - screenW * 0.13),
                                (int) (-screenHight*0.13+barRect.bottom-(int) (maxHeight * (mItems.get(i).itemValue / maxValue))), -1, -1);

                        MyLog.e(TAG,(int) (lastPointX - screenW * 0.13)+" ----------" +
                                "----"+ (int) (-screenHight*0.13+barRect.bottom-(int) (maxHeight * (mItems.get(i).itemValue / maxValue))));
                        timeView.setText(mItems.get(i).itemType);
                        step_number_textview.setText((int)mItems.get(i).itemValue+"");
                        break;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (popupWindow.isShowing()){
                touchDown = false ;
                float x_move = event.getRawX();
                movingLeftThisTime = lastPointX - x_move;

                leftMoving += movingLeftThisTime;
                lastPointX = x_move;
//              自动清屏,屏幕刷新
                for (int i = 0; i < mItems.size(); i++){
                    if (((int) y_index_startX + barItemWidth * i + barSpace * (i + 1) - (int) leftMoving)<lastPointX
                            &&lastPointX< ((int) y_index_startX + barItemWidth * (i+1) + barSpace * (i + 2) - (int) leftMoving)){
                        popupWindow.update(anchorView,(int) (lastPointX - screenW * 0.13),
                                (int) (-screenHight*0.13+barRect.bottom-(int) (maxHeight * (mItems.get(i).itemValue / maxValue))), -1, -1);
                        MyLog.e(TAG,(int) (lastPointX - screenW * 0.13)+" ----------" +
                                "----              "+ (int) (-screenHight*0.13+barRect.bottom-(int) (maxHeight * (mItems.get(i).itemValue / maxValue))));
                        timeView.setText(mItems.get(i).itemType);
                        step_number_textview.setText((int)mItems.get(i).itemValue+"");
                        iCheck = i ;
                        invalidate();
                    }
                }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (popupWindow.isShowing()) {
                    mdialogListerer.dismissPopupWindow();
                }
                iCheck=-1;
                //smooth scroll
                new Thread(new SmoothScrollThread(movingLeftThisTime)).start();
                break;

            default:
                return super.onTouchEvent(event);
        }

        return true;
    }


    /**
     * Check the value of leftMoving to ensure that the view is not out of the screen.
     */
    private void checkLeftMoving() {
        if (leftMoving < 0) {
            leftMoving = 0;
        }

        if (leftMoving > (maxRight - minRight)) {
            leftMoving = maxRight - minRight;
        }
    }

    public List getItems() {
        return mItems;
    }


    public void setItems(List<WeekBarChartView.BarChartItemBean> items) {

        this.mItems = items;
        maxValue = items.get(0).itemValue;
        for (BarChartItemBean bean : items) {
            if (bean.itemValue > maxValue) {
                maxValue = bean.itemValue;
            }
        }
        //Calculate the max division value.
        getRange(maxValue, 0);

        //Get the width of each bar.
        getBarItemWidth(screenW, items.size());

        //Refresh the view.
        invalidate();

    }

    private int maxRight, minRight;

    /**
     * Get the width of each bar which is depended on the screenW and item count.
     */
    private void getBarItemWidth(int screenW, int itemCount) {
        //The min width of the bar is 50dp.
//        int minBarWidth = ScreenUtils.dp2px(getContext(), 20);
        int minBarWidth = 20;
        //The min width of spacing.
        int minBarSpacing = ScreenUtils.dp2px(getContext(), 3);
//        修改条目的宽度
//        barItemWidth = (screenW - leftMargin * 2) / (itemCount + 5);
//        barItemWidth = screenW / itemCount;
//        barItemWidth = 20;


//        修改条目的间隔
        barSpace = (int) ((screenW -y_index_startX*2- leftMargin * 2 - barItemWidth * itemCount) / (itemCount + 6));
        barSpace = 3;
        if (barSpace < minBarSpacing) {
//            barItemWidth = minBarWidth;
            barSpace = minBarSpacing;
        }
        barItemWidth = (int) ((screenW -y_index_startX*2-itemCount*barSpace)/itemCount);

        maxRight = (int) y_index_startX + lineStrokeWidth + (barSpace + barItemWidth) * mItems.size();
        minRight = screenW - leftMargin - barSpace;
    }

    /**
     * Sub the height of status bar and action bar to get the accurate height of screen.
     */
    private void subStatusBarHeight() {
        //The height of the status bar

//        long  statusHeight = ScreenUtils.getStatusBarHeight((Activity) getContext());
        //The height of the actionBar
        /*ActionBar ab = ((ActionBarActivity) getContext()).getSupportActionBar();
        int abHeight = ab == null ? 0 : ab.getHeight();*/
        int abHeight = 0;
//        screenH -= (statusHeight + abHeight);
        screenH -= (abHeight);
        barRect.top = topMargin * 2;
        barRect.bottom = screenH - topMargin * 3;
        maxHeight = barRect.bottom - barRect.top;

        x_index_startY = barRect.bottom;
        x_index_arrowRect = new Rect(screenW - leftMargin, (int) (x_index_startY - 10),
                screenW - leftMargin + 10, (int) (x_index_startY + 10));
    }

    private float maxDivisionValue, minDivisionValue;

    private void getRange(float maxValue, float minValue) {
        //max
        int scale = Utility.getScale(maxValue);
        float unscaledValue = (float) (maxValue / Math.pow(10, scale));

        maxDivisionValue = (float) (getRangeTop(unscaledValue) * Math.pow(10, scale));
        y_index_arrowRect = new Rect((int) (y_index_startX - 5), topMargin / 2 - 20,
                (int) (y_index_startX + 5), topMargin / 2);

    }

    private float getRangeTop(float value) {
        //value: [1,10)
        if (value < 1.2) {
            return 1.2f;
        }

        if (value < 1.5) {
            return 1.5f;
        }

        if (value < 2.0) {
            return 2.0f;
        }

        if (value < 3.0) {
            return 3.0f;
        }

        if (value < 4.0) {
            return 4.0f;
        }

        if (value < 5.0) {
            return 5.0f;
        }

        if (value < 6.0) {
            return 6.0f;
        }

        if (value < 8.0) {
            return 8.0f;
        }

        return 10.0f;
    }

    /**
     * Get the max width of the division value text.
     */
    private float getDivisionTextMaxWidth(float maxDivisionValue) {
        Paint textPaint = new Paint();
        textPaint.setTextSize(ScreenUtils.dp2px(getContext(), 10));

        float max = textPaint.measureText(String.valueOf(maxDivisionValue * 0.1f));
        for (int i = 2; i <= 10; i++) {
            float w = textPaint.measureText(String.valueOf(maxDivisionValue * 0.1f * i));
            if (w > max) {
                max = w;
            }
        }

        return max;
    }

    /**
     * Use this thread to create a smooth scroll after ACTION_UP.
     */
    private class SmoothScrollThread implements Runnable {
        float lastMoving;
        boolean scrolling = true;

        private SmoothScrollThread(float lastMoving) {
            this.lastMoving = lastMoving;
            scrolling = true;
        }

        @Override
        public void run() {
            while (scrolling) {
                long start = System.currentTimeMillis();
                lastMoving = (int) (0.9f * lastMoving);
                leftMoving += lastMoving;

                checkLeftMoving();
                postInvalidate();

                if (Math.abs(lastMoving) < 5) {
                    scrolling = false;
                }

                long end = System.currentTimeMillis();
                if (end - start < 20) {
                    try {
                        Thread.sleep(20 - (end - start));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * A model class to keep the bar item info.
     */
    public static class BarChartItemBean {
        public String itemType;
        public float itemValue;

        public BarChartItemBean(String itemType, float itemValue) {
            this.itemType = itemType;
            this.itemValue = itemValue;
        }
    }
}
