package com.linkloving.dyh08.logic.UI.launch.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

/**
 * Created by zoubo on 16/3/16.
 * 自定义横向滚动刻度尺
 *
 * @version 1.0
 */

public class ScaleRulerView extends View {
    public static final int MOD_TYPE = 5;  //刻度盘精度

    private static final int ITEM_HEIGHT_DIVIDER = 12;

    private static final int ITEM_MAX_HEIGHT = 36;  //最大刻度高度
    private static final int ITEM_MIN_HEIGHT = 25;  //最小刻度高度
    private static final int TEXT_SIZE = 18;

    private float mDensity;
    private float mValue = 50;
    private float mMaxValue = 100;
    private float mDefaultMinValue = 0;
    private int mModType = MOD_TYPE;
    private int mLineDivider = ITEM_HEIGHT_DIVIDER;
    private int mLastX, mMove;
    private int mWidth, mHeight;

    private int mMinVelocity;
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;

    private OnValueChangeListener mListener;

    private Paint mhighLinePaint = new Paint();
    private Paint mLinePaint = new Paint();
    private Paint mSelectPaint = new Paint();
    private int mSelectWidth = 8;
    private int mNormalLineWidth = 4;
    private String mSelectColor = "#ffa737"; //选择器颜色
    private String mNormalLineColor = "#666666"; //刻度尺颜色
    private String mhighLineColor = "#3A87AD"; //5倍数刻度尺颜色


    public interface OnValueChangeListener {
        void onValueChange(float value);
    }

    public ScaleRulerView(Context context) {
        this(context, null);
    }

    public ScaleRulerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public ScaleRulerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    protected void init(Context context) {
        mScroller = new Scroller(context);
        mDensity = context.getResources().getDisplayMetrics().density;

        mMinVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();
    }

    /**
     * @param defaultValue 初始值
     * @param maxValue     最大值
     */
    public void initViewParam(float defaultValue, float maxValue, float defaultMinValue) {
        mValue = defaultValue;
        mMaxValue = maxValue;
        mDefaultMinValue = defaultMinValue;

        invalidate();

        mLastX = 0;
        mMove = 0;
        notifyValueChange();
    }

    public void  setdefaultValue(float mDefaultMinValue){
        mValue = mDefaultMinValue ;
    }

    /**
     * 设置用于接收结果的监听器
     *
     * @param listener
     */
    public void setValueChangeListener(OnValueChangeListener listener) {
        mListener = listener;
    }

    /**
     * 获取当前刻度值
     *
     * @return
     */
    public float getValue() {
        return mValue;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mWidth = getWidth();
        mHeight = getHeight();
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawScaleLine(canvas);
        drawMiddleLine(canvas);
    }


    /**
     * 从中间往两边开始画刻度线
     *
     * @param canvas
     */
    private void drawScaleLine(Canvas canvas) {
        canvas.save();

        mLinePaint.setStrokeWidth(mNormalLineWidth);
        mLinePaint.setColor(Color.parseColor(mNormalLineColor));

        mhighLinePaint.setStrokeWidth(mNormalLineWidth);
        mhighLinePaint.setColor(Color.parseColor(mhighLineColor));

        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(TEXT_SIZE * mDensity);
        float textWidth = Layout.getDesiredWidth("0", textPaint);

        int width = mWidth;
        int drawCount = 0;
        float xPosition;

        for (int i = 0; drawCount <= 4 * width; i++) {

            xPosition = (width / 2 - mMove) + i * mLineDivider * mDensity;
            if (xPosition + getPaddingRight() < mWidth && (mValue + i) <= mMaxValue) {
                if ((mValue + i) % mModType == 0) {
                    canvas.drawLine(xPosition, 0, xPosition, mDensity * ITEM_MAX_HEIGHT*2, mhighLinePaint); //画5倍数的刻度线
                    if (mValue + i <= mMaxValue) { //画值
                        canvas.drawText(String.valueOf((int)(mValue + i)), countLeftStart((int) (mValue + i), xPosition, textWidth), mDensity * ITEM_MAX_HEIGHT * 3 + textWidth, textPaint);
                    }
                } else {
                    canvas.drawLine(xPosition, 0, xPosition,  mDensity * ITEM_MIN_HEIGHT, mLinePaint);//普通的刻度线
                }
            }

            xPosition = (width / 2 - mMove) - i * mLineDivider * mDensity;
            if (xPosition > getPaddingLeft() && (mValue - i) >= mDefaultMinValue) {
                if ((mValue - i) % mModType == 0) {
                    canvas.drawLine(xPosition, 0, xPosition, mDensity * ITEM_MAX_HEIGHT*2, mhighLinePaint);
                    if (mValue - i >= 0) {
                        canvas.drawText(String.valueOf((int)(mValue - i)), countLeftStart((int) (mValue - i), xPosition, textWidth), mDensity * ITEM_MAX_HEIGHT * 3 + textWidth, textPaint);
                    }
                } else {
                    canvas.drawLine(xPosition, 0, xPosition,  mDensity * ITEM_MIN_HEIGHT, mLinePaint);
                }
            }

            drawCount += 2 * mLineDivider * mDensity;
        }

        canvas.restore();
    }

    /**
     * 计算没有数字显示位置的辅助方法
     *
     * @param value
     * @param xPosition
     * @param textWidth
     * @return
     */
    private float countLeftStart(int value, float xPosition, float textWidth) {
        float xp = 0f;
        if (value < 20) {
            xp = xPosition - (textWidth * 1 / 2);
        } else {
            xp = xPosition - (textWidth * 3 / 2);
        }
        return xp;
    }

    /**
     * 画中间的红色指示线、阴影等。指示线两端简单的用了两个矩形代替
     *
     * @param canvas
     */
    private void drawMiddleLine(Canvas canvas) {
        canvas.save();

        mSelectPaint.setStrokeWidth(mSelectWidth);
        mSelectPaint.setColor(Color.parseColor(mSelectColor));
        canvas.drawLine(mWidth / 2, 0, mWidth / 2, mHeight, mSelectPaint);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int xPosition = (int) event.getX();

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mScroller.forceFinished(true);

                mLastX = xPosition;
                mMove = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                mMove += (mLastX - xPosition);
                changeMoveAndValue();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                countMoveEnd();
                countVelocityTracker(event);
                return false;
            // break;
            default:
                break;
        }

        mLastX = xPosition;
        return true;
    }

    private void countVelocityTracker(MotionEvent event) {
        mVelocityTracker.computeCurrentVelocity(1000);
        float xVelocity = mVelocityTracker.getXVelocity();
        if (Math.abs(xVelocity) > mMinVelocity) {
            mScroller.fling(0, 0, (int) xVelocity, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
        }

    }

    private void changeMoveAndValue() {
        int tValue = (int) (mMove / (mLineDivider * mDensity));
        if (Math.abs(tValue) > 0) {
            mValue += tValue;
            mMove -= tValue * mLineDivider * mDensity;
            if (mValue <= mDefaultMinValue || mValue > mMaxValue) {
                mValue = mValue <= mDefaultMinValue ? mDefaultMinValue : mMaxValue;
                mMove = 0;
                mScroller.forceFinished(true);
            }

            notifyValueChange();
        }
        postInvalidate();
    }

    private void countMoveEnd() {
        int roundMove = Math.round(mMove / (mLineDivider * mDensity));
        mValue = mValue + roundMove;
        mValue = mValue <= 0 ? 0 : mValue;
        mValue = mValue > mMaxValue ? mMaxValue : mValue;

        mLastX = 0;
        mMove = 0;

        notifyValueChange();
        postInvalidate();
    }

    private void notifyValueChange() {
        if (null != mListener) {
            if (mModType == MOD_TYPE) {
                mListener.onValueChange(mValue);
            }
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            if (mScroller.getCurrX() == mScroller.getFinalX()) { // over
                countMoveEnd();
            } else {
                int xPosition = mScroller.getCurrX();
                mMove += (mLastX - xPosition);
                changeMoveAndValue();
                mLastX = xPosition;
            }
        }
    }
}
