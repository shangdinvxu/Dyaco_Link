package com.linkloving.dyh08.logic.UI.Bluetooth;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.linkloving.dyh08.IntentFactory;
import com.linkloving.dyh08.R;
import com.linkloving.dyh08.basic.toolbar.ToolBarActivity;
import com.linkloving.dyh08.logic.dto.SportDeviceEntity;
import com.linkloving.dyh08.logic.dto.UserEntity;
import com.linkloving.dyh08.prefrences.PreferencesToolkits;
import com.linkloving.dyh08.utils.CutString;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Daniel.Xu on 2016/9/9.
 */
public class BluetoothBindActivity extends ToolBarActivity {
    @InjectView(R.id.blue_middle_changeIV)
    ImageView blueMiddleChangeIV;
    @InjectView(R.id.equipment_name)
    TextView equipmentName;
    @InjectView(R.id.equipment_adress)
    TextView equipmentAdress;
    @InjectView(R.id.list_item_imageview)
    ImageView listItemImageview;
    @InjectView(R.id.Reconnect)
    LinearLayout cancel;
    @InjectView(R.id.Disconnect)
    LinearLayout confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tw_binding_bluetooth_activity1);
        ButterKnife.inject(this);
        UserEntity userAuthedInfo = PreferencesToolkits.getLocalUserInfoForLaunch(BluetoothBindActivity.this);
        SportDeviceEntity deviceEntity = userAuthedInfo.getDeviceEntity();
        String model_name = deviceEntity.getLast_sync_device_id2();
        deviceEntity.getCard_number();
        String last_sync_device_id = deviceEntity.getLast_sync_device_id();
        equipmentName.setText(model_name);
        String macEndTwo = CutString.macSplitEndTwo(last_sync_device_id);
        equipmentAdress.setText(macEndTwo);
//        equipmentAdress.setText(last_sync_device_id);
        init();
    }

    private void init() {

    }

    @Override
    protected void getIntentforActivity() {

    }

    protected void initView() {

    }

    @Override
    protected void initListeners() {

    }

    @OnClick(R.id.Disconnect)
    void clickDisconnect(View view){
        finish();
        IntentFactory.startBindActivity2(BluetoothBindActivity.this);
    }
    @OnClick (R.id.Reconnect)
    void clickReconnect(View view)
    {
        finish();
        IntentFactory.start_Bluetooth(BluetoothBindActivity.this);
    }
}
