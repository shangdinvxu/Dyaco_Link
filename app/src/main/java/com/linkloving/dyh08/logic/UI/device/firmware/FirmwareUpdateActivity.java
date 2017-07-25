package com.linkloving.dyh08.logic.UI.device.firmware;

import android.os.Bundle;

import com.linkloving.dyh08.R;
import com.linkloving.dyh08.basic.toolbar.ToolBarActivity;

public class FirmwareUpdateActivity extends ToolBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firmware_update);

    }
    @Override
    protected void getIntentforActivity() {

    }

    @Override
    protected void initView() {
        SetBarTitleText(getString((R.string.firmware_update_title)));
    }

    @Override
    protected void initListeners() {

    }

}
