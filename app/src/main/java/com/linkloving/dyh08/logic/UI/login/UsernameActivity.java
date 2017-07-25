package com.linkloving.dyh08.logic.UI.login;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.linkloving.dyh08.IntentFactory;
import com.linkloving.dyh08.MyApplication;
import com.linkloving.dyh08.R;
import com.linkloving.dyh08.logic.dto.UserBase;
import com.linkloving.dyh08.logic.dto.UserEntity;
import com.linkloving.dyh08.utils.logUtils.MyLog;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

/**
 * Created by Daniel.Xu on 2016/9/5.
 */
public class UsernameActivity extends Activity {
    @InjectView(R.id.user_name)
    EditText userName;
    @InjectView(R.id.next)
    ImageView next;
    private UserEntity userEntity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_username);
        ButterKnife.inject(this);
        userEntity = MyApplication.getInstance(UsernameActivity.this).getLocalUserInfoProvider();
        initView();

    }

    private void initView() {
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = userName.getText().toString();
                if (name==null||name.length()<=0){
                    Toast.makeText(UsernameActivity.this, R.string.usernamecannot,Toast.LENGTH_SHORT).show();
                    return;
                }
               userEntity.getUserBase().setNickname(name);
                IntentFactory.startGender(UsernameActivity.this);
            }
        });
    }
}
