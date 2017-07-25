package com.linkloving.dyh08.logic.UI.main.materialmenu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.MapsInitializer;
import com.linkloving.dyh08.BleService;
import com.linkloving.dyh08.CommParams;
import com.linkloving.dyh08.IntentFactory;
import com.linkloving.dyh08.MyApplication;
import com.linkloving.dyh08.R;
import com.linkloving.dyh08.basic.AppManager;
import com.linkloving.dyh08.logic.UI.Bluetooth.BluetoothActivity;
import com.linkloving.dyh08.logic.UI.main.PortalActivity;
import com.linkloving.dyh08.logic.dto.UserEntity;
import com.linkloving.dyh08.notify.NotificationService;
import com.linkloving.dyh08.prefrences.PreferencesToolkits;
import com.linkloving.dyh08.utils.CommonUtils;
import com.linkloving.dyh08.utils.GoogleApiUtils;
import com.linkloving.dyh08.utils.MyToast;
import com.linkloving.dyh08.utils.ToolKits;
import com.linkloving.dyh08.utils.logUtils.MyLog;

import java.text.MessageFormat;
import java.util.List;



/**
 * Created by zkx on 2016/3/11.
 */
public class MenuNewAdapter extends RecyclerView.Adapter {
    private static final String TAG = MenuNewAdapter.class.getSimpleName();
    public  static int JUMP_FRIEND_TAG=1;
    UserEntity userEntity;
    private Context mContext;
    private OnRecyclerViewListener onRecyclerViewListener;


    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

    public static interface OnRecyclerViewListener {
        void onItemClick(int position);
    }

    private List<MenuVO> list;

    public MenuNewAdapter(Context context, List<MenuVO> list) {
        this.mContext = context;
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_menu, null);
//        不知道为什么在xml设置的“android:layout_width="match_parent"”无效了，需要在这里重新设置
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        MenuViewHolder holder = (MenuViewHolder) viewHolder;
        holder.itemImg.setImageResource(list.get(position).getImgID());
        holder.itemName.setText(list.get(position).getTextID());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    //还可以添加长点击事件
    class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public LinearLayout layout;
        public TextView itemName;
        public ImageView itemImg;
        public TextView unread;

        public MenuViewHolder(View convertView) {
            super(convertView);
            itemImg = (ImageView) convertView.findViewById(R.id.fragment_item_img);
            itemName = (TextView) convertView.findViewById(R.id.fragment_item_text);
            unread = (TextView) convertView.findViewById(R.id.fragment_item_unread_text);
            layout = (LinearLayout) convertView.findViewById(R.id.Layout);
            layout.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (null != onRecyclerViewListener) {
                onRecyclerViewListener.onItemClick(this.getPosition());
                switch (this.getPosition()){
                    case Left_viewVO1.Bluetooth:
                        UserEntity userAuthedInfo = PreferencesToolkits.getLocalUserInfoForLaunch(mContext);
                        String last_sync_device_id = userAuthedInfo.getDeviceEntity().getLast_sync_device_id();
                        if (last_sync_device_id==null||last_sync_device_id.length()==0){
                            IntentFactory.start_Bluetooth((Activity) mContext);
                        }else{
                           IntentFactory.startBindActivity1((Activity) mContext);
                        }
                        break;
                    case Left_viewVO1.Settings:
                        IntentFactory.startSetting((Activity) mContext);
                        break;

                    case Left_viewVO1.Workout:
                        IntentFactory.start2GroupsActivity((Activity) mContext);
                        break;

                    case Left_viewVO1.Groups:
                        IntentFactory.start2MapActivity((Activity) mContext);
                        break;
                    case Left_viewVO1.map:
                        boolean mapSelect = PreferencesToolkits.getMapSelect(mContext);
//                        int flg = MapsInitializer.initialize(mContext);
//                        GooglePlayServicesUtil.getErrorDialog(flg,
//                                (Activity) mContext, 0).show();
                        if (mapSelect){
//                            boolean googlePlaySupport = GoogleApiUtils.isGoogleMapsInstalled((Activity) mContext);
//                            if (googlePlaySupport){
                                IntentFactory.startGoogleActivity((Activity)mContext);
//                            }else {
//                                MyToast.showShort(mContext,"此设备不支持谷歌服务，无法使用google map");
//                            }
                        }else {
                            IntentFactory.start2WorkoutActivity((Activity) mContext);
                        }
                        break;
                    case Left_viewVO1.Heart_Rate:
                        IntentFactory.startHeartrateActivity((Activity) mContext);
                        break;
                    case Left_viewVO1.Height:
                        IntentFactory.start2HeightActivity((Activity) mContext);
                        break;

                    case Left_viewVO1.Weight:
                        IntentFactory.start2WeightActivity((Activity) mContext);
                        break;

                    case Left_viewVO1.Sign_out:
                        android.support.v7.app.AlertDialog dialog = new android.support.v7.app.AlertDialog.Builder(mContext)
                                .setTitle(mContext.getResources().getString(R.string.Sign_Out))
                                .setMessage(R.string.doyouwantto)
                                .setPositiveButton(R.string.Confirm, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog_, int which) {
                                        /**设置APP状态为退出*/
                               /*         BleService.setEXIT_APP(true);
                                        AppManager.getAppManager().finishAllActivity();
                                        if(CommonUtils.isStringEmpty(MyApplication.getInstance(mContext).getLocalUserInfoProvider().getDeviceEntity().getLast_sync_device_id())
                                                || !isEnabled(mContext)
                                                || MyApplication.getInstance(mContext).getLocalUserInfoProvider().getDeviceEntity().getDevice_type()==MyApplication.DEVICE_BAND
                                                ){
                                            //未绑定
                                            BleService.getInstance(mContext).getCurrentHandlerProvider().clearProess();
                                            // 以下的注释都开启后 并且 通知的服务也关闭后 就可以完全关闭APP 否则 退出不退出服务
                                            MyApplication.getInstance(mContext).stopBleService();
                                            MyApplication.getInstance(mContext).setLocalUserInfoProvider(null);
                                            System.exit(0);
                                        }*/
                                        UserEntity userEntity = new UserEntity();
                                        int ran = (int) ((10000000) * Math.random() + 10000);
                                        userEntity.setUser_id(ran);
                                        MyApplication.getInstance(mContext).setLocalUserInfoProvider(userEntity);
                                        IntentFactory.startUsername((Activity) mContext);
                                    }
                                })
                                .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        positiveButton.setAllCaps(false);
                        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                        negativeButton.setAllCaps(false);
                        break;
                    default :
                }
            }
        }
    }
        //判断是否获取了消息通知权限
        public static boolean isEnabled(Context context)
        {
            String pkgName = context.getPackageName();
            final String flat = Settings.Secure.getString(context.getContentResolver(), NotificationService.ENABLED_NOTIFICATION_LISTENERS);
            if (!TextUtils.isEmpty(flat)) {
                final String[] names = flat.split(":");
                for (int i = 0; i < names.length; i++) {
                    final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                    if (cn != null) {
                        if (TextUtils.equals(pkgName, cn.getPackageName())) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }


}
