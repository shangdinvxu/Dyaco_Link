package com.linkloving.dyh08.logic.UI.workout;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.linkloving.dyh08.R;
import com.linkloving.dyh08.basic.toolbar.ToolBarActivity;
import com.linkloving.dyh08.logic.UI.workout.Greendao.TraceGreendao;
import com.linkloving.dyh08.logic.UI.workout.trackshow.TrackUploadFragment;
import com.linkloving.dyh08.logic.UI.workout.trackshow.WorkoutActivity;
import com.linkloving.dyh08.utils.GpsUtils;
import com.linkloving.dyh08.utils.logUtils.MyLog;
import com.linkloving.dyh08.utils.sportUtils.LocationUtils;
import com.linkloving.utils.TimeZoneHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import Trace.GreenDao.DaoMaster;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Daniel.Xu on 2016/12/27.
 */

public class GooglemapActivity extends ToolBarActivity implements OnMapReadyCallback, LocationListener {

    private static  final  String TAG = GooglemapActivity.class.getSimpleName();
    @InjectView(R.id.start)
    RadioButton start;
    @InjectView(R.id.stop)
    RadioButton stop;
    @InjectView(R.id.OK_btn)
    Button OKBtn;
    @InjectView(R.id.first_middle)
    LinearLayout firstMiddle;
    @InjectView(R.id.second_middle)
    LinearLayout secondMiddle;
    @InjectView(R.id.chronometer)
    TextView chronometer;
    private String filePathCache = "/sdcard/ranking_v777.png";
    private LocationRequest mLocationRequest;
    private GoogleApiClient googleApiClient;
    private GoogleMap mgoogleMap;
    private int stopType = 1;
    private int clickType = 0;
    private boolean isClickStart = false;
    private Handler handler;
    private long startTimeLong;
    private Runnable update_thread;


    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat simMonth = new SimpleDateFormat("yyyy-MM");

    private Date dateStartTrace;
    private String formatMonth;
    private String formatStartTime;
    private Date dateStopTrace;
    private String formatEndTime;

    private SQLiteDatabase db;
    private DaoMaster.DevOpenHelper devOpenHelper;
    private TraceGreendao traGreendao;
    private SharedPreferences sharedPreferences;
    private SharedPreferences locationsp;
    private SharedPreferences.Editor edit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.googlemap);
        ButterKnife.inject(this);
//        SupportMapFragment mapFragment =
//                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
        addGoogleMapFragment();
        createLocationRequest();
        googleApiClient = new GoogleApiClient.Builder(GooglemapActivity.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        MyLog.e(TAG,"google 连接+onConnected");
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        MyLog.e(TAG,"google 连接+onConnectionSuspended");
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(GooglemapActivity.this,"连接到Google Client 失败",Toast.LENGTH_SHORT).show();
                    }
                }).build();
        googleApiClient.connect();


        chronometer = (TextView) findViewById(R.id.chronometer);
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

        devOpenHelper = new DaoMaster.DevOpenHelper(GooglemapActivity.this, "Note", null);
        db = devOpenHelper.getReadableDatabase();
        traGreendao = new TraceGreendao(GooglemapActivity.this, db);
        getRuntime();
        SharedPreferences sharedPreferences =getSharedPreferences("clickType", Context.MODE_PRIVATE);
        clickType = sharedPreferences.getInt("clickType", 0);

        SharedPreferences sharedPreferencesbegin = getSharedPreferences("clickType", Context.MODE_PRIVATE);
        isClickStart = sharedPreferencesbegin.getBoolean("isClickStart", false);
        startTimeLong = sharedPreferencesbegin.getLong("startTimeLong", 0);
        if (isClickStart) {
            secondMiddle.setVisibility(View.VISIBLE);
        }
        locationsp = getSharedPreferences("Location", MODE_PRIVATE);
        edit = locationsp.edit();
    }


    private void addGoogleMapFragment(){
        SupportMapFragment supportMapFragment = new SupportMapFragment();
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.google_map_framelayout,supportMapFragment);
        fragmentTransaction.commit();
        supportMapFragment.getMapAsync(this);
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
    public void onResume() {
        super.onResume();
        if (isClickStart && GpsUtils.isOPen(GooglemapActivity.this)) {
            firstMiddle.setVisibility(View.GONE);
            secondMiddle.setVisibility(View.VISIBLE);
            getRuntime();
        }
        if (GpsUtils.isOPen(GooglemapActivity.this)) {
            firstMiddle.setVisibility(View.GONE);
        }

    }


    /**
     * 初始化
     */
    private void init() {

        start.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                MyLog.e(TAG, "clicktype=====" + clickType);
                if (clickType == 0) {
                    //先判断Gps有没有开,要是GPs没有开则在用户点确定后开启
                    if (!GpsUtils.isOPen(GooglemapActivity.this)) {
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
                        formatStartTime = simpleDateFormat.format(dateStartTrace);
                        sharedPreferences = getSharedPreferences("clickType", Context.MODE_PRIVATE);
                        SharedPreferences.Editor edit = sharedPreferences.edit();
                        edit.putString("formatMonth", formatMonth);
                        edit.putString("formatStartTime", formatStartTime);
                        edit.putLong("startTimeLong", startTimeLong);
                        edit.commit();
                        clickType = 1;
                        startTrace();
                    }
                } else {
                    Toast.makeText(GooglemapActivity.this, "轨迹服务已开启,请关闭轨迹服务", Toast.LENGTH_LONG).show();
                }
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {

            private long startTimeLong;

            public void onClick(View v) {
                MyLog.e(TAG, "clicktype=====" + clickType);
                // TODO Auto-generated method stub
                if (clickType == 1) {
                    isClickStart = false;
//                    Toast.makeText(getActivity(), "停止轨迹服务", Toast.LENGTH_SHORT).show();
//                    把开始时间取出来,记录下来
                    SharedPreferences sharedPreferences = getSharedPreferences("clickType", Context.MODE_PRIVATE);
                    String formatStartTime = sharedPreferences.getString("formatStartTime", "");
                    String formatMonth = sharedPreferences.getString("formatMonth", "");
                    startTimeLong = sharedPreferences.getLong("startTimeLong", 0);
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(startTimeLong);
                    Date dateStartTrace = c.getTime();
                    traGreendao.addStartTime(formatStartTime, dateStartTrace, 2);
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
                    stopType = 2 ;
                    secondMiddle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (stopType == 2) {
                                secondMiddle.setVisibility(View.GONE);
                            }
                        }
                    });
                    clickType = 0;
                    stopTrace();

                } else {
                    Toast.makeText(GooglemapActivity.this, getString(R.string.pleaseturnon), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void stopTrace() {
        stopLocationUpdates();
    }

    private void startTrace() {
        requestLocationUpdates();
    }


    public void getRuntime() {
        //延时1s后又将线程加入到线程队列中
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


    @Override
    public void onDestroy() {
        SharedPreferences sharedPreferences = getSharedPreferences("clickType", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt("clickType", clickType);
        edit.putBoolean("isClickStart", isClickStart);
        edit.commit();
        super.onDestroy();
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


    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient, this);
    }

    public void requestLocationUpdates(Location location) {

        Location myLocation = location ;
        LatLng mapCenter = null ;
        if(myLocation==null) {
            float latitude = locationsp.getFloat("Latitude", 0);
            float longitude = locationsp.getFloat("Longitude", 0);
            mapCenter = new LatLng(latitude,longitude);
        }else {
            mapCenter = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        }
        mgoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapCenter, 13));

        // Flat markers will rotate when the map is rotated,
        // and change perspective when the map is tilted.
        mgoogleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_geosmall))
                .position(mapCenter)
                .flat(true)
                .rotation(245));

        CameraPosition cameraPosition = CameraPosition.builder()
                .target(mapCenter)
                .zoom(13)
                .bearing(90)
                .build();

        // Animate the change in camera view over 2 seconds
        mgoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                2000, null);
    }


    public void requestLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
        Location myLocation = mgoogleMap.getMyLocation();
        if (myLocation==null)return;
        LatLng mapCenter = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

        mgoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapCenter, 13));

        // Flat markers will rotate when the map is rotated,
        // and change perspective when the map is tilted.
        mgoogleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_geosmall))
                .position(mapCenter)
                .flat(true)
                .rotation(245));

        CameraPosition cameraPosition = CameraPosition.builder()
                .target(mapCenter)
                .zoom(13)
                .bearing(90)
                .build();

        // Animate the change in camera view over 2 seconds
        mgoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                2000, null);
    }

    @Override
    public void onLocationChanged(Location location) {
//        Toast.makeText(GooglemapActivity.this, location.getLatitude() + "___________" + location.getLongitude()+"------"+location.getTime(), Toast.LENGTH_SHORT).show();
        MyLog.e("googleLocation", location.getLatitude() + "___________" + location.getLongitude());


        edit.putFloat("Latitude", (float) location.getLatitude());
        edit.putFloat("Longitude", (float) location.getLongitude());
        edit.commit();
        mgoogleMap.addMarker(new MarkerOptions().position(new LatLng(location.getLongitude(), location.getLatitude())).title("Marker"));
        mgoogleMap.addPolyline(new PolylineOptions().add(new LatLng(location.getLatitude(), location.getLongitude())));
        traGreendao.addNote(null,new Date(location.getTime()),new Date(location.getTime()),location.getLatitude(),location.getLongitude());
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mgoogleMap = googleMap;
        Location location = LocationUtils.getLocation(GooglemapActivity.this);
        requestLocationUpdates(location);
        init();
        googleMap.setMyLocationEnabled(true);
    }
}

