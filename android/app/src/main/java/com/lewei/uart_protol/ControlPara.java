package com.lewei.uart_protol;


/**
 * 飞机控制类
 * ***/
public class ControlPara {

	public int  startFlag;
	public ProductMode product;    // 产品形态。飞机(1)、车(2)

	public ActionPara  actionPara=new ActionPara();//动作指令
	// 第一个位域(8B)
	public int shoot;    // 对战发射，按键按下(或者按住)为1、放开为0
	// 第二个位域(8B)
	public int photo;    // 拍照，正在拍照(1) 无动作(0)
	public int video;    // 遥控器录像  开启/正在录像(1)、停止(0)



	public int SeqNum;  //包序列号，0~255

	public JoystickPara joystickPara=new JoystickPara();  //摇杆参数
	public int unlock;   ///  //解锁/上锁       接口
	public int autoTakeoff;  //一键起飞   接口
	public int autoLanding;  //一键降落   接口
	public int speed;    //速度档 ,0 --低速，1--中速，2--高速          接口
	public int GpsMode;   //GPS 模式
	public int stop;      //急停            接口
	public int headless;    //无头模式     接口
	public int roll;         //翻滚模式         接口
	public int accCalibrate;   //水平仪校准 接口
	public int geoCalibrate;   //地磁校准   接口
	public int joystickOn;     //摇杆打开     接口
	public int circleFly;     //环绕模式
	public int pointFly;    //航点模式
	public int pointClear;   //清除航点，清除环绕、航点包   接口
	public int followFly;    //跟随标志，GL 协议控制      接口
	public int followMode;    //跟随标志,用于FLGPS 协议控制 接口
	public int followType;   //跟随标志,用于FLGPS 协议控制 接口
	public int hover;       //悬停   接口
	public int altHold;     //定高 接口
	public int  indoorMode;//室内模式 接口 1 室内模式 2 室外模式
	public int homeward;///  //返航 接口
	public int snap;      //拍照   接口
	public int recording;   //录像 接口
	public int recordCircle;  //环绕录像 接口
	public int getFlyInfo;    //获取飞机信息 接口
	public int pointget;    //获取航点参数  接口
	public int pointset;   //设置航点参数   接口
	public FlyParaInfo flyParaInfo=new FlyParaInfo(); //飞机参数  //接口
	public long saveCommandTime;   //控制标志修改时间
	public ControlMode ctlmode;   //控制模式 接口
	public Uart_Protocol uartProtol;
	public Uart_Protocol multiProtol;
	public Uart_Protocol supportLWGPSType;
	public Uart_Protocol supportGLGPSType;//支持蓝光、慧源GPS类型，两种协议只能兼容一种指定
	public ShortVideoType shortVideoType;
	public int shortVideoType_int;   //反设上层要知道是否已经设置shortVideoType ，需要重新设置
	public Coordinate userCoordinate=new Coordinate();   //手机 GPS 经纬度 接口    EGWERGWERGWERGE
	public int horizontalAccuracy;   //跟随发送标志 接口    EGWERGWER 1
	public float GpsSpeed;//运动速度
	public float header;//运动方向
	public int GPSAuthorizationStatusDenied;   //跟随发送标志 接口    EGWERGWER 1
	public int followSendFlag;   //跟随发送标志 接口    EGWERGWER 1
	public long followTime;     //跟随发送时间
	
	public CirclePara circlePara=new CirclePara();   ////环绕参数
	public int circleSendFlag;    //环绕发送标志 接口
	public PointPara[] pointArray;  //这个的长度赋值给 pointNum   //	//航点参数
	public int pointNum;   //航点数 接口

	public int pointCout;   //发送计数
	public int PointSendFlag;  //航点发送标志 接口
	public int commantCount;
	public long heartTime;  //心跳包时间
	public long  WSControlDataheartTime; //心跳包时间 80ms发一次
	public long  WSGetDataHeartTime; //心跳包时间 100ms fayici

	public int test;
	public int  MFTest;//生产测试模式  ;///  unsigned MFTest:1;//生产测试模式

	public int enumType=-1;  //todo 这个是标识协议枚举的类型  Uart_Protocol
	public int multiProtolType=-1;  //todo 这个是标识协议枚举的类型  Uart_Protocol

	public int batLowBalGet; //获取低电参数  接口
	public int batLowBalSet;//设置低电参数   接口

	public int handTack;//手势操控
	public int droneLed;//

	public int beginnerMode;//新手模式


	public VisionFollowPara visionPara=new VisionFollowPara();

	public enum VisionTrackID
	{
		VisionTrack_None ,//不支持视觉跟随   followType 00
		VisionTrack_HelicuteH818HW,//飞立特H818HW followType 01
		VisionTrack_AttopXT1PLUS,//雅得XT-1 PLUS followType 02
		VisionTrack_JX1802,//捷翔1802    followType 03
		VisionTrack_HeliMax1332,//亨迪1332   followType 04
		VisionTrack_WLModelQ636,//伟力Q636   followType 05
		VisionTrack_LMRCF2,//大道F2    followType 06
		VisionTrack_JYGPS020,//君怡020  followType 07
		VisionTrack_XLLA6HW,//小六郎A6HW  followType 08
		VisionTrack_YH19HW,//毅恒YH-19HW  followType 09
		VisionTrack_FY603R,//飞宇FY603R  followType 10
		VisionTrack_CG033,//奥森马CG033  followType 11
		VisionTrack_U37,//优迪 U37   followType 12
		VisionTrack_U88PlusG,//优迪 U88Plus G   followType 13
		VisionTrack_XinXun01,//鑫讯,型号不明   followType 14
		VisionTrack_XMZK01,//小名智控，型号不明   followType 15
		VisionTrack_JY021,//君怡JY021   followType 16
		VisionTrack_XBM23, //小白马    17
		VisionTrack_HelicuteH826HW,  //飞立特H826HW  18
		VisionTrack_Xinlin,//欣琳   19
		VisionTrack_xiaoliulang,//小六郎   20
		VisionTrack_DaMing, //达铭
		VisionTrack_suMing,//速铭 16xx
		VisionTrack_XSW819,//天驱819 MR100
		VisionTrack_JX1811,//捷翔1811 MR100
		VisionTrack_XT1PLUS_MR100,//雅得XT-1 PLUS MR100

	}

	public enum ShortVideoType
	{
		ShortVideoType_None,    //shortVideoType_int=0
		ShortVideoType_UpAway,//渐远  shortVideoType_int=1
		ShortVideoType_Circle,//环绕  shortVideoType_int=2
		ShortVideoType_Spiral,//螺旋上升  shortVideoType_int=3
		ShortVideoType_Rocket,//一飞冲天  shortVideoType_int=4
		ShortVideoType_Comet,//彗星 轨道  shortVideoType_int=5
		ShortVideoType_Planetoid,//小行星 轨道  shortVideoType_int=6
		ShortVideoType_Panorama,//全景  shortVideoType_int=7
		ShortVideoType_Spring,//弹跳模式  shortVideoType_int=8
	}

	public enum ProductMode{
	        	ProductModeDrone,         //飞机
				ProductModeCar,             //小车
				ProductModeRobot,           //机器人
	}
	
	public enum ControlMode{ //控制模式
	    CTL_Joystick, //自稳模式，摇杆操控
	    CTL_Follow,     //跟随
	    CTL_Circle,     //环绕
	    CTL_Point,      //航点/指点
		CTL_VisionFollow, ////视觉跟随
		CTL_ShortVideo,//短片模式
		CTL_show,//演示模式
		CTL_Program,//编程模式
		CTL_VR,//演示模式
		CTL_ModeNull,//编程模式
	}
	public enum Uart_Protocol//协议，需要兼容时，设置为Protocol_None
	{
		Protocol_None,    //未知协议   enumType=0
		Protocol_LW,       //乐为公版   1
		Protocol_LW_D,     //乐为公版-舵机   2
		Protocol_RCLeading,//遥控前沿   3
		Protocol_Udirc,    //优迪   4
		Protocol_XBM,      //小白马   5
		Protocol_XBM_D,    //小白马-舵机  6
		Protocol_WL,       //伟力   7
		Protocol_WL2,      //伟力二厂   8
		Protocol_CX,       //澄星   9
		Protocol_Helicmax, //亨迪    10
		Protocol_XT1AR,    //雅得 XT-1 AR   11
		Protocol_BTGPS,    //无锡比特-万机   12
		Protocol_LWGPS,    //乐为公版 GPS   13
		Protocol_LWGPS_HF, //乐为公版 黑飞智能(辉科专用) GPS    14
		Protocol_LWGPS_YD, //乐为公版 雅得 GPS   15
		Protocol_FLGPS,    //飞轮-科霸电子   16
		Protocol_GLGPS,    //蓝光 GPS  17
		Protocol_FY602,    //飞宇602    18
		Protocol_LWGPS_HK,    //乐为公版 辉科(辉科专用) gps   19
		Protocol_HYGPS,    //慧源 GPS   20

		Protocol_LWGPS_HFWS,    //乐为公版 黑飞伟胜
		Protocol_LSGPS,    //乐为公版 乐升GPS

		Protocol_LWFollow,    //普通四轴协议，LW Follow 系列APP使用  21
		Protocol_HY,//慧源普通版本协议    22
		Protocol_WSGPS,//纬盛GPS协议    23
		Protocol_HHFGPS,   //鸿昊丰GPS协议  24
		Protocol_FSGPS,    //大飞沙GPS协议   25
		Protocol_LW_FOR4DRC,       // 乐为公版 有改动 for 辉科 4drc  26

	}

	public long saveSendTwoSecondTime;  //发两秒遥感数据时间   辉科没有遥感，默认发两秒遥感数据

}
