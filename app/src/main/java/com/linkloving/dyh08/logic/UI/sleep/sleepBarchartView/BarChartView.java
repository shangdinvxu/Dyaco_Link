package com.linkloving.dyh08.logic.UI.sleep.sleepBarchartView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.linkloving.dyh08.R;
import com.linkloving.dyh08.ViewUtils.barchartview.ScreenUtils;
import com.linkloving.dyh08.ViewUtils.barchartview.WeekBarChartView;
import com.linkloving.dyh08.utils.logUtils.MyLog;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义组件：条形统计图
 */
public class BarChartView extends View {
    private int screenW, screenH;
    private List<BarChartView.BarChartItemBean> mItems = new ArrayList<BarChartItemBean>();
    //第一个是深色,第二个是total色,第三是画横线
    private int[] mBarColors = new int[]{Color.rgb(26, 43, 95), Color.rgb(38, 174, 222), Color.rgb(190, 190, 190)};
    private Paint barPaint, linePaint, textPaint;
    private Rect barRect;
    private int lineStrokeWidth;
    private double barItemWidth, barSpace, oneHourHight;
    private Rect deepSleepRect;
    private Paint deepSleepPaint;
    public DialogListerer mdialogListerer;
    public PopupWindow popupWindow = new PopupWindow();
    private TextView deepSleepHr;
    private TextView lightSleepHr;
    private TextView dateTextView;
    private Context context ;

    public BarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        this.context =context ;
    }
    public void setDialogListerer(DialogListerer DialogListerer) {
        this.mdialogListerer = DialogListerer;
    }

    public interface DialogListerer {
        void showDialog(int number, int x, int y);

        void dismissPopupWindow();
    }


    private void init(Context context) {
        screenW = ScreenUtils.getScreenW(context);
        screenH = ScreenUtils.getScreenH(context);

        barPaint = new Paint();
        deepSleepPaint = new Paint();
        barPaint.setColor(mBarColors[0]);
        linePaint = new Paint();
        lineStrokeWidth = ScreenUtils.dp2px(context, 1);
        linePaint.setStrokeWidth(lineStrokeWidth);
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        barRect = new Rect(0, 0, 0, 0);
        deepSleepRect = new Rect(0, 0, 0, 0);
        barItemWidth = screenW * 0.09;
        barSpace = screenW * 0.035;
//        设置一个一小时的高度
        oneHourHight = screenH * 0.017;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
//        画2 4 6 8 10 线
        linePaint.setColor(mBarColors[2]);
        canvas.drawLine(0, (float) (oneHourHight * 11.5), (float) (screenW * 0.9), (float) (oneHourHight * 11.5), linePaint);
        canvas.drawLine(0, (float) (oneHourHight * 9), (float) (screenW * 0.9), (float) (oneHourHight * 9), linePaint);
        canvas.drawLine(0, (float) (oneHourHight * 6.5), (float) (screenW * 0.9), (float) (oneHourHight * 6.5), linePaint);
        canvas.drawLine(0, (float) (oneHourHight * 4), (float) (screenW * 0.9), (float) (oneHourHight * 4), linePaint);
        canvas.drawLine(0, (float) (oneHourHight * 1.5), (float) (screenW * 0.9), (float) (oneHourHight * 1.5), linePaint);
        textPaint.setTextSize(ScreenUtils.dp2px(getContext(), 16));
        for (int i = 0; i < mItems.size(); i++) {
//          画深睡的rect
            barRect.left = (int) (screenW * 0.02 + barItemWidth * i + barSpace * (i + 1));
            barRect.top = (int) (oneHourHight * 14 - mItems.get(i).itemDeepValue * oneHourHight * 1.25);
            barRect.right = (int) (barRect.left + barItemWidth);
            barRect.bottom = (int) (oneHourHight * 14);
            barPaint.setColor(mBarColors[0]);
            canvas.drawRect(barRect, barPaint);
            barRect.bottom = barRect.top;
            barRect.top = (int) (oneHourHight * 14 - (mItems.get(i).itemDeepValue+mItems.get(i).itemLightValue) * oneHourHight * 1.25);
            barPaint.setColor(mBarColors[1]);
            canvas.drawRect(barRect, barPaint);

//            //draw bar rect
//            deepSleepRect.left = (int) (screenW * 0.02 + barItemWidth * i + barSpace * (i + 1));
//            deepSleepRect.top = (int) (oneHourHight * 14 - mItems.get(i).itemDeepValue * oneHourHight * 2);
//            deepSleepRect.right = (int) (barRect.left + barItemWidth);
//            deepSleepRect.bottom = (int) (oneHourHight * 14);
//            deepSleepPaint.setColor(mBarColors[0]);
//            canvas.drawRect(deepSleepRect, deepSleepPaint);
        }
        //draw x-index line.
        canvas.drawLine(0, (float) (oneHourHight * 14), screenW, (float) (oneHourHight * 14), linePaint);


//        画 2 4 6 8 10H 文字
        String[] twoHour = {"2h", "4h", "6h", "8h", "10h"};
        textPaint.setColor(Color.BLACK);
        for (int i = 0; i < 5; i++) {
            String typeText = twoHour[i];
            float texttypeStartx = (float) (screenW * 0.9);
            float texttypeStarty = (float) (oneHourHight * (12 - 2.5 * i));
            canvas.drawText(typeText, texttypeStartx, texttypeStarty, textPaint);
        }

        String[] week = {"S", "M", "T", "W", "T", "F", "S"};
        for (int i = 0; i < 7; i++) {
            String typeText = week[i];
            float texttypeStartx = (float) (screenW * (0.1 + i * 0.12));
            float texttypeStarty = (float) (oneHourHight * 16.5);
            canvas.drawText(typeText, texttypeStartx, texttypeStarty, textPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                int x = (int) event.getX();
                int y = (int) event.getY();
                for (int i = 0; i < mItems.size(); i++) {
                    barRect.left = (int) (screenW * 0.02 + barItemWidth * i + barSpace * (i + 1));
                    barRect.top = (int) (oneHourHight * 14 - mItems.get(i).itemDeepValue * oneHourHight * 1.25);
                    barRect.right = (int) (barRect.left + barItemWidth);
                    barRect.bottom = (int) (oneHourHight * 14);
                    int left = barRect.left;
                    int right = barRect.right;
                    if (x > left && x < right && y > (barRect.top-160)&& y<barRect.bottom) {
                        MyLog.e("点击", "点击的是" + mItems.get(i).itemType);
                        mdialogListerer.showDialog(i,(int) (screenW * 0.02 + barItemWidth * i + barSpace * (i + 1)+barItemWidth*0.5-screenW*0.13),
                                (int) (oneHourHight*8+ (mItems.get(i).itemDeepValue+mItems.get(i).itemLightValue) * oneHourHight * 1.25));
                        popupWindow.update((int) (screenW * 0.02 + barItemWidth * i + barSpace * (i + 1)+barItemWidth*0.5-screenW*0.13),
                                (int) (oneHourHight*8+ (mItems.get(i).itemDeepValue+mItems.get(i).itemLightValue) * oneHourHight * 1.25),-1,-1);
                        deepSleepHr.setText(context.getString(R.string.deep)+"  "+mItems.get(i).itemLightValue+"hr");
                        lightSleepHr.setText(context.getString(R.string.light)+"  "+mItems.get(i).itemDeepValue+"hr");
                        dateTextView.setText(mItems.get(i).itemType);
                        break;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (popupWindow.isShowing()){
                    float x_move = event.getRawX();
//              自动清屏,屏幕刷新
                    for (int i = 0; i < mItems.size(); i++){
                        if (((screenW * 0.02 + barItemWidth * i + barSpace * (i + 1)))<x_move
                                &&x_move<(screenW * 0.02 + barItemWidth * i + barSpace * (i + 1)+barItemWidth)){
                            popupWindow.update((int) (screenW * 0.02 + barItemWidth * i + barSpace * (i + 1)+barItemWidth*0.5-screenW*0.13),
                                    (int) (oneHourHight*8+ (mItems.get(i).itemDeepValue+mItems.get(i).itemLightValue) * oneHourHight * 1.25),-1,-1);

                            MyLog.e("点击", "(left+right)/2 :" + (int) (screenW * 0.02 + barItemWidth * i + barSpace * (i + 1)+barItemWidth*0.5-screenW*0.13) +
                                    "========i=move====="+i+"         "+
                                    "(barRect.top-160)=:" +(int) (oneHourHight*8+ (mItems.get(i).itemDeepValue+mItems.get(i).itemLightValue) * oneHourHight * 1.25));

                            deepSleepHr.setText(context.getString(R.string.deep)+"  "+mItems.get(i).itemLightValue+"hr");
                            lightSleepHr.setText(context.getString(R.string.light)+"  "+mItems.get(i).itemDeepValue+"hr");
                            dateTextView.setText(mItems.get(i).itemType);
                        }
                    }
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (popupWindow.isShowing()) {
                    mdialogListerer.dismissPopupWindow();
                }
                break;
            default:
                return super.onTouchEvent(event);
        }
        return true;
    }

    public void showPopupWindow(View view,int  deep , int light, int x, int y) {
        View popupView = LayoutInflater.from(getContext()).inflate(R.layout.tw_sleep_popupwiew, null);
        deepSleepHr = (TextView) popupView.findViewById(R.id.deepSleepHr);
        dateTextView = (TextView) popupView.findViewById(R.id.date);
        popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        lightSleepHr = (TextView) popupView.findViewById(R.id.lightSleepHr);
        popupWindow.setTouchable(true);
        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setClippingEnabled(false);
        popupWindow.showAsDropDown(view,  x, y,Gravity.TOP);
//        }
        MyLog.e("点击", "调用了popupwindow");
    }





    public List<BarChartItemBean> getItems() {
        return mItems;
    }

    public void setItems(List<BarChartItemBean> items) {

        this.mItems = items;
        invalidate();
    }

    /**
     * A model class to keep the bar item info.
     */
    public static class BarChartItemBean {
        public String itemType;
        public double itemDeepValue;
        public double itemLightValue;


        public BarChartItemBean(String itemType, double itemDeepValue, double itemLightValue) {
            this.itemType = itemType;
            this.itemDeepValue = itemDeepValue;
            this.itemLightValue = itemLightValue;
        }
    }
}