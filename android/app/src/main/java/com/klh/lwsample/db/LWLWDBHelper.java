package com.klh.lwsample.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.klh.lwsample.util.LWLogUtils;


public class LWLWDBHelper extends SQLiteOpenHelper{
	// public static String databaseName = "lewei.db";
	 
	 public static String databaseName = "lwfpv.db";
	    //版数据库版本
	    private static final int DATABASE_VERSION =12;
	    private static LWLWDBHelper mdbHelper;
	    private Context mContext;

	    public LWLWDBHelper(Context context) {
	        super(context, databaseName, null, DATABASE_VERSION);
	        mContext=context;
	    }

	    /**
	     * 获取DBHelper实例（该方法仅供DBManager类使用，所有和数据库有关的参照有由DBManager统一管理）
	     *
	     * @param context
	     * @return
	     */
	    public static LWLWDBHelper getInstance(Context context) {
	        if (mdbHelper == null) {
	            mdbHelper = new LWLWDBHelper(context);
	        }
	        return mdbHelper;
	    }



	    @Override
	    public void onCreate(SQLiteDatabase db) {
	        db.beginTransaction();
	        try {
	            db.execSQL(LWHistoryItem.CREATE_TABLE_SQL_2_0_0);
	            db.execSQL(FlyMileage.CREATE_TABLE_Mileage);//创建一张表
	            db.execSQL(FlyHeight.CREATE_TABLE_Height);//创建一张表
	           // db.execSQL(FlyDuration.CREATE_TABLE_Duration);//创建一张表
	            db.setTransactionSuccessful();
	        } finally {
	            db.endTransaction();
	        }
	    }


	    /***
	     * 数据库的升级操作，现在还没实现，后面根据表格的改变而修改
	     * **/
	    @Override
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	        LWLogUtils.e( "onUpgrade " + databaseName + "从" + oldVersion + "升级到" + newVersion);
	        db.beginTransaction();
	        try {
	            if(oldVersion < DATABASE_VERSION){
	            	//只要数据库版本号不一样,先删除表,然后再创建一个
					db.execSQL(LWHistoryItem.DROP_TABLE_SQL);
					db.execSQL(FlyMileage.DROP_TABLE_Mileage);

	            	db.execSQL(LWHistoryItem.CREATE_TABLE_SQL_2_0_0);
	            	db.execSQL(FlyMileage.CREATE_TABLE_Mileage);
	            	
	            	
	            	db.execSQL(FlyHeight.DROP_TABLE_Height);
	            	db.execSQL(FlyHeight.CREATE_TABLE_Height);
	            	
	            	
	            }
	            db.setTransactionSuccessful();
	        } finally {
	            db.endTransaction();
	        }
	    }
}
