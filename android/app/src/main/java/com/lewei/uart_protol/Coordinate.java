package com.lewei.uart_protol;

/***
 * //GPS 坐标
 * **/
public class Coordinate {

	public double latitude;  //纬度
	public double longitude;  //经度

	@Override
	public String toString() {
		return "Coordinate{" +
				"latitude=" + latitude +
				", longitude=" + longitude +
				'}';
	}
}
