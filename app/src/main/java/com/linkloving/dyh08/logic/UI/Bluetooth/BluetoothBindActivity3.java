package com.linkloving.dyh08.logic.UI.Bluetooth;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bluetoothlegatt.BLEHandler;
import com.example.android.bluetoothlegatt.BLEListHandler;
import com.example.android.bluetoothlegatt.BLEListProvider;
import com.example.android.bluetoothlegatt.BLEProvider;
import com.example.android.bluetoothlegatt.proltrol.dto.LPDeviceInfo;
import com.linkloving.dyh08.BleService;
import com.linkloving.dyh08.IntentFactory;
import com.linkloving.dyh08.MyApplication;
import com.linkloving.dyh08.R;
import com.linkloving.dyh08.basic.toolbar.ToolBarActivity;
import com.linkloving.dyh08.logic.UI.main.PortalActivity;
import com.linkloving.dyh08.logic.dto.UserEntity;
import com.linkloving.dyh08.prefrences.PreferencesToolkits;
import com.linkloving.dyh08.utils.CutString;
import com.linkloving.dyh08.utils.logUtils.MyLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Daniel.Xu on 2016/9/12.
 */
public class BluetoothBindActivity3 extends ToolBarActivity {
    private static final String TAG = BluetoothBindActivity3.class.getSimpleName();
    @InjectView(R.id.blue_middle_changeIV)
    ImageView middleChangeIV;
    @InjectView(R.id.bluetooth_list)
    ListView mlistview;

    @InjectView(R.id.Disconnect)
    LinearLayout btn_Next;
    @InjectView(R.id.Reconnect)
    LinearLayout btn_reconnect ;
    private BLEProvider provider;
    public static final int RESULT_BACK = 999;
    public static final int RESULT_FAIL = 998;
    public static final int RESULT_NOCHARGE = 997;
    public static final int RESULT_DISCONNECT = 996;
    private BLEProviderObserver1 observerAdapter;
    private BLEListHandler handler;
    private BLEListProvider listProvider;
    private List<DeviceVO> macList = new ArrayList<DeviceVO>();
    private int selectionPostion;
    private BluetoothBindActivity3.macListAdapterNew macListAdapterNew;
    private ImageView stateIV;
    private RotateAnimation rotateAnimation;
    public static final int sendcount_MAX = 15;
    private int sendcount = 0;
    public static final int sendcount_time = 2000;
    public  String modelName = null;

    private Timer timer;
    private AlertDialog dialog_bound;

    public static final int REFRESH_BUTTON = 0x123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tw_binding_bluetooth_activity3);
        ButterKnife.inject(this);
        middleChangeIV.setVisibility(View.INVISIBLE);
//        btn_Next.setVisibility(View.GONE);
        observerAdapter = new BLEProviderObserver1();
        provider = BleService.getInstance(this).getCurrentHandlerProvider();
        provider.setBleProviderObserver(observerAdapter);
        handler = new BLEListHandler(BluetoothBindActivity3.this) {
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
                RotateAnimation rotateAnimation = getRotateAnimation();
                stateIV.setAnimation(rotateAnimation);
                rotateAnimation.start();
                stateIV.setVisibility(View.VISIBLE);
                provider.setCurrentDeviceMac(macList.get(position).mac);
                provider.setmBluetoothDevice(macList.get(position).bledevice);
                provider.connect_mac(macList.get(position).mac);
                modelName = macList.get(position).name ;
                new AlertDialog.Builder(BluetoothBindActivity3.this).setMessage("连接中").setCancelable(false).show();
            }
        });

        btn_Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BluetoothBindActivity3.this, PortalActivity.class);
                startActivity(intent);
            }
        });
    }


    public  RotateAnimation  getRotateAnimation(){
        rotateAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
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
            View view = LayoutInflater.from(BluetoothBindActivity3.this).inflate(R.layout.bluetooth_listviewitem, null);
            equipment_nameTV = (TextView) view.findViewById(R.id.equipment_name);
            equipment_adressTV = (TextView) view.findViewById(R.id.equipment_adress);
            item_stateIV = (ImageView) view.findViewById(R.id.list_item_imageview);
            String name = macList.get(position).name;
            MyLog.e("name是", name);
            equipment_nameTV.setText(name);
            String mac = macList.get(position).mac;
            String macEndTwo = CutString.macSplitEndTwo(mac);
            equipment_adressTV.setText(macEndTwo);
            return view;
        }
    }

    /*  class macListAdapter extends CommonAdapter<DeviceVO>{
          public class ViewHolder{
              public TextView equipment_name ;
              public TextView equipment_adress ;
              public ImageView item_state ;

          }
          ViewHolder holder ;

          public macListAdapter(Context context, List<DeviceVO> list) {
              super(context, list);
          }
          @Override
          protected View noConvertView(int position, View convertView, ViewGroup parent) {
              convertView = inflater.inflate(R.layout.bluetooth_listviewitem, parent, false);
  //            holder = new ViewHolder();
  //            holder.equipment_name = (TextView) convertView.findViewById(R.id.equipment_name);
  //            holder.equipment_adress = (TextView) convertView.findViewById(R.id.equipment_adress);
  //            holder.item_state = (ImageView) convertView.findViewById(R.id.list_item_imageview);
              convertView.setTag(holder);
              return convertView;
          }
          @Override
          protected View hasConvertView(int position, View convertView, ViewGroup parent) {
              holder = (ViewHolder) convertView.getTag();
              return convertView;
          }
          @Override
          protected View initConvertView(int position, View convertView, ViewGroup parent) {
              String name = list.get(position).name;
              MyLog.e("name是",name);
  //            equipment_nameTV.setText(name);
  ////            String mac = list.get(position).mac.substring(list.get(position).mac.length() - 5, list.get(position).mac.length());
  ////            holder.equipment_adress.setText("ID:   " + removeCharAt(mac, 2));
  //            String mac = list.get(position).mac;
  //            equipment_adressTV.setText(mac);
              return convertView;
          }
      }*/
    public static String removeCharAt(String s, int pos) {
        return s.substring(0, pos) + s.substring(pos + 1);
    }


    private class BLEProviderObserver extends BLEHandler.BLEProviderObserverAdapter {

        @Override
        protected Activity getActivity() {
            return BluetoothBindActivity3.this;
        }

        @Override
        public void updateFor_handleConnectSuccessMsg() {
//            provider.requestbound_fit(BluetoothBindActivity3.this);
            UserEntity userAuthedInfo = PreferencesToolkits.getLocalUserInfoForLaunch(BluetoothBindActivity3.this);
            userAuthedInfo.getDeviceEntity().setLast_sync_device_id(macList.get(selectionPostion).mac);
            userAuthedInfo.getDeviceEntity().setModel_name(macList.get(selectionPostion).name);
            MyApplication.getInstance(BluetoothBindActivity3.this).setLocalUserInfoProvider(userAuthedInfo);
            String last_sync_device_id = userAuthedInfo.getDeviceEntity().getLast_sync_device_id();
            MyLog.e(TAG,last_sync_device_id);
            MyLog.e(TAG, macList.get(selectionPostion).mac);
//            stateIV.setVisibility(View.INVISIBLE);
            rotateAnimation.cancel();
            stateIV.setImageResource(R.mipmap.selected);
            middleChangeIV.setImageResource(R.mipmap.link);
            middleChangeIV.setVisibility(View.VISIBLE);
            btn_Next.setVisibility(View.VISIBLE);
            Toast.makeText(BluetoothBindActivity3.this, "绑定成功", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void updateFor_handleConnectLostMsg() {
            MyLog.e("BandListActivity", "断开连接");
            middleChangeIV.setImageResource(R.mipmap.nofind);
            middleChangeIV.setVisibility(View.VISIBLE);
            provider.clearProess();
//            stateIV.setVisibility(View.INVISIBLE);
            provider.setCurrentDeviceMac(null);
            provider.setmBluetoothDevice(null);
            provider.resetDefaultState();
            setResult(RESULT_DISCONNECT);
//            Toast.makeText(BluetoothBindActivity3.this, "绑定失败", Toast.LENGTH_SHORT).show();
            myfinish();
        }
    }
    public void myfinish(){
        listProvider.stopScan();
        provider = BleService.getInstance(this).getCurrentHandlerProvider();
        if (provider != null)
            provider.setBleProviderObserver(null);
    }

    public void finish() {
        super.finish();
        listProvider.stopScan();
        provider = BleService.getInstance(this).getCurrentHandlerProvider();
        if (provider != null)
            provider.setBleProviderObserver(null);

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
                    provider.requestbound_recy(BluetoothBindActivity3.this);
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
            return BluetoothBindActivity3.this;
        }

        @Override
        public void updateFor_handleNotEnableMsg() {
            super.updateFor_handleNotEnableMsg();
            MyLog.e(TAG, "updateFor_handleNotEnableMsg");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            BluetoothBindActivity3.this.startActivityForResult(enableBtIntent, BleService.REQUEST_ENABLE_BT);
        }

        @Override
        public void updateFor_handleSendDataError() {
            MyLog.e(TAG, "updateFor_handleSendDataError");
            super.updateFor_handleSendDataError();
            if (dialog_bound != null && dialog_bound.isShowing()) {
                dialog_bound.dismiss();
            }
        }

        @Override
        public void updateFor_handleConnectLostMsg() {
            Log.e("BluetoothBindActivity3", "updateFor_handleConnectLostMsg");
            listProvider.stopScan();
            if (dialog_bound != null && dialog_bound.isShowing()) {
                dialog_bound.dismiss();
            }
//            provider.connect_mac(provider.getCurrentDeviceMac());
//
            provider.clearProess();
            provider.setCurrentDeviceMac(null);
            provider.setmBluetoothDevice(null);
            provider.resetDefaultState();
            setResult(RESULT_FAIL);
            Toast.makeText(BluetoothBindActivity3.this, "绑定失败", Toast.LENGTH_SHORT).show();
            finish();
        }

        @Override
        public void updateFor_handleConnectFailedMsg() {
            super.updateFor_handleConnectFailedMsg();
            MyLog.e(TAG, "updateFor_handleConnectFailedMsg");
            if (dialog_bound != null && dialog_bound.isShowing()) {
                dialog_bound.dismiss();
            }
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
            Log.e("BluetoothBindActivity3", "连接成功");
            try {
                new Thread().sleep(BleService.TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            BleService.getInstance(BluetoothBindActivity3.this).syncAllDeviceInfo(BluetoothBindActivity3.this);
//
        }

        @Override
        public void updateFor_notifyFor0x13ExecSucess_D(LPDeviceInfo latestDeviceInfo) {
            super.updateFor_notifyFor0x13ExecSucess_D(latestDeviceInfo);
            MyLog.e(TAG, "updateFor_notifyFor0x13ExecSucess_D");
            if (latestDeviceInfo != null && latestDeviceInfo.recoderStatus == 5) {
                Log.e("BluetoothBindActivity3", "用户非法");
                Toast.makeText(BluetoothBindActivity3.this, "设备已经被其他用户绑定", Toast.LENGTH_SHORT).show();
                provider.release();
                provider.setCurrentDeviceMac(null);
                provider.setmBluetoothDevice(null);
                provider.resetDefaultState();
                provider.clearProess();
                finish();
             /*   new AlertDialog.Builder(BluetoothBindActivity3.this)
                        .setTitle(R.string.portal_main_gobound)
                        .setMessage(R.string.portal_main_mustbund)
                        //
                        .setPositiveButton(R.string.general_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                provider.unBoundDevice(BluetoothBindActivity3.this);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.general_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                if (dialog_bound != null && dialog_bound.isShowing()) {
                                    dialog_bound.dismiss();
                                }
                                provider.release();
                                provider.setCurrentDeviceMac(null);
                                provider.setmBluetoothDevice(null);
                                provider.resetDefaultState();
                                setResult(RESULT_FAIL);
                                finish();
                            }
                        }).create().show();*/
            }
            else if(latestDeviceInfo != null && latestDeviceInfo.recoderStatus == 6){
                Log.e("BluetoothActivity", "设备未授权");
                Toast.makeText(BluetoothBindActivity3.this, "设备未授权", Toast.LENGTH_SHORT).show();
                provider.release();
                provider.setCurrentDeviceMac(null);
                provider.setmBluetoothDevice(null);
                provider.resetDefaultState();
                provider.clearProess();
                finish();
            }
            else {
                provider.requestbound_fit(BluetoothBindActivity3.this);
            }

        }


        //非法用户
        @Override
        public void notifyForInvaildUser() {
            super.notifyForInvaildUser();
            Log.e("BluetoothBindActivity3", "notifyForInvaildUser");
            new AlertDialog.Builder(BluetoothBindActivity3.this)
                    .setTitle(R.string.portal_main_gobound)
                    .setMessage(R.string.portal_main_mustbund)
                    //
                    .setPositiveButton(R.string.general_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            provider.unBoundDevice(BluetoothBindActivity3.this);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.general_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (dialog_bound != null && dialog_bound.isShowing()) {
                                dialog_bound.dismiss();
                            }
                            provider.release();
                            provider.setCurrentDeviceMac(null);
                            provider.setmBluetoothDevice(null);
                            provider.resetDefaultState();
                            setResult(RESULT_FAIL);
                            finish();
                        }
                    }).create().show();
        }

        @Override
        public void updateFor_notifyForDeviceUnboundSucess_D() {
            super.updateFor_notifyForDeviceUnboundSucess_D();
            Log.e("BluetoothBindActivity3", "updateFor_notifyForDeviceUnboundSucess_D");
            Log.e("BluetoothBindActivity3", "清除设备信息/解绑成功");
            provider.clearProess();
            finish();

        }




        @Override
        public void updateFor_BoundNoCharge() {
            super.updateFor_BoundNoCharge();
            Log.e("BluetoothBindActivity3", "updateFor_BoundNoCharge");
            if (dialog_bound != null && dialog_bound.isShowing()) {
                dialog_bound.dismiss();
            }
            setResult(RESULT_NOCHARGE);
            finish();
        }

        @Override
        public void updateFor_BoundContinue() {
            super.updateFor_BoundContinue();
            Log.e("BluetoothBindActivity3", "updateFor_BoundContinue");
            if (dialog_bound != null && !dialog_bound.isShowing())
                dialog_bound.show();
            if (dialog_bound != null && dialog_bound.isShowing()) {
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
            }
            if (sendcount < sendcount_MAX) {
                boundhandler.postDelayed(boundRunnable, sendcount_time);
                sendcount++;
            }
// else {
//                Log.e("BluetoothBindActivity3", "已经发送超出15次");
//                provider.clearProess();
//                BleService.getInstance(BluetoothBindActivity3.this).releaseBLE();
//                setResult(RESULT_FAIL);
//                finish();
//            }
        }

        @Override
        public void updateFor_BoundSucess() {
            Log.e("BluetoothBindActivity3", "updateFor_BoundSucess");
            provider.SetDeviceTime(BluetoothBindActivity3.this);
            if (dialog_bound != null && dialog_bound.isShowing()) {
                dialog_bound.dismiss();
            }
            //获取成功
            startBound();
        }

//        @Override
//        public void updateFor_handleSetTime() {
//            provider.getModelName(BluetoothBindActivity3.this);
//        }

        @Override
        public void updateFor_notifyForModelName(LPDeviceInfo latestDeviceInfo) {
            Log.e("BluetoothBindActivity3", "updateFor_notifyForModelName");
            if (latestDeviceInfo == null) {
                //未获取成功  重新获取
                provider.getModelName(BluetoothBindActivity3.this);
            } else {
                modelName = latestDeviceInfo.modelName;
                if (dialog_bound != null && dialog_bound.isShowing()) {
                    dialog_bound.dismiss();
                }
                //获取成功
                startBound();
            }
        }

        /**
         * 通知：设备绑定信息同步到服务端完成
         */
        @Override
        public void updateFor_boundInfoSyncToServerFinish(Object resultFromServer) {
            Log.e("BluetoothBindActivity3", "updateFor_boundInfoSyncToServerFinish");
            if (resultFromServer != null) {
                if (((String) resultFromServer).equals("1")) {
                    Log.e(TAG, "绑定成功！");
                    Toast.makeText(BluetoothBindActivity3.this, "绑定成功", Toast.LENGTH_SHORT).show();
                    provider.getModelName(BluetoothBindActivity3.this);
                    MyLog.e(TAG,provider.getCurrentDeviceMac()+"provider.getCurrentDeviceMac()");
                    MyApplication.getInstance(BluetoothBindActivity3.this).getLocalUserInfoProvider().getDeviceEntity().setLast_sync_device_id(provider.getCurrentDeviceMac());
                    MyApplication.getInstance(BluetoothBindActivity3.this).getLocalUserInfoProvider().getDeviceEntity().setDevice_type(MyApplication.DEVICE_BAND);
                    setResult(RESULT_OK);
                    IntentFactory.startPortalActivityIntent(BluetoothBindActivity3.this);
                    myfinish();
                } else if (((String) resultFromServer).equals("10024")) {
                    MyLog.e(TAG, "========绑定失败！========");
                    new android.support.v7.app.AlertDialog.Builder(BluetoothBindActivity3.this)
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
            // BluetoothBindActivity3.this.notifyAll();
            if (dialog_bound != null && dialog_bound.isShowing()) {
                dialog_bound.dismiss();
            }
            provider.clearProess();
//            provider.unBoundDevice(BluetoothBindActivity3.this);
            setResult(RESULT_FAIL);
            finish();
        }

    }

    private void startBound() {
        // 绑定设备时必须保证首先从服务端取来标准UTC时间，以便给设备校时(要看看网络是否连接)
        MyLog.e(TAG, "startBound()");
//        if (ToolKits.isNetworkConnected(BluetoothBindActivity3.this)) {
        if (true) {
            UserEntity ue = MyApplication.getInstance(getApplicationContext()).getLocalUserInfoProvider();
            String user_id = ue.getUser_id() + "";
            if (ue != null && provider != null && provider.getCurrentDeviceMac() != null) {
                MyLog.e(TAG,"离线里面的方法执行了");
                // 将用户id 和 MAC地址交到服务端进行匹配
//                submitBoundMACToServer(user_id, provider.getCurrentDeviceMac());
                //***********************************台湾离线版本**********************************************************//
                UserEntity userEntity = MyApplication.getInstance(BluetoothBindActivity3.this).getLocalUserInfoProvider();
                userEntity.getDeviceEntity().setLast_sync_device_id(provider.getCurrentDeviceMac());
                userEntity.getDeviceEntity().setDevice_type(MyApplication.DEVICE_BAND);
                userEntity.getDeviceEntity().setLast_sync_device_id2(modelName);
                MyApplication.getInstance(BluetoothBindActivity3.this).setLocalUserInfoProvider(userEntity);
                if (observerAdapter != null)
                    observerAdapter.updateFor_boundInfoSyncToServerFinish("1");
                //****************************************台湾离线版本****************************************************//
            } else {
                BleService.getInstance(BluetoothBindActivity3.this).releaseBLE();
                setResult(RESULT_FAIL);
                finish();
            }
        } else {
            BleService.getInstance(BluetoothBindActivity3.this).releaseBLE(); // 没有网络去绑定设备
            // 就断开连接
        }
    }


}