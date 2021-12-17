package com.lewei.uart_protol;

/**
 * ////飞机参数
 * **/
public class FlyParaInfo {

	public int  voltage;    // 电压，0~100，100满电。
	public int altitude;    // 高度，单位1cm。
	public int  live;        // 血量，0~5，5满血，0落地。
	public int photo;    // 遥控器拍照  脉冲1s
	public int video;    // 遥控器录像  开启/正在录像(1)、停止(0)
	public int reserve;    // 保留
	public int  product;    // 产品形态。飞机(1)、车(2)，飞控检测到的状态，不可靠，仅供参考
	public int  sn;        // 当前执行动作的序列号。
	public int  acstatus;    // 动作状态。正在执行(1)、执行完成(0)。
	public int reserve1;    // 保留1
	public int reserve2;    // 保留2

	public int  flyinfo;  //是否有飞机信息
	public String name="";  //飞机型号
	public String version="";//飞机版本号
	public int MaxPoint;  //最大航点数
	public int Pointspeed;   //航点速度 0.1m/s
	public int Pointtime;   //航点停留时间 s
	public int homewardHeight;   // 返航高度
	public int limitedRadius;    //最大飞行半径
	public int limitedHeight;    //最大飞行高度
	public int circleRadius;     //环绕半径
	public int circleHeight;    //环绕高度
	public int followDistance;   //跟随距离
	public int followHeight;    //跟随高度
	public int takeoffHeight;   //起飞高度

	public int BatLowVal1; //一级报警电压   v*100
	public int BatLowVal2; //二级报警电压   v*100
	public int BatLowVal3; //三级报警电压   v*100
}
