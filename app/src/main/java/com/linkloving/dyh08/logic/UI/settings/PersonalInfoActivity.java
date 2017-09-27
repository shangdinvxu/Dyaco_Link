package com.linkloving.dyh08.logic.UI.settings;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.linkloving.dyh08.IntentFactory;
import com.linkloving.dyh08.MyApplication;
import com.linkloving.dyh08.R;
import com.linkloving.dyh08.basic.toolbar.ToolBarActivity;
import com.linkloving.dyh08.logic.dto.UserBase;
import com.linkloving.dyh08.logic.dto.UserEntity;
import com.linkloving.dyh08.logic.utils.CircleImageView;
import com.linkloving.dyh08.utils.AvatarHelper;
import com.linkloving.dyh08.utils.logUtils.MyLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by zkx on 2016/7/26.
 */
public class PersonalInfoActivity extends ToolBarActivity {
    public final static String TAG = PersonalInfoActivity.class.getSimpleName();

    @InjectView(R.id.user_head)
    CircleImageView userHead;
    @InjectView(R.id.edit_nickname)
    EditText editNickname;
    @InjectView(R.id.radio_man)
    AppCompatRadioButton radioMan;
    @InjectView(R.id.radio_woman)
    AppCompatRadioButton radioWoman;
    @InjectView(R.id.edit_year)
    AppCompatEditText editYear;
    @InjectView(R.id.edit_mon)
    AppCompatEditText editMon;
    @InjectView(R.id.edit_day)
    AppCompatEditText editDay;
    @InjectView(R.id.radio_left)
    AppCompatRadioButton radioLeft;
    @InjectView(R.id.radio_right)
    AppCompatRadioButton radioRight;
    @InjectView(R.id.radio_rem_on)
    AppCompatRadioButton radioRemOn;
    @InjectView(R.id.radio_rem_off)
    AppCompatRadioButton radioRemOff;
    @InjectView(R.id.btn_enter)
    AppCompatButton btnEnter;
    @InjectView(R.id.birthday)
    TextView birthday;
    @InjectView(R.id.layout_peosoninfo)
    LinearLayout layoutPeosoninfo;
    private UserEntity userEntity;
    private UserBase userBase;
    private int wearingStyle = 0;
    private int remindtype = 0;
    //自定义的弹出框类
    private PopupWindow menuWindow = null;
    /**
     * 回调常量之：拍照
     */
    private static final int TAKE_BIG_PICTURE = 991;
    /**
     * 回调常量之：拍照后裁剪
     */
    private static final int CROP_BIG_PICTURE = 993;
//	/** 回调常量之：从相册中选取 */
//	private static final int CHOOSE_BIG_PICTURE = 995;
    /**
     * 回调常量之：从相册中选取2
     */
    private static final int CHOOSE_BIG_PICTURE2 = 996;
    /**
     * 图像保存大小（微信的也是这个大小）
     */
    private static final int AVATAR_SIZE = 640;
    // 修改头像的临时文件存放路径（头像修改成功后，会自动删除之）
    private String __tempImageFileLocation = null;
    private View totalView;
    private LayoutInflater layoutInflater;
     private    String day,month,year1 = "2000" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tw_setting);
        layoutInflater = LayoutInflater.from(PersonalInfoActivity.this);
        totalView = layoutInflater.inflate(R.layout.tw_setting, null);
        ButterKnife.inject(this);
        userEntity = MyApplication.getInstance(PersonalInfoActivity.this).getLocalUserInfoProvider();
        userBase = userEntity.getUserBase();
        editNickname.setText(userBase.getNickname());
        int user_sex = userBase.getUser_sex();
        if (user_sex == 1) {
            radioMan.setChecked(true);
        } else {
            radioWoman.setChecked(true);
        }
        if (userBase.getUser_wearingStyle() == 1) {
            radioLeft.setChecked(true);
        } else {
            radioRight.setChecked(true);
        }
        String birthdate = userBase.getBirthdate();
        birthday.setText(birthdate);
        if (birthdate.length() > 0) {
            String[] split = birthdate.split("-");
            editYear.setText(split[0]);
            editMon.setText(split[1]);
            editDay.setText(split[2]);
        }
        if (userBase.getRemind() == 1) {
            radioRemOn.setChecked(true);
        } else {
            radioRemOff.setChecked(true);
        }
        if (getTempImageFileUri()!=null){
            Bitmap bitmap = decodeUriAsBitmap(getTempImageFileUri());
            if (bitmap==null){
                bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.default_avatar_m);
            }
            userHead.setImageBitmap(bitmap);
        }
    }

    @OnClick(R.id.radio_left)
    void setRadioLeft(View view) {
        wearingStyle = 0;
    }

    @OnClick(R.id.radio_right)
    void setRadioRight(View view) {
        wearingStyle = 1;
    }

    @OnClick(R.id.btn_enter)
    void enter(View view) {
        btnEnter.setBackgroundResource(R.drawable.enteron);
        String name = editNickname.getText().toString().trim();
        userBase.setNickname(name);
//         day = editDay.getText().toString().trim();
//        month = editMon.getText().toString().trim();
//        year1 = editYear.getText().toString().trim();
//        String birthday = year1 + "-" + month + "-" + day;


        String s = birthday.getText().toString();
        if (s==null||s.length()==0){
            Toast.makeText(PersonalInfoActivity.this, getString(R.string.pleasebirthday), Toast.LENGTH_SHORT).show();
            return;
        }
        String[] split = s.split("-");
        if (split[0].equals("null")){
            Toast.makeText(PersonalInfoActivity.this, getString(R.string.pleasebirthday), Toast.LENGTH_SHORT).show();
            return;
        }
        userBase.setBirthdate( birthday.getText().toString());
        userBase.setUser_wearingStyle(wearingStyle);
        userBase.setRemind(remindtype);

        IntentFactory.startPortalActivityIntent(PersonalInfoActivity.this);
    }

    @OnClick(R.id.radio_rem_off)
    void setRadioRemOff(View view) {
        remindtype = 2;
    }

    @OnClick(R.id.radio_rem_on)
    void setRadioRemOn(View view) {
        remindtype = 1;
    }

    @Override
    protected void getIntentforActivity() {

    }

    @Override
    protected void initView() {

    }

    @OnClick(R.id.birthday)
    void setBirthday(View view){
        DatePickerDialog datePickerDialog = new DatePickerDialog(PersonalInfoActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                day = dayOfMonth + "";
                year1 = year + "";
                month = monthOfYear+1 + "";
            }
        }, 2000, 12, 2);
        datePickerDialog.show();
        datePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                int yearage = Integer.parseInt(year1);
                int nowYearInt = Calendar.getInstance().get(Calendar.YEAR);
                int userAge = nowYearInt - yearage;
                if (userAge>110||userAge<7){
                    Toast.makeText(PersonalInfoActivity.this, "请填写正确的生日", Toast.LENGTH_SHORT).show();
                }else {
                    birthday.setText(year1+"-"+month+"-"+day);
                }
            }
        });

    }

    @OnClick(R.id.user_head)
    void setUserHead(View view) {
        initpopwindow();
    }

    private void initpopwindow() {
        View view = layoutInflater.inflate(R.layout.popphotopopupwindow, null);
        Button deletePhoto = (Button) view.findViewById(R.id.deletePhoto);
        Button takePhotoBtn = (Button) view.findViewById(R.id.takePhotoBtn);
        Button pickPhotoBtn = (Button) view.findViewById(R.id.pickPhotoBtn);
        Button cancel_Btn = (Button) view.findViewById(R.id.cancelBtn);
        ImageView dismiss = (ImageView) view.findViewById(R.id.dismiss);
        final PopupWindow popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0xffD3D3D3));
        popupWindow.showAtLocation(totalView, Gravity.BOTTOM, 0, 0);
        deletePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePhotoPopupWindow();
            }
        });
        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入拍照
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//action is capture
                intent.putExtra(MediaStore.EXTRA_OUTPUT, getTempImageFileUri());
                startActivityForResult(intent, TAKE_BIG_PICTURE);
            }
        });
        pickPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPhoto();//相册
            }
        });
        cancel_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                int photoType = userBase.getPhotoType();
                if (photoType == 1) {
                    userHead.setImageResource(R.mipmap.default_avatar_m);
                }
                if (getTempImageFileUri() != null) {
                    Bitmap bitmap = decodeUriAsBitmap(getTempImageFileUri());
                    userHead.setImageBitmap(bitmap);
                }
            }
        });
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            menuWindow.dismiss();
            switch (v.getId()) {
                // 拍照
                case R.id.deletePhoto:

                case R.id.takePhotoBtn:
                    //进入拍照
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//action is capture
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, getTempImageFileUri());
                    startActivityForResult(intent, TAKE_BIG_PICTURE);
                    break;
                // 相册选择图片
                case R.id.pickPhotoBtn:
                    startPhoto();//相册
                    break;
                default:
                    break;
            }
        }
    };

    private void deletePhotoPopupWindow() {
        final Uri imagePhotoUri = getTempImageFileUri();
        LayoutInflater layoutInflater = LayoutInflater.from(PersonalInfoActivity.this);
        View view = layoutInflater.inflate(R.layout.photopopupwindow, null);
        final CircleImageView photoView = (CircleImageView) view.findViewById(R.id.photo);
        Button cancel_btn = (Button) view.findViewById(R.id.cancel_Btn);
        Button confirm_btn = (Button) view.findViewById(R.id.confirm_Btn);
        final PopupWindow popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0xffD3D3D3));
        popupWindow.showAtLocation(totalView, Gravity.BOTTOM, 0, 0);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoView.setImageResource(R.mipmap.default_avatar_m);
                File file = new File(getTempImageFileLocation());
                Resources r = getResources();
                Bitmap bitmap = BitmapFactory.decodeResource(r, R.mipmap.default_avatar_m);
                FileOutputStream fileOutputStream = null;
                try {
                    fileOutputStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 20, fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Bitmap bitmap = decodeUriAsBitmap(imagePhotoUri);
        photoView.setImageBitmap(bitmap);
    }


    /*进入相册*/
    private void startPhoto() {
        Intent intent = new Intent("android.intent.action.PICK");
        intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", AVATAR_SIZE);
        intent.putExtra("outputY", AVATAR_SIZE);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getTempImageFileUri());
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, CHOOSE_BIG_PICTURE2);
    }

    /**
     * 获得临时文件存放地址的Uri(此地址存在与否并不代表该文件一定存在哦).
     *
     * @return 正常获得uri则返回，否则返回null
     */
    private Uri getTempImageFileUri() {
        String tempImageFileLocation = getTempImageFileLocation();
        if (tempImageFileLocation != null) {
            return Uri.parse("file://" + tempImageFileLocation);
        }
        return null;
    }

    /**
     * 获得临时文件存放地址(此地址存在与否并不代表该文件一定存在哦).
     *
     * @return 正常获得则返回，否则返回null
     */
    private String getTempImageFileLocation() {
        try {
            if (__tempImageFileLocation == null) {
                String avatarTempDirStr = AvatarHelper.getUserAvatarSavedDir(PersonalInfoActivity.this);
                File avatarTempDir = new File(avatarTempDirStr);
                if (avatarTempDir != null) {
                    // 目录不存在则新建之
                    if (!avatarTempDir.exists())
                        avatarTempDir.mkdirs();
                    // 临时文件名
                    int user_id = MyApplication.getInstance(PersonalInfoActivity.this).getLocalUserInfoProvider().getUser_id();
                    String userIdString = Integer.toString(user_id);
                    __tempImageFileLocation = avatarTempDir.getAbsolutePath() + "/" + "local_avatar_temp_dy.jpg";
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "【ChangeAvatar】读取本地用户的头像临时存储路径时出错了，" + e.getMessage(), e);
        }

        Log.d(TAG, "【ChangeAvatar】正在获取本地用户的头像临时存储路径：" + __tempImageFileLocation);

        return __tempImageFileLocation;
    }

    @Override
    protected void initListeners() {

    }

    /**
     * 拍照后裁剪图片方法实现
     */
    public void startPhotoZoom(Uri uri, int outputX, int outputY, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, requestCode);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final Uri imagePhotoUri = getTempImageFileUri();
        switch (requestCode) {
            case TAKE_BIG_PICTURE:// 拍照完成则新拍的文件将会存放于指定的位置（即uri、tempImaheLocation所表示的地方）
            {
                if (resultCode == RESULT_OK) {
                    //从相机拍摄保存的Uri中取出图片，调用系统剪裁工具
                    if (imagePhotoUri != null) {
                        startPhotoZoom(imagePhotoUri, AVATAR_SIZE, AVATAR_SIZE, CROP_BIG_PICTURE);
                    } else {
//                        MyToast.showShort(this, "没有得到拍照图片");
                    }
                } else if (resultCode == RESULT_CANCELED) {

//                        MyToast.showShort(this, "取消拍照");

                } else {

//                   MyToast.showShort(this, "拍照失败");

                }
                break;
            }
            //裁切完成后的处理（上传头像）
            case CROP_BIG_PICTURE://from crop_big_picture
            {
                if (resultCode == RESULT_OK) {
                    MyLog.i("裁剪完成");
//                    processAvatarUpload(imagePhotoUri);
                    Bitmap bitmap = decodeUriAsBitmap(imagePhotoUri);
                    userHead.setImageBitmap(bitmap);
                }
                break;
            }

            case CHOOSE_BIG_PICTURE2:// 图片选取完成时，其实该图片还有原处，如要裁剪则应把它复制出来一份（以免裁剪时覆盖原图)
            {

                if (resultCode == RESULT_OK) {
                    MyLog.i("选择图片,裁剪完成");
//                    processAvatarUpload(imagePhotoUri);
                    Bitmap bitmap = decodeUriAsBitmap(imagePhotoUri);
                    userHead.setImageBitmap(bitmap);
                }
            }
            break;

        }
    }

    private Bitmap decodeUriAsBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inPreferredConfig = Bitmap.Config.RGB_565;
            opt.inPurgeable = true;
            opt.inInputShareable = true;
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, opt);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }


}