package com.linkloving.dyh08.logic.UI.launch;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import com.linkloving.dyh08.BleService;
import com.linkloving.dyh08.IntentFactory;
import com.linkloving.dyh08.MyApplication;
import com.linkloving.dyh08.R;
import com.linkloving.dyh08.basic.AppManager;
import com.linkloving.dyh08.logic.UI.login.LoginFinishActivity;
import com.linkloving.dyh08.logic.dto.EntEntity;
import com.linkloving.dyh08.logic.dto.SportDeviceEntity;
import com.linkloving.dyh08.logic.dto.UserBase;
import com.linkloving.dyh08.logic.dto.UserEntity;
import com.linkloving.dyh08.prefrences.PreferencesToolkits;

import net.hockeyapp.android.CrashManager;

import java.util.ArrayList;
import java.util.List;

public class AppStartActivity extends AppCompatActivity {

    private final static String TAG = AppStartActivity.class.getSimpleName();
    public static String SHAREDPREFERENCES_NAME = "first_pref";
    private RelativeLayout startLL = null;
    private int logined = 0;
    public static final int VERSION = 1;
    public static SharedPreferences sp;
    private ViewPager viewPager;
    private View view1, view2, view3;
    private List<View> viewList;//view数组
    private PagerAdapter pagerAdapter;
    private UserEntity userAuthedInfo;
    /**
     * Shared preferences key for always send dialog button.
     */
    private static final String ALWAYS_SEND_KEY = "always_send_crash_reports";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // FIX: 以下代码是为了解决Android自level 1以来的[安装完成点击“Open”后导致的应用被重复启动]的Bug
        // @see https://code.google.com/p/android/issues/detail?id=52247
        // @see https://code.google.com/p/android/issues/detail?id=2373
        // @see https://code.google.com/p/android/issues/detail?id=26658
        // @see https://github.com/cleverua/android_startup_activity
        // @see http://stackoverflow.com/questions/4341600/how-to-prevent-multiple-instances-of-an-activity-when-it-is-launched-with-differ/
        // @see http://stackoverflow.com/questions/12111943/duplicate-activities-on-the-back-stack-after-initial-installation-of-apk
        // 加了以下代码还得确保Manifast里加上权限申请：“android.permission.GET_TASKS”
        if (!isTaskRoot()) {// FIX START
            final Intent intent = getIntent();
            final String intentAction = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) &&
                    intentAction != null && intentAction.equals(Intent.ACTION_MAIN)) {
                finish();
            }
        }// FIX END
        super.onCreate(savedInstanceState);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        final View view = View.inflate(this, R.layout.activity_app_start, null);
        setContentView(R.layout.tw_start_activity);
        viewPager = (ViewPager) findViewById(R.id.ViewPager);
//        startLL = (RelativeLayout) findViewById(R.id.start_LL);
        LayoutInflater inflater = getLayoutInflater();
        view1 = inflater.inflate(R.layout.tw_start1, null);
        view2 = inflater.inflate(R.layout.tw_start2, null);
        view3 = inflater.inflate(R.layout.tw_start3, null);
        viewList = new ArrayList<View>();// 将要分页显示的View装入数组中
        viewList.add(view1);
        viewList.add(view2);
        viewList.add(view3);

        pagerAdapter = new PagerAdapter() {
            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                // TODO Auto-generated method stub
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return viewList.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                // TODO Auto-generated method stub
                container.removeView(viewList.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                // TODO Auto-generated method stub
                container.addView(viewList.get(position));
                return viewList.get(position);
            }
        };
        /**设置APP状态为打开*/
        BleService.getInstance(this).setEXIT_APP(false);


        UserEntity user = PreferencesToolkits.getLocalUserInfo(this);

        AppManager.getAppManager().addActivity(this);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == viewList.size() - 1) {
                    IntentFactory.startUsername(AppStartActivity.this);
                    finish();
                }
            }


            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // 读取SharedPreferences中需要的数据
        sp = getSharedPreferences("Y_Setting", Context.MODE_PRIVATE);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(AppStartActivity.this);
        boolean aBoolean = prefs.getBoolean(ALWAYS_SEND_KEY, false);
        if (!aBoolean ) {
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean(ALWAYS_SEND_KEY, true);
            edit.apply();
        }

        /**
         * 如果用户不是第一次使用则直接调转到显示界面,否则调转到引导界面
         */
        if (sp.getInt("VERSION", 0) != VERSION) {
            redirectTo();
//            获取权限
            if (Build.VERSION.SDK_INT >= 23) {
                String[] mPermissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE, Manifest.permission.READ_LOGS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.SET_DEBUG_APP, Manifest.permission.SYSTEM_ALERT_WINDOW, Manifest.permission.GET_ACCOUNTS, Manifest.permission.WRITE_APN_SETTINGS};
                ActivityCompat.requestPermissions(this, mPermissionList, 123);
            }
            viewPager.setAdapter(pagerAdapter);
        } else {
            redirectTo();
            Intent portalActivityIntent = IntentFactory.createPortalActivityIntent(AppStartActivity.this);
            startActivity(portalActivityIntent);
        }
    }



/*        // 渐变展示启动屏
        AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
        aa.setDuration(2000);
        view.startAnimation(aa);
        aa.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationEnd(Animation arg0)
            {
                redirectTo();
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {

            }

            @Override
            public void onAnimationStart(Animation animation)
            {
            }
        });*/


    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().removeActivity(this);
        SharedPreferences.Editor edit = sp.edit();
        edit.putInt("VERSION", 1);
        edit.commit();
    }


    /**
     * 跳转到...
     */
    private void redirectTo() {
        userAuthedInfo = PreferencesToolkits.getLocalUserInfoForLaunch(this);
        if (userAuthedInfo == null) {
            userAuthedInfo = new UserEntity();
            //1,000,000,00
            int ran = (int) ((10000000) * Math.random() + 10000);
            SharedPreferences userid = getSharedPreferences("userid", MODE_PRIVATE);
            SharedPreferences.Editor edit = userid.edit();
            edit.putInt("id", ran);
            edit.commit();
            userAuthedInfo.setUser_id(ran);
            userAuthedInfo.setDeviceEntity(new SportDeviceEntity());
            userAuthedInfo.setUserBase(new UserBase("1980-01-01"));
            userAuthedInfo.setEntEntity(new EntEntity());
            userAuthedInfo.getUserBase().setUser_status(1);
            userAuthedInfo.getUserBase().setNickname("User");
            userAuthedInfo.getUserBase().setUser_weight(60);
            userAuthedInfo.getUserBase().setUser_height(175);
//                userAuthedInfo.getDeviceEntity().setLast_sync_device_id("");
        }
        MyApplication.getInstance(this).setLocalUserInfoProvider(userAuthedInfo);
////        开始跳转到用户信息设置界面
//        SharedPreferences logined1 = getSharedPreferences("logined", MODE_PRIVATE);
//        int login = logined1.getInt("login", 0);
//        logined=login ;
//        if (logined==0){
//            IntentFactory.startUsername(AppStartActivity.this);
//            SharedPreferences logined = getSharedPreferences("logined", MODE_PRIVATE);
//            SharedPreferences.Editor edit = logined.edit();
//            edit.putInt("login",1);
//            edit.commit();
//        }else{
//                        startActivity(IntentFactory.createPortalActivityIntent(AppStartActivity.this));
//        }
//            AppStartActivity.this.finish();
    }


}
