package com.klh.lwsample.plugin;

import com.klh.lwsample.view.MyViewFactory;

import io.flutter.embedding.engine.FlutterEngine;

public class MyViewFlutterPlugin {
    private static String NATIVE_VIEW_TYPE_ID = "com.lewei.flutter.surfaceView";
    
    public static void registerWith(FlutterEngine flutterEngine) {
        flutterEngine.getPlatformViewsController().getRegistry().registerViewFactory(NATIVE_VIEW_TYPE_ID, new MyViewFactory(flutterEngine.getDartExecutor().getBinaryMessenger()));
    }
}
