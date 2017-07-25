package com.linkloving.dyh08.logic.UI.sleep;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.linkloving.dyh08.R;
import com.linkloving.dyh08.ViewUtils.Wheelview.WheelView;
import com.linkloving.dyh08.logic.UI.sleep.asyncktask.DaySportAsynck;
import com.linkloving.dyh08.utils.logUtils.MyLog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by leo.wang on 2016/7/28.
 */
public class MonthViewFragment extends Fragment {

    private static final String TAG = MonthViewFragment.class.getSimpleName();
    private com.linkloving.dyh08.logic.UI.sleep.IDataChangeListener dataChangeListener;
    private WheelView wva;
    private IClickNextlistener iClickNextlistener ;

    public void setDataChangeListener(com.linkloving.dyh08.logic.UI.sleep.IDataChangeListener dataChangeListener){
        this.dataChangeListener = dataChangeListener;
    }

    public void setClickNextListener(IClickNextlistener iClickNextlistener){
        this.iClickNextlistener = iClickNextlistener ;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tw_month_view_fragment, container, false);
        ButterKnife.inject(this, view);
        ImageView step_week_next = (ImageView) view.findViewById(R.id.step_month_next);

        wva = (WheelView) view.findViewById(R.id.step_month_wheelView);
        wva.setOffset(1);
        List<String> monthList = getMonth();
        wva.setSeletion(monthList.size()-1);
        wva.setItems(monthList);
        wva.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String month) {
                dataChangeListener.onDataChange(month);
                Log.e("MonthViewFragment", "选中的月:" + month);
            }
        });
        step_week_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String seletedItem = wva.getSeletedItem();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                Month2CalendarFragment monthchartviewFragment = new Month2CalendarFragment();
                Bundle bundle = new Bundle();
                bundle.putString("monthDate", seletedItem);
                monthchartviewFragment.setArguments(bundle);
                transaction.replace(R.id.step_middle, monthchartviewFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                iClickNextlistener.nextFragment();
            }
        });

        return view ;
    }
    //    获取周数集合
    public List<String> getMonth(){
        List<String> month =  new ArrayList<String>();
        Calendar rightNow = Calendar.getInstance();
        SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM");
        wva.setType(WheelView.TYPE_MONTH);
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD);
//        rightNow.add(Calendar.DATE,-49);

        for (int i=0;i<40;i++){

            String date = sim.format(rightNow.getTime());
            month.add(date);
            rightNow.add(Calendar.MONTH,-1);
        }
        Collections.reverse(month);
        return  month ;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return false;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

