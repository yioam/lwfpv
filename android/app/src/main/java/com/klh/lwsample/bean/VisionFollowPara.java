package com.klh.lwsample.bean;

import com.lewei.uart_protol.ControlPara;

/**
 * Created by Andy on 2018/6/5.
 * Email: 963069079@qq.com
 */

public class VisionFollowPara {
//    float objectX;
//    float objectY;
//    float objectWidth;
//    float objectHeight;
//    long long resultTime; //视觉识别结果时间
//
//    float bmpWidth;
//    float bmpHeight;
//    uint8_t trackRudderSpeed;//转向速度  1~10 分10档
//    uint8_t trackElvatorSpeed;//前后速度  1~10 分10档
//
//    unsigned trackFollowState:1; // 0-正常跟踪，1-丢失
//    unsigned rudderSearch:2; //1-水平方向搜索 2-水平搜索结束
//    long long searchStartTime;//开始搜索时间
//    long long searchRudderDuration;//水平方向搜索持续时间
//
//    unsigned throttleSeatch:2;//1-向上搜索  2- 向下搜索 3 垂直方向搜索结束
//    float limitedMaxH;//最大飞行高度
//    float limitedMinH;//最低飞行高度
//    unsigned start:1;//设置目标时置1
//unsigned autoMode:3; //模式-0 支持前进后退、转向、距离控制;1-支持前进后退、转向、升降\距离控制;2 支持前进后退、转向，不支持距离控制;3 支持转向、升降;4 只支持转向

//    public float objectX;
//    public  float objectY;
//    public  float objectWidth;
//    public  float objectHeight;
//    public long resultTime; //视觉识别结果时间
//    public  float bmpWidth;
//    public  float bmpHeight;
//    public int trackRudderSpeed;//转向速度  1~10 分10档;
//    public int supportSearch;//  unsigned supportSearch:1; // 0-不支持搜索，1-支持搜索
//    public int trackElvatorSpeed;//前后速度  1~10 分10档
//    public int trackFollowState; // 0-正常跟踪，1-丢失
//    public int  rudderSearch; //1-水平方向搜索 2-水平搜索结束
//    public long searchStartTime;//开始搜索时间;
//    public long searchRudderDuration;//水平方向搜索持续时间
//    public int throttleSeatch;//1-向上搜索  2- 向下搜索 3 垂直方向搜索结束
//    public float limitedMaxH;//最大飞行高度;
//    public float limitedMinH;//最低飞行高度
//    public VisionRect rect=new VisionRect();
//    public int  start;//设置目标时置1
//    public int  autoMode;


    public ControlPara.VisionTrackID droneID;
    public VisionRect rect=new VisionRect();
    public long resultTime; //视觉识别结果时间
    public float bmpWidth;
    public float bmpHeight;
    public int start;//设置目标时置1
    public int  trackRudderSpeed;//转向速度  1~10 分10档
    public int  trackElvatorSpeed;//前后速度  1~10 分10档

    public int  trackFollowState; // 0-正常跟踪，1-丢失

    public int  supportSearch; // 0-不支持搜索，1-支持搜索
    public int  autoMode; //模式-0 支持前进后退、转向、距离控制;1-支持前进后退、转向、升降\距离控制;2 支持前进后退、转向，不支持距离控制;3 支持转向、升降;4 只支持转向

    public int  rudderSearch; //1-水平方向搜索 2-水平搜索结束
    public  long searchStartTime;//开始搜索时间
    public long searchRudderDuration;//水平方向搜索持续时间

    public int  throttleSeatch;//1-向上搜索  2- 向下搜索 3 垂直方向搜索结束
    public float limitedMaxH;//最大飞行高度
    public float limitedMinH;//最低飞行高度



//    uint16_t  disElvatorOffset;//阈值
//    uint16_t  rudderOffset;
//    uint16_t  throttleOffset;
//    float elvatorXmax;
//    float elvatorXmin;
//    float elvatorYmax;
//    float elvatorYmin;
//    float elvatorYcenter;
//    long long reverseInterval;//相反方向移动间隔

    public int  disElvatorOffset;//
    public int  rudderOffset;//
    public int  throttleOffset;//
    public float elvatorXmax;//
    public float elvatorXmin;//
    public float elvatorYmax;//
    public float elvatorYmin;//
    public float elvatorYcenter;//
    public long reverseInterval;//

}
