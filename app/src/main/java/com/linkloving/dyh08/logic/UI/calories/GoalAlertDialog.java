package com.linkloving.dyh08.logic.UI.calories;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.linkloving.dyh08.R;
import com.linkloving.dyh08.ViewUtils.Wheelview.WheelView;

import java.util.List;

/**
 * Created by Administrator on 2016/8/9.
 */
public class GoalAlertDialog extends AlertDialog {
    WheelView wheelView;


    public GoalAlertDialog(Context context, List<String> valueList) {
        super(context);
        LayoutInflater inflater = getLayoutInflater();
        final View layout = inflater.inflate(R.layout.goalwheel, (LinearLayout) findViewById(R.id.goal_view));
        setView(layout);
        wheelView = (WheelView)findViewById(R.id.goal_wheelView);
        wheelView.setOffset(1);
        wheelView.setItems(valueList);
    }

    public WheelView getWheelView(){
        return wheelView;
    }


}
