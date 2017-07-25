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
import com.linkloving.dyh08.logic.UI.launch.LoginFromPhoneActivity;
import com.linkloving.dyh08.logic.dto.UserBase;
import com.linkloving.dyh08.logic.dto.UserEntity;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Daniel.Xu on 2016/9/5.
 */
public class GenderActivity extends Activity {
    @InjectView(R.id.man)
    ImageView man;
    @InjectView(R.id.woman)
    ImageView woman;
    @InjectView(R.id.left)
    ImageView left;
    @InjectView(R.id.right)
    ImageView right;
    private int type = 2 ;
    private UserEntity userEntity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_gender);
        ButterKnife.inject(this);
        userEntity = MyApplication.getInstance(GenderActivity.this).getLocalUserInfoProvider();
        init();
    }

    private void init() {
        man.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = 1 ;
                man.setImageResource(R.drawable.manon);
                woman.setImageResource(R.drawable.woman);
            }
        });
        woman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = 0 ;
                woman.setImageResource(R.drawable.womanon);
                man.setImageResource(R.drawable.man);
            }
        });
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentFactory.startUsername(GenderActivity.this);
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type ==2  ){
                    Toast.makeText(GenderActivity.this, "请选择性别", Toast.LENGTH_SHORT).show();
                    return;
                }
               userEntity.getUserBase().setUser_sex(type);
                IntentFactory.startBirthday(GenderActivity.this);
            }
        });


    }

}
