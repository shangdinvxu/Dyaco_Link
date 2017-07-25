package com.linkloving.dyh08.logic.UI.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.linkloving.dyh08.IntentFactory;
import com.linkloving.dyh08.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Daniel.Xu on 2016/9/5.
 */
public class LoginFinishActivity extends Activity {
    @InjectView(R.id.Link)
    ImageView link;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tw_finishlogin);
        ButterKnife.inject(this);
        init();
    }

    @OnClick(R.id.Link)
    void finishLogin(View view){
        Intent portalActivityIntent = IntentFactory.createPortalActivityIntent(LoginFinishActivity.this);
        startActivity(portalActivityIntent);
//        IntentFactory.start_Bluetooth(LoginFinishActivity.this);
    }

    private void init() {
    }
}
