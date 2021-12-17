package com.klh.lwsample.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

/**
 * 对数据插入,查找和删除
 * 
 *
 */
public class LWHeightDao {
	private String TAG="LWHeightDao";
	private LWDBManager mDBManager;

	public LWHeightDao(Context context) {
		mDBManager = LWDBManager.getInstance(context);
	}
	
	
	/***********里程 的数据操作************/
	/**
	 * 插入
	 * 
	 * @param item
	 */
	public long insertHeight(FlyHeight item) throws Exception {
		ContentValues values = new ContentValues();
		values.put(FlyHeight.HEIGHT_TIME, item.getHeightTime());
		values.put(FlyHeight.FLY_MODE, item.getFly_mode());
		values.put(FlyHeight.FLY_STATE, item.getFly_state());
		values.put(FlyHeight.FLY_BETTERY, item.getFly_bettery());
		values.put(FlyHeight.LIFTING_SPEED, item.getLifting_speed());
		
		values.put(FlyHeight.SPEED, item.getSpeed());
		
		values.put(FlyHeight.HEIGHT, item.getHeight());
		values.put(FlyHeight.DISTANCE, item.getDistance());
		values.put(FlyHeight.ELEVATION, item.getElevation());
		
		values.put(FlyHeight.DEFLECTION, item.getDeflection());
		
		values.put(FlyHeight.STAR_NUMBER, item.getStar_number());
		values.put(FlyHeight.ACCURACY, item.getAccuracy());
		values.put(FlyHeight.LAST_AIR_LATITUDE, item.getLatitude());
		
		values.put(FlyHeight.LAST_AIR_LONGITUDE, item.getLongitude());
		values.put(FlyMileage.FLY_NUMBER_ID, item.getFly_number_id());
		

		return mDBManager.insert(FlyHeight.TABLE_NAME, values);
	}

	/**
	 * 插入
	 * 
	 * @param list
	 * @throws
	 */
	public void insertH(List<FlyHeight> list) throws Exception {
		if (list != null && list.size() > 0) {
			for (FlyHeight item : list) {
				insertHeight(item);
			}
		}
	}

	/**
	 * 根据路径删除某条记录
	 * 根据时间来删除的
	 * @throws
	 */
	public void deleteHeightByTime(String time) throws Exception {
		String WhereClause = FlyHeight.HEIGHT_TIME + " = ?";
		String[] whereArgs = new String[] { String.valueOf(time), };
		mDBManager.delete(FlyHeight.TABLE_NAME, WhereClause, whereArgs);
	}

	/**
	 * 删除一堆
	 * 
	 * @throws
	 * **/
	public void delHeightList(List<FlyHeight> list) throws Exception {
		if (list != null && list.size() > 0) {
			for (FlyHeight item : list) {
				deleteHeightByTime(item.height_time);
			}
		}
	}
	
	/**
	 * 根据某个fly_number_id数据
	 * 根据时间来删除的
	 * @throws
	 */
	public void deleteHeightByFlyNumberId(String fly_number_id) throws Exception {
		String WhereClause = FlyMileage.FLY_NUMBER_ID + " = ?";
		String[] whereArgs = new String[] { String.valueOf(fly_number_id), };
		mDBManager.delete(FlyHeight.TABLE_NAME, WhereClause, whereArgs);
	}
	/**
	 * 查询某个时间段的全部条数
	 * 
	 * @param
	 */
	public List<FlyHeight> queryListByFlyNumberId(int fly_number_id) {
		
		String sql = "select * from " + FlyHeight.TABLE_NAME+" where "+FlyMileage.FLY_NUMBER_ID+" = ? ";
	
		String[] args = new String[]{
				String.valueOf(fly_number_id),
				
				};
		
		List<FlyHeight> list = new ArrayList<FlyHeight>();
		
		
		try {
			Cursor cursor = mDBManager.rawQuery(sql, args);
			try {
				if (cursor != null) {
					while (cursor.moveToNext()) {
						list.add(pack(cursor));
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
	 * 查询全部历史列表
	 * 
	 * @param
	 */
	public List<FlyHeight> queryHeightList() {
		String sql = "select * from " + FlyHeight.TABLE_NAME;
		String[] args = new String[] {

		};
		
		List<FlyHeight> list = new ArrayList<FlyHeight>();
		
		
		try {
			Cursor cursor = mDBManager.rawQuery(sql, args);
			try {
				if (cursor != null) {
					while (cursor.moveToNext()) {
						list.add(pack(cursor));
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
	public FlyHeight pack(Cursor cursor) {
		FlyHeight item = new FlyHeight();
		item.setHeightTime(cursor.getString(cursor.getColumnIndex(FlyHeight.HEIGHT_TIME)));
		
		
		
		item.setFly_mode(cursor.getInt(cursor.getColumnIndex(FlyHeight.FLY_MODE)));
		item.setFly_state(cursor.getInt(cursor.getColumnIndex(FlyHeight.FLY_STATE)));
		item.setFly_bettery(cursor.getInt(cursor.getColumnIndex(FlyHeight.FLY_BETTERY)));
		
		item.setLifting_speed(cursor.getFloat(cursor
				.getColumnIndex(FlyHeight.LIFTING_SPEED)));
		item.setSpeed(cursor.getFloat(cursor
				.getColumnIndex(FlyHeight.SPEED)));
		item.setHeight(cursor.getFloat(cursor
				.getColumnIndex(FlyHeight.HEIGHT)));
		
		
		item.setDistance(cursor.getFloat(cursor
				.getColumnIndex(FlyHeight.DISTANCE)));
		item.setElevation(cursor.getFloat(cursor
				.getColumnIndex(FlyHeight.ELEVATION)));
		item.setDeflection(cursor.getFloat(cursor
				.getColumnIndex(FlyHeight.DEFLECTION)));
		item.setStar_number(cursor.getInt(cursor
				.getColumnIndex(FlyHeight.STAR_NUMBER)));
		item.setAccuracy(cursor.getFloat(cursor
				.getColumnIndex(FlyHeight.ACCURACY)));
		
		item.setLatitude(cursor.getDouble(cursor
				.getColumnIndex(FlyHeight.LAST_AIR_LATITUDE)));
		
		item.setLongitude(cursor.getDouble(cursor
				.getColumnIndex(FlyHeight.LAST_AIR_LONGITUDE)));
		
		item.setFly_number_id(cursor.getInt(cursor.getColumnIndex(FlyMileage.FLY_NUMBER_ID)));

		return item;
	}
	/*************************/

}
