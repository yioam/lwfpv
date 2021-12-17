package com.klh.lwsample.db;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 创建一张表,存储里程,高度,时长和飞机最后的降落点的坐标,坐标是方便查找飞机
 * 
 * 
 */
public class FlyMileage implements Parcelable {
	public static final String TABLE_NAME = "record_mileage"; // 表名
	// 删除表语句
	public static final String DROP_TABLE_Mileage = "drop table if exists record_mileage";
	// 创建一个保存里程的表
	public static final String CREATE_TABLE_Mileage = "create table record_mileage"
			+ "(fly_number_id INTEGER PRIMARY KEY AUTOINCREMENT, mileage_time varchar,"
			+ "mileage float,height float,duration varchar,air_latitude double,air_longitude double);";

	public static final String MILEAGE_TIME = "mileage_time";
	public static final String MALEAGE = "mileage";
	public static final String HEIGHT = "height";
	public static final String DURATION = "duration";
	
	public static final String LAST_AIR_LATITUDE = "air_latitude";
	public static final String LAST_AIR_LONGITUDE = "air_longitude";
	
	public static final String FLY_NUMBER_ID = "fly_number_id";
	public String mileage_time; // 记录时间
	public float mileage; // 里程
	public float height; // 高度
	public String duration; // 时长
	
	public double air_latitude; // 飞机坐标
	public double air_longitude; // 飞机坐标
	
	public int fly_number_id; //现在是根据这个id来查找

	public int getFly_number_id() {
		return fly_number_id;
	}

	public void setFly_number_id(int fly_number_id) {
		this.fly_number_id = fly_number_id;
	}

	

	public static final Creator<FlyMileage> CREATOR = new Creator<FlyMileage>() {
		@Override
		public FlyMileage createFromParcel(Parcel in) {
			// return new LWHistoryItem(in);
			FlyMileage item = new FlyMileage();
			item.setMileageTime(in.readString());

			item.setMileage(in.readFloat());
			item.setHeight(in.readFloat());
			item.setDuration(in.readString());
			item.setLatitude(in.readDouble());
			item.setLongitude(in.readDouble());
			item.setFly_number_id(in.readInt());
			return item;
		}

		@Override
		public FlyMileage[] newArray(int size) {
			return new FlyMileage[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(mileage_time);

		parcel.writeFloat(mileage);
		parcel.writeFloat(height);
		parcel.writeString(duration);
		parcel.writeDouble(air_latitude);
		parcel.writeDouble(air_longitude);
		parcel.writeInt(fly_number_id);
	}

	@Override
	public String toString() {
		return "MileageDB{" + "mileage_time='" + mileage_time + '\''
				+ ", mileage='" + mileage + " height=" + height + "duration="
				+ duration +", air_latitude="+ air_latitude +"  ,air_longitude="+air_longitude+ ",fly_number_id=" + fly_number_id + '}';
	}
	
	/******这个是用来标记删除选择的*************/
	public boolean select = false;

	public boolean isSelect() {
		return select;
	}

	public void setSelect(boolean select) {
		this.select = select;
	}
	/**********************/

	public String getMileageTime() {
		return mileage_time;
	}

	public void setMileageTime(String time) {
		this.mileage_time = time;
	}

	public float getMileage() {
		return mileage;
	}

	public void setMileage(float mil) {
		this.mileage = mil;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float h) {
		this.height = h;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String dur) {
		this.duration = dur;
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
