package com.linkloving.dyh08.utils.sportUtils;

import android.content.Context;

import com.linkloving.dyh08.logic.UI.main.PortalActivity;
import com.linkloving.dyh08.utils.ToolKits;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Daniel.Xu on 2017/6/5.
 */

public class CaloriesUtils {

    /**
     * 基础卡路里
     * @param context
     * @return
     */
    public static int getCaloriesNow(Context context){
        ToolKits toolKits = new ToolKits();
        int calorieseveryday = toolKits.getCalories(context);
        Date dateToday = new Date();
        SimpleDateFormat hh = new SimpleDateFormat("HH", Locale.getDefault());
        String HH = hh.format(dateToday);
        SimpleDateFormat mm = new SimpleDateFormat("mm", Locale.getDefault());
        String MM = mm.format(dateToday);
        int caloriesNow = calorieseveryday * (Integer.parseInt(HH) * 60 + Integer.parseInt(MM)) / 1440;
        return caloriesNow ;
    }
}
