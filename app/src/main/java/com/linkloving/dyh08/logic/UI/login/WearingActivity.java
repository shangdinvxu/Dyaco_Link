package com.linkloving.dyh08.logic.UI.login;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.linkloving.dyh08.IntentFactory;
import com.linkloving.dyh08.MyApplication;
import com.linkloving.dyh08.R;
import com.linkloving.dyh08.logic.dto.UserEntity;
import com.linkloving.dyh08.utils.logUtils.MyLog;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Daniel.Xu on 2016/9/5.
 */
public class WearingActivity extends Activity {
    @InjectView(R.id.lefthand)
    ImageView lefthand;
    @InjectView(R.id.righthand)
    ImageView righthand;
    @InjectView(R.id.left)
    ImageView left;
    @InjectView(R.id.right)
    ImageView right;
    private UserEntity userEntity;
    int wearingStyle = 2 ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_wearingstyles);
        MyLog.e("kaishi", "kais");
        ButterKnife.inject(this);
        userEntity = MyApplication.getInstance(WearingActivity.this).getLocalUserInfoProvider();
        init();
    }


    private void init() {
        lefthand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wearingStyle = 0 ;
                restartState();
                lefthand.setImageResource(R.mipmap.lefton);
            }
        });
        righthand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wearingStyle = 1 ;
                restartState();
                righthand.setImageResource(R.mipmap.righthandon);
            }
        });
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentFactory.startHeight(WearingActivity.this);
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wearingStyle==2){
                    Toast.makeText(WearingActivity.this, R.string.choosewearingstyles, Toast.LENGTH_SHORT).show();
                    return;
                }
                userEntity.getUserBase().setUser_wearingStyle(wearingStyle);
//                IntentFactory.startLoginFinish(WearingActivity.this);
                    IntentFactory.startPortalActivityIntent(WearingActivity.this);
            }
        });

    }
    private void restartState(){
        lefthand.setImageResource(R.mipmap.left);
        righthand.setImageResource(R.mipmap.righthand);
    }
}
