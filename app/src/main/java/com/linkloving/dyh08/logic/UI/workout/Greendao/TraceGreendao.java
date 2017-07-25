package com.linkloving.dyh08.logic.UI.workout.Greendao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.linkloving.dyh08.utils.logUtils.MyLog;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import Trace.GreenDao.DaoMaster;
import Trace.GreenDao.DaoSession;
import Trace.GreenDao.Note;
import Trace.GreenDao.NoteDao;
import de.greenrobot.dao.query.Query;

/**
 * Created by Daniel.Xu on 2016/8/16.
 */
public class TraceGreendao {
    private static final String TAG = TraceGreendao.class.getSimpleName();
    private SQLiteDatabase db;
    private DaoMaster.DevOpenHelper devOpenHelper;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private Context context ;

    public TraceGreendao(Context context,SQLiteDatabase db){
        this.context = context ;
        this.db = db ;
        init();
    }
    private void init(){
        // 官方推荐将获取 DaoMaster 对象的方法放到 Application 层，这样将避免多次创建生成 Session 对象
        setupDatabase(db);
        // 获取 NoteDao 对象
        getNoteDao();
    }
    private void  setupDatabase(SQLiteDatabase db){
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
//            devOpenHelper = new DaoMaster.DevOpenHelper(context, "Note", null);
//            db = devOpenHelper.getReadableDatabase();
            daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
    }
    private NoteDao getNoteDao(){
        return daoSession.getNoteDao();
    }

    /**
     * 添加数据
     * @param date  数据当天的日期
     * @param startDate    点击开始运动的时间
     * @param runDate       跑步进行时的时间
     */
    public void addNote(String date ,Date startDate ,Date runDate, double latitude,double longitude){

//        public Note(Long id, java.util.Date date, java.util.Date startDate, java.util.Date runDate, String TraceLocation)
//        MyLog.e(TAG,"addNote方法执行了");
        Note note = new Note(null, date,  startDate, runDate,3, latitude,longitude);
        getNoteDao().insertOrReplace(note);
    }

    /**
     * 添加workout的数据,type的类型是10
     * @param starttime
     * @param endtime
     */
    public void addworkoutData(long id ,long starttime,long endtime){
        Date startData = new Date();
        startData.setTime(starttime*1000);
        Date endData = new Date();
        endData.setTime(endtime*1000);
        Note note = new Note(null, null, startData, endData, 10, null, null);
        getNoteDao().insertOrReplaceInTx(note);
    }



    /**
     * 添加开始时间
     * @param date  每天的日期
     * @param startDate 每次开始记录轨迹的开始时间
     * @param dou 0百度   2 google     1设备传上来的
     *
     */
    public void addStartTime(String date,Date startDate,double  dou){
        MyLog.e(TAG,"addStartTime方法执行了");
        Note note = new Note(null, date,  startDate, null,1, dou,null);
        getNoteDao().insert(note);
    }
    public void addStartMonth(String date,Date startDate){
        MyLog.e(TAG,"addStartMoth方法执行了");
        Note note = new Note(null, date,  startDate, null,0, null,null);
        getNoteDao().insertOrReplace(note);
    }
    /**
     * 添加结束时间
     * @param date  每天的日期
     * @param endDate 每次开始记录轨迹的开始时间
     */
    public void addEndTime(String date,Date endDate){
        MyLog.e(TAG,"addEndTime方法执行了");
        Note note = new Note(null, date,  endDate, null,2, null,null);
        getNoteDao().insertOrReplace(note);
    }

    /**
     * 根据开始时间和结束时间查数据库里面的数据
     * @param startdate
     * @param endDate
     * @return
     */
    public List<Note> searchLocation(Date startdate,Date endDate){
        Query<Note> query = getNoteDao().queryBuilder().where(NoteDao.Properties.StartDate.between(startdate, endDate),
                NoteDao.Properties.Type.eq(3)).orderAsc(NoteDao.Properties.Id).build();
        List<Note> list = query.list();
        return list ;
    }
    public List<Note> searchWorkData(){
        Query<Note> build = getNoteDao().queryBuilder().where(NoteDao.Properties.Type.eq(10)).orderDesc(NoteDao.Properties.Id).build();
        List<Note> list = build.list();
        return list;
    }




    /**
     * 查找每天跑了多少次
     * @param date 数据当天的日期
     */
    public List searchtimes(String date){
        MyLog.e(TAG,"searchtimes方法执行了");
        Query<Note> query = getNoteDao().queryBuilder().where(NoteDao.Properties.Date.eq(date),
                NoteDao.Properties.Type.eq(1))
                .orderAsc(NoteDao.Properties.Id)
                .build();
        List<Note> list = query.list();
     return list ;
    }




    public List <Note>searchAllStarttime(){
        Query<Note> query = getNoteDao().queryBuilder()
                .where(NoteDao.Properties.Type.eq(1))
                .orderAsc(NoteDao.Properties.Id)
                .build();
        List<Note> list = query.list();
        for (int i=0 ; i<list.size();i++){
            MyLog.e(TAG,list.get(i).getId()+"-------------"+"======searchAllStarttime"+"---的id");
        }
        return list ;
    }
    public List<Note>searchAllEndTime(){
        Query<Note> query = getNoteDao().queryBuilder()
                .where(NoteDao.Properties.Type.eq(2))
                .orderAsc(NoteDao.Properties.Id)
                .build();
        List<Note> list = query.list();
        for (int i=0 ; i<list.size();i++){
            MyLog.e(TAG,list.get(i).getId()+"-------------"+"searchAllEndTime"+"---的id");
        }
        return list ;
    }

    /**
     * 查询所有的开始时间
     */
    public List<Note> searchAllMonthtimes(){
//        MyLog.e(TAG, "searchAlltimes方法执行了");
        Query<Note> query = getNoteDao().queryBuilder().where(NoteDao.Properties.Type.eq(0))
                .orderAsc(NoteDao.Properties.Id)
                .build();
        List<Note> list = query.list();
//        for (int i=0 ; i<list.size();i++){
//            MyLog.e(TAG,list.get(i).getId()+"-------------"+"------searchAllMonthtimes"+list.get(i)+"---的id");
//        }
        return list ;
    }

    /**
     * 删除特定位置的数据
     */
    public void deleteByKey(long postion){
        getNoteDao().deleteByKey(postion);
        MyLog.e(TAG,"deleteByKey()"+postion);

    }

    /**
     * 根据开始时间运动时的坐标点集合
     */
    public List searchCoordinate(Date startDate){
        MyLog.e(TAG,"searchselection方法执行了");
        Query<Note> query = getNoteDao().queryBuilder().where(NoteDao.Properties.StartDate.eq(startDate), NoteDao.Properties.Type.eq(2))
                .orderAsc(NoteDao.Properties.Id).build();
        List<Note> list = query.list();
        return list ;
    }

    public void Delete(long id){
        getNoteDao().deleteByKey(id);
    }
}
