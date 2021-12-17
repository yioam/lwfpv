import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:lewei_pro/api/lw_api.dart';
import 'package:lewei_pro/bean/lw_uart_protol_bean.dart';

class FlyController extends GetxController {
  static FlyController? _controlModel;

  static FlyController getInstance() {
    return _controlModel ??= FlyController._();
  }

  bool hasPermission = false;

  FlyController._();

  int wifiLevel = 0;
  int _remoteControlMode = 1; //1日本手 0美国手
  bool _gravityMode = false; //重力模式
  bool _nedGPSGood = true; //GPS信号良好才能起飞
  bool _highSpeed = true; //高速/低速
  //校准加速器
  //校准地图

  int defaultHeight = 20; //默认航点高度(5-50m)
  int maxHeight = 50; //最大航点高度
  int speed = 1; //航点速度（1-6m/s)
  int time = 0; //航点停留时间0-60s
  int radius = 50; //航点最大半径0-50m
  int minHeight = 10; //返航最低高度10-30m

  bool mapCenter = true; //飞机显示地图中心
  bool autoCorrect = true; //坐标自动纠正
  int mapType = 1; //地图类型 1标准 2卫星 3夜视

  FlyMode _flyMode = FlyMode.stableMode;

  /// 1 航点画轨迹 2删除 0默认
  int _waypointModeType = 0;

  ///控制地图上的透明控件的显隐，用来绘制标注点
  bool isDrawMarkers = false;

  ///航点模式下0 连续添加marker  1一个一个添加     3默认
  ///环绕模式下4 添加单个marker  5点击marker删除
  int _drawType = 3;

  bool _isVrView = false;


  bool isRecording = false;
  int _recordingSeconds = 0;
  ValueNotifier<String> recordingTimeValue = ValueNotifier('');
  ValueNotifier<List<String>> leftRemoteSensingValue = ValueNotifier(['50%', '0%']);
  ValueNotifier<List<String>> rightRemoteSensingValue = ValueNotifier(['0%', '0%']);

  //重力加速器控制遥感小圆位置
  ValueNotifier<List<int>> leftBallPointValue = ValueNotifier([1500, 1500]);
  ValueNotifier<List<int>> rightBallPointValue = ValueNotifier([1500, 1500]);


  ValueNotifier<bool> visibleValue = ValueNotifier(false);

  int get recordingSeconds => _recordingSeconds;

  set recordingSeconds(int value) {
    _recordingSeconds = value;
    int m = value ~/ 60;
    int s = value % 60;
    String mString = m < 10 ? '0$m' : '$m';
    String sString = s < 10 ? '0$s' : '$s';
    recordingTimeValue.value = '$mString:$sString';
  }

  bool get nedGPSGood => _nedGPSGood;

  set nedGPSGood(bool value) {
    _nedGPSGood = value;
    LwApi.getInstance().isGPSLocationSuccess(value);
  }

  int get remoteControlMode => _remoteControlMode;

  set remoteControlMode(int value) {
    LwApi.getInstance().setRightHandMode(value == 0);
    _remoteControlMode = value;
  }

  bool get gravityMode => _gravityMode;

  set gravityMode(bool value) {
    LwApi.getInstance().setSensorOn(value);
    _gravityMode = value;
  }

  bool get highSpeed => _highSpeed;

  set highSpeed(bool value) {
    LwApi.getInstance().setSpeed(value ? 1 : 0);
    _highSpeed = value;
  }

  FlyMode get flyMode => _flyMode;

  set flyMode(FlyMode value) {
    _flyMode = value;
  }

  int get waypointModeType => _waypointModeType;

  set waypointModeType(int value) {
    _waypointModeType = value;
    isDrawMarkers = false;
    _drawType = 3;
  }

  int get drawType => _drawType;

  set drawType(int value) {
    _drawType = value;
    isDrawMarkers = _drawType == 0;
    _waypointModeType = 0;
  }


  bool get isVrView => _isVrView;

  set isVrView(bool value) {
    _isVrView = value;
    if(value){
      LwApi.getInstance().setVRView();
    }else{
      LwApi.getInstance().setPlaneView();
    }
  }

  String getTipText(LwUartProtolBean lwUartProtolBean) {
    String flyModeStr = '定高模式';
    String flyStateStr = '已上锁';
    switch (lwUartProtolBean.lwUartProtol?.mFlyInfo?.mFlyMode ?? 0) {
      case 1:
        flyModeStr = '定高模式';
        break;
      case 2:
        flyModeStr = '定点模式';
        break;

      case 3:
        flyModeStr = '返航模式';
        break;
      case 4:
        flyModeStr = '起飞模式';
        break;
      case 5:
        flyModeStr = '降落模式';
        break;
      case 6:
        flyModeStr = '航点模式';
        break;
      case 7:
        flyModeStr = '跟随模式';
        break;
      case 8:
        flyModeStr = '环绕模式';
        break;
      case 9:
        flyModeStr = '自稳模式';
        break;
    }

    switch (lwUartProtolBean.lwUartProtol?.mFlyInfo?.mFlySate ?? 0) {
      case 1:
        flyStateStr = '已上锁';
        break;
      case 2:
        flyStateStr = '已解锁未起飞';
        break;
      case 3:
        flyStateStr = '已解锁已起飞';
        break;
      case 4:
        flyStateStr = '失控返航';
        break;
      case 5:
        flyStateStr = '一级返航';
        break;
      case 6:
        flyStateStr = '二级返航';
        break;
      case 7:
        flyStateStr = '一键返航';
        break;
      case 8:
        flyStateStr = '低压降落';
        break;
      case 9:
        flyStateStr = '一键降落';
        break;
      case 10:
        flyStateStr = '一键起飞';
        break;
    }

    return '$flyModeStr/$flyStateStr';
  }

  void destroy(){
    _gravityMode = false;
    _nedGPSGood = true;
    _remoteControlMode = 1;
  }
}

enum FlyMode {
  stableMode,
  waypointMode,
  followMode,
  surroundMode,
}
