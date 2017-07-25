package com.linkloving.dyh08.logic.UI.login;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.linkloving.dyh08.IntentFactory;
import com.linkloving.dyh08.MyApplication;
import com.linkloving.dyh08.R;
import com.linkloving.dyh08.basic.AppManager;
import com.linkloving.dyh08.logic.UI.launch.view.ScaleRulerView;
import com.linkloving.dyh08.logic.UI.weight.*;
import com.linkloving.dyh08.logic.dto.UserEntity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Daniel.Xu on 2016/9/5.
 */
public class WeightNewActivity extends Activity {
    @InjectView(R.id.scaleWheelView_weight)
    ScaleRulerView weightRulerView;
    @InjectView(R.id.et_userWeight)
    AppCompatTextView weight;
    @InjectView(R.id.left)
    ImageView left;
    @InjectView(R.id.right)
    ImageView right;
    private UserEntity userEntity;

    private float mHeight = 60;
    private float mMaxHeight = 200;
    private float mMinHeight = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tw_activity_weightforlauch);
        AppManager.getAppManager().addActivity(this);
        ButterKnife.inject(this);
        userEntity = MyApplication.getInstance(WeightNewActivity.this).getLocalUserInfoProvider();
        weightRulerView.initViewParam(mHeight,mMaxHeight,mMinHeight);
        weightRulerView.setValueChangeListener(new ScaleRulerView.OnValueChangeListener() {
            @Override
            public void onValueChange(float value) {
                weight.setText((int)value+"");
            }
        });
        init();
    }

    private void init() {
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentFactory.startBirthday(WeightNewActivity.this);
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String weight1 = weight.getText().toString();
                int  weightInt = Integer.parseInt(weight1);
                userEntity.getUserBase().setUser_weight(weightInt);
                IntentFactory.startNewHeight(WeightNewActivity.this);
            }
        });
    }
}
