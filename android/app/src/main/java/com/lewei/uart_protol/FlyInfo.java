package com.lewei.uart_protol;


/**
 * 飞机信息
 * **/
public class FlyInfo {
	public float distant;  //距离起飞点距离   //
	public float height;    //距离起飞点高度
	public float velocity;   //水平速度
	public float speed;     //垂直速度
	public int BatVal;    //电池电压       0~100（%）
	public int BatVal1;            //飞机当前电压   mv
	public int BatVal2;            //遥控器当前电压   mv
	public int InsInitOk;    //陀螺仪正常            0无陀螺仪，1陀螺仪初始化正常
	public int BaroInitOk;    //气压计正常            0无气压计，1气压计初始化正常
	public int MagInitOk;    //罗盘正常            0无罗盘，1罗盘初始化正常
	public int GpsInitOk;    //GPS正常            0无GPS，1有GPS初始化正常
	public int FlowInitOk;     //光流正常            0无光流，1光流初始化正常
	public int AccCalib;       //加计校准指示        0开始校准，1校准成功，2校准失败
	public int GeoCalib;        //地磁校准指示           1 已解锁，不允许校准地磁 2开始校准 3 校准成功 4校准失败 5-开始校准 X，6-开始校准 Y
	public int MagXYCalib;     //罗盘X校准指示    0正在校准，1校准结束
	public int MagZCalib;      //罗盘Y校准指示    0正在校准，1校准结束
	public int CalibProgress;     //校准进度            0~100（%）
	public int LowBat;           //低电报警            0正常，1一次低电，2二次低电
	public int TempOver;          //温度报警            0无，1温度过高
	public int CurrOver;        //堵转报警            0无，1堵转保护，2倾角保护
	public int unLowHomeward;    //用户取消了低电返航
	public int GpsNum;     //卫星颗数            0~31颗
	public int GpsFine;     //GPS定位            0没有定位，1已经定位
	public int IndoorMode;        //室内模式
	public float GPSAccuracy;     //GPS 精度
	public Coordinate coordinate=new Coordinate();   //飞机 GPS 经纬度    各位如果微软各位如果
	public FlyAttitude attitude=new FlyAttitude();    //姿态角
	public FlyMode mode;
	public FlySate state;
	public FlyParaInfo paraInfo=new FlyParaInfo();

	public GLFlightRecord glFlightRcord=new GLFlightRecord();
	//struct GLFlightRecord glFlightRcord;

	public int paraInfoSetOK;  //参数设置结果，0 失败，1 成功
	public int circleFlyOk;   //环绕模式，0 失败，1 成功
	public int pointFlyOk;  //航点模式，0 失败，1 成功

	public int droneVersionSync;//1 -- 准备升级 2--正在更新，3--放弃升级，4--升级成功 5--升级出错
	public int versionSyncProgress;//升级进度，0~100

	public int versionSyncOK;


	public int fire;  //射击，开火
	public int RF_follow;  //蓝光遥控器跟随

	public int droneLedStatus;  //蓝光遥控器跟随
	public int VideoOn;        //摄像状态            0无，1正在摄像

	public int AutoLand;
//	// 欣琳app同步遥控器快档模式 --- by hl
//	unsigned RcFastMode:2;    //遥控器快档模式        0慢档，1中档，2快档, 3遥控器关闭
	public int RcFastMode;

	public int MagNoise;//电磁干扰量
	public int shockproofness;//振动水平
	public int HFParaInfoType;//01 黑飞old， 02 黑飞新类型


//	ShortVideoType_None =0,



	//############################
	public int mode_gps;//:2;        //定点模式            0非定点模式，1 GPS定点模式，2光流定点模式
	public int headless;//;        //  unsigned headless:1;        //无头模式
	//#############################
	public int paraInfoSync;//:1;       //同步飞机参数   增加

	public int rockerSignal;//7;        //遥控器信号强度
	public int rockerBal;//遥控器电量  0~100

	public int flyBeginnerMode;          //新手模式            0正常，1新手
	public int rockerLink;          //0遥控器未连接    1遥控器已连接（遥控器链接时app不要摇杆）
	public int rockerLowBat;  //0     1遥控器已低电

	public long update_time;//刷新时间
	public int preBatteryValue;//记录上一次电量，规避电量显示波动


	/*****
	 * FlyMode的枚举类型，jni赋值不了只能通过int类型的识别
	 * FlyMode_Hover,  //定高模式                 1
	 * FlyMode_Point,    //定点模式             2
	 * FlyMode_HomeWard, //返航模式             3
	 * FlyMode_Takeoff,  //起飞模式             4
	 * FlyMode_Landing,  //降落模式             5
	 * FlyMode_POI,      //航点模式             6
	 * FlyMode_Follow,   //跟随模式             7
	 * FlyMode_Circle,   //环绕模式             8
	 * FlyMode_selfStability, //自稳模式   9 
	 ***/
	public int mFlyMode;
	 public enum FlyMode {
		//飞行模式
		    FlyMode_Hover,  //定高模式
		    FlyMode_Point,    //定点模式
		    FlyMode_HomeWard, //返航模式
		    FlyMode_Takeoff,  //起飞模式
		    FlyMode_Landing,  //降落模式
		    FlyMode_POI,      //航点模式
		    FlyMode_Follow,   //跟随模式
		    FlyMode_Circle,   //环绕模式
		    FlyMode_selfStability, //自稳模式
		 FlyMode_ShortVideoCircle,   //短片-环绕   10
		 FlyMode_ShortVideoUpAway,   //短片-渐远   11
		 FlyMode_ShortVideoRocket,   //短片-冲天   12
		 FlyMode_ShortVideoSpiral,   //短片-螺旋   13
		 FlyMode_ShortVideoComet,   //短片-彗星    14
		 FlyMode_ShortVideoPlanetoid,   //短片-小行星   15

		 FlyMode_ShortVideoSpring,//弹跳   16
		 FlyMode_ShortVideoPanorama,//全景  17

	  }
	 
	 
		/**
		 * //飞行状态的枚举类型，jni赋值不了只能通过int类型的识别
		 *   Fly_Lock,          //已上锁                  1
		 *   Fly_UnLock,         //已解锁未起飞      2
		 *   Fly_Unlock_Takeoff, //已解锁已起飞      3
		 *   Fly_OutOfControl,   //失控返航             4
		 *   Fly_OneHomeWard,    //一级返航             5
		 *   Fly_TwoHomeWard,    //二级返航             6
		 *   Fly_HomeWard,       //一键返航             7
		 *   Fly_LPLanding,      //低电降落             8
		 *   Fly_Landing,        //一键降落             9
		 *   Fly_takeoff,        //一键起飞             10
		 * **/
		public int mFlySate;
	 public enum FlySate {
		//飞行状态
		    Fly_Lock,         //已上锁
		    Fly_UnLock,         //已解锁未起飞
		    Fly_Unlock_Takeoff, //已解锁已起飞
		    Fly_OutOfControl,   //失控返航
		    Fly_OneHomeWard,    //一级返航
		    Fly_TwoHomeWard,    //二级返航
		    Fly_HomeWard,       //一键返航
		    Fly_LPLanding,      //低电降落
		    Fly_Landing,        //一键降落
		    Fly_takeoff,        //一键起飞
		    Fly_Calibrate,//校准
		  Fly_Error,//故障
	  }
}
