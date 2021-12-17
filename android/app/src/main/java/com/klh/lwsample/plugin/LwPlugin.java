package com.klh.lwsample.plugin;

import android.content.Intent;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.OrientationEventListener;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.klh.lwsample.MainActivity;
import com.klh.lwsample.MyApplication;
import com.klh.lwsample.bean.RecList;
import com.klh.lwsample.controller.FlyCtrl;
import com.klh.lwsample.controller.FlyModel;
import com.klh.lwsample.util.PathConfig;
import com.lewei.lib.LeweiLib;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

import static com.klh.lwsample.MyApplication.context;

public class LwPlugin implements MethodChannel.MethodCallHandler, EventChannel.StreamHandler {
    public static final String METHOD_CHANNEL = "lw_method_channel";
    public static final String EVENT_CHANNEL = "lw_event_channel";
    public static EventChannel.EventSink mEventSink;


    public static void registerTIMPlugin(@NonNull FlutterEngine flutterEngine) {
        MethodChannel methodChannel = new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), METHOD_CHANNEL);
        EventChannel eventChannel = new EventChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), EVENT_CHANNEL);
        LwPlugin handler = new LwPlugin();
        methodChannel.setMethodCallHandler(handler);
        eventChannel.setStreamHandler(handler);
    }


    @Override
    public void onListen(Object arguments, EventChannel.EventSink events) {
        mEventSink = events;
    }

    @Override
    public void onCancel(Object arguments) {

    }

    @Override
    public void onMethodCall(@NonNull @org.jetbrains.annotations.NotNull MethodCall call, @NonNull @org.jetbrains.annotations.NotNull MethodChannel.Result result) {
        switch (call.method) {
            case "test":
                Toast.makeText(context, "123", Toast.LENGTH_LONG).show();
                break;
            case "onCreate":
                FlyModel.getInstance().onCreate((boolean) call.arguments);
                break;
            case "onStop":
                break;
            case "onDestroy":
                FlyModel.getInstance().onDestroy();
                break;
            case "getFlyFiles":
                FlyModel.getInstance().getFlyFiles();
                break;
            case "takePhoto":
                FlyModel.getInstance().takePhoto();
                break;
            case "takeRec":
                result.success(FlyModel.getInstance().takeRec());
                break;
            case "getPhotoPath":

                result.success(Environment.getExternalStorageDirectory().toString() + PathConfig.PHOTOS_PATH);
                break;
            case "getPhVideoPath":

                result.success(Environment.getExternalStorageDirectory().toString() + PathConfig.VIDEOS_PATH);
                break;
            case "canUnlock":
                result.success(FlyModel.getInstance().canUnlock());
                break;
            case "unlock":
                FlyModel.getInstance().unlock();
                break;
            case "flyUp":
                result.success(FlyModel.getInstance().flyUp(false));
                break;
            case "unlockAndFlyUp":
                result.success(FlyModel.getInstance().flyUp(true));
                break;
            case "flyDown":
                FlyModel.getInstance().flyDown();
                break;

            case "setVRView":
                FlyModel.getInstance().setVRView();
                break;

            case "setPlaneView":
                FlyModel.getInstance().setPlaneView();
                break;
            case "setTrackRouteControlData":
                Log.d("TAG", "onMethodCall: ");
                FlyModel.getInstance().setTrackRouteControlData((List<Map>) call.arguments);
                break;
            case "setStableMode":
                FlyModel.getInstance().setStableMode();
                break;
            case "setWaypointMode":
                FlyModel.getInstance().setWaypointMode();
                break;
            case "setFollowMode":
                FlyModel.getInstance().setFollowMode();
                break;
            case "setSurroundMode":
                FlyModel.getInstance().setSurroundMode();
                break;

            case "setFlyCircleControlData":
                FlyModel.getInstance().setFlyCircleControlData((double) ((Map) call.arguments).get("latitude"), (double) ((Map) call.arguments).get("longitude"));
                break;
            //设置相关*****start
            case "onSettingDialogClose":
                FlyModel.getInstance().onSettingDialogClose((int) ((Map) call.arguments).get("maxHeight"), (int) ((Map) call.arguments).get("minHeight"));

                // 这个因为不会自动置0,所以要手动变0    FlyCtrl.mLWUartProtolBean.mControlPara.pointset = 0
                break;
            case "setRightHandMode":
                FlyModel.getInstance().setRightHandMode((boolean) call.arguments);
                break;
            case "setSensorOn":
                FlyModel.getInstance().setSensorOn((boolean) call.arguments);
                break;
            case "setSpeed":
                FlyModel.getInstance().setSpeed((int) call.arguments);
                break;
            case "accCalibrate":
                FlyModel.getInstance().accCalibrate();
                break;
            case "geoCalibrate":
                result.success(FlyModel.getInstance().geoCalibrate());
                break;
            case "getIs1080Model":
                int moduleType = LeweiLib.getModuleType();
                result.success(moduleType == 16 || moduleType == 18 || moduleType == 25);
                break;
            case "queryMileageList":
                result.success(FlyModel.getInstance().queryMileageList());
                break;
            case "queryHeightData":
                result.success(FlyModel.getInstance().queryHeightData((int)call.arguments));
                break;
            case "deleteHeightData":
                result.success(FlyModel.getInstance().deleteHeightData((String)call.arguments));
                break;
            case "setMirrorCamera":
                FlyModel.getInstance().setMirrorCamera();
                break;
            //设置相关*****end

            case "refreshPhoto":
                Uri uri = Uri.parse("file:///" + call.arguments);
                MyApplication.context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                break;

            case "shareImage":

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);//设置分享行为
                shareIntent.setType("image/*");  //设置分享内容的类型
                shareIntent.putExtra(Intent.EXTRA_STREAM, (String) call.arguments);
                //创建分享的Dialog
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "分享");//添加分享内容标题
                MainActivity.activity.startActivity(Intent.createChooser(shareIntent, "分享"));
                break;

            case "setRudderData":///1500 ± 500
                Map rudderData = (Map) call.arguments;
                FlyCtrl.setRudderData((int) rudderData.get("x"), (int) rudderData.get("y"));
                break;
            case "setPowerData":///1500 ± 500
                Map powerData = (Map) call.arguments;
                FlyCtrl.setPowerData((int) powerData.get("x"), (int) powerData.get("y"));
                break;
            case "isGPSLocationSuccess":
                FlyModel.getInstance().isGPSLocationSuccess = (boolean) call.arguments;
                break;
            case "isShowAirPlan":
                FlyModel.getInstance().isShowAirPlan = (boolean) call.arguments;
                break;
            case "remoteSensing":
                FlyModel.getInstance().remoteSensing((boolean)call.arguments);
                break;
            case "getRecList":
                RecList[] a = LeweiLib.LW93SendGetRecList();
                Log.d("TAG", "onMethodCall: ");
                break;
        }
    }

    ///返回遥感数据
    public static void returnRudderData(int x, int y) {
        if (mEventSink != null) {
            mEventSink.success("{\"returnRudderData\":{\"x\":" + x + ",\"y\":" + y + "}}");
        }
    }


}
