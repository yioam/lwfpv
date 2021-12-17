package com.lewei.uart_protol;

/**
 * Created by Andy on  2020/7/9.
 * Email: 963069079@qq.com
 */
public class GLFlightRecord {
//    struct GLFlightRecord{
//        //蓝光 飞行记录
//        unsigned PwmMot0:11;    //马达0PWM        1000~2000
//        unsigned PwmMot1:11;    //马达1PWM        1000~2000
//        unsigned PwmMot2:11;    //马达2PWM        1000~2000
//        unsigned PwmMot3:11;    //马达3PWM        1000~2000
//        unsigned Armed:2;        //锁定状态             0上锁，1解锁，2飞行
//        unsigned RcStopt:1;        //遥控器急停            0正常，1 遥控器急停
//        unsigned AppStopt:1;        //App急停            0正常，1 App急停
//        unsigned LandLock:1;        //降落上锁            0正常，1 降落上锁
//        unsigned GyroErrLock:1;    //陀螺仪异常上锁        0正常，1 陀螺仪异常上锁
//    };
       public int PwmMot0;
        public int PwmMot1;
        public int PwmMot2;
        public int PwmMot3;
        public int Armed;
        public int RcStopt;
        public int AppStopt;
        public int LandLock;
        public int GyroErrLock;


}
