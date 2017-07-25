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
import com.linkloving.dyh08.logic.UI.height.*;
import com.linkloving.dyh08.logic.UI.launch.view.ScaleRulerView;
import com.linkloving.dyh08.logic.dto.UserEntity;
import com.linkloving.dyh08.utils.logUtils.MyLog;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Daniel.Xu on 2016/9/5.
 */
public class HeightNewActivity extends Activity {
    @InjectView(R.id.scaleWheelView_height)
    ScaleRulerView heightRulerView;
    @InjectView(R.id.et_userHeight)
    AppCompatTextView height;
    @InjectView(R.id.left)
    ImageView left;
    @InjectView(R.id.right)
    ImageView right;


    private float mHeight = 158;
    private float mMaxHeight = 220;
    private float mMinHeight = 100;
    private UserEntity userEntity;

    @Override
    public void finish() {
        super.finish();
        AppManager.getAppManager().removeActivity(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tw_activity_heightforlauch);
        AppManager.getAppManager().addActivity(this);
        ButterKnife.inject(this);
        userEntity = MyApplication.getInstance(HeightNewActivity.this).getLocalUserInfoProvider();
        heightRulerView.initViewParam(mHeight,mMaxHeight,mMinHeight);
        heightRulerView.setValueChangeListener(new ScaleRulerView.OnValueChangeListener() {
            @Override
            public void onValueChange(float value) {
                height.setText((int)value+"");
            }
        });
        init();

    }
    private void init() {
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentFactory.startNewWeight(HeightNewActivity.this);
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyLog.e("kaishi", "kais");
                String height1 = height.getText().toString().trim();
                userEntity.getUserBase().setUser_height(Integer.parseInt(height1));
                IntentFactory.startPortalActivityIntent(HeightNewActivity.this);
            }
        });
    }



}
