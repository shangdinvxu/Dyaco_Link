package com.linkloving.dyh08.logic.UI.Bluetooth;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.android.bluetoothlegatt.BLEHandler;
import com.example.android.bluetoothlegatt.BLEProvider;
import com.linkloving.dyh08.BleService;
import com.linkloving.dyh08.CommParams;
import com.linkloving.dyh08.IntentFactory;
import com.linkloving.dyh08.MyApplication;
import com.linkloving.dyh08.R;
import com.linkloving.dyh08.basic.toolbar.ToolBarActivity;
import com.linkloving.dyh08.http.basic.CallServer;
import com.linkloving.dyh08.http.basic.HttpCallback;
import com.linkloving.dyh08.http.basic.NoHttpRuquestFactory;
import com.linkloving.dyh08.http.data.DataFromServer;
import com.linkloving.dyh08.logic.UI.setting.NotificationSettingActivity;
import com.linkloving.dyh08.logic.dto.UserEntity;
import com.linkloving.dyh08.notify.NotificationService;
import com.linkloving.dyh08.prefrences.PreferencesToolkits;
import com.linkloving.dyh08.prefrences.devicebean.ModelInfo;
import com.linkloving.dyh08.utils.CommonUtils;
import com.linkloving.dyh08.utils.MyToast;
import com.linkloving.dyh08.utils.ToolKits;
import com.linkloving.dyh08.utils.logUtils.MyLog;
import com.yolanda.nohttp.Response;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Daniel.Xu on 2016/9/12.
 */
public class BluetoothDisconnectActivity extends ToolBarActivity {
    private static final String TAG = BluetoothActivity.class.getSimpleName();
    @InjectView(R.id.blue_middle_changeIV)
    ImageView blueMiddleChangeIV;
    @InjectView(R.id.cancel)
    Button cancel;
    @InjectView(R.id.confirm)
    Button confirm;
    private UserEntity userEntity;
    private BLEProvider provider;
    private ModelInfo modelInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tw_binding_bluetooth_activity2);
        ButterKnife.inject(this);
        userEntity = MyApplication.getInstance(BluetoothDisconnectActivity.this).getLocalUserInfoProvider();
        provider = BleService.getInstance(BluetoothDisconnectActivity.this).getCurrentHandlerProvider();
        BLEProviderObserverAdapterImpl bleProviderObserver = new BLEProviderObserverAdapterImpl();
        provider.setBleProviderObserver(bleProviderObserver);
        modelInfo = PreferencesToolkits.getInfoBymodelName(BluetoothDisconnectActivity.this, userEntity.getDeviceEntity().getModel_name());
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

    @OnClick(R.id.cancel)
    void onCancel(View view) {
        finish();
        IntentFactory.startPortalActivityIntent(BluetoothDisconnectActivity.this);
    }

    @OnClick(R.id.confirm)
    void onConfirm(View view) {
        MyLog.e("BluetoothActivity",provider.isConnectedAndDiscovered()+"provider.isConnectedAndDiscovered()");
        UserEntity userAuthedInfo = PreferencesToolkits.getLocalUserInfoForLaunch(BluetoothDisconnectActivity.this);
        String last_sync_device_id = userAuthedInfo.getDeviceEntity().getLast_sync_device_id();
        if (last_sync_device_id==null||last_sync_device_id.length()==0){
        }else{
            if(provider.isConnectedAndDiscovered()) {
                provider.unBoundDevice(BluetoothDisconnectActivity.this);
            }else {
                UserEntity userEntity = MyApplication.getInstance(BluetoothDisconnectActivity.this).getLocalUserInfoProvider();
                //设备号置空
                userEntity.getDeviceEntity().setLast_sync_device_id(null);
                //设备类型置空
                userEntity.getDeviceEntity().setDevice_type(0);
                //*******模拟断开   不管有没有连接 先执行这个再说
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
                BleService.getInstance(BluetoothDisconnectActivity.this).releaseBLE();
                provider.clearProess();
                provider.setCurrentDeviceMac(null);
                provider.setmBluetoothDevice(null);
                provider.resetDefaultState();
                ToolKits.showCommonTosat(BluetoothDisconnectActivity.this, false, ToolKits.getStringbyId(BluetoothDisconnectActivity.this, R.string.unbound_success), Toast.LENGTH_SHORT);
                finish();
                IntentFactory.startPortalActivityIntent(BluetoothDisconnectActivity.this);
            }
        }

    }
    private HttpCallback<String> httpCallback = new HttpCallback<String>() {
        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {
        }
        @Override
        public void onSucceed(int what, Response<String> response) {
            DataFromServer dataFromServer = JSON.parseObject(response.get(), DataFromServer.class);

            String value = dataFromServer.getReturnValue().toString();
            MyLog.e(TAG, "respone.get()" + response.get());
            switch (what) {

                case CommParams.HTTP_UNBUND:
                    MyLog.e(TAG, "====HTTP_UNBUND====response=" + response.get());
                    //不做处理
                    if (dataFromServer.getErrorCode() == 1) {
                        if (provider.isConnectedAndDiscovered()) {
                            //连接才去下这个指令
                            provider.unBoundDevice(BluetoothDisconnectActivity.this);
                        }
                        //曾经出现过modelInfo为null的情况(服务器没有存这些信息)
                        if(modelInfo!=null){
                            if(modelInfo.getAncs()!=0){ //有消息提醒
                                startActivity(new Intent(NotificationService.ACTION_NOTIFICATION_LISTENER_SETTINGS));
                                ToolKits.showCommonTosat(BluetoothDisconnectActivity.this, true, ToolKits.getStringbyId(BluetoothDisconnectActivity.this, R.string.unbound_success_msg), Toast.LENGTH_LONG);
                            }else{
                                ToolKits.showCommonTosat(BluetoothDisconnectActivity.this, true, ToolKits.getStringbyId(BluetoothDisconnectActivity.this, R.string.unbound_success), Toast.LENGTH_LONG);
                            }
                        }
                        UserEntity userEntity = MyApplication.getInstance(BluetoothDisconnectActivity.this).getLocalUserInfoProvider();
                        //设备号置空
                        userEntity.getDeviceEntity().setLast_sync_device_id(null);
                        //设备类型置空
                        userEntity.getDeviceEntity().setDevice_type(0);
                        MyLog.e(TAG, "====HTTP_UNBUND====userEntity=" + userEntity.toString());
                        //*******模拟断开   不管有没有连接 先执行这个再说
                        BleService.getInstance(BluetoothDisconnectActivity.this).releaseBLE();
//                        finish();
                    } else if (dataFromServer.getErrorCode() == 10004) {
                        MyToast.show(BluetoothDisconnectActivity.this, BluetoothDisconnectActivity.this.getString(R.string.debug_device_unbound_failed), Toast.LENGTH_LONG);
                    }
                    break;
            }
        }
    };

    private class BLEProviderObserverAdapterImpl extends BLEHandler.BLEProviderObserverAdapter {

        @Override
        protected Activity getActivity() {
            return BluetoothDisconnectActivity.this;
        }

        @Override
        public void updateFor_notifyForDeviceUnboundSucess_D() {
            super.updateFor_notifyForDeviceUnboundSucess_D();
            MyLog.e(TAG,"解绑执行了");
            UserEntity userEntity = MyApplication.getInstance(BluetoothDisconnectActivity.this).getLocalUserInfoProvider();
            //设备号置空
            userEntity.getDeviceEntity().setLast_sync_device_id(null);
            //设备类型置空
            userEntity.getDeviceEntity().setDevice_type(0);
            //*******模拟断开   不管有没有连接 先执行这个再说
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            BleService.getInstance(BluetoothDisconnectActivity.this).releaseBLE();
            provider.clearProess();
            provider.setCurrentDeviceMac(null);
            provider.setmBluetoothDevice(null);
            provider.resetDefaultState();
            ToolKits.showCommonTosat(BluetoothDisconnectActivity.this, false, ToolKits.getStringbyId(BluetoothDisconnectActivity.this, R.string.unbound_success), Toast.LENGTH_SHORT);
            finish();
            IntentFactory.startPortalActivityIntent(BluetoothDisconnectActivity.this);
        }
    }

    }
