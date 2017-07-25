package com.linkloving.dyh08.utils;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

/**
 * Created by Daniel.Xu on 2017/3/15.
 */

public class GoogleApiUtils {

    public static boolean isGooglePlaySupport(Activity activity) {

        boolean googleserviceFlag = true;
        try {
            Class.forName("com.google.android.maps.MapActivity");
        } catch (Exception e) {
            googleserviceFlag = false;
        }
        return googleserviceFlag ;
    }

    public  static boolean isGoogleMapsInstalled(Activity activity) {
        try {
            ApplicationInfo info = activity.getPackageManager().getApplicationInfo(
                    "com.google.android.gms.maps", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
