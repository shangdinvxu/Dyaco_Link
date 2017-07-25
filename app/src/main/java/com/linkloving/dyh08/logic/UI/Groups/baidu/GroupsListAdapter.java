//package com.linkloving.dyh08.logic.UI.Groups.baidu;
//
//import android.content.Context;
//import android.database.sqlite.SQLiteDatabase;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AutoCompleteTextView;
//import android.widget.BaseAdapter;
//import android.widget.ListAdapter;
//
//import com.linkloving.band.dto.SportRecord;
//import com.linkloving.dyh08.MyApplication;
//import com.linkloving.dyh08.R;
//import com.linkloving.dyh08.db.sport.UserDeviceRecord;
//import com.linkloving.dyh08.logic.UI.workout.Greendao.TraceGreendao;
//import com.linkloving.dyh08.logic.UI.workout.trackshow.TrackUploadFragment;
//import com.linkloving.dyh08.utils.CommonUtils;
//import com.linkloving.dyh08.utils.logUtils.MyLog;
//import com.linkloving.utils.TimeZoneHelper;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//import java.util.TimeZone;
//
//import Trace.GreenDao.DaoMaster;
//import Trace.GreenDao.Note;
//
///**
// * Created by Daniel.Xu on 2016/8/18.
// */
//public class GroupsListAdapter extends BaseAdapter {
//    private static final String TAG = GroupsListAdapter.class.getSimpleName();
//    private Context mcontext ;
//
//    private LayoutInflater mInflater;
//
//    private String[] mMonthData;
//    private int[] mSectionIndices; //源数据中每种类型头索引
//    private String[] mSectionLetters;
//
//    private TraceGreendao traGreendao ;
//
//    private SQLiteDatabase db;
//    private DaoMaster.DevOpenHelper devOpenHelper;
//    private final List<Note> startTimeList;
//    private final List<Note> endTimeList;
//    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
//    private final int user_id;
//    private final List<Note> list;
//    private String duration;
//
//    public long getItemDuration() {
//        return itemDuration;
//    }
//
//    public String getDuration() {
//        return duration;
//    }
//
//    public Date getItemEndDate() {
//        return itemEndDate;
//    }
//
//    public Date getItemStartDate() {
//        return itemStartDate;
//    }
//
//    private long itemDuration;
//    private Date itemStartDate ;
//    private Date itemEndDate ;
//
//    public GroupsListAdapter(Context context) {
//        this.mcontext = context ;
//        user_id = MyApplication.getInstance(mcontext).getLocalUserInfoProvider().getUser_id();
//        mInflater = LayoutInflater.from(mcontext);
//        //源数据
//        devOpenHelper = new DaoMaster.DevOpenHelper(context, "Note", null);
//        db = devOpenHelper.getReadableDatabase();
//        traGreendao = new TraceGreendao(context,db);
//        list = traGreendao.searchAllMonthtimes();
//        mMonthData = new String[list.size()];
//        for (int i = 0 ;i< list.size();i++){
//            mMonthData[i]= list.get(i).getDate();
//        }
//
//        startTimeList = traGreendao.searchAllStarttime();
//        endTimeList = traGreendao.searchAllEndTime();
//    }
//
//    @Override
//    public int getCount() {
//        return startTimeList.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return position;
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        ViewHolder holder;
//        if (convertView == null) {
//            holder = new ViewHolder();
//            convertView = mInflater.inflate(R.layout.sticklist_item_layout, parent, false);
//            holder.dataTime = (AutoCompleteTextView) convertView.findViewById(R.id.dataTime);
//            holder.distance = (AutoCompleteTextView) convertView.findViewById(R.id.distance);
//            holder.duration = (AutoCompleteTextView) convertView.findViewById(R.id.duration);
//            holder.avgSpeed = (AutoCompleteTextView) convertView.findViewById(R.id.avgSpeed);
//            convertView.setTag(holder);
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//        }
//        holder.dataTime.setText(startTimeList.get(position).getDate());
////      每个item的开始时间和结束时间
//       itemStartDate = startTimeList.get(position).getStartDate();
//         itemEndDate = endTimeList.get(position).getStartDate();
//        long endTime = itemEndDate.getTime();
//        long startTime = itemStartDate.getTime();
////        时间间隔转UTC
//        itemDuration = Math.abs(endTime - startTime);
//        MyLog.e(TAG, itemDuration +"itemDuration");
//        Date date = new Date(itemDuration);
//        Calendar instance = Calendar.getInstance();
//        instance.setTime(date);
//        instance.add(Calendar.MINUTE,-TimeZoneHelper.getTimeZoneOffsetMinute());
//        Date time = instance.getTime();
//        duration = simpleDateFormat.format(time);
//        float distance = 0 ;
//        ArrayList<SportRecord> sportRecordArrayList = UserDeviceRecord.
//                findHistoryChartwithHMS(mcontext, String.valueOf(user_id), itemStartDate, itemEndDate);
////        MyLog.e(TAG,"sportRecordArrayList"+sportRecordArrayList.get(0).toString());
//        if (sportRecordArrayList.size()==0){
//                distance = 0 ;
//        }else{
//            for (SportRecord sportRecordArray:sportRecordArrayList) {
//                MyLog.e(TAG,"distance"+sportRecordArray.getDistance());
//                distance =Integer.parseInt(sportRecordArray.getDistance())+distance ;
//            }
//        }
////        这一步吧distance转换成如5.3 的用来加km
//        MyLog.e("distance",distance+"");
//        double distanceKM = CommonUtils.getDoubleValue(distance / 1000, 1);
//        MyLog.e("itemDuration",itemDuration+"");
//        float hourtime = (float)itemDuration/360000 ;
//        MyLog.e("ossss",hourtime+"");
//        float avgSpeed = (distance/1000) /hourtime;
//        MyLog.e("distance",distanceKM+"");
//        holder.distance.setText(distance+" km");
//
//        holder.duration.setText(duration);
//        holder.avgSpeed.setText(avgSpeed+" km/h");
//        return convertView;
//
//    }
//
//    class ViewHolder {
//        AutoCompleteTextView dataTime;
//        AutoCompleteTextView distance;
//        AutoCompleteTextView duration;
//        AutoCompleteTextView avgSpeed;
//    }
//}
