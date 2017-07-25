package com.linkloving.dyh08.logic.UI.sleep;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.linkloving.dyh08.R;
import com.linkloving.dyh08.ViewUtils.Wheelview.WheelView;
import com.linkloving.dyh08.utils.ToolKits;
import com.linkloving.dyh08.utils.logUtils.MyLog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by leo.wang on 2016/7/28.
 */
public class WeekViewFragment extends Fragment {
    public static final String TAG = WeekViewFragment.class.getSimpleName();
    private com.linkloving.dyh08.logic.UI.sleep.IDataChangeListener dataChangeListener;
    private String weekitem;
    private ImageView step_week_next;
    private WheelView wva;
    //    private IGetDate getData ;
    private IClickNextlistener iClickNextlistener ;
    public void setDataChangeListener(com.linkloving.dyh08.logic.UI.sleep.IDataChangeListener dataChangeListener){
        this.dataChangeListener = dataChangeListener;
    }
    public void setSleepNextListener(IClickNextlistener iClickNextlistener){
        this.iClickNextlistener = iClickNextlistener ;

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tw_week_view_fragment, container, false);
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
                com.linkloving.dyh08.logic.UI.sleep.WeekchartviewFragment weekchartviewFragment =  new WeekchartviewFragment();
                Bundle bundle = new Bundle();
                bundle.putString("date", seletedItem);
                MyLog.e(TAG, seletedItem + "seletedItem");
                weekchartviewFragment.setArguments(bundle);
                transaction.replace(R.id.step_middle, weekchartviewFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                iClickNextlistener.nextFragment();
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
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD);
//        rightNow.add(Calendar.DATE,-49);
        for (int i=0;i<45;i++){
            String date = sim.format(rightNow.getTime());
////        获取每周的第一天日期
            Date mondayOfThisWeek = ToolKits.getFirstSundayOfThisWeek(rightNow.getTime());
////        获取每周的周末日期
//            Date sundayofThisWeek = ToolKits.getStaurdayofThisWeek(rightNow.getTime());
            week.add(date);
            rightNow.add(Calendar.DATE,-7);
        }
        Collections.reverse(week);
        return  week ;
    }

}
