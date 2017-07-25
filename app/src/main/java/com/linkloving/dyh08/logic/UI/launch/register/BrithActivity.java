package com.linkloving.dyh08.logic.UI.launch.register;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.linkloving.dyh08.CommParams;
import com.linkloving.dyh08.IntentFactory;
import com.linkloving.dyh08.MyApplication;
import com.linkloving.dyh08.R;
import com.linkloving.dyh08.basic.AppManager;
import com.linkloving.dyh08.basic.toolbar.ToolBarActivity;
import com.linkloving.dyh08.http.basic.CallServer;
import com.linkloving.dyh08.http.basic.HttpCallback;
import com.linkloving.dyh08.http.basic.NoHttpRuquestFactory;
import com.linkloving.dyh08.http.data.DataFromServer;
import com.linkloving.dyh08.logic.UI.launch.view.DatePicker;
import com.linkloving.dyh08.logic.dto.UserBase;
import com.linkloving.dyh08.prefrences.PreferencesToolkits;
import com.linkloving.dyh08.utils.ToolKits;
import com.linkloving.dyh08.utils.logUtils.MyLog;
import com.yolanda.nohttp.Response;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class BrithActivity extends ToolBarActivity {
    @InjectView(R.id.next)
    Button next;
    @InjectView(R.id.date_picker)
    DatePicker datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brith);
        ButterKnife.inject(this);
    }

    @Override
    protected void getIntentforActivity() {
        int tag = getIntent().getIntExtra("tag",0);
    }

    @OnClick(R.id.next)
    void next(){
        String[] date = datePicker.getDate();
        String dateString = TextUtils.join("-", date);

        UserBase userbase = MyApplication.getInstance(BrithActivity.this).getLocalUserInfoProvider().getUserBase();
        userbase.setBirthdate(dateString);
        //网络交互：提交目标数据
        CallServer.getRequestInstance().add(BrithActivity.this, getString(R.string.general_submitting), CommParams.HTTP_SUBMIT_BODYDATA, NoHttpRuquestFactory.submit_RegisterationToServer_Modify(userbase), httpCallback);
    }

    private HttpCallback<String> httpCallback = new HttpCallback<String>() {

        @Override
        public void onSucceed(int what, Response<String> response) {
            switch (what) {
                case CommParams.HTTP_SUBMIT_BODYDATA:

                    DataFromServer dataFromServer = JSON.parseObject(response.get(), DataFromServer.class);
                    MyLog.e("注册", "=====onSucceed====Body返回结果：" + dataFromServer.getReturnValue().toString() + "===getErrorCode====" + dataFromServer.getErrorCode() + "====getErrMsg====" + dataFromServer.getErrMsg());
                    if (dataFromServer.getErrorCode() == 1) {

                        PreferencesToolkits.setAppStartFitst(BrithActivity.this);
                        //跳转到主页面
                        startActivity(IntentFactory.createPortalActivityIntent(BrithActivity.this));
                        //销毁前面的avtivity
                        AppManager.getAppManager().finishAllActivity();
                        finish();
                        break;
                    }
            }
        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {
            MyLog.e("注册", "Body返回结果：what=" +what+",message="+ message+",url"+url+",tag="+tag+",responseCode="+responseCode);
            ToolKits.showCommonTosat(BrithActivity.this, false, getString(R.string.login_form_error), Toast.LENGTH_SHORT);
        }
    };
    @Override
    protected void initView() {

    }

    @Override
    protected void initListeners() {

    }
}
