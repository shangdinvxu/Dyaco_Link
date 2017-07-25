package com.linkloving.dyh08.logic.UI.workout.trackshow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.LocationMode;
import com.baidu.trace.OnEntityListener;
import com.baidu.trace.OnStartTraceListener;
import com.baidu.trace.OnStopTraceListener;
import com.baidu.trace.Trace;
import com.baidu.trace.TraceLocation;
import com.linkloving.dyh08.R;
import com.linkloving.dyh08.basic.toolbar.ToolBarActivity;
import com.linkloving.dyh08.logic.UI.workout.Greendao.TraceGreendao;
import com.linkloving.dyh08.logic.UI.workout.trackutils.DateUtils;
import com.linkloving.dyh08.utils.logUtils.MyLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Trace.GreenDao.DaoMaster;
import Trace.GreenDao.Note;

public class work_outActivity extends ToolBarActivity {

    private static final String TAG = work_outActivity.class.getSimpleName();
    /**
     * 轨迹服务
     */
    protected static Trace trace = null;
    /**
     * entity标识
     */
    protected static String entityName = null;
    /**
     * 开启轨迹服务监听器
     */
    protected static OnStartTraceListener startTraceListener = null;

    /**
     * 停止轨迹服务监听器
     */
    protected static OnStopTraceListener stopTraceListener = null;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 采集周期（单位 : 秒）
     */
    private int gatherInterval = 10;

    /**
     * 打包周期（单位 : 秒）
     */
    private int packInterval = 20;

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
     * 刷新地图线程(获取实时点)
     */
    protected RefreshThread refreshThread = null;

    protected static MapStatusUpdate msUpdate = null;

    private View view = null;

    private LayoutInflater mInflater = null;

    protected static boolean isInUploadFragment = true;

    private static boolean isRegister = false;

    protected static PowerManager pm = null;

    protected static PowerManager.WakeLock wakeLock = null;

    private PowerReceiver powerReceiver = new PowerReceiver();

    private   SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private   SimpleDateFormat simMonth = new SimpleDateFormat("yyyy-MM");

    /**
     * 鹰眼服务ID，开发者创建的鹰眼服务对应的服务ID
     */
    protected static long serviceId = 123056;

    /**
     * 轨迹服务类型（0 : 不建立socket长连接， 1 : 建立socket长连接但不上传位置数据，2 : 建立socket长连接并上传位置数据）
     */
    private int traceType = 2;

    /**
     * 轨迹服务客户端
     */
    protected static LBSTraceClient client = null;

    /**
     * Entity监听器
     */
    protected static OnEntityListener entityListener = null;

    private Button btnTrackUpload;

    private Button btnTrackQuery;
    protected static MapView bmapView = null;

    protected static BaiduMap mBaiduMap = null;

    private Button btnStartTrace = null;

    private Button btnStopTrace = null;
    /**
     * 用于对Fragment进行管理
     */
    private FragmentManager fragmentManager;

    private TrackUploadFragment mTrackUploadFragment;

    private TrackQueryFragment mTrackQueryFragment;

    protected static Context mContext = null;

    private TraceGreendao traGreendao ;

    private SQLiteDatabase db;
    private DaoMaster.DevOpenHelper devOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.tw_work_out_activity);
        mContext = getApplicationContext();
        // 初始化轨迹服务客户端
        client = new LBSTraceClient(mContext);
        // 设置定位模式
        client.setLocationMode(LocationMode.High_Accuracy);
        // 初始化entity标识
        entityName = "myTrace";
        // 初始化轨迹服务
        trace = new Trace(getApplicationContext(), serviceId, entityName, traceType);
        // 初始化EntityListener
        initOnEntityListener();
        // 初始化
        init();
        // 初始化监听器
        initListener();
        // 设置采集周期
        setInterval();
        // 设置http请求协议类型
        setRequestType();
        // 初始化组件
        initComponent();
        Geofence.addEntity();
        devOpenHelper = new DaoMaster.DevOpenHelper(this, "Note", null);
        db = devOpenHelper.getReadableDatabase();
        traGreendao = new TraceGreendao(work_outActivity.this,db);
    }
    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }
    /**
     * 初始化组件
     */
    private void initComponent() {
        // 初始化控件
        btnTrackUpload = (Button) findViewById(R.id.btn_startTrace);
        btnTrackQuery = (Button) findViewById(R.id.btn_trackQuery);
        bmapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = bmapView.getMap();
        bmapView.showZoomControls(false);
    }

    /**
     * 点击事件
     */
    public void onClick(View v) {
        // TODO Auto-generated method stub
        handlerButtonClick(v.getId());
    }

    /**
     * 初始化OnEntityListener
     */
    private void initOnEntityListener() {
        entityListener = new OnEntityListener() {

            // 请求失败回调接口
            @Override
            public void onRequestFailedCallback(String arg0) {
                // TODO Auto-generated method stub
                // TrackApplication.showMessage("entity请求失败回调接口消息 : " + arg0);
                System.out.println("entity请求失败回调接口消息 : " + arg0);
            }

            // 添加entity回调接口
            public void onAddEntityCallback(String arg0) {
                // TODO Auto-generated method stub
                TrackApplication.showMessage("添加entity回调接口消息 : " + arg0);
            }

            // 查询entity列表回调接口
            @Override
            public void onQueryEntityListCallback(String message) {
                // TODO Auto-generated method stub
                System.out.println("entityList回调消息 : " + message);
            }
            @Override
            public void onReceiveLocation(TraceLocation location) {
                // TODO Auto-generated method stub
                if (mTrackUploadFragment != null) {
                    mTrackUploadFragment.showRealtimeTrack(location);
                    Date date = new Date();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String format = simpleDateFormat.format(date);
                    traGreendao.addNote(format, date, date, location.getLatitude(), location.getLongitude());
                }
            }
        };
    }

    /**
     * 处理tab点击事件
     *
     * @param id
     */
    private void handlerButtonClick(int id) {

        switch (id) {
            //历史轨迹
            case R.id.btn_trackQuery:
                mTrackQueryFragment.addMarker();
                btnTrackQuery.setTextColor(Color.rgb(0x00, 0x00, 0xd8));
                btnTrackQuery.setBackgroundColor(Color.rgb(0x99, 0xcc, 0xff));
                mBaiduMap.setOnMapClickListener(null);
                break;
            //轨迹追踪
            case R.id.btn_trackUpload:
startRefreshThread(true);
                TrackUploadFragment.addMarker();
                Geofence.addMarker();
                btnTrackUpload.setTextColor(Color.rgb(0x00, 0x00, 0xd8));
                btnTrackUpload.setBackgroundColor(Color.rgb(0x99, 0xcc, 0xff));
                mBaiduMap.setOnMapClickListener(null);
                break;
        }

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        TrackUploadFragment.isInUploadFragment = false;
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        client.onDestroy();
//        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 初始化
     */
    private void init() {

        btnStartTrace = (Button)findViewById(R.id.btn_startTrace);

        btnStopTrace = (Button) findViewById(R.id.btn_stopTrace);

        btnStartTrace.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub

                Toast.makeText(work_outActivity.this, "正在开启轨迹服务，请稍候", Toast.LENGTH_LONG).show();
                startTrace();
                if (!isRegister) {
                    if (null == pm) {
                        pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
                    }
                    if (null == wakeLock) {
                        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "track upload");
                    }
                    IntentFilter filter = new IntentFilter();
                    filter.addAction(Intent.ACTION_SCREEN_OFF);
                    filter.addAction(Intent.ACTION_SCREEN_ON);
                    mContext.registerReceiver(powerReceiver, filter);
                    isRegister = true;
                }
            }
        });
        btnStopTrace.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(work_outActivity.this, "正在停止轨迹服务，请稍候", Toast.LENGTH_SHORT).show();
                stopTrace();
                if (isRegister) {
                    try {
                        mContext.unregisterReceiver(powerReceiver);
                        isRegister = false;
                    } catch (Exception e) {
                    }
                }
                Date date = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String format = simpleDateFormat.format(date);
                List<Note> searchtimes = traGreendao.searchtimes
                        (format);
                for (int i=0;i<searchtimes.size();i++) {
                    MyLog.e(TAG, searchtimes.get(i).getStartDate().toString());
                }
            }
        });
    }
    public void startMonitorService() {
        serviceIntent = new Intent(mContext,
                MonitorService.class);
     mContext.startService(serviceIntent);
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
        client.startTrace(trace, startTraceListener);
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

        // 停止监听service
        MonitorService.isCheck = false;
        MonitorService.isRunning = false;

        // 通过轨迹服务客户端client停止轨迹服务

        client.stopTrace(trace, stopTraceListener);

        if (null != serviceIntent) {
            mContext.stopService(serviceIntent);
        }
    }

    /**
     * 设置采集周期和打包周期
     */
    private void setInterval() {
       client.setInterval(gatherInterval, packInterval);
    }

    /**
     * 设置请求协议
     */
    protected static void setRequestType() {
        int type = 0;
        client.setProtocolType(type);
    }

    /**
     * 查询实时轨迹
     */
    private void queryRealtimeLoc() {
       client.queryRealtimeLoc(serviceId, entityListener);
    }

    /**
     * 查询entityList
     */
    @SuppressWarnings("unused")
    private void queryEntityList() {
        // // entity标识列表（多个entityName，以英文逗号"," 分割）
        String entityNames = entityName;
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
        client.queryEntityList(serviceId, entityNames, columnKey, returnType, activeTime,
                pageSize,
                pageIndex, entityListener);
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
//  成功开启轨迹服务,说明开始跑了,记录开始时间,数据类型设置为type
                Date date = new Date();
                String formatMonth = simMonth.format(date);
                String format = simpleDateFormat.format(date);
//                    traGreendao.addStartTime(format, date);
//                记录月份格式的日期type为0,方便索引排序;
                    traGreendao.addStartMonth(formatMonth,date);
                showMessage(" " + arg1, Integer.valueOf(arg0));
//                showMessage("开启轨迹服务回调接口消息 [消息编码 : " + arg0 + "，消息内容 : " + arg1 + "]", Integer.valueOf(arg0));
                if (0 == arg0 || 10006 == arg0 || 10008 == arg0 || 10009 == arg0) {
                    isTraceStart = true;
                    // startRefreshThread(true);
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
                            showMessage("监控对象[" + mPerson + "]于" + date + " [" + action + "][" + fenceId + "号]围栏",
                                    null);
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        showMessage("轨迹服务推送接口消息 [消息类型 : " + arg0 + "，消息内容 : " + arg1 + "]", null);
                    }
                } else {
                    showMessage("轨迹服务推送接口消息 [消息类型 : " + arg0 + "，消息内容 : " + arg1 + "]", null);
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
                showMessage("停止轨迹服务成功", Integer.valueOf(1));
                    //  成功停止轨迹服务,说明停止跑了,记录开始时间,数据类型设置为type
                    Date date = new Date();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String format = simpleDateFormat.format(date);
                    MyLog.e(TAG,"结束的时间是"+date.toString());
                    traGreendao.addEndTime(format, date);
                isTraceStart = false;
                startRefreshThread(false);
                client.onDestroy();
            }
            // 轨迹服务停止失败（arg0 : 错误编码，arg1 : 消息内容，详情查看类参考）
            public void onStopTraceFailed(int arg0, String arg1) {
                // TODO Auto-generated method stub
                showMessage("停止轨迹服务接口消息 [错误编码 : " + arg0 + "，消息内容 : " + arg1 + "]", null);
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
    protected  void  showRealtimeTrack(TraceLocation location) {

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
                converter.from(CoordinateConverter.CoordType.GPS);
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

        if (null == realtimeBitmap) {
            realtimeBitmap = BitmapDescriptorFactory
                    .fromResource(R.mipmap.icon_geo);
        }

        overlayOptions = new MarkerOptions().position(point)
                .icon(realtimeBitmap).zIndex(9).draggable(true);

        if (pointList.size() >= 2 && pointList.size() <= 10000) {
            // 添加路线（轨迹）
            polyline = new PolylineOptions().width(10)
                    .color(Color.RED).points(pointList);
        }





        addMarker();

    }

    /**
     * 添加地图覆盖物
     */
    protected static void addMarker() {

        if (null != msUpdate) {
            mBaiduMap.setMapStatus(msUpdate);
        }

        // 路线覆盖物
        if (null != polyline) {
            mBaiduMap.addOverlay(polyline);
        }

        // 实时点覆盖物
        if (null != overlayOptions) {
            overlay = mBaiduMap.addOverlay(overlayOptions);
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

        new Handler(mContext.getMainLooper()).post(new Runnable() {
            public void run() {
                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();

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

    @Override
    protected void getIntentforActivity() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListeners() {

    }
}
