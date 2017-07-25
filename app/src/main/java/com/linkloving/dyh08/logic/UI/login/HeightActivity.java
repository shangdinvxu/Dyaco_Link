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
import com.linkloving.dyh08.utils.logUtils.MyLog;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Daniel.Xu on 2016/9/5.
 */
public class HeightActivity extends Activity {
    @InjectView(R.id.HeightBar)
    SeekBar heightBar;
    @InjectView(R.id.left)
    ImageView left;
    @InjectView(R.id.right)
    ImageView right;
    @InjectView(R.id.heightTV)
    TextView heightTV ;
    private UserEntity userEntity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_height);
        ButterKnife.inject(this);
        userEntity = MyApplication.getInstance(HeightActivity.this).getLocalUserInfoProvider();
        heightBar.setMax(200);
        heightBar.setOnSeekBarChangeListener(seekBarChangeListener);
        init();
    }
    private  SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            heightTV.setText(Integer.toString(progress+40));
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
                IntentFactory.startWeight(HeightActivity.this);
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyLog.e("kaishi", "kais");
                String height = heightTV.getText().toString().trim();
                userEntity.getUserBase().setUser_height(Integer.parseInt(height));
                IntentFactory.startWearing(HeightActivity.this);
            }
        });
    }
}
