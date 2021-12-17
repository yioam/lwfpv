package com.lewei.uart_protol;

/**
 * Created by Andy on  2019/9/21.
 * Email: 963069079@qq.com
 */
public class ActionPara {
    public int     sn;        // 序列号。演示或编程模式，每产生一个新动作，序列号加1。0到255然后再到0，循环。
    public int   style;        // 指令类型。动作(1)、灯光(2)、延时(3)
    public int   actionid;        // 动作id
    public int   parameter1;    // 动作参数1
    public int   parameter2;    // 动作参数2
}
