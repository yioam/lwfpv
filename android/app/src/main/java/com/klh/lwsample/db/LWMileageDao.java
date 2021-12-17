package com.klh.lwsample.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * 对数据插入,查找和删除
 * 
 *
 */
public class LWMileageDao {
	private String TAG="LWMileageDao";
	private LWDBManager mDBManager;

	public LWMileageDao(Context context) {
		mDBManager = LWDBManager.getInstance(context);
	}
	
	
	/***********里程 的数据操作************/
	/**
	 * 插入  
	 * 
	 * @param item
	 */
	public long insertMil(FlyMileage item) throws Exception {
		ContentValues values = new ContentValues();
		values.put(FlyMileage.MILEAGE_TIME, item.getMileageTime());
		
		values.put(FlyMileage.MALEAGE, item.getMileage());
		
		values.put(FlyMileage.HEIGHT, item.getHeight());
		
		values.put(FlyMileage.DURATION, item.getDuration());
		
		values.put(FlyMileage.LAST_AIR_LATITUDE, item.getLatitude());
		values.put(FlyMileage.LAST_AIR_LONGITUDE, item.getLongitude());
		
	//	values.put(FlyMileage.FLY_NUMBER_ID, item.getFly_number_id());
		return mDBManager.insert(FlyMileage.TABLE_NAME, values);
	}

	/**
	 * 插入
	 * 
	 * @param list
	 * @throws
	 */
	public void insertM(List<FlyMileage> listM) throws Exception {
		if (listM != null && listM.size() > 0) {
			for (FlyMileage item : listM) {
				insertMil(item);
			}
		}
	}
	
	/**
	 * 更新
	 * @param c
	 */
	public void update(FlyMileage item){
		
		String WhereClause = FlyMileage.FLY_NUMBER_ID+" = ? ";
		
		String[] whereArgs = new String[]{
				String.valueOf(item.getFly_number_id()),
				};
		
		ContentValues values = new ContentValues();
//		values.put(FlyMileage.FLY_NUMBER_ID, item.getFly_number_id());
	
//		values.put(FlyMileage.MILEAGE_TIME, item.getMileageTime());
		values.put(FlyMileage.MALEAGE, item.getMileage());			
		values.put(FlyMileage.HEIGHT, item.getHeight());
		values.put(FlyMileage.DURATION, item.getDuration());
		int count = mDBManager.update(FlyMileage.TABLE_NAME, values , WhereClause, whereArgs);
		//Log.e(TAG, "andy_aaaabbbb-更新飞机状态 count="+count);
	}
	
	/**
	 * 获取最后一条记录
	 * SELECT TOP 1 * FROM table_name
	 * */
	public int getFinalId(){
		//String sql = "select top 1 * from " + FlyMileage.TABLE_NAME;
		String sql = "select  * from " + FlyMileage.TABLE_NAME+" ORDER BY "+FlyMileage.FLY_NUMBER_ID+" DESC LIMIT 1";
		//SELECT * FROM user ORDER BY id DESC LIMIT 1;
		String[] args = new String[] {
		};
		
		List<FlyMileage> list = new ArrayList<FlyMileage>();
	
		try {
			Cursor cursor = mDBManager.rawQuery(sql, args);
			try {
				if (cursor != null) {
					while (cursor.moveToNext()) {
						list.add(packM(cursor));
					}
					
				}
			} finally {
				cursor.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(list!=null&&list.size()>0){
			return list.get(0).getFly_number_id();
		}else {
			return -1;
		}
		
		//return 1;
		
	}

	/**
	 * 根据路径删除某条记录
	 * 根据时间来删除的
	 * @throws
	 */
	public void deleteMileageByTime(String time) throws Exception {
		String WhereClause = FlyMileage.MILEAGE_TIME + " = ?";
		String[] whereArgs = new String[] { String.valueOf(time), };
		mDBManager.delete(FlyMileage.TABLE_NAME, WhereClause, whereArgs);
	}

	/**
	 * 删除一堆
	 * 
	 * @throws
	 * **/
	public void delMileageList(List<FlyMileage> list) throws Exception {
		if (list != null && list.size() > 0) {
			for (FlyMileage item : list) {
				deleteMileageByTime(item.mileage_time);
			}
		}
	}

	
	/**
	 * 查询全部历史列表
	 * 
	 * @param
	 */
	public List<FlyMileage> queryMileageList() {
		String sql = "select * from " + FlyMileage.TABLE_NAME;
		String[] args = new String[] {

		};
		
		List<FlyMileage> list = new ArrayList<FlyMileage>();
		
	
		try {
			Cursor cursor = mDBManager.rawQuery(sql, args);
			try {
				if (cursor != null) {
					while (cursor.moveToNext()) {
						Log.e("sql", "queryMileageList_sql="+sql);
						list.add(packM(cursor));
					}
					
				}
			} finally {
				cursor.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;

	}

	/**
	 * 获取数据集合
	 */
	public FlyMileage packM(Cursor cursor) {
		FlyMileage item = new FlyMileage();
		item.setMileageTime(cursor.getString(cursor.getColumnIndex(FlyMileage.MILEAGE_TIME)));
		
		item.setMileage(cursor.getFloat(cursor
				.getColumnIndex(FlyMileage.MALEAGE)));
		
		item.setHeight(cursor.getFloat(cursor
				.getColumnIndex(FlyMileage.HEIGHT)));
		
		item.setDuration(cursor.getString(cursor.getColumnIndex(FlyMileage.DURATION)));
		
		item.setLatitude(cursor.getDouble(cursor
				.getColumnIndex(FlyMileage.LAST_AIR_LATITUDE)));
		item.setLongitude(cursor.getDouble(cursor
				.getColumnIndex(FlyMileage.LAST_AIR_LONGITUDE)));
		
		item.setFly_number_id(cursor.getInt(cursor.getColumnIndex(FlyMileage.FLY_NUMBER_ID)));
		return item;
	}
	/*************************/

}
