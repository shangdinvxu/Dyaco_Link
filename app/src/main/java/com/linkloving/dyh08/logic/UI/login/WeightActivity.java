package com.linkloving.dyh08.logic.UI.login;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.linkloving.dyh08.IntentFactory;
import com.linkloving.dyh08.MyApplication;
import com.linkloving.dyh08.R;
import com.linkloving.dyh08.logic.dto.UserBase;
import com.linkloving.dyh08.logic.dto.UserEntity;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Daniel.Xu on 2016/9/5.
 */
public class WeightActivity extends Activity {
    @InjectView(R.id.weightBar)
    SeekBar weightBar;
    @InjectView(R.id.left)
    ImageView left;
    @InjectView(R.id.right)
    ImageView right;
    @InjectView(R.id.weightTextview)
    TextView weightTV;
    int weight  ;
    private UserEntity userEntity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_weight);
        ButterKnife.inject(this);
        userEntity = MyApplication.getInstance(WeightActivity.this).getLocalUserInfoProvider();
        weightBar.setMax(150);
        weightBar.setOnSeekBarChangeListener(seekBarChangeListener);
        init();
    }

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            weightTV.setText(Integer.toString(progress+40));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private void init() {
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentFactory.startBirthday(WeightActivity.this);
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String weight = weightTV.getText().toString().trim();
                userEntity.getUserBase().setUser_weight(Integer.parseInt(weight));
                IntentFactory.startHeight(WeightActivity.this);
            }
        });
    }
}
