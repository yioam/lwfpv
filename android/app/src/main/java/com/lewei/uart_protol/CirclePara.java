package com.lewei.uart_protol;


/***
 * ////环绕参数
 * 
 * **/
public class CirclePara {
	public Coordinate center=new Coordinate();  //GPS 坐标  //环绕中心点坐标
	public float height;  //高度
	public  float speed;  //环绕速度
	public float radius;  //环绕半径
	public int circleNum;  //环绕圈数,0-一直环绕，手动退出
}
