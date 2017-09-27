package com.linkloving.dyh08.logic.UI.main;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.android.bluetoothlegatt.BLEHandler;
import com.example.android.bluetoothlegatt.BLEProvider;
import com.example.android.bluetoothlegatt.proltrol.GetHeartEvent;
import com.example.android.bluetoothlegatt.proltrol.dto.LPDeviceInfo;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jaeger.library.StatusBarUtil;
import com.linkloving.band.dto.DaySynopic;
import com.linkloving.band.dto.SportRecord;
import com.linkloving.dyh08.BleService;
import com.linkloving.dyh08.CommParams;
import com.linkloving.dyh08.IntentFactory;
import com.linkloving.dyh08.MyApplication;
import com.linkloving.dyh08.R;
import com.linkloving.dyh08.RetrofitUtils.Bean.CheckFirmwareVersionReponse;
import com.linkloving.dyh08.RetrofitUtils.FirmwareRetrofitClient;
import com.linkloving.dyh08.RetrofitUtils.RetrofitApi.OADApi;
import com.linkloving.dyh08.basic.AppManager;
import com.linkloving.dyh08.db.sport.UserDeviceRecord;
import com.linkloving.dyh08.db.summary.DaySynopicTable;
import com.linkloving.dyh08.http.basic.CallServer;
import com.linkloving.dyh08.http.basic.HttpCallback;
import com.linkloving.dyh08.http.basic.NoHttpRuquestFactory;
import com.linkloving.dyh08.http.data.DataFromServer;
import com.linkloving.dyh08.logic.UI.OAD.DfuService;
import com.linkloving.dyh08.logic.UI.device.DeviceActivity;
import com.linkloving.dyh08.logic.UI.device.FirmwareDTO;
import com.linkloving.dyh08.logic.UI.main.boundwatch.BoundActivity;
import com.linkloving.dyh08.logic.UI.main.materialmenu.Left_viewVO1;
import com.linkloving.dyh08.logic.UI.main.materialmenu.MenuNewAdapter;
import com.linkloving.dyh08.logic.UI.main.materialmenu.MenuVO;
import com.linkloving.dyh08.logic.UI.more.MoreActivity;
import com.linkloving.dyh08.logic.dto.SportRecordUploadDTO;
import com.linkloving.dyh08.logic.dto.UserEntity;
import com.linkloving.dyh08.prefrences.PreferencesToolkits;
import com.linkloving.dyh08.prefrences.devicebean.LocalInfoVO;
import com.linkloving.dyh08.utils.AvatarHelper;
import com.linkloving.dyh08.utils.CommonUtils;
import com.linkloving.dyh08.utils.DateSwitcher;
import com.linkloving.dyh08.utils.IsBackgroundUtils;
import com.linkloving.dyh08.utils.ScreenUtils;
import com.linkloving.dyh08.utils.ToolKits;
import com.linkloving.dyh08.utils.TypefaceUtils;
import com.linkloving.dyh08.utils.logUtils.MyLog;
import com.linkloving.dyh08.utils.manager.AsyncTaskManger;
import com.linkloving.dyh08.utils.sportUtils.CaloriesUtils;
import com.linkloving.dyh08.utils.sportUtils.SportDataHelper;
import com.linkloving.utils.TimeZoneHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yolanda.nohttp.Response;
import com.zhy.autolayout.AutoLayoutActivity;

import net.hockeyapp.android.CrashManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import no.nordicsemi.android.dfu.DfuProgressListenerAdapter;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class PortalActivity extends AutoLayoutActivity implements View.OnClickListener {
    @InjectView(R.id.user_linerLayout)
    LinearLayout userLinerLayout;
    private SimpleDateFormat sdf = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD);
    private static final String TAG = PortalActivity.class.getSimpleName();
    private static final int REQUSET_FOR_PERSONAL = 1;
    private static final int LOW_BATTERY = 3;
    private static final int JUMP_FRIEND_TAG_TWO = 2;
    ViewGroup contentLayout;
    @InjectView(R.id.drawer_layout)
    DrawerLayout drawer;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.recycler_view)
    RecyclerView menu_RecyclerView;
    @InjectView(R.id.user_head)
    ImageView user_head;//头像
    @InjectView(R.id.user_name)
    TextView user_name;//昵称
    /*---------------------------------------------------------*/
    @InjectView(R.id.Rl_step)
    RelativeLayout stepLayout;
    @InjectView(R.id.Rl_calories)
    RelativeLayout caloriesLayout;
    @InjectView(R.id.Rl_sleep)
    RelativeLayout sleepLayout;
    @InjectView(R.id.Rl_distance)
    RelativeLayout distanceLayout;
    @InjectView(R.id.main_tv_hour)
    AppCompatTextView main_tv_hour;
    @InjectView(R.id.main_tv_day)
    AppCompatTextView main_tv_day;

    @InjectView(R.id.main_tv_Step)
    AppCompatTextView stepView;
    @InjectView(R.id.main_tv_Calories)
    TextView calView;
    @InjectView(R.id.main_tv_Distance)
    AppCompatTextView distanceView;
    @InjectView(R.id.main_tv_Sleep)
    AppCompatTextView sleepView;
    @InjectView(R.id.distanceUnit)
    AppCompatTextView  distanceUnit ;

    // 修改头像的临时文件存放路径（头像修改成功后，会自动删除之）
    private String __tempImageFileLocation = null;

    /*--------------------------------------------------------*/
    /**
     * 侧滑栏适配器
     */
    private MenuNewAdapter menuAdapter;
    /**
     * 计算运动数据的进度条
     */
    private ProgressDialog progressDialog;
    /**
     * 地理位置
     */
    private LocationClient mLocationClient;
    private MyLocationListener mMyLocationListener;

    private String User_avatar_file_name;
    /**
     * 用户封装类
     */
    private UserEntity userEntity;
    /**
     * 蓝牙以及蓝牙回调事件
     */
    private BLEProvider provider;
    private BLEHandler.BLEProviderObserverAdapter bleProviderObserver;

    private Handler timeHandler = new Handler();

    private FragmentTransaction transaction;

    /**
     * 当前正在运行中的数据加载异步线程(放全局的目的是尽量控制当前只有一个在运行，防止用户恶意切换导致OOM)
     */
    private AsyncTask<Object, Object, DaySynopic> currentDataAsync = null;
    private PullToRefreshListView pulltorefreshView;
    private int localSettingUnitInfo;
    private AlertDialog.Builder builder;

    //判断是否从后台进入到前台的flag
    private boolean flag = false;
    private boolean needTocheck = false ;
    private ProgressBar progressbar;
    private TextView progressInt;
    private AlertDialog downloadingProgressDialog;

    public static final int sendcount_time = 2000;



    Runnable boundRunnable = new Runnable() {
        @Override
        public void run() {
            Message msg = new Message();
            msg.what = 0x333;
            boundhandler.sendMessage(msg);
        }
    };

    Handler boundhandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x333:
                    provider.requestbound_recy(PortalActivity.this);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tv_activity_portal_main);
        View heardView = LayoutInflater.from(PortalActivity.this).inflate(R.layout.tw_activity_main_lunch, null);
        pulltorefreshView = (PullToRefreshListView) findViewById(R.id.pullTorefreshView);
        ListView refreshableView = pulltorefreshView.getRefreshableView();
        refreshableView.addHeaderView(heardView);
        needTocheck = true ;
        checkBle();
        AppManager.getAppManager().addActivity(this);
        ButterKnife.inject(this);
        contentLayout = (ViewGroup) findViewById(R.id.main);
        //获取本地用户类
        userEntity = MyApplication.getInstance(this).getLocalUserInfoProvider();

        //绑定蓝牙事件START
        provider = BleService.getInstance(this).getCurrentHandlerProvider();
        bleProviderObserver = new BLEProviderObserverAdapterImpl();
        provider.setBleProviderObserver(bleProviderObserver);
        //绑定蓝牙事件OVER

        // 系统到本界面中，应该已经完成准备好了，开启在网络连上事件时自动启动同步线程的开关吧
        MyApplication.getInstance(this).setPreparedForOfflineSyncToServer(true);
        //初始化百度地图
        initLocation();
        //初始化UI
        initView();
        initListener();
        //开始定位
        mLocationClient.start();
        builder = new AlertDialog.Builder(PortalActivity.this);

        EventBus.getDefault().register(this);

        /*--------------------------------*/
        pulltorefreshView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                String s = MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider().getDeviceEntity().getLast_sync_device_id();
                getCalroiesNow();
                if (CommonUtils.isStringEmpty(s)) {
                    //
//                    showBundDialog();
                    pulltorefreshView.onRefreshComplete();
                } else {
                    // 启动超时处理handler
                    // 进入扫描和连接处理过程
                    provider.setCurrentDeviceMac(s);
                    //开始同步
                    BleService.getInstance(PortalActivity.this).syncAllDeviceInfoAuto(PortalActivity.this, false, null);
                    mScrollViewRefreshingHandler.post(mScrollViewRefreshingRunnable);
                }
            }

        });
        pulltorefreshView.autoRefreshListener();

        /*-------------------日历----------------*/
        initCheckUnit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        DfuServiceListenerHelper.registerProgressListener(this, mDfuProgressListener);
        initCheckUnit();
        if (getTempImageFileUri()!=null){
            Bitmap bitmap = decodeUriAsBitmap(getTempImageFileUri());
            if (bitmap==null){
                bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.default_avatar_m);
            }
            user_head.setImageBitmap(bitmap);
        }
        checkForCrashes();
        if (flag) {
            flag = false;
            pulltorefreshView.setRefreshing();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (provider.getBleProviderObserver() != null) {
            provider.setBleProviderObserver(null);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        boolean background = IsBackgroundUtils.isBackground(PortalActivity.this);
        Log.e("BaseActivity", "===onStop");
        Log.e("BaseActivity", "background:" + background);
        if (background) {
            flag = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//反注册EventBus
        provider.setBleProviderObserver(null);
        AppManager.getAppManager().removeActivity(this);
        // 如果有未执行完成的AsyncTask则强制退出之，否则线程执行时会空指针异常哦！！！
        AsyncTaskManger.getAsyncTaskManger().finishAllAsyncTask();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(HeartrateFinishEvent event){
        if (pulltorefreshView != null && pulltorefreshView.isRefreshing()) {
            pulltorefreshView.onRefreshComplete();
        }
    }

    @Subscribe (threadMode = ThreadMode.MAIN)
    public void onGetHeaert(GetHeartEvent event){
        if (pulltorefreshView!=null) {
            int total = event.getTotal();
            int left = event.getLeft();
            pulltorefreshView.getHeaderLayout().getmHeaderText().setText("获取心率中");
        }

    }

    private void checkForCrashes() {
        CrashManager.register(this);
    }


    @Override
    protected void onPostResume() {
        MyLog.e(TAG, "onPostResume()了");
        super.onPostResume();

        provider = BleService.getInstance(PortalActivity.this).getCurrentHandlerProvider();
        provider.setBleProviderObserver(bleProviderObserver);
        UserEntity userEntity = MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider();

//        if (userEntity == null || userEntity.getDeviceEntity() == null || userEntity.getUserBase() == null)
//            return;
        MyLog.e(TAG, "u.getDeviceEntity().getDevice_type():" + userEntity.getDeviceEntity().getDevice_type());
        //去数据库查询所要显示的数据
        getInfoFromDB();
        //绑定了蓝牙就去同步蓝牙数据
        if (!CommonUtils.isStringEmpty(userEntity.getDeviceEntity().getLast_sync_device_id())) {
//            if (provider.isConnectedAndDiscovered())
            BleService.getInstance(PortalActivity.this).syncAllDeviceInfoAuto(PortalActivity.this, false, null);
        }

        if (userEntity.getUserBase().getUser_avatar_file_name() == null) {
            MyLog.e(TAG, "u.getUserBase().getUser_avatar_file_name()是空的........");
            return;
        }

        if (!userEntity.getUserBase().getUser_avatar_file_name().equals(User_avatar_file_name) || !userEntity.getUserBase().getNickname().equals(userEntity.getUserBase().getNickname())) {
            refreshHeadView();
        }
    }


    private void initCheckUnit() {
        localSettingUnitInfo = PreferencesToolkits.getLocalSettingUnitInfo(PortalActivity.this);
        if (localSettingUnitInfo==ToolKits.UNIT_GONG){
            distanceUnit.setText(R.string.KM);
        }else {
            distanceUnit.setText(R.string.KMunit);
        }
    }

    /**
     * 下拉同步ui的超时复位延迟执行handler （防止意外情况下，一直处于“同步中”的状态）
     */
    private Handler mScrollViewRefreshingHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            if (pulltorefreshView.isRefreshing())
//                pulltorefreshView.onRefreshComplete();
            super.handleMessage(msg);
        }
    };
    Runnable mScrollViewRefreshingRunnable = new Runnable() {
        @Override
        public void run() {
            Message ms = new Message();
            mScrollViewRefreshingHandler.sendMessageDelayed(ms, 15000);
        }
    };

    private void checkBle() {
        //判断是否有权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //请求权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            //判断是否需要 向用户解释，为什么要申请该权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                Toast.makeText(this, "shouldShowRequestPermissionRationale", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @OnClick(R.id.title_right_button)
    void setTitleRightButton(View view){
        pulltorefreshView.setRefreshing();
    }

    /*-----------------------------------------------------------------------*/
    @OnClick(R.id.user_linerLayout)
    void setUserLinerLayout(View view) {
        IntentFactory.start2SettingsActivity(PortalActivity.this);
    }

    @OnClick(R.id.Rl_step)
    void stepLayout(View view) {
        IntentFactory.start_step(PortalActivity.this);
    }

    @OnClick(R.id.Rl_calories)
    void caloriesLayout(View view) {
        IntentFactory.start_calories(PortalActivity.this);
    }

    @OnClick(R.id.Rl_distance)
    void distanceLayout(View view) {
        IntentFactory.start_distance(PortalActivity.this);
    }

    @OnClick(R.id.Rl_sleep)
    void sleepLayout(View view) {
        IntentFactory.start_sleep(PortalActivity.this);
    }


    @Override
    public void onClick(View v) {
    }

    private void initLocation() {
        mLocationClient = new LocationClient(PortalActivity.this.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
        option.setCoorType("gcj02");// 返回的定位结果是百度经纬度，默认值gcj02 bd09ll bd09
        option.setScanSpan(1000);// 设置发起定位请求的间隔时间为1ms
        option.setIsNeedAddress(true); // 返回地址
        mLocationClient.setLocOption(option);
    }

    private void initView() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.general_loading));
        toolbar.setBackgroundColor(getResources().getColor(R.color.transparent));
        Rect outRect = new Rect();
        this.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
        setSupportActionBar(toolbar);
        //隐藏title
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        menu_RecyclerView.setLayoutManager(layoutManager);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.END);
        //无阴影
        drawer.setScrimColor(Color.TRANSPARENT);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        contentLayout.setBackgroundDrawable(getResources().getDrawable(R.mipmap.header));
        StatusBarUtil.setTranslucentForDrawerLayout(this, drawer, 0);
        ScreenUtils.setMargins(toolbar, 0, ScreenUtils.getStatusHeight(this), 0, 0);

        //侧边栏适配器
        setAdapter();
        refreshHeadView();
        //更新日历 1s一次
        updateTimeThread();

        if (getTempImageFileUri()!=null){
            Bitmap bitmap = decodeUriAsBitmap(getTempImageFileUri());
            if (bitmap==null){
                bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.default_avatar_m);
            }
            user_head.setImageBitmap(bitmap);
        }
//        getCalroiesNow();
    }

    private void getCalroiesNow(){
        ToolKits toolKits = new ToolKits();
        int calorieseveryday = toolKits.getCalories(PortalActivity.this);
        Date dateToday = new Date();
        SimpleDateFormat hh = new SimpleDateFormat("HH", Locale.getDefault());
        String HH = hh.format(dateToday);
        SimpleDateFormat mm = new SimpleDateFormat("mm", Locale.getDefault());
        String MM = mm.format(dateToday);
        int caloriesNow = calorieseveryday * (Integer.parseInt(HH) * 60 + Integer.parseInt(MM)) / 1440;
        MyLog.e("caloriesNow",caloriesNow+"caloriesNow");
//        calView.setText(caloriesNow + "");

    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        refreshHeadView();
    }

    private Bitmap decodeUriAsBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inPreferredConfig = Bitmap.Config.RGB_565;
            opt.inPurgeable = true;
            opt.inInputShareable = true;
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri),null,opt);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    private Uri getTempImageFileUri() {
        String tempImageFileLocation = getTempImageFileLocation();
        if (tempImageFileLocation != null) {
            return Uri.parse("file://" + tempImageFileLocation);
        }
        return null;
    }

    private String getTempImageFileLocation() {
        try {
            if (__tempImageFileLocation == null) {
                String avatarTempDirStr = AvatarHelper.getUserAvatarSavedDir(PortalActivity.this);
                File avatarTempDir = new File(avatarTempDirStr);
                if (avatarTempDir != null) {
                    // 目录不存在则新建之
                    if (!avatarTempDir.exists())
                        avatarTempDir.mkdirs();
                    // 临时文件名
                    int user_id = MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider().getUser_id();
                    String userIdString = Integer.toString(user_id);
                    __tempImageFileLocation = avatarTempDir.getAbsolutePath() + "/" +"local_avatar_temp_dy.jpg";
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "【ChangeAvatar】读取本地用户的头像临时存储路径时出错了，" + e.getMessage(), e);
        }

        Log.d(TAG, "【ChangeAvatar】正在获取本地用户的头像临时存储路径：" + __tempImageFileLocation);

        return __tempImageFileLocation;
    }

/*--------------------------------Daniel-----------------------------------*/


    /*--------------------------------------*/


    private void initListener() {
        user_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentFactory.start2SettingsActivity(PortalActivity.this);
            }
        });


    }

    /**
     * 实现GPS定位回调监听。
     */
    private class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (PortalActivity.this != null) {
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                String city = location.getCity();
                MyLog.e("city", city);
                UserEntity userEntity = MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider();
                if (userEntity.getUserBase() == null) {
                    return;
                }
                userEntity.getUserBase().setLongitude(longitude + "");
                userEntity.getUserBase().setLatitude(latitude + "");
                mLocationClient.stop();
            } else {
                mLocationClient.stop();
            }
        }
    }

    /**
     * 刷新用户头像和昵称
     */
    private void refreshHeadView() {
        MyLog.i(TAG, "刷新头像和昵称,等数据");
        //图像以后设置
        UserEntity u = MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider();
        if (u == null) {
            MyLog.i(TAG, "获得的UserEntity是空的");
            return;
        }
        MyLog.i(TAG, "获得的UserEntity的名字=" + u.getUserBase().getNickname());
        user_name.setText(u.getUserBase().getNickname());
        User_avatar_file_name = u.getUserBase().getUser_avatar_file_name();
        if (User_avatar_file_name != null) {
            String url = NoHttpRuquestFactory.getUserAvatarDownloadURL(PortalActivity.this, u.getUser_id() + "", u.getUserBase().getUser_avatar_file_name(), true);
            User_avatar_file_name = u.getUserBase().getUser_avatar_file_name();
            DisplayImageOptions options;
            options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                    .cacheOnDisc(true)//设置下载的图片是否缓存在SD卡中
                    .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
                    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
                    .showImageOnFail(R.mipmap.default_avatar)//加载失败显示图片
                    .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
                    .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
                    .build();//构建完成

            ImageLoader.getInstance().displayImage(url, user_head, options, new ImageLoadingListener() {

                @Override
                public void onLoadingStarted(String imageUri, View view) {
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                    ImageView mhead = (ImageView) view;
//                    mhead.setImageBitmap(loadedImage);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
        }

    }


    /**
     * 侧滑栏适配器
     */
    private void setAdapter() {
        List<MenuVO> list = new ArrayList<MenuVO>();
        for (int i = 0; i < Left_viewVO1.menuIcon.length; i++) {
            MenuVO vo = new MenuVO();
            vo.setImgID(Left_viewVO1.menuIcon[i]);
            vo.setTextID(Left_viewVO1.menuText[i]);
            list.add(vo);
        }
        menuAdapter = new MenuNewAdapter(this, list);
        menuAdapter.setOnRecyclerViewListener(new MenuNewAdapter.OnRecyclerViewListener() {
            //侧边栏点击事件 可以在这里复写 暂时没用到
            @Override
            public void onItemClick(int position) {

            }
        });
        menu_RecyclerView.setAdapter(menuAdapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            MoreActivity.ExitApp(this);
        }
    }


    //刷新手表电量  + 获取存在数据库的数据
    private void getInfoFromDB() {
        //子线程去计算汇总数据
        MyLog.e(TAG, "====================开始执行异步任务====================");
        AsyncTask<Object, Object, DaySynopic> dataAsyncTask = new AsyncTask<Object, Object, DaySynopic>() {
            String todyaStr;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
//                if (progressDialog != null && !progressDialog.isShowing())
//                    progressDialog.show();
            }

            @Override
            protected DaySynopic doInBackground(Object... params) {
                DaySynopic mDaySynopic = null;
                todyaStr = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(new Date());
                ArrayList<DaySynopic> mDaySynopicArrayList = new ArrayList<DaySynopic>();
                MyLog.e(TAG, "endDateString:" + todyaStr);
                //今天的话 无条件去汇总查询
                mDaySynopic = SportDataHelper.offlineReadMultiDaySleepDataToServer(PortalActivity.this, todyaStr, todyaStr);
                if (mDaySynopic.getTime_zone() == null) {
                    return null;
                }
                MyLog.e(TAG, "daySynopic:" + mDaySynopic.toString());
                mDaySynopicArrayList.add(mDaySynopic);
                DaySynopicTable.saveToSqliteAsync(PortalActivity.this, mDaySynopicArrayList, userEntity.getUser_id() + "");
                return mDaySynopic;
            }

            @Override
            protected void onPostExecute(DaySynopic mDaySynopic) {
                super.onPostExecute(mDaySynopic);
                TypefaceUtils.setNumberType(PortalActivity.this, stepView);
                TypefaceUtils.setNumberType(PortalActivity.this, distanceView);
                TypefaceUtils.setNumberType(PortalActivity.this, sleepView);
                TypefaceUtils.setNumberType(PortalActivity.this, calView);
                //=============计算基础卡路里=====START========//
//                if (timeNow.equals(sdf.format(new Date()))) {
//                    int hour = Integer.parseInt(new SimpleDateFormat("HH").format(new Date()));
//                    int minute = Integer.parseInt(new SimpleDateFormat("mm").format(new Date()));
//                    cal_base = (int) ((hour * 60 + minute) * 1.15);//当前时间今天的卡路里
//                } else {
//                    cal_base = 1656;
//                }
//                //=============计算基础卡路里=====OVER========//
                if (mDaySynopic == null) {
                    MyLog.e(TAG, "mDaySynopic空的");
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    AsyncTaskManger.getAsyncTaskManger().removeAsyncTask(this);
                    /**基础卡路里*/
                    int caloriesNow = CaloriesUtils.getCaloriesNow(PortalActivity.this);
                    MyApplication.getInstance(PortalActivity.this).setOld_calories(caloriesNow);
                    calView.setText(caloriesNow+ "");
                    stepView.setText(0 + "");
                    MyApplication.getInstance(PortalActivity.this).setOld_step(0);
                    MyApplication.getInstance(PortalActivity.this).setOld_distance(0);
                    distanceView.setText(0 + "");

                    return;
                }
                //daySynopic:[data_date=2016-04-14,data_date2=null,time_zone=480,record_id=null,user_id=null,run_duration=1.0,run_step=68.0,run_distance=98.0
                // ,create_time=null,work_duration=178.0,work_step=6965.0,work_distance=5074.0,sleepMinute=2.0916666984558105,deepSleepMiute=1.25 gotoBedTime=1460645100 getupTime=1460657160]
                //走路 步数
                int walkStep = (int) (CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getWork_step()), 0));
                //跑步 步数

                int runStep = (int) (CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getRun_step()), 0));
                int step = walkStep + runStep;
                /****************今天的步数给到 方便OAD完成后回填步数 的变量里面去****************/
                MyApplication.getInstance(PortalActivity.this).setOld_step(step);
                stepView.setText(step + "");

                //走路 里程
                int walkDistance = (int) (CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getWork_distance()), 0));
                //跑步 里程
                int runDistance = (int) (CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getRun_distance()), 0));
                int distance = walkDistance + runDistance;
                MyApplication.getInstance(PortalActivity.this).setOld_distance(distance);
                float distanceKm = (float) distance / 1000;
                String distancekm = "" ;
                if (distance<1000){
                    distancekm = DateSwitcher.twoFloat(distanceKm);
                }else {
                     distancekm = DateSwitcher.oneFloat(distanceKm);
                }
                localSettingUnitInfo = PreferencesToolkits.getLocalSettingUnitInfo(PortalActivity.this);
                if (localSettingUnitInfo==ToolKits.UNIT_GONG){

                    distanceView.setText(distancekm + "");
                    distanceUnit.setText(R.string.KM);
                }else {
                    distanceView.setText(Double.toString((Math.round(distanceKm*0.6214 * 100 + 0.5) / 100.0)));
                    distanceUnit.setText(R.string.KMunit);
                }

                //浅睡 小时
                double lightSleepHour = CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getSleepMinute()), 1);
                //深睡 小时
                double deepSleepHour = CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getDeepSleepMiute()), 1);
                double sleepTime = CommonUtils.getScaledDoubleValue(lightSleepHour + deepSleepHour, 1);
                sleepView.setText(sleepTime + "");

                //走路 分钟
                double walktime = CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getWork_duration()), 1);
                //跑步 分钟
                double runtime = CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getRun_duration()), 1);

                double worktime = CommonUtils.getScaledDoubleValue(walktime + runtime, 1);

                int walkCal = ToolKits.calculateCalories(Float.parseFloat(mDaySynopic.getWork_distance()), (int) walktime * 60, userEntity.getUserBase().getUser_weight());
                int runCal = ToolKits.calculateCalories(Float.parseFloat(mDaySynopic.getRun_distance()), (int) runtime * 60, userEntity.getUserBase().getUser_weight());
                //基础卡路里
                int caloriesNow = CaloriesUtils.getCaloriesNow(PortalActivity.this);
                MyLog.e("caloriesNow",caloriesNow+"caloriesNow");
                int calValue = caloriesNow+walkCal+runCal ;
                MyApplication.getInstance(PortalActivity.this).setOld_calories(calValue);
                calView.setText(calValue+ "");
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                AsyncTaskManger.getAsyncTaskManger().removeAsyncTask(this);
            }
        };
        // 确保当前只有一个AsyncTask在运行，否则用户恶心切换会OOM
        if (currentDataAsync != null) {
            AsyncTaskManger.getAsyncTaskManger().removeAsyncTask(currentDataAsync, true);
        }
        AsyncTaskManger.getAsyncTaskManger().addAsyncTask(currentDataAsync = dataAsyncTask);
        dataAsyncTask.execute();
    }


    /**
     * 更新时钟上的UI显示
     */
    private void updateTimeThread() {
        TypefaceUtils.setNumberType(PortalActivity.this, main_tv_day);
        TypefaceUtils.setNumberType(PortalActivity.this, main_tv_hour);
        new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        timeHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                String timeWithStr = CommonUtils.getTimeWithStrNoS(":");
                                String dateWithStr = CommonUtils.getDateWithStr();
                                String timeWithStr1 = CommonUtils.getTimeWithStr();
                                if (timeWithStr1.equals("00:00:00")){
                                    getInfoFromDB();
                                }
                                main_tv_hour.setText(timeWithStr);
                                main_tv_day.setText(dateWithStr);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BleService.REQUEST_ENABLE_BT) {
            switch (resultCode) {
                case Activity.RESULT_CANCELED: //用户取消打开蓝牙
                    BleService.setNEED_SCAN(false);
                    break;

                case Activity.RESULT_OK:       //用户打开蓝牙
                    Log.e(TAG, "//用户打开蓝牙");
                    BleService.setNEED_SCAN(true);
                    provider.scanForConnnecteAndDiscovery();
                    break;

                default:
                    break;
            }
            return;
        } else if (requestCode == CommParams.REQUEST_CODE_BOUND && resultCode == Activity.RESULT_OK) {
            String type = data.getStringExtra(BundTypeActivity.KEY_TYPE);
            if (type.equals(BundTypeActivity.KEY_TYPE_WATCH)) {
                startActivityForResult(new Intent(PortalActivity.this, BoundActivity.class), CommParams.REQUEST_CODE_BOUND_WATCH);
            } else if (type.equals(BundTypeActivity.KEY_TYPE_BAND)) {
                startActivityForResult(IntentFactory.startActivityBundBand(PortalActivity.this), CommParams.REQUEST_CODE_BOUND_BAND);
            }
        } else if (requestCode == CommParams.REQUEST_CODE_BOUND_BAND && resultCode == Activity.RESULT_OK) {
            MyLog.e(TAG, "手环绑定成功");
        } else if (requestCode == CommParams.REQUEST_CODE_BOUND_WATCH && resultCode == Activity.RESULT_OK) {
        }
    }


    public void onUploadClicked() {
        MyLog.e(TAG, "onUploadClicked执行了");
        AlertDialog.Builder builder = new AlertDialog.Builder(PortalActivity.this);
        View view = LayoutInflater.from(PortalActivity.this).inflate(R.layout.progress_dialog, null);
        progressbar = (ProgressBar) view.findViewById(R.id.progressbar);
        progressInt = (TextView) view.findViewById(R.id.progressInt);
        builder.setView(view);
        downloadingProgressDialog = builder.create();
        downloadingProgressDialog.setCancelable(false);
        downloadingProgressDialog.show();
        DfuServiceInitiator starter = new DfuServiceInitiator(userEntity.getDeviceEntity().getLast_sync_device_id())
                .setDeviceName("DYH_01")
                .setKeepBond(false)
                .setForceDfu(false)
                .setPacketsReceiptNotificationsEnabled(true)
                .setPacketsReceiptNotificationsValue(12);
        starter.setZip(file.getPath());
        starter.start(this, DfuService.class);
    }


    /**
     * 提示绑定的弹出框
     */
    private void showBundDialog() {
        // 您还未绑定 请您绑定一个设备
        AlertDialog dialog = new AlertDialog.Builder(PortalActivity.this)
                .setTitle(ToolKits.getStringbyId(PortalActivity.this, R.string.portal_main_unbound))
                .setMessage(ToolKits.getStringbyId(PortalActivity.this, R.string.portal_main_unbound_msg))
                .setPositiveButton(ToolKits.getStringbyId(PortalActivity.this, R.string.general_ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                IntentFactory.startBundTypeActivity(PortalActivity.this);
                            }
                        })
                .setNegativeButton(ToolKits.getStringbyId(PortalActivity.this, R.string.general_cancel), null)
                .create();
        dialog.show();
    }

    /**
     * 蓝牙观察者实现类.
     */
    private class BLEProviderObserverAdapterImpl extends BLEHandler.BLEProviderObserverAdapter {

        @Override
        protected Activity getActivity() {
            return PortalActivity.this;
        }

        /**********
         * 用户没打开蓝牙
         *********/
        @Override
        public void updateFor_handleNotEnableMsg() {
            //用户未打开蓝牙
            Log.i(TAG, "updateFor_handleNotEnableMsg");
            if (pulltorefreshView!=null&&pulltorefreshView.isRefreshing())
                pulltorefreshView.onRefreshComplete();
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            getActivity().startActivityForResult(enableBtIntent, BleService.REQUEST_ENABLE_BT);
        }

        @Override
        public void updateFor_handleUserErrorMsg(int id) {
            MyLog.e(TAG, "updateFor_handleConnectSuccessMsg");
        }

        /**********
         * BLE连接中
         *********/
        @Override
        public void updateFor_handleConnecting() {
            //正在连接
            MyLog.e(TAG, "updateFor_handleConnecting");
        }

        /**********
         * 扫描BLE设备TimeOut
         *********/
        @Override
        public void updateFor_handleScanTimeOutMsg() {
            MyLog.e(TAG, "updateFor_handleScanTimeOutMsg");
            if (pulltorefreshView!=null&&pulltorefreshView.isRefreshing())
                pulltorefreshView.onRefreshComplete();
        }

        /**********
         * BLE连接失败
         *********/
        @Override
        public void updateFor_handleConnectFailedMsg() {
            //连接失败
            MyLog.e(TAG, "updateFor_handleConnectFailedMsg");
            if (pulltorefreshView != null && pulltorefreshView.isRefreshing())
                pulltorefreshView.onRefreshComplete();
        }

        @Override
        public void updateFor_handleSendDataError() {
            super.updateFor_handleSendDataError();
            if (pulltorefreshView != null && pulltorefreshView.isRefreshing())
                pulltorefreshView.onRefreshComplete();
        }

        /**********
         * BLE连接成功
         *********/
        @Override
        public void updateFor_handleConnectSuccessMsg() {
            //连接成功
            MyLog.e(TAG, "updateFor_handleConnectSuccessMsg");
        }

        /**********
         * BLE断开连接
         *********/
        @Override
        public void updateFor_handleConnectLostMsg() {
            MyLog.e(TAG, "updateFor_handleConnectLostMsg");
                if (pulltorefreshView != null && pulltorefreshView.isRefreshing())
                    pulltorefreshView.onRefreshComplete();
            //蓝牙断开的显示
        }

        /**********
         * 0X13命令返回
         *********/
        @Override
        public void updateFor_notifyFor0x13ExecSucess_D(LPDeviceInfo latestDeviceInfo) {
            if (latestDeviceInfo != null && latestDeviceInfo.recoderStatus == 5) {
                Log.e("BluetoothActivity", "用户非法");
                Toast.makeText(PortalActivity.this, "设备已经被其他用户绑定", Toast.LENGTH_SHORT).show();
                provider.release();
                provider.setCurrentDeviceMac(null);
                provider.setmBluetoothDevice(null);
                provider.resetDefaultState();
                provider.clearProess();
            }
            if (latestDeviceInfo!=null&&latestDeviceInfo.recoderStatus==66){
                if (!CommonUtils.isStringEmpty(MyApplication.getInstance(PortalActivity.this)
                        .getLocalUserInfoProvider().getDeviceEntity().getLast_sync_device_id())){
                    if (builder!=null&&!builder.create().isShowing()) {
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                provider.unBoundDevice(PortalActivity.this);
                                provider.requestbound_fit(PortalActivity.this);
//                                try {
//                                    Thread.sleep(1000);
//                                    BleService.getInstance(PortalActivity.this).releaseBLE();
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                                IntentFactory.start_Bluetooth(PortalActivity.this);
                            }
                        }).setMessage(getString(R.string.Need_before))
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).show();
                    }
                }else {
                    provider.unBoundDevice(PortalActivity.this);
                }
            }
        }


        @Override
        public void updateFor_BoundContinue() {
            super.updateFor_BoundContinue();
            boundhandler.postDelayed(boundRunnable, sendcount_time);
        }

        @Override
        public void updateFor_BoundSucess() {
            BleService.getInstance(PortalActivity.this).syncAllDeviceInfo(PortalActivity.this);
        }

        @Override
        public void updateFor_notifyForDeviceUnboundSucess_D() {
            MyLog.e(TAG, "updateFor_notifyForDeviceUnboundSucess_D");
        }

        /**********
         * 剩余同步运动条目
         *********/
        @Override
        public void updateFor_SportDataProcess(Integer obj) {
            super.updateFor_SportDataProcess(obj);
            MyLog.e(TAG, "updateFor_SportDataProcess");
        }

        /**********
         * 运动记录读取完成
         *********/
        @Override
        public void updateFor_handleDataEnd() {
            MyLog.e(TAG, " updateFor_handleDataEnd ");
            //把数据库未同步到server的数据提交上去
            if (ToolKits.isNetworkConnected(PortalActivity.this)) {
                new AsyncTask<Object, Object, SportRecordUploadDTO>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                    }

                    @Override
                    protected SportRecordUploadDTO doInBackground(Object... params) {
                        // 看看数据库中有多少未同步（到服务端的数据）
                        final ArrayList<SportRecord> up_List = UserDeviceRecord.findHistoryWitchNoSync(PortalActivity.this, MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider().getUser_id() + "");
                        MyLog.e(TAG, "【NEW离线数据同步】一共查询出" + up_List.size() + "条数据");
                        //有数据才去算
                        if (up_List.size() > 0) {
                            SportRecordUploadDTO sportRecordUploadDTO = new SportRecordUploadDTO();
                            final String startTime = up_List.get(0).getStart_time();
                            MyLog.e(TAG, "starttime" + startTime);
                            final String endTime = up_List.get(up_List.size() - 1).getStart_time();
                            MyLog.e(TAG, "starttime" + endTime);
                            sportRecordUploadDTO.setDevice_id("1");
                            sportRecordUploadDTO.setUtc_time("1");
                            sportRecordUploadDTO.setOffset(TimeZoneHelper.getTimeZoneOffsetMinute() + "");
                            sportRecordUploadDTO.setUser_id(MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider().getUser_id());
                            sportRecordUploadDTO.setList(up_List);
//                            HttpUtils.doPostAsyn(CommParams.SERVER_CONTROLLER_URL_NEW, HttpHelper.sport2Server(sportRecordUploadDTO), new HttpUtils.CallBack() {
//                                @Override
//                                public void onRequestComplete(String result) {
//                                    MyLog.e(TAG, "【NEW离线数据同步】服务端返回" + result);
//                                    long sychedNum = UserDeviceRecord.updateForSynced(PortalActivity.this, MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider().getUser_id() + "", startTime, endTime);
//                                    MyLog.d(TAG, "【NEW离线数据同步】本次共有" + sychedNum + "条运动数据已被标识为\"已同步\"！[" + startTime + "~" + endTime + "]");
//                                }
//                            });
                            return sportRecordUploadDTO;
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(SportRecordUploadDTO sportRecordUploadDTO) {
                        super.onPostExecute(sportRecordUploadDTO);
                    }
                }.execute();
            }
            //运动数据同步完毕 去UI上显示同步完毕的结果
            getInfoFromDB();
        }

        /**********
         * 消息提醒设置成功
         *********/
        @Override
        public void updateFor_notify() {
            super.updateFor_notify();
            MyLog.e(TAG, "消息提醒设置成功！");
//            DfuServiceListenerHelper.registerProgressListener(PortalActivity.this,mDfuProgressListener);
//            OAD升级
//            onUploadClicked();
        }

        @Override
        public void updateFor_notifyForModelName(LPDeviceInfo latestDeviceInfo) {
            super.updateFor_notifyForModelName(latestDeviceInfo);
            if (latestDeviceInfo.modelName == null)
                latestDeviceInfo.modelName = "LW100";
            MyLog.e(TAG, "modelName:" + latestDeviceInfo.modelName);
            MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider().getDeviceEntity().setModel_name(latestDeviceInfo.modelName);
//            if((System.currentTimeMillis()/1000)-PreferencesToolkits.getOADUpdateTime(getActivity())>86400)
            {
                // 查询是否要更新固件
                final LocalInfoVO vo = PreferencesToolkits.getLocalDeviceInfo(PortalActivity.this);
                FirmwareDTO firmwareDTO = new FirmwareDTO();
                int deviceType = MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider().getDeviceEntity().getDevice_type();
                if (deviceType == MyApplication.DEVICE_BAND || deviceType == MyApplication.DEVICE_BAND - 1) {
                    deviceType = 1;
                } else {
                    deviceType = MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider().getDeviceEntity().getDevice_type();
                }
                firmwareDTO.setDevice_type(deviceType);
                firmwareDTO.setFirmware_type(DeviceActivity.DEVICE_VERSION_TYPE);
                int version_int = ToolKits.makeShort(vo.version_byte[1], vo.version_byte[0]);
                firmwareDTO.setVersion_int(version_int + "");
                firmwareDTO.setModel_name(latestDeviceInfo.modelName);
                if (MyApplication.getInstance(PortalActivity.this).isLocalDeviceNetworkOk()) {
                    //请求网络
                    CallServer.getRequestInstance().add(PortalActivity.this, false, CommParams.HTTP_OAD, NoHttpRuquestFactory.create_OAD_Request(firmwareDTO), new HttpCallback<String>() {
                        @Override
                        public void onSucceed(int what, Response<String> response) {
                            DataFromServer dataFromServer = JSON.parseObject(response.get(), DataFromServer.class);
                            String value = dataFromServer.getReturnValue().toString();
                            if (!CommonUtils.isStringEmpty(response.get())) {
                                if (dataFromServer.getErrorCode() != 10020) {
                                    JSONObject object = JSON.parseObject(value);
                                    String version_code = object.getString("version_code");
                                    if (Integer.parseInt(version_code, 16) > Integer.parseInt(vo.version, 16)) {
                                        PreferencesToolkits.setOADUpdateTime(PortalActivity.this);
                                        AlertDialog dialog = new AlertDialog.Builder(PortalActivity.this)
                                                .setTitle(ToolKits.getStringbyId(PortalActivity.this, R.string.general_tip))
                                                .setMessage(ToolKits.getStringbyId(PortalActivity.this, R.string.bracelet_oad_Portal))
                                                .setPositiveButton(ToolKits.getStringbyId(PortalActivity.this, R.string.general_ok),
                                                        new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                startActivity(IntentFactory.star_DeviceActivityIntent(PortalActivity.this, DeviceActivity.DEVICE_UPDATE));
                                                            }
                                                        })
                                                .setNegativeButton(ToolKits.getStringbyId(PortalActivity.this, R.string.general_cancel), null)
                                                .create();
                                        dialog.show();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {

                        }
                    });
                }
            }

            if (needTocheck){
                userEntity.getDeviceEntity().setModel_name(latestDeviceInfo.modelName);
                needTocheck = false ;
//                downloadZip();
            }

        }

        /**********
         * 闹钟提醒设置成功
         *********/
        @Override
        public void updateFor_notifyForSetClockSucess() {
            super.updateFor_notifyForSetClockSucess();
            MyLog.e(TAG, "updateFor_notifyForSetClockSucess！");
        }

        /**********
         * 久坐提醒设置成功
         *********/
        @Override
        public void updateFor_notifyForLongSitSucess() {
            super.updateFor_notifyForLongSitSucess();
            MyLog.e(TAG, "updateFor_notifyForLongSitSucess！");
        }

        /**********
         * 身体信息(激活设备)设置成功
         *********/
        @Override
        public void updateFor_notifyForSetBodySucess() {
            MyLog.e(TAG, "updateFor_notifyForSetBodySucess");
        }

        /**********
         * 设置时间失败
         *********/
        @Override
        public void updateFor_handleSetTimeFail() {
            MyLog.e(TAG, "updateFor_handleSetTimeFail");
            super.updateFor_handleSetTimeFail();
        }



        /**********
         * 设置时间成功
         *********/
        @Override
        public void updateFor_handleSetTime() {

            MyLog.e(TAG, "updateFor_handleSetTime");
        }

        /***********
         * 获取设备ID=
         *********/
        @Override
        public void updateFor_getDeviceId(String obj) {
            super.updateFor_getDeviceId(obj);
            MyLog.e(TAG, "读到的deviceid:" + obj);
        }

        /**********
         * 卡号读取成功
         *********/
        @Override
        public void updateFor_CardNumber(String cardId) {
            MyLog.e(TAG, "updateFor_CardNumber：" + cardId);
            super.updateFor_CardNumber(cardId);

        }
    }
    LocalInfoVO vo;
    private ProgressDialog dialog;
    private ProgressDialog dialog_connect;//下载进度
    private URI uri = null;
    private File file;

    private void downloadZip() {
        vo = PreferencesToolkits.getLocalDeviceInfo(PortalActivity.this);
        dialog = new ProgressDialog(PortalActivity.this);
        dialog.setMessage(getString(R.string.getting_version_information));
        int version_int = ToolKits.makeShort(vo.version_byte[1], vo.version_byte[0]);
//        int version_int = 500 ;
        CallServer.getRequestInstance().add(PortalActivity.this, false,
                CommParams.HTTP_OAD, NoHttpRuquestFactory.creat_New_OAD_Request(userEntity.getDeviceEntity().getModel_name()
                        ,version_int), newHttpCallback);
    }


    private HttpCallback<String> newHttpCallback = new HttpCallback<String>() {
        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {
            MyLog.e(TAG,"failed________"+message.toString());
            dialog.dismiss();
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            dialog.dismiss();
            MyLog.e(TAG + "devicefragment", response.toString() );
            if (response.get()!=null&&!response.get().isEmpty()) {
                try {
                    dialog.dismiss();
                    CheckFirmwareVersionReponse checkVersionReponse = JSONObject.parseObject(response.get(), CheckFirmwareVersionReponse.class);
                    if(checkVersionReponse.getModel_name()==null){
//                        MyToast.show(PortalActivity.this, getString(R.string.bracelet_oad_version_top), Toast.LENGTH_LONG);
                    }else {
//                        powerManager = (PowerManager) activity.getSystemService(Service.POWER_SERVICE);
//                        wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Lock");
//                        //是否需计算锁的数量
//                        wakeLock.setReferenceCounted(false);
//                        //请求屏幕常亮，onResume()方法中执行
//                        wakeLock.acquire();
                        downLoadByRetrofit(checkVersionReponse.getModel_name(),
                                checkVersionReponse.getFile_name(),Integer.parseInt(checkVersionReponse.getVersion_code()) ,
                                "downloaddyh08.zip",false);
                    }
                }catch (Exception e){
//                    MyToast.show(PortalActivity.this, getString(R.string.bracelet_oad_version_top), Toast.LENGTH_LONG);
                }
            } else {
                dialog.dismiss();
//                MyToast.show(PortalActivity.this,  getString(R.string.bracelet_oad_version_top), Toast.LENGTH_LONG);
            }
        }
    };

    public void downLoadByRetrofit(String model_name, String file_name, int version_int, final String saveFileName, final boolean OADDirect) {
//        String message = getString(R.string.general_uploadingnewfirmware);
        dialog_connect = new ProgressDialog(PortalActivity.this);
        dialog_connect.setCancelable(false);
        dialog_connect.setMessage(getString(R.string.downloading));
        dialog_connect.show();
        OADApi oadApi = FirmwareRetrofitClient.getInstance().create(OADApi.class);
        HashMap<String, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("model_name", model_name);
        objectObjectHashMap.put("file_name", file_name);

        String versionString = version_int + "";
        if (versionString.length()%2==1){
            versionString = "0"+versionString ;
        }
        objectObjectHashMap.put("version_code",versionString);
        Call<ResponseBody> responseBodyCall = oadApi.download_file("close",objectObjectHashMap);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                try {
                    if (response.body()!=null) {
                        MyLog.e(TAG, response.body().byteStream() + "");
                        InputStream is = response.body().byteStream();
                        file = getTempFile(PortalActivity.this, saveFileName);
                        FileOutputStream fos = new FileOutputStream(file);
                        BufferedInputStream bis = new BufferedInputStream(is);
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = bis.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                        }
                        fos.flush();
                        fos.close();
                        bis.close();
                        is.close();
                        Log.e(TAG, "下载完成" + saveFileName);
                        dialog_connect.dismiss();
                        onUploadClicked();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                MyLog.e(TAG, t.toString());
                dialog_connect.dismiss();
                Toast.makeText(PortalActivity.this, getString(R.string.bracelet_down_file_fail), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public File getTempFile(Context context, String name) {
        File file = null;
        try {
            file = new File(context.getCacheDir(),name);
        } catch (Exception e) {
            // Error while creating file
        }
        return file;
    }
    //固件更新
    private final DfuProgressListenerAdapter mDfuProgressListener = new DfuProgressListenerAdapter() {
        @Override
        public void onProgressChanged(String deviceAddress, int percent, float speed, float avgSpeed, int currentPart, int partsTotal) {
            progressbar.setProgress(percent);
            progressInt.setText(percent+"%");
            MyLog.e(TAG, "mDfuProgressListener" + percent + "----");

        }

        @Override
        public void onDfuCompleted(String deviceAddress) {
            super.onDfuCompleted(deviceAddress);
            MyLog.e(TAG, "mDfuProgressListener" + "---onDfuCompleted-");
            downloadingProgressDialog.dismiss();
            Toast.makeText(PortalActivity.this,getString(R.string.user_info_update_success),Toast.LENGTH_SHORT).show();
            provider.connect();
        }

        @Override
        public void onError(String deviceAddress, int error, int errorType, String message) {
            super.onError(deviceAddress, error, errorType, message);
            MyLog.e(TAG, "mDfuProgressListener" + "--onError--");
            Toast.makeText(PortalActivity.this,getString(R.string.user_info_update_failure),Toast.LENGTH_SHORT).show();
            downloadingProgressDialog.dismiss();
        }
    };

}



