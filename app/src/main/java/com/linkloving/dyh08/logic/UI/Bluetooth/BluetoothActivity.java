package com.linkloving.dyh08.logic.UI.Bluetooth;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.view.menu.MenuView;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.android.bluetoothlegatt.BLEHandler;
import com.example.android.bluetoothlegatt.BLEListHandler;
import com.example.android.bluetoothlegatt.BLEListProvider;
import com.example.android.bluetoothlegatt.BLEProvider;
import com.example.android.bluetoothlegatt.proltrol.dto.LPDeviceInfo;
import com.example.android.bluetoothlegatt.proltrol.dto.LpHeartrateData;
import com.linkloving.dyh08.BleService;
import com.linkloving.dyh08.IntentFactory;
import com.linkloving.dyh08.MyApplication;
import com.linkloving.dyh08.R;
import com.linkloving.dyh08.basic.toolbar.ToolBarActivity;
import com.linkloving.dyh08.logic.UI.main.PortalActivity;
import com.linkloving.dyh08.logic.UI.main.materialmenu.CommonAdapter;
import com.linkloving.dyh08.logic.dto.UserEntity;
import com.linkloving.dyh08.prefrences.PreferencesToolkits;
import com.linkloving.dyh08.utils.CommonUtils;
import com.linkloving.dyh08.utils.CutString;
import com.linkloving.dyh08.utils.ToolKits;
import com.linkloving.dyh08.utils.logUtils.MyLog;
import com.yolanda.nohttp.Response;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by leo.wang on 2016/7/25.
 */
public class BluetoothActivity extends ToolBarActivity {
    private static final String TAG = BluetoothActivity.class.getSimpleName();
    @InjectView(R.id.blue_middle_changeIV)
    ImageView middleChangeIV;
    @InjectView(R.id.bluetooth_list)
    ListView mlistview;
    @InjectView(R.id.bluetooth_next)
    ImageView btn_Next;
    private BLEProvider provider;
    public static final int RESULT_BACK = 999;
    public static final int RESULT_FAIL = 998;
    public static final int RESULT_NOCHARGE = 997;
    public static final int RESULT_DISCONNECT = 996;
    private BLEProviderObserver1 observerAdapter;
    private BLEListHandler handler;
    private BLEListProvider listProvider;
    private List<DeviceVO> macList =
            new ArrayList<DeviceVO>();
    private int selectionPostion;
    private BluetoothActivity.macListAdapterNew macListAdapterNew;
    private ImageView stateIV;
    private RotateAnimation rotateAnimation;
    public static final int sendcount_MAX = 15;
    private int sendcount = 0;
    public static final int sendcount_time = 2000;
    public String modelName = null;
    private Timer timer;

    public static final int REFRESH_BUTTON = 0x123;
    private AlertDialog progressDialog;
    private android.support.v7.app.AlertDialog.Builder builder;
    public String deviceName = null ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tw_bluetooth_activity);
        ButterKnife.inject(this);
        middleChangeIV.setVisibility(View.INVISIBLE);
        btn_Next.setVisibility(View.GONE);
        observerAdapter = new BLEProviderObserver1();
        provider = BleService.getInstance(this).getCurrentHandlerProvider();
        provider.setBleProviderObserver(observerAdapter);
        rotateAnimation = getRotateAnimation();
        handler = new BLEListHandler(BluetoothActivity.this) {
            @Override
            protected void handleData(BluetoothDevice bluetoothDevice) {
                for (DeviceVO v : macList) {
                    if (v.mac.equals(bluetoothDevice.getAddress()))
                        return;
                }
                DeviceVO vo = new DeviceVO();
                vo.mac = bluetoothDevice.getAddress();
                vo.name = bluetoothDevice.getName();
                vo.bledevice = bluetoothDevice;
                macList.add(vo);
                macListAdapterNew.notifyDataSetChanged();
            }
        };
        listProvider = new BLEListProvider(this, handler);
//        mAdapter = new macListAdapter(this, macList);
        macListAdapterNew = new macListAdapterNew();
        listProvider.scanDeviceList();
        mlistview.setDivider(null);
        mlistview.setDividerHeight(10);
        mlistview.setAdapter(macListAdapterNew);
        mlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                stateIV = (ImageView) view.findViewById(R.id.list_item_imageview);
                stateIV.setAnimation(rotateAnimation);
                stateIV.setVisibility(View.VISIBLE);
                rotateAnimation.start();
                selectionPostion = position;
                provider.setCurrentDeviceMac(macList.get(position).mac);
                provider.setmBluetoothDevice(macList.get(position).bledevice);
                provider.connect_mac(macList.get(position).mac);
                deviceName = macList.get(position).name;
                middleChangeIV.setVisibility(View.VISIBLE);
                progressDialog = new AlertDialog.Builder(BluetoothActivity.this).setMessage(R.string.connect).setCancelable(false).show();
            }
        });

        btn_Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BluetoothActivity.this, PortalActivity.class);
                startActivity(intent);
            }
        });
        builder = new android.support.v7.app.AlertDialog.Builder(BluetoothActivity.this);
    }

    public RotateAnimation getRotateAnimation() {
        RotateAnimation rotateAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setRepeatCount(2000);
        return rotateAnimation;
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

    class macListAdapterNew extends BaseAdapter {
        private TextView equipment_nameTV;
        private TextView equipment_adressTV;
        private ImageView item_stateIV;


        @Override
        public int getCount() {
            return macList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(BluetoothActivity.this).inflate(R.layout.bluetooth_listviewitem, null);
            equipment_nameTV = (TextView) view.findViewById(R.id.equipment_name);
            equipment_adressTV = (TextView) view.findViewById(R.id.equipment_adress);
            item_stateIV = (ImageView) view.findViewById(R.id.list_item_imageview);
            String name = macList.get(position).name;
            equipment_nameTV.setText(name);
            String mac = macList.get(position).mac;
            String macEndTwo = CutString.macSplitEndTwo(mac);
            equipment_adressTV.setText(macEndTwo);
            return view;
        }
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (provider != null) {
            if (provider.getBleProviderObserver() == null) {
                provider.setBleProviderObserver(observerAdapter);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing())
        {
            progressDialog.dismiss();
        }
    }

    public void finish() {
        super.finish();
        listProvider.stopScan();
        provider = BleService.getInstance(this).getCurrentHandlerProvider();
        if (provider != null)
            provider.setBleProviderObserver(null);
    }

    class DeviceVO {
        public String mac;
        public String name;
        public BluetoothDevice bledevice;

    }

    Runnable butttonRunnable = new Runnable() {
        @Override
        public void run() {
            Message msg = new Message();
            msg.what = REFRESH_BUTTON;
            boundhandler.sendMessage(msg);
        }

        ;
    };

    Runnable boundRunnable = new Runnable() {
        @Override
        public void run() {
            Message msg = new Message();
            msg.what = 0x333;
            boundhandler.sendMessage(msg);
        }

        ;
    };
    Handler boundhandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x333:
                    provider.requestbound_recy(BluetoothActivity.this);
                    break;
//                case REFRESH_BUTTON:
//                    button_txt[0] = button_txt_count;
//                    Log.e(TAG, button_txt_count + "");
//                    String second_txt = MessageFormat.format(getString(R.string.bound_scan_sqr), button_txt);
//                    boundBtn.setText(second_txt);
//                    if (button_txt_count == 0) {
//                        if (dialog_bound != null && dialog_bound.isShowing()) {
//                            if (timer != null)
//                                timer.cancel();
//                            dialog_bound.dismiss();
//                        }
//                        BleService.getInstance(BandListActivity.this).releaseBLE();
//                        setResult(RESULT_FAIL);
//                        finish();
//                    }
//                    break;
            }
        }

        ;
    };


    private class BLEProviderObserver1 extends BLEHandler.BLEProviderObserverAdapter {

        @Override
        protected Activity getActivity() {
            return BluetoothActivity.this;
        }

        @Override
        public void updateFor_handleNotEnableMsg() {
            super.updateFor_handleNotEnableMsg();
            MyLog.e(TAG, "updateFor_handleNotEnableMsg");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            BluetoothActivity.this.startActivityForResult(enableBtIntent, BleService.REQUEST_ENABLE_BT);
        }

        @Override
        public void updateFor_handleSendDataError() {
            MyLog.e(TAG, "updateFor_handleSendDataError");
            super.updateFor_handleSendDataError();
        }

        @Override
        public void updateFor_handleConnectLostMsg() {
            Log.e("BluetoothActivity", "updateFor_handleConnectLostMsg");
            listProvider.stopScan();
//            provider.connect_mac(provider.getCurrentDeviceMac());
//
            provider.clearProess();
            provider.setCurrentDeviceMac(null);
            provider.setmBluetoothDevice(null);
            provider.resetDefaultState();
            setResult(RESULT_FAIL);
            Toast.makeText(BluetoothActivity.this, "绑定失败", Toast.LENGTH_SHORT).show();
            finish();
        }

        @Override
        public void updateFor_handleConnectFailedMsg() {
            super.updateFor_handleConnectFailedMsg();
            MyLog.e(TAG, "updateFor_handleConnectFailedMsg");
            provider.release();
            provider.setCurrentDeviceMac(null);
            provider.setmBluetoothDevice(null);
            provider.resetDefaultState();
            setResult(RESULT_FAIL);
            finish();
        }

        @Override
        public void updateFor_handleConnectSuccessMsg() {
            MyLog.e(TAG, "updateFor_handleConnectSuccessMsg");
            listProvider.stopScan();
            Log.e("BluetoothActivity", "连接成功");
            try {
                new Thread().sleep(BleService.TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            BleService.getInstance(BluetoothActivity.this).syncAllDeviceInfo(BluetoothActivity.this);
//
        }

        @Override
        public void updateFor_notifyFor0x13ExecSucess_D(LPDeviceInfo latestDeviceInfo) {
            super.updateFor_notifyFor0x13ExecSucess_D(latestDeviceInfo);
            MyLog.e(TAG, "updateFor_notifyFor0x13ExecSucess_D");
            if (latestDeviceInfo != null && latestDeviceInfo.recoderStatus == 5) {
                Log.e("BluetoothActivity", "用户非法");
                Toast.makeText(BluetoothActivity.this, "设备已经被其他用户绑定", Toast.LENGTH_SHORT).show();
                provider.release();
                provider.setCurrentDeviceMac(null);
                provider.setmBluetoothDevice(null);
                provider.resetDefaultState();
                provider.clearProess();
                finish();
            }else if(latestDeviceInfo != null && latestDeviceInfo.recoderStatus == 6){
                Log.e("BluetoothActivity", "设备未授权");
                Toast.makeText(BluetoothActivity.this, "设备未授权", Toast.LENGTH_SHORT).show();
                provider.release();
                provider.setCurrentDeviceMac(null);
                provider.setmBluetoothDevice(null);
                provider.resetDefaultState();
                provider.clearProess();
                finish();
            }else if (latestDeviceInfo!=null&&latestDeviceInfo.recoderStatus==66){
                   if (builder!=null&&!builder.create().isShowing()) {
                       builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               provider.unBoundDevice(BluetoothActivity.this);
                               provider.requestbound_fit(BluetoothActivity.this);
                           }
                       }).setMessage(getString(R.string.Need_format))
                               .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {
                                       provider.disConnect();
                                       if (progressDialog != null && progressDialog.isShowing()){
                                           progressDialog.dismiss();
                                       }

                                   }
                               })
                               .setCancelable(false)
                               .show();
                   }
            } else {
                provider.requestbound_fit(BluetoothActivity.this);
            }

        }


        //非法用户
        @Override
        public void notifyForInvaildUser() {
            super.notifyForInvaildUser();
            Log.e("BluetoothActivity", "notifyForInvaildUser");
            new AlertDialog.Builder(BluetoothActivity.this)
                    .setTitle(R.string.portal_main_gobound)
                    .setMessage(R.string.portal_main_mustbund)
                    //
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            provider.unBoundDevice(BluetoothActivity.this);
                            provider.connect();
                        }
                    })
                    .setNegativeButton(R.string.general_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            provider.disConnect();
//                            provider.release();
//                            provider.setCurrentDeviceMac(null);
//                            provider.setmBluetoothDevice(null);
//                            provider.resetDefaultState();
//                            setResult(RESULT_FAIL);
//                            finish();
                        }
                    }).create().show();
        }

        @Override
        public void updateFor_notifyForDeviceUnboundSucess_D() {
            super.updateFor_notifyForDeviceUnboundSucess_D();
            Log.e("BluetoothActivity", "清除设备信息/解绑成功");

        }




        @Override
        public void updateFor_BoundNoCharge() {
            super.updateFor_BoundNoCharge();
            Log.e("BluetoothActivity", "updateFor_BoundNoCharge");
            setResult(RESULT_NOCHARGE);
            finish();
        }

        @Override
        public void updateFor_BoundContinue() {
            super.updateFor_BoundContinue();
            Log.e("BluetoothActivity", "updateFor_BoundContinue");
            if (timer == null) {
                    timer = new Timer(); // 每1s更新一下
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            boundhandler.post(butttonRunnable);
//                            MyLog.e(TAG, "Timer开始了");
                        }
                    }, 0, 1000);
                }
            if (sendcount < sendcount_MAX) {
                boundhandler.postDelayed(boundRunnable, sendcount_time);
                sendcount++;
            }else {
                Log.e("BandListActivity", "已经发送超出15次");
                provider.clearProess();
                BleService.getInstance(BluetoothActivity.this).releaseBLE();
                setResult(RESULT_FAIL);
                finish();
            }
        }

        @Override
        public void updateFor_BoundSucess() {
            Log.e("BluetoothActivity", "updateFor_BoundSucess");
            provider.SetDeviceTime(BluetoothActivity.this);
            //获取成功
            startBound();
        }

//        @Override
//        public void updateFor_handleSetTime() {
//            provider.getModelName(BluetoothActivity.this);
//        }

        @Override
        public void updateFor_notifyForModelName(LPDeviceInfo latestDeviceInfo) {
            Log.e("BluetoothActivity", "updateFor_notifyForModelName");
            if (latestDeviceInfo == null) {
                //未获取成功  重新获取
                provider.getModelName(BluetoothActivity.this);
            } else {
                modelName = latestDeviceInfo.modelName;
                //获取成功
                startBound();
            }
        }

        /**
         * 通知：设备绑定信息同步到服务端完成
         */
        @Override
        public void updateFor_boundInfoSyncToServerFinish(Object resultFromServer) {
            Log.e("BluetoothActivity", "updateFor_boundInfoSyncToServerFinish");
            if (resultFromServer != null) {
                if (((String) resultFromServer).equals("1")) {
                    Toast.makeText(BluetoothActivity.this, R.string.boundSuccess, Toast.LENGTH_SHORT).show();
                    provider.getModelName(BluetoothActivity.this);
                    MyLog.e(TAG,provider.getCurrentDeviceMac()+"provider.getCurrentDeviceMac()");
                    MyApplication.getInstance(BluetoothActivity.this).getLocalUserInfoProvider().getDeviceEntity().setLast_sync_device_id(provider.getCurrentDeviceMac());
                    MyApplication.getInstance(BluetoothActivity.this).getLocalUserInfoProvider().getDeviceEntity().setDevice_type(MyApplication.DEVICE_BAND);
                    BleService.getInstance(BluetoothActivity.this).syncAllDeviceInfo(BluetoothActivity.this);
                    setResult(RESULT_OK);
                    finish();

                } else if (((String) resultFromServer).equals("10024")) {
                    MyLog.e(TAG, "========绑定失败！========");
                    new android.support.v7.app.AlertDialog.Builder(BluetoothActivity.this)
                            .setTitle(getActivity().getResources().getString(R.string.general_prompt))
                            .setPositiveButton(getActivity().getResources().getString(R.string.general_ok), new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog_, int which) {
                                    dialog_.dismiss();
                                    BleService.getInstance(getActivity()).releaseBLE();
                                    setResult(RESULT_FAIL);
                                    finish();
                                }
                            })
                            .show();
                }
            } else {

                Log.e(TAG, "boundAsyncTask result is null!!!!!!!!!!!!!!!!!");
                BleService.getInstance(getActivity()).releaseBLE();
            }
        }


        @Override
        public void updateFor_BoundFail() {
            Log.e(TAG, "updateFor_BoundFail 绑定失败");
            // BluetoothActivity.this.notifyAll();
            provider.clearProess();
            Toast.makeText(BluetoothActivity.this,"绑定失败",Toast.LENGTH_SHORT).show();
//            provider.unBoundDevice(BluetoothActivity.this);
            setResult(RESULT_FAIL);
            finish();
        }

    }

    private void startBound() {
        // 绑定设备时必须保证首先从服务端取来标准UTC时间，以便给设备校时(要看看网络是否连接)
//        if (ToolKits.isNetworkConnected(BluetoothActivity.this)) {
        if (true) {
            UserEntity ue = MyApplication.getInstance(getApplicationContext()).getLocalUserInfoProvider();
            String user_id = ue.getUser_id() + "";
            if (ue != null && provider != null && provider.getCurrentDeviceMac() != null) {
              MyLog.e(TAG,"离线里面的方法执行了");
                // 将用户id 和 MAC地址交到服务端进行匹配
//                submitBoundMACToServer(user_id, provider.getCurrentDeviceMac());
                //***********************************台湾离线版本**********************************************************//
                UserEntity userEntity = MyApplication.getInstance(BluetoothActivity.this).getLocalUserInfoProvider();
                userEntity.getDeviceEntity().setLast_sync_device_id(provider.getCurrentDeviceMac());
                userEntity.getDeviceEntity().setDevice_type(MyApplication.DEVICE_BAND);
                userEntity.getDeviceEntity().setModel_name(modelName);
                userEntity.getDeviceEntity().setLast_sync_device_id2(deviceName);
                MyApplication.getInstance(BluetoothActivity.this).setLocalUserInfoProvider(userEntity);
                if (observerAdapter != null)
                    observerAdapter.updateFor_boundInfoSyncToServerFinish("1");
                //****************************************台湾离线版本****************************************************//
            } else {
                BleService.getInstance(BluetoothActivity.this).releaseBLE();
                setResult(RESULT_FAIL);
                finish();
            }
        } else {
            BleService.getInstance(BluetoothActivity.this).releaseBLE(); // 没有网络去绑定设备
            // 就断开连接
        }
    }


}
