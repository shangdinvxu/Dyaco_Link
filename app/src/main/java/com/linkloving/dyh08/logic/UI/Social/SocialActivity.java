package com.linkloving.dyh08.logic.UI.Social;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.linkloving.band.dto.DaySynopic;
import com.linkloving.band.dto.SportRecord;
import com.linkloving.band.sleep.DLPSportData;
import com.linkloving.band.sleep.SleepDataHelper;
import com.linkloving.band.ui.DatasProcessHelper;
import com.linkloving.band.ui.DetailChartCountData;
import com.linkloving.dyh08.MyApplication;
import com.linkloving.dyh08.R;
import com.linkloving.dyh08.basic.toolbar.ToolBarActivity;
import com.linkloving.dyh08.db.sport.UserDeviceRecord;
import com.linkloving.dyh08.db.summary.DaySynopicTable;
import com.linkloving.dyh08.logic.UI.main.PortalActivity;
import com.linkloving.dyh08.logic.UI.workout.Greendao.TraceGreendao;
import com.linkloving.dyh08.logic.dto.UserEntity;
import com.linkloving.dyh08.prefrences.PreferencesToolkits;
import com.linkloving.dyh08.utils.CommonUtils;
import com.linkloving.dyh08.utils.DateSwitcher;
import com.linkloving.dyh08.utils.GetDBInfo;
import com.linkloving.dyh08.utils.ToolKits;
import com.linkloving.dyh08.utils.TypefaceUtils;
import com.linkloving.dyh08.utils.logUtils.MyLog;
import com.linkloving.dyh08.utils.manager.AsyncTaskManger;
import com.linkloving.dyh08.utils.sportUtils.SportDataHelper;
import com.linkloving.utils.TimeZoneHelper;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import Trace.GreenDao.DaoMaster;
import Trace.GreenDao.Note;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.OnekeyShareTheme;

/**
 * Created by Daniel.Xu on 2016/9/21.
 */

public class SocialActivity extends ToolBarActivity {
    private static final String TAG = SocialActivity.class.getSimpleName();
    @InjectView(R.id.groups_time)
    TextView groupsTime;
    @InjectView(R.id.groups_tv_Step)
    AppCompatTextView groupsTvStep;
    @InjectView(R.id.Rl_step)
    RelativeLayout RlStep;
    @InjectView(R.id.groups_tv_Calories)
    AppCompatTextView groupsTvCalories;
    @InjectView(R.id.Rl_calories)
    RelativeLayout RlCalories;
    @InjectView(R.id.groups_tv_Distance)
    AppCompatTextView groupsTvDistance;
    @InjectView(R.id.Rl_distance)
    RelativeLayout RlDistance;
    @InjectView(R.id.main_tv_Duration)
    AppCompatTextView mainTvDuration;
    @InjectView(R.id.Rl_sleep)
    RelativeLayout RlSleep;
    @InjectView(R.id.sharebutton)
    Button sharebutton;
    @InjectView(R.id.shareEditText)
    EditText shareEditText;
    @InjectView(R.id.screenhot)
    LinearLayout screenhot;
    private View view;
    private String filePathCache = "/sdcard/ranking_v444.png";
    private ImageView fb,wx,qq,linkin,instagram;
    private ImageView twitter;
    private TraceGreendao traGreendao;
    private static List<LatLng> pointList = new ArrayList<LatLng>();
    private SQLiteDatabase db;
    private List<Note> startTimeList;
    private List<Note> endTimeList;
    private DaoMaster.DevOpenHelper devOpenHelper;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private Date todayDate;
    private String stringToday;
    private Date startDate,endDate;
    private UserEntity userEntity;
    private int user_id;
    private ArrayList<SportRecord> sportRecordArrayList;
    private float distance = 0;
    private int step = 0;
    private  int calValue = 0;
    private DetailChartCountData count;
    private String filePathCacheTotal = "/sdcard/ranking_v333.png";
    private List<Note> startNoteList;
    private List<Note> endNoteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tw_share_day);
        view = LayoutInflater.from(SocialActivity.this).inflate(R.layout.tw_share_day, null);
        ButterKnife.inject(this);
        ShareSDK.initSDK(SocialActivity.this);
        userEntity = MyApplication.getInstance(SocialActivity.this).getLocalUserInfoProvider();
        user_id = userEntity.getUser_id();
        initPopupWindow();
//        加载每日数据
        init();

    }

    private void initPopupWindow() {
        final View popupView = LayoutInflater.from(SocialActivity.this).inflate(R.layout.tw_share_popuwindow, null);
        final PopupWindow popupWindow = new PopupWindow(popupView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setContentView(popupView);
        fb = (ImageView) popupView.findViewById(R.id.fb);
        wx = (ImageView) popupView.findViewById(R.id.wx);
        qq = (ImageView) popupView.findViewById(R.id.qq);
        linkin = (ImageView) popupView.findViewById(R.id.Linkin);
        instagram = (ImageView) popupView.findViewById(R.id.instagram);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0xffffffff));
        sharebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 20);
                ToolKits.getScreenHot(screenhot, filePathCache);
            }
        });

        share();

    }

    @OnClick(R.id.Rl_step)
    void sendDB(View view){
        GetDBInfo.getDBInfoToFile(SocialActivity.this,"HeartRate.txt");
    }

    @OnClick(R.id.Rl_calories)
    void sendTxt(View view){
        GetDBInfo.toSendEmail(SocialActivity.this,GetDBInfo.getDiskCacheDir(SocialActivity.this)+"/"+"except.txt");
    }

    private void share() {
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShare(SocialActivity.this,"Facebook",false);

            }
        });
        wx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShare(SocialActivity.this,"Wechat",false);
            }
        });

        qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShare(SocialActivity.this,"QQ",false);
            }
        });
        linkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShare(SocialActivity.this,"LinkedIn",false);
            }
        });
        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShare(SocialActivity.this,"Instagram",false);
            }
        });

    }

    /**
     * 演示调用ShareSDK执行分享
     *
     * @param context
     * @param platformToShare  指定直接分享平台名称（一旦设置了平台名称，则九宫格将不会显示）
     * @param showContentEdit  是否显示编辑页
     */
    public  void showShare(Context context, String platformToShare, boolean showContentEdit) {
        OnekeyShare oks = new OnekeyShare();
        oks.setSilent(!showContentEdit);
        if (platformToShare != null) {
            oks.setPlatform(platformToShare);
        }
        //ShareSDK快捷分享提供两个界面第一个是九宫格 CLASSIC  第二个是SKYBLUE
        oks.setTheme(OnekeyShareTheme.CLASSIC);
        // 令编辑页面显示为Dialog模式
        oks.setDialogMode();
        // 在自动授权时可以禁用SSO方式
        oks.disableSSOWhenAuthorize();
        //oks.setAddress("12345678901"); //分享短信的号码和邮件的地址
//        oks.setTitle("ShareSDK--Title");
//        oks.setTitleUrl("http://mob.com");
//        oks.setText("ShareSDK--文本");
        //oks.setImagePath("/sdcard/test-pic.jpg");  //分享sdcard目录下的图片
//        oks.setImageUrl("http://99touxiang.com/public/upload/nvsheng/125/27-011820_433.jpg");
        oks.setFilePath(filePathCache);
        oks.setImagePath(filePathCache);
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



    //        得到哪几个记录是今天的
    private List<Note> getStartListToday(){
        List<Note> startNoteList = new ArrayList<>();

        for (int i = 0; i < startTimeList.size(); i++) {
            startDate = startTimeList.get(i).getStartDate();
            MyLog.e(TAG,"startDate"+startDate);
            String starttime = simpleDateFormat.format(startDate);
            MyLog.e(TAG,"starttime-----------"+starttime);
            if (starttime.equals(stringToday)) {
                startNoteList.add(startTimeList.get(i));
            }
        }
        return startNoteList ;
    }
    private List<Note> getEndListToday(){
        List<Note> endNoteList = new ArrayList<>();
        for (int i = 0; i < endTimeList.size(); i++) {
            endDate = endTimeList.get(i).getStartDate();
            MyLog.e(TAG,"endDate"+endDate);
            String endtime = simpleDateFormat.format(endDate);
            MyLog.e(TAG,"endtime-----------"+endtime);
            if (endtime.equals(stringToday)) {
                endNoteList.add(endTimeList.get(i));
            }
        }
        return endNoteList ;
    }


    private void initDuration() {
        long duration = 0 ;
        if (startNoteList.size()!=0){
            for (int i = 0; i< startNoteList.size(); i++) {
                long startTime = startNoteList.get(i).getStartDate().getTime();
                long endTime = endNoteList.get(i).getStartDate().getTime();
                duration = duration+endTime-startTime;
            }
        }
        String durtionText = getDurtion(duration);
        mainTvDuration.setText(durtionText);
    }

    private void initDistance(){
        //求Distance的逻辑
        float distanceTotal = 0 ;
        float stepTotal = 0 ;
        int  caloriesTotal = 0 ;
        if (startNoteList.size()!=0){
            for (int i = 0; i< startNoteList.size(); i++) {
                Date startDate = startNoteList.get(i).getStartDate();
                Date endDate = endNoteList.get(i).getStartDate();
                sportRecordArrayList = UserDeviceRecord.findHistoryChartwithHMS
                        (SocialActivity.this, String.valueOf(user_id), startDate, endDate);
                if (sportRecordArrayList.size() == 0) {
                    distance = 0;
                    step = 0 ;
                    calValue = 0 ;
                } else {
                    for (SportRecord sportRecordArray : sportRecordArrayList) {
                        if (sportRecordArray.getState().equals("1")||sportRecordArray.getState().equals("2")||sportRecordArray.getState().equals("3")) {
                            MyLog.e(TAG, "distance" + sportRecordArray.getDistance());
                            distance = Integer.parseInt(sportRecordArray.getDistance()) + distance;
                            step = Integer.parseInt(sportRecordArray.getStep()) + step;
                            //这一部分是求Calories的逻辑
                            List<DLPSportData> srs = SleepDataHelper.querySleepDatas2(sportRecordArrayList);
                            String startDateLocal = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(startDate);
                            try {
                                count = DatasProcessHelper.countSportData(srs, startDateLocal);
                                MyLog.e(TAG, "DEBUG【历史数据查询】汇总" + count.toString());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            int walkCal = ToolKits.calculateCalories(Float.parseFloat(String.valueOf(count.walking_distance)),
                                    (int) count.walking_duration * 60, userEntity.getUserBase().getUser_weight());
                            MyLog.e("walkCal", walkCal + "");
//        userEntity.getUserBase().getUser_weight()
                            int runCal = ToolKits.calculateCalories(Float.parseFloat(String.valueOf(count.runing_distance)), (int) count.runing_duation * 60, userEntity.getUserBase().getUser_weight());
                            calValue = walkCal + runCal;
                        }
                    }
                }
                caloriesTotal = caloriesTotal+calValue ;
                distanceTotal = distanceTotal+distance ;
                stepTotal = stepTotal +step ;
            }}
//        这一步吧distance转换成如5.3 的用来加km
        MyLog.e("distance", distance + "");
        double distanceKM = CommonUtils.getDoubleValue(distanceTotal / 1000, 1);
        String distanceTotalString = new Formatter().format("%.1f", distanceKM).toString();
        groupsTvDistance.setText(distanceTotalString);
        groupsTvStep.setText((int)stepTotal+"");
        groupsTvCalories.setText(caloriesTotal+"");
    }


    private void init() {
//        devOpenHelper = new DaoMaster.DevOpenHelper(SocialActivity.this, "Note", null);
//        db = devOpenHelper.getReadableDatabase();
//        traGreendao = new TraceGreendao(SocialActivity.this, db);
        String middleTime = getMiddleTime();
        groupsTime.setText(middleTime);
//        startTimeList = traGreendao.searchAllStarttime();
//        endTimeList = traGreendao.searchAllEndTime();
////        得到哪几个记录是今天的
//        startNoteList = getStartListToday();
//        endNoteList = getEndListToday();
//        initDuration();
//        initDistance();



        //改成分享幕界面的数据
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
                mDaySynopic = SportDataHelper.offlineReadMultiDaySleepDataToServer(SocialActivity.this, todyaStr, todyaStr);
                if (mDaySynopic.getTime_zone() == null) {
                    return null;
                }
                MyLog.e(TAG, "daySynopic:" + mDaySynopic.toString());
                mDaySynopicArrayList.add(mDaySynopic);
                DaySynopicTable.saveToSqliteAsync(SocialActivity.this, mDaySynopicArrayList, userEntity.getUser_id() + "");
                return mDaySynopic;
            }

            @Override
            protected void onPostExecute(DaySynopic mDaySynopic) {
                super.onPostExecute(mDaySynopic);
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
                    AsyncTaskManger.getAsyncTaskManger().removeAsyncTask(this);
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
                MyApplication.getInstance(SocialActivity.this).setOld_step(step);
                groupsTvStep.setText(step + "");

                //走路 里程
                int walkDistance = (int) (CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getWork_distance()), 0));
                //跑步 里程
                int runDistance = (int) (CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getRun_distance()), 0));
                int distance = walkDistance + runDistance;
                MyApplication.getInstance(SocialActivity.this).setOld_distance(distance);
                float distanceKm = (float) distance / 1000;
                String distancekm = "" ;
                if (distance<1000){
                    distancekm = DateSwitcher.oneFloat(distanceKm);
                }else {
                    distancekm = DateSwitcher.oneFloat(distanceKm);
                }
                int localSettingUnitInfo = PreferencesToolkits.getLocalSettingUnitInfo(SocialActivity.this);
                if (localSettingUnitInfo==ToolKits.UNIT_GONG){

                    groupsTvDistance.setText(distancekm + "");
//                    distanceUnit.setText(R.string.KM);
                }else {
                    groupsTvDistance.setText(Double.toString((Math.round(distanceKm*0.6214 * 100 + 0.5) / 100.0)));
//                    distanceUnit.setText(R.string.KMunit);
                }
//
//                //浅睡 小时
//                double lightSleepHour = CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getSleepMinute()), 1);
//                //深睡 小时
//                double deepSleepHour = CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getDeepSleepMiute()), 1);
//                double sleepTime = CommonUtils.getScaledDoubleValue(lightSleepHour + deepSleepHour, 1);
//                sleepView.setText(sleepTime + "");

                //走路 分钟
                double walktime = CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getWork_duration()), 1);
                //跑步 分钟
                double runtime = CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getRun_duration()), 1);

                double worktime = CommonUtils.getScaledDoubleValue(walktime + runtime, 1);

               int workSeconds = (int)(worktime * 60);
                int workHours =  workSeconds / 3600;

                String hourString ;
                if (workHours<10){
                    hourString = "0"+workHours ;
                }else {
                    hourString = workHours+"" ;
                }

                int workMinutes = (workSeconds - workHours * 3600) / 60;
                String minuteString ;
                if (workMinutes<10){
                    minuteString = "0"+workMinutes ;
                }else {
                    minuteString = workMinutes+"" ;
                }

                int workSecond = workSeconds-workHours*3600-workMinutes*60;
                String secondString ;
                if (workSecond<10){
                    secondString = "0"+workSecond ;
                }else {
                    secondString = workSecond+"" ;
                }
                mainTvDuration.setText(hourString+":"+minuteString+":"+secondString);
                int walkCal = ToolKits.calculateCalories(Float.parseFloat(mDaySynopic.getWork_distance()), (int) walktime * 60, userEntity.getUserBase().getUser_weight());
                int runCal = ToolKits.calculateCalories(Float.parseFloat(mDaySynopic.getRun_distance()), (int) runtime * 60, userEntity.getUserBase().getUser_weight());
                ToolKits toolKits = new ToolKits();
                int calorieseveryday = toolKits.getCalories(SocialActivity.this);
                Date dateToday = new Date();
                SimpleDateFormat hh = new SimpleDateFormat("HH", Locale.getDefault());
                String HH = hh.format(dateToday);
                SimpleDateFormat mm = new SimpleDateFormat("mm", Locale.getDefault());
                String MM = mm.format(dateToday);
                int caloriesNow = calorieseveryday * (Integer.parseInt(HH) * 60 + Integer.parseInt(MM)) / 1440;
                MyLog.e("caloriesNow",caloriesNow+"caloriesNow");
                int calValue = caloriesNow+walkCal+runCal ;
                groupsTvCalories.setText(calValue + "");
            }
        };
        dataAsyncTask.execute();
    }


    /**
     * long 转String
     * @param durationTime
     * @return
     */
    public String getDurtion(long durationTime) {
//        时间间隔转UTC
        Date date = new Date(durationTime);
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        instance.add(Calendar.MINUTE, -TimeZoneHelper.getTimeZoneOffsetMinute());
        Date time = instance.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
       String duration = simpleDateFormat.format(time);
        return duration;
    }


    private String getMiddleTime() {
        todayDate = new Date();
        SimpleDateFormat simpleDateFormatWithM = new SimpleDateFormat("yyyy-MM-dd a.HH:MM", Locale.US);
        String middleTime = simpleDateFormatWithM.format(todayDate);
        stringToday = simpleDateFormat.format(todayDate);
        return middleTime;
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
