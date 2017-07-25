package com.linkloving.dyh08.ViewUtils.clock;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;

import com.linkloving.dyh08.R;
import com.linkloving.dyh08.utils.logUtils.MyLog;

import java.util.Calendar;

/**
 * Created by Daniel on 2016/7/26.
 */
public class ClockView extends View {
    //View默认最小宽度
    private static final int DEFAULT_MIN_WIDTH = 200;
    //秒针长度
    private float secondPointerLength;
    //分针长度
    private float minutePointerLength;
    //时针长度
    private float hourPointerLength;
    //外圆边框宽度
    private static final float DEFAULT_BORDER_WIDTH = 6f;
    //指针反向超过圆点的长度
    private static final float DEFAULT_POINT_BACK_LENGTH = 40f;
    private Context context;
    //长刻度线
    private static float DEFAULT_LONG_DEGREE_LENGTH = 40f;
    //短刻度线
    private static final float DEFAULT_SHORT_DEGREE_LENGTH = 30f;

    private Thread timeThread = new Thread() {
        @Override
        public void run() {
            try {
                while (true) {
                    updateHandler.sendEmptyMessage(0);
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    private Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            invalidate();
        }
    };

    public ClockView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public ClockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public ClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    //启动线程让指针动起来
    private void init() {
        timeThread.start();
    }

    /**
     * 计算时针、分针、秒针的长度
     */
    private void reset() {
        float r = (float) (Math.min(getHeight() * 0.55, getWidth() * 0.55) - DEFAULT_BORDER_WIDTH / 2);
        secondPointerLength = r * 0.8f;
        minutePointerLength = r * 0.6f;
        hourPointerLength = r * 0.5f;
    }

    /**
     * 根据角度和长度计算线段的起点和终点的坐标
     *
     * @param angle
     * @param length
     * @return
     */
    private float[] calculatePoint(float angle, float length) {
        float[] points = new float[4];
        if (angle <= 90f) {
            points[0] = -(float) Math.sin(angle * Math.PI / 180) * DEFAULT_POINT_BACK_LENGTH;
            points[1] = (float) Math.cos(angle * Math.PI / 180) * DEFAULT_POINT_BACK_LENGTH;
            points[2] = (float) Math.sin(angle * Math.PI / 180) * length;
            points[3] = -(float) Math.cos(angle * Math.PI / 180) * length;
        } else if (angle <= 180f) {
            points[0] = -(float) Math.cos((angle - 90) * Math.PI / 180) * DEFAULT_POINT_BACK_LENGTH;
            points[1] = -(float) Math.sin((angle - 90) * Math.PI / 180) * DEFAULT_POINT_BACK_LENGTH;
            points[2] = (float) Math.cos((angle - 90) * Math.PI / 180) * length;
            points[3] = (float) Math.sin((angle - 90) * Math.PI / 180) * length;
        } else if (angle <= 270f) {
            points[0] = (float) Math.sin((angle - 180) * Math.PI / 180) * DEFAULT_POINT_BACK_LENGTH;
            points[1] = -(float) Math.cos((angle - 180) * Math.PI / 180) * DEFAULT_POINT_BACK_LENGTH;
            points[2] = -(float) Math.sin((angle - 180) * Math.PI / 180) * length;
            points[3] = (float) Math.cos((angle - 180) * Math.PI / 180) * length;
        } else if (angle <= 360f) {
            points[0] = (float) Math.cos((angle - 270) * Math.PI / 180) * DEFAULT_POINT_BACK_LENGTH;
            points[1] = (float) Math.sin((angle - 270) * Math.PI / 180) * DEFAULT_POINT_BACK_LENGTH;
            points[2] = -(float) Math.cos((angle - 270) * Math.PI / 180) * length;
            points[3] = -(float) Math.sin((angle - 270) * Math.PI / 180) * length;
        }
        return points;
    }

    protected void onDraw(Canvas canvas) {
        float borderWidth = DEFAULT_BORDER_WIDTH;
        float r = Math.min(getHeight() / 2, getWidth() / 2) - borderWidth / 2;
        /*reset();
        //画外圆
        float borderWidth = DEFAULT_BORDER_WIDTH;
        float r = Math.min(getHeight() / 2, getWidth() / 2) - borderWidth / 2;
        Paint paintCircle = new Paint();
        paintCircle.setStyle(Paint.Style.STROKE);
        paintCircle.setAntiAlias(true);
        paintCircle.setStrokeWidth(borderWidth);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, r, paintCircle);
*/
     /*   //画刻度线
        float degreeLength = 0f;
        Paint paintDegree = new Paint();
        paintDegree.setAntiAlias(true);
        for(int i=0;i<60;i++){
            if(i % 5 == 0){
                paintDegree.setStrokeWidth(6);
                degreeLength = DEFAULT_LONG_DEGREE_LENGTH;
            }else{
                paintDegree.setStrokeWidth(3);
                degreeLength = DEFAULT_SHORT_DEGREE_LENGTH;
            }
            canvas.drawLine(getWidth()/2, Math.abs(getHeight()/2 - r), getWidth()/2,Math.abs(getHeight()/2 - r) + degreeLength, paintDegree);
            canvas.rotate(360/60, getWidth()/2, getHeight()/2);
        }
*/
        //刻度数字
        int degressNumberSize = 30;
        canvas.translate(getWidth() / 2, getHeight() / 2);
        Paint paintDegreeNumber = new Paint();
        paintDegreeNumber.setColor(Color.WHITE);
        paintDegreeNumber.setTextAlign(Paint.Align.CENTER);
        paintDegreeNumber.setTextSize(degressNumberSize);
        paintDegreeNumber.setFakeBoldText(true);

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(calendar.HOUR);
        int hour_clock = hour - 1;
//根据时间判断画点
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        for (int i = -1; i < 11; i++) {
            float[] temp = calculatePoint((i + 1) * 30, r - DEFAULT_LONG_DEGREE_LENGTH - degressNumberSize / 2 - 15);
          /*  canvas.drawText((i+1)+"", temp[2], temp[3] + degressNumberSize/2-6, paintDegreeNumber);*/
            if (i == hour_clock) {
                Bitmap bitmap = null;
                if (i == -1 | i == 2 | i == 5 | i == 8) {
                    bitmap = loadBallView(getContext(), R.mipmap.point_3, (int) (0.12 * width), (int) (0.12 * width));
                    canvas.drawBitmap(bitmap, temp[2] - 60, temp[3] - 30, paintDegreeNumber);
                } else {
                    bitmap = loadBallView(getContext(), R.mipmap.point_3, (int) (0.074 * width), (int) (0.074 * width));
                    canvas.drawBitmap(bitmap, temp[2] - 30, temp[3] - 30, paintDegreeNumber);
                }
            } else {

                if (i == -1 | i == 2 | i == 5 | i == 8) {
                    canvas.drawCircle(temp[2], temp[3] + degressNumberSize / 2 - 6, (int) (0.037 * width), paintDegreeNumber);
                } else {
                    canvas.drawCircle(temp[2], temp[3] + degressNumberSize / 2 - 6, (int) (0.015 * width), paintDegreeNumber);
                }

            }

        }
    }

    public static Bitmap loadBallView(Context context, int resId, int width, int height) {

        Resources resources = context.getResources();

        Drawable image = resources.getDrawable(resId);

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        image.setBounds(0, 0, width, height);
        image.draw(canvas);

        return bitmap;

    }

    /*    //画指针
        Paint paintHour = new Paint();
        paintHour.setAntiAlias(true);
        paintHour.setStrokeWidth(15);
        Paint paintMinute = new Paint();
        paintMinute.setAntiAlias(true);
        paintMinute.setStrokeWidth(10);
        Paint paintSecond = new Paint();
        paintSecond.setAntiAlias(true);
        paintSecond.setStrokeWidth(5);
        Calendar now = Calendar.getInstance();
        float[] hourPoints = calculatePoint(now.get(Calendar.HOUR_OF_DAY)%12/12f*360, hourPointerLength);
        canvas.drawLine(hourPoints[0], hourPoints[1], hourPoints[2], hourPoints[3], paintHour);
        float[] minutePoints = calculatePoint(now.get(Calendar.MINUTE)/60f*360, minutePointerLength);
        canvas.drawLine(minutePoints[0], minutePoints[1], minutePoints[2], minutePoints[3], paintMinute);
        float[] secondPoints = calculatePoint(now.get(Calendar.SECOND)/60f*360, secondPointerLength);
        canvas.drawLine(secondPoints[0], secondPoints[1], secondPoints[2], secondPoints[3], paintSecond);

        //画圆心
        Paint paintCenter = new Paint();
        paintCenter.setColor(Color.WHITE);
        canvas.drawCircle(0, 0, 2, paintCenter);*/


    /**
     * 当布局为wrap_content时设置默认长宽
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec), measure(heightMeasureSpec));
    }

    private int measure(int origin) {
        int result = DEFAULT_MIN_WIDTH;
        int specMode = MeasureSpec.getMode(origin);
        int specSize = MeasureSpec.getSize(origin);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }
}