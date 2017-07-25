package com.linkloving.dyh08.logic.UI.workout.trackshow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ZoomButton;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.LocationMode;
import com.baidu.trace.OnEntityListener;
import com.baidu.trace.OnTrackListener;
import com.baidu.trace.Trace;
import com.baidu.trace.TraceLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.linkloving.dyh08.R;
import com.linkloving.dyh08.basic.toolbar.ToolBarActivity;
import com.linkloving.dyh08.logic.UI.Groups.baidu.GroupsDetailsActivity;
import com.linkloving.dyh08.logic.UI.workout.GooglemapActivity;
import com.linkloving.dyh08.logic.UI.workout.Greendao.TraceGreendao;
import com.linkloving.dyh08.logic.UI.main.PortalActivity;
import com.linkloving.dyh08.logic.UI.workout.trackutils.GsonService;
import com.linkloving.dyh08.logic.UI.workout.trackutils.HistoryTrackData;
import com.linkloving.dyh08.utils.logUtils.MyLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import Trace.GreenDao.DaoMaster;

//,OnMapReadyCallback
@SuppressLint("NewApi")
public class WorkoutActivity extends ToolBarActivity implements OnClickListener{

    private static final String TAG = WorkoutActivity.class.getSimpleName();
    /**
     * 轨迹服务
     */
    protected static Trace trace = null;
    /**
     * entity标识
     */
    public static String entityName = null;

    /**
     * 鹰眼服务ID，开发者创建的鹰眼服务对应的服务ID
     */
    public static long serviceId = 123056;

    /**
     * 轨迹服务类型（0 : 不建立socket长连接， 1 : 建立socket长连接但不上传位置数据，2 : 建立socket长连接并上传位置数据）
     */
    private int traceType = 2;

    /**
     * 轨迹服务客户端
     */
    public static LBSTraceClient client = null;

    /**
     * Track监听器
     */
    protected static OnTrackListener trackListener = null;

    /**
     * Entity监听器
     */
    protected static OnEntityListener entityListener = null;

    private Button btnTrackUpload;

    private Button btnTrackQuery;
    protected static MapView bmapView = null;

    protected static BaiduMap mBaiduMap = null;


    protected static MapStatusUpdate msUpdate = null;
    protected static OverlayOptions overlayOptions;
    private static BitmapDescriptor realtimeBitmap;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    /**
     * 用于对Fragment进行管理
     */
    private FragmentManager fragmentManager;

    private TrackUploadFragment mTrackUploadFragment;

    private TrackQueryFragment mTrackQueryFragment;

    protected static Context mContext = null;

    private TraceGreendao traGreendao;

    private SQLiteDatabase db;
    private DaoMaster.DevOpenHelper devOpenHelper;

    // 起点图标
    private static BitmapDescriptor bmStart;
    // 终点图标
    private static BitmapDescriptor bmEnd;

    // 起点图标覆盖物
    private static MarkerOptions startMarker = null;
    // 终点图标覆盖物
    private static MarkerOptions endMarker = null;
    // 路线覆盖物
    public static PolylineOptions polyline = null;

    private static MarkerOptions markerOptions = null;
    private int noteStartTime, noteEndTime;
    private SharedPreferences locationsp;
    /**
     * google 地图
     */
    private GoogleMap mgoogleMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SDKInitializer.initialize(getApplicationContext());

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.tw_workout_trace_activity);

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

        // 初始化组件
        initComponent();

        Geofence.addEntity();
        devOpenHelper = new DaoMaster.DevOpenHelper(this, "Note", null);
        db = devOpenHelper.getReadableDatabase();
        traGreendao = new TraceGreendao(WorkoutActivity.this, db);
        initOnTrackListener();
        locationsp = getSharedPreferences("Location", MODE_PRIVATE);

      /*  SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);*/
    }


    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        // 设置默认的Fragment
        setDefaultFragment();
    }

    /**
     * 初始化组件
     */
    private void initComponent() {
        // 初始化控件
        btnTrackUpload = (Button) findViewById(R.id.btn_trackUpload);
        btnTrackQuery = (Button) findViewById(R.id.btn_trackQuery);
        btnTrackUpload.setOnClickListener(this);
        btnTrackQuery.setOnClickListener(this);
        fragmentManager = getSupportFragmentManager();
        bmapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = bmapView.getMap();

//        设置是否显示缩放控件
       /* Point point = new Point();
        point.x = 200 ;
        point.y = 200 ;
        bmapView.setZoomControlsPosition(point);*/
        bmapView.showZoomControls(false);

    }

    /**
     * 设置默认的Fragment
     */
    private void setDefaultFragment() {
        handlerButtonClick(R.id.btn_trackUpload);
    }

    /**
     * 点击事件
     */
    public void onClick(View v) {
        // TODO Auto-generated method stub
        handlerButtonClick(v.getId());
    }

    public void queryHistoryTrack(int startTime, int endTime) {
        // 是否返回精简的结果（0 : 否，1 : 是）
        int simpleReturn = 0;
        // 是否返回纠偏后轨迹（0 : 否，1 : 是）
        int isProcessed = 1;
        // 分页大小
        int pageSize = 1000;
        // 分页索引
        int pageIndex = 1;
        noteStartTime = startTime;
        noteEndTime = endTime;
        LBSTraceClient client = new LBSTraceClient(WorkoutActivity.this);
        client.setLocationMode(LocationMode.High_Accuracy);
        client.queryHistoryTrack(123056, "myTrace", simpleReturn, isProcessed, "need_denoise=1,need_vacuate=1,need_mapmatch=1", startTime, endTime,
                pageSize,
                pageIndex,
                trackListener);
    }


    /**
     * 初始化OnTrackListener
     */
    private void initOnTrackListener() {

        trackListener = new OnTrackListener() {

            // 请求失败回调接口
            @Override
            public void onRequestFailedCallback(String arg0) {
                // TODO Auto-generated method stub
                TrackApplication.showMessage("track请求失败回调接口消息 : " + arg0);
                MyLog.e(TAG, "onRequestFailedCallback" + arg0);
            }

            // 查询历史轨迹回调接口
            @Override
            public void onQueryHistoryTrackCallback(String arg0) {
                // TODO Auto-generated method stub
                super.onQueryHistoryTrackCallback(arg0);
                showHistoryTrack(arg0);
                MyLog.e(TAG, "onQueryHistoryTrackCallback" + arg0);
            }

            @Override
            public void onQueryDistanceCallback(String arg0) {
                MyLog.e(TAG, "onQueryDistanceCallback" + arg0);
                // TODO Auto-generated method stub
                try {
                    JSONObject dataJson = new JSONObject(arg0);
                    if (null != dataJson && dataJson.has("status") && dataJson.getInt("status") == 0) {
                        double distance = dataJson.getDouble("distance");
                        DecimalFormat df = new DecimalFormat("#.0");
                        TrackApplication.showMessage("里程 : " + df.format(distance) + "米");
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    TrackApplication.showMessage("queryDistance回调消息 : " + arg0);
                }

            }

            @Override
            public Map<String, String> onTrackAttrCallback() {
                // TODO Auto-generated method stub
                MyLog.e(TAG, "onTrackAttrCallback");
                return null;
            }

        };
    }


    /**
     * 显示历史轨迹
     *
     * @param historyTrack
     */
    private void showHistoryTrack(final String historyTrack) {

        final HistoryTrackData historyTrackData = GsonService.parseJson(historyTrack,
                HistoryTrackData.class);

        List<LatLng> latLngList = new ArrayList<LatLng>();

        final Date date = new Date((long) noteStartTime * 1000 + 2000);


        if (historyTrackData != null && historyTrackData.getStatus() == 0) {
            if (historyTrackData.getListPoints() != null) {
                latLngList.addAll(historyTrackData.getListPoints());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        traGreendao.addNote(historyTrack, date, date, 0, 0);
                    }
                }).start();
            }
            // 绘制历史轨迹
            drawHistoryTrack(latLngList, historyTrackData.distance);
        }
    }


    /**
     * 绘制历史轨迹
     *
     * @param points
     */
    private void drawHistoryTrack(final List<LatLng> points, final double distance) {

        // 绘制新覆盖物前，清空之前的覆盖物
        mBaiduMap.clear();

        if (points.size() == 1) {
            points.add(points.get(0));
        }

        if (points == null || points.size() == 0) {
            TrackApplication.showMessage("当前查询无轨迹点");
            resetMarker();
        } else if (points.size() > 1) {

            LatLng llC = points.get(0);
            LatLng llD = points.get(points.size() - 1);
            LatLngBounds bounds = new LatLngBounds.Builder()
                    .include(llC).include(llD).build();

            msUpdate = MapStatusUpdateFactory.newLatLngBounds(bounds);

            bmStart = BitmapDescriptorFactory.fromResource(R.mipmap.icon_start);
            bmEnd = BitmapDescriptorFactory.fromResource(R.mipmap.icon_end);

            // 添加起点图标
            startMarker = new MarkerOptions()
                    .position(points.get(points.size() - 1)).icon(bmStart)
                    .zIndex(9).draggable(true);

            // 添加终点图标
            endMarker = new MarkerOptions().position(points.get(0))
                    .icon(bmEnd).zIndex(9).draggable(true);

            // 添加路线（轨迹）
            polyline = new PolylineOptions().width(10).color(Color.RED).points(points);

            markerOptions = new MarkerOptions();
            markerOptions.flat(true);
            markerOptions.anchor(0.5f, 0.5f);
            markerOptions.icon(BitmapDescriptorFactory
                    .fromResource(R.mipmap.icon_gcoding));
            markerOptions.position(points.get(points.size() - 1));
            addMarker();
            MyLog.e("当前轨迹里程为 : " + (int) distance + "米");
            TrackApplication.showMessage("当前轨迹里程为 : " + (int) distance + "米");
        }

    }

    /**
     * 添加覆盖物
     */
    protected void addMarker() {

        if (null != msUpdate) {
            mBaiduMap.animateMapStatus(msUpdate, 2000);
        }

        if (null != startMarker) {
            mBaiduMap.addOverlay(startMarker);
        }

        if (null != endMarker) {
            mBaiduMap.addOverlay(endMarker);
        }

        if (null != polyline) {
            mBaiduMap.addOverlay(polyline);
        }

    }

    /**
     * 重置覆盖物
     */
    private void resetMarker() {
        startMarker = null;
        endMarker = null;
        polyline = null;
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
//                TrackApplication.showMessage("添加entity回调接口消息 : " + arg0);
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
//                    MyLog.e(TAG,"在接受到坐标"+location.toString());
                    mTrackUploadFragment.showRealtimeTrack(location);
                    SharedPreferences.Editor edit = locationsp.edit();
                    edit.putFloat("Latitude", (float) location.getLatitude());
                    edit.putFloat("Longitude", (float) location.getLongitude());
                    edit.commit();
//                    Date date = new Date();
//
//                    String format = simpleDateFormat.format(date);
//                    traGreendao.addNote(format, date, date, location.getLatitude(), location.getLongitude());
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
        // 重置button状态
        onResetButton();
        // 开启Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 隐藏Fragment
        hideFragments(transaction);

        switch (id) {
            //历史轨迹
            case R.id.btn_trackQuery:

                TrackUploadFragment.isInUploadFragment = false;

                if (mTrackQueryFragment == null) {
                    mTrackQueryFragment = new TrackQueryFragment();
                    transaction.add(R.id.fragment_content, mTrackQueryFragment);
                } else {
                    transaction.show(mTrackQueryFragment);
                }
                if (null != mTrackUploadFragment) {
                    mTrackUploadFragment.startRefreshThread(false);
                }
                mTrackQueryFragment.addMarker();
                btnTrackQuery.setTextColor(Color.rgb(0x00, 0x00, 0xd8));
                btnTrackQuery.setBackgroundColor(Color.rgb(0x99, 0xcc, 0xff));
                mBaiduMap.setOnMapClickListener(null);
                break;
            //轨迹追踪
            case R.id.btn_trackUpload:

                TrackUploadFragment.isInUploadFragment = true;

                if (mTrackUploadFragment == null) {
                    mTrackUploadFragment = new TrackUploadFragment();
                    transaction.add(R.id.fragment_content, mTrackUploadFragment);
                } else {
                    transaction.show(mTrackUploadFragment);
                }

                mTrackUploadFragment.startRefreshThread(true);
                TrackUploadFragment.addMarker();
                Geofence.addMarker();
                btnTrackUpload.setTextColor(Color.rgb(0x00, 0x00, 0xd8));
                btnTrackUpload.setBackgroundColor(Color.rgb(0x99, 0xcc, 0xff));
                mBaiduMap.setOnMapClickListener(null);
                break;
        }
        // 事务提交
        transaction.commit();

    }


    /**
     * 重置button状态
     */
    private void onResetButton() {
        btnTrackQuery.setTextColor(Color.rgb(0x00, 0x00, 0x00));
        btnTrackQuery.setBackgroundColor(Color.rgb(0xFF, 0xFF, 0xFF));
        btnTrackUpload.setTextColor(Color.rgb(0x00, 0x00, 0x00));
        btnTrackUpload.setBackgroundColor(Color.rgb(0xFF, 0xFF, 0xFF));
    }

    /**
     * 隐藏Fragment
     */
    private void hideFragments(FragmentTransaction transaction) {

        if (mTrackQueryFragment != null) {
            transaction.hide(mTrackQueryFragment);
        }
        if (mTrackUploadFragment != null) {
            transaction.hide(mTrackUploadFragment);
        }
        // 清空地图覆盖物
        mBaiduMap.clear();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        TrackUploadFragment.isInUploadFragment = false;
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


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
//        client.onDestroy();
//        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 获取设备IMEI码
     *
     * @param context
     * @return
     */
    protected static String getImei(Context context) {
        String mImei = "NULL";
        try {
            mImei = ((TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        } catch (Exception e) {
            System.out.println("获取IMEI码失败");
            mImei = "NULL";
        }
        return mImei;
    }


 /*   public void initGoogleServer() {
        createLocationRequest();
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,mLocationRequest,this);

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        MyLog.e(TAG, "googleMap++好了");
        mgoogleMap = googleMap;

        googleMap.setMyLocationEnabled(true);
        Location myLocation = googleMap.getMyLocation();

        com.google.android.gms.maps.model.LatLng mapCenter = new com.google.android.gms.maps.model.LatLng(31.298886, 120.58531600000003);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapCenter, 13));


        googleMap.addMarker(new com.google.android.gms.maps.model.MarkerOptions()
                .icon(com.google.android.gms.maps.model.BitmapDescriptorFactory.fromResource(R.mipmap.icon_geo))
                .position(mapCenter)
                .flat(true)
                .rotation(245));

        CameraPosition cameraPosition = CameraPosition.builder()
                .target(mapCenter)
                .zoom(13)
                .bearing(90)
                .build();

        // Animate the change in camera view over 2 seconds
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                3000, null);


        googleApiClient = new GoogleApiClient.Builder(WorkoutActivity.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {

                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                }).build();
        googleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(WorkoutActivity.this, location.getLatitude() + "___________" + location.getLongitude(), Toast.LENGTH_SHORT).show();
        MyLog.e("googleLocation", location.getLatitude() + "___________" + location.getLongitude());
        mgoogleMap.addMarker(new com.google.android.gms.maps.model.MarkerOptions().position(new com.google.android.gms.maps.model.LatLng(location.getLongitude(), location.getLatitude())).title("Marker"));
        mgoogleMap.addPolyline(new com.google.android.gms.maps.model.PolylineOptions().add(new com.google.android.gms.maps.model.LatLng(location.getLatitude(), location.getLongitude())));
    }*/
}
