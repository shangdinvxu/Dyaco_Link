package com.linkloving.dyh08.logic.UI.setting;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.Toast;

import com.example.android.bluetoothlegatt.BLEProvider;
import com.linkloving.dyh08.BleService;
import com.linkloving.dyh08.IntentFactory;
import com.linkloving.dyh08.R;
import com.linkloving.dyh08.basic.toolbar.ToolBarActivity;
import com.linkloving.dyh08.notify.NotificationService;
import com.linkloving.dyh08.prefrences.PreferencesToolkits;
import com.linkloving.dyh08.utils.ToolKits;
import com.linkloving.dyh08.utils.logUtils.MyLog;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Daniel.Xu on 2016/10/21.
 */

public class SettingActivity extends ToolBarActivity {
    @InjectView(R.id.listview)
    ListView listview;
    private LayoutInflater layoutInflater;
    private View totalView;
    private BLEProvider provider;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tw_setting_activity);
        ButterKnife.inject(this);
        layoutInflater = LayoutInflater.from(SettingActivity.this);
        totalView = layoutInflater.inflate(R.layout.tw_setting_activity, null);
        provider = BleService.getInstance(SettingActivity.this).getCurrentHandlerProvider();
       String[] strings = {getString(R.string.Notificationsetting),
                getString(R.string.General),
                getString(R.string.heartrateSwitch),
        };
        Myadapter myadapter = new Myadapter(SettingActivity.this, strings);
        listview.setAdapter(myadapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        IntentFactory.startNotification(SettingActivity.this);
                        break;
                    case 1:
                        IntentFactory.startGeneral(SettingActivity.this);
                        break;
                    case 2:
//                        initDatasynchroizationPopupwindow();
                        initAutomaticHR();
                        break;
                }
            }
        });
    }
    private void initAutomaticHR() {
        View view = layoutInflater.inflate(R.layout.automatichrpopupwindow, null);
        final Switch realtime = (Switch) view.findViewById(R.id.realtime);
        final Switch wearable = (Switch) view.findViewById(R.id.wearable);
        boolean handRingSet = PreferencesToolkits.getHandRingSet(SettingActivity.this);
        boolean heartrateSync = PreferencesToolkits.getHeartrateSync(SettingActivity.this);
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
                PreferencesToolkits.setHandRingSet(SettingActivity.this, wearable.isChecked());
                PreferencesToolkits.setHeartrateSync(SettingActivity.this, realtime.isChecked());
            }
        });
        realtime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (provider.isConnectedAndDiscovered()) {
                    if (isChecked) {
                        provider.setHeartrateSync(SettingActivity.this);
                    } else {
                        provider.setCloseHeartrateSync(SettingActivity.this);
                    }
                } else {
                    Toast.makeText(SettingActivity.this, getString(R.string.keepthe), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void initDatasynchroizationPopupwindow() {
        View view = layoutInflater.inflate(R.layout.datasynchronizationpopupwindow, null);
        ImageView retrievedata = (ImageView) view.findViewById(R.id.retrievedata);
        ImageView dismiss = (ImageView) view.findViewById(R.id.dismiss);
        final PopupWindow popupWindow = getnewPopupWindow(view);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        retrievedata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initRetrievedata();
            }
        });

    }

    private void initRetrievedata() {
        View view = layoutInflater.inflate(R.layout.retrievedatapopupwindow, null);
        ImageView retrievedata = (ImageView) view.findViewById(R.id.retrievedata);
        ImageView dismiss = (ImageView) view.findViewById(R.id.dismiss);
        final PopupWindow popupWindow = getnewPopupWindow(view);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    private PopupWindow getnewPopupWindow(View view){
        final PopupWindow popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
//        popupWindow.setClippingEnabled(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0xffD3D3D3));
        popupWindow.showAtLocation(totalView, Gravity.BOTTOM,0,0);
        return popupWindow;
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
