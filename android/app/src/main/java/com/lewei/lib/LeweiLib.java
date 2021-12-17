package com.lewei.lib;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.klh.lwsample.MyApplication;
import com.klh.lwsample.bean.RecList;
import com.klh.lwsample.constant.HandlerParams;
import com.klh.lwsample.util.LWFileUtils;
import com.klh.lwsample.util.PathConfig;

import java.io.File;
import java.io.FileInputStream;
import java.util.TimeZone;

public class LeweiLib {
    public static final int PORT_COMMON = 0; // 公版
    public static final int PORT_HENQI = 1; // 恒奇
    public static final int PORT_RCLEADING = 2; // 前沿RC
    public static final int PORT_HUIKE = 3; // 辉科
    public static final int PORT_HAICONG = 10; // TODO:4 PORTS
    public static final int PORT_FEIBAO = 11;
    public static final int PORT_WEILI = 12; // 伟力

    public static final int ENCRY_COMPATIBLE = 0; // 兼容版本
    public static final int ENCRY_ENCRY = 1 << 0; // 加密
    public static final int ENCRY_NOENCYR = 1 << 1; // 不加密
    private Handler handler;
    private Thread mThread;
    private boolean isStop = false;

    private boolean isFisrtSendCMD = true;
    public static boolean isNeedTakePhoto = false;
    public static boolean isNeedTakeRecord = false;

    public static boolean isNeedTakePhoto_Big = false;
    public static boolean needTakePhoto_4k = false;
    public static boolean isSDCardRecord = false;

    public static int HD_flag = 0;// 高清和普清设置 (720p和480p)
    public static int Hardware_flag = 0;
    private OnTcpListener tcpListener;

    private int retRecordPlan;

    public LeweiLib() {
    }

    public void setHandler(Handler mHandler) {
        this.handler = mHandler;
    }

    static {

        try {

            System.loadLibrary("lewei-3.0");

        } catch (Exception e) {
            Log.e("lewei-3.0", "Can not load library libscreenrecorderrtmp.so");
            e.printStackTrace();

        }

    }

    /**
     * 如果串口透传用的是TCP才需要用到此回调
     *
     * @param tcpListener
     */
    public void setOnTcpListener(OnTcpListener tcpListener) {
        this.tcpListener = tcpListener;
    }

    private void sendHandlerMsg(int what, int arg0) {
        Message msg = handler.obtainMessage();
        msg.what = what;
        if (arg0 > 0)
            msg.arg1 = arg0;
        handler.sendMessage(msg);
    }

    public void startCMDThread() {
        if (mThread == null || !mThread.isAlive()) {
            mThread = new Thread(() -> {
                isStop = false;
                while (!isStop) {
                    if (isFisrtSendCMD) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        isFisrtSendCMD = false;
                        int mTimeZoneOffset = TimeZone.getDefault()
                                .getOffset(System.currentTimeMillis());

                        Log.i("LeweiLib", "Now get the timezone is " + mTimeZoneOffset + "  " + TimeZone.getDefault().getRawOffset());
                        retRecordPlan = LeweiLib.LW93SendSetRemoteTime2(mTimeZoneOffset / 1000);

                        if (retRecordPlan <= 0) {
                            sendHandlerMsg(HandlerParams.SET_REMOTETIME_FAIL, 0);
                        }

                        // ret = LeweiLib.LW93SendGetRecPlan();
                        retRecordPlan = LeweiLib.LW93SendGetRecPlan();
                        // if (ret == 1) // 如果开机获取到SD卡正在录像
                        if (retRecordPlan == 1) // 如果开机获取到SD卡正在录像
                        {
                            sendHandlerMsg(HandlerParams.SET_RECPLAN_START,
                                    0);
                            Log.d("", "remote sdcard not recording.");
                        } else {
                            Log.d("", "remote sdcard not recording.");
                        }
                    }

                    if (isNeedTakePhoto) {
                        takeSDcardCapture();
                        isNeedTakePhoto = false;
                        isNeedTakePhoto_Big = false;
                        needTakePhoto_4k = false;
                    }

                    if (isNeedTakeRecord) {
                        takeSDcardRecord();
                        isNeedTakeRecord = false;
                    }
                    try {
                        Thread.sleep(150);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            });
            mThread.start();
        }
    }

    public void stopThread() {
        isStop = true;

        isNeedTakeRecord = false;
        if (isSDCardRecord) {
            retRecordPlan = LeweiLib.LW93SendChangeRecPlan(0);

        }
        Log.e("LeweiLib", "isStop=" + isStop);
    }

    @SuppressWarnings("unused")
    public void takeSDcardCapture() {
        String folder = Environment.getExternalStorageDirectory().toString()
                + PathConfig.PHOTOS_PATH;
        File folderFile = new File(folder);
        if (!folderFile.exists())
            folderFile.mkdirs();
        String file_name = LeweiLib.LW93SendCapturePhoto(folder);

        Log.d("TAG", "takeSDcardCapture===========: ");
        /***** 因为有的1080p的模块拍照的分辨率是1920*1088,所以要改为1920*1080 *******/
        Bitmap bitmap = LWFileUtils.raedPic(file_name);
        if (bitmap == null) {
            return;
        }

        ///通知相册刷新
        Uri uri = Uri.parse("file:///" + file_name);
        MyApplication.context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));

        int moduleType = getModuleType();
        Log.e("LeweiLib", "moduleType==" + moduleType + "   原来照片分辨率=" + bitmap.getWidth() + "*" + bitmap.getHeight());

        ///todo yioam
        //因为有一模块出来的分辨率是1920*1088,现在要改正为1920*1080
        if (bitmap.getHeight() == 1088) {
            bitmap = Stream93.interpolation_Photo_to4k(bitmap,1920.0f,1080.0f, bitmap.getWidth(),
                    bitmap.getHeight());
            Stream93.savePhoto(bitmap, file_name);
        }
        //Log.e("", "0000===" + bitmap.getWidth() + "*" + bitmap.getHeight());

        /*********************************************/

		if (isNeedTakePhoto_Big) {
			//1280*720插值1920*1080
			bitmap = Stream93.interpolation_Photo_to4k(bitmap,1920.0f,1080.0f, bitmap.getWidth(),
					bitmap.getHeight());
			Stream93.savePhoto(bitmap, file_name);
		}
		/****2.5k模块插值为4k或者needTakePhoto_4k==true*****************/

		if (moduleType == 25||needTakePhoto_4k||moduleType==93) {
			bitmap = Stream93.interpolation_Photo_to4k(bitmap,3840.0f,2160.0f, bitmap.getWidth(),
					bitmap.getHeight());
			Stream93.savePhoto(bitmap, file_name);
		}

        /**********************/
        Message msg = Message.obtain();
        msg.what = HandlerParams.SEND_CAPTURE_PHOTO;
        msg.obj = file_name;
        handler.sendMessage(msg);
    }

    private void takeSDcardRecord() {
        int ret;

        ret = LeweiLib.LW93SendGetRecPlan();
        if (ret < 0) {
            sendHandlerMsg(HandlerParams.GET_RECPLAN_FAIL, 0);
            isSDCardRecord = false;
        } else if (ret == 0) {
            ret = LeweiLib.LW93SendChangeRecPlan(1);
            if (ret != 0) {
                sendHandlerMsg(HandlerParams.SET_RECPLAN_FAIL, ret);
                isSDCardRecord = false;
            } else {
                sendHandlerMsg(HandlerParams.SET_RECPLAN_START, 0);
                isSDCardRecord = true;
            }
        } else {
            ret = LeweiLib.LW93SendChangeRecPlan(0);
            sendHandlerMsg(HandlerParams.SET_RECPLAN_STOP, 0);
            isSDCardRecord = false;
        }
    }

    public static Bitmap readBitmapFromFileDescriptor(String filePath,
                                                      int width, int height) {
        try {
            FileInputStream fis = new FileInputStream(filePath);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(fis.getFD(), null, options);
            float srcWidth = options.outWidth;
            float srcHeight = options.outHeight;
            int inSampleSize = 1;

            if (srcHeight > height || srcWidth > width) {
                if (srcWidth > srcHeight) {
                    inSampleSize = Math.round(srcHeight / height);
                } else {
                    inSampleSize = Math.round(srcWidth / width);
                }
            }

            Log.e("", "999999=width=" + width + "  height=" + height);

            options.inJustDecodeBounds = false;
            options.inSampleSize = inSampleSize;

            return BitmapFactory.decodeFileDescriptor(fis.getFD(), null,
                    options);
        } catch (Exception ex) {
        }
        return null;
    }

    // public native void LW93NativeInit(String folder, int port_flag,
    // int encry_flag);
    public native void LW93NativeInit(String folder, int port_flag,
                                      int encry_flag, int offsetTime);

    // stream
    public native static int LW93StartLiveStream(int interval, int hd_flag);

    public native static void LW93StopLiveStream();

    public native static int LW93StartRecordReplay(String name, int start,
                                                   int end, int interval);

    public native static void LW93StopRecordReplay();

    public native static void LW93ChangeRecordReplayAttr(String name,
                                                         int start, int end);

    public native static int LW93GetCurrTimestamp();

    public native static int LW93DrawBitmapFrame(Bitmap bmp);

    // for download file
    public static native String LW93StartDownloadFile(String folder,
                                                      String file_name, int interval);

    public static native void LW93StopDownloadFile();

    // command
    public static native int LW93SendGetRemoteTime();

    @Deprecated
    public static native int LW93SendSetRemoteTime();

    public static native int LW93SendSetRemoteTime2(int offsetTime);

    public static native String LW93SendCapturePhoto(String folder);

    /**
     * 新的拍照接口
     *
     * @param folder      保存本地的文件夹路径
     * @param save_local  是否保存本地，1保存，0不保存
     * @param save_sdcard 是否保存远程sdcard，1保存，0不保存
     * @return 保存本地返回本地路径，保存sdcard返回sd卡路径，null为失败
     */
    public static native String LW93SendCapturePhotoNew(String folder,
                                                        int save_local, int save_sdcard);

    public static native RecList[] LW93SendGetRecList();

    public static native int LW93SendDeleteFile(String path);

    public static native int LW93SendSdcardFormat();

    public static native int LW93SendChangeWifiName(String name);

    public static native int LW93SendChangeWifiPassword(String password);

    public static native int LW93SendChangeChannel(int channel);

    public static native int LW93SendResetWifi();

    public static native int LW93SendRebootWifi();

    public static native int LW93SendGetCameraFlip();

    public static native int LW93SendSetCameraFlip(int flip);

    // serial setting
    public static native int LW93SendGetBaudrate();

    public static native int LW93SendSetBaudrate(int baudrate);

    public static native int LW93SendGetFHDFlag();

    public static native int LW93SendSetFHDFlag(int flag);

    // public static native int LW93SendGetBaseParams(BaseParams params);

    // udp data to serial
    public static native int LW93InitUdpSocket();

    public static native void LW93CloseUdpSocket();

    public static native int LW93SendUdpData(byte[] data, int size);

    public static native byte[] LW93RecvUdpData();

    // tcp data to serial
    public void LW93TcpConnected() {
        if (tcpListener != null) {
            tcpListener.TcpConnected();
        }
    }

    public void LW93TcpDisconnected() {
        if (tcpListener != null) {
            tcpListener.TcpDisconnected();
        }
    }

    public void LW93TcpReceived(byte[] data, int size) {
        if (tcpListener != null) {
            tcpListener.TcpReceive(data, size);
        }
    }

    /**
     * 0也是拍照 #define CMD_KEY_SNAP 1 //photo #define CMD_KEY_STARTRECORD 2
     * 飞控的内存卡stert rec #define CMD_KEY_STOPRECORD 3 飞控的内存卡stop rec #define
     * CMD_KEY_NOCARD_RECORD 4 没卡手机录像 #define CMD_KEY_SHRINK 5 缩小 #define
     * CMD_KEY_MAGNIFY 6 放大 //这个后面添加 keystatus==0x81（开始录像或者正在录像）
     * keystatus==0x82（停止录像）
     **/
    public void LW93TcpRemoteKey(int key_value, int key_index, int key_status) {
        if (tcpListener != null) {
            tcpListener.TcpRemoteKeyListener(key_value, key_index, key_status);
        }
    }

    /****
     * CameraOk 摄像头适口准备可以了， cameraCurFps 摄像头当前的帧率
     */
    public void LWCameraOk(int CameraOk, int cameraCurFps) {
        if (tcpListener != null) {
            tcpListener.OnCameraOk(CameraOk, cameraCurFps);
        }
    }

    public void LWTakePhotoOk(int TakePhotoOk, String path) {
        if (tcpListener != null) {
            tcpListener.OnTakePhotoOk(TakePhotoOk, path);
        }
    }

    public native int LW93StartTcpThread();

    public native void LW93StopTcpThread();

    public native int LW93SendTcpData(byte[] data, int size);

    /**
     * @return 1: recording, 0: not recording, -1: error
     */
    public static native int LW93SendGetRecPlan();

    public static native int LW93SendChangeRecPlan(int flag);

    // local record
    // public static native int LW93StartLocalRecord(String path, int
    // frameRate);

    // public static native int LW93StopLocalRecord();

    // return the record time milliseconds
    public static native int LW93GetRecordTimestamp();

    // 从JNI层获取一些参数
    public static native int getSdcardStatus();

    public static native H264Frame getH264Frame();

    public static native int getDownloadFileSize();

    public static native int getDownloadRecvSize();

    public static native int getFrameWidth();

    public static native int getFrameHeight();

    // -1 false
    // 0 has other device connect
    // 1 bind success
    public static native int LW93SendBindMacControl(int flag);

    // return 1:跟随
    public static native int getFollowType();

    // return 15: 获取vga的帧率
    public static native int getVgaFps();

    // return 20: 获取720P的帧率
    public static native int get720PFps();

    // return 25: 获取1080P的帧率
    public static native int get1080PFps();

    /**
     * soft1080P_flag这个标识为，我重新定义一下， 当soft1080P_flag==0x01表示拍照插值成1080P，
     * soft1080P_flag==0x10(就是16),表示录像插值1080P， soft1080P_flag==0x11
     * (17)表示都插值成1080P
     ***/
    public static native int getSoft1080pFlag();

    // 0x01 onlyPhotoTo4K 0x02 OnlyVideoTo4K 0x03 bothPhotoVideoTo4K
    public static native int getPhotoVideoTo4kFlag();

    /***
     * heart.module_type = 93; 是LW98 和 LW96 系列 720P的板子 if([g_93_control
     * getModule_type]==72) if ([g_93_control getModule_type]==18) { if
     * ([g_93_control getModule_type]==16) {
     * ***/
    public static native int getModuleType();

    public static native int LW93SetUvcCameraResolution(int widht, int height,
                                                        int fps);

    // ////////////
    public static native LWCameraResolution[] LW93GetUvcCameraResolution();

    public static native int LW93SendUvcLedKey();

    public static native int LW93SendReboot();

    // 这两个先不用 LW93SendChangeWifiPassword（null）代替
    public static native int LW93SendChangePwd(String pwd);

    public static native int LW93SendClearPwd();
}
