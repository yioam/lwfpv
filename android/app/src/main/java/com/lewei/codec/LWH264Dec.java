package com.lewei.codec;


import com.klh.lwsample.util.LWLogUtils;

/**
 * Created by Andy on 2018/4/10.
 * Email: 963069079@qq.com
 */

public class LWH264Dec {

    static {
        try {
            System.loadLibrary("ffmpeg");
            System.loadLibrary("leweiffmpeg_utile");
        } catch (UnsatisfiedLinkError ule) {
            LWLogUtils.e("loadLibrary(ffmpeg)," + ule.getMessage());
            System.out.println("loadLibrary(leweiffmpeg_utile)," + ule.getMessage());
        }
    }

    /****
     * 打开解码器
     * **/
    public static native int H264Open();
    /**
     * 解码一帧
     * ***/
    public static native int H264Decode(byte[] in, int insize,int[] variables, byte[] out,int iFrame);

    /***
     * 关闭解码器
     * ***/
    public static native void H264Close();
}
