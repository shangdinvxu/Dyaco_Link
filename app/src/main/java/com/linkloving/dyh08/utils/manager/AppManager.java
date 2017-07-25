package com.linkloving.dyh08.utils.manager;

import android.app.ActivityManager;
import android.content.Context;

import com.linkloving.dyh08.utils.logUtils.MyLog;

import java.util.ArrayList;

/**
 * Created by Daniel.Xu on 2017/3/10.
 */

public class AppManager {

    public static boolean isServiceRunning(Context context, String string) {
        ActivityManager myManager = (ActivityManager) context
                .getApplicationContext().getSystemService(
                        Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager
                .getRunningServices(30);
        for (int i = 0; i < runningService.size(); i++) {
//            MyLog.e("PortalActivity",runningService.get(i).service.getClassName().toString());
            if (runningService.get(i).service.getClassName().toString()
                    .equals(string)) {
                return true;
            }
        }
        return false;
    }
}
