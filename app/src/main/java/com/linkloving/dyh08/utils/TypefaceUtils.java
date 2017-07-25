package com.linkloving.dyh08.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

/**
 * Created by Daniel.Xu on 2016/9/7.
 */
public class TypefaceUtils {

    public  static  void  setNumberType( Context context ,TextView view){
        AssetManager assets =  context.getAssets();
        //        Typeface tf=Typeface.createFromAsset(mgr, "fonts/ttf.ttf");//根据路径得到Typeface
        Typeface typeface = Typeface.createFromAsset(assets, "fonts/number.ttf");
        view.setTypeface(typeface);
    }
    public  static  void  setNumberType( Context context ,AutoCompleteTextView view){
        AssetManager assets =  context.getAssets();
        Typeface typeface = Typeface.createFromAsset(assets, "fonts/number.ttf");
        view.setTypeface(typeface);
    }
}
