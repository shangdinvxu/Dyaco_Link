package com.linkloving.dyh08.ViewUtils.drawAngle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.linkloving.dyh08.R;


/**
 * Created by leo.wang on 2016/8/1.
 */public  class DrawArc extends View {
    /** 画图片的画笔 */
    private Paint bitmapPaint;
    /** 画圆环的画笔 */
    private Paint circlePaint;

    /** 白色较粗圆滑 */
    protected Bitmap _mbmCircleWhite = null;

    /** 圆环占的百分比:0.0f~1.0f的百分比值 */
    private float percent = 0.0f;

    public float getPercent()
    {
        return percent;
    }

    private OnAnimationEndListener mOnAnimationEndListener;

    /**
     * set animation end listener
     * @param onAnimationEndListener
     */
    public void setOnAnimationEndListener(OnAnimationEndListener onAnimationEndListener){
        mOnAnimationEndListener = onAnimationEndListener;
    }

    public void setPercent(float percent)
    {
        this.percent = percent;
//        ObjectAnimator animator = ObjectAnimator.ofInt(this, "progress", (int)percent*100).setDuration(1000);
//        animator.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                super.onAnimationEnd(animation);
//                if (mOnAnimationEndListener!=null) {
//                    mOnAnimationEndListener.onAnimationEnd();
//                }
//            }
//        });
//        animator.start();
        this.invalidate();
    }

    public DrawArc(Context context) {
        super(context,null);
    }

    public DrawArc(Context context, AttributeSet attrs) {
        super(context, attrs,0);
    }

    public DrawArc(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected  void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        //构建图片画笔
        bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG); //ANTI_ALIAS_FLAG 是使位图抗锯齿的标志
        //构建圆滑画笔
        circlePaint = new Paint();
        circlePaint.setStrokeWidth(18);
        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Paint.Style.STROKE);

        float padding = 0;
        // 【第一步】绘制底层白色圆环
        circlePaint.setColor(Color.rgb(255,255,255)); //color
        RectF circleWhiteRect = new RectF(padding+10 , padding+10 , width-10, height-10 );
        canvas.drawArc(circleWhiteRect,270.0f,360.0f, false, circlePaint);

        circlePaint.setColor(Color.rgb(239,190,29)); //color
        // 【第二步】绘制上层动态圆环
        float startDegree = 0.0f;
        float endDegree = getPercent() >= 1? 360.0f : (360.0f * getPercent()); // 当大于100%时，只需要绘制一圈就行了，否则扇形裁切算法好像有问题

        float circleGreenStartDegree =  startDegree + 270;
        RectF circleGreenRect = new RectF(padding+10 , padding+10 , width-10, height-10 );
        canvas.drawArc(circleGreenRect,circleGreenStartDegree,endDegree, false, circlePaint);

    }

    protected Bitmap getBmCircleWhite()
    {
        if(_mbmCircleWhite == null)
            _mbmCircleWhite = BitmapFactory.decodeResource(this.getContext().getResources(), R.mipmap.bigloop);
        return _mbmCircleWhite;
    }

    //回收bitmap 防止OOM
    public void recycleBitmap()
    {
        if(_mbmCircleWhite != null && !_mbmCircleWhite.isRecycled())
            _mbmCircleWhite.recycle();
    }

    public interface OnAnimationEndListener{
        void onAnimationEnd();
    }

}
