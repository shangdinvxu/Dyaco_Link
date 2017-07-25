package com.linkloving.dyh08.logic.UI.setting;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.linkloving.dyh08.R;

/**
 * Created by Daniel.Xu on 2016/10/16.
 */

public class Myadapter extends BaseAdapter {
    private Context context ;
    private String[] strings ;


    public Myadapter(Context context,String[] obj){
        this.context=context;
        this.strings = obj ;
    }

    @Override
    public int getCount() {
        return strings.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(context, R.layout.tw_setting_detail, null);
        TextView text = (TextView) view.findViewById(R.id.textView);
        text.setText(strings[position].toString());
        return view;
    }
}
