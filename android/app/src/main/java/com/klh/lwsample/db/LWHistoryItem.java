package com.klh.lwsample.db;

import android.os.Parcel;
import android.os.Parcelable;

public class LWHistoryItem implements Parcelable {
	public static final String TABLE_NAME = "lw_fly_history"; // 表名
	// 删除表语句
	public static final String DROP_TABLE_SQL = "drop table if exists lw_fly_history";
	// 建表语句lw_fly_history
	//public static final String CREATE_TABLE_SQL_1_0_0 = "create table lw_fly_history(id INTEGER PRIMARY KEY AUTOINCREMENT,time varchar,"
	//		+ "date varchar,path varchar,is_select boolean,is_show boolean,timeLeng varchar,userId varchar,type integer);";
	
	public static final String CREATE_TABLE_SQL_2_0_0 = "create table lw_fly_history(id INTEGER PRIMARY KEY AUTOINCREMENT,time varchar,"
			+ "fly_time_length varchar,max_distance float,max_height float,max_lifting_speed float,max_speed float,air_latitude double,air_longitude double);";
	
	
	
	public static final String TIME = "time";
	public static final String FLY_TIME_LENGTH = "fly_time_length";
	public static final String MAX_DISTANCE = "max_distance";
	public static final String MAX_HEIGHT = "max_height";
	public static final String MAX_LIFTING_SPEED = "max_lifting_speed";
	public static final String MAX_SPEED = "max_speed";

	public static final String LAST_AIR_LATITUDE = "air_latitude";
	public static final String LAST_AIR_LONGITUDE = "air_longitude";

	public String time; // 记录时间
	public String fly_time_length; // 飞行时长
	public float max_distance; // 最大飞行距离
	public float max_height; // 最大飞行高度
	public float max_lifting_speed; // 最大升降速度
	public float max_speed; // 最大速递

	public double air_latitude; // 飞机坐标
	public double air_longitude; // 飞机坐标



	public static final Creator<LWHistoryItem> CREATOR = new Creator<LWHistoryItem>() {
		@Override
		public LWHistoryItem createFromParcel(Parcel in) {
			// return new LWHistoryItem(in);
			LWHistoryItem item = new LWHistoryItem();
			item.setTime(in.readString());
			item.setFly_time_length(in.readString());
			item.setMax_distance(in.readFloat());
			item.setMax_height(in.readFloat());
			item.setMax_lifting_speed(in.readFloat());
			item.setMax_speed(in.readFloat());
			
			item.setLatitude(in.readDouble());
			item.setLongitude(in.readDouble());
			
			return item;
		}

		@Override
		public LWHistoryItem[] newArray(int size) {
			return new LWHistoryItem[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(time);
		parcel.writeString(fly_time_length);
		parcel.writeFloat(max_distance);
		parcel.writeFloat(max_height);
		parcel.writeFloat(max_lifting_speed);
		parcel.writeFloat(max_speed);
		parcel.writeDouble(air_latitude);
		parcel.writeDouble(air_longitude);
	}

	@Override
	public String toString() {
		return "LWHistoryItem{" + "time='" + time + '\''
				+ ", fly_time_length='" + fly_time_length + '\''
				+ ", max_distance=" + max_distance + ", max_height="
				+ max_height + ", max_lifting_speed=" + max_lifting_speed
				+ ", max_speed=" + max_speed +", air_latitude="+ air_latitude +"  ,air_longitude="+air_longitude+'}';
	}

	public boolean select = false;

	public boolean isSelect() {
		return select;
	}

	public void setSelect(boolean select) {
		this.select = select;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getFly_time_length() {
		return fly_time_length;
	}

	public void setFly_time_length(String fly_time_length) {
		this.fly_time_length = fly_time_length;
	}

	public float getMax_distance() {
		return max_distance;
	}

	public void setMax_distance(float max_distance) {
		this.max_distance = max_distance;
	}

	public float getMax_height() {
		return max_height;
	}

	public void setMax_height(float max_height) {
		this.max_height = max_height;
	}

	public float getMax_lifting_speed() {
		return max_lifting_speed;
	}

	public void setMax_lifting_speed(float max_lifting_speed) {
		this.max_lifting_speed = max_lifting_speed;
	}

	public float getMax_speed() {
		return max_speed;
	}

	public void setMax_speed(float max_speed) {
		this.max_speed = max_speed;
	}

	public double getLatitude() {
		return air_latitude;
	}

	public void setLatitude(double lat) {
		this.air_latitude = lat;
	}

	public double getLongitude() {
		return air_longitude;
	}

	public void setLongitude(double log) {
		this.air_longitude = log;
	}
}
