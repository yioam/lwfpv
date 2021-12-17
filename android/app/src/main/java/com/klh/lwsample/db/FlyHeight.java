package com.klh.lwsample.db;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 创建一张记录高度的表
 * 
 * 
 */
public class FlyHeight implements Parcelable {
	public static final String TABLE_NAME = "record_height"; // 表名
	// 删除表语句
	public static final String DROP_TABLE_Height = "drop table if exists record_height";
	// 创建一个保存里程的表
	public static final String CREATE_TABLE_Height = "create table record_height(id INTEGER PRIMARY KEY "
			+ "AUTOINCREMENT,fly_number_id integer,height_time varchar,"
			+ "fly_mode integer,fly_state integer,fly_bettery integer,lifting_speed float,speed float,height float,distance float,elevation float,deflection float,star_number integer,accuracy float,air_latitude double,air_longitude double);";

	public static final String HEIGHT_TIME = "height_time";
	public static final String FLY_MODE = "fly_mode";
	public static final String FLY_STATE = "fly_state";
	public static final String FLY_BETTERY = "fly_bettery";
	public static final String LIFTING_SPEED = "lifting_speed";
	public static final String SPEED = "speed";
	
	public static final String HEIGHT = "height";
	public static final String DISTANCE = "distance";
	
	public static final String ELEVATION = "elevation";
	public static final String DEFLECTION = "deflection";
	public static final String STAR_NUMBER = "star_number";
	
	public static final String ACCURACY = "accuracy";
	public static final String LAST_AIR_LATITUDE = "air_latitude";
	public static final String LAST_AIR_LONGITUDE = "air_longitude";

	
	public String height_time; // 记录时间
	public int fly_mode;
	public int fly_state;
	public int fly_bettery;
	public float lifting_speed;
	public float speed;

	public float height; 
	public float distance;
	
	public float elevation;
	public float deflection;
	public int star_number;
	public float accuracy;
	
	public double air_latitude; // 飞机坐标
	public double air_longitude; // 飞机坐标

	public int getFly_number_id() {
		return fly_number_id;
	}

	public void setFly_number_id(int fly_number_id) {
		this.fly_number_id = fly_number_id;
	}

	public int fly_number_id; //

	public static final Creator<FlyHeight> CREATOR = new Creator<FlyHeight>() {
		@Override
		public FlyHeight createFromParcel(Parcel in) {
			// return new LWHistoryItem(in);
			FlyHeight item = new FlyHeight();
			item.setHeightTime(in.readString());
			item.setFly_mode(in.readInt());
			item.setFly_state(in.readInt());
			item.setFly_bettery(in.readInt());
			item.setLifting_speed(in.readFloat());
			item.setSpeed(in.readFloat());
			
			item.setHeight(in.readFloat());
			item.setDistance(in.readFloat());
			item.setElevation(in.readFloat());
			item.setDeflection(in.readFloat());
			item.setStar_number(in.readInt());
			item.setAccuracy(in.readFloat());
			item.setLatitude(in.readDouble());
			item.setLongitude(in.readDouble());
			
			item.setFly_number_id(in.readInt());
			return item;
		}

		@Override
		public FlyHeight[] newArray(int size) {
			return new FlyHeight[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(height_time);
		parcel.writeInt(fly_mode);
		parcel.writeInt(fly_state);
		parcel.writeInt(fly_bettery);
		parcel.writeFloat(lifting_speed);
		parcel.writeFloat(speed);
		
		parcel.writeFloat(height);
		parcel.writeFloat(distance);
		parcel.writeFloat(elevation);
		
		parcel.writeFloat(deflection);
		
		parcel.writeInt(star_number);
		parcel.writeFloat(accuracy);
		parcel.writeDouble(air_latitude);
		parcel.writeDouble(air_longitude);
		parcel.writeInt(fly_number_id);
	}


	@Override
	public String toString() {
		return "FlyHeight{" + "height_time='" + height_time + '\''
				+ ", fly_mode='" + fly_mode + ",  fly_state="+fly_state+", fly_bettery="+fly_bettery+", fly_bettery="+fly_bettery+", lifting_speed="+lifting_speed+", speed="+speed+", height="+height+", distance="+distance+", elevation="+elevation+", deflection="+deflection+", star_number="+star_number+", accuracy="+accuracy+", air_latitude="+air_latitude+", air_longitud="+air_longitude+",fly_number_id=" + fly_number_id
				+ '}';
	}

	public boolean select = false;

	public boolean isSelect() {
		return select;
	}

	public void setSelect(boolean select) {
		this.select = select;
	}

	
	
	public String getHeightTime() {
		return height_time;
	}

	public void setHeightTime(String time) {
		this.height_time = time;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float mil) {
		this.height = mil;
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

	public float getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(float accuracy) {
		this.accuracy = accuracy;
	}

	public int getFly_mode() {
		return fly_mode;
	}

	public void setFly_mode(int fly_mode) {
		this.fly_mode = fly_mode;
	}

	public int getFly_state() {
		return fly_state;
	}

	public void setFly_state(int fly_state) {
		this.fly_state = fly_state;
	}

	public int getFly_bettery() {
		return fly_bettery;
	}

	public void setFly_bettery(int fly_bettery) {
		this.fly_bettery = fly_bettery;
	}

	public float getLifting_speed() {
		return lifting_speed;
	}

	public void setLifting_speed(float lifting_speed) {
		this.lifting_speed = lifting_speed;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public float getDistance() {
		return distance;
	}

	public void setDistance(float distance) {
		this.distance = distance;
	}

	public float getElevation() {
		return elevation;
	}

	public void setElevation(float elevation) {
		this.elevation = elevation;
	}

	public float getDeflection() {
		return deflection;
	}

	public void setDeflection(float deflection) {
		this.deflection = deflection;
	}

	public int getStar_number() {
		return star_number;
	}

	public void setStar_number(int star_number) {
		this.star_number = star_number;
	}

}
