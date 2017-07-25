package com.linkloving.dyh08.logic.UI.setting;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.android.bluetoothlegatt.BLEHandler;
import com.example.android.bluetoothlegatt.BLEProvider;
import com.example.android.bluetoothlegatt.proltrol.dto.LPDeviceInfo;
import com.google.android.gms.maps.GoogleMap;
import com.linkloving.dyh08.BleService;
import com.linkloving.dyh08.CommParams;
import com.linkloving.dyh08.MyApplication;
import com.linkloving.dyh08.R;
import com.linkloving.dyh08.RetrofitUtils.Bean.CheckFirmwareVersionReponse;
import com.linkloving.dyh08.RetrofitUtils.FirmwareRetrofitClient;
import com.linkloving.dyh08.RetrofitUtils.RetrofitApi.OADApi;
import com.linkloving.dyh08.basic.toolbar.ToolBarActivity;
import com.linkloving.dyh08.http.basic.CallServer;
import com.linkloving.dyh08.http.basic.HttpCallback;
import com.linkloving.dyh08.http.basic.NoHttpRuquestFactory;
import com.linkloving.dyh08.logic.UI.OAD.DfuService;
import com.linkloving.dyh08.logic.UI.device.DeviceActivity;
import com.linkloving.dyh08.logic.UI.main.PortalActivity;
import com.linkloving.dyh08.logic.dto.UserEntity;
import com.linkloving.dyh08.prefrences.LocalUserSettingsToolkits;
import com.linkloving.dyh08.prefrences.PreferencesToolkits;
import com.linkloving.dyh08.prefrences.devicebean.DeviceSetting;
import com.linkloving.dyh08.prefrences.devicebean.LocalInfoVO;
import com.linkloving.dyh08.utils.DeviceInfoHelper;
import com.linkloving.dyh08.utils.MyToast;
import com.linkloving.dyh08.utils.ToolKits;
import com.linkloving.dyh08.utils.logUtils.MyLog;
import com.yolanda.nohttp.Response;

import net.hockeyapp.android.metrics.MetricsManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.Provider;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import no.nordicsemi.android.dfu.DfuProgressListenerAdapter;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Daniel.Xu on 2016/10/24.
 */

public class GeneralActivity extends ToolBarActivity {
    private static String TAG = GeneralActivity.class.getSimpleName();
    @InjectView(R.id.listview)
    ListView listview;
    private LayoutInflater layoutInflater;
    private View totalView;
    private BLEProvider provider;
    private UserEntity userEntity;
    private DeviceSetting deviceSetting;
    private LocalInfoVO localDeviceInfo;
    private ProgressDialog dialog;
    private ProgressDialog dialog_connect;//下载进度
    private File file;
    private ProgressDialog progressDialog;
    private ImageView uploadImageview;
    private TextView updateTextview;

    private ProgressBar progressbar;
    private TextView progressInt;
    private AlertDialog downloadingProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tw_setting_activity);
        ButterKnife.inject(this);
        String[] strings = {getString(R.string.Goalsetting),
                getString(R.string.Searchingwearable),
                getString(R.string.unitsetting),
                getString(R.string.Selectthe)
//                getString(R.string.Communityweb),
//                getString(R.string.AutomaticHR)
                , getString(R.string.showhand)
                ,getString(R.string.batteryShow)
                ,getString(R.string.firmwareupdate)
        };
        provider = BleService.getInstance(GeneralActivity.this).getCurrentHandlerProvider();
        BLEProviderObserverAdapterImpl bleProviderObserver = new BLEProviderObserverAdapterImpl();
        provider.setBleProviderObserver(bleProviderObserver);
        userEntity = MyApplication.getInstance(getApplicationContext()).getLocalUserInfoProvider();
        deviceSetting = LocalUserSettingsToolkits.getLocalSetting(GeneralActivity.this, userEntity.getUser_id() + "");
        localDeviceInfo = PreferencesToolkits.getLocalDeviceInfo(GeneralActivity.this);
        layoutInflater = LayoutInflater.from(GeneralActivity.this);
        totalView = layoutInflater.inflate(R.layout.tw_setting_activity, null);
        Myadapter myadapter = new Myadapter(GeneralActivity.this, strings);
        listview.setAdapter(myadapter);
        View heardview = LayoutInflater.from(GeneralActivity.this).inflate(R.layout.tw_setting_heardview, null);
        TextView textHeartView = (TextView) heardview.findViewById(R.id.textHeartView);
        textHeartView.setText(R.string.General);
        listview.addHeaderView(heardview);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /**因为有头所以从1开始*/
                switch (position) {
                    case 1:
                        initGoalSettingsPopupWindow();
                        break;
                    case 2:
                        initSearchingWearablePopupWindow();
                        break;
                    case 3:
                        initUnitsetting();
                        break;
                    case 4:
                        initSelecttheMap();
                        break;
                    case 5:
//                        initAutomaticHR();
                        initShowhand();
//                        initCommunitywebsite();
                        break;
                    case 6:
                        initBatteryShowPopupwindow();
                        break;
                    case 7:
                        initUpdatePopupWindow();
                        break;

                }
            }


        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        DfuServiceListenerHelper.registerProgressListener(this, mDfuProgressListener);
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
            Toast.makeText(GeneralActivity.this,getString(R.string.user_info_update_success),Toast.LENGTH_SHORT).show();
            updateTextview.setText(getString(R.string.firmwareupdate_finish));
            uploadImageview.setImageResource(R.mipmap.update_finish);
            provider.connect();
            downloadingProgressDialog.dismiss();
        }

        @Override
        public void onError(String deviceAddress, int error, int errorType, String message) {
            super.onError(deviceAddress, error, errorType, message);
            Toast.makeText(GeneralActivity.this,getString(R.string.user_info_update_failure),Toast.LENGTH_SHORT).show();
            MyLog.e(TAG, "mDfuProgressListener" + "--onError--");
            downloadingProgressDialog.dismiss();
            provider.connect();
        }
    };

    public void onUploadClicked() {
        MyLog.e(TAG, "onUploadClicked执行了");
        AlertDialog.Builder builder = new AlertDialog.Builder(GeneralActivity.this);
        View view = LayoutInflater.from(GeneralActivity.this).inflate(R.layout.progress_dialog, null);
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


    private void initUpdatePopupWindow() {

        View view = layoutInflater.inflate(R.layout.firmupdate_popupwindow, null);
        uploadImageview = (ImageView) view.findViewById(R.id.updateImageview);
        uploadImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startUpdate();
            }
        });
        updateTextview = (TextView) view.findViewById(R.id.updateTextview);
        final PopupWindow popupWindow = getnewPopupWindow(view);
        ImageView dismiss = (ImageView) view.findViewById(R.id.dismiss);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }


    private class BLEProviderObserverAdapterImpl extends BLEHandler.BLEProviderObserverAdapter {


        @Override
        protected Activity getActivity() {
            return GeneralActivity.this;
        }

        /**********
         * BLE连接失败
         *********/
        @Override
        public void updateFor_handleConnectFailedMsg() {
            //连接失败
            MyLog.e(TAG, "updateFor_handleConnectFailedMsg");
            if (dialog_connect!=null&&dialog_connect.isShowing()) {
                dialog_connect.dismiss();
            }
        }

        @Override
        public void updateFor_handleSendDataError() {
            super.updateFor_handleSendDataError();
            if (dialog_connect!=null&&dialog_connect.isShowing()) {
                dialog_connect.dismiss();
            }
        }


        @Override
        public void updateFor_notifyForModelName(LPDeviceInfo latestDeviceInfo) {
            super.updateFor_notifyForModelName(latestDeviceInfo);
            if (dialog_connect!=null&&dialog_connect.isShowing()) {
                dialog_connect.dismiss();
            }
            downloadZip(latestDeviceInfo.modelName);
        }
    }

    private void startUpdate() {
        UserEntity userAuthedInfo = PreferencesToolkits.getLocalUserInfoForLaunch(GeneralActivity.this);
        String last_sync_device_id = userAuthedInfo.getDeviceEntity().getLast_sync_device_id();
        if (last_sync_device_id==null||last_sync_device_id.length()==0){
            Toast.makeText(GeneralActivity.this,getString(R.string.portal_main_unbound_msg),Toast.LENGTH_SHORT).show();
        }else {
            boolean networkConnected = ToolKits.isNetworkConnected(GeneralActivity.this);
            if (networkConnected){
//                downloadZip();
                BleService.getInstance(GeneralActivity.this).syncAllDeviceInfo(GeneralActivity.this);
                dialog_connect = new ProgressDialog(GeneralActivity.this);
                dialog_connect.show();
            }else {
                showOpenNetWorkDialog();
            }
        }
    }


    private void downloadZip(String modelname) {
        if (modelname==null) return;
        dialog = new ProgressDialog(GeneralActivity.this);
        dialog.setMessage(getString(R.string.getting_version_information));
        localDeviceInfo = PreferencesToolkits.getLocalDeviceInfo(GeneralActivity.this);
        int version_int = ToolKits.makeShort(localDeviceInfo.version_byte[1], localDeviceInfo.version_byte[0]);
        CallServer.getRequestInstance().add(GeneralActivity.this, false,
                CommParams.HTTP_OAD, NoHttpRuquestFactory.creat_New_OAD_Request(modelname
                        ,version_int), newHttpCallback);
    }


    private HttpCallback<String> newHttpCallback = new HttpCallback<String>() {
        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {
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
                        MyToast.show(GeneralActivity.this, getString(R.string.bracelet_oad_version_top), Toast.LENGTH_LONG);
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
                    MyToast.show(GeneralActivity.this, getString(R.string.bracelet_oad_version_top), Toast.LENGTH_LONG);
                }
            } else {
                dialog.dismiss();
                MyToast.show(GeneralActivity.this,  getString(R.string.bracelet_oad_version_top), Toast.LENGTH_LONG);
            }
        }
    };

    public void downLoadByRetrofit(String model_name, String file_name, int version_int, final String saveFileName, final boolean OADDirect) {
//        String message = getString(R.string.general_uploadingnewfirmware);
        dialog_connect = new ProgressDialog(GeneralActivity.this);
        dialog_connect.setCancelable(false);
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
                        file = getTempFile(GeneralActivity.this, saveFileName);
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
                Toast.makeText(GeneralActivity.this, getString(R.string.bracelet_down_file_fail), Toast.LENGTH_SHORT).show();
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


    private void showOpenNetWorkDialog() {
        new AlertDialog.Builder(GeneralActivity.this)
                .setMessage(R.string.connect_network)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }


    private void initBatteryShowPopupwindow() {
        View view = layoutInflater.inflate(R.layout.battery_show_popupwindow, null);
        ImageView battery = (ImageView) view.findViewById(R.id.battery_imageview);
        int  batteryNumber = localDeviceInfo.battery;
        if (batteryNumber>=100){
            battery.setBackgroundResource(R.mipmap.battary100);
        }else if (batteryNumber>=90){
            battery.setBackgroundResource(R.mipmap.battary90);
        }else if (batteryNumber>=80){
            battery.setBackgroundResource(R.mipmap.battary80);
        }else if (batteryNumber>=70){
            battery.setBackgroundResource(R.mipmap.battary70);
        }else if (batteryNumber>=60){
            battery.setBackgroundResource(R.mipmap.battary60);
        }else if (batteryNumber>=50){
            battery.setBackgroundResource(R.mipmap.battary50);
        }else if (batteryNumber>=40){
            battery.setBackgroundResource(R.mipmap.battary40);
        }else if (batteryNumber>=30){
            battery.setBackgroundResource(R.mipmap.battary30);
        }else if (batteryNumber>=20){
            battery.setBackgroundResource(R.mipmap.battary20);
        }else if (batteryNumber>=10){
            battery.setBackgroundResource(R.mipmap.battary10);
        }else {
            battery.setBackgroundResource(R.mipmap.battary0);
        }
        final PopupWindow popupWindow = getnewPopupWindow(view);
        ImageView dismiss = (ImageView) view.findViewById(R.id.dismiss);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

    }

    private void initShowhand() {
        View view = layoutInflater.inflate(R.layout.showhandpopupwindow, null);
        ImageView dismiss = (ImageView) view.findViewById(R.id.dismiss);
        final PopupWindow popupWindow = getnewPopupWindow(view);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        byte showhand = deviceSetting.getShowhand();
        Switch showhandSwitch = (Switch) view.findViewById(R.id.showhand);
        if (showhand == (byte) 0x01) {
            showhandSwitch.setChecked(true);
        } else {
            showhandSwitch.setChecked(false);
        }
        showhandSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                deviceSetting.setShowhand((byte) (isChecked ? 0x01 : 0x00));
                LocalUserSettingsToolkits.updateLocalSetting(GeneralActivity.this, deviceSetting);
                if (provider.isConnectedAndDiscovered()) {
//                    provider.setIndexShowhand(GeneralActivity.this, DeviceInfoHelper.fromUserEntity(GeneralActivity.this,
//                            userEntity));
                    if (isChecked) {
                        provider.setIndexShowhand(GeneralActivity.this);
                    } else {
                        provider.setIndexcloseShowhand(GeneralActivity.this);
                    }
                } else {
                    Toast.makeText(GeneralActivity.this, getString(R.string.keepthe), Toast.LENGTH_SHORT).show();
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

    private void initAutomaticHR() {
        View view = layoutInflater.inflate(R.layout.automatichrpopupwindow, null);
        final Switch realtime = (Switch) view.findViewById(R.id.realtime);
        final Switch wearable = (Switch) view.findViewById(R.id.wearable);
        boolean handRingSet = PreferencesToolkits.getHandRingSet(GeneralActivity.this);
        boolean heartrateSync = PreferencesToolkits.getHeartrateSync(GeneralActivity.this);
        realtime.setChecked(heartrateSync);
        wearable.setChecked(handRingSet);
        ImageView dismiss = (ImageView) view.findViewById(R.id.dismiss);
        final PopupWindow popupWindow = getnewPopupWindow(view);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popupWindow.dismiss();
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                PreferencesToolkits.setHandRingSet(GeneralActivity.this, wearable.isChecked());
                PreferencesToolkits.setHeartrateSync(GeneralActivity.this, realtime.isChecked());
            }
        });
        realtime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (provider.isConnectedAndDiscovered()) {
                    if (isChecked) {
                        provider.setHeartrateSync(GeneralActivity.this);
                    } else {
                        provider.setCloseHeartrateSync(GeneralActivity.this);
                    }
                } else {
                    Toast.makeText(GeneralActivity.this, getString(R.string.keepthe), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void initCommunitywebsite() {
        View view = layoutInflater.inflate(R.layout.communitywebpopupwindow, null);
        ImageView dismiss = (ImageView) view.findViewById(R.id.dismiss);
        final PopupWindow popupWindow = getnewPopupWindow(view);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    private void initSelecttheMap() {
        View view = layoutInflater.inflate(R.layout.selectthemappopupwindow, null);
        ImageView dismiss = (ImageView) view.findViewById(R.id.dismiss);
        final PopupWindow popupWindow = getnewPopupWindow(view);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        final RadioButton google = (RadioButton) view.findViewById(R.id.google);
        final RadioButton baidu = (RadioButton) view.findViewById(R.id.baidu);
        if (PreferencesToolkits.getMapSelect(GeneralActivity.this)) {
            google.setBackgroundColor(0xFFfbc400);
            baidu.setBackgroundColor(0xffF5F5F5);
        } else {
            baidu.setBackgroundColor(0xFFfbc400);
            google.setBackgroundColor(0xffF5F5F5);
        }
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferencesToolkits.setMapSelect(GeneralActivity.this,true);
                google.setBackgroundColor(0xFFfbc400);
                baidu.setBackgroundColor(0xffF5F5F5);
            }
        });
        baidu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferencesToolkits.setMapSelect(GeneralActivity.this,false);
                baidu.setBackgroundColor(0xFFfbc400);
                google.setBackgroundColor(0xffF5F5F5);
            }
        });
    }

    private void initUnitsetting() {
        View view = layoutInflater.inflate(R.layout.unitsettingpopupwindow, null);
        final Button metric = (Button) view.findViewById(R.id.metric);
        final Button britsh = (Button) view.findViewById(R.id.britsh);
        int localSettingUnitInfo = PreferencesToolkits.getLocalSettingUnitInfo(GeneralActivity.this);
        if (localSettingUnitInfo == ToolKits.UNIT_GONG) {
            metric.setBackgroundColor(0xFFfbc400);
            britsh.setBackgroundColor(0xffF5F5F5);
        } else {
            britsh.setBackgroundColor(0xFFfbc400);
            metric.setBackgroundColor(0xffF5F5F5);
        }
        metric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferencesToolkits.setLocalSettingUnitInfo(GeneralActivity.this, ToolKits.UNIT_GONG);
                metric.setBackgroundColor(0xFFfbc400);
                britsh.setBackgroundColor(0xffF5F5F5);
                if (provider.isConnectedAndDiscovered()) {
                    LPDeviceInfo lpDeviceInfo = new LPDeviceInfo();
                    lpDeviceInfo.unitSetting=0x00;
                    provider.setUnitSetting(GeneralActivity.this,lpDeviceInfo);
                } else {
                    Toast.makeText(GeneralActivity.this, getString(R.string.keepthe), Toast.LENGTH_SHORT).show();
                }
            }
        });
        britsh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferencesToolkits.setLocalSettingUnitInfo(GeneralActivity.this, ToolKits.UNIT_YING);
                britsh.setBackgroundColor(0xFFfbc400);
                metric.setBackgroundColor(0xffF5F5F5);
                if (provider.isConnectedAndDiscovered()) {
                    LPDeviceInfo lpDeviceInfo = new LPDeviceInfo();
                    lpDeviceInfo.unitSetting=0x01;
                    provider.setUnitSetting(GeneralActivity.this,lpDeviceInfo);
                } else {
                    Toast.makeText(GeneralActivity.this, getString(R.string.keepthe), Toast.LENGTH_SHORT).show();
                }
            }
        });
        ImageView dismiss = (ImageView) view.findViewById(R.id.dismiss);
        final PopupWindow popupWindow = getnewPopupWindow(view);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    private void initSearchingWearablePopupWindow() {
        if (provider.isConnectedAndDiscovered()) {
            provider.SetBandRing(GeneralActivity.this);
        } else {
            Toast.makeText(GeneralActivity.this, getString(R.string.keepthe), Toast.LENGTH_SHORT).show();
        }
        View view = layoutInflater.inflate(R.layout.searchingwearablepopupwindow, null);
        ImageView dismiss = (ImageView) view.findViewById(R.id.dismiss);
        final PopupWindow popupWindow = getnewPopupWindow(view);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    private void initGoalSettingsPopupWindow() {
        View view = layoutInflater.inflate(R.layout.goalsettingpopupwindow, null);
        final SeekBar stepsSeekbar = (SeekBar) view.findViewById(R.id.stepsSeekbar);
        final TextView distancesTextview = (TextView) view.findViewById(R.id.distances_textView);
        final TextView caloriesTextview = (TextView) view.findViewById(R.id.calories_textView);
        ImageView dismiss = (ImageView) view.findViewById(R.id.dismiss);
        final TextView stepsTextview = (TextView) view.findViewById(R.id.steps_textView);
        String stepGogalnfo = PreferencesToolkits.getGoalInfo(GeneralActivity.this, PreferencesToolkits.KEY_GOAL_STEP);
        if (!"".equals(stepGogalnfo)) {
            stepsTextview.setText(stepGogalnfo);
            stepsSeekbar.setProgress(Integer.parseInt(stepGogalnfo) - 2000);
        }

        stepsSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int i = (progress + 10000) / 1000;
                i = i * 1000;
                stepsTextview.setText(i + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        SeekBar distanceSeekbar = (SeekBar) view.findViewById(R.id.DistanceBar);
        String distanceGogalnfo = PreferencesToolkits.getGoalInfo(GeneralActivity.this, PreferencesToolkits.KEY_GOAL_DISTANCE);
        if (!"".equals(distanceGogalnfo)) {
            distancesTextview.setText(distanceGogalnfo);
            distanceSeekbar.setProgress(Integer.parseInt(distanceGogalnfo));
        }
        distanceSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                distancesTextview.setText((progress + 1) + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        SeekBar caloriesSeekbar = (SeekBar) view.findViewById(R.id.caloriesBar);
        String caloriesGogalnfo = PreferencesToolkits.getGoalInfo(GeneralActivity.this, PreferencesToolkits.KEY_GOAL_CAL);
        if (!"".equals(caloriesGogalnfo)) {
            caloriesTextview.setText(caloriesGogalnfo);
            caloriesSeekbar.setProgress(Integer.parseInt(caloriesGogalnfo) - 1000);
        }
        caloriesSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int i = (progress + 1000) / 100;
                i = i * 100;
                if (i == 10000)
                    i = 9900;
                caloriesTextview.setText(i + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        final PopupWindow popupWindow = getnewPopupWindow(view);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                PreferencesToolkits.setGoalInfo(GeneralActivity.this,
                        PreferencesToolkits.KEY_GOAL_STEP, stepsTextview.getText().toString());
                PreferencesToolkits.setGoalInfo(GeneralActivity.this,
                        PreferencesToolkits.KEY_GOAL_DISTANCE, distancesTextview.getText().toString());
                PreferencesToolkits.setGoalInfo(GeneralActivity.this,
                        PreferencesToolkits.KEY_GOAL_CAL, caloriesTextview.getText().toString());
            }
        });

    }

    private PopupWindow getnewPopupWindow(View view) {
        final PopupWindow popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
//        popupWindow.setClippingEnabled(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0xffD3D3D3));
        popupWindow.showAtLocation(totalView, Gravity.BOTTOM, 0, 0);
        return popupWindow;
    }
}
