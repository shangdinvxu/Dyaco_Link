package com.linkloving.dyh08.logic.UI.main.materialmenu;

import com.linkloving.dyh08.R;

/**
 * Created by leo.wang on 2016/7/25.
 */
public class Left_viewVO1 {
    public static final int Bluetooth = 0;
    public static final int Settings = Bluetooth+1;
    public static final int Workout = Settings+1;
    public static final  int map = Workout+1 ;
    public static final int Groups = map+1;
    public static final int Heart_Rate = Groups+1;
    public static final int Height = Heart_Rate+1;
    public static final int Weight = Height+1;
    public static final int Sign_out = Weight+1;

    public static int[] menuIcon = {
            R.mipmap.imageblueicon,
            R.mipmap.imageseticon,
            R.mipmap.imageworkouticon,
            R.mipmap.mapon1,
            R.mipmap.imagegroupsicon,
            R.mipmap.imageheartsicon,
            R.mipmap.imageheighticon,
            R.mipmap.imageweighticon,
//            R.mipmap.imagesignouticon
    };
    public static int[] menuText = {
           R.string.Bluetooth,
            R.string.Settings,
            R.string.Workout,
            R.string.Map,
                    R.string.Groups,
                    R.string.Heart_Rate,
                    R.string.Height,
                    R.string.Weight,
//                    R.string.Sign_Out
    };
}