package com.klh.lwsample.constant;

public class HandlerParams {
	public static final int START_STREAM = 0x00;
	public static final int GET_FIRST_FRAME_93 = 0x01;
	public static final int GET_FIRST_FRAME_63 = 0x02;
	public static final int GET_FIRST_FRAME_23 = 0x03;
	public static final int UPDATE_RECORDTIME = 0x0A;

	public static final int UPDATE_SENDFOLLOW = 0x0B;

	public static final int RECORD_START_OK = 0x11;
	public static final int RECORD_START_FAIL = 0x12;
	public static final int RECORD_STOP = 0x13;
	public static final int SET_720P = 0xA1;
	public static final int GET_REMOTETIME_FAIL = 0x30;
	public static final int SET_REMOTETIME_FAIL = 0x31;
	public static final int GET_RECPLAN_FAIL = 0x32;
	public static final int GET_RECPLAN_NOT_RECORD = 0x33;
	public static final int GET_RECPLAN_RECORDING = 0x34;
	public static final int SET_RECPLAN_FAIL = 0x35;
	public static final int SET_RECPLAN_START = 0x36;
	public static final int SET_RECPLAN_STOP = 0x37;
	public static final int SEND_CAPTURE_PHOTO = 0x38;

	public static final int ONE_KEY_STOP = 0x83;

	public static final int FLYCTRL_FLIP_OVER = 0x84;

	public static final int FLYCTRL_JIAO_ZHENG = 0x85;

	public static final int CONNECT_93WIFI = 0x90;
	public static final int CONNECT_63WIFI = 0x93;

	public static final int WiFi_disconnection = 0x91;//wifi断开
	

	public static final int STARTDRAWER = 0x92;// 开始画轨迹，把那个手隐藏

	public static final int RESET = 0x99;// 控制重置微调参数

	public static final int DELEREFILEPhoto = 0x10;
	public static final int DELEREFILEVideo = 0x20;

	public static final int DELEREFILESDVideo = 0x45;// sd卡裏面的視頻刪除
	public static final int TRANSFERESDVideo = 0x46;// sd卡裏面的視頻下載

	public static final int DWAWERTRACK = 0x44;// 画轨迹时候更新百分比

	public static final int SHAKE_TIME = 0x50;// 手机震动时间

	public static final int UPDATE23 = 0x51;// 23拍照传来的路径

	public static final int NO_CONNECT_23 = 0x53;

	public static final int LOCATION_SET = 0x54;
	
	
	public static final int SET_TAKEPHOTO = 0x55;
	
	public static final int SET_RECORD_TIME=0x56;

	public static final int DELETE_FLY_PARAMETER = 0x58;

	public static final int SURFACE_VISIBLE = 0x59;//SURFACE显示
	public static final int SURFACE_INVISIBLE = 0x60;//SURFACE隐藏
}
