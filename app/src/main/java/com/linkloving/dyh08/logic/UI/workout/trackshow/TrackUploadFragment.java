package com.linkloving.dyh08.logic.UI.workout.trackshow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.LocationMode;
import com.baidu.trace.OnStartTraceListener;
import com.baidu.trace.OnStopTraceListener;
import com.baidu.trace.OnTrackListener;
import com.baidu.trace.TraceLocation;
import com.linkloving.dyh08.R;
import com.linkloving.dyh08.logic.UI.Groups.baidu.GroupsDetailsActivity;
import com.linkloving.dyh08.logic.UI.workout.Greendao.TraceGreendao;
import com.linkloving.dyh08.logic.UI.workout.trackutils.DateUtils;
import com.linkloving.dyh08.logic.UI.workout.trackutils.GsonService;
import com.linkloving.dyh08.logic.UI.workout.trackutils.HistoryTrackData;
import com.linkloving.dyh08.utils.CommonUtils;
import com.linkloving.dyh08.utils.GpsUtils;
import com.linkloving.dyh08.utils.TimeUtils;
import com.linkloving.dyh08.utils.logUtils.MyLog;
import com.linkloving.utils.TimeZoneHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import Trace.GreenDao.DaoMaster;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


/**
 * 轨迹追踪
 */
@SuppressLint("NewApi")
public class TrackUploadFragment extends Fragment {
    private static final String TAG = TrackUploadFragment.class.getSimpleName();
    @InjectView(R.id.OK_btn)
    Button OKBtn;
    @InjectView(R.id.first_middle)
    LinearLayout firstMiddle;
    @InjectView(R.id.second_middle)
    LinearLayout secondMiddle;

    private Button btnStartTrace = null;

    private Button btnStopTrace = null;
    private int stopType = 1;

    /**
     * 舒适化数据查找对象
     */

    /**
     * 开启轨迹服务监听器
     */
    protected static OnStartTraceListener startTraceListener = null;

    /**
     * 停止轨迹服务监听器
     */
    protected static OnStopTraceListener stopTraceListener = null;

    /**
     * 采集周期（单位 : 秒）
     */
    private int gatherInterval = 5;

    /**
     * 打包周期（单位 : 秒）
     */
    private int packInterval = 5;

    /**
     * 图标
     */
    private static BitmapDescriptor realtimeBitmap;

    private static Overlay overlay = null;

    // 覆盖物
    protected static OverlayOptions overlayOptions;
    // 路线覆盖物
    private static PolylineOptions polyline = null;

    private static List<LatLng> pointList = new ArrayList<LatLng>();

    protected boolean isTraceStart = false;

    private Intent serviceIntent = null;
    /**
     * Track监听器
     */
    protected static OnTrackListener trackListener = null;

    /**
     * 刷新地图线程(获取实时点)
     */
    protected RefreshThread refreshThread = null;

    protected static MapStatusUpdate msUpdate = null;

    private View view = null;

    private LayoutInflater mInflater = null;

    protected static boolean isInUploadFragment = true;

    private static boolean isRegister = false;

    protected static PowerManager pm = null;

    protected static WakeLock wakeLock = null;

    private PowerReceiver powerReceiver = new PowerReceiver();

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat simMonth = new SimpleDateFormat("yyyy-MM");
    private SQLiteDatabase db;
    private DaoMaster.DevOpenHelper devOpenHelper;
    private TraceGreendao traGreendao;
    private int clickType = 0;
    private boolean isClickStart = false;
    private Date dateStartTrace;
    private String formatMonth;
    private String formatStartTime;
    private Date dateStopTrace;
    private String formatEndTime;

    @InjectView(R.id.chronometer)
    TextView chronometer;
    private RelativeLayout relativeLayout;
    private SharedPreferences sharedPreferences;
    private Handler handler;
    private Runnable update_thread;
    private long startTimeLong;
    private WorkoutActivity workoutActivity;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View inflate = LayoutInflater.from(getActivity()).inflate(R.layout.tw_trace_fragment_trackupload, null);
        chronometer = (TextView) inflate.findViewById(R.id.chronometer);
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        long time = (long) msg.obj;
                        long runtimeDuration = time - startTimeLong;
                        Date date = new Date(runtimeDuration);
                        String format = simpleDateFormat.format(date);
                        if (chronometer != null && stopType == 1) {
                            chronometer.setText(format);
                        }
                }
            }
        };
        workoutActivity = (WorkoutActivity) getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tw_trace_fragment_trackupload, container, false);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.uploadRelative);
        ButterKnife.inject(this, view);
        mInflater = inflater;
        SharedPreferences sharedPreferences = WorkoutActivity.mContext.getSharedPreferences("clickType", Context.MODE_PRIVATE);
        clickType = sharedPreferences.getInt("clickType", 0);
        // 初始化
        init();
        // 初始化监听器
        initListener();
        // 设置采集周期
        setInterval();
        // 设置http请求协议类型
        setRequestType();
        getRuntime();
        devOpenHelper = new DaoMaster.DevOpenHelper(getContext(), "Note", null);
        db = devOpenHelper.getReadableDatabase();
        traGreendao = new TraceGreendao(getContext(), db);
        SharedPreferences sharedPreferencesbegin = WorkoutActivity.mContext.getSharedPreferences("clickType", Context.MODE_PRIVATE);
        isClickStart = sharedPreferencesbegin.getBoolean("isClickStart", false);
        startTimeLong = sharedPreferencesbegin.getLong("startTimeLong", 0);
        if (isClickStart) {
            secondMiddle.setVisibility(View.VISIBLE);
//            relativeLayout.setBackgroundColor(Color.BLACK);
//            relativeLayout.getBackground().setAlpha(150);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isClickStart && GpsUtils.isOPen(getContext())) {
            firstMiddle.setVisibility(View.GONE);
            secondMiddle.setVisibility(View.VISIBLE);
            getRuntime();
        }
        if (GpsUtils.isOPen(getContext())) {
            firstMiddle.setVisibility(View.GONE);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onDestroy() {
        SharedPreferences sharedPreferences = WorkoutActivity.mContext.getSharedPreferences("clickType", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt("clickType", clickType);
        edit.putBoolean("isClickStart", isClickStart);
        edit.commit();
        super.onDestroy();
    }


    public void getRuntime() {
        //延时1s后又将线程加入到线程队列中
        MyLog.e(TAG, "postDelayed执行了");
        if (isClickStart) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Calendar instance = Calendar.getInstance();
                                    instance.add(Calendar.MINUTE, -TimeZoneHelper.getTimeZoneOffsetMinute());
                                    long timeInMillis = instance.getTimeInMillis();
                                    Message message = new Message();
                                    message.what = 1;
                                    message.obj = timeInMillis;
                                    handler.sendMessage(message);
                                    handler.postDelayed(update_thread, 1000);
                                }
                            });
                            sleep(1000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }

    }

    /**
     * 按下ok后帮用户开启gps
     */
    @OnClick(R.id.OK_btn)
    void setOKBtn(View view) {
//        GpsUtils.openGPS(getContext());
        Intent intent = new Intent(
                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, 0);
    }


    /**
     * 初始化
     */
    private void init() {

        btnStartTrace = (Button) view.findViewById(R.id.btn_startTrace);

        btnStopTrace = (Button) view.findViewById(R.id.btn_stopTrace);
        btnStartTrace.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                MyLog.e(TAG, "clicktype=====" + clickType);
                if (clickType == 0) {
                    //先判断Gps有没有开,要是GPs没有开则在用户点确定后开启
                    if (!GpsUtils.isOPen(getContext())) {
                        firstMiddle.setVisibility(View.VISIBLE);
                    } else {
                        isClickStart = true;
                        secondMiddle.setVisibility(View.VISIBLE);
                        getRuntime();
//                        Toast.makeText(getActivity(), "开启轨迹服务", Toast.LENGTH_LONG).show();
//                        relativeLayout.setBackgroundColor(Color.BLACK);
//                        relativeLayout.getBackground().setAlpha(150);

//                    把开始时间等数据记录下来,等按结束时一起记录.
                        //  成功开启轨迹服务,说明开始跑了,记录开始时间.
                        dateStartTrace = new Date();
                        startTimeLong = dateStartTrace.getTime();
                        formatMonth = simMonth.format(dateStartTrace);
                        formatStartTime = TrackUploadFragment.this.simpleDateFormat.format(dateStartTrace);
                        sharedPreferences = WorkoutActivity.mContext.getSharedPreferences("clickType", Context.MODE_PRIVATE);
                        SharedPreferences.Editor edit = sharedPreferences.edit();
                        edit.putString("formatMonth", formatMonth);
                        edit.putString("formatStartTime", formatStartTime);
                        edit.putLong("startTimeLong", startTimeLong);
                        edit.commit();
                        clickType = 1;
                        startTrace();
                        if (!isRegister) {
                            if (null == pm) {
                                pm = (PowerManager) WorkoutActivity.mContext.getSystemService(Context.POWER_SERVICE);
                            }
                            if (null == wakeLock) {
                                wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "track upload");
                            }
                            IntentFilter filter = new IntentFilter();
                            filter.addAction(Intent.ACTION_SCREEN_OFF);
                            filter.addAction(Intent.ACTION_SCREEN_ON);
                            WorkoutActivity.mContext.registerReceiver(powerReceiver, filter);
                            isRegister = true;
                        }
                    }
                } else {
                    Toast.makeText(getActivity(), "轨迹服务已开启,请关闭轨迹服务", Toast.LENGTH_LONG).show();
                }
            }
        });


        btnStopTrace.setOnClickListener(new View.OnClickListener() {

            private long startTimeLong;

            public void onClick(View v) {
                MyLog.e(TAG, "clicktype=====" + clickType);
                // TODO Auto-generated method stub
                if (clickType == 1) {
                    isClickStart = false;
//                    Toast.makeText(getActivity(), "停止轨迹服务", Toast.LENGTH_SHORT).show();
//                    把开始时间取出来,记录下来
                    SharedPreferences sharedPreferences = WorkoutActivity.mContext.getSharedPreferences("clickType", Context.MODE_PRIVATE);
                    String formatStartTime = sharedPreferences.getString("formatStartTime", "");
                    String formatMonth = sharedPreferences.getString("formatMonth", "");
                    startTimeLong = sharedPreferences.getLong("startTimeLong", 0);
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(startTimeLong);
                    Date dateStartTrace = c.getTime();
                    traGreendao.addStartTime(formatStartTime, dateStartTrace, 0);
                    //                记录月份格式的日期type为0,方便索引排序;
                    traGreendao.addStartMonth(formatMonth, dateStartTrace);
                    //  成功停止轨迹服务,说明停止跑了,记录开始时间,数据类型设置为type
                    dateStopTrace = new Date();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    formatEndTime = simpleDateFormat.format(dateStopTrace);
                    MyLog.e(TAG, "结束的时间是" + dateStopTrace.toString());
                    traGreendao.addEndTime(formatEndTime, dateStopTrace);
//                    停止后更新ui界面
//                    secondMiddle.setVisibility(View.GONE);
                    secondMiddle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (stopType == 2) {
                                secondMiddle.setVisibility(View.GONE);
                            }
                        }
                    });
                    relativeLayout.setBackgroundColor(Color.BLACK);
                    relativeLayout.getBackground().setAlpha(0);
                    clickType = 0;
                    stopTrace();
                    if (isRegister) {
                        try {
                            WorkoutActivity.mContext.unregisterReceiver(powerReceiver);
                            isRegister = false;
                        } catch (Exception e) {
                            // TODO: handle
                        }
                        queryHistoryTrack(1, "need_denoise=1,need_vacuate=1,need_mapmatch=1");
                    }
                } else {
                    Toast.makeText(getActivity(), getString(R.string.pleaseturnon), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /**
     * 查询历史轨迹
     */
    private void queryHistoryTrack(int processed, String processOption) {

//        // entity标识
//        String entityName = WorkoutActivity.entityName;
//        // 是否返回精简的结果（0 : 否，1 : 是）
//        int simpleReturn = 0;
//        // 是否返回纠偏后轨迹（0 : 否，1 : 是）
        int isProcessed = processed;
        int startTime = (int) (startTimeLong / 1000);
        int endTime = (int) (dateStopTrace.getTime() / 1000);
        // 开始时间
        if (startTime == 0) {
            startTime = (int) (System.currentTimeMillis() / 1000 - 12 * 60 * 60);
        }
        if (endTime == 0) {
            endTime = (int) (System.currentTimeMillis() / 1000);
        }
        workoutActivity.queryHistoryTrack(startTime, endTime);
        // 分页大小
//        int pageSize = 1000;
//        // 分页索引
//        int pageIndex = 1;
//        LBSTraceClient client = new LBSTraceClient(getActivity());
//        client.setLocationMode(LocationMode.High_Accuracy);
//
//        client.queryHistoryTrack(123056, "myTrace", simpleReturn, isProcessed, processOption, startTime, endTime,
//                pageSize,
//                pageIndex,
//                trackListener);
    }


    public void startMonitorService() {
        serviceIntent = new Intent(WorkoutActivity.mContext,
                MonitorService.class);
        WorkoutActivity.mContext.startService(serviceIntent);
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        // 初始化开启轨迹服务监听器
        initOnStartTraceListener();

        // 初始化停止轨迹服务监听器
        initOnStopTraceListener();
    }

    /**
     * 开启轨迹服务
     */
    private void startTrace() {
        // 通过轨迹服务客户端client开启轨迹服务
        stopType = 1;
        WorkoutActivity.client.startTrace(WorkoutActivity.trace, startTraceListener);
        if (!MonitorService.isRunning) {
            // 开启监听service
            MonitorService.isCheck = true;
            MonitorService.isRunning = true;
            startMonitorService();
        }
    }

    /**
     * 停止轨迹服务
     */
    private void stopTrace() {
        stopType = 2;
        // 停止监听service
        MonitorService.isCheck = false;
        MonitorService.isRunning = false;

        // 通过轨迹服务客户端client停止轨迹服务

        WorkoutActivity.client.stopTrace(WorkoutActivity.trace, stopTraceListener);

        if (null != serviceIntent) {
            WorkoutActivity.mContext.stopService(serviceIntent);
        }
    }

    /**
     * 设置采集周期和打包周期
     */
    private void setInterval() {
        WorkoutActivity.client.setInterval(gatherInterval, packInterval);
    }

    /**
     * 设置请求协议
     */
    protected static void setRequestType() {
        int type = 0;
        WorkoutActivity.client.setProtocolType(type);
    }

    /**
     * 查询实时轨迹
     */
    private void queryRealtimeLoc() {
        WorkoutActivity.client.queryRealtimeLoc(WorkoutActivity.serviceId, WorkoutActivity.entityListener);
    }

    /**
     * 查询entityList
     */
    @SuppressWarnings("unused")
    private void queryEntityList() {
        // // entity标识列表（多个entityName，以英文逗号"," 分割）
        String entityNames = WorkoutActivity.entityName;
        // 属性名称（格式为 : "key1=value1,key2=value2,....."）
        String columnKey = "key1=value1,key2=value2";
        // 返回结果的类型（0 : 返回全部结果，1 : 只返回entityName的列表）
        int returnType = 0;
        // 活跃时间（指定该字段时，返回从该时间点之后仍有位置变动的entity的实时点集合）
        // int activeTime = (int) (System.currentTimeMillis() / 1000 - 30);
        int activeTime = 0;
        // 分页大小
        int pageSize = 10;
        // 分页索引
        int pageIndex = 1;

        WorkoutActivity.client.queryEntityList(WorkoutActivity.serviceId, entityNames, columnKey, returnType, activeTime,
                pageSize,
                pageIndex, WorkoutActivity.entityListener);
    }

    /**
     * 初始化OnStartTraceListener
     */
    private void initOnStartTraceListener() {
        // 初始化startTraceListener
        startTraceListener = new OnStartTraceListener() {

            // 开启轨迹服务回调接口（arg0 : 消息编码，arg1 : 消息内容，详情查看类参考）
            public void onTraceCallback(int arg0, String arg1) {
                // TODO Auto-generated method stub
//                showMessage(" " + arg1, Integer.valueOf(arg0));
//                showMessage("开启轨迹服务回调接口消息 [消息编码 : " + arg0 + "，消息内容 : " + arg1 + "]", Integer.valueOf(arg0));
                if (0 == arg0 || 10006 == arg0 || 10008 == arg0 || 10009 == arg0) {
                    isTraceStart = true;
                    // TODO: 2016/8/25
//                    startRefreshThread(true);
                    startRefreshThread(true);
                }
            }

            // 轨迹服务推送接口（用于接收服务端推送消息，arg0 : 消息类型，arg1 : 消息内容，详情查看类参考）
            public void onTracePushCallback(byte arg0, String arg1) {
                // TODO Auto-generated method stub
                if (0x03 == arg0 || 0x04 == arg0) {
                    try {
                        JSONObject dataJson = new JSONObject(arg1);
                        if (null != dataJson) {
                            String mPerson = dataJson.getString("monitored_person");
                            String action = dataJson.getInt("action") == 1 ? "进入" : "离开";
                            String date = DateUtils.getDate(dataJson.getInt("time"));
                            long fenceId = dataJson.getLong("fence_id");
//                            showMessage("监控对象[" + mPerson + "]于" + date + " [" + action + "][" + fenceId + "号]围栏",
//                                    null);
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
//                        showMessage("轨迹服务推送接口消息 [消息类型 : " + arg0 + "，消息内容 : " + arg1 + "]", null);
                    }
                } else {
//                    showMessage("轨迹服务推送接口消息 [消息类型 : " + arg0 + "，消息内容 : " + arg1 + "]", null);
                }
            }

        };
    }

    /**
     * 初始化OnStopTraceListener
     */
    private void initOnStopTraceListener() {
        // 初始化stopTraceListener
        stopTraceListener = new OnStopTraceListener() {

            // 轨迹服务停止成功
            public void onStopTraceSuccess() {
                // TODO Auto-generated method stub
//                showMessage("停止轨迹服务成功", Integer.valueOf(1));
                isTraceStart = false;
                startRefreshThread(false);
                WorkoutActivity.client.onDestroy();
            }

            // 轨迹服务停止失败（arg0 : 错误编码，arg1 : 消息内容，详情查看类参考）
            public void onStopTraceFailed(int arg0, String arg1) {
                // TODO Auto-generated method stub
//                showMessage("停止轨迹服务接口消息 [错误编码 : " + arg0 + "，消息内容 : " + arg1 + "]", null);
                startRefreshThread(false);
            }
        };
    }

    //    设置多少秒去获取数据
    protected class RefreshThread extends Thread {

        protected boolean refresh = true;

        @Override
        public void run() {
            // TODO Auto-generated method stub
            Looper.prepare();
            while (refresh) {
                // 查询实时位置
                queryRealtimeLoc();

                try {
                    Thread.sleep(gatherInterval * 1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    System.out.println("线程休眠失败");
                }
            }
            Looper.loop();
        }
    }
//// TODO: 2016/8/18 画轨迹图

    /**
     * 显示实时轨迹
     *
     * @param location
     */
    protected void showRealtimeTrack(TraceLocation location) {

        if (null == refreshThread || !refreshThread.refresh) {
            return;
        }

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        if (Math.abs(latitude - 0.0) < 0.000001 && Math.abs(longitude - 0.0) < 0.000001) {
            showMessage("当前查询无轨迹点", null);
        } else {

            LatLng latLng = new LatLng(latitude, longitude);

            if (1 == location.getCoordType()) {
                LatLng sourceLatLng = latLng;
                CoordinateConverter converter = new
                        CoordinateConverter();
                converter.from(CoordType.GPS);
                converter.coord(sourceLatLng);
                latLng = converter.convert();
            }

            pointList.add(latLng);

            if (isInUploadFragment) {
                // 绘制实时点
                drawRealtimePoint(latLng);
            }

        }

    }

    /**
     * 绘制实时点
     *
     * @param point
     */
    private void drawRealtimePoint(LatLng point) {

        if (null != overlay) {
            overlay.remove();
        }

        MapStatus mMapStatus = new MapStatus.Builder().target(point).zoom(19).build();

        msUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);

//        if (null == realtimeBitmap) {
//            realtimeBitmap = BitmapDescriptorFactory
//                    .fromResource(R.mipmap.icon_geo);
//        }
//
//        overlayOptions = new MarkerOptions().position(point)
//                .icon(realtimeBitmap).zIndex(9).draggable(true);
//
//        if (pointList.size() >= 2 && pointList.size() <= 10000) {
//            // 添加路线（轨迹）
//            polyline = new PolylineOptions().width(10)
//                    .color(Color.RED).points(pointList);
//        }

        addMarker();

    }

    /**
     * 添加地图覆盖物
     */
    protected static void addMarker() {

        if (null != msUpdate) {
            WorkoutActivity.mBaiduMap.setMapStatus(msUpdate);
        }

        // 路线覆盖物
        if (null != polyline) {
            WorkoutActivity.mBaiduMap.addOverlay(polyline);
        }

        // 实时点覆盖物
        if (null != overlayOptions) {
            overlay = WorkoutActivity.mBaiduMap.addOverlay(overlayOptions);
        }

    }

    protected void startRefreshThread(boolean isStart) {
        if (null == refreshThread) {
            refreshThread = new RefreshThread();
        }
        refreshThread.refresh = isStart;
        if (isStart) {
            if (!refreshThread.isAlive()) {
                refreshThread.start();
            }
        } else {
            refreshThread = null;
        }
    }

    private void showMessage(final String message, final Integer errorNo) {

        new Handler(WorkoutActivity.mContext.getMainLooper()).post(new Runnable() {
            public void run() {
                Toast.makeText(WorkoutActivity.mContext, message, Toast.LENGTH_LONG).show();

                if (null != errorNo) {
                    if (0 == errorNo.intValue() || 10006 == errorNo.intValue() || 10008 == errorNo.intValue()
                            || 10009 == errorNo.intValue()) {
                        btnStartTrace.setBackgroundColor(Color.rgb(0x99, 0xcc, 0xff));
                        btnStartTrace.setTextColor(Color.rgb(0x00, 0x00, 0xd8));
                    } else if (1 == errorNo.intValue() || 10004 == errorNo.intValue()) {
                        btnStartTrace.setBackgroundColor(Color.rgb(0xff, 0xff, 0xff));
                        btnStartTrace.setTextColor(Color.rgb(0x00, 0x00, 0x00));
                    }
                }
            }
        });

    }


}

