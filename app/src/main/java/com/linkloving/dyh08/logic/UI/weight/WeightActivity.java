package com.linkloving.dyh08.logic.UI.weight;

import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.ImageView;

import com.linkloving.dyh08.MyApplication;
import com.linkloving.dyh08.R;
import com.linkloving.dyh08.basic.AppManager;
import com.linkloving.dyh08.logic.UI.height.HeightActivity;
import com.linkloving.dyh08.logic.UI.launch.view.ScaleRulerView;
import com.linkloving.dyh08.logic.dto.UserEntity;
import com.linkloving.dyh08.prefrences.PreferencesToolkits;
import com.linkloving.dyh08.utils.ToolKits;
import com.zhy.autolayout.AutoLayoutActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/8/1.
 */
public class WeightActivity extends AutoLayoutActivity {
    @InjectView(R.id.scaleWheelView_weight)
    ScaleRulerView weightRulerView;
    @InjectView(R.id.et_userWeight)
    AppCompatTextView weight;
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.unitkg)
    AppCompatTextView unitkg;
    private UserEntity userEntity;

    private float mHeight = 80;
    private float mMaxHeight = 200;
    private float mMinHeight = 0;
    private int localSettingUnitInfo;
    int user_weight = 0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tw_activity_weight);
        AppManager.getAppManager().addActivity(this);
        ButterKnife.inject(this);
        userEntity = MyApplication.getInstance(WeightActivity.this).getLocalUserInfoProvider();
         user_weight = userEntity.getUserBase().getUser_weight();
        localSettingUnitInfo = PreferencesToolkits.getLocalSettingUnitInfo(WeightActivity.this);

        if (localSettingUnitInfo != ToolKits.UNIT_GONG) {
            user_weight = (int) (user_weight *2.205)+2;
            unitkg.setText(R.string.unit_lb);
            weightRulerView.initViewParam(user_weight, mMaxHeight, mMinHeight);
            weight.setText(user_weight + "");
        } else {
            unitkg.setText(R.string.unit_kilogramme);
            weightRulerView.initViewParam(user_weight, mMaxHeight, mMinHeight);
            weight.setText(user_weight + "");
        }

        weightRulerView.setValueChangeListener(new ScaleRulerView.OnValueChangeListener() {
            @Override
            public void onValueChange(float value) {
                weight.setText((int) value + "");
            }
        });

    }


    @OnClick(R.id.back)
    void back(View view) {
        String weight = this.weight.getText().toString();
        int weightInt = Integer.parseInt(weight);
        if (localSettingUnitInfo != ToolKits.UNIT_GONG){
            weightInt = (int) (weightInt/2.205);
        }

        userEntity.getUserBase().setUser_weight(weightInt);
        WeightActivity.this.finish();
    }
}
