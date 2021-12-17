package com.klh.lwsample.controller;

import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.amap.api.maps.model.LatLng;
import com.klh.lwsample.constant.HandlerParams;
import com.lewei.lib.LeweiLib;
import com.lewei.lib63.LeweiLib63;
import com.lewei.uart_protol.ControlPara;
import com.lewei.uart_protol.Coordinate;
import com.lewei.uart_protol.LWUartProtolBean;
import com.lewei.uart_protol.LWUartProtolSdk;
import com.lewei.uart_protol.PointPara;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class FlyCtrl {
    private static final String TAG = "FlyCtrl";

    private Thread sendThread93;
    private Thread sendThread63;
    private Thread recThread93;
    private Thread recThread63;
    private boolean isRecStop93 = false;
    private boolean isStop63 = false;
    private boolean isNeedSendData = true;

    private long sendOneKeyUpTime = 0;
    private long sendOneKeyjiaozhengTime = 0;
    private long sendOneKeyDownTime = 0;
    private long sendOneKeyStopTime = 0;
    private long sendOneKeyLockTime = 0;
    private long sendOneKeyFlip = 0;
    private long sendOneKeyReturn = 0;
    private long sendOneKeyFollow = 0;
    private long sendOneKeyHeadless = 0;

    private static long sendOneKeyFollowTime = 0;

    private boolean isSendOneKeyUp = false;
    private boolean isSendOneKeyDown = false;
    private boolean isOneKeyStop = false;
    private boolean isJiaozheng = false;
    private boolean isOneKeyLock = false;
    private boolean isOneKeyFlip = false;
    private boolean isOneKeyReturn = false;
    private boolean isOneKeyFollow = false;
    private boolean isOneKeyHeadless = false;

    private boolean isLow = false;
    private boolean isHeadless = false;

    public final static int ONEKEYSTOP = (0x01 << 6);// 紧急停止
    public final static int ONEKEYFLYUP = (0x01 << 4);// 起飞
    public final static int ONEKEYFLYDOWN = (0x01 << 5);// 下降
    public final static int ONEKEYFLIP = (0x01 << 3);// 360翻转
    public final static int ONEKEYJIAOZHENG = (0x01 << 7);// 校准


    /**
     * 地图相关
     **/
    public final static int MAP_DETAIL = 7999;
    public final static int MAP_ROLL = 8000;
    public final static int MAP_PITCH = 8001;
    public final static int MAP_YAW = 8002;
    public final static int MAP_ALTITUDE = 8003;
    public final static int MAP_DISTANCE = 8004;
    public final static int MAP_SPEED_V = 8005;
    public final static int MAP_SPEED_H = 8006;
    public final static int MAP_LATITUDE_S = 8007;
    public final static int MAP_LATITUDE_N = 8008;
    public final static int MAP_LONGITUFE_E = 8009;
    public final static int MAP_LONGITUFE_W = 8010;

    public final static int MAP_LOCK = 8011;
    public final static int MAP_UNLOCK = 8012;
    public final static int MAP_TAKEOFF = 8013;
    public final static int MAP_LANDING = 8014;
    public final static int MAP_TRACK_MODE = 8015;
    public final static int MAP_FOLLOW_MODE = 8016;
    public final static int MAP_CIRCLE = 8017;
    public final static int MAP_STOP = 8018;
    public final static int MAP_HOMEWARD = 8019;
    public final static int MAP_ControlDataHover = 8020;
    public final static int MAP_AccelerometerCalibration = 8021;
    public final static int MAP_GeomagnetometerCalibration = 8022;
    public final static int MAP_getWayPointParam = 8023;
    public final static int MAP_satellitenum = 8024;

    public final static int MAP_AccelerometerCalibration_unlock = 8025;
    public final static int MAP_AccelerometerCalibration_ok = 8026;
    public final static int MAP_AccelerometerCalibration_fail = 8027;

    public final static int MAP_getWayPointParam_start = 8028;
    public final static int MAP_getWayPointParam_ok = 8029;
    public final static int MAP_getWayPointParam_fail = 8030;

    public final static int FLY_STATE1 = 8031;
    public final static int FLY_STATE2 = 8032;
    public final static int FLY_STATE3 = 8033;
    public final static int FLY_STATE4 = 8034;
    public final static int FLY_STATE5 = 8035;
    public final static int FLY_STATE6 = 8036;
    public final static int FLY_STATE7 = 8037;
    public final static int FLY_STATE8 = 8038;
    public final static int FLY_STATE9 = 8039;

    public final static int FLY_MODE1 = 8040;
    public final static int FLY_MODE2 = 8041;
    public final static int FLY_MODE3 = 8042;
    public final static int FLY_MODE4 = 8043;
    public final static int FLY_MODE5 = 8044;
    public final static int FLY_MODE6 = 8045;
    public final static int FLY_MODE7 = 8046;
    public final static int FLY_MODE8 = 8047;
    public final static int FLY_MODE9 = 8048;

    public final static int FLY_STATE10 = 8049;

    public final static int SHOW_AIR_POSITION = 8050;// 这个用来一直更新飞机返回的经纬度在地图上的位置

    public final static int MAP_HANGDIAN = 8051;
    public final static int MAP_HUANRAO = 8052;
    public final static int MAP_FOLLOW = 8053;

    public final static int MAP_AIR = 8054;// 飞行
    public final static int FLYMODE_GPS = 8055;// GPS

    public final static int RETURN_BACK = 8056;
    public final static int RETURN_UP = 8057;
    public final static int RETURN_FLY = 8058;
    public final static int RETURN_DOWN = 8059;

    public final static int FLY_MODE10 = 8060;// 航点模式

    public final static int TAKE_DOWN_FINISH = 8061;// 降落完成

    public final static int MAP_calib_progress = 8064;

    public final static int MAP_Accuracy = 8065;

    public final static int MAP_get_paraInfoSync = 8066;

    public final static int MAP_Calib_x = 8067;
    public final static int MAP_Calib_y = 8068;

    public final static int FOLLOW_OFF = 8076;//

    public final static int FOLLOW_ON = 8077;//
    public final static int FLY_PARAMETER = 8080;
    public static int getCalib = 0;
    public static int calibPress = 0;

    /*
     * data send to serial
     */
    public static byte[] serialdata = new byte[18];///63才需要
    public static int[] rudderdata = new int[18];

    // LW93MainActivitiy画地图线轨迹或者点轨迹时候保存的经纬度(高德地图)（最终得到发送的经纬度数据）
    public static List<LatLng> gaode_mapLatLngList = new ArrayList<LatLng>();
    // LW93MainActivitiy画地图线轨迹或者点轨迹时候保存的经纬度(谷歌地图)（最终得到发送的经纬度数据）
    public static List<LatLng> mapLatLngList = new ArrayList<LatLng>();

    // 保存对应航点对应的高度(轨迹模式)默认30m
    public static List<Integer> mapTtrackPointHeight = new ArrayList<Integer>();
    // 保存对应航点，停留时间
    public static List<Integer> mapTtrackPointStayTime = new ArrayList<Integer>();
    // 保存对应航点，速度
    public static List<Integer> mapTtrackPointSpeed = new ArrayList<Integer>();

    // 保存航点对应的高度（环绕模式）
    public static int mapCirclePointHeight = 10;
    // 保存航点对应的半径（环绕模式）
    public static int mapCirclePointRadius = 3;
    // 保存航点对应的速度(设置里面的)
    public static int mapCirclePointSpeed = 0;
    public static int mapCirclePointCycles = 0;

    public static LatLng gaode_mAirLatlng = null;// 高德地图用来接收飞机返回的经纬度
    public static LatLng google_mAirLatlng = null;// 高德地图用来接收飞机返回的经纬度
    // (22.575034,113.857521)

    private int cur_number = 26;

    public static int trim_left_landscape = 0; // 微调补偿 左侧横向
    public static int trim_right_landscape = 0; // 右侧 横向
    public static int trim_right_portrait = 0; // 右侧 纵向

    private boolean isFlipCtrl = false;
    private boolean isFlipOver = false;
    private int sendFlipTimes = 0;
    private boolean isX = true;
    private boolean isOver = true;

    // private Handler handler;
    private static Handler handler;


    /**
     * 发送数据的新方法
     **/
    public static byte[] get_sendData;

    public static LWUartProtolBean mLWUartProtolBean;
    public static LWUartProtolSdk mLWUartProtolSdk;

    /**
     * 初始化数据
     */
    public void initSendData() {
        mLWUartProtolSdk = new LWUartProtolSdk();
        mLWUartProtolBean = new LWUartProtolBean();
        // Protocol_LWGPS_HF Protocol_None Protocol_LWGPS
        mLWUartProtolBean.mControlPara.uartProtol = ControlPara.Uart_Protocol.Protocol_None;
        mLWUartProtolBean.mControlPara.supportGLGPSType = ControlPara.Uart_Protocol.Protocol_GLGPS;
        mLWUartProtolBean.mControlPara.ctlmode = ControlPara.ControlMode.CTL_Joystick;

        mLWUartProtolBean.mControlPara.joystickPara.throttle = 1500;
        mLWUartProtolBean.mControlPara.joystickPara.rudder = 1500;// 转向
        mLWUartProtolBean.mControlPara.joystickPara.aileron = 1500;// 左右
        mLWUartProtolBean.mControlPara.joystickPara.elvator = 1500;// 前后

        mLWUartProtolBean.mControlPara.joystickPara.rudderTrim = 32;
        mLWUartProtolBean.mControlPara.joystickPara.aileronTrim = 32;// 左右微调
        mLWUartProtolBean.mControlPara.joystickPara.elvatorTrim = 32;// 前后微调

        mLWUartProtolBean.mControlPara.joystickPara.ptz_h = 1500; // 云台-水平
        mLWUartProtolBean.mControlPara.joystickPara.ptz_v = 1500; // 云台-垂直

        mLWUartProtolBean.mControlPara.getFlyInfo = 1;// 获取飞机版本号 型号要设置这个

    }

    public FlyCtrl(Handler handler) {
        // this.handler = handler;
        FlyCtrl.handler = handler;
        rudderdata[1] = 1500;
        rudderdata[2] = 1500;
        rudderdata[3] = 1500;
        rudderdata[4] = 1500;
        rudderdata[13] = 1500;
        rudderdata[14] = 1500;

        initSendData();

        Log.i(TAG, "initialize the serial data.");
    }

    public static String byteToHex(byte[] bytes) {
        if (bytes == null) {
            return null;
            // Log.e(TAG, "nulllll");
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            hex = hex + " ";
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * 发送数据给飞机
     */
    public void startSendDataThread93() {
        if (sendThread93 == null || !sendThread93.isAlive()) {
            sendThread93 = new Thread() {
                @Override
                public void run() {
                    isNeedSendData = true;


                    LeweiLib.LW93InitUdpSocket();

                    while (isNeedSendData) {

                        updateSendData();
                        if (get_sendData != null) {

                            LeweiLib.LW93SendUdpData(get_sendData, get_sendData.length);
                            Log.e(TAG, "最终发送数据Data: " + byteToHex(get_sendData));

                        }

                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                    LeweiLib.LW93CloseUdpSocket();
                }
            };
            sendThread93.start();
        }
    }


    private static void ParseUartProtol() {
//		Log.e("eee", "mLWUartProtolBean.mControlPara.enumType："
//				+ mLWUartProtolBean.mControlPara.enumType);
        if (mLWUartProtolBean.mControlPara.enumType == 0) {
            mLWUartProtolBean.mControlPara.uartProtol = ControlPara.Uart_Protocol.Protocol_None;
        } else if (mLWUartProtolBean.mControlPara.enumType == 1) {
            //mLWUartProtolBean.mControlPara.uartProtol = ControlPara.Uart_Protocol.Protocol_HYGPS;
        } else if (mLWUartProtolBean.mControlPara.enumType == 2) {
            mLWUartProtolBean.mControlPara.uartProtol = ControlPara.Uart_Protocol.Protocol_LW;
        } else if (mLWUartProtolBean.mControlPara.enumType == 3) {
            mLWUartProtolBean.mControlPara.uartProtol = ControlPara.Uart_Protocol.Protocol_LW_D;
        } else if (mLWUartProtolBean.mControlPara.enumType == 4) {
            mLWUartProtolBean.mControlPara.uartProtol = ControlPara.Uart_Protocol.Protocol_RCLeading;
        } else if (mLWUartProtolBean.mControlPara.enumType == 5) {
            mLWUartProtolBean.mControlPara.uartProtol = ControlPara.Uart_Protocol.Protocol_Udirc;
        } else if (mLWUartProtolBean.mControlPara.enumType == 6) {
            mLWUartProtolBean.mControlPara.uartProtol = ControlPara.Uart_Protocol.Protocol_XBM;
        } else if (mLWUartProtolBean.mControlPara.enumType == 7) {
            mLWUartProtolBean.mControlPara.uartProtol = ControlPara.Uart_Protocol.Protocol_XBM_D;
        } else if (mLWUartProtolBean.mControlPara.enumType == 8) {
            mLWUartProtolBean.mControlPara.uartProtol = ControlPara.Uart_Protocol.Protocol_WL;
        } else if (mLWUartProtolBean.mControlPara.enumType == 9) {
            mLWUartProtolBean.mControlPara.uartProtol = ControlPara.Uart_Protocol.Protocol_WL2;
        } else if (mLWUartProtolBean.mControlPara.enumType == 10) {
            mLWUartProtolBean.mControlPara.uartProtol = ControlPara.Uart_Protocol.Protocol_Helicmax;
        } else if (mLWUartProtolBean.mControlPara.enumType == 11) {
            mLWUartProtolBean.mControlPara.uartProtol = ControlPara.Uart_Protocol.Protocol_XT1AR;
        } else if (mLWUartProtolBean.mControlPara.enumType == 12) {
            mLWUartProtolBean.mControlPara.uartProtol = ControlPara.Uart_Protocol.Protocol_BTGPS;
        } else if (mLWUartProtolBean.mControlPara.enumType == 13) {
            mLWUartProtolBean.mControlPara.uartProtol = ControlPara.Uart_Protocol.Protocol_LWGPS;
        } else if (mLWUartProtolBean.mControlPara.enumType == 14) {
            mLWUartProtolBean.mControlPara.uartProtol = ControlPara.Uart_Protocol.Protocol_LWGPS_HF;
        } else if (mLWUartProtolBean.mControlPara.enumType == 15) {
            mLWUartProtolBean.mControlPara.uartProtol = ControlPara.Uart_Protocol.Protocol_LWGPS_YD;
        } else if (mLWUartProtolBean.mControlPara.enumType == 16) {
            mLWUartProtolBean.mControlPara.uartProtol = ControlPara.Uart_Protocol.Protocol_FLGPS;
        } else if (mLWUartProtolBean.mControlPara.enumType == 17) {
            mLWUartProtolBean.mControlPara.uartProtol = ControlPara.Uart_Protocol.Protocol_GLGPS;
        } else if (mLWUartProtolBean.mControlPara.enumType == 18) {
            mLWUartProtolBean.mControlPara.uartProtol = ControlPara.Uart_Protocol.Protocol_FY602;
        } else if (mLWUartProtolBean.mControlPara.enumType == 19) {
            mLWUartProtolBean.mControlPara.uartProtol = ControlPara.Uart_Protocol.Protocol_LWGPS_HK;
        } else if (mLWUartProtolBean.mControlPara.enumType == 20) {
            mLWUartProtolBean.mControlPara.uartProtol = ControlPara.Uart_Protocol.Protocol_HYGPS;
        } else if (mLWUartProtolBean.mControlPara.enumType == 21) {
            mLWUartProtolBean.mControlPara.uartProtol = ControlPara.Uart_Protocol.Protocol_LWFollow;
        } else if (mLWUartProtolBean.mControlPara.enumType == 22) {
            mLWUartProtolBean.mControlPara.uartProtol = ControlPara.Uart_Protocol.Protocol_HY;
        } else if (mLWUartProtolBean.mControlPara.enumType == 23) {
            mLWUartProtolBean.mControlPara.uartProtol = ControlPara.Uart_Protocol.Protocol_WSGPS;
        }
    }

    /**
     * 接收数据处理
     */
    public void receiveData93() {
        recThread93 = new Thread(() -> {
            isRecStop93 = false;

            while (!isRecStop93) {
                try {
                    byte[] recvBufFlyState = null;

                    recvBufFlyState = LeweiLib.LW93RecvUdpData();
                    
                    handler.sendEmptyMessage(recvBufFlyState == null ? HandlerParams.SURFACE_INVISIBLE : HandlerParams.SURFACE_VISIBLE);
                    interpretingData(recvBufFlyState);
                } catch (Exception e) {

                    // TODO: handle exception
                }
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        });
        recThread93.start();
    }

    static boolean isFo = false;

    /**
     * 解析数据 飞机返回来的
     */
    public static void interpretingData(byte[] recvBuf) {

        mLWUartProtolSdk.LWUartProtolFlyInfoParseData(recvBuf, recvBuf.length, ControlPara.Uart_Protocol.Protocol_None, mLWUartProtolBean);
        ParseUartProtol();
        sendHandlerMessage(MAP_DETAIL, mLWUartProtolBean);
     

        /********** 保存飞机数据 **************/

        long nowtime = System.currentTimeMillis();
        if (((nowtime - sendOneKeyFollowTime) > 1500)
                && FlyCtrl.mLWUartProtolBean.mFlyInfo.RF_follow == 1 && !isFo) {
            // 开启跟随和取消跟随都用同的一个
            isFo = true;

            sendHandlerMessage(FlyCtrl.FOLLOW_ON, null);

        } else if (((nowtime - sendOneKeyFollowTime) > 1500) && FlyCtrl.mLWUartProtolBean.mFlyInfo.RF_follow == 0 && isFo) {
            isFo = false;
            sendHandlerMessage(FlyCtrl.FOLLOW_OFF, null);
        }
        // 飞行模式
        int flyMode = mLWUartProtolBean.mFlyInfo.mFlyMode;



        // 飞行状态
        int flyState = mLWUartProtolBean.mFlyInfo.mFlySate;

        if (flyState == 1) {
            // 上锁
            sendHandlerMessage(FLY_STATE1, null);
        } else if (flyState == 2) {
            // 解锁
            sendHandlerMessage(FLY_STATE2, null);
        } else if (flyState == 3) {
            // 飞行
            sendHandlerMessage(FLY_STATE3, null);
            // Log.e(TAG, "已解锁已起飞");
        } else if (flyState == 4) {
            sendHandlerMessage(FLY_STATE4, null);
            // Log.e(TAG, "失控返航");
        } else if (flyState == 5) {///todo 提示文字
            sendHandlerMessage(FLY_STATE5, null);
            // Log.e(TAG, "一级返航");
        } else if (flyState == 6) {
            sendHandlerMessage(FLY_STATE6, null);
            // Log.e(TAG, "二级级返航");
        } else if (flyState == 7) {
            sendHandlerMessage(FLY_STATE7, null);
            // Log.e(TAG, "一键返航");
        } else if (flyState == 8) {
            sendHandlerMessage(FLY_STATE8, null);
            // Log.e(TAG, "低压降落");
        } else if (flyState == 9) {
            sendHandlerMessage(FLY_STATE9, null);
            // Log.e(TAG, "一键降落");
        } else if (flyState == 10) {
            sendHandlerMessage(FLY_STATE10, null);
            // Log.e(TAG, "一键起飞");
        }

        if (flyState == 1 || flyState == 2) {
            //上锁,或者解锁没有起飞时候不保存
        } else {
            sendHandlerMessage(FLY_PARAMETER, null);
        }

        // 校验进度
        calibPress = mLWUartProtolBean.mFlyInfo.CalibProgress;
        sendHandlerMessage(MAP_calib_progress, calibPress);
        // 加计校准指示
        int acc_Calib = mLWUartProtolBean.mFlyInfo.AccCalib;
        if (acc_Calib == 0) {
            // 开始校准加速度计
            sendHandlerMessage(MAP_AccelerometerCalibration, null);
        } else if (acc_Calib == 1) {
            // 校准加速度计成功
            sendHandlerMessage(MAP_AccelerometerCalibration_ok, null);
        } else if (acc_Calib == 2) {
            // 校准加速度计失败
            // sendHandlerMessage(
            // MAP_AccelerometerCalibration_fail,
            // null);
        }
        // 表示飞机返回的gps已经定位完成
        // 飞机的GPS正常 0无GPS，1有GPS初始化正常
        int gps_fine = mLWUartProtolBean.mFlyInfo.GpsFine;
        if (gps_fine == 1) {
            // 已定位
            FlyModel.getInstance().isLocationSuccess = true;
            FlyModel.getInstance().isGPSLocationSuccess = true;

        } else if (gps_fine == 0) {
            // 未定位
            FlyModel.getInstance().isLocationSuccess = false;
        }
        // 设置参数设置结果，0 失败，1 成功
        int para_InfoSetOK = mLWUartProtolBean.mFlyInfo.paraInfoSetOK;
        if (para_InfoSetOK == 0) {
            // Log.e(TAG, "设置参数里面的设置失败");
        } else if (para_InfoSetOK == 1) {
            // Log.e(TAG, "设置参数里面的设置成功");
        }
        // 收到环绕模式指令，0 失败，1 成功
        int circle_FlyOk = mLWUartProtolBean.mFlyInfo.circleFlyOk;
        if (circle_FlyOk == 0) {

        } else if (circle_FlyOk == 1) {
            sendHandlerMessage(MAP_HUANRAO, null);
            // Log.e(TAG, "收到环绕指令");
        }
        // 收到航点模式指令，0 失败，1 成功
        int point_FlyOk = mLWUartProtolBean.mFlyInfo.pointFlyOk;
        if (point_FlyOk == 0) {

        } else if (point_FlyOk == 1) {
            sendHandlerMessage(MAP_HANGDIAN, null);
            // Log.e(TAG, "收到航点指令");
        }

        if (mLWUartProtolBean.mFlyInfo.paraInfoSync == 1) {
            FlyCtrl.mLWUartProtolBean.mControlPara.pointset = 0;// 这个因为不会自动置0,所以要手动变0
        }

        // // 距起飞点距离,水平速度,距起飞点高度, 垂直速
        float d_altitude = mLWUartProtolBean.mFlyInfo.height;

        // 计算飞行的最大高度
        maximumAltitude(d_altitude);

        // 如果是蓝光协议
        if (mLWUartProtolBean.mControlPara.uartProtol == ControlPara.Uart_Protocol.Protocol_GLGPS) {
            // 垂直速度:
            // float d_speed_v = mLWUartProtolBean.mFlyInfo.speed;
            float d_speed_v = mLWUartProtolBean.mFlyInfo.velocity;
            sendHandlerMessage(MAP_SPEED_V, d_speed_v);

            // 水平速度
            // float d_speed_h = mLWUartProtolBean.mFlyInfo.velocity;
            float d_speed_h = mLWUartProtolBean.mFlyInfo.speed;
            sendHandlerMessage(MAP_SPEED_H, d_speed_h);
        } else {
            // 垂直速度:
            float d_speed_v = mLWUartProtolBean.mFlyInfo.speed;
            // float d_speed_v = mLWUartProtolBean.mFlyInfo.velocity;
            sendHandlerMessage(MAP_SPEED_V, d_speed_v);

            // 水平速度
            float d_speed_h = mLWUartProtolBean.mFlyInfo.velocity;
            // float d_speed_h = mLWUartProtolBean.mFlyInfo.speed;
            sendHandlerMessage(MAP_SPEED_H, d_speed_h);
        }

    }

    /***************************/


    private boolean isMDis = false;
    private float dis_1 = 0.0f, dis_2 = 0.0f;
    private float maxD = 0.0f;

    /**
     * 计算最大距离
     *
     * @param distance 1 2 3
     */
    private void maximumDistance(float distance) {
        // Log.e(TAG, "maxD==" + maxD + "    distance=" + distance);
        if (!isMDis) {
            isMDis = true;
            if (distance < dis_1) {
                return;
            }
            dis_1 = distance;

        } else {
            isMDis = false;
            if (distance < dis_2) {
                return;
            }
            dis_2 = distance;

        }
        if (dis_1 > dis_2) {
            maxD = dis_1;
        } else {
            maxD = dis_2;
        }

    }

    /**
     * 得到最大距离
     *
     * @return
     */
    public float getMaxDistance() {
        return maxD;

    }


    private static boolean isMAlt = false;
    private static float alt_1 = 0.0f, alt_2 = 0.0f;
    private static float maxA = 0.0f;

    /**
     * 计算最大高度
     * <p>
     * 1 2 3
     */
    private static void maximumAltitude(float alt) {
        // Log.e(TAG, "maxA==" + maxA + "    distance=" + alt);
        if (!isMAlt) {
            isMAlt = true;
            if (alt < alt_1) {
                return;
            }
            alt_1 = alt;

        } else {
            isMAlt = false;
            if (alt < alt_2) {
                return;
            }
            alt_2 = alt;

        }
        if (alt_1 > alt_2) {
            maxA = alt_1;
        } else {
            maxA = alt_2;
        }

    }

    public float getMaxAltitude() {
        return maxA;
    }

    private boolean isMFtightS = false;
    private float fls_1 = 0.0f, fls_2 = 0.0f;
    private float maxFS = 0.0f;


    private boolean isMSpeed = false;
    private float speed_1 = 0.0f, speed_2 = 0.0f;
    private float maxSpeed = 0.0f;

    /**
     * 计算最大速度
     *
     * @return
     */
    private void maximumSpeed(float sp) {
        if (!isMSpeed) {
            isMSpeed = true;
            if (sp < speed_1) {
                return;
            }
            speed_1 = sp;

        } else {
            isMSpeed = false;
            if (sp < speed_2) {
                return;
            }
            speed_2 = sp;

        }
        if (speed_1 > speed_2) {
            maxSpeed = speed_1;
        } else {
            maxSpeed = speed_2;
        }

    }

    /**
     * 清除记录的数据
     */
    public void cleanRecrodData() {
        dis_1 = 0.0f;
        dis_2 = 0.0f;
        alt_1 = 0.0f;
        alt_2 = 0.0f;
        fls_1 = 0.0f;
        fls_2 = 0.0f;
        speed_1 = 0.0f;
        speed_2 = 0.0f;
        maxD = 0.0f;
        maxA = 0.0f;
        maxFS = 0.0f;
        maxSpeed = 0.0f;
    }

    /************************************/

    public void startSendDataThread63() {
        if (sendThread63 == null || !sendThread63.isAlive()) {
            sendThread63 = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.e(TAG, "start send serial data");
                        isNeedSendData = true;
                        int mClientCount = 0;
                        Thread.sleep(100);

                        while (isNeedSendData) {
                            mClientCount = LeweiLib63.LW63GetClientSize();
                            if (mClientCount == 1) {
                                break;
                            } else {
                                Thread.sleep(2000);
                            }
                        }

                        while (isNeedSendData) {
                            if (LeweiLib63.LW63GetLogined()) {
                                if (!LeweiLib63.LW63GetSerialState()) {
                                    LeweiLib63.LW63StartSerial(19200);
                                }

                                if (!FlyModel.getInstance().isAllCtrlHide) {
                                    updateSendData();
                                    LeweiLib63.LW63SendSerialData(serialdata,
                                            serialdata.length);

                                }
                                if (FlyModel.getInstance().isStartDrawing) {
                                    updateSendData();
                                    LeweiLib63.LW63SendSerialData(serialdata,
                                            serialdata.length);

                                }
                                Thread.sleep(50);
                                // Log.e(TAG, "  63Data:  "
                                // + byteToHex(serialdata));

                            } else {
                                if (LeweiLib63.LW63GetSerialState()) {
                                    LeweiLib63.LW63StopSerial();
                                }
                            }
                        }

                        Thread.sleep(20);
                        Log.e(TAG, "stop send serial data");
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            });
            sendThread63.start();
        }
    }

    /**
     * 主要负责发送遥杆的数据
     */
    private void updateSendData() {
        int rotate = FlyCtrl.rudderdata[4] + trim_left_landscape * 2;
        int throttle = rudderdata[3];
        int ail = rudderdata[1];
        int ele = rudderdata[2];

        Log.d(TAG, "updateSendData: " + rotate + "======" + throttle + "=========" + ail + "=====" + ele);

        // 收星完成才能推上油门起飞飞机

        mLWUartProtolBean.mControlPara.joystickPara.throttle = throttle;// 油门
        mLWUartProtolBean.mControlPara.joystickPara.rudder = rotate;// 转向
        mLWUartProtolBean.mControlPara.joystickPara.aileron = ail;// 左右
        mLWUartProtolBean.mControlPara.joystickPara.elvator = ele;// 前后
        checkSendOneKeyFlipTime();

        get_sendData = mLWUartProtolSdk.LWUartProtolGetControlData(ControlPara.Uart_Protocol.Protocol_None, mLWUartProtolBean);
    }

    /**
     * 摇杆打开，置1,关闭置0
     */
    public void joystick_On() {
        if (FlyModel.getInstance().isAllCtrlHide) {
            // 摇杆关闭
            mLWUartProtolBean.mControlPara.joystickOn = 0;
        } else {
            mLWUartProtolBean.mControlPara.joystickOn = 1;
        }
    }

    public void setLow_Hight(boolean isLows) {
        this.isLow = isLows;
    }

    public void setHeadless(boolean isH) {
        this.isHeadless = isH;
    }

    /**
     * 跟随模式发送的数据
     */

    public void getFlyFollowControlData() {

        // 坐标（22.5720210,113.8623280）
        mLWUartProtolBean.mControlPara.saveCommandTime = System.currentTimeMillis();
        mLWUartProtolBean.mControlPara.hover = 1;
        mLWUartProtolBean.mControlPara.ctlmode = ControlPara.ControlMode.CTL_Follow;//
        mLWUartProtolBean.mControlPara.followSendFlag = 1;
    }


    public void stopSendDataThread() {
        isNeedSendData = false;
    }

    public void stopRecDataThread() {
        isRecStop93 = true;
    }

    /**
     * 翻滚
     */
    private void checkSendOneKeyFlipTime() {
        long nowtime = System.currentTimeMillis();

        if (isOneKeyFlip) {
            if (nowtime - sendOneKeyFlip > 1200) {
                isOneKeyFlip = false;
                // sendDataBluRay[8] &= ~0x40;
                mLWUartProtolBean.mControlPara.roll = 0;
                // if (handler != null)
                // handler.sendEmptyMessage(HandlerParams.FLYCTRL_JIAO_ZHENG);
            } else {
                // if ((sendDataBluRay[8] & 0x40) > 0) {

                // } else
                {
                    mLWUartProtolBean.mControlPara.roll = 1;
                    // sendDataBluRay[8] |= 0x40;
                }
            }
        }

    }

    public void setFollowTime() {
        sendOneKeyFollowTime = System.currentTimeMillis();
    }

    private static void sendHandlerMessage(int type, Object obj) {
        if (handler != null) {
            Message message = Message.obtain();
            message.what = type;
            message.obj = obj;
            handler.sendMessage(message);
        }
    }

    public static void setRudderData(int x, int y) {
        FlyCtrl.rudderdata[3] = y;// 右手模式且未开启sensor，左边rudderY轴变前后，X轴不变
        FlyCtrl.rudderdata[4] = x;
    }

    public static void setPowerData(int x, int y) {
        FlyCtrl.rudderdata[1] = x;
        FlyCtrl.rudderdata[2] = y;
    }

}
