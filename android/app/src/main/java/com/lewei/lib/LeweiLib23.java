package com.lewei.lib;

import android.graphics.Bitmap;

public class LeweiLib23
{

	static {
		System.loadLibrary("lewei-3.0");
	}

	public LeweiLib23()
	{
		// TODO Auto-generated constructor stub
	}

	private OnLib23CallBack mOnLib23CallBack;

	public boolean initStream(OnLib23CallBack cb, int port_type) {
		return nativeInitStream(cb, port_type);
	}

	public void deinitStream() {
		nativeDeInitStream();
	}

	public H264Frame getH264Frame() {
		return nativeGetMJPEGFrame();
	}

	public void setMirror() {
		nativeSetMirror();
	}

	public boolean getRemoteKey(Object obj) {
		return nativeGetRemoteKey(obj);
	}

	public static boolean getHeartBitParams(Object obj) {
		return nativeGetHeartBitParams(obj);
	}
//
//	public boolean startRecord(String path) {
//		return nativeRecStart(path);
//	}
//
//	public void stopRecord() {
//		nativeRecStop();
//	}

	public void setRecordParams(int w, int h, int fps) {
		nativeRecSetParams(w, h, fps);
	}

	public void addRecordData(byte[] data, int size) {
		nativeRecInsertData(data, size);
	}

	public interface OnLib23CallBack {
		void onHeartBit(String ssid, int channel, int baudrate,
				int cameraValue, int droneType);

		void onUdpRecv(byte[] data, int size);
		void onKeyRecv(int key_id, int key_value);
	}

	// below for live stream
	private native static boolean nativeInitStream(OnLib23CallBack cb, int port_type);

	private native static void nativeDeInitStream();

	private native static H264Frame nativeGetMJPEGFrame();

	private native static void nativeSetMirror();

	public native static void nativeSetWiFiChannel(int channel);

	public native static void nativeSetWiFiSSID(String ssid);

	/**
	 * 不再使用此接口，使用新的回调接口 onKeyRecv
	 * @param obj
	 * @return
	 */
	@Deprecated
	private native static boolean nativeGetRemoteKey(Object obj);

	/**
	 * 不再使用此接口，使用新的回调接口 onHeartBit
	 * @param obj
	 * @return
	 */
	@Deprecated
	private native static boolean nativeGetHeartBitParams(Object obj);

//	// below for local record
//	private native static boolean nativeRecStart(String absPath);
//
//	private native static void nativeRecStop();

	@Deprecated
	private native static void nativeRecSetParams(int width, int height, int fps);

	private native static void nativeRecInsertData(byte[] data, int size);

	public native static int nativeRecGetTimestamp();

	public native static void nativeRecAddData(Bitmap bmp);

//	// below for AVI play
//	public native static boolean nativeAVIOpen(String absPath);
//
//	public native static void nativeAVIClose();
//
//	public native static double nativeAVIGetTotalTime();
//
//	public native static int nativeAVIGetTotalFrame();
//
//	public native static byte[] nativeAVIGetFrameAtIndex(int index);
//
//	public native static byte[] nativeAVIGetFrameAtTime(double time);
//
//	public native static byte[] nativeAVIGetVoiceAtTime(double time);
	//return 1:if is360P==1   录像和拍照的分辨率都插值成1280*720    640*360
	public static native int get360P();
		public static native int getRoll();
		//is_gyro 有陀螺仪为1
		public static native int getGyro();
		
		//return >0:跟随  返回跟随的类型，大于0就是有跟随，小于0就是没有
		public static native int getFollowType();
		// return  23  86 返回模块的类型        86传图走23，发送指令走93
		public static native int getModuleType();
}
