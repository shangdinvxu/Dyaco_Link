package com.linkloving.dyh08;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.linkloving.dyh08.logic.UI.Bluetooth.BluetoothActivity;
import com.linkloving.dyh08.logic.UI.Bluetooth.BluetoothBindActivity;
import com.linkloving.dyh08.logic.UI.Bluetooth.BluetoothBindActivity3;
import com.linkloving.dyh08.logic.UI.Bluetooth.BluetoothDisconnectActivity;
import com.linkloving.dyh08.logic.UI.Groups.baidu.GroupsDetailsActivity;
import com.linkloving.dyh08.logic.UI.HeartRate.HeartRateActivity;
import com.linkloving.dyh08.logic.UI.calories.CaloriesActivity;
import com.linkloving.dyh08.logic.UI.customerservice.CustomerServiceActivity;
import com.linkloving.dyh08.logic.UI.device.DeviceActivity;
import com.linkloving.dyh08.logic.UI.device.alarm.AlarmActivity;
import com.linkloving.dyh08.logic.UI.device.alarm.SetAlarmActivity;
import com.linkloving.dyh08.logic.UI.device.firmware.FirmwareUpdateActivity;
import com.linkloving.dyh08.logic.UI.device.handup.HandUpActivity;
import com.linkloving.dyh08.logic.UI.device.incomingtel.IncomingTelActivity;
import com.linkloving.dyh08.logic.UI.device.longsit.LongSitActivity;
import com.linkloving.dyh08.logic.UI.device.movedevice.MoveDeviceActivity;
import com.linkloving.dyh08.logic.UI.device.power.PowerActivity;
import com.linkloving.dyh08.logic.UI.distance.DistanceActivity;
import com.linkloving.dyh08.logic.UI.goal.SportGoalActivity;
import com.linkloving.dyh08.logic.UI.height.HeightActivity;
import com.linkloving.dyh08.logic.UI.launch.AppStartActivity;
import com.linkloving.dyh08.logic.UI.launch.LoginFromPhoneActivity;
import com.linkloving.dyh08.logic.UI.launch.dto.UserRegisterDTO;
import com.linkloving.dyh08.logic.UI.launch.register.BodyActivity;
import com.linkloving.dyh08.logic.UI.launch.register.BrithActivity;
import com.linkloving.dyh08.logic.UI.launch.register.CommonWebActivity;
import com.linkloving.dyh08.logic.UI.launch.register.PassWordActivity;
import com.linkloving.dyh08.logic.UI.launch.register.RegisterPhoneActivity;
import com.linkloving.dyh08.logic.UI.launch.register.RegisteredSuccessActivity;
import com.linkloving.dyh08.logic.UI.launch.register.SexActivity;
import com.linkloving.dyh08.logic.UI.launch.register.UpdataAvatarActivity;
import com.linkloving.dyh08.logic.UI.login.BirthdayActivity;
import com.linkloving.dyh08.logic.UI.login.GenderActivity;
import com.linkloving.dyh08.logic.UI.login.UsernameActivity;
import com.linkloving.dyh08.logic.UI.main.BundTypeActivity;
import com.linkloving.dyh08.logic.UI.main.PortalActivity;
import com.linkloving.dyh08.logic.UI.main.bundband.bundbandstep1;
import com.linkloving.dyh08.logic.UI.Social.SocialActivity;
import com.linkloving.dyh08.logic.UI.more.MoreActivity;
import com.linkloving.dyh08.logic.UI.setting.GeneralActivity;
import com.linkloving.dyh08.logic.UI.setting.NotificationSettingActivity;
import com.linkloving.dyh08.logic.UI.setting.SettingActivity;
import com.linkloving.dyh08.logic.UI.settings.PersonalInfoActivity;
import com.linkloving.dyh08.logic.UI.sleep.SleepActivity;
import com.linkloving.dyh08.logic.UI.step.StepActivity;
import com.linkloving.dyh08.logic.UI.weight.WeightActivity;
import com.linkloving.dyh08.logic.UI.Groups.baidu.GroupsActivity;
import com.linkloving.dyh08.logic.UI.workout.GooglemapActivity;
import com.linkloving.dyh08.logic.UI.workout.trackshow.WorkoutActivity;
import com.linkloving.dyh08.logic.dto.UserEntity;
import com.linkloving.dyh08.utils.ToolKits;
import com.linkloving.utils.CommonUtils;

//import com.linkloving.dyh08.logic.UI.step.StepActivity;

/**
 * Created by zkx on 2016/2/24.
 */
public class IntentFactory {

    public static Intent createPrssedHomeKeyIntent() {
        Intent i = new Intent("android.intent.action.MAIN");
        i.setFlags(270532608);
        i.addCategory("android.intent.category.HOME");
        return i;
    }

    /**
     * 创建进入设置身体信息的方法
     */
    public static void startBodyActivityIntent(Activity activity) {
        Intent intent = new Intent(activity, BodyActivity.class);
        activity.startActivity(intent);
    }

    public static void startAvatarActivityIntent(Activity activity,int tag) {
        Intent intent = new Intent(activity, UpdataAvatarActivity.class);
        intent.putExtra("tag",tag); //1是注册
        activity.startActivity(intent);
    }

    public static void startSexActivityIntent(Activity activity,int tag) {
        Intent intent = new Intent(activity, SexActivity.class);
        intent.putExtra("tag",tag); //1是注册
        activity.startActivity(intent);
    }

    public static void startHeightActivityIntent(Activity activity,int tag) {
        Intent intent = new Intent(activity, HeightActivity.class);
        intent.putExtra("tag",tag); //1是注册
        activity.startActivity(intent);
    }
    public static void startAPPstartActivity(Activity activity) {
        Intent intent = new Intent(activity, AppStartActivity.class);
        activity.startActivity(intent);
    }


    public static void startWeightActivityIntent(Activity activity,int tag) {
        Intent intent = new Intent(activity, WeightActivity.class);
        intent.putExtra("tag",tag); //1是注册
        activity.startActivity(intent);
    }

    public static void startGoogleActivity(Activity activity) {
        Intent intent = new Intent(activity, GooglemapActivity.class);
        activity.startActivity(intent);
    }

    public static void startBrithActivityIntent(Activity activity,int tag) {
        Intent intent = new Intent(activity, BrithActivity.class);
        intent.putExtra("tag",tag); //1是注册
        activity.startActivity(intent);
    }


    public static Intent star_DeviceActivityIntent(Activity thisActivity,int type) {
        Intent deviceintent = new Intent(thisActivity, DeviceActivity.class);
        deviceintent.putExtra("type", type);
        return deviceintent;
    }

    public static Intent start_AlarmActivityIntent(Activity activity) {
    //无声闹钟
        Intent alarmIntent = new Intent(activity, AlarmActivity.class);
        return alarmIntent;
    }

    public static void start_LongSitActivityIntent(Activity activity) {
//        久坐提醒
        Intent longsitIntent = new Intent(activity, LongSitActivity.class);
        activity.startActivity(longsitIntent);
    }



    public static void start_HandUpActivity(Activity activity) {
    //勿扰模式
        Intent intent = new Intent(activity, HandUpActivity.class);
        activity.startActivity(intent);
    }

    public static void start_IncomingTel(Activity activity) {
    //来电提醒
        Intent intent = new Intent(activity, IncomingTelActivity.class);
        activity.startActivity(intent);
    }

    public static void start_PowerActivity(Activity activity) {
    //电源管理
        Intent intent = new Intent(activity, PowerActivity.class);
        activity.startActivity(intent);
    }

    public static void start_FirmwareUpdateActivity(Activity activity) {
    //固件更新
        Intent intent = new Intent(activity, FirmwareUpdateActivity.class);
        activity.startActivity(intent);
    }

    public static void start_MoveDeviceActivity(Activity activity) {
    //删除设备
        Intent intent = new Intent(activity, MoveDeviceActivity.class);
        activity.startActivity(intent);
    }

    public static void start_SetAlarmActivity(Activity activity, int i, String string, String strRepeat) {
        Intent intent = new Intent(activity, SetAlarmActivity.class);
        intent.putExtra("str", i);
        intent.putExtra("str_tv", string);
        intent.putExtra("strRepeat", strRepeat);
        activity.startActivityForResult(intent, i);
    }
    //进入主界面
    public static Intent createPortalActivityIntent(Activity activity) {
        Intent intent = new Intent(activity, PortalActivity.class);
        return intent;
    }
    public static void startPortalActivityIntent(Activity activity) {
        Intent intent = new Intent(activity, PortalActivity.class);
        activity.startActivity(intent);
    }

    public static void start_LoginPage_Activity(Activity activity) {

        Intent intent = new Intent(activity, LoginFromPhoneActivity.class);

        activity.startActivity(intent);

    }

    public static void start_RegisterActivity_Activity(Activity activity) {
        Intent intent = new Intent(activity, RegisterPhoneActivity.class);
        activity.startActivity(intent);
    }

    public static Intent start_RegisterSuccess_Activity(Activity thisActivity, String u) {
        Intent intent = new Intent(thisActivity, RegisteredSuccessActivity.class);
        intent.putExtra("__UserRegisterDTO__", u);
        return intent;
    }

    public static Intent start_BodyActivityIntent(Activity thisActivity) {
        Intent intent = new Intent(thisActivity, BodyActivity.class);
        return intent;
    }

    /**
     * 解析intent传过来的RegisterActivity数据
     * @param intent
     * @return
     */
    public static UserRegisterDTO parseRegisterSuccessIntent(Intent intent) {
        return (UserRegisterDTO) intent.getSerializableExtra("__UserRegisterDTO__");
    }


    /**
     * 打开GoalActivity的Intent构造方法
     */
    public static void startGoalActivityIntent(Activity thisActivity, UserEntity user) {
        Intent intent = new Intent(thisActivity, SportGoalActivity.class);
        double bmi = ToolKits.getBMI(CommonUtils.getFloatValue(user.getUserBase().getUser_weight() + ""), CommonUtils.getIntValue(user.getUserBase().getUser_height()));
        intent.putExtra("user_sex", user.getUserBase().getUser_sex());
        intent.putExtra("user_BMI", bmi + "");
        intent.putExtra("user_BMIDesc", ToolKits.getBMIDesc(thisActivity, bmi));
        intent.putExtra("user_target", user.getUserBase().getPlay_calory() + "");
        thisActivity.startActivity(intent);
    }

    public static void start_CustomerService_ActivityIntent(Activity activity, int index) {
        Intent intent = new Intent(activity, CustomerServiceActivity.class);
        intent.putExtra("__inedx__", index);
        activity.startActivity(intent);
    }

    /**
     * 通用WebActivity
     * @param thisActivity
     * @param url
     * @return
     */
    public static Intent createCommonWebActivityIntent(Activity thisActivity, String url) {
        Intent intent = new Intent(thisActivity, CommonWebActivity.class);
        intent.putExtra("__url__", url);
        return intent;
    }

    public static String parseCommonWebIntent(Intent intent) {
        return intent.getStringExtra("__url__");
    }
//
//    //登陆主界面
//    public static Intent createLoginPageActivity(Activity appStartActivity) {
//        Intent intent = new Intent(appStartActivity, ThirdLoginActivity.class);
//        return intent;
//    }

    public static Intent start_MoreActivityIntent(Activity activity) {
//        无声闹钟
        Intent alarmIntent = new Intent(activity, MoreActivity.class);
        return alarmIntent;
    }

    public static int[] parseUserDetialActivityIntent(Intent intent) {
        int[] tmp = new int[3];
        tmp[0] = intent.getExtras().getInt("__user_id__");
        tmp[1] = intent.getExtras().getInt("__to_user_id__");
        tmp[2] = intent.getExtras().getInt("__check_tag_");
        return tmp;
    }

    //绑定手环
    public static Intent startActivityBundBand(Context context) {
        Intent intent = new Intent(context, bundbandstep1.class);
        return intent;
    }

    public static void startBundTypeActivity(Activity activity) {
        Intent intent = new Intent(activity, BundTypeActivity.class);
        activity.startActivityForResult(intent,CommParams.REQUEST_CODE_BOUND);
    }

    public static Intent create_Password_ActivityIntent(RegisterPhoneActivity registerPhoneActivity) {
        Intent intent = new Intent(registerPhoneActivity, PassWordActivity.class);
        return intent;
    }

/*------------------------------------------------------*/
    //蓝牙
    public static void start_Bluetooth(Activity activity){
        Intent intent = new Intent(activity, BluetoothActivity.class);
        activity.startActivity(intent);
    }

    public static void start_step(Activity activity){
        Intent intent = new Intent(activity, StepActivity.class);
        activity.startActivity(intent);
    }
    public static void start_calories(Activity activity){
        Intent intent = new Intent(activity, CaloriesActivity.class);
        activity.startActivity(intent);
    }
    public static void start_distance(Activity activity){
        Intent intent = new Intent(activity, DistanceActivity.class);
        activity.startActivity(intent);
    }
    public static void start_sleep(Activity activity){
        Intent intent = new Intent(activity, SleepActivity.class);
        activity.startActivity(intent);
    }

    //设置信息
    public static void startSetting(Activity activity){
        Intent intent = new Intent(activity, SettingActivity.class);
        activity.startActivity(intent);
    }

    public static void startNotification(Activity activity){
        Intent intent = new Intent(activity, NotificationSettingActivity.class);
        activity.startActivity(intent);
    }

    public static void startGeneral(Activity activity){
        Intent intent = new Intent(activity, GeneralActivity.class);
        activity.startActivity(intent);
    }

    //设置信息
    public static void start2SettingsActivity(Activity activity){
        Intent intent = new Intent(activity, PersonalInfoActivity.class);
        activity.startActivity(intent);
    }

    //修改身高
    public static void start2HeightActivity(Activity activity){
        Intent intent = new Intent(activity, HeightActivity.class);
        activity.startActivity(intent);
    }

    //修改体重
    public static void start2WeightActivity(Activity activity){
        Intent intent = new Intent(activity, WeightActivity.class);
        activity.startActivity(intent);
    }

    //开启groups
    public static void start2GroupsActivity(Activity activity){
        Intent intent = new Intent(activity, GroupsActivity.class);
        activity.startActivity(intent);
    }

    /**
     * map就是改动后的groups,
      * @param activity
     */
    // 开启map
    public static  void start2MapActivity(Activity activity){
        Intent intent = new Intent(activity, SocialActivity.class);
        activity.startActivity(intent);
    }


    //开启workout
    public static void start2WorkoutActivity(Activity activity){
        Intent intent = new Intent(activity, WorkoutActivity.class);
        activity.startActivity(intent);
    }
    public static void start2GroupsDetailActivity(Activity activity){
        Intent intent = new Intent(activity, GroupsDetailsActivity.class);
        activity.startActivity(intent);
    }

    /*login界面*/

    public static void startUsername(Activity activity){
        Intent intent = new Intent(activity, UsernameActivity.class);
        activity.startActivity(intent);
    }

    public static void startBirthday(Activity activity){
        Intent intent = new Intent(activity, BirthdayActivity.class);
        activity.startActivity(intent);
    }
    public static void startGender(Activity activity){
        Intent intent = new Intent(activity, GenderActivity.class);
        activity.startActivity(intent);
    }

    public static void startWeight(Activity activity){
        Intent intent = new Intent(activity, com.linkloving.dyh08.logic.UI.login.WeightActivity.class);
        activity.startActivity(intent);
    }
    public static void startNewWeight(Activity activity){
        Intent intent = new Intent(activity, com.linkloving.dyh08.logic.UI.login.WeightNewActivity.class);
        activity.startActivity(intent);
    }

    public static void startHeight(Activity activity){
        Intent intent = new Intent(activity, com.linkloving.dyh08.logic.UI.login.HeightActivity.class);
        activity.startActivity(intent);
    }
    public static void startNewHeight(Activity activity){
        Intent intent = new Intent(activity, com.linkloving.dyh08.logic.UI.login.HeightNewActivity.class);
        activity.startActivity(intent);
    }
    public static void startWearing(Activity activity){
        Intent intent = new Intent(activity, com.linkloving.dyh08.logic.UI.login.WearingActivity.class);
        activity.startActivity(intent);
    }

    public static void startLoginFinish(Activity activity) {
        Intent intent = new Intent(activity, com.linkloving.dyh08.logic.UI.login.LoginFinishActivity.class);
        activity.startActivity(intent);
    }
    public static void startBindActivity1(Activity activity) {
        Intent intent = new Intent(activity, BluetoothBindActivity.class);
        activity.startActivity(intent);
    }
    public static void startBindActivity2(Activity activity) {
        Intent intent = new Intent(activity, BluetoothDisconnectActivity.class);
        activity.startActivity(intent);
    }
    public static void startBindActivity3(Activity activity) {
        Intent intent = new Intent(activity, BluetoothBindActivity3.class);
        activity.startActivity(intent);
    }
    public static void startHeartrateActivity(Activity activity) {
        Intent intent = new Intent(activity, HeartRateActivity.class);
        activity.startActivity(intent);
    }

}
