package com.linkloving.dyh08.logic.UI.login;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.linkloving.dyh08.IntentFactory;
import com.linkloving.dyh08.MyApplication;
import com.linkloving.dyh08.R;
import com.linkloving.dyh08.logic.UI.main.PortalActivity;
import com.linkloving.dyh08.logic.dto.UserBase;
import com.linkloving.dyh08.logic.dto.UserEntity;
import com.linkloving.dyh08.utils.logUtils.MyLog;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created by Daniel.Xu on 2016/9/5.
 */
public class BirthdayActivity extends Activity {
    private static final String TAG = PortalActivity.class.getSimpleName();
    @InjectView(R.id.yearET)
    EditText yearET;
    @InjectView(R.id.monthET)
    EditText monthET;
    @InjectView(R.id.dayET)
    EditText dayET;
    @InjectView(R.id.left)
    ImageView left;
    @InjectView(R.id.right)
    ImageView right;
    private UserEntity userEntity;
    private int monthInt;
    private int dayInt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_birthday);
        ButterKnife.inject(this);
        userEntity = MyApplication.getInstance(BirthdayActivity.this).getLocalUserInfoProvider();
        init();
    }

    private void init() {
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentFactory.startGender(BirthdayActivity.this);
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String year = yearET.getText().toString().trim();
                int nowYearInt = Calendar.getInstance().get(Calendar.YEAR);
                String month = monthET.getText().toString().trim();
                if (month.length()>0){
                    monthInt = Integer.parseInt(month);
                }
                String day = dayET.getText().toString().trim();
                if(day.length()>0) {
                    dayInt = Integer.parseInt(day);
                }
                String birthday = year + "-" + month + "-" + day;
                if (year.length() <= 0 || month.length() <= 0 || day.length() <= 0||
                        monthInt ==0|| monthInt >12
                        ||dayInt==0||dayInt>31) {

                    Toast.makeText(BirthdayActivity.this, "请填写正确的生日", Toast.LENGTH_SHORT).show();
                } else {
                    int yearage = Integer.parseInt(year);
                    int userAge = nowYearInt - yearage;
                    if (userAge>110||userAge<7){
                        Toast.makeText(BirthdayActivity.this, "请填写正确的生日", Toast.LENGTH_SHORT).show();
                    }else {
                        userEntity.getUserBase().setBirthdate(birthday);
                        IntentFactory.startNewWeight(BirthdayActivity.this);
                    }

                }

            }
        });
    }
}
