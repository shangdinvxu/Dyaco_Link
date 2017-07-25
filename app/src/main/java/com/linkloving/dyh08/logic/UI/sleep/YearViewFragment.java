package com.linkloving.dyh08.logic.UI.sleep;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.linkloving.dyh08.R;
import com.linkloving.dyh08.ViewUtils.Wheelview.WheelView;
import com.linkloving.dyh08.logic.UI.calories.*;
import com.linkloving.dyh08.logic.UI.calories.YearchartviewFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by leo.wang on 2016/7/28.
 */
public class YearViewFragment extends Fragment {
    private final int YEAR = 1;
    TextView step_tv_date;

    private com.linkloving.dyh08.logic.UI.sleep.IDataChangeListener dataChangeListener;
    private WheelView wva;

    public void setDataChangeListener(com.linkloving.dyh08.logic.UI.sleep.IDataChangeListener dataChangeListener) {
        this.dataChangeListener = dataChangeListener;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tw_year_view_fragment, container, false);
        wva = (WheelView) view.findViewById(R.id.step_year_wheelView);
        ImageView step_week_next = (ImageView) view.findViewById(R.id.step_year_next);

        List<String> year = getYear();
        wva.setOffset(1);
        wva.setItems(year);
        wva.setSeletion(year.size() - 1);
        wva.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String year) {
                dataChangeListener.onDataChange(year);
            }
        });
        step_week_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String seletedItem = wva.getSeletedItem();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                com.linkloving.dyh08.logic.UI.calories.YearchartviewFragment yearchartviewFragment = new YearchartviewFragment();
                Bundle bundle = new Bundle();
                bundle.putString("yearDate", seletedItem);

                yearchartviewFragment.setArguments(bundle);
                transaction.replace(R.id.step_middle, yearchartviewFragment);
                transaction.commit();
            }
        });

        return view ;
    }

    public List<String> getYear(){

        List<String> year = new ArrayList<>();

        for (int i=2000;i<= Calendar.getInstance().get(Calendar.YEAR);i++) {
            year.add(i+"");
        }
        return  year;
    }

}
