package com.klh.lwsample;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.multidex.MultiDex;

import com.klh.lwsample.controller.FlyModel;
import com.klh.lwsample.util.ParamsConfig;
import com.klh.lwsample.util.PathConfig;
import com.lewei.lib.LeweiLib;

import java.util.TimeZone;

public class MyApplication extends Application {
    public static final String TAG = "MyApplication";

    public static Context context;

    static {
        try {
            System.loadLibrary("track");
            System.loadLibrary("jni_obtrack_api");
            FlyModel.getInstance().cpuV7a = true;
        } catch (UnsatisfiedLinkError ule) {
            Log.d(TAG, "static initializer: ");
            FlyModel.getInstance().cpuV7a = false;
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        FlyModel.getInstance().mLeweiLib = new LeweiLib();
        int mTimeZoneOffset = TimeZone.getDefault().getOffset(System.currentTimeMillis());
        FlyModel.getInstance().mLeweiLib.LW93NativeInit(PathConfig.getRootPath() + PathConfig.PHOTOS_PATH, LeweiLib.PORT_COMMON,
                LeweiLib.ENCRY_COMPATIBLE, mTimeZoneOffset / 1000);
        LeweiLib.HD_flag = ParamsConfig.readHDPLAY(getApplicationContext());
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }
}
