package com.klh.lwsample.view;

import android.content.Context;
import android.view.View;

import com.klh.lwsample.MyApplication;
import com.klh.lwsample.controller.FlyModel;
import com.lewei.lib.Stream23;
import com.lewei.lib.Stream93;

import java.util.Map;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.platform.PlatformView;

public class FlutterSurfaceView implements PlatformView, MethodChannel.MethodCallHandler {
    public static final String TAG = "flutter====";
    public static MySurfaceView surfaceView;

    MySurfaceView view;

    public FlutterSurfaceView(Context context, BinaryMessenger messenger, int id, Map<String, Object> params) {
        view = new  MySurfaceView(context);
        view.setVisibility(View.INVISIBLE);

        if((boolean)params.get("copyBuffer")){
            surfaceView = view;
            view.post(() -> {
                ///93start
                if (FlyModel.getInstance().mStream93 != null) {
                    FlyModel.getInstance().mStream93.stopStream93();
                }
                FlyModel.getInstance().mStream93 = new Stream93(MyApplication.context, FlyModel.getInstance().handler);
                FlyModel.getInstance().mStream93.startStream93();
                ///93end

                ///23start
                if (FlyModel.getInstance().mStream23 != null) {
                    FlyModel.getInstance().mStream23.stopStream23();
                }
                FlyModel.getInstance().mStream23 = new Stream23(MyApplication.context, FlyModel.getInstance().handler);
                FlyModel.getInstance().mStream23.startStream23();
                ///23end


            });
        }

        MethodChannel methodChannel = new MethodChannel(messenger, "com.flutter/surfaceView" + (int)params.get("id"));
        methodChannel.setMethodCallHandler(this);
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public void dispose() {

    }

    @Override
    public void onMethodCall(MethodCall call, MethodChannel.Result result) {
        if ("copyBuffer".equals(call.method)) {
            surfaceView = view;
            result.success(null);
        }
    }
}
