package com.klh.lwsample.controller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;


import com.google.gson.Gson;
import com.klh.lwsample.MyApplication;
import com.klh.lwsample.R;
import com.klh.lwsample.bean.RecList;
import com.klh.lwsample.constant.HandlerParams;
import com.klh.lwsample.db.FlyHeight;
import com.klh.lwsample.db.FlyMileage;
import com.klh.lwsample.db.LWDBManager;
import com.klh.lwsample.db.LWHeightDao;
import com.klh.lwsample.db.LWMileageDao;
import com.klh.lwsample.plugin.LwPlugin;
import com.klh.lwsample.util.DateUtils;
import com.klh.lwsample.util.Sensors;
import com.klh.lwsample.view.FlutterSurfaceView;
import com.lewei.lib.LeweiLib;
import com.lewei.lib.OnTcpListener;
import com.lewei.lib.Stream23;
import com.lewei.lib.Stream93;
import com.lewei.lib63.LeweiLib63;
import com.lewei.uart_protol.ControlPara;
import com.lewei.uart_protol.Coordinate;
import com.lewei.uart_protol.LWUartProtolBean;
import com.lewei.uart_protol.PointPara;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static android.content.Context.WIFI_SERVICE;
import static com.klh.lwsample.MyApplication.context;

public class FlyModel {
    public static final String TAG = "FlyModel";
    public static final int SPEED_LOW = 1;
    public static final int SPEED_MID = 2;
    public static final int SPEED_MAX = 3;

    public LeweiLibType leweiLibType = LeweiLibType.empty;
    public ActivityLifeCycle activityState = ActivityLifeCycle.onDestroy;

    private static FlyModel flyModel = new FlyModel();

    private FlyModel() {
    }

    public static FlyModel getInstance() {
        return flyModel;
    }

    public boolean isChinese = true;// 判断是中文true,加载高德地图;
    public boolean isRED = false;// 控制电池是显示红色还是绿色
    public boolean isLocationSuccess = false;// 飞机返回的gps是否定位成功
    public boolean isGPSLocationSuccess = true;// 设置里面的gps良好
    public boolean isAllCtrlHide = false; // 隐藏&显示控制，隐藏则不发送指令
    public boolean isStartDrawing = false;
    public int speed_level = SPEED_MAX; // 1 30%; 2 60%; 3 100%
    public boolean isMapMode = false;

    public boolean isUnlock = false;// 标志解锁/上锁
    private boolean isFlyUP = false;
    public boolean isShowAirPlan = true;// 是否让飞机返回来的GPS显示在地图上

    public LeweiLib mLeweiLib;

    public boolean cpuV7a = false;
    public boolean isConnectWifi = false;// 判断是否连接上了wifi 连接上为true;

    public boolean isNeedAudio = false;// 控制是否需要录视频时候也把声音录进去
    public boolean isParamsAutoSave = true;
    public boolean isFollowing = false;// 标志是否正在出于跟随模式
    private boolean isStartFollow = false;
    private boolean isCheckGPSSignal = false;
    public Stream93 mStream93;
    public Stream23 mStream23;

    private boolean isReturn = false;// 一键返航

    private WifiInfo wifiInfo; // 获得的Wifi信息
    private WifiManager wifiManager; // Wifi管理器
    private Media media;

    public boolean isRightHandMode = false;//右手模式
    public boolean isSensorOn = false;//重力模式

    private Sensors sensors;

    /************** 保存飞行记录的数据 ******************/
    private String timeDate = "";// 时间
    private boolean isFlyState = false;
    private boolean isStartFly = false;

    private long startFlyTime = 0;// 记录飞机每次开始起飞的时间

    // ==========插入飞机返回来的各个参数===========//
    public int fly_number_id;

    public void onCreate(boolean hasPermission) {
        activityState = ActivityLifeCycle.onCreate;
        FlyModel.getInstance().initLib93();
        if (Build.VERSION.SDK_INT >= 24) {
            initNetwork();
        }
        isGPSLocationSuccess = true;
        // 获得WifiManager
        wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        media = new Media(context);

        sensors = new Sensors(context);
        sensors.setOnSensorValue((x, y) -> {
            int ail = 0, ele = 0;

            switch (speed_level) {
                case SPEED_MAX:
                    /** 公版GPS协议的 **/
                    ail = x * 500 / Sensors.SENSOR_ROTATE_LEVEL;
                    ele = y * 500 / Sensors.SENSOR_ROTATE_LEVEL;

                    break;
                default:
                    break;
            }

            /** 公版GPS协议的 **/
            ail = 1500 + ail;
            ele = 1500 - ele;

            ///todo yioam
            // 蓝光
            FlyCtrl.rudderdata[1] = ail; // 副翼
            FlyCtrl.rudderdata[2] = ele; // 升降舵

            LwPlugin.returnRudderData(ail, ele);

        });

        if (hasPermission) {
            gps();
        }
    }

    public void onDestroy() {
        activityState = ActivityLifeCycle.onDestroy;
        sensors.unregister();

        if (mStream93 != null && mStream93.is_recording_now) mStream93.takeRecord();
        if (mStream23 != null && mStream23.is_recording_now) mStream23.takeRecord();

        if (mStream93 != null) mStream93.stopStream93();
        if (mStream23 != null) mStream23.stopStream23();

        mLeweiLib.stopThread();
        mFlyCtrl.stopSendDataThread();
        mFlyCtrl.stopRecDataThread();
        // 修复当按home时候wifi连接上没有图像的问题
//        mDevice63.setOnConnectState(null);

        // 如果正在是跟随模式，那么停止
        if (isFollowing) isFollowing = false;
        if (gpsManager != null) {
            if (mLocation != null) {
                gpsManager.removeUpdates(mLocation);
                mLocation = null;
            }
            gpsManager = null;
        }
        if (mLocation != null) {
            mLocation = null;
        }
        if (Build.VERSION.SDK_INT >= 24) {
            MyApplication.context.unregisterReceiver(networkChangeReceiver);
        }
    }

    /**
     * 拍照
     */
    public void takePhoto() {

        switch (leweiLibType){
            case empty:
                break;
            case lib93:
                if (mStream93 != null) mStream93.takePhoto();
                break;
            case lib23:
                if (mStream23 != null) mStream23.takePhoto();
                break;
            case lib63:
                break;
        }

        if (isConnectWifi) {// 连接上wifi才有特效
            media.playShutter();
        }

    }

    /**
     * 录像
     */
    public boolean takeRec() {
        switch (leweiLibType){
            case empty:
                break;
            case lib93:
                if (mStream93 != null) {
                    mStream93.takeRecord();
                    return true;
                }
                break;
            case lib23:
                if (mStream23 != null) {
                    mStream23.takeRecord();
                    return true;
                }
                break;
            case lib63:
                break;
        }

        return false;
    }

    /**
     * 是否能解锁
     */
    public boolean canUnlock() {
        return !isGPSLocationSuccess || isLocationSuccess;
    }

    /**
     * 解锁
     */
    public void unlock() {
        FlyCtrl.mLWUartProtolBean.mControlPara.saveCommandTime = System.currentTimeMillis();
        FlyCtrl.mLWUartProtolBean.mControlPara.unlock = 1;// 解锁
    }


    /**
     * 起飞/降落
     */
    public boolean flyUp(boolean needUnlock) {
        ///todo isUnlock
//        if ( needUnlock) {
        if (!isFlyUP) {
            // 起飞
            if (isGPSLocationSuccess) {// 如果选择了设置里面GPS信号良好
                if (isLocationSuccess) {// 如果定位成功了

                    if (needUnlock)
                        FlyCtrl.mLWUartProtolBean.mControlPara.unlock = 1;// 解锁

                    FlyCtrl.mLWUartProtolBean.mControlPara.saveCommandTime = System.currentTimeMillis();
                    FlyCtrl.mLWUartProtolBean.mControlPara.autoTakeoff = 1;
                    FlyCtrl.mLWUartProtolBean.mControlPara.autoLanding = 0;
                }
            } else {

                if (needUnlock)
                    FlyCtrl.mLWUartProtolBean.mControlPara.unlock = 1;// 解锁


                FlyCtrl.mLWUartProtolBean.mControlPara.saveCommandTime = System.currentTimeMillis();
                FlyCtrl.mLWUartProtolBean.mControlPara.autoTakeoff = 1;
                FlyCtrl.mLWUartProtolBean.mControlPara.autoLanding = 0;
            }
        } else {
            if (isFollowing) {
                // 如果正在是跟随模式，那么停止
                isFollowing = false;
            }
            // 下降
            FlyCtrl.mLWUartProtolBean.mControlPara.saveCommandTime = System
                    .currentTimeMillis();
            FlyCtrl.mLWUartProtolBean.mControlPara.autoLanding = 1;
            FlyCtrl.mLWUartProtolBean.mControlPara.autoTakeoff = 0;
        }

        return true;
//        } else {
//            return false;
//        }
    }

    /**
     * 返航
     */
    public void flyDown() {
        if (isFollowing) {
            // 如果正在是跟随模式，那么停止
            isFollowing = false;
        }
        if (!isReturn) {
            isReturn = true;
            FlyCtrl.mLWUartProtolBean.mControlPara.saveCommandTime = System
                    .currentTimeMillis();
            FlyCtrl.mLWUartProtolBean.mControlPara.homeward = 1;
        } else {
            isReturn = false;
            // 这里是要发送一下定点模式
            FlyCtrl.mLWUartProtolBean.mControlPara.saveCommandTime = System
                    .currentTimeMillis();
            FlyCtrl.mLWUartProtolBean.mControlPara.hover = 1;
        }
    }

    /**
     * 分屏
     */
    public void setVRView() {
        if (FlutterSurfaceView.surfaceView != null) {
            FlutterSurfaceView.surfaceView.setVRView();
        }
    }

    /**
     * 全屏
     */
    public void setPlaneView() {
        if (FlutterSurfaceView.surfaceView != null) {
            // 全屏调用的方法
            FlutterSurfaceView.surfaceView.setPlaneView();
        }
    }

    /**
     * 地图线轨迹或者点轨迹时候发送的数据 (航点模式)
     */
    public void setTrackRouteControlData(List<Map> markers) {

        FlyCtrl.mLWUartProtolBean.mControlPara.ctlmode = ControlPara.ControlMode.CTL_Point;// 设置为航点模式
        FlyCtrl.mLWUartProtolBean.mControlPara.PointSendFlag = 1;
        PointPara[] mPointPara = new PointPara[markers.size()];

        for (int i = 0; i < markers.size(); i++) {

            PointPara mPP = new PointPara();
            Coordinate mCoo = new Coordinate();
            mCoo.latitude = (double) markers.get(i).get("latitude");
            mCoo.longitude = (double) markers.get(i).get("longitude");
            mPP.height = ((int) markers.get(i).get("height")) * 10.0f;// 高度
            mPP.speed = ((int) markers.get(i).get("speed")) * 10.0f;// 设置里面的速度
            mPP.time = ((int) markers.get(i).get("time"));// 设置里面的停留时间

            Log.e(TAG, " 11高度=" + mPP.height);
            Log.e(TAG, "11速度=" + mPP.speed);
            Log.e(TAG, "11停留时间=" + mPP.time);
            mPP.coordinate = mCoo;
            mPointPara[i] = mPP;
        }

        FlyCtrl.mLWUartProtolBean.mControlPara.pointNum = markers.size();
        FlyCtrl.mLWUartProtolBean.mControlPara.pointArray = mPointPara;

    }

    /**
     * 自稳模式
     */
    public void setStableMode() {
        // 这里是要发送一下定点模式
        FlyCtrl.mLWUartProtolBean.mControlPara.saveCommandTime = System
                .currentTimeMillis();
        FlyCtrl.mLWUartProtolBean.mControlPara.hover = 1;
        // 这里是要发送一下定点模式结束
        FlyCtrl.mLWUartProtolBean.mControlPara.ctlmode = ControlPara.ControlMode.CTL_Joystick;// 设置为航点模式

//        isCircle = 0;
    }

    /**
     * 航点模式
     */
    public void setWaypointMode() {
        if (isLocationSuccess) {
            // 这个条件是新添加的
            if (FlyCtrl.mLWUartProtolBean.mControlPara.ctlmode == ControlPara.ControlMode.CTL_Point) {
                // 如果是跟随模式 那么再点击时候退出跟随模式
                FlyCtrl.mLWUartProtolBean.mControlPara.saveCommandTime = System.currentTimeMillis();
                FlyCtrl.mLWUartProtolBean.mControlPara.hover = 1;
                // 这里是要发送一下定点模式结束
                FlyCtrl.mLWUartProtolBean.mControlPara.ctlmode = ControlPara.ControlMode.CTL_Joystick;

            } else {
                // 这里是要发送一下定点模式
                FlyCtrl.mLWUartProtolBean.mControlPara.saveCommandTime = System.currentTimeMillis();
                FlyCtrl.mLWUartProtolBean.mControlPara.hover = 1;
                isFollowing = false;
            }
        }
    }

    /**
     * 环绕模式
     */
    public void setSurroundMode() {
        if (isLocationSuccess) {
            // 这个条件是新添加的
            if (FlyCtrl.mLWUartProtolBean.mControlPara.ctlmode == ControlPara.ControlMode.CTL_Circle) {
                // 如果是跟随模式 那么再点击时候退出跟随模式
                FlyCtrl.mLWUartProtolBean.mControlPara.saveCommandTime = System.currentTimeMillis();
                FlyCtrl.mLWUartProtolBean.mControlPara.hover = 1;
                // 这里是要发送一下定点模式结束
                FlyCtrl.mLWUartProtolBean.mControlPara.ctlmode = ControlPara.ControlMode.CTL_Joystick;

            } else {
                // 这里是要发送一下定点模式(就是悬停指令)
                FlyCtrl.mLWUartProtolBean.mControlPara.saveCommandTime = System.currentTimeMillis();
                FlyCtrl.mLWUartProtolBean.mControlPara.hover = 1;
                // 这里是要发送一下定点模式结束
                FlyCtrl.mLWUartProtolBean.mControlPara.ctlmode = ControlPara.ControlMode.CTL_Circle;// 设置为环绕模式

                isFollowing = false;
            }
        }
    }

    /**
     * 跟随模式
     */
    public boolean setFollowMode() {
        if (!isOPen()) {
            return false;
        }
        ///判断位置权限

        ///todo yioam需要判断搜星成功

        if (isLocationSuccess) {
            if (FlyCtrl.mLWUartProtolBean.mControlPara.ctlmode == ControlPara.ControlMode.CTL_Follow) {
                // 如果是跟随模式 那么再点击时候退出跟随模式
                FlyCtrl.mLWUartProtolBean.mControlPara.saveCommandTime = System.currentTimeMillis();
                FlyCtrl.mLWUartProtolBean.mControlPara.hover = 1;
                // 这里是要发送一下定点模式结束
                FlyCtrl.mLWUartProtolBean.mControlPara.ctlmode = ControlPara.ControlMode.CTL_Joystick;// 设置为航点模式
                isFollowing = false;

                isCheckGPSSignal = false;
                Log.e(TAG, "999=1111关");
            } else {

                isFollowing = true;
                isStartFollow = true;
                mFlyCtrl.setFollowTime();
                // mFlyCtrl.setFollowState(false);


            }
        }

        return true;
    }

    /**
     * 环绕发送的数据
     */
    public void setFlyCircleControlData(double latitude, double longitude) {


        FlyCtrl.mLWUartProtolBean.mControlPara.circleSendFlag = 1;// 设置标志位
        FlyCtrl.mLWUartProtolBean.mControlPara.circlePara.center.latitude = latitude;
        FlyCtrl.mLWUartProtolBean.mControlPara.circlePara.center.longitude = longitude;
        FlyCtrl.mLWUartProtolBean.mControlPara.circlePara.height = FlyCtrl.mapCirclePointHeight * 10.0f;// 高度
        FlyCtrl.mLWUartProtolBean.mControlPara.circlePara.radius = FlyCtrl.mapCirclePointRadius * 10.0f;// 半径
        FlyCtrl.mLWUartProtolBean.mControlPara.circlePara.speed = FlyCtrl.mapCirclePointSpeed * 10.0f;// 环绕速度
        FlyCtrl.mLWUartProtolBean.mControlPara.circlePara.circleNum = FlyCtrl.mapCirclePointCycles;// 环绕圈数,0-一直环绕，手动退出

        Log.e(TAG, "环绕模式高度=" + FlyCtrl.mLWUartProtolBean.mControlPara.circlePara.height);
        Log.e(TAG, "环绕模式半径=" + FlyCtrl.mLWUartProtolBean.mControlPara.circlePara.radius);
        Log.e(TAG, "环绕模式速度=" + FlyCtrl.mLWUartProtolBean.mControlPara.circlePara.speed);
        Log.e(TAG, "环绕模式圈数=" + FlyCtrl.mLWUartProtolBean.mControlPara.circlePara.circleNum);
    }

    /**
     * 左右手模式
     */
    public void setRightHandMode(boolean mode) {
        isRightHandMode = mode;
    }


    /**
     * 重力模式
     */
    public void setSensorOn(boolean mode) {
        isSensorOn = mode;
        if (isSensorOn) {
            sensors.register();
        } else {
            sensors.unregister();
            FlyCtrl.setRudderData(1500, 1500);
            FlyCtrl.setPowerData(1500, 1500);
        }
    }


    /**
     * 速度模式
     */
    public void setSpeed(int mode) {
        FlyCtrl.mLWUartProtolBean.mControlPara.speed = mode;
    }

    /**
     * 校准加速度计
     */
    public void accCalibrate() {
        FlyCtrl.mLWUartProtolBean.mControlPara.saveCommandTime = System.currentTimeMillis();
        FlyCtrl.mLWUartProtolBean.mControlPara.accCalibrate = 1;
    }

    /**
     * 发送地磁校准命令
     */
    public boolean geoCalibrate() {
        FlyCtrl.mLWUartProtolBean.mControlPara.saveCommandTime = System.currentTimeMillis();
        FlyCtrl.mLWUartProtolBean.mControlPara.geoCalibrate = 1;
        return true;
    }

    /**
     * 镜头反向
     */
    public void setMirrorCamera() {
        switch (leweiLibType){
            case lib93:
                int flip = LeweiLib.LW93SendGetCameraFlip();
                if (flip == 0) {
                    LeweiLib.LW93SendSetCameraFlip(3);
                } else if (flip == 3) {
                    LeweiLib.LW93SendSetCameraFlip(0);
                }
                break;
            case lib23:
                mStream23.setMirror();
                break;
            case lib63:
                LeweiLib63.LW63SetMirrorCamera();
                break;
        }
    }

    public void remoteSensing(boolean isOpen) {
        FlyCtrl.mLWUartProtolBean.mControlPara.joystickOn = isOpen ? 1 : 0;
    }

    /**
     * 查询
     */
    public String queryMileageList() {
        LWMileageDao lwMileageDao = new LWMileageDao(context);
        LWDBManager lwdbManager = LWDBManager.getInstance(context);
        lwdbManager.beginTransaction();
        boolean resultCheck = false;
        try {
            List<FlyMileage> mileages = lwMileageDao.queryMileageList();
            resultCheck = true;

            return new Gson().toJson(mileages);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lwdbManager.endTransaction(resultCheck);
        }
        return "error";
    }

    /**
     *
     */
    public String queryHeightData(int flyId) {
        LWHeightDao lwHeightDao = new LWHeightDao(context);
        LWDBManager mDBManager_check = LWDBManager.getInstance(context);
        mDBManager_check.beginTransaction();
        boolean resultCheck = false;
        try {
            List<FlyHeight> heights = lwHeightDao.queryListByFlyNumberId(flyId);
            resultCheck = true;
            return new Gson().toJson(heights);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mDBManager_check.endTransaction(resultCheck);
        }
        return "error";
    }

    /**
     * 根据time删除记录
     * @param times  多条用,分隔
     * @return 是否删除成功
     */
    public boolean deleteHeightData(String times){
        LWHeightDao dao_del = new LWHeightDao(context);
        LWDBManager mDBManager_del = LWDBManager.getInstance(context);
        mDBManager_del.beginTransaction();
        boolean resultDel = false;
        try {

            String[] split = times.split(",");
            for (String s : split) {
                dao_del.deleteHeightByTime(s);
            }
            resultDel = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mDBManager_del.endTransaction(resultDel);
        }
        return resultDel;
    }


    ///发送设置参数
    public void onSettingDialogClose(int maxHeight, int minHeight) {
        // 获取数据
        FlyCtrl.mLWUartProtolBean.mControlPara.saveCommandTime = System.currentTimeMillis();
        FlyCtrl.mLWUartProtolBean.mControlPara.flyParaInfo.limitedHeight = maxHeight;// 发送最大高度
        FlyCtrl.mLWUartProtolBean.mControlPara.flyParaInfo.homewardHeight = minHeight;// 返航最低高度
        FlyCtrl.mLWUartProtolBean.mControlPara.pointset = 1;
    }

    public void getFlyFiles() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                RecList[] recLists = LeweiLib.LW93SendGetRecList();
//                handler.sendMessage()
            }
        }.start();

    }


    /**
     * GPS初始化(当前坐标的初始化)
     */
    /**
     * GPS相关
     **/
    private LocationManager gpsManager;

    private MyLocationListener mLocation = null;

    private FlyCtrl mFlyCtrl;

    public void initLib93() {
        mLeweiLib.setHandler(handler);
        mLeweiLib.startCMDThread();
        mLeweiLib.setOnTcpListener(tcpListener);
        mFlyCtrl = new FlyCtrl(handler);

        mFlyCtrl.startSendDataThread93();
        mFlyCtrl.receiveData93();
    }

    public Handler handler = new Handler(msg -> {

        switch (msg.what) {
            case FlyCtrl.FLY_PARAMETER:
                // 一直保存飞机的参数
//                countMileage();// 计算里程
                insertParameterData();
                break;

            case FlyCtrl.MAP_DETAIL:
                LWUartProtolBean lwUartProtol = (LWUartProtolBean) msg.obj;
                LwPlugin.mEventSink.success("{\"lwUartProtol\":" + new Gson().toJson(lwUartProtol) + "}");
                break;

            case HandlerParams.SURFACE_VISIBLE:
                if (FlutterSurfaceView.surfaceView != null && FlutterSurfaceView.surfaceView.getVisibility() != View.VISIBLE)
                    FlutterSurfaceView.surfaceView.setVisibility(View.VISIBLE);
                break;

            case HandlerParams.SURFACE_INVISIBLE:
//                if (FlutterSurfaceView.surfaceView != null && FlutterSurfaceView.surfaceView.getVisibility() != View.INVISIBLE)
//                    FlutterSurfaceView.surfaceView.setVisibility(View.INVISIBLE);
                break;

            // 判断链接了93的wifi
            case HandlerParams.CONNECT_93WIFI:
                wifiInfo = wifiManager.getConnectionInfo();
                int level = wifiInfo.getRssi();
                LwPlugin.mEventSink.success("{\"wifiLevel\":" + level + "}");
                break;

            case HandlerParams.GET_FIRST_FRAME_93:
                leweiLibType = LeweiLibType.lib93;
                break;
            case HandlerParams.GET_FIRST_FRAME_63:
                leweiLibType = LeweiLibType.lib63;
                break;
            case HandlerParams.GET_FIRST_FRAME_23:
                leweiLibType = LeweiLibType.lib23;
                break;

            // 收到航点指令
            case FlyCtrl.MAP_HANGDIAN:
                isUnlock = true;
                isFlyUP = true;
                ///todo yioam
                Log.e(TAG, "收到航点指令，设置按钮不能再发送数据");
                break;

            case FlyCtrl.FLY_MODE7:
                isUnlock = true;
                isFlyUP = true;
                Log.e(TAG, "跟随模式");
                break;
            case FlyCtrl.FLY_MODE8:
                isUnlock = true;
                isFlyUP = true;
                isCheckGPSSignal = false;
                Log.e(TAG, "环绕模式");
                break;
            case FlyCtrl.FLY_STATE1:
                isUnlock = false;
                isReturn = false;
                isFlyUP = false;
                if (isFlyState) {
                    isFlyState = false;
                    // 停止

                    upParameterData();

                }
                isStartFly = false;
                startFlyTime = 0;
                break;
            case FlyCtrl.FLY_STATE2:
                isUnlock = true;
                /************/
                if (isFlyState) {
                    isFlyState = false;
                    upParameterData();
                    // Log.e(TAG, "andy_aaaabbbb-停止飞机保存 ");
                }
                isStartFly = false;
                startFlyTime = 0;
                break;
            case FlyCtrl.FLY_STATE3:
                isFlyUP = true;
                isUnlock = true;

                isFlyState = true;
                if (!isStartFly) {
                    isStartFly = true;
                    startFlyTime = System.currentTimeMillis();
                    timeDate = DateUtils.getStringDateShort();// 获取当前的时间
                    // 这里做保存飞行记录的处理
                    setFightRecord();
                    // Log.e(TAG, "andy_aaaabbbb-起飞保存");

                    initId();
                    // Log.e(TAG, "andy_aaaabbbb-起飞保存 id=" + id);
                }
                break;
            case FlyCtrl.FLY_STATE4:
                isFlyUP = false;
                Log.e(TAG, "失控返航");
                break;
            case FlyCtrl.FLY_STATE9:
                isFlyUP = false;
                isReturn = false;
                // 如果正在是跟随模式，那么停止
                isFollowing = false;
                Log.e(TAG, "一键降落");
                break;
            case FlyCtrl.FLY_STATE10:
                isFlyUP = true;
                Log.e(TAG, "一键起飞");
                break;
        }
        return true;
    });

    /**
     * 飞机返回的数据的写入
     */
    private void insertParameterData() {

        /******** 这里的插入里程的数据就是为了测试用而已 ************/
        int flyMode = FlyCtrl.mLWUartProtolBean.mFlyInfo.mFlyMode;// 获取模式
        // 飞行状态
        int flyState = FlyCtrl.mLWUartProtolBean.mFlyInfo.mFlySate;

        // 获取电压
        int channel = FlyCtrl.mLWUartProtolBean.mFlyInfo.BatVal;

        // 升降速,就是垂直速度
        float d_speed_v = FlyCtrl.mLWUartProtolBean.mFlyInfo.speed;

        // 水平速度
        float d_speed_h = FlyCtrl.mLWUartProtolBean.mFlyInfo.velocity;

        // 高度
        float d_altitude = FlyCtrl.mLWUartProtolBean.mFlyInfo.height;
        // 距离
        float d_distance = FlyCtrl.mLWUartProtolBean.mFlyInfo.distant;
        // 仰角
        float pitch = FlyCtrl.mLWUartProtolBean.mFlyInfo.attitude.pitch;
        // 偏角
        float yaw = FlyCtrl.mLWUartProtolBean.mFlyInfo.attitude.yaw;

        // 卫星数目

        int satellitenum = FlyCtrl.mLWUartProtolBean.mFlyInfo.GpsNum;
        // 精度
        float accuracy = FlyCtrl.mLWUartProtolBean.mFlyInfo.GPSAccuracy;

        // 纬度
        double latitude = FlyCtrl.mLWUartProtolBean.mFlyInfo.coordinate.latitude;

        // 经度
        double longitude = FlyCtrl.mLWUartProtolBean.mFlyInfo.coordinate.longitude;

        String parameterTimeDate = DateUtils.getTimeShort();// 获取当前的时间
        FlyHeight mItem = new FlyHeight();
        mItem.setHeightTime(parameterTimeDate);
        mItem.setFly_mode(flyMode);// 模式
        mItem.setFly_state(flyState);// 状态
        mItem.setFly_bettery(channel);// 电量
        mItem.setLifting_speed(d_speed_v);// 升降速
        mItem.setSpeed(d_speed_h);// 速度
        mItem.setHeight(d_altitude);// 高度
        mItem.setDistance(d_distance);// 距离
        mItem.setElevation(pitch);// 仰角
        mItem.setDeflection(yaw);// 偏角
        mItem.setStar_number(satellitenum);// 星数

        mItem.setAccuracy(accuracy);// 精度
        mItem.setLatitude(latitude);// 纬度
        mItem.setLongitude(longitude);// 经度

        mItem.setFly_number_id(fly_number_id);// ID
        LWHeightDao m_dao = new LWHeightDao(context);
        LWDBManager mDBManager = LWDBManager.getInstance(context);
        mDBManager.beginTransaction();
        boolean result = false;
        try {
            m_dao.insertHeight(mItem);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mDBManager.endTransaction(result);
        }

    }

    private final OnTcpListener tcpListener = new OnTcpListener() {

        @Override
        public void TcpRemoteKeyListener(int key_value, int key_index,
                                         int key_status) {
            Log.d(TAG, "TcpRemoteKeyListener: 111");

            if (key_value == 1) {
                // media.playShutter();
            } else if (key_value == 2) {


            } else if (key_value == 3) {

            } else if (key_value == 4) {

            }
        }

        @Override
        public void TcpReceive(byte[] data, int size) {
            Log.d(TAG, "TcpReceive: 111");
        }

        @Override
        public void TcpDisconnected() {
            Log.d(TAG, "TcpDisconnected: 111");
        }

        @Override
        public void TcpConnected() {
            Log.d(TAG, "TcpConnected: 111");
        }

        @Override
        public void OnCameraOk(int CameraOk, int cameraCurFps) {
            Log.d(TAG, "OnCameraOk: 111");
        }

        @Override
        public void OnTakePhotoOk(int CameraOk, String path) {

            Log.d(TAG, "OnTakePhotoOk: 111");
        }
    };


    /******************* 指定连网 ********************/

    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;

    public void initNetwork() {
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        MyApplication.context.registerReceiver(networkChangeReceiver, intentFilter);
    }


    /**
     * 实现GPS的方法
     */
    public void gps() {

        if (isOPen()) {
            // gps已经打开了

            // 定义Criteria对象
            Criteria criteria = new Criteria();
            // 定位的精准度
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            // 海拔信息是否关注
            criteria.setAltitudeRequired(false);
            // 对周围的事情是否进行关心
            criteria.setBearingRequired(false);
            // 是否支持收费的查询
            criteria.setCostAllowed(true);
            // 是否耗电
            criteria.setPowerRequirement(Criteria.POWER_LOW);
            // 对速度是否关注
            criteria.setSpeedRequired(false);

            /**
             * 获取用户位置 1.用哪种方法定位,这里的provider是由上面获取的,系统自己计算出用哪个方法定位最好
             * 2.两次获取位置信息之间的间隔(推荐大于60秒 也就是600000 设置为0表示不关心时间 只要能得到位置信息就获取)
             * 3.每隔几米获取一次位置信息 0表示不关心 只要能获取到位置信息就获取
             */
            mLocation = new MyLocationListener();

            if (ActivityCompat.checkSelfPermission(MyApplication.context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MyApplication.context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            gpsManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocation, Looper.getMainLooper());
        } else {
            Toast.makeText(MyApplication.context, "mLocationDialog.show()", Toast.LENGTH_LONG).show();
        }

    }

    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     *
     * @return true 表示开启
     */
    private boolean isOPen() {

        if (gpsManager == null) gpsManager = (LocationManager) MyApplication.context.getSystemService(Context.LOCATION_SERVICE);

        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        return gpsManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private final class NetworkChangeReceiver extends BroadcastReceiver {
        @SuppressLint("NewApi")
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager cm = (ConnectivityManager) MyApplication.context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo == null) {
                // WIFI 和 移动网络都关闭 即没有有效网络
                // Log.e("aaa", "当前无网络连接");
                return;
            }
            String typeName = "";
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                // WIFI网络跳转的页面.
                typeName = networkInfo.getTypeName();// ==> WIFI
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                // 无线网络跳转的页面
                typeName = networkInfo.getTypeName();// ==> MOBILE
            }
            // Log.e("aaa", "==>" + typeName);
            // Log.e("aaa", "==>" + networkInfo.getDetailedState());

            if (Build.VERSION.SDK_INT >= 21) {
                final ConnectivityManager connectivityManager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkRequest.Builder builder = new NetworkRequest.Builder();

                // 设置指定的网络传输类型(蜂窝传输) 等于手机网络
                builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);

                // 设置感兴趣的网络功能
                builder.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);

                NetworkRequest request = builder.build();
                ConnectivityManager.NetworkCallback callback = new ConnectivityManager.NetworkCallback() {
                    @Override
                    public void onAvailable(Network network) {
                        // TODO Auto-generated method stub
                        super.onAvailable(network);
                        // Log.i("aaa", "已根据功能和传输类型找到合适的网络");

                        // 可以通过下面代码将app接下来的请求都绑定到这个网络下请求
                        if (Build.VERSION.SDK_INT >= 23) {
                            connectivityManager.bindProcessToNetwork(network);
                        } else {
                            // 23后这个方法舍弃了
                            ConnectivityManager
                                    .setProcessDefaultNetwork(network);
                        }

                    }
                    /**
                     * Called when the framework connects and has declared a new
                     * network ready for use. This callback may be called more
                     * than once if the {@link Network} that is satisfying the
                     * request changes.
                     *
                     */

                };
                connectivityManager.requestNetwork(request, callback);
            }

        }
    }

    /***********************************************/

    // 实现GPS监听接口
    private class MyLocationListener implements LocationListener {
        @Override
        // 位置的改变
        public void onLocationChanged(Location location) {
            if (location != null) {
                if (location.getLatitude() != 0.0 && location.getLongitude() != 0.0) {
                    if (isFollowing) {
                        // 跟随模式

                        if (isStartFollow) {

                            isStartFollow = false;
                            mFlyCtrl.getFlyFollowControlData();

                        }
                        // 直接赋值经纬度

                        /** 这里是发送跟随的数据 **/

                        FlyCtrl.mLWUartProtolBean.mControlPara.userCoordinate.latitude = location.getLatitude();
                        // 1138623280
                        FlyCtrl.mLWUartProtolBean.mControlPara.userCoordinate.longitude = location.getLongitude();
                        Log.e(TAG, "定位成功=" + isFollowing);
                        /*****************/

                        isCheckGPSSignal = false;
                    }
                } else {
                    // 提示gps信号不好
                    if (!isCheckGPSSignal) {
                        isCheckGPSSignal = true;
                        Toast.makeText(MyApplication.context, "gps信号不好..", Toast.LENGTH_LONG).show();
                    }
                }

            }
        }

        @Override
        // gps卫星有一个没有找到
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub
        }

        @Override
        // 某个设置被打开
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        // 某个设置被关闭
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }

    }

    /**
     * 计算飞机飞行时长
     */
    // 合并飞行时长
    private String timeDuration = "0";

    private String getTimeDuration() {
        /******* 时长的计算 ******/
        long getCurTime = System.currentTimeMillis();
        long totalSeconds = (getCurTime - startFlyTime) / 1000;
        // 求出现在的秒
        long currentSecond = totalSeconds % 60;
        String strSecond = String.valueOf(currentSecond);

        // 秒转换为零点几分钟
        double douSecone = currentSecond / 60.0;
        // 保留一位小数
        BigDecimal b_s = new BigDecimal(douSecone);
        float getMin = (float) b_s.setScale(1, BigDecimal.ROUND_HALF_UP)
                .floatValue();

        // 求出现在的分
        long totalMinutes = totalSeconds / 60;
        long currentMinute = totalMinutes % 60;
        String strcurrentMinute = String.valueOf(currentMinute + getMin);

        // 合并飞行时长
        // timeDuration = strcurrentMinute + "min" + strSecond + "s";
        timeDuration = strcurrentMinute + "min";
        /************/
        return timeDuration;
    }

    /**
     * 更新数据
     */
    private void upParameterData() {

        String getT = getTimeDuration();// 获取飞机飞行的时长

        LWMileageDao m_dao = new LWMileageDao(context);
        LWDBManager mDBManager = LWDBManager.getInstance(context);
        mDBManager.beginTransaction();
        boolean result = false;
        try {
            // Log.e(TAG, "LW93MainActivity.fly_number_id="
            // + fly_number_id);
            // Log.e(TAG, "andy_aaaabbbb-更新飞机状态 id="+fly_number_id);
            FlyMileage item = new FlyMileage();
            item.setFly_number_id(fly_number_id);
            item.setMileage(countMileage());
            item.setHeight(mFlyCtrl.getMaxAltitude());
            item.setDuration(getT);
            m_dao.update(item);

            // Log.e(TAG, "LW93MainActivity.fly_number_id="
            // + LW93MainActivity.fly_number_id);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mDBManager.endTransaction(result);
        }

        // 重新初始化一下这些数据
        mFlyCtrl.cleanRecrodData();

        count1 = 0;
        countTime1 = 0;
        countTime2 = 0;
        countTime3 = 0;
        mMileage1 = 0;
        mMileage2 = 0;
        mMileage3 = 0;
        sum = 0;
    }

    /**
     * 插入数据
     */
    private void setFightRecord() {

        // 插入数据
        FlyMileage mItem = new FlyMileage();
        mItem.setMileageTime(timeDate);// 保存时间
        mItem.setMileage(0.9f);// 里程
        mItem.setHeight(mFlyCtrl.getMaxAltitude());// 保存飞行的最大高度
        mItem.setDuration(timeDuration);// 保存飞行时长 ,就是从已解锁已起飞开始算减去上锁时候的时间差

        LWMileageDao dao = new LWMileageDao(context);
        LWDBManager mDBManager = LWDBManager.getInstance(context);
        mDBManager.beginTransaction();

        boolean result = false;
        try {
            dao.insertMil(mItem);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mDBManager.endTransaction(result);
        }

    }

    private int initId() {
        int id = -1;
        LWMileageDao m_dao = new LWMileageDao(context);
        LWDBManager mDBManager = LWDBManager.getInstance(context);
        mDBManager.beginTransaction();
        boolean result = false;
        try {
            id = m_dao.getFinalId();
            fly_number_id = id;
            Log.e(TAG, "m_dao.getFinalId()=" + id);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mDBManager.endTransaction(result);
        }
        return id;
    }

    ;

    // ==========插入飞机返回来的各个参数===========//

    /**
     * 计算里程
     */

    private int count1 = 0;
    long countTime1 = 0, countTime2 = 0;
    double countTime3 = 0;
    double mMileage1 = 0, mMileage2 = 0, mMileage3 = 0, sum = 0;

    private float countMileage() {

        if (count1 == 0) {
            countTime1 = System.currentTimeMillis();
            count1 = 1;
            if (countTime2 != 0) {
                countTime3 = (countTime1 - countTime2) / 1000.0;
                mMileage1 = countTime3 * FlyCtrl.mLWUartProtolBean.mFlyInfo.velocity;
            }
        } else if (count1 == 1) {
            countTime2 = System.currentTimeMillis();
            count1 = 0;
            countTime3 = (countTime2 - countTime1) / 1000.0;
            mMileage2 = countTime3 * FlyCtrl.mLWUartProtolBean.mFlyInfo.velocity;

        }
        mMileage3 = mMileage1 + mMileage2;
        sum += mMileage3;

        // 保留一位小数
        BigDecimal b_s = new BigDecimal(sum);
        return (float) b_s.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
    }
}

enum LeweiLibType {
    lib93,
    lib63,
    lib23,
    empty
}

