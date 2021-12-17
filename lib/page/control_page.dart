import 'dart:async';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/painting.dart';
import 'package:flutter/services.dart';
import 'package:lewei_pro/api/lw_api.dart';
import 'package:lewei_pro/bean/lw_uart_protol_bean.dart';
import 'package:lewei_pro/contoller/fly_control.dart';
import 'package:lewei_pro/main.dart';
import 'package:lewei_pro/page/map/g_map.dart';
import 'package:lewei_pro/page/map/gaode_map.dart';
import 'package:lewei_pro/page/photo/view.dart';
import 'package:lewei_pro/page/setting_remote_control.dart';
import 'package:lewei_pro/page/setting_map.dart';
import 'package:lewei_pro/page/setting_other.dart';
import 'package:lewei_pro/page/setting_parameter.dart';
import 'package:lewei_pro/res/text_style.dart';
import 'package:lewei_pro/util/flutter_screenutil.dart';
import 'package:lewei_pro/widget/battery_widget.dart';
import 'package:lewei_pro/widget/gesture_tap.dart';
import 'package:lewei_pro/widget/remote_sensing_widget.dart';
import 'package:lewei_pro/widget/surface_view.dart';
import 'package:lewei_pro/widget/take_photo_anim.dart';
import 'package:lewei_pro/widget/top_error_tip_widget.dart';
import 'package:orientation/orientation.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:get/get.dart';
import 'package:amap_flutter_location/amap_location_option.dart';
import 'package:amap_flutter_location/amap_flutter_location.dart';

class ControlPage extends StatefulWidget {
  const ControlPage({Key? key}) : super(key: key);

  @override
  _ControlPageState createState() => _ControlPageState();
}

class _ControlPageState extends State<ControlPage> with WidgetsBindingObserver, SingleTickerProviderStateMixin {
  Timer? timer;

  ///true 展示左上角飞行模式按钮
  bool flightVisible = false;

  ///ture 展示遥感组件
  bool remoteSensing = false;

  ///true surfaceview大图  false 地图大图
  bool showBg = true;

  /// 高德地图 or Google地图
  bool isShowGaode = true;

  GlobalKey<GaodeMapState> gaodeKey = GlobalKey();
  GlobalKey<GMapState> googleKey = GlobalKey();

  GlobalKey<TakePhotoAnimState> takePhotoKey = GlobalKey();

  final AMapFlutterLocation _aMapFlutterLocation = AMapFlutterLocation();
  StreamSubscription<Map<String, Object>>? _locationListener;

  final FlyController model = Get.find();
  final LwUartProtolBean lwUartProtolBean = Get.find();

  Widget mapWidget() {
    return Stack(
      children: [
        ValueListenableBuilder(
            valueListenable: model.visibleValue,
            builder: (BuildContext context, bool value, Widget? child) {
              return Offstage(
                offstage: !value, //直接加载会导致页面跳转看起来不流畅
                child: isShowGaode ? GaodeMap(key: gaodeKey) : GMap(key: googleKey),
              );
            }),
        Visibility( 
          child: GestureDetector(
              onTap: () {
                print('123');
              },
              onTapDown: (TapDownDetails d) {},
              onPanStart: (DragStartDetails d) {
                gaodeKey.currentState?.onPanStart((ScreenUtil.pixelRatio * d.localPosition.dx).toInt(), (ScreenUtil.pixelRatio * d.localPosition.dy).toInt());
                googleKey.currentState?.onPanStart((ScreenUtil.pixelRatio * d.localPosition.dx).toInt(), (ScreenUtil.pixelRatio * d.localPosition.dy).toInt());
              },
              onPanUpdate: (DragUpdateDetails d) {
                gaodeKey.currentState?.onPanUpdate((ScreenUtil.pixelRatio * d.localPosition.dx).toInt(), (ScreenUtil.pixelRatio * d.localPosition.dy).toInt());
                googleKey.currentState?.onPanUpdate((ScreenUtil.pixelRatio * d.localPosition.dx).toInt(), (ScreenUtil.pixelRatio * d.localPosition.dy).toInt());
              },
              onPanEnd: (DragEndDetails d) {
                if (model.drawType != 0) return;
                model.drawType = 3;
                setState(() {});
              },
              child: Container(color: Colors.black12)),
          visible: model.isDrawMarkers,
        )
      ],
    );
  }

  // bool visible = false;

  Widget mainBg(Map map) {
    return Stack(
      children: [
        Image.asset('images/main_bg.png', fit: BoxFit.fill, width: MediaQuery.of(context).size.width, gaplessPlayback: true),
        ValueListenableBuilder(
            valueListenable: model.visibleValue,
            builder: (BuildContext context, bool value, Widget? child) {
              return Offstage(
                offstage: !value,
                child: SurfaceView(
                  map: map,
                  // key: surfaceKey,
                ),
              );
            }),
      ],
    );
  }

  ///顶部wifi图标
  Widget getWifiImage() {
    String img;
    if (model.wifiLevel >= -50) {
      img = 'images/btn_wifi.png';
    } else if (model.wifiLevel >= -70) {
      img = 'images/btn_wifi4.png';
    } else if (model.wifiLevel >= -80) {
      img = 'images/btn_wifi3.png';
    } else if (model.wifiLevel >= -100) {
      img = 'images/btn_wifi2.png';
    } else {
      img = 'images/btn_wifi1.png';
    }
    return Image.asset(img, width: 50);
  }

  ///航点模式和环绕模式时的地图操作
  Widget mapAction() {
    if (model.flyMode == FlyMode.waypointMode) {
      return Column(
        mainAxisAlignment: MainAxisAlignment.center,
        crossAxisAlignment: CrossAxisAlignment.end,
        children: [
          Container(width: 50, padding: const EdgeInsets.only(top: 5, bottom: 5), color: Colors.grey, alignment: Alignment.center, child: Text('航点', style: textStyle14w)),
          Row(
            children: [
              Visibility(visible: model.waypointModeType == 1, child: actionItemWidget('images/map_draw_l.png', () => setState(() => model.drawType = 0))),
              Visibility(visible: model.waypointModeType == 1, child: actionItemWidget('images/map_draw_p.png', () => setState(() => model.drawType = 1))),
              actionItemWidget(
                  'images/map_draw.png',
                  () => setState(() {
                        gaodeKey.currentState?.mapController?.setMarkerClickToDel(false);
                        googleKey.currentState?.mapController?.setMarkerClickToDel(false);
                        model.waypointModeType = 1;
                      })),
            ],
          ),
          Row(
            children: [
              Visibility(
                  visible: model.waypointModeType == 2,
                  child: actionItemWidget('images/map_delete_m.png', () {
                    ///删除所有marker
                    gaodeKey.currentState?.clearMarker();
                    googleKey.currentState?.clearMarker();
                  })),
              Visibility(
                  visible: model.waypointModeType == 2,
                  child: actionItemWidget('images/map_delete_p.png', () {
                    ///设置点击marker时直接删除
                    gaodeKey.currentState?.mapController?.setMarkerClickToDel(true);
                    googleKey.currentState?.mapController?.setMarkerClickToDel(true);
                  })),
              actionItemWidget(
                  'images/map_delete.png',
                  () => setState(() {
                        gaodeKey.currentState?.mapController?.setMarkerClickToDel(true);
                        googleKey.currentState?.mapController?.setMarkerClickToDel(true);
                        model.waypointModeType = 2;
                      })),
            ],
          ),
          actionItemWidget('images/map_start.png', () {
            gaodeKey.currentState?.setTrackRouteControlData();
            googleKey.currentState?.setTrackRouteControlData();
          }),
        ],
      );
    }
    return Column(
      mainAxisAlignment: MainAxisAlignment.center,
      crossAxisAlignment: CrossAxisAlignment.end,
      children: [
        Container(width: 50, padding: const EdgeInsets.only(top: 5, bottom: 5), color: Colors.grey, alignment: Alignment.center, child: Text('环绕', style: textStyle14w)),
        actionItemWidget('images/map_point.png', () {
          gaodeKey.currentState?.mapController?.setMarkerClickToDel(false);
          googleKey.currentState?.mapController?.setMarkerClickToDel(false);
          model.drawType = 4;
        }),
        actionItemWidget('images/map_delete_p.png', () {
          model.drawType = 5;
          gaodeKey.currentState?.mapController?.setMarkerClickToDel(true);
          googleKey.currentState?.mapController?.setMarkerClickToDel(true);
        }),
        actionItemWidget('images/map_start.png', () {
          gaodeKey.currentState?.setFlyCircleControlData();
          googleKey.currentState?.setFlyCircleControlData();
        }),
      ],
    );
  }

  Widget actionItemWidget(String img, Function onTap) {
    return GestureTap(
        child: Column(
          children: [
            Container(
              color: Colors.black87,
              width: 50,
              // padding: const EdgeInsets.all(15),
              child: Image.asset(img),
            ),
            // Container(width: 40, height: 1, color: Colors.white)
          ],
        ),
        onTap: onTap);
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
        child: Scaffold(
          resizeToAvoidBottomInset: false,
          body: Stack(
            children: [
              SizedBox(
                width: MediaQuery.of(context).size.width,
                height: MediaQuery.of(context).size.height,
                child: mainBg({'id': 0, 'copyBuffer': showBg}),
              ),

              SizedBox(
                width: MediaQuery.of(context).size.width,
                height: MediaQuery.of(context).size.height,
                child: Visibility(
                  child: mapWidget(),
                  visible: !showBg,
                ),
              ),

              GetBuilder<LwUartProtolBean>(builder: (model) {
                return Container(
                  height: 50,
                  color: const Color(0x55000000),
                  child: Row(
                    // mainAxisAlignment: MainAxisAlignment.spaceAround,
                    children: [
                      Container(
                        child: customButton(Image.asset('images/menu_home.png'), () {
                          FlyController.getInstance().visibleValue.value = false;
                          OrientationPlugin.forceOrientation(DeviceOrientation.portraitUp);
                          Get.back();
                        }),
                        margin: const EdgeInsets.only(left: 20, right: 20),
                      ),
                      Expanded(
                          child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                        children: [
                          Text("高度:${model.lwUartProtol?.mFlyInfo?.height ?? 0.0}m", style: textStyle10w),
                          Text('距离:${model.lwUartProtol?.mFlyInfo?.distant ?? 0.0}m', style: textStyle10w),
                        ],
                      )),
                      Expanded(
                          child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                        children: [
                          Text('垂直速度:${model.lwUartProtol?.mFlyInfo?.velocity ?? 0.0}m/s', style: textStyle10w),
                          Text('水平速度:${model.lwUartProtol?.mFlyInfo?.speed ?? 0.0}m/s', style: textStyle10w),
                        ],
                      )),
                      Expanded(
                          child: Row(
                        children: [
                          Column(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                              Image.asset('images/top_satellite_h.png'),
                              Text('${model.lwUartProtol?.mFlyInfo?.gpsNum ?? 0}N/S', style: textStyle10w),
                            ],
                          ),
                          const SizedBox(width: 10),
                          Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                            children: [
                              Text('维度:${model.lwUartProtol?.mFlyInfo?.coordinate?.latitude ?? 0.0}', style: textStyle10w),
                              Text('经度${model.lwUartProtol?.mFlyInfo?.coordinate?.longitude ?? 0.0}', style: textStyle10w),
                            ],
                          ),
                        ],
                      )),
                      Expanded(
                          child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                        children: [
                          Text('横滚角:${model.lwUartProtol?.mFlyInfo?.attitude?.roll ?? 0.0}', style: textStyle10w),
                          Text('俯仰角:${model.lwUartProtol?.mFlyInfo?.attitude?.pitch ?? 0.0}', style: textStyle10w),
                          Text('偏航角:${model.lwUartProtol?.mFlyInfo?.attitude?.yaw ?? 0.0}', style: textStyle10w),
                        ],
                      )),
                      Expanded(
                        child: Row(children: [
                          Column(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                              Text('${model.lwUartProtol?.mFlyInfo?.batVal ?? 0}%', style: textStyle14w),

                              ///电池图标
                              CustomPaint(
                                size: const Size(30, 10),
                                painter: BatteryPaint(model.lwUartProtol?.mFlyInfo?.batVal ?? 0.0, (model.lwUartProtol?.mFlyInfo?.batVal ?? 0.0) > 20 ? Colors.lightGreenAccent : Colors.red),
                              ),
                            ],
                          ),
                          const SizedBox(width: 10),
                          getWifiImage(),
                        ]),
                      ),
                      GestureDetector(
                          onTap: showSettingDialog,
                          child: Container(
                            padding: const EdgeInsets.all(5),
                            color: Colors.transparent,
                            child: Image.asset('images/setting.png', width: 20),
                          )),
                    ],
                  ),
                );
              }),

              ///’定高模式‘提示
              GetBuilder<LwUartProtolBean>(builder: (v) {
                return Align(
                  alignment: Alignment.topCenter,
                  child: Container(
                    margin: const EdgeInsets.only(top: 52),
                    padding: const EdgeInsets.all(4),
                    color: Colors.black,
                    child: Text(model.getTipText(lwUartProtolBean), style: textStyle10w),
                  ),
                );
              }),

              ///遥感提示数据
              Positioned(
                top: 52,
                right: 140,
                child: Visibility(
                    visible: remoteSensing,
                    child: Container(
                      padding: const EdgeInsets.all(1),
                      decoration: BoxDecoration(color: Colors.white, borderRadius: BorderRadius.circular(5)),
                      child: Container(
                        decoration: BoxDecoration(color: Colors.black, borderRadius: BorderRadius.circular(5)),
                        padding: const EdgeInsets.all(2),
                        width: 100,
                        height: 50,
                        child: Row(
                          children: [
                            Expanded(
                                child: ValueListenableBuilder<List<String>>(
                              valueListenable: model.leftRemoteSensingValue,
                              builder: (BuildContext context, List<String> value, Widget? child) {
                                return Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  mainAxisAlignment: MainAxisAlignment.spaceAround,
                                  children: [
                                    Text('T:${value[0]}', style: textStyle12w),
                                    Text('E:${value[1]}', style: textStyle12w),
                                  ],
                                );
                              },
                            )),
                            Expanded(
                                child: ValueListenableBuilder<List<String>>(
                              valueListenable: model.rightRemoteSensingValue,
                              builder: (BuildContext context, List<String> value, Widget? child) {
                                return Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  mainAxisAlignment: MainAxisAlignment.spaceAround,
                                  children: [
                                    Text('E:${value[0]}', style: textStyle12w),
                                    Text('A:${value[1]}', style: textStyle12w),
                                  ],
                                );
                              },
                            )),
                          ],
                        ),
                      ),
                    )),
              ),

              ///校准加速器提示文字
              Align(
                alignment: Alignment.topCenter,
                child: Container(
                  margin: const EdgeInsets.only(top: 80),
                  child: const TopErrorTipWidget(),
                ),
              ),

              ///左边五个按钮
              Positioned(
                  left: 10,
                  top: 50,
                  bottom: 0,
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.spaceAround,
                    children: [
                      customButton(Image.asset('images/top_flightmode.png', width: 40), () {
                        setState(() {
                          flightVisible = !flightVisible;
                        });
                      }),
                      customButton(Image.asset(remoteSensing ? 'images/off_icon.png' : 'images/on_icon.png', width: 40), () {
                        setState(() {
                          remoteSensing = !remoteSensing;
                          LwApi.getInstance().remoteSensing(remoteSensing);
                        });
                      }),
                      customButton(Image.asset('images/fly_return.png', width: 40), LwApi.getInstance().flyDown),
                      customButton(Image.asset('images/top_unlock.png', width: 40), unlock),
                      customButton(Image.asset('images/fly_up.png', width: 40), LwApi.getInstance().flyUp),
                    ],
                  )),

              ///右上角小图
              Positioned(
                top: 52,
                right: 10,
                child: SizedBox(
                    width: 110,
                    height: 80,
                    child: mainBg({
                      'id': 1,
                      'copyBuffer': !showBg,
                    })),
              ),
              Positioned(
                top: 52,
                right: 10,
                child: SizedBox(
                    width: 110,
                    height: 80,
                    child: Visibility(
                      child: mapWidget(),
                      visible: showBg,
                    )),
              ),

              ///那地图手势拦截不了，so盖个透明视图在上面
              Positioned(
                top: 60,
                right: 10,
                child: GestureDetector(
                  onTap: () {
                    setState(() {
                      showBg = !showBg;
                    });
                    surfaceViewMap[showBg ? 0 : 1]?.invokeMethod('copyBuffer');
                  },
                  child: Container(
                    width: 100,
                    height: 60,
                    color: Colors.transparent,
                  ),
                ),
              ),

              ///右边4个按钮
              Positioned(
                  right: 0,
                  top: 50,
                  bottom: 0,
                  child: model.flyMode == FlyMode.waypointMode || model.flyMode == FlyMode.surroundMode
                      ? mapAction()
                      : Padding(
                          padding: const EdgeInsets.only(right: 10),
                          child: Column(
                            mainAxisAlignment: MainAxisAlignment.spaceAround,
                            crossAxisAlignment: CrossAxisAlignment.end,
                            children: [
                              const SizedBox(width: 40, height: 40),

                              ///vr
                              customButton(Image.asset('images/splitscreen_hd1.png', width: 40), () {
                                model.isVrView = !model.isVrView;
                              }),

                              ///录像
                              ValueListenableBuilder<String>(
                                valueListenable: model.recordingTimeValue,
                                builder: (BuildContext context, String value, Widget? child) {
                                  return Row(
                                    mainAxisAlignment: MainAxisAlignment.end,
                                    children: [
                                      Visibility(visible: model.isRecording, child: Text(value, style: const TextStyle(color: Colors.white))),
                                      customButton(Image.asset(!model.isRecording ? 'images/videodis.png' : 'images/videoen.png', width: 40), () async {
                                        if (!await Permission.storage.request().isGranted) {
                                          Fluttertoast.showToast(msg: '无存储权限~');
                                          return;
                                        }
                                        bool value = await LwApi.getInstance().takeRec();
                                        if (!value) return;
                                        model.isRecording = !model.isRecording;
                                        if (model.isRecording) {
                                          model.recordingSeconds = 0;

                                          timer = Timer.periodic(const Duration(milliseconds: 1000), (timer) {
                                            model.recordingSeconds++;
                                          });
                                        } else {
                                          model.recordingTimeValue.notifyListeners();
                                          timer?.cancel();
                                        }
                                      }),
                                    ],
                                  );
                                },
                              ),

                              ///拍照
                              customButton(Image.asset('images/main_sdcard_capture.png', width: 40), () async {
                                if (!await Permission.storage.request().isGranted) {
                                  Fluttertoast.showToast(msg: '无存储权限~');
                                  return;
                                }
                                LwApi.getInstance().takePhoto();
                                takePhotoKey.currentState!.showAnimal();
                              }),

                              ///图片
                              customButton(Image.asset('images/top_gallery.png', width: 40), () {
                                Get.to(PhotoPage());
                              }),
                            ],
                          ),
                        )),

              ///两遥感
              Align(
                  alignment: Alignment.bottomCenter,
                  child: Visibility(
                    visible: remoteSensing,
                    child: Container(
                      padding: const EdgeInsets.only(bottom: 50),
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          ValueListenableBuilder(
                              valueListenable: model.leftBallPointValue,
                              builder: (BuildContext context, List<int> value, Widget? child) {
                                bool needGesture = model.remoteControlMode == 1 || !model.gravityMode;

                                return SizedBox(
                                    width: 200,
                                    height: 200,
                                    child: RemoteSensingWidget(
                                      (double proportionX, double proportionY) {
                                        model.leftRemoteSensingValue.value = ['${50 - (proportionY * 50).toInt()}%', '${(proportionX * 50).toInt()}%'];

                                        LwApi.getInstance().setRudderData((500 * proportionX).toInt(), (500 * proportionY).toInt());
                                      },
                                      x: needGesture ? 0 : value[0] - 1500,
                                      y: needGesture ? 0 : 1500 - value[1],
                                      needGesture: needGesture,
                                    ));
                              }),
                          const SizedBox(width: 100),
                          ValueListenableBuilder(
                            valueListenable: model.rightBallPointValue,
                            builder: (BuildContext context, List<int> value, Widget? child) {
                              bool needGesture = model.remoteControlMode != 1 || !model.gravityMode;

                              return SizedBox(
                                  width: 200,
                                  height: 200,
                                  child: RemoteSensingWidget(
                                    (double proportionX, double proportionY) {
                                      model.rightRemoteSensingValue.value = ['${-(proportionY * 50).toInt()}%', '${(proportionX * 50).toInt()}%'];

                                      LwApi.getInstance().setPowerData((500 * proportionX).toInt(), (500 * proportionY).toInt());
                                    },
                                    x: needGesture ? 0 : value[0] - 1500,
                                    y: needGesture ? 0 : 1500 - value[1],
                                    needGesture: needGesture,
                                  ));
                            },
                          ),
                        ],
                      ),
                    ),
                  )),

              ///地图画轨迹相关按钮
              Positioned(
                top: 55,
                left: 70,
                child: Visibility(
                  visible: flightVisible,
                  child: Container(
                    color: const Color(0x77000000),
                    child: Row(
                      children: [
                        customButton(Image.asset('images/top_virtulrocker.png', width: 60), () => setFlyMode(FlyMode.stableMode)),
                        customButton(Image.asset(model.flyMode == FlyMode.waypointMode ? 'images/top_track_h.png' : 'images/top_track.png', width: 60), () => setFlyMode(FlyMode.waypointMode)),
                        customButton(Image.asset(model.flyMode == FlyMode.followMode ? 'images/top_follow_h.png' : 'images/top_follow.png', width: 60), () => setFlyMode(FlyMode.followMode)),
                        customButton(Image.asset(model.flyMode == FlyMode.surroundMode ? 'images/top_circle_h.png' : 'images/top_circle.png', width: 60), () => setFlyMode(FlyMode.surroundMode)),
                      ],
                    ),
                  ),
                ),
              ),

              ///拍照动画
              TakePhotoAnim(key: takePhotoKey),
            ],
          ),
        ),
        onWillPop: () {
          model.visibleValue.value = false;
          OrientationPlugin.forceOrientation(DeviceOrientation.portraitUp);
          return Future.value(true);
        });
  }

  String currentActionType = '遥感器';
  StateSetter? _setter;

  Future<void> unlock() async {
    if (await LwApi.getInstance().canUnlock()) {
      showDialog(
        context: context,
        barrierColor: Colors.transparent,
        builder: (BuildContext context) {
          return GestureDetector(
            onTap: () => Get.back(),
            child: Scaffold(
              backgroundColor: Colors.transparent,
              body: GestureDetector(
                onTap: () {},
                child: Align(
                  child: Container(
                    width: 300,
                    decoration: BoxDecoration(
                      color: Colors.white,
                      borderRadius: BorderRadius.circular(15),
                    ),
                    child: Column(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        const SizedBox(height: 20),
                        Text('警告', style: textStyle14b),
                        const SizedBox(height: 25),
                        Text('准备解锁', style: textStyle12b),
                        const SizedBox(height: 25),
                        Container(height: 1, color: Colors.grey),
                        Row(
                          mainAxisSize: MainAxisSize.min,
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            MaterialButton(
                              onPressed: () => Get.back(),
                              height: 0,
                              padding: EdgeInsets.zero,
                              textColor: Colors.blue,
                              clipBehavior: Clip.antiAlias,
                              child: Container(
                                height: 36,
                                alignment: Alignment.center,
                                width: 149.5,
                                child: Text('否'),
                              ),
                            ),
                            Container(width: 1, height: 36, color: Colors.grey),
                            MaterialButton(
                              padding: EdgeInsets.zero,
                              height: 0,
                              textColor: Colors.blue,
                              onPressed: () {
                                Get.back();
                                LwApi.getInstance().unlock();
                              },
                              child: Container(
                                height: 36,
                                alignment: Alignment.center,
                                width: 149.5,
                                decoration: BoxDecoration(borderRadius: BorderRadius.only(bottomRight: Radius.circular(15))),
                                child: Text('是'),
                              ),
                            ),
                          ],
                        )
                      ],
                    ),
                  ),
                ),
              ),
            ),
          );
        },
      );
    }
  }

  Future showSettingDialog() {
    return Get.dialog(StatefulBuilder(builder: (BuildContext context, setter) {
      _setter = setter;
      return GestureDetector(
        onTap: () => Get.back(),
        child: Scaffold(
          backgroundColor: Colors.transparent,
          body: Align(
            child: GestureDetector(
              onTap: () {},
              child: Container(
                color: const Color(0xaa000000),
                width: MediaQuery.of(context).size.width - 200,
                height: MediaQuery.of(context).size.height - 100,
                child: Column(
                  children: [
                    Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        actionItem('遥感器', 'images/settings_virtul_rocker.png'),
                        actionItem('参数', 'images/settings_param.png'),
                        actionItem('地图', 'images/settings_map.png'),
                        actionItem('其他', 'images/settings_camera.png'),
                      ],
                    ),
                    Expanded(child: settingContent()),
                  ],
                ),
              ),
            ),
          ),
        ),
      );
    }))
      ..then((value) {
        _setter = null;
        OrientationPlugin.setEnabledSystemUIOverlays([]);
        LwApi.getInstance().onSettingDialogClose(model.maxHeight, model.minHeight);
        setState(() {});
      });

    return showDialog(
        context: context,
        barrierColor: Colors.transparent,
        builder: (BuildContext context) {
          return StatefulBuilder(builder: (BuildContext context, setter) {
            _setter = setter;
            return GestureDetector(
              onTap: () {
                Get.back();
              },
              child: Scaffold(
                backgroundColor: Colors.transparent,
                body: Align(
                  child: GestureDetector(
                    onTap: () {},
                    child: Container(
                      color: const Color(0xaa000000),
                      width: MediaQuery.of(context).size.width - 200,
                      height: MediaQuery.of(context).size.height - 100,
                      child: Column(
                        children: [
                          Row(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                              actionItem('遥感器', 'images/settings_virtul_rocker.png'),
                              actionItem('参数', 'images/settings_param.png'),
                              actionItem('地图', 'images/settings_map.png'),
                              actionItem('其他', 'images/settings_camera.png'),
                            ],
                          ),
                          Expanded(child: settingContent()),
                        ],
                      ),
                    ),
                  ),
                ),
              ),
            );
          });
        })
      ..then((value) {
        _setter = null;
        OrientationPlugin.setEnabledSystemUIOverlays([]);
        LwApi.getInstance().onSettingDialogClose(model.maxHeight, model.minHeight);
        setState(() {});
      });
  }

  Widget settingContent() {
    switch (currentActionType) {
      case '遥感器':
        return const RemoteControl();
      case '参数':
        return const SettingParameter();
      case '地图':
        return const SettingMap();
      case '其他':
        return const SettingOther();
    }
    return const RemoteControl();
  }

  Widget actionItem(String text, String img) {
    return GestureDetector(
      onTap: () {
        if (_setter != null) {
          _setter!(() {
            currentActionType = text;
          });
        }
      },
      child: Container(
        width: (MediaQuery.of(context).size.width - 200) / 4,
        padding: const EdgeInsets.symmetric(vertical: 15),
        color: currentActionType == text ? Colors.grey : Colors.greenAccent,
        child: Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Image.asset(img, width: 30),
            Text(text, style: const TextStyle(color: Colors.white)),
          ],
        ),
      ),
    );
  }

  Widget customButton(Widget child, VoidCallback onTap) {
    return GestureDetector(
      onTap: onTap,
      child: child,
    );
  }

  void setFlyMode(FlyMode mode) {
    if (model.flyMode != mode && (model.flyMode == FlyMode.waypointMode || model.flyMode == FlyMode.surroundMode)) {
      gaodeKey.currentState?.clearMarker();
      googleKey.currentState?.clearMarker();
    }

    if (mode == FlyMode.stableMode) {
      LwApi.getInstance().setStableMode();
    } else if (mode == FlyMode.waypointMode) {
      LwApi.getInstance().setWaypointMode();
    } else if (mode == FlyMode.followMode) {
      LwApi.getInstance().setFollowMode();
    } else if (mode == FlyMode.surroundMode) {
      LwApi.getInstance().setSurroundMode();
    }

    showBg = false;
    surfaceViewMap[1]?.invokeMethod('copyBuffer');
    model.flyMode = mode;
    flightVisible = false;
    setState(() {});
  }

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance?.addObserver(this);
    SystemChrome.setEnabledSystemUIOverlays([]);
    // OrientationPlugin.forceOrientation(DeviceOrientation.landscapeRight);
    LwApi.getInstance().onCreate(model.hasPermission);
    Future.delayed(const Duration(milliseconds: 500)).then((value) => model.visibleValue.value = true);

    initLocation();
  }

  initLocation() {
    AMapFlutterLocation().setLocationOption(AMapLocationOption(
      needAddress: false,
    ));
    _aMapFlutterLocation.startLocation();
    _locationListener = _aMapFlutterLocation.onLocationChanged().listen((event) {
      if (event['errorCode'] == null) {
        gaodeKey.currentState?.moveToCurrentLocation(event['latitude'] as double, event['longitude'] as double);
        googleKey.currentState?.moveToCurrentLocation(event['latitude'] as double, event['longitude'] as double);
      } else if (event['errorCode'] == 12) {
        Fluttertoast.showToast(msg: '缺少定位权限');
      } else {}
    });
  }

  @override
  void dispose() {
    super.dispose();
    timer?.cancel();
    model.isRecording = false;

    ///移除定位监听
    _locationListener?.cancel();
    _aMapFlutterLocation.stopLocation();
    _aMapFlutterLocation.destroy();
    WidgetsBinding.instance?.removeObserver(this);
    LwApi.getInstance().onDestroy();
    model.destroy();
  }

  @override
  Future<void> didChangeAppLifecycleState(AppLifecycleState state) async {
    if (state == AppLifecycleState.paused) {
      model.visibleValue.value = false;

      ///退到后台后刷图会有问题。。
      Get.offAll(const MyHomePage());
      OrientationPlugin.forceOrientation(DeviceOrientation.portraitUp);
    } else if (state == AppLifecycleState.resumed) {
      OrientationPlugin.forceOrientation(DeviceOrientation.landscapeRight);
    }
  }
}
