package com.lewei.uart_protol;

/**
 * Created by Andy on 2018/6/5.
 * Email: 963069079@qq.com
 */

public class FlyTestInfo {
    public int gyro_x;            //陀螺仪X轴        -32767~32767
    public int gyro_y;            //陀螺仪Y轴        -32767~32767
    public int gyro_z;            //陀螺仪Z轴        -32767~32767
    public int acc_x;             //加速度X轴        -32767~32767
    public int acc_y;             //加速度Y轴        -32767~32767
    public int acc_z;             //加速度Z轴        -32767~32767
    public int mag_x;             //罗盘X轴          -32767~32767
    public int mag_y;             //罗盘Y轴          -32767~32767
    public int mag_z;             //罗盘Z轴          -32767~32767

    public float BatVal;              //电池电压            20.48 V
    public int GpsNum;            //卫星颗数            0~31颗

    public FlyAttitude attitude=new FlyAttitude();

    public int InsInitOk;        //陀螺仪正常            0无陀螺仪，1陀螺仪初始化正常
    public int BaroInitOk;       //气压计正常            0无气压计，1气压计初始化正常
    public int MagInitOk;        //罗盘正常            0无罗盘，1罗盘初始化正常
    public int GpsInitOk;        //GPS正常            0无GPS，1有GPS初始化正常
    public int FlowInitOk;       //光流正常            0无光流，1光流初始化正常

    //-------4Bytes
    public Coordinate coordinate=new Coordinate(); //飞机 GPS 经纬度
    public int GpsFine;            //GPS定位             0没有定位，1已经定位
   public float GpsQuality;         //GPS定位精度          精度0.01m

    //-------8Bytes
    public int temperature;        //温度                -127~127 (degree Celsius)
    public float baro_alt;           //气压计高度           0.1m


    //-------4Bytes
    public int current1;        //电流检测1            0~63



    public int current2;        //电流检测2            0~63
    @Override
    public String toString() {
        return "FlyTestInfo{" +
                "gyro_x=" + gyro_x +
                ", gyro_y=" + gyro_y +
                ", gyro_z=" + gyro_z +
                ", acc_x=" + acc_x +
                ", acc_y=" + acc_y +
                ", acc_z=" + acc_z +
                ", mag_x=" + mag_x +
                ", mag_y=" + mag_y +
                ", mag_z=" + mag_z +
                ", BatVal=" + BatVal +
                ", GpsNum=" + GpsNum +
                ", attitude=" + attitude +
                ", InsInitOk=" + InsInitOk +
                ", BaroInitOk=" + BaroInitOk +
                ", MagInitOk=" + MagInitOk +
                ", GpsInitOk=" + GpsInitOk +
                ", FlowInitOk=" + FlowInitOk +
                ", coordinate=" + coordinate +
                ", GpsFine=" + GpsFine +
                ", GpsQuality=" + GpsQuality +
                ", temperature=" + temperature +
                ", baro_alt=" + baro_alt +
                ", current1=" + current1 +
                ", current2=" + current2 +
                '}';
    }
}
