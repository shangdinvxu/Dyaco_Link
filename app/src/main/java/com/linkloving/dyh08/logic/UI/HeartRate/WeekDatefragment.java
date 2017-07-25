package com.linkloving.dyh08.logic.UI.HeartRate;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.linkloving.dyh08.R;
import com.linkloving.dyh08.ViewUtils.Wheelview.WheelView;
import com.linkloving.dyh08.logic.UI.step.IDataChangeListener;
import com.linkloving.dyh08.logic.UI.step.WeekViewFragment;
import com.linkloving.dyh08.logic.UI.step.WeekchartviewFragment;
import com.linkloving.dyh08.utils.ToolKits;
import com.linkloving.dyh08.utils.logUtils.MyLog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Daniel.Xu on 2016/11/3.
 */

public class    WeekDatefragment extends Fragment{
    public static final String TAG = WeekDatefragment.class.getSimpleName();
    private IDataChangeListener dataChangeListener;
    private String weekitem;
    private ImageView step_week_next;
    private WheelView wva;
    //    private IGetDate getData ;
    public void setDataChangeListener(IDataChangeListener dataChangeListener){
        this.dataChangeListener = dataChangeListener;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tw_week_view_fragment, container, false);
        LinearLayout totalView = (LinearLayout) view.findViewById(R.id.step_linear_day);
        totalView.setPadding(0,200,0,0);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().add(new WeekViewFragment(),"WeekViewFragment");
        wva = (WheelView) view.findViewById(R.id.step_week_wheelView);
        wva.setOffset(1);
        final List<String> weeklist = getWeek();
        for(String str:weeklist){
            Log.e(TAG, "weeklist-------" + str);
        }
        wva.setItems(weeklist);
        MyLog.e(TAG,weeklist.size()+"");
        wva.setSeletion(weeklist.size()-1);
        step_week_next = (ImageView) view.findViewById(R.id.step_week_next);
        step_week_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String seletedItem = wva.getSeletedItem();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                WeekFragment weekFragment = new WeekFragment();
                Bundle bundle = new Bundle();
                bundle.putString("date", seletedItem);
                MyLog.e(TAG, seletedItem+ "seletedItem");
                weekFragment.setArguments(bundle);
                transaction.replace(R.id.middle_framelayout, weekFragment);
                fragmentManager.popBackStack(null,1);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        wva.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                Log.e(TAG, "onSelected-------" + item);

                dataChangeListener.onDataChange(item);

            }
        });
        return view ;
    }

    //    获取周数集合
    public List<String> getWeek(){
        List<String> week =  new ArrayList<String>();
        Calendar rightNow = Calendar.getInstance();
        SimpleDateFormat sim = new SimpleDateFormat("yyyy/MM/dd");
        wva.setType(WheelView.TYPE_WEEK);
        for (int i=0;i<45;i++){
            String date = sim.format(rightNow.getTime());
            week.add(date);
            rightNow.add(Calendar.DATE,-7);
        }
        Collections.reverse(week);
        return  week ;
    }
}
