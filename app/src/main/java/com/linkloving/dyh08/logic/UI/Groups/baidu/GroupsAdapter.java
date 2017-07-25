package com.linkloving.dyh08.logic.UI.Groups.baidu;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.linkloving.band.dto.SportRecord;
import com.linkloving.band.ui.DetailChartCountData;
import com.linkloving.dyh08.MyApplication;
import com.linkloving.dyh08.R;
import com.linkloving.dyh08.db.sport.UserDeviceRecord;
import com.linkloving.dyh08.logic.UI.login.LoginFinishActivity;
import com.linkloving.dyh08.logic.UI.workout.Greendao.TraceGreendao;
import com.linkloving.dyh08.logic.dto.UserEntity;
import com.linkloving.dyh08.utils.CommonUtils;
import com.linkloving.dyh08.utils.logUtils.MyLog;
import com.linkloving.utils.TimeZoneHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import Trace.GreenDao.DaoMaster;
import Trace.GreenDao.Note;
import rx.Observable;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by Administrator on 2016/8/11.
 */

public class GroupsAdapter extends BaseAdapter implements StickyListHeadersAdapter, SectionIndexer {
    private static final String TAG = GroupsAdapter.class.getSimpleName();
    private Context mContext;
    private LayoutInflater mInflater;
    private String[] mMonthData;
    private TraceGreendao traGreendao ;
    private SQLiteDatabase db;
    private DaoMaster.DevOpenHelper devOpenHelper;
    public final List<Note>startTimeList;
    public final List<Note> endTimeList;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
    private SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final int user_id;
    private long itemDuration;
    private Date itemStartDate ;
    private Date itemEndDate ;
    private String duration;
    private ArrayList<Integer> sectionIndices;
    public final List<Note> list;
    private DetailChartCountData count;
    private final UserEntity userEntity;

//    private final List<Note> workDataNotes;

    public GroupsAdapter(Context context) {
        this.mContext = context;
        userEntity = MyApplication.getInstance(mContext).getLocalUserInfoProvider();

        user_id   = userEntity.getUser_id();
        mInflater = LayoutInflater.from(mContext);
        //源数据
        devOpenHelper = new DaoMaster.DevOpenHelper(context, "Note", null);
        db = devOpenHelper.getReadableDatabase();
        traGreendao = new TraceGreendao(context,db);
        list = traGreendao.searchAllMonthtimes();
        startTimeList = traGreendao.searchAllStarttime();
        endTimeList = traGreendao.searchAllEndTime();



        mMonthData = new String[list.size()];
        for (int i = 0 ;i< list.size();i++){
            mMonthData[i]= list.get(i).getDate();
            MyLog.e(TAG,"mMonthData[i].toString():"+mMonthData[i]);
        }

        sort sort = new sort();
        Collections.sort(startTimeList,sort);
        Collections.sort(endTimeList,sort);
    }

 /*   private int[] getSectionIndices() {

        //需要知道每个头在源数据里面的索引位置
        sectionIndices = new ArrayList<Integer>();
        String lastChar = mMonthData[0];
        sectionIndices.add(0);

        for(int i = 1;i< mMonthData.length;i++){
            if(mMonthData[i] != lastChar){
                sectionIndices.add(i);
            }
        }
        int[] sections = new int[sectionIndices.size()];
        for(int i = 0;i< sectionIndices.size();i++){
            sections[i] = sectionIndices.get(i);
        }
        return sections;
    }

    *//**
     * 通过下标的索引 获取Head上的数据集
     * @return
     *//*
    public String [] getSectionLetters() {
        String[] letters = new String[mSectionIndices.length];
        for(int i = 0;i<mSectionIndices.length;i++){
            letters[i] = mMonthData[mSectionIndices[i]];
        }
        return letters;
    }*/


    public View getHeaderView(int i, View convertView, ViewGroup parent) {
        final HeaderViewHolder holder;
        if(convertView == null){
            holder = new HeaderViewHolder();
            convertView = mInflater.inflate(R.layout.tw_groups_itemheard, parent, false);
            holder.heardYearMonth = (TextView) convertView.findViewById(R.id.groupsYearMonth);
           holder.activityNumbers = (TextView) convertView.findViewById(R.id.activityMonuts);
            convertView.setTag(holder);
        }else{
            holder = (HeaderViewHolder) convertView.getTag();
        }
        String yearMonth = mMonthData[i];
        MyLog.e(TAG, yearMonth);
        holder.heardYearMonth.setText(mMonthData[i]);
        int amount = 0 ;
        for (Note listitem:list) {
            if (listitem.getDate().equals(yearMonth)){
                amount = amount +1 ;
            }
        }
        return convertView;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        MyLog.e(TAG,"getView的position是---------"+position);
//        if (convertView == null) {
            holder = new ViewHolder();
          View  convertView1 = mInflater.inflate(R.layout.sticklist_item_layout, parent, false);
            holder.dataTime = (AutoCompleteTextView) convertView1.findViewById(R.id.dataTime);
            holder.distance = (AutoCompleteTextView) convertView1.findViewById(R.id.distance);
            holder.duration = (AutoCompleteTextView) convertView1.findViewById(R.id.duration);
            holder.avgSpeed = (AutoCompleteTextView) convertView1.findViewById(R.id.avgSpeed);
            holder.mapState = (ImageView) convertView1.findViewById(R.id.mapState);
        if (startTimeList.get(position).getLatitude()!=null&&startTimeList.get(position).getLatitude()==1){
            holder.mapState.setBackgroundResource(R.mipmap.mapoff);
        }else if (startTimeList.get(position).getLatitude()!=null&&startTimeList.get(position).getLatitude()==0
                &&startTimeList.get(position).getLatitude()==2){
            holder.mapState.setBackgroundResource(R.mipmap.mapon);
        }
        MyLog.e(TAG,startTimeList.get(position).getDate()+"-------------startTimeList的getDate");
        holder.dataTime.setText(simpleDateFormat2.format(startTimeList.get(position).getStartDate()));
//      每个item的开始时间和结束时间
        itemStartDate = startTimeList.get(position).getStartDate();
        itemEndDate = endTimeList.get(position).getStartDate();
        long endTime = itemEndDate.getTime();
        long startTime = itemStartDate.getTime();
//        时间间隔转UTC
        itemDuration = Math.abs(endTime - startTime);
        MyLog.e(TAG, itemDuration + "itemDuration");
        Date date = new Date(itemDuration);
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        instance.add(Calendar.MINUTE,-TimeZoneHelper.getTimeZoneOffsetMinute());
        Date time = instance.getTime();
        duration = simpleDateFormat.format(time);
        float distance = 0 ;
        ArrayList<SportRecord> sportRecordArrayList = UserDeviceRecord.
                findHistoryChartwithHMS(mContext, String.valueOf(user_id), itemStartDate, itemEndDate);
        if (sportRecordArrayList.size()==0){
            distance = 0 ;
        }else{
            for (SportRecord sportRecordArray:sportRecordArrayList) {
                MyLog.e(TAG,"distance"+sportRecordArray.getDistance());
                if (sportRecordArray.getState().equals("1")||sportRecordArray.getState().equals("2")||sportRecordArray.getState().equals("3"))
                distance =Integer.parseInt(sportRecordArray.getDistance())+distance ;
            }
        }

//        这一步吧distance转换成如5.3 的用来加km
        double distanceKM = CommonUtils.getDoubleValue(distance / 1000, 1);
        float hourtime = (float)itemDuration/3600000 ;
        float avgSpeed = (distance/1000) /hourtime;
        String distanceStr = new Formatter().format("%.1f", distanceKM).toString();
        holder.distance.setText(distanceStr + " km");
        holder.duration.setText(duration);
        String avgSpeedStr = new Formatter().format("%.1f", avgSpeed).toString();
        holder.avgSpeed.setText(avgSpeedStr+" km/h");
        return convertView1;
    }

    @Override
    public long getHeaderId(int position){
        MyLog.e(TAG, "heardID是:  " + mMonthData[position].charAt(0));
        String s = mMonthData[position];
//        return mMonthData[position].charAt(0);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        Date parse= null ;
        try {
             parse = simpleDateFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long time = parse.getTime();
        MyLog.e(TAG, "position" + position);
        return time ;
    }

    @Override
    public int getCount() {
        return startTimeList.size() ;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    class HeaderViewHolder {
        TextView heardYearMonth;
        TextView activityNumbers ;

    }

    class ViewHolder {
        AutoCompleteTextView dataTime;
        AutoCompleteTextView distance;
        AutoCompleteTextView duration;
        AutoCompleteTextView avgSpeed;
        ImageView mapState ;
    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }
}
