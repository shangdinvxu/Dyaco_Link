package com.linkloving.dyh08.logic.UI.setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bluetoothlegatt.BLEHandler;
import com.example.android.bluetoothlegatt.BLEProvider;
import com.example.android.bluetoothlegatt.proltrol.LPUtil;
import com.example.android.bluetoothlegatt.proltrol.dto.LPDeviceInfo;
import com.linkloving.dyh08.BleService;
import com.linkloving.dyh08.MyApplication;
import com.linkloving.dyh08.R;
import com.linkloving.dyh08.ViewUtils.Wheelview.WheelView;
import com.linkloving.dyh08.basic.toolbar.ToolBarActivity;
import com.linkloving.dyh08.logic.UI.OAD.NotificationActivity;
import com.linkloving.dyh08.logic.dto.UserEntity;
import com.linkloving.dyh08.notify.NotificationService;
import com.linkloving.dyh08.prefrences.LocalUserSettingsToolkits;
import com.linkloving.dyh08.prefrences.PreferencesToolkits;
import com.linkloving.dyh08.prefrences.devicebean.DeviceSetting;
import com.linkloving.dyh08.utils.DeviceInfoHelper;
import com.linkloving.dyh08.utils.ToolKits;
import com.linkloving.dyh08.utils.logUtils.MyLog;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Daniel.Xu on 2016/10/16.
 */

public class NotificationSettingActivity extends ToolBarActivity {
    private final static String TAG = NotificationSettingActivity.class.getSimpleName();
    @InjectView(R.id.listview)
    ListView listview;
    private LayoutInflater layoutInflater;
    private View totalView;
    private ArrayList<String> hrStrings;
    private ArrayList<String> minStrings;
    private int ischecked = 0;
    private int isLongsettingChecked = 0;
    private DeviceSetting deviceSetting;
    private final static int STARTSEDENTARY = 1;
    private final static int ENDSEDENTARY = 2;
    private final static int CLOCKONE = 3;
    private final static int CLOCKTWO = 4;
    private final static int CLOCKTHREE = 5;
    private final static int CLOCKFOUR = 6;
    private final static int TIMESETTINGONE = 7;
    private final static int TIMESETTINGTWO = 8;
    private final static int TIMESETTINGTHREE = 9;
    private final static int TIMESETTINGFOUR = 10;
    private final static int INTERVALSTIME = 11;

    private int Intervalstime = 60;
    /**
     * 闹钟
     */
    private String clockoneHr = "00";
    private String clockoneMin = "00";
    private String clocktwoHr = "00";
    private String clocktwoMin = "00";
    private String clockthreeHr = "00";
    private String clockthreeMin = "00";
    private String clockfourHr = "00";
    private String clockfourMin = "00";
    /**
     * 定时器
     */
    private String timeoneHr = "00";
    private String timeoneMin = "00";
    private String timetwoHr;
    private String timetwoMin;
    private String timethreeHr;
    private String timethreeMin;
    private String timefourHr;
    private String timefourMin;

    private int clock1Switch;
    private int clock2Switch;
    private int clock3Switch;
    private int clock4Switch;
    private int timeType = 0;
    private String starttimeHr;
    private String starttimeMin;
    private String endTimeHr;
    private String endTimeMin;
    private String longsit_vaild = "0" ;
    private BLEProvider provider;
    private UserEntity userEntity;
    private int phonecall;
    private int SMScall;
    private int Emailcall;
    private int appscall;
    private TextView timeSetting1;
    private TextView clock1;
    private TextView clock2;
    private TextView clock3;
    private SharedPreferences.Editor edit;
    private TextView clock4;
    private String longsit_step;
    private TextView starttimeView;
    private TextView endtimeTextview;
    private TextView intervalTextview;
    private ImageView ok_imageview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tw_setting_activity);
        ButterKnife.inject(this);
        userEntity = MyApplication.getInstance(getApplicationContext()).getLocalUserInfoProvider();
        deviceSetting = LocalUserSettingsToolkits.getLocalSetting(NotificationSettingActivity.this, userEntity.getUser_id() + "");
        SharedPreferences clock = getSharedPreferences("clock", MODE_PRIVATE);
        edit = clock.edit();
        provider = BleService.getInstance(NotificationSettingActivity.this).getCurrentHandlerProvider();
        provider.setProviderHandler(new BLEHandler(NotificationSettingActivity.this) {
            @Override
            protected BLEProvider getProvider() {
                return provider;
            }

            @Override
            protected void notifyforSettingTime() {
                super.notifyforSettingTime();
                if (ok_imageview!=null){
                    ok_imageview.setVisibility(View.VISIBLE);
                }
            }
        });
        hrStrings = new ArrayList<>();
        for (int i = 0; i <= 23; i++) {
            String s;
            if (i < 10) {
                s = "0" + i;
            } else {
                s = Integer.toString(i);
            }
            hrStrings.add(s);
        }
        minStrings = new ArrayList<>();
        for (int i = 0; i <= 59; i++) {
            String s;
            if (i < 10) {
                s = "0" + i;
            } else {
                s = Integer.toString(i);
            }
            minStrings.add(s);
        }
        String[] strings = {getString(R.string.messagenoti),
                getString(R.string.sendentarynotif),
                getString(R.string.alarmclock),
                getString(R.string.timersetting),
        };
        layoutInflater = LayoutInflater.from(NotificationSettingActivity.this);
        totalView = layoutInflater.inflate(R.layout.tw_setting_activity, null);
        Myadapter myadapter = new Myadapter(NotificationSettingActivity.this, strings);
        listview.setAdapter(myadapter);
        View heardview = LayoutInflater.from(NotificationSettingActivity.this).inflate(R.layout.tw_setting_heardview, null);
        TextView textHeartView = (TextView) heardview.findViewById(R.id.textHeartView);
        textHeartView.setText(R.string.Notificationsetting);
        listview.addHeaderView(heardview);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /**因为有头所以从1开始*/
                switch (position) {
                    case 1:
                        initStartNotification();
                        initMessagePopupWindow();
                        break;
                    case 2:
                        initSedentaryPopupWindow();
                        break;
                    case 3:
                        initClockPopupWindow();
                        break;
                    case 4:
                        initTimeSettingPopupWindow();
                        break;

                }
            }
        });
    }

    private void initStartNotification() {
        if (!ToolKits.isEnabled(NotificationSettingActivity.this)) {
            startActivity(new Intent(NotificationService.ACTION_NOTIFICATION_LISTENER_SETTINGS));
        }
    }


    private void initTimeSettingPopupWindow() {
        View view = layoutInflater.inflate(R.layout.timesettingpopupwindow, null);
        ImageView dismiss = (ImageView) view.findViewById(R.id.dismiss);
        timeSetting1 = (TextView) view.findViewById(R.id.clock_1);
        final Switch switch1 = (Switch) view.findViewById(R.id.timesetting1);
        ok_imageview = (ImageView) view.findViewById(R.id.ok_picture);
        final PopupWindow popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        timeSetting1.setText(PreferencesToolkits.getTimeSettingString(NotificationSettingActivity.this));
//        switch1.setChecked(PreferencesToolkits.getTimeSettingBoolean(NotificationSettingActivity.this));
        popupWindow.setTouchable(true);
//        popupWindow.setClippingEnabled(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0xffD3D3D3));
        popupWindow.showAtLocation(totalView, Gravity.BOTTOM, 0, 0);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                String timeSetting = timeSetting1.getText().toString();
                boolean checked = switch1.isChecked();
                PreferencesToolkits.setTimeSettingBoolean(NotificationSettingActivity.this, checked);
                PreferencesToolkits.setTimeSettingString(NotificationSettingActivity.this, timeSetting);
            }
        });
        timeSetting1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeType = TIMESETTINGONE;
                String s = timeSetting1.getText().toString();
                String[] split = s.split(":");

                showTimePopupWindow(Integer.parseInt(split[0]), Integer.parseInt(split[1]));

            }
        });
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ok_imageview.setVisibility(View.GONE);
                if (isChecked) {
                    MyLog.e(TAG, "clockone" + timeSetting1.getText().toString());
                    if (timeSetting1.getText().toString().equals("00:00")) {
                        //都为0就不发送命令
                    } else {
                        if (provider.isConnectedAndDiscovered()) {
                            MyLog.e(TAG, "发送定时器的指令了");
                            LPDeviceInfo lpDeviceInfo1 = new LPDeviceInfo();
                            String s = timeSetting1.getText().toString();
                            String[] split = s.split(":");
                            lpDeviceInfo1.millions = Integer.parseInt(split[0]) * 3600 + Integer.parseInt(split[1]) * 60;
                            provider.SetTimeSetting(NotificationSettingActivity.this, lpDeviceInfo1);
                        } else {
                            Toast.makeText(NotificationSettingActivity.this, getString(R.string.keepthe), Toast.LENGTH_SHORT).show();
                        }
                    }
                }else {
                    LPDeviceInfo lpDeviceInfo1 = new LPDeviceInfo();
                    lpDeviceInfo1.millions=0;
                    provider.SetTimeSetting(NotificationSettingActivity.this, lpDeviceInfo1);
                }
            }
        });
//        clock2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                timeType = TIMESETTINGTWO ;
//                showTimePopupWindow();
//            }
//        });
//        clock3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                timeType = TIMESETTINGTHREE ;
//                showTimePopupWindow();
//            }
//        });
//        clock4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                timeType = TIMESETTINGFOUR ;
//                showTimePopupWindow();
//            }
//        });
    }

    private void initClockPopupWindow() {
        View view = layoutInflater.inflate(R.layout.alarmclockpopupwindow, null);
        ImageView dismiss = (ImageView) view.findViewById(R.id.dismiss);
        clock1 = (TextView) view.findViewById(R.id.clock_1);
        clock2 = (TextView) view.findViewById(R.id.clock2);
        clock3 = (TextView) view.findViewById(R.id.clock_3);
        clock4 = (TextView) view.findViewById(R.id.clock_4);
        final Switch switch1 = (Switch) view.findViewById(R.id.clock1);
        Switch switch2 = (Switch) view.findViewById(R.id.clock22);
        Switch switch3 = (Switch) view.findViewById(R.id.clock3);
        Switch switch4 = (Switch) view.findViewById(R.id.clock4);
        final PopupWindow popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
//        popupWindow.setClippingEnabled(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0xffD3D3D3));
        popupWindow.showAtLocation(totalView, Gravity.BOTTOM, 0, 0);
        //从本地取出闹钟的状态
//        闹钟1
        String Alarm_one = deviceSetting.getAlarm_one();
        MyLog.e(TAG, "闹钟字符串：" + Alarm_one);
        String[] strAlarm_one = Alarm_one.split("-");
        String strTime1 = strAlarm_one[0];
        clock1.setText(strTime1);
        int sw1 = Integer.parseInt(strAlarm_one[2]);
        switch1.setChecked(sw1 == 1);
        //        闹钟2
        String Alarm_two = deviceSetting.getAlarm_two();
        MyLog.e(TAG, "闹钟字符串：" + Alarm_two);
        String[] strAlarm_two = Alarm_two.split("-");
        String strTime2 = strAlarm_two[0];
        clock2.setText(strTime2);
        int sw2 = Integer.parseInt(strAlarm_two[2]);
        switch2.setChecked(sw2 == 1);
        //        闹钟3
        String Alarm_three = deviceSetting.getAlarm_three();
        MyLog.e(TAG, "闹钟字符串：" + Alarm_three);
        String[] strAlarm_three = Alarm_three.split("-");
        String strTime3 = strAlarm_three[0];
        clock3.setText(strTime3);
        int sw3 = Integer.parseInt(strAlarm_three[2]);
        switch3.setChecked(sw3 == 1);
        //        闹钟4
        String Alarm_four = deviceSetting.getAlarm_four();
        MyLog.e(TAG, "闹钟字符串：" + Alarm_four);
        String[] strAlarm_four = Alarm_four.split("-");
        String strTime4 = strAlarm_four[0];
        clock4.setText(strTime4);
        int sw4 = Integer.parseInt(strAlarm_four[2]);
        switch4.setChecked(sw4 == 1);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        clock1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeType = CLOCKONE;
                String s = clock1.getText().toString();
                String[] split = s.split(":");
                showTimePopupWindow(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
            }
        });
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                clock1Switch = isChecked ? 1 : 0;
                String clock1 = clockoneHr + ":" + clockoneMin + "-" + "127" + "-" + clock1Switch;
                deviceSetting.setAlarm_one(clock1);
                LocalUserSettingsToolkits.updateLocalSetting(NotificationSettingActivity.this, deviceSetting);
                if (provider.isConnectedAndDiscovered()) {
                    MyLog.e(TAG, "发送闹钟1的指令了");
                    provider.SetClock(NotificationSettingActivity.this, DeviceInfoHelper.fromUserEntity(NotificationSettingActivity.this,
                            userEntity));
                } else {
                    Toast.makeText(NotificationSettingActivity.this, getString(R.string.keepthe), Toast.LENGTH_SHORT).show();
                }
            }
        });
        clock2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeType = CLOCKTWO;
                String s = clock2.getText().toString();
                String[] split = s.split(":");
                showTimePopupWindow(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
            }
        });
        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                clock2Switch = isChecked ? 1 : 0;
                String clock2 = clocktwoHr + ":" + clocktwoMin + "-" + "127" + "-" + clock2Switch;
                deviceSetting.setAlarm_two(clock2);
                LocalUserSettingsToolkits.updateLocalSetting(NotificationSettingActivity.this, deviceSetting);

                if (provider.isConnectedAndDiscovered()) {
                    MyLog.e(TAG, "发送闹钟1的指令了");
                    provider.SetClock(NotificationSettingActivity.this, DeviceInfoHelper.fromUserEntity(NotificationSettingActivity.this,
                            userEntity));
                } else {
                    Toast.makeText(NotificationSettingActivity.this, getString(R.string.keepthe), Toast.LENGTH_SHORT).show();
                }

            }
        });
        clock3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeType = CLOCKTHREE;
                String s = clock3.getText().toString();
                String[] split = s.split(":");
                showTimePopupWindow(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
            }
        });
        switch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                clock3Switch = isChecked ? 1 : 0;
                String clock3 = clockthreeHr + ":" + clockthreeMin + "-" + "127" + "-" + clock3Switch;
                deviceSetting.setAlarm_three(clock3);
                MyLog.e(TAG, "clock3Switch" + clock3Switch);
                LocalUserSettingsToolkits.updateLocalSetting(NotificationSettingActivity.this, deviceSetting);

                if (provider.isConnectedAndDiscovered()) {
                    MyLog.e(TAG, "发送闹钟3的指令了");
                    provider.SetClock(NotificationSettingActivity.this, DeviceInfoHelper.fromUserEntity(NotificationSettingActivity.this,
                            userEntity));
                } else {
                    Toast.makeText(NotificationSettingActivity.this, getString(R.string.keepthe), Toast.LENGTH_SHORT).show();
                }

            }
        });
        clock4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeType = CLOCKFOUR;
                String s = clock4.getText().toString();
                String[] split = s.split(":");
                showTimePopupWindow(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
            }
        });
        switch4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                clock4Switch = isChecked ? 1 : 0;
                String clock4 = clockfourHr + ":" + clockfourMin + "-" + "127" + "-" + clock4Switch;
                deviceSetting.setAlarm_four(clock4);
                LocalUserSettingsToolkits.updateLocalSetting(NotificationSettingActivity.this, deviceSetting);

                if (provider.isConnectedAndDiscovered()) {
                    MyLog.e(TAG, "发送闹钟4的指令了");
                    provider.SetClock(NotificationSettingActivity.this, DeviceInfoHelper.fromUserEntity(NotificationSettingActivity.this,
                            userEntity));
                } else {
                    Toast.makeText(NotificationSettingActivity.this, getString(R.string.keepthe), Toast.LENGTH_SHORT).show();
                }
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                String clock11 = clock1.getText().toString() + "-" + "127" + "-" + (switch1.isChecked() ? 1 : 0);
                deviceSetting.setAlarm_one(clock11);
                String clock22 = clock2.getText().toString() + "-" + "127" + "-" + clock2Switch;
                deviceSetting.setAlarm_two(clock22);
                String clock33 = clock3.getText().toString() + "-" + "127" + "-" + clock3Switch;
                deviceSetting.setAlarm_three(clock33);
                String clock44 = clock4.getText().toString() + "-" + "127" + "-" + clock4Switch;
                deviceSetting.setAlarm_four(clock44);
                LocalUserSettingsToolkits.updateLocalSetting(NotificationSettingActivity.this, deviceSetting);
            }
        });


    }

    private void initSedentaryPopupWindow() {
        View view = layoutInflater.inflate(R.layout.sedentarypopupwindow, null);
        ImageView dismiss = (ImageView) view.findViewById(R.id.dismiss);
        starttimeView = (TextView) view.findViewById(R.id.starttimeTextview);
        endtimeTextview = (TextView) view.findViewById(R.id.endtimeTextview);
        intervalTextview = (TextView) view.findViewById(R.id.intervalTextview);
        ImageView startTimeNext = (ImageView) view.findViewById(R.id.startTimeNext);
        final ImageView endTimeNext = (ImageView) view.findViewById(R.id.endTimeNext);
        final Switch switchInterval = (Switch) view.findViewById(R.id.switchinterval);
        final ImageView Intervalsnext = (ImageView) view.findViewById(R.id.Intervalsnext);
        deviceSetting = LocalUserSettingsToolkits.getLocalSetting(NotificationSettingActivity.this, userEntity.getUser_id() + "");
        final String longsit_time = deviceSetting.getLongsit_time();
        String[] split = longsit_time.split("-");
        String[] splits = split[0].split(":");
        starttimeHr = splits[0];
        starttimeMin = splits[1];
        starttimeView.setText(starttimeHr+":"+starttimeMin);
        String[] splitsEndTime = split[1].split(":");
        endTimeHr = splitsEndTime[0];
        endTimeMin = splitsEndTime[1];
        endtimeTextview.setText(endTimeHr+":"+endTimeMin);
        Intervalstime = deviceSetting.getLongsit_intervals();
        intervalTextview.setText(Intervalstime+"");
        longsit_vaild = deviceSetting.getLongsit_vaild();
        longsit_step = deviceSetting.getLongsit_step();
        final String[] longsit_vaild = {deviceSetting.getLongsit_vaild()};
        if ("".equals(longsit_vaild[0]) || "0".equals(longsit_vaild[0])) {
            switchInterval.setChecked(false);
        } else {
            switchInterval.setChecked(true);
        }
        final PopupWindow popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
//        popupWindow.setClippingEnabled(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0xffD3D3D3));
        popupWindow.showAtLocation(totalView, Gravity.BOTTOM, 0, 0);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        startTimeNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deviceSetting = LocalUserSettingsToolkits.getLocalSetting(NotificationSettingActivity.this, userEntity.getUser_id() + "");
                timeType = STARTSEDENTARY;
                String longsit_time = deviceSetting.getLongsit_time();
                String[] split = longsit_time.split("-");
                String[] splits = split[0].split(":");
                showTimePopupWindow(Integer.parseInt(splits[0]), Integer.parseInt(splits[1]));
//                showTimePopupWindow(0, 0);
            }
        });
        endTimeNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deviceSetting = LocalUserSettingsToolkits.getLocalSetting(NotificationSettingActivity.this, userEntity.getUser_id() + "");
                timeType = ENDSEDENTARY;
                String longsit_time = deviceSetting.getLongsit_time();
                String[] split = longsit_time.split("-");
                String[] splits = split[1].split(":");
                showTimePopupWindow(Integer.parseInt(splits[0]), Integer.parseInt(splits[1]));
            }
        });

        Intervalsnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int longsit_intervals = deviceSetting.getLongsit_intervals();
                int i = longsit_intervals / 15 - 1;
                View longsit_popupwindow = layoutInflater.inflate(R.layout.longsitpopupwindow, null);
                final PopupWindow longsitTimePopupwindow = new PopupWindow(longsit_popupwindow, LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT, true);
                longsitTimePopupwindow.setTouchable(true);
                longsitTimePopupwindow.setBackgroundDrawable(new ColorDrawable(0xffD3D3D3));
                longsitTimePopupwindow.showAtLocation(totalView, Gravity.BOTTOM, 0, 0);
                final WheelView longsitWheelview = (WheelView) longsit_popupwindow.findViewById(R.id.longsitWheelview);
                ImageView dismiss = (ImageView) longsit_popupwindow.findViewById(R.id.dismiss);
                dismiss.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        longsitTimePopupwindow.dismiss();
                    }
                });

                Button buttonOK = (Button) longsit_popupwindow.findViewById(R.id.okBtn);
                buttonOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        longsitTimePopupwindow.dismiss();
                    }
                });
                ArrayList<String> strings = new ArrayList<>();
                strings.add("15 min");
                strings.add("30 min");
                strings.add("45 min");
                strings.add("60 min");
                longsitWheelview.setItems(strings);
                longsitWheelview.setSeletion(i);
                longsitTimePopupwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        int seletedIndex = longsitWheelview.getSeletedIndex();
                        Intervalstime = (seletedIndex + 1) * 15;
                        String time = starttimeHr + ":" + starttimeMin + "-" + endTimeHr + ":" + endTimeMin + "-" + "00" + ":"
                                + "00" + "-" + "00" + ":" + "00";
                        deviceSetting.setLongsit_time(time);
                        deviceSetting.setLongsit_step(longsit_step);
                        deviceSetting.setLongsit_intervals(Intervalstime);
                        intervalTextview.setText(Intervalstime+"");
                        LocalUserSettingsToolkits.updateLocalSetting(NotificationSettingActivity.this,
                                deviceSetting);
                    }
                });
            }
        });

        switchInterval.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if ((starttimeHr + starttimeMin).equals("00:00") && (endTimeHr + endTimeMin).equals("00:00")) {
                        //都为0就不发送命令
                        MyLog.e(TAG, "不发送久坐提醒的指令");
                    } else {
                        if (starttimeHr == null) {
                            starttimeHr = "00";
                        }
                        if (starttimeMin == null) {
                            starttimeMin = "00";
                        }
                        if (endTimeHr == null) {
                            endTimeHr = "00";
                        }
                        if (endTimeMin == null) {
                            endTimeMin = "00";
                        }
                        String time = starttimeHr + ":" + starttimeMin + "-" + endTimeHr + ":" + endTimeMin + "-" + "00" + ":"
                                + "00" + "-" + "00" + ":" + "00";
                        deviceSetting.setLongsit_intervals(Intervalstime);
                        MyLog.e(TAG, "久坐提醒的time是" + time);
                        deviceSetting.setLongsit_time(time);
                        deviceSetting.setLongsit_vaild("1");
                        longsit_step = "60";
                        deviceSetting.setLongsit_step(60 + "");
                        LocalUserSettingsToolkits.updateLocalSetting(NotificationSettingActivity.this,
                                deviceSetting);
                        if (provider.isConnectedAndDiscovered()) {
                            MyLog.e(TAG, "发送久坐的指令了");
                            provider.SetLongSit(NotificationSettingActivity.this, DeviceInfoHelper.fromUserEntity(NotificationSettingActivity.this,
                                    userEntity));
                        } else {
                            Toast.makeText(NotificationSettingActivity.this, getString(R.string.keepthe), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    longsit_step = "0";
                    deviceSetting.setLongsit_vaild("0");
                    deviceSetting.setLongsit_step(0 + "");
                    LocalUserSettingsToolkits.updateLocalSetting(NotificationSettingActivity.this,
                            deviceSetting);
                }
            }
        });
    }

    public PopupWindow showTimePopupWindow(int showhr, int showmin) {
        View view = View.inflate(NotificationSettingActivity.this, R.layout.starttimepopupwindow, null);
        ImageView dismiss = (ImageView) view.findViewById(R.id.dismiss);
        final WheelView hrWheelView = (WheelView) view.findViewById(R.id.hr);
        hrWheelView.setItems(hrStrings);
        hrWheelView.setSeletion(showhr);
        final WheelView minWheelView = (WheelView) view.findViewById(R.id.min);
        Button okBtn = (Button) view.findViewById(R.id.okBtn);
        TextView text = (TextView) view.findViewById(R.id.text);
        minWheelView.setItems(minStrings);
        minWheelView.setSeletion(showmin);
        final PopupWindow popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
//        popupWindow.setClippingEnabled(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0xffD3D3D3));
        popupWindow.showAtLocation(totalView, Gravity.BOTTOM, 0, 0);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (timeType) {
                    case STARTSEDENTARY:
                        String time = starttimeHr + ":" + starttimeMin + "-" + endTimeHr + ":" + endTimeMin + "-" + "00" + ":"
                                + "00" + "-" + "00" + ":" + "00";
                        deviceSetting.setLongsit_time(time);
                        deviceSetting.setLongsit_step(longsit_step);
                        deviceSetting.setLongsit_intervals(Integer.parseInt(longsit_vaild));
                        starttimeView.setText(starttimeHr+":"+starttimeMin);
                        LocalUserSettingsToolkits.updateLocalSetting(NotificationSettingActivity.this,
                                deviceSetting);
                        break;
                    case ENDSEDENTARY:
                        String time1 = starttimeHr + ":" + starttimeMin + "-" + endTimeHr + ":" + endTimeMin + "-" + "00" + ":"
                                + "00" + "-" + "00" + ":" + "00";
                        deviceSetting.setLongsit_time(time1);
                        deviceSetting.setLongsit_step(longsit_step);
                        deviceSetting.setLongsit_intervals(Integer.parseInt(longsit_vaild));
                        endtimeTextview.setText(endTimeHr+":"+endTimeMin);
                        LocalUserSettingsToolkits.updateLocalSetting(NotificationSettingActivity.this,
                                deviceSetting);

                        break;
                }
                popupWindow.dismiss();
            }
        });
        if (timeType == STARTSEDENTARY || timeType == ENDSEDENTARY) {
            text.setText(R.string.sendentarynotif);
        } else if (timeType == CLOCKONE || timeType == CLOCKTWO || timeType == CLOCKTHREE || timeType == CLOCKFOUR) {
            text.setText(R.string.alarmclock);
        } else {
            text.setText(R.string.timersetting);
        }
        hrWheelView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                switch (timeType) {
                    case STARTSEDENTARY:
                        starttimeHr = item;
                        starttimeHr = starttimeHr.length() == 0 ? "00" : item;
                        break;
                    case ENDSEDENTARY:
                        endTimeHr = item;
                        endTimeHr = endTimeHr.length() == 0 ? "00" : item;
                        break;
                    case CLOCKONE:
                        MyLog.e(TAG, item + "-----------clockonedehr");
                        clockoneHr = item;
                        edit.putString("clockoneHr", clockoneHr);
                        break;
                    case CLOCKTWO:
                        clocktwoHr = item;
                        break;
                    case CLOCKTHREE:
                        clockthreeHr = item;
                        break;
                    case CLOCKFOUR:
                        clockfourHr = item;
                        break;
                    case TIMESETTINGONE:
                        timeoneHr = item;
                        break;
                    case TIMESETTINGTWO:
                        timetwoHr = item;
                        break;
                    case TIMESETTINGTHREE:
                        timethreeHr = item;
                        break;
                    case TIMESETTINGFOUR:
                        timefourHr = item;
                        break;


                }
            }
        });
        minWheelView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                switch (timeType) {
                    case STARTSEDENTARY:
                        starttimeMin = item;
                        starttimeMin = starttimeMin.length() == 0 ? "00" : item;
                        break;
                    case ENDSEDENTARY:
                        endTimeMin = item;
                        endTimeMin = endTimeMin.length() == 0 ? "00" : item;
                        break;
                    case CLOCKONE:
                        MyLog.e(TAG, clockoneMin + "------------");
                        clockoneMin = clockoneMin.length() == 0 ? "00" : item;
                        break;
                    case CLOCKTWO:
                        clocktwoMin = item;
                        break;
                    case CLOCKTHREE:
                        clockthreeMin = item;
                        break;
                    case CLOCKFOUR:
                        clockfourMin = item;
                        break;
                    case TIMESETTINGONE:
                        timeoneMin = item;
                        break;

                }

            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                switch (timeType) {
                    case CLOCKONE:
                        String minString = "00";
                        if (!minWheelView.getSeletedItem().equals("")) {
                            int i = Integer.parseInt(minWheelView.getSeletedItem()) + 1;
                            minString = i < 10 ? "0" + i : i + "";
                        }
                        String hrString = "00";
                        if (!hrWheelView.getSeletedItem().equals("")) {
                            int i = Integer.parseInt(hrWheelView.getSeletedItem()) + 1;
                            hrString = i < 10 ? "0" + i : i + "";
                        }
                        clock1.setText(hrString + ":" + minString);
                        break;
                    case CLOCKTWO:
                        String minString2 = "00";
                        if (!minWheelView.getSeletedItem().equals("")) {
                            int i = Integer.parseInt(minWheelView.getSeletedItem()) + 1;
                            minString2 = i < 10 ? "0" + i : i + "";
                        }
                        String hrString2 = "00";
                        if (!hrWheelView.getSeletedItem().equals("")) {
                            int i = Integer.parseInt(hrWheelView.getSeletedItem()) + 1;
                            hrString2 = i < 10 ? "0" + i : i + "";
                        }
                        clock2.setText(hrString2 + ":" + minString2);
                        break;
                    case CLOCKTHREE:
                        String minString3 = "00";
                        if (!minWheelView.getSeletedItem().equals("")) {
                            int i = Integer.parseInt(minWheelView.getSeletedItem()) + 1;
                            minString3 = i < 10 ? "0" + i : i + "";
                        }
                        String hrString3 = "00";
                        if (!hrWheelView.getSeletedItem().equals("")) {
                            int i = Integer.parseInt(hrWheelView.getSeletedItem()) + 1;
                            hrString3 = i < 10 ? "0" + i : i + "";
                        }
                        clock3.setText(hrString3 + ":" + minString3);
                        break;
                    case CLOCKFOUR:
                        String minString4 = "00";
                        if (!minWheelView.getSeletedItem().equals("")) {
                            int i = Integer.parseInt(minWheelView.getSeletedItem()) + 1;
                            minString4 = i < 10 ? "0" + i : i + "";
                        }
                        String hrString4 = "00";
                        if (!hrWheelView.getSeletedItem().equals("")) {
                            int i = Integer.parseInt(hrWheelView.getSeletedItem()) + 1;
                            hrString4 = i < 10 ? "0" + i : i + "";
                        }
                        clock4.setText(hrString4 + ":" + minString4);
                        break;
                    case TIMESETTINGONE:
                        timeSetting1.setText(timeoneHr + ":" + timeoneMin);
                        break;

                }
            }
        });
        return popupWindow;
    }

    private void initMessagePopupWindow() {
        View view = layoutInflater.inflate(R.layout.messagepopupwindow, null);
        ImageView dismiss = (ImageView) view.findViewById(R.id.dismiss);
        final PopupWindow popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
//        popupWindow.setClippingEnabled(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0xffD3D3D3));
        popupWindow.showAtLocation(totalView, Gravity.BOTTOM, 0, 0);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        final SharedPreferences messagenotification = getSharedPreferences("Messagenotification", MODE_PRIVATE);
        boolean switchPhonecallBoolean = messagenotification.getBoolean("switchPhonecall", true);
        boolean switchTextmessageBoolean = messagenotification.getBoolean("switchTextmessage", true);
        boolean switchEmailBoolean = messagenotification.getBoolean("switchEmail", true);
        boolean switchMessagingBoolean = messagenotification.getBoolean("switchMessagingapps", true);

        Switch switchPhonecall = (Switch) view.findViewById(R.id.switchPhoneCall);
        switchPhonecall.setChecked(switchPhonecallBoolean);
        phonecall = switchPhonecallBoolean ? 1 : 0;
        switchPhonecall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    phonecall = 1;
                } else {
                    phonecall = 0;
                }
            }
        });
        Switch switchTextmessage = (Switch) view.findViewById(R.id.switchTextmessage);
        switchTextmessage.setChecked(switchTextmessageBoolean);
        SMScall = switchTextmessageBoolean ? 1 : 0;
        switchTextmessage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SMScall = 1;
                } else {
                    SMScall = 0;
                }
            }
        });
        Switch switchEmail = (Switch) view.findViewById(R.id.switchEmail);
        switchEmail.setChecked(switchEmailBoolean);
        Emailcall = switchEmailBoolean ? 1 : 0;
        switchEmail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Emailcall = 1;
                } else {
                    Emailcall = 0;
                }
            }
        });
        Switch switchapps = (Switch) view.findViewById(R.id.switchMessagingapps);
        switchapps.setChecked(switchMessagingBoolean);
        appscall = switchMessagingBoolean ? 1 : 0;
        switchapps.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    appscall = 1;
                } else {
                    appscall = 0;
                }
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                SharedPreferences messagenotification = getSharedPreferences("Messagenotification", MODE_PRIVATE);
                SharedPreferences.Editor edit = messagenotification.edit();
                edit.putBoolean("switchPhonecall", phonecall == 1);
                edit.putBoolean("switchTextmessage", SMScall == 1);
                edit.putBoolean("switchEmail", Emailcall == 1);
                edit.putBoolean("switchMessagingapps", appscall == 1);
                edit.commit();
                String notif_ = "" + phonecall + SMScall + Emailcall + appscall;
                int notif_data = Integer.parseInt(notif_, 2);
                byte[] send_data = intto2byte(notif_data);
                deviceSetting.setAncs_value(notif_data);
                LocalUserSettingsToolkits.updateLocalSetting(NotificationSettingActivity.this
                        , deviceSetting);
                if (provider.isConnectedAndDiscovered()) {
                    provider.setNotification(NotificationSettingActivity.this, send_data);
                } else {
                    MyLog.e(TAG, "消息提醒的蓝牙没连接");
                }
            }
        });
    }

    char[] charr;
    char[] array = {0, 0, 0, 0, 0};

    /**
     * int转byte
     */
    public static byte[] intto2byte(int a) {
        byte[] m = new byte[2];
        m[0] = (byte) ((0xff & a));
        m[1] = (byte) (0xff & (a >> 8));
        return m;
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
