package com.lewei.lib63;

import com.lewei.lib.H264Frame;

public class LeweiLib63
{
	public static final int FHNPEN_SYS_Kick = 1; // 被踢除
	public static final int FHNPEN_SYS_OffLine = 2; // 与设备断开连接(心跳停止)
	public static final int FHNPEN_SYS_ShutDown = 4; // 设备关机
	public static final int FHNPEN_SYS_Reboot = 5; // 设备重启
	public static final int FHNPEN_SYS_Reset = 6; // 设备恢复出厂设置
	public static final int FHNPEN_SYS_Upgrade = 7; // 设备升级
	public static final int FHNPEN_SYS_ReConnect = 13; // 设备重连成功
	public static final int FHNPEN_SYS_BatteryV = 15; // 电池电压通知(1: 标示电压低 2:
														// 标示电压超低
	// 将自动关机 0: 标示充电完成)
	public static final int FHNPEN_SYS_IRLight = 16; // IRLight改变通知
	public static final int FHNPEN_SYS_FlipMirror = 17;// 图像翻转镜像通知
	public static final int FHNPEN_SYS_ShotBtnPush = 18;// 截图实体按钮按下通知

	/* 上报消息类型(报警消息) */
	public static final int FHNPEN_ALM_SDError = 80; // SD卡错误
	public static final int FHNPEN_ALM_SDFull = 81; // SD卡满
	public static final int FHNPEN_ALM_VideoLost = 86; // 信号丢失
	public static final int FHNPEN_ALM_MDAlarm = 84; // 移动侦测报警
	public static final int FHNPEN_ALM_IPConflict = 88; // IP冲突
	public static final int FHNPEN_ALM_WriteRecErr = 91; // 录像失败(FHNPEN_WriteError_e)
	public static final int FHNPEN_ALM_WritePicErr = 92; // 截图失败(FHNPEN_WriteError_e)
	/* 上报消息类型(操作消息) */
	public static final int FHNPEN_OPT_RecStart = 130; // 录像开始
	public static final int FHNPEN_OPT_RecFinish = 131; // 录像结束
	public static final int FHNPEN_OPT_PicFinish = 132; // 截图完成
	private OnRemoteKeyListener mRemoteKeyListener;

	static
	{
		System.loadLibrary("FHDEV_Discover");
		System.loadLibrary("FHDEV_Net");
		System.loadLibrary("lewei63");
	}

	public void setRemoteKeyListener(OnRemoteKeyListener onRemoteKeyListener)
	{
		this.mRemoteKeyListener = onRemoteKeyListener;
	}
	
	public void LW63KeyListener(int type, int value)
	{
		mRemoteKeyListener.OnRemoteKeyValue(type, value);
	}
	/* preview operations */
	public native static int LW63StartPreview(int type, long start, long stop);

	public native static void LW63StopPreview();

//	public native static int LW63DrawBitmapFrame(Bitmap bmp);
	
	public static native H264Frame getH264Frame();

	public native static boolean LW63GetReplayState();// return true:replay over

	public native static long LW63GetNowPts();

	/* take photos and records */
	public native static boolean LW63TakePhoto(String path, boolean isLocal);

	public native static boolean LW63TakeRemoteRecord();

	public native static boolean LW63StopRemoteRecord();

	// return true:recording now
	public native static boolean LW63GetRemoteRecordState();

//	public native static void LW63TakeLocalRecord(String path);
//
//	public native static void LW63StopLocalRecord();

	// return the record time now milliseconds
//	public native static int LW63GetRecordTimestamp();

	/* preview screen mirror */
	public native static void LW63SetMirrorCamera();

	/* login & logout */
	/* 为保证JNI回调的注册，去掉了static */
	public native boolean LW63Login();
	
	public native boolean LW63LoginNew(String username, String password, byte[] key);

	public native void LW63Logout();

	public native static boolean LW63GetLogined();

	public native static int LW63GetDevStatus();// return device status
	public native static int LW63GetLoginType();

	/* records operations */
	public native static boolean LW63SearchRecInit();

	public native static void LW63SearchRecClean();

	public native static boolean LW63SearchRecords(RecordInfo recInfo);

	/* serial operations */
	public native static boolean LW63StartSerial(long baudrate);

	public native static void LW63StopSerial();

	// return true:serial initialized; false:not initialized
	public native static boolean LW63GetSerialState();

	// return -1:serial not initialized; 0:send error; >0:send success
	public native static int LW63SendSerialData(byte[] data, int size);

	/* WiFi configuration operations */
	public native static boolean LW63GetWiFiConfig(WiFiConfig config);

	public native static boolean LW63SetWiFiConfig(WiFiConfig config);

	/* sdcard operations */
	public native static boolean LW63GetSDCardInfo(SDCardInfo info);

	public native static boolean LW63StartSDCardFormat(int formatType);

	public native static boolean LW63GetSDCardFormatState(SDCardInfo info);

	public native static boolean LW63StopSDCardFormat();
	
	public native static int LW63GetClientSize();

}
