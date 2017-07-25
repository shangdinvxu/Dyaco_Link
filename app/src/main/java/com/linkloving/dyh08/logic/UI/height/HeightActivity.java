package com.linkloving.dyh08.logic.UI.height;

import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.linkloving.dyh08.MyApplication;
import com.linkloving.dyh08.R;
import com.linkloving.dyh08.basic.AppManager;
import com.linkloving.dyh08.logic.UI.launch.view.ScaleRulerView;
import com.linkloving.dyh08.logic.dto.UserEntity;
import com.linkloving.dyh08.prefrences.PreferencesToolkits;
import com.linkloving.dyh08.utils.ToolKits;
import com.zhy.autolayout.AutoLayoutActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class HeightActivity extends AutoLayoutActivity {
    @InjectView(R.id.scaleWheelView_height)
    ScaleRulerView heightRulerView;
    @InjectView(R.id.et_userHeight)
    AppCompatTextView height;
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.unit_cm)
    AppCompatTextView unitCm;
    @InjectView(R.id.textft)
    TextView textft;
    @InjectView(R.id.textUnitft)
    TextView textUnitft;
    @InjectView(R.id.linerlayout)
    LinearLayout linerlayout;
    private int localSettingUnitInfo;


    private float mMaxHeight = 220;
    private float mMinHeight = 0;
    private UserEntity userEntity;
    int unitGong = 0 ;
    @Override
    public void finish() {
        super.finish();
        AppManager.getAppManager().removeActivity(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tw_activity_height);
        AppManager.getAppManager().addActivity(this);
        ButterKnife.inject(this);
        localSettingUnitInfo = PreferencesToolkits.getLocalSettingUnitInfo(HeightActivity.this);
        userEntity = MyApplication.getInstance(HeightActivity.this).getLocalUserInfoProvider();
        int user_height = userEntity.getUserBase().getUser_height();

        if (localSettingUnitInfo != ToolKits.UNIT_GONG) {
            textft.setVisibility(View.VISIBLE);
            textUnitft.setVisibility(View.VISIBLE);
           unitGong  = (int) (user_height * 0.3937)+1;
            int unit_in = unitGong%12 ;
            int unit_ft = (unitGong-unit_in)/12 ;
            unitCm.setText(R.string.unit_inch);
            textft.setText(unit_ft+"");
            height.setText(unit_in+"");
            heightRulerView.initViewParam(unitGong, mMaxHeight, mMinHeight);
        } else {
            textft.setVisibility(View.GONE);
            textUnitft.setVisibility(View.GONE);
            unitCm.setText(R.string.unit_cm);
            heightRulerView.initViewParam(user_height, mMaxHeight, mMinHeight);
            height.setText(user_height + "");
        }
        heightRulerView.setValueChangeListener(new ScaleRulerView.OnValueChangeListener() {
            @Override
            public void onValueChange(float value) {
                if (localSettingUnitInfo != ToolKits.UNIT_GONG) {
                    int unit_in = (int) (value % 12);
                    int unit_ft = (int) ((value - unit_in) / 12);
                    textft.setText(unit_ft + "");
                    height.setText(unit_in + "");
                } else {
                    height.setText((int) value + "");
                }
            }
        });


    }

    @OnClick(R.id.back)
    void back(View view) {
        String heightString = height.getText().toString().trim();
        int heightInt = Integer.parseInt(heightString);
        if (localSettingUnitInfo != ToolKits.UNIT_GONG) {
            heightInt = (int) (unitGong / 0.3937);
        }
        userEntity.getUserBase().setUser_height(heightInt);
        HeightActivity.this.finish();
    }

}
