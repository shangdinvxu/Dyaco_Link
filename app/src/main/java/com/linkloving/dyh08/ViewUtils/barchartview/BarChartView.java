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
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.linkloving.dyh08.R;
import com.linkloving.dyh08.utils.logUtils.MyLog;

import java.util.ArrayList;
import java.util.List;


/**
 * 自定义组件：条形统计图
 * Created by hanj on 14-12-30.
 */
public class BarChartView extends View {
    private static final String TAG = BarChartView.class.getSimpleName();
    private int screenW, screenH, screenHight;
    private List<BarChartView.BarChartItemBean> mItems = new ArrayList<BarChartView.BarChartItemBean>();
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


    /**
     * The x-position of y-index and the y-position of the x-index..
     */
    private float x_index_startY, y_index_startX;
    public PopupWindow popupWindow = new PopupWindow();

    private Bitmap arrowBmp;
    private Rect x_index_arrowRect, y_index_arrowRect;
    //    加载dialogview对象
    private static final int BG_COLOR = Color.parseColor("#ffffff");
    public DialogListerer mdialogListerer;

    public void setDialogListerer(DialogListerer DialogListerer) {
        this.mdialogListerer = DialogListerer;
    }

    public interface DialogListerer {
        void showDialog(int number, int x, int y);

        void dismissPopupWindow();
    }

    public BarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    private void init(Context context) {


        screenW = ScreenUtils.getScreenW(context);
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

        textPaint.setTextSize(ScreenUtils.dp2px(getContext(), 16));

        for (int i = 0; i < mItems.size(); i++) {
            //draw bar rect
            barRect.left = (int) ((int) y_index_startX + barItemWidth * i + barSpace * (i + 1) - (int) leftMoving);
//            MyLog.e(TAG, y_index_startX+"========================="+barRect.left);
//            对值做个判断,如果为0的话设置一个高度,不然会很高 ;
            if (mItems.get(i).itemValue == 0) {
                barRect.top = (int) (screenH * 0.61875);
//                MyLog.e(TAG, barRect.top+"========================="+barRect.top);
            } else {
                barRect.top = topMargin * 2 + (int) (maxHeight * (1.0f - mItems.get(i).itemValue / maxValue));
                MyLog.e(TAG, barRect.top + "======================" + barRect.top);
            }

            barRect.right = barRect.left + barItemWidth;
            barPaint.setColor(Color.rgb(220, 220, 221));
            canvas.drawRect(barRect, barPaint);

            if (i == iCheck) {
//             barRect.left = (int) y_index_startX + barItemWidth * i + barSpace * (i + 1) - (int) leftMoving;
//             barRect.top = topMargin * 2 + (int) (maxHeight * (1.0f - mItems[i].itemValue / maxValue));
//             barRect.right = barRect.left + barItemWidth;
                barPaint.setColor(Color.rgb(251, 195, 0));
                canvas.drawRect(barRect, barPaint);
                barTextpaint.setColor(Color.WHITE);
                barTextpaint.setTextSize(30);
                canvas.drawText(mItems.get(i).itemType, barRect.left + 10, barRect.top + 40, barTextpaint);
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
    public void showPopupWindow(View view,String time, int number, int x, int y) {
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
        TextView step_number_textview = (TextView) popupView.findViewById(R.id.dialog_number);
        step_number_textview.setText(number + "");
        TextView textShowTime = (TextView) popupView.findViewById(R.id.time);
        textShowTime.setText(time);
        popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setTouchable(true);
        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setClippingEnabled(false);
/*//        有参数的话，就是一view的左下角进行偏移，xoff正的向左，负的向右.
//        View stepAcitvityLayout = findViewById(R.id.step_activity_layout);*/
        popupWindow.showAsDropDown(view, x, y);

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
                    barRect.left = (int) ((int) y_index_startX + barItemWidth * i + barSpace * (i + 1) - (int) leftMoving);
                    int left = barRect.left;
//                    -60方便点击
                    if (mItems.get(i).itemValue == 0) {
                        barRect.top = (int) (screenH * 0.61875);
//                        MyLog.e(TAG, barRect.top+"========================="+barRect.top);
                    } else {
                        barRect.top = topMargin * 2 + (int) (maxHeight * (1.0f - mItems.get(i).itemValue / maxValue));
                        MyLog.e(TAG, barRect.top + "=========================" + barRect.top);
                    }
                    int top = barRect.top - 60;
                    barRect.right = barRect.left + barItemWidth;
                    int right = barRect.right;
                    if (x > left && x < right && y > top) {
                        iCheck = i;
                        MyLog.e("点击", "点击的是" + mItems.get(i).itemType);
//                        (int) mItems[i].itemValue
//                        y = screenHight-y +1000 ;
                        x = (int) (barRect.left - screenW * 0.1);
                        y = (int) (-screenH + barRect.top + 0.078 * screenHight);
                        MyLog.e("点击", screenHight + "screenHight....." + screenH + "screenH...." + y + "y......." + screenW + "screenW-----" + x + "");
                        mdialogListerer.showDialog(i, x, y);
                        break;

                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:
                float x_move = event.getRawX();
                movingLeftThisTime = lastPointX - x_move;

                leftMoving += movingLeftThisTime;
                lastPointX = x_move;
//              自动清屏,屏幕刷新
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                //smooth scroll
                new Thread(new SmoothScrollThread(movingLeftThisTime)).start();
              /*  if (popupWindow.isShowing()) {
                    mdialogListerer.dismissPopupWindow();
                }*/
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


    public void setItems(List<BarChartView.BarChartItemBean> items) {
     /*   if (items == null) {
            throw new RuntimeException("BarChartView.setItems(): the param items cannot be null.");
        }
        if (items.length == 0) {
            return;
        }*/
        this.mItems = items;
        //Calculate the max value.
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
        int minBarWidth = ScreenUtils.dp2px(getContext(), 35);
        //The min width of spacing.
        int minBarSpacing = ScreenUtils.dp2px(getContext(), 5);
//        修改条目的宽度
        barItemWidth = (screenW - leftMargin * 2) / (itemCount + 5);
        barItemWidth = 35;
//        修改条目的间隔
        barSpace = (screenW - leftMargin * 2 - barItemWidth * itemCount) / (itemCount + 6);
        barSpace = 5;
        if (barItemWidth < minBarWidth || barSpace < minBarSpacing) {
            barItemWidth = minBarWidth;
            barSpace = minBarSpacing;
        }

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

    //The max and min division value.
    private float maxDivisionValue, minDivisionValue;

    //Get the max and min division value by the max and min value in mItems.
    private void getRange(float maxValue, float minValue) {
        //max
        int scale = Utility.getScale(maxValue);
        float unscaledValue = (float) (maxValue / Math.pow(10, scale));

        maxDivisionValue = (float) (getRangeTop(unscaledValue) * Math.pow(10, scale));
        y_index_startX = (getDivisionTextMaxWidth(maxDivisionValue) + 10);
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
//            onDraw 里面的方法 类型不需要
//draw type text
     /*       String typeText = mItems[i].starttime;
            float textPathStartX = barRect.left + barItemWidth / 2 -
                    (float) (Math.sin(Math.PI / 6)) * textPaint.measureText("好") / 2;
            float textPathStartY = barRect.bottom;
            textPath.reset();
            textPath.moveTo(textPathStartX, textPathStartY);
            textPath.lineTo(textPathStartX + (float) (1000 * Math.tan(Math.PI / 6)), textPathStartY + 1000);
            canvas.drawTextOnPath(typeText, textPath, smallMargin * 1.5f, smallMargin * 2, textPaint);*/

//draw value text
         /*   String valueText = String.valueOf(mItems[i].itemValue);
            canvas.drawText(valueText, barRect.left - (textPaint.measureText(valueText) - barItemWidth) / 2,
                    barRect.top - smallMargin, textPaint);*/


//draw left white space and right white space
     /*   int c = barPaint.getColor();
        barPaint.setColor(BG_COLOR);
        leftWhiteRect.right = (int) y_index_startX;

        canvas.drawRect(leftWhiteRect, barPaint);
        canvas.drawRect(rightWhiteRect, barPaint);
        barPaint.setColor(c);*/

//draw x-index line.
      /*  canvas.drawLine(
                y_index_startX - lineStrokeWidth / 2,
                x_index_startY,
                screenW - leftMargin,
                x_index_startY,
                linePaint);
        //draw y-index line.
        canvas.drawLine(
                y_index_startX,
                x_index_startY + lineStrokeWidth / 2,
                y_index_startX,
                topMargin / 2,
                linePaint);*/

    /*    canvas.drawBitmap(arrowBmp, null, y_index_arrowRect, null);
        canvas.save();
       canvas.rotate(90, (x_index_arrowRect.left + x_index_arrowRect.right) / 2, (x_index_arrowRect.top + x_index_arrowRect.bottom) / 2);
        canvas.drawBitmap(arrowBmp, null, x_index_arrowRect, null);
        canvas.restore();
        */
//draw division value
      /*  int maxDivisionValueHeight = (int) (maxHeight * 1.0f / maxValue * maxDivisionValue);
        textPaint.setTextSize(ScreenUtils.dp2px(getContext(), 10));
        for (int i = 1; i <= 10; i++) {
            float startY = barRect.bottom - maxDivisionValueHeight * 0.1f * i;
            if (startY < topMargin / 2) {
                break;
            }
            canvas.drawLine(y_index_startX, startY, y_index_startX + 10, startY, linePaint);

            String text = String.valueOf(maxDivisionValue * 0.1f * i);
            canvas.drawText(text,
                    y_index_startX - textPaint.measureText(text) - 5,
                    startY + textPaint.measureText("0") / 2,
                    textPaint);
        }*/