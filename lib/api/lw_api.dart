import 'dart:convert';

import 'package:flutter/services.dart';
import 'package:lewei_pro/bean/lw_uart_protol_bean.dart';
import 'package:get/get.dart';
import 'package:lewei_pro/contoller/fly_control.dart';
import 'package:lewei_pro/widget/top_error_tip_widget.dart';

class LwApi {
  static LwApi? _lwApi;
  MethodChannel? _methodChannel;
  EventChannel? _eventChannel;

  ///测试用
  bool needDrawing = true;

  LwApi._() {
    _initChannel();
  }

  static LwApi getInstance() {
    return _lwApi ??= LwApi._();
  }

  void _initChannel() async {
    _methodChannel = const MethodChannel('lw_method_channel');
    _eventChannel = const EventChannel('lw_event_channel');
    _eventChannel!.receiveBroadcastStream().listen(_onNativeCall);
  }

  ///
  void _onNativeCall(dynamic res) {
    try {
      Map<String, dynamic> map = jsonDecode(res.toString());
      if (map.containsKey("lwUartProtol")) {
        LwUartProtolBean lwUartProtolBean = Get.find();
        lwUartProtolBean.lwUartProtol = LwUartProtolBean.fromJson(map).lwUartProtol;

        num accCalib = lwUartProtolBean.lwUartProtol?.mFlyInfo?.accCalib ?? 0;
        if (accCalib == 1) topErrorTipValue.value = '校准加速度计成功';

        // 地磁校验返回参数
        num geoCalib = lwUartProtolBean.lwUartProtol?.mFlyInfo?.geoCalib ?? 0;
        if (geoCalib == 2) {
          topErrorTipValue.value = '开始校准地磁';
        } else if (geoCalib == 3) {
          topErrorTipValue.value = '校准地磁成功';
        } else if (geoCalib == 4) {
          topErrorTipValue.value = '校准地磁失败';
        } else if (geoCalib == 5) {
          // 开始校准 X
          topErrorTipValue.value = '开始校准X';
        } else if (geoCalib == 6) {
          // 开始校准 Y
          topErrorTipValue.value = '开始校准Y';
        }

        lwUartProtolBean.update();
      } else if (map.containsKey("wifiLevel")) {
        FlyController.getInstance().wifiLevel = map['wifiLevel'];
      } else if (map.containsKey("returnRudderData")) {
        ///刷新遥感中心点位置

        int x = map['returnRudderData']['x'];
        int y = map['returnRudderData']['y'];
        if (FlyController.getInstance().remoteControlMode != 1) {
          FlyController.getInstance().leftBallPointValue.value = [x, y];
        } else {
          FlyController.getInstance().rightBallPointValue.value = [x, y];
        }
      } else {
        print("原生消息（undefined）:$res");
      }
    } catch (e) {
      print('=======$e}');
    }
  }

  Future<String> test() async {
    final String value = await _methodChannel!.invokeMethod('test');
    print('123');
    return value;
  }

  Future<void> onCreate(bool hasPermission) async {
    if (!needDrawing) return;
    await _methodChannel!.invokeMethod('onCreate', hasPermission);
  }

  Future<void> onDestroy() async {
    if (!needDrawing) return;
    await _methodChannel!.invokeMethod('onDestroy');
  }

  ///拍照
  Future<String> takePhoto() async {
    final String value = await _methodChannel!.invokeMethod('takePhoto');
    return value;
  }

  /// 录视频/停止录视频
  /// return true则指令成功
  Future<bool> takeRec() async {
    final bool value = await _methodChannel!.invokeMethod('takeRec');
    return value;
  }

  /// 是否能解锁
  Future<bool> canUnlock() async {
    return await _methodChannel!.invokeMethod('canUnlock');
  }

  /// 解锁
  Future<void> unlock() async {
    await _methodChannel!.invokeMethod('unlock');
  }

  /// 起飞/降落
  Future<bool> flyUp() async {
    return await _methodChannel!.invokeMethod('flyUp');
  }

  /// 返航
  Future<bool> flyDown() async {
    return await _methodChannel!.invokeMethod('flyDown');
  }

  /// 一键起飞/降落
  Future<bool> unlockAndFlyUp() async {
    return await _methodChannel!.invokeMethod('unlockAndFlyUp');
  }

  ///分屏
  Future<bool> setVRView() async {
    return await _methodChannel!.invokeMethod('setVRView');
  }

  ///全屏
  Future<bool> setPlaneView() async {
    return await _methodChannel!.invokeMethod('setPlaneView');
  }

  ///右手模式
  Future<bool> setRightHandMode(bool mode) async {
    return await _methodChannel!.invokeMethod('setRightHandMode', mode);
  }

  ///重力模式
  Future<bool> setSensorOn(bool on) async {
    return await _methodChannel!.invokeMethod('setSensorOn', on);
  }

  ///速度模式
  Future<bool> setSpeed(int mode) async {
    //0 --低速，1--中速，2--高速
    return await _methodChannel!.invokeMethod('setSpeed', mode);
  }

  ///速度模式
  Future<bool> accCalibrate() async {
    return await _methodChannel!.invokeMethod('accCalibrate');
  }

  ///发送地磁校准命令
  Future<bool> geoCalibrate() async {
    return await _methodChannel!.invokeMethod('geoCalibrate');
  }

  ///720p ? 1080p
  Future<bool> getIs1080Model() async {
    return await _methodChannel!.invokeMethod('getIs1080Model');
  }

  ///获取照片存储路径
  Future<String> getPhotoPath() async {
    final String value = await _methodChannel!.invokeMethod('getPhotoPath');
    return value;
  }

  ///获取视频存储路径
  Future<String> getPhVideoPath() async {
    final String value = await _methodChannel!.invokeMethod('getPhVideoPath');
    return value;
  }

  ///
  Future<void> setRudderData(int x, int y) async {
    await _methodChannel!.invokeMethod('setRudderData', {'x': 1500 + x, 'y': 1500 - y});
  }

  ///
  Future<void> setPowerData(int x, int y) async {
    await _methodChannel!.invokeMethod('setPowerData', {'x': 1500 + x, 'y': 1500 - y});
  }

  ///刷新相册
  Future<void> refreshPhoto(String uri) async {
    await _methodChannel!.invokeMethod('refreshPhoto', uri);
  }

  ///分享图片
  Future<void> shareImage(String uri) async {
    await _methodChannel!.invokeMethod('shareImage', uri);
  }

  ///GPS信号良好才能起飞
  Future<void> isGPSLocationSuccess(bool b) async {
    await _methodChannel!.invokeMethod('isGPSLocationSuccess', b);
  }

  ///航点模式发送参数
  Future<void> setTrackRouteControlData(List<Map<String, dynamic>> markers) async {
    await _methodChannel!.invokeMethod('setTrackRouteControlData', markers);
  }

  ///自稳模式
  Future<void> setStableMode() async {
    await _methodChannel!.invokeMethod('setStableMode');
  }

  ///航点模式
  Future<void> setWaypointMode() async {
    await _methodChannel!.invokeMethod('setWaypointMode');
  }

  ///跟随模式
  Future<void> setFollowMode() async {
    await _methodChannel!.invokeMethod('setFollowMode');
  }

  ///跟环绕模式
  Future<void> setSurroundMode() async {
    await _methodChannel!.invokeMethod('setSurroundMode');
  }

  ///环绕模式发送参数
  Future<void> setFlyCircleControlData(double latitude, double longitude) async {
    await _methodChannel!.invokeMethod('setFlyCircleControlData', {'latitude': latitude, 'longitude': longitude});
  }

  ///发送设置里数据
  Future<void> onSettingDialogClose(int maxHeight, int minHeight) async {
    await _methodChannel!.invokeMethod('onSettingDialogClose', {'maxHeight': maxHeight, 'minHeight': minHeight});
  }

  ///查询飞行数据
  Future<String> queryMileageList() async {
    return await _methodChannel!.invokeMethod('queryMileageList');
  }

  ///查询飞行日志详情
  Future<String> queryHeightData(int id) async {
    return await _methodChannel!.invokeMethod('queryHeightData', id);
  }

  ///删除多条飞行日志
  Future<bool> deleteHeightData(String times) async {
    return await _methodChannel!.invokeMethod('queryHeightData', times);
  }

  ///镜头反向
  Future<void> setMirrorCamera() async {
    await _methodChannel!.invokeMethod('setMirrorCamera');
  }

  ///打开/关闭遥感
  Future<void> remoteSensing(bool isOpen) async {
    await _methodChannel!.invokeMethod('remoteSensing', isOpen);
  }
}
