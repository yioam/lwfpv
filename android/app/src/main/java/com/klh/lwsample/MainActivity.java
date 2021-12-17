package com.klh.lwsample;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.klh.lwsample.plugin.LwPlugin;
import com.klh.lwsample.plugin.MyViewFlutterPlugin;

import org.jetbrains.annotations.NotNull;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;

public class MainActivity extends FlutterActivity {
    
    public static Activity activity;


    @Override
    public void configureFlutterEngine(@NonNull @NotNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        activity = this;
        registerCustomPlugin(flutterEngine);
        MyViewFlutterPlugin.registerWith(flutterEngine);
    }

    private void registerCustomPlugin(@NonNull FlutterEngine flutterEngine) {
        LwPlugin.registerTIMPlugin(flutterEngine);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    
    
}
