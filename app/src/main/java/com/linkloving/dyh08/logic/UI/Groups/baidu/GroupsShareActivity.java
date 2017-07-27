package com.linkloving.dyh08.logic.UI.Groups.baidu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
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
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.LocationMode;
import com.baidu.trace.OnTrackListener;
import com.example.android.bluetoothlegatt.utils.ToastUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.linkloving.band.dto.SportRecord;
import com.linkloving.dyh08.MyApplication;
import com.linkloving.dyh08.R;
import com.linkloving.dyh08.basic.toolbar.ToolBarActivity;
import com.linkloving.dyh08.db.sport.UserDeviceRecord;
import com.linkloving.dyh08.logic.UI.workout.Greendao.TraceGreendao;
import com.linkloving.dyh08.logic.UI.workout.trackshow.TrackApplication;
import com.linkloving.dyh08.logic.UI.workout.trackshow.WorkoutActivity;
import com.linkloving.dyh08.logic.UI.workout.trackutils.GsonService;
import com.linkloving.dyh08.logic.UI.workout.trackutils.HistoryTrackData;
import com.linkloving.dyh08.prefrences.PreferencesToolkits;
import com.linkloving.dyh08.utils.CommonUtils;
import com.linkloving.dyh08.utils.ToolKits;
import com.linkloving.dyh08.utils.logUtils.MyLog;
import com.linkloving.dyh08.utils.sportUtils.LocationUtils;
import com.linkloving.utils.TimeZoneHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import Trace.GreenDao.DaoMaster;
import Trace.GreenDao.Note;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.linkedin.LinkedIn;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.OnekeyShareTheme;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import static com.linkloving.dyh08.R.id.gourps_topmap;

/**
 * Created by Daniel.Xu on 2016/8/22.
 */
public class GroupsShareActivity extends ToolBarActivity implements OnMapReadyCallback {
    private static final String TAG = GroupsShareActivity.class.getSimpleName();
    @InjectView(gourps_topmap)
    MapView gourpsTopmap;
    @InjectView(R.id.groups_time)
    TextView groupsTime;
    @InjectView(R.id.groups_tv_Step)
    AppCompatTextView groupsTvStep;
    @InjectView(R.id.groups_tv_Calories)
    AppCompatTextView groupsTvCalories;
    @InjectView(R.id.groups_tv_Distance)
    AppCompatTextView groupsTvDistance;
    @InjectView(R.id.main_tv_Duration)
    AppCompatTextView groupsTvDuration;
    @InjectView(R.id.shareET)
    EditText shareText;

    @InjectView(R.id.shareWhere)
    EditText shareWhereText;
    @InjectView(R.id.sharebutton)
    Button shareButton;
    @InjectView(R.id.screenhot)
    LinearLayout screenhot;

    private static Overlay overlay = null;
    private int user_id;
    private static BaiduMap baiduMap = null;
    private SQLiteDatabase db;
    private DaoMaster.DevOpenHelper devOpenHelper;
    private List<Note> startTimeList;
    private List<Note> endTimeList;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
    private TraceGreendao traGreendao;
    private Integer position;
    private ArrayList<SportRecord> sportRecordArrayList;
    private Date startDate;
    private Date endDate;
    private float distance = 0;
    private int step = 0;
    private int calories = 0;
    private long itemDuration;
    private String duration;
    protected static MapStatusUpdate msUpdate = null;
    protected static OverlayOptions overlayOptions;
    private static BitmapDescriptor realtimeBitmap;
    private static List<LatLng> pointList = new ArrayList<LatLng>();
    private String filePathCache = "/sdcard/ranking_v111.png";
    private String filePathCacheUnder = "/sdcard/ranking_v222.png";
    private String filePathCacheTotal = "/sdcard/ranking_v333.png";
    private LocationClient mlocationClient;
    private MyLocationListener mMyLocationListener;
    private ImageView fb, wx, qq, linkin, instagram;
    private ImageView twitter;
    private View view;
    private List<Note> workDataNotes;
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

    /**
     * Track监听器
     */
    protected static OnTrackListener trackListener = null;
    private int user_weight;

    private GoogleMap mGoogleMap ;
    private SupportMapFragment mapFragment;
    private SharedPreferences locationsp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tw_groups_share);
        ButterKnife.inject(this);
        view = LayoutInflater.from(GroupsShareActivity.this).inflate(R.layout.tw_share_day, null);
        locationsp = getSharedPreferences("Location", MODE_PRIVATE);
//        ShareSDK.initSDK(GroupsShareActivity.this);
        user_id = MyApplication.getInstance(GroupsShareActivity.this).getLocalUserInfoProvider().getUser_id();
        user_weight = MyApplication.getInstance(GroupsShareActivity.this).getLocalUserInfoProvider().getUserBase().getUser_weight();

        /**加载google 地图*/
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        baiduMap = gourpsTopmap.getMap();
        initDataOnView();
        initOnTrackListener();
        initPopupWindow();
    }

    private void initDataOnView() {
        Intent intent = getIntent();
        String postionStr = intent.getStringExtra("postion");
        position = Integer.valueOf(postionStr);
        devOpenHelper = new DaoMaster.DevOpenHelper(GroupsShareActivity.this, "Note", null);
        db = devOpenHelper.getReadableDatabase();
        traGreendao = new TraceGreendao(GroupsShareActivity.this, db);
        startTimeList = traGreendao.searchAllStarttime();
        endTimeList = traGreendao.searchAllEndTime();
        sort sort = new sort();
        Collections.sort(startTimeList, sort);
        Collections.sort(endTimeList, sort);
        startDate = startTimeList.get(position).getStartDate();
        endDate = endTimeList.get(position).getStartDate();
        String durtion = getDurtion(position);
        groupsTvDuration.setText(durtion);
        groupsTvDistance.setText(getDistanceKM() + "");
        groupsTvStep.setText(getStep() + "");
        groupsTime.setText(getMiddleTime());
        groupsTvCalories.setText(getCalories() + "");

    }


    //判断百度还是google 2,运动数据1.
    public void isGoogle(){
        if ( startTimeList.get(position).getLatitude()!=null&&startTimeList.get(position).getLatitude()==2){
            addGoogleMap();
        }else if (startTimeList.get(position).getLatitude()!=null&&startTimeList.get(position).getLatitude()==1){
            boolean mapSelect = PreferencesToolkits.getMapSelect(GroupsShareActivity.this);
            if (mapSelect){
                addGoogleMapOnePoint();
            }else {
                hideGoogleMap();
            }
        } else {
            hideGoogleMap();
            initTrace();
        }

    }

    public void addGoogleMapOnePoint(){
        gourpsTopmap.setVisibility(View.GONE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                //从数据库中获取的坐标点画,有问题,需要纠偏.暂不采用.
                List<Note> lists = traGreendao.searchLocation(startDate, endDate);
                if (lists.size() == 0) {
                    SharedPreferences location = getSharedPreferences("Location", MODE_PRIVATE);
                    float latitude = location.getFloat("Latitude", 11);
                    float longitude = location.getFloat("Longitude", 11);
                    lists.add(new Note(null,null,null,null,null,(double) latitude,(double)longitude));
                }
                List<Note> listsOnePoint = new ArrayList<>();
                listsOnePoint.add(lists.get(0));
                showGoogleLine(listsOnePoint);
            }
        }).start();
    }

    private void hideGoogleMap() {
        gourpsTopmap.setVisibility(View.VISIBLE);
    }


    public void addGoogleMap(){
        gourpsTopmap.setVisibility(View.GONE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                //从数据库中获取的坐标点画,有问题,需要纠偏.暂不采用.
                List<Note> lists = traGreendao.searchLocation(startDate, endDate);
                if (lists.size() == 0) {
                    SharedPreferences location = getSharedPreferences("Location", MODE_PRIVATE);
                    float latitude = location.getFloat("Latitude", 11);
                    float longitude = location.getFloat("Longitude", 11);
                    lists.add(new Note(null,null,null,null,null,(double) latitude,(double)longitude));
                }
                showGoogleLine(lists);
            }
        }).start();
    }

    private void showGoogleLine(List<Note> lists) {
        final com.google.android.gms.maps.model.PolylineOptions polylineOptions = new com.google.android.gms.maps.model.PolylineOptions();
        for (Note list : lists){
            com.google.android.gms.maps.model.LatLng latLng = new com.google.android.gms.maps.model.LatLng(list.getLatitude(), list.getLongitude());
            polylineOptions.add(latLng);
        }
        final com.google.android.gms.maps.model.LatLng latLng = new com.google.android.gms.maps.model.LatLng(lists.get(lists.size() - 1).getLatitude(), lists.get(lists.size() - 1).getLongitude());

        polylineOptions.color(Color.RED);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Observable.just("Hello","world")
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
                                mGoogleMap.addMarker(new com.google.android.gms.maps.model.MarkerOptions().title("dyh08").position(latLng));
                                mGoogleMap.addPolyline(polylineOptions);
                            }
                        });
            }
        },2000);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap ;
        Location location = LocationUtils.getLocation(GroupsShareActivity.this);
        locationUpdates(location);
        isGoogle();
    }

    public void locationUpdates(Location location) {
        Location myLocation = location;
        com.google.android.gms.maps.model.LatLng mapCenter = null;
        if (myLocation == null) {
            float latitude = locationsp.getFloat("Latitude", 0);
            float longitude = locationsp.getFloat("Longitude", 0);
            mapCenter = new com.google.android.gms.maps.model.LatLng(latitude, longitude);
        } else {
            mapCenter = new com.google.android.gms.maps.model.LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        }
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapCenter, 13));
    }



    /**
     * 查询历史轨迹
     */
    private void queryHistoryTrack(int processed, String processOption) {

        // entity标识
        String entityName = WorkoutActivity.entityName;
        // 是否返回精简的结果（0 : 否，1 : 是）
        int simpleReturn = 0;
        // 是否返回纠偏后轨迹（0 : 否，1 : 是）
        int isProcessed = processed;
//        int startTime = startDate.getDate();
//        int endTime = endDate.getDate();
//        long startTime = startDate.;

        int startTime = (int) (startDate.getTime() / 1000);
        int endTime = (int) (endDate.getTime() / 1000);
        // 开始时间
        if (startTime == 0) {
            startTime = (int) (System.currentTimeMillis() / 1000 - 12 * 60 * 60);
        }
        if (endTime == 0) {
            endTime = (int) (System.currentTimeMillis() / 1000);
        }
        // 分页大小
        int pageSize = 1000;
        // 分页索引
        int pageIndex = 1;
        LBSTraceClient client = new LBSTraceClient(GroupsShareActivity.this);
        client.setLocationMode(LocationMode.High_Accuracy);
        client.queryHistoryTrack(123056, "myTrace", simpleReturn, isProcessed, processOption, startTime, endTime,
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
    private void showHistoryTrack(String historyTrack) {

        HistoryTrackData historyTrackData = GsonService.parseJson(historyTrack,
                HistoryTrackData.class);

        List<LatLng> latLngList = new ArrayList<LatLng>();
        if (historyTrackData != null && historyTrackData.getStatus() == 0) {
            if (historyTrackData.getListPoints() != null) {
                latLngList.addAll(historyTrackData.getListPoints());

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
        baiduMap.clear();

        if (points.size() == 1) {
            points.add(points.get(0));
        }

        if (points == null || points.size() == 0) {
//            TrackApplication.showMessage("当前查询无轨迹点");
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
            /**设置精度*/
            baiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(16));


            MyLog.e("当前轨迹里程为 : " + (int) distance + "米");
//            TrackApplication.showMessage("当前轨迹里程为 : " + (int) distance + "米");

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


    private void initTrace() {
        MyLog.e(TAG, "initrace执行了");

        new Thread(new Runnable() {
            @Override
            public void run() {
                //从数据库中获取的坐标点画,有问题,需要纠偏.暂不采用.
                List<Note> lists = traGreendao.searchLocation(startDate, endDate);
                if (lists.size() == 0) {
                    SharedPreferences location = getSharedPreferences("Location", MODE_PRIVATE);
                    float latitude = location.getFloat("Latitude", 11);
                    float longitude = location.getFloat("Longitude", 11);
                    showRealtimeTrack(latitude, longitude);
                } else {
//                    for (int i = 0; i < lists.size(); i++) {
//            Date runDate = lists.get(i).getRunDate();
//            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//            String format1 = simpleDateFormat1.format(runDate);
                    showHistoryTrack(lists.get(0).getDate());
//                    }
                }

            }
        }).start();
    }

    @OnClick(R.id.earthImageview)
    void setLocation(View view) {
        MyLog.e("方法执行了", "执行了");
        mlocationClient = new LocationClient(GroupsShareActivity.this.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mlocationClient.registerLocationListener(mMyLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
        option.setCoorType("gcj02");// 返回的定位结果是百度经纬度，默认值gcj02 bd09ll bd09
        option.setScanSpan(1000);// 设置发起定位请求的间隔时间为1ms
        option.setIsNeedAddress(true); // 返回地址
        mlocationClient.setLocOption(option);
        mlocationClient.start();


    }


    /**
     * 保存百度mapview的图片
     *
     * @param mBaiduMap
     */
    public void getScreenHot(BaiduMap mBaiduMap) {
        // 截图，在SnapshotReadyCallback中保存图片到 sd 卡


        mBaiduMap.snapshot(new BaiduMap.SnapshotReadyCallback() {
            public void onSnapshotReady(Bitmap snapshot) {
                File file = new File(filePathCache);
                FileOutputStream out;
                try {
                    out = new FileOutputStream(file);
                    if (snapshot.compress(
                            Bitmap.CompressFormat.PNG, 20, out)) {
                        out.flush();
                        out.close();
                    }
//                    Toast.makeText(GroupsShareActivity.this,
//                            "屏幕截图成功，图片存在: " + file.toString(),
//                            Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
//        Toast.makeText(GroupsShareActivity.this, "正在截取屏幕图片...",
//                Toast.LENGTH_SHORT).show();
    }



    private class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            String city = location.getCity();
            shareWhereText.setText(city);
            MyLog.e("city", city);
        }
    }


    private void initPopupWindow() {
        final View popupView = LayoutInflater.from(GroupsShareActivity.this).inflate(R.layout.tw_share_popuwindow, null);
        final PopupWindow popupWindow = new PopupWindow(popupView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setContentView(popupView);
        fb = (ImageView) popupView.findViewById(R.id.fb);
        wx = (ImageView) popupView.findViewById(R.id.wx);
        qq = (ImageView) popupView.findViewById(R.id.qq);
        linkin = (ImageView) popupView.findViewById(R.id.Linkin);
        instagram = (ImageView) popupView.findViewById(R.id.instagram);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0xffffffff));
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
//                ToolKits.getScreenHot(GroupsShareActivity.this.getWindow().getDecorView(), filePathCache);
//               截图分两步
                /**把下面的部分截图*/
                ToolKits.saveFile(ToolKits.getViewBitmap(screenhot), filePathCacheUnder);
                getScreenHot(baiduMap);
                getScreenGoogle(mGoogleMap);
                if (BitmapFactory.decodeFile(filePathCache) != null) {
                    final Bitmap bitmap = ToolKits.mergeBitmap_TB(BitmapFactory.decodeFile(filePathCache),
                            BitmapFactory.decodeFile(filePathCacheUnder), true);
                    ToolKits.saveBitmap2file(bitmap, filePathCacheTotal);
                }
            }
        });
        share();

    }


    private void getScreenGoogle(GoogleMap mGoogleMap) {
        if (mGoogleMap!=null) {
            mGoogleMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
                @Override
                public void onSnapshotReady(Bitmap bitmap) {
                    File file = new File(filePathCache);
                    FileOutputStream out;
                    try {
                        out = new FileOutputStream(file);
                        if (bitmap.compress(
                                Bitmap.CompressFormat.PNG, 20, out)) {
                            out.flush();
                            out.close();
                        }
//                    Toast.makeText(GroupsShareActivity.this,
//                            "屏幕截图成功，图片存在: " + file.toString(),
//                            Toast.LENGTH_SHORT).show();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }


    private boolean saveBitmap2file(Bitmap bmp, String filename) {
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        int quality = 100;
        OutputStream stream = null;
        try {
            stream = new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return bmp.compress(format, quality, stream);
    }

    private void share() {
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShare(GroupsShareActivity.this, "Facebook", false);

            }
        });
        wx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShare(GroupsShareActivity.this, "Wechat", false);
            }
        });

        qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShare(GroupsShareActivity.this, "QQ", false);
            }
        });
        linkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinkedIn.ShareParams shareParams = new LinkedIn.ShareParams();
                shareParams.setFilePath(filePathCache);
                shareParams.setImagePath(filePathCache);
                Platform platform = ShareSDK.getPlatform(LinkedIn.NAME);
                platform.share(shareParams);
                boolean b = platform.hasShareCallback();
                showToast(b);
            }
        });
        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShare(GroupsShareActivity.this, "Instagram", false);
            }
        });
    }

    private void showToast(boolean b) {
        if (b) {
            ToastUtil.showMyToast(GroupsShareActivity.this, "success");
        } else {
            ToastUtil.showMyToast(GroupsShareActivity.this, "failed");
        }

    }


    /**
     * 演示调用ShareSDK执行分享
     *
     * @param context
     * @param platformToShare 指定直接分享平台名称（一旦设置了平台名称，则九宫格将不会显示）
     * @param showContentEdit 是否显示编辑页
     */
    public void showShare(Context context, String platformToShare, boolean showContentEdit) {
        OnekeyShare oks = new OnekeyShare();
        oks.setSilent(!showContentEdit);
        if (platformToShare != null) {
            oks.setPlatform(platformToShare);
        }
        //ShareSDK快捷分享提供两个界面第一个是九宫格 CLASSIC  第二个是SKYBLUE
        oks.setTheme(OnekeyShareTheme.CLASSIC);
        // 令编辑页面显示为Dialog模式
//        oks.setDialogMode();
        // 在自动授权时可以禁用SSO方式
        oks.disableSSOWhenAuthorize();
        //oks.setAddress("12345678901"); //分享短信的号码和邮件的地址
//        oks.setTitle("ShareSDK--Title");
//        oks.setTitleUrl("http://mob.com");
//        oks.setText("ShareSDK--文本");
        //oks.setImagePath("/sdcard/test-pic.jpg");  //分享sdcard目录下的图片
//        oks.setImageUrl("http://99touxiang.com/public/upload/nvsheng/125/27-011820_433.jpg");
        if (BitmapFactory.decodeFile(filePathCache) == null) {
            oks.setFilePath(filePathCacheUnder);
            oks.setImagePath(filePathCacheUnder);
        } else {
            oks.setFilePath(filePathCacheTotal);
            oks.setImagePath(filePathCacheTotal);
        }

        oks.setUrl("http://www.mob.com"); //微信不绕过审核分享链接
        //oks.setFilePath("/sdcard/test-pic.jpg");  //filePath是待分享应用程序的本地路劲，仅在微信（易信）好友和Dropbox中使用，否则可以不提供
//        oks.setComment("分享"); //我对这条分享的评论，仅在人人网和QQ空间使用，否则可以不提供
//        oks.setSite("ShareSDK");  //QZone分享完之后返回应用时提示框上显示的名称
//        oks.setSiteUrl("http://mob.com");//QZone分享参数
//        oks.setVenueName("ShareSDK");
//        oks.setVenueDescription("This is a beautiful place!");
        // 将快捷分享的操作结果将通过OneKeyShareCallback回调
        //oks.setCallback(new OneKeyShareCallback());
        // 去自定义不同平台的字段内容
        //oks.setShareContentCustomizeCallback(new ShareContentCustomizeDemo());
        // 在九宫格设置自定义的图标
        Bitmap logo = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
        String label = "ShareSDK";
        View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(View v) {

            }
        };
        oks.setCustomerLogo(logo, label, listener);

        // 为EditPage设置一个背景的View
        //oks.setEditPageBackground(getPage());
        // 隐藏九宫格中的新浪微博
        // oks.addHiddenPlatform(SinaWeibo.NAME);

        // String[] AVATARS = {
        // 		"http://99touxiang.com/public/upload/nvsheng/125/27-011820_433.jpg",
        // 		"http://img1.2345.com/duoteimg/qqTxImg/2012/04/09/13339485237265.jpg",
        // 		"http://diy.qqjay.com/u/files/2012/0523/f466c38e1c6c99ee2d6cd7746207a97a.jpg",
        // 		"http://diy.qqjay.com/u2/2013/0422/fadc08459b1ef5fc1ea6b5b8d22e44b4.jpg",
        // 		"http://img1.2345.com/duoteimg/qqTxImg/2012/04/09/13339510584349.jpg",
        // 		"http://diy.qqjay.com/u2/2013/0401/4355c29b30d295b26da6f242a65bcaad.jpg" };
        // oks.setImageArray(AVATARS);              //腾讯微博和twitter用此方法分享多张图片，其他平台不可以

        // 启动分享
        oks.show(context);
    }


    private String getMiddleTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd a.HH:MM", Locale.US);
        String middleTime = simpleDateFormat.format(startDate);
        return middleTime;
    }

    private int getStep() {
        if (sportRecordArrayList.size() == 0) {
            step = 0;
        } else {
            for (SportRecord sportRecordArray : sportRecordArrayList) {
                MyLog.e(TAG, "step" + sportRecordArray.getStep());
                if (sportRecordArray.getState().equals("1")||sportRecordArray.getState().equals("2")||sportRecordArray.getState().equals("3"))
                step = Integer.parseInt(sportRecordArray.getStep()) + step;
            }
        }
        return step;
    }

    private int getCalories() {
        if (sportRecordArrayList.size() == 0) {
            calories = 0;
        } else {
            for (SportRecord sportRecordArray : sportRecordArrayList) {
                if (sportRecordArray.getState().equals("1") || sportRecordArray.getState().equals("2") || sportRecordArray.getState().equals("3")) {
                    calories = ToolKits.calculateCalories(Float.parseFloat(sportRecordArray.getDistance()),
                            Integer.parseInt(sportRecordArray.getDuration()), user_weight) + calories;
                }
            }
        }
        return calories;
    }

    public String getDurtion(int position) {
        long endTime = endDate.getTime();
        long startTime = startDate.getTime();
//        时间间隔转UTC
        itemDuration = Math.abs(endTime - startTime);
        MyLog.e(TAG, itemDuration + "itemDuration");
        Date date = new Date(itemDuration);
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        instance.add(Calendar.MINUTE, -TimeZoneHelper.getTimeZoneOffsetMinute());
        Date time = instance.getTime();
        duration = simpleDateFormat.format(time);
        return duration;
    }


    private String getDistanceKM() {

        sportRecordArrayList = UserDeviceRecord.findHistoryChartwithHMS
                (GroupsShareActivity.this, String.valueOf(user_id), startDate, endDate);
//        MyLog.e(TAG,"sportRecordArrayList"+sportRecordArrayList.get(0).toString());
        if (sportRecordArrayList.size() == 0) {
            distance = 0;
        } else {
            for (SportRecord sportRecordArray : sportRecordArrayList) {
                MyLog.e(TAG, "distance" + sportRecordArray.getDistance());
                if (sportRecordArray.getState().equals("1")||sportRecordArray.getState().equals("2")||sportRecordArray.getState().equals("3"))
                distance = Integer.parseInt(sportRecordArray.getDistance()) + distance;
            }
        }
//        这一步吧distance转换成如5.3 的用来加km
        MyLog.e("distance", distance + "");
        double distanceKM = CommonUtils.getDoubleValue(distance / 1000, 1);
        return new Formatter().format("%.1f", distanceKM).toString();
    }


    protected void showRealtimeTrack(double latitude, double longitude) {
        MyLog.e(TAG, latitude + "==========" + longitude);
        if (Math.abs(latitude - 0.0) < 0.000001 && Math.abs(longitude - 0.0) < 0.000001) {
            Toast.makeText(GroupsShareActivity.this, "当前查询无轨迹点", Toast.LENGTH_SHORT).show();
        } else {
            LatLng latLng = new LatLng(latitude, longitude);
            LatLng sourceLatLng = latLng;
            CoordinateConverter converter = new
                    CoordinateConverter();
            converter.from(CoordinateConverter.CoordType.GPS);
            converter.coord(sourceLatLng);
            latLng = converter.convert();
            pointList.add(latLng);
            // 绘制实时点
            drawRealtimePoint(latLng);
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
            baiduMap.setMapStatus(msUpdate);
        }

        // 路线覆盖物
        if (null != polyline) {
            baiduMap.addOverlay(polyline);
        }

        // 实时点覆盖物
        if (null != overlayOptions) {
            overlay = baiduMap.addOverlay(overlayOptions);
        }

    }


    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
//关闭sso授权
        oks.disableSSOWhenAuthorize();

// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
        oks.setTitle("标题");
// titleUrl是标题的网络链接，QQ和QQ空间等使用
        oks.setTitleUrl("http://sharesdk.cn");
// text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
// url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
// comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
// site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
// siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

// 启动分享GUI
        oks.show(this);
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
