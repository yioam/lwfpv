package com.klh.lwsample.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class LWDBManager {
	private static LWDBManager mInstance = null;
    private LWLWDBHelper dbHelper = null;
    private SQLiteDatabase db = null;

    private LWDBManager(Context context){
        if(dbHelper == null){
            dbHelper = LWLWDBHelper.getInstance(context);

            db = dbHelper.getWritableDatabase();
        }

    }

    /**
     * 获取HWDBManager实例
     * @return
     */
    public static LWDBManager getInstance(Context context){

        if(mInstance == null ){
            synchronized(LWDBManager.class){
                if(mInstance == null ){
                    mInstance = new LWDBManager(context);
                }
            }
        }
        return mInstance;
    }

    public void onDestroy()throws Exception{

        synchronized(LWDBManager.class){

            if(db != null){
                db.close();
                db = null;
            }

            mInstance = null;
        }
    }

    private void checkOpenState(){
        if (db == null || !db.isOpen()){
            db = dbHelper.getWritableDatabase();
        }
    }

    /**
     * 插入
     * @param table
     * @param values
     * @return
     * @throws
     */
    public  long insert(String table, ContentValues values)throws Exception{
        checkOpenState();
        return db.insert(table, null,values);
    }


    /**
     * 删除
     * @param tableName		表名
     * @param whereClause	删除的条件
     * @param whereArgs		删除参数
     * @return				影响到的行数
     */
    public  final int delete(String tableName, String whereClause,String[] whereArgs)throws Exception {
        checkOpenState();
        return db.delete(tableName, whereClause, whereArgs);
    }

    /**
     * 更新
     * @param table
     * @param values
     * @param whereClause
     * @param whereArgs
     * @return
     */
    public  int update(String table, ContentValues values, String whereClause, String[] whereArgs){
        checkOpenState();
        return db.update(table, values, whereClause, whereArgs);
    }


    /**
     * 查询（供需要用到Cursor的调用者使用，若调用者只是想获取查询的数据集合，请使用：rawQuery()查询方法）
     *
     * @param tableName		表名
     * @param columns		需查询的列
     * @param whereStr		查询条件
     * @param whereArgs		查询参数
     * @return
     */
    public  final Cursor query(String tableName, String[] columns, String whereStr,
                               String[] whereArgs, String orderBy) throws Exception{
        checkOpenState();
        return db.query(tableName, columns, whereStr, whereArgs, null, null,
                orderBy);
    }

    public final Cursor rawQuery(String sql,String[] selectionArgs) throws Exception{
        checkOpenState();
        return db.rawQuery(sql, selectionArgs);
    }


    /**
     * 获取表中对应角色的记录数目
     *
     * @return
     */
    public final int queryCount(String sql,String[] selectionArgs) throws Exception{

        checkOpenState();

        Cursor cursor = db.rawQuery(sql, selectionArgs);

        int count = cursor.getCount();

        cursor.close();

        return count;
    }

    /**
     * 关闭数据库（不要顺便调用，系统程序完全退出是才调用）
     */
    public void closeDb() {
        if (db != null) {
            db.close();
            db = null;
        }

    }

    /**
     * 开启事务
     */
    public void beginTransaction(){
        if (db != null) {
            db.beginTransaction();
        }
    }

    /***
     * 结束事务
     * */
    public void endTransaction (boolean isSuccess){

        if (db != null) {
            if(isSuccess){
                db.setTransactionSuccessful();
            }
            db.endTransaction();
        }
    }
}
