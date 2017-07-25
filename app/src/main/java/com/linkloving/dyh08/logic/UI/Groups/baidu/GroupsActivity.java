package com.linkloving.dyh08.logic.UI.Groups.baidu;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.opengl.Visibility;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.linkloving.dyh08.R;
import com.linkloving.dyh08.basic.toolbar.ToolBarActivity;
import com.linkloving.dyh08.logic.UI.workout.Greendao.TraceGreendao;
import com.linkloving.dyh08.utils.logUtils.MyLog;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;

import Trace.GreenDao.DaoMaster;
import Trace.GreenDao.Note;
import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.schedulers.Schedulers;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

import static android.widget.AdapterView.*;

public class GroupsActivity extends ToolBarActivity implements Serializable {
    private static final String TAG = GroupsActivity.class.getSimpleName();
    private int[] mSectionIndices; //源数据中每种类型头索引
    private String[] mSectionLetters;

    private TraceGreendao traGreendao;

    private SQLiteDatabase db;
    private DaoMaster.DevOpenHelper devOpenHelper;
    private List<Note> startTimeList;
    private List<Note> endTimeList;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

    @InjectView(R.id.stickyList)
    StickyListHeadersListView stickyList;
    private boolean fadeHeader = true; //隐藏头
    private GroupsAdapter groupsAdapter;
    private Long deleteStartTime;
    private Long deleteEndTime;
    private View itemView;
    private List<Note> listMonth;
    private Long deleteMonthTime;
    private int lasttimePostion = -1;
    private boolean isonCreate = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tw_activity_groups);
        devOpenHelper = new DaoMaster.DevOpenHelper(GroupsActivity.this, "Note", null);
        db = devOpenHelper.getReadableDatabase();
        traGreendao = new TraceGreendao(GroupsActivity.this, db);
//        startTimeList = traGreendao.searchAllStarttime();
//        endTimeList = traGreendao.searchAllEndTime();
        groupsAdapter = new GroupsAdapter(GroupsActivity.this);
        stickyList.setAdapter(groupsAdapter);
        startTimeList = groupsAdapter.startTimeList;
        endTimeList = groupsAdapter.endTimeList;
        listMonth = groupsAdapter.list;
        initView();


    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        isonCreate = true;
    }

    protected void getIntentforActivity() {
    }

    protected void initView() {
        ButterKnife.inject(this);
        stickyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(GroupsActivity.this, "Item " + position + " clicked!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(GroupsActivity.this, GroupsShareActivity.class);
                intent.putExtra("postion", String.valueOf(position));
                startActivity(intent);
            }
        });


        stickyList.setOnHeaderClickListener(new StickyListHeadersListView.OnHeaderClickListener() {
            @Override
            public void onHeaderClick(StickyListHeadersListView l, View header, int itemPosition, long headerId, boolean currentlySticky) {
//                Toast.makeText(GroupsActivity.this, "Header " + headerId + " currentlySticky ? " + currentlySticky, Toast.LENGTH_SHORT).show();
            }
        });
        stickyList.setOnStickyHeaderChangedListener(new StickyListHeadersListView.OnStickyHeaderChangedListener() {
            @Override
            public void onStickyHeaderChanged(StickyListHeadersListView l, View header, int itemPosition, long headerId) {
                header.setAlpha(1);
            }
        });
        stickyList.setOnStickyHeaderOffsetChangedListener(new StickyListHeadersListView.OnStickyHeaderOffsetChangedListener() {
            @Override
            public void onStickyHeaderOffsetChanged(StickyListHeadersListView l, View header, int offset) {
                if (fadeHeader && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    header.setAlpha(1 - (offset / (float) header.getMeasuredHeight()));
                }
            }
        });
//        stickyList.addHeaderView(getLayoutInflater().inflate(R.layout.list_header, null)); //在listview 上添加头布局
//        stickyList.addFooterView(getLayoutInflater().inflate(R.layout.list_footer, null)); //在listview 下添加脚布局
        //当数据为null的时候 显示一个view
//        stickyList.setEmptyView(findViewById(R.id.empty));
        stickyList.setDrawingListUnderStickyHeader(true);
        stickyList.setAreHeadersSticky(true);

        stickyList.setStickyHeaderTopOffset(-20);
        stickyList.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
//                if (isonCreate){
                lasttimePostion = position;
                MyLog.e(TAG, "position" + position);
                itemView = view;
                ImageView nextIV = (ImageView) view.findViewById(R.id.next);
                ImageView mapState = (ImageView) view.findViewById(R.id.mapState);
                ImageView delete = (ImageView) view.findViewById(R.id.delete);
                deleteStartTime = startTimeList.get(position).getId();
                deleteEndTime = endTimeList.get(position).getId();
                deleteMonthTime = listMonth.get(position).getId();
                MyLog.e(TAG, deleteStartTime + "");
                MyLog.e(TAG, deleteEndTime + "");
                if (nextIV != null) {
                    nextIV.setVisibility(GONE);
                    mapState.setVisibility(GONE);
                    delete.setVisibility(VISIBLE);
                    delete.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MyLog.e(TAG, "onClick执行了");
//                        数据库删除数据
                            traGreendao.deleteByKey(deleteEndTime);
                            traGreendao.deleteByKey(deleteStartTime);
                            traGreendao.deleteByKey(deleteMonthTime);
//                         刷新数据源刷新View
                            startTimeList.remove(position);
                            endTimeList.remove(position);
                            listMonth.remove(position);
                            groupsAdapter.notifyDataSetChanged();
//                                groupsAdapter = new GroupsAdapter(GroupsActivity.this);
//                                stickyList.setAdapter(groupsAdapter);
                        }
                    });
                }
//                    isonCreate = false ;
//                }else {
//                    if (lasttimePostion == position){
//                        return  true ;
//                    }else {
//                        lasttimePostion = position ;
//                        itemView = view ;
//                        ImageView nextIV = (ImageView) view.findViewById(R.id.next);
//                        ImageView mapState = (ImageView) view.findViewById(R.id.mapState);
//                        ImageView delete = (ImageView) view.findViewById(R.id.delete);
//                        nextIV.setVisibility(GONE);
//                        mapState.setVisibility(GONE);
//                        delete.setVisibility(VISIBLE);
//                        deleteStartTime = startTimeList.get(position).getId();
//                        deleteEndTime = endTimeList.get(position).getId();
//                        deleteMonthTime = listMonth.get(position).getId();
//                        MyLog.e(TAG, deleteStartTime +"");
//                        MyLog.e(TAG, deleteEndTime +"");
//                        delete.setOnClickListener(new OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                MyLog.e(TAG,"onClick执行了");
////                        数据库删除数据
//                                traGreendao.deleteByKey(deleteEndTime);
//                                traGreendao.deleteByKey(deleteStartTime);
//                                traGreendao.deleteByKey(deleteMonthTime);
////                         刷新数据源刷新View
//                                startTimeList.remove(position);
//                                endTimeList.remove(position);
//                                listMonth.remove(position);
//
//                                groupsAdapter.notifyDataSetChanged();
////                                groupsAdapter = new GroupsAdapter(GroupsActivity.this);
////                                stickyList.setAdapter(groupsAdapter);
//                            }
//                        });
//                        isonCreate = false ;
//                    }
//                }
                return true;
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        groupsAdapter.notifyDataSetChanged();
    }

    protected void initListeners() {


    }
}




