import 'dart:convert';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:get/get.dart';
import 'package:lewei_pro/api/lw_api.dart';
import 'package:lewei_pro/bean/fly_log_bean.dart';
import 'package:lewei_pro/widget/gesture_tap.dart';
import 'package:lewei_pro/widget/public_dialog.dart';

class FlyLog extends StatefulWidget {
  final int id;
  final String date;

  const FlyLog({Key? key, required this.id, required this.date}) : super(key: key);

  @override
  _FlyLogState createState() => _FlyLogState();
}

class _FlyLogState extends State<FlyLog> {
  List<FlyLogBean> flyLogs = [];
  bool loadComplete = false;
  ScrollController controllerTime = ScrollController();
  ScrollController controller = ScrollController();

  bool isTimeScroll = false;
  bool isContentScroll = false;
  bool isEdit = false;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black,
      body: SafeArea(
          child: Column(
        children: [
          Container(
            color: const Color(0xff283138),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                IconButton(onPressed: () => Get.back(), icon: const Icon(Icons.keyboard_return, color: Colors.white)),
                Text('FlightLog${widget.date}', style: const TextStyle(color: Colors.white)),
                Row(
                  mainAxisAlignment: MainAxisAlignment.end,
                  children: [
                    GestureDetector(
                      onTap: () {
                        isEdit = !isEdit;
                        setState(() => flyLogs.forEach((element) => element.isSelect = isEdit));
                      },
                      child: const Text('全选', style: TextStyle(color: Colors.white)),
                    ),
                    const SizedBox(width: 40),
                    IconButton(
                        onPressed: () {
                          if (!isEdit) {
                            setState(() => isEdit = true);
                          } else {
                            StringBuffer times = StringBuffer();
                            List<FlyLogBean> res = flyLogs.where((element) {
                              if (element.isSelect) times.write(',${element.heightTime}');
                              return element.isSelect;
                            }).toList();

                            if (res.isEmpty) {
                              setState(() => isEdit = false);
                            } else {
                              PublicDialog.showBottomAlertDialog(context, '确定删除？').then((value) {
                                if (value == '确定') {
                                  ///删除是异步操作，可以加个loading
                                  LwApi.getInstance().deleteHeightData(times.toString().replaceFirst(',', ''));

                                  flyLogs.removeWhere((element) => element.isSelect);
                                  isEdit = false;
                                  setState(() {});
                                }
                              });
                            }
                          }
                        },
                        icon: const Icon(Icons.delete_outline, color: Colors.white)),
                  ],
                )
              ],
            ),
          ),
          Expanded(
            child: flyLogs.isEmpty && !loadComplete
                ? const Center(
                    child: SizedBox(
                      width: 30,
                      height: 30,
                      child: CircularProgressIndicator(color: Colors.grey),
                    ),
                  )
                : Row(
                    children: [
                      SizedBox(
                        width: 150,
                        child: Column(
                          mainAxisSize: MainAxisSize.max,
                          children: [
                            const Text('时间', style: TextStyle(color: Colors.white)),
                            Expanded(
                                child: NotificationListener(
                              onNotification: (Notification notification) {
                                if (notification is ScrollStartNotification) {
                                  isTimeScroll = true;
                                }
                                if (notification is ScrollEndNotification) {
                                  isTimeScroll = false;
                                }

                                if (!isContentScroll) controller.jumpTo(controllerTime.offset);
                                return true;
                              },
                              child: ListView.builder(
                                itemCount: flyLogs.length,
                                controller: controllerTime,
                                itemBuilder: (context, index) {
                                  return GestureDetector(
                                    child: Container(
                                      color: Colors.transparent,
                                      padding: const EdgeInsets.symmetric(vertical: 10),
                                      child: Row(
                                        mainAxisAlignment: MainAxisAlignment.center,
                                        children: [
                                          Visibility(
                                            visible: isEdit,
                                            child: Padding(
                                              child: Icon(flyLogs[index].isSelect ? Icons.radio_button_checked : Icons.radio_button_unchecked, color: Colors.blue, size: 16),
                                              padding: const EdgeInsets.only(right: 20),
                                            ),
                                          ),
                                          Text(flyLogs[index].heightTime, style: const TextStyle(color: Colors.white)),
                                        ],
                                      ),
                                    ),
                                    onTap: () => itemClick(index),
                                  );
                                },
                              ),
                            )),
                          ],
                        ),
                      ),
                      Expanded(
                        child: SingleChildScrollView(
                            scrollDirection: Axis.horizontal,
                            child: SizedBox(
                              width: 100 * 13,
                              child: Column(
                                mainAxisSize: MainAxisSize.max,
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  itemWidget('模式', '状态', '电量', '升降速', '速度', '高度', '距离', '仰角', '偏角', '星数', '精度', '纬度', '经度'),
                                  Expanded(
                                      child: NotificationListener(
                                    onNotification: (Notification notification) {
                                      if (notification is ScrollStartNotification) {
                                        isContentScroll = true;
                                      }
                                      if (notification is ScrollEndNotification) {
                                        print('停止滚动');
                                        isContentScroll = false;
                                      }

                                      if (!isTimeScroll) controllerTime.jumpTo(controller.offset);
                                      return true;
                                    },
                                    child: ListView.builder(
                                      itemCount: flyLogs.length,
                                      controller: controller,
                                      // shrinkWrap: true,
                                      // physics: NeverScrollableScrollPhysics(),
                                      itemBuilder: (context, index) {
                                        return itemWidget(
                                          flyLogs[index].flyMode.toString(),
                                          flyLogs[index].flyState.toString(),
                                          flyLogs[index].flyBattery.toString(),
                                          flyLogs[index].liftingSpeed.toString(),
                                          flyLogs[index].speed.toString(),
                                          flyLogs[index].height.toString(),
                                          flyLogs[index].distance.toString(),
                                          flyLogs[index].elevation.toString(),
                                          flyLogs[index].deflection.toString(),
                                          flyLogs[index].starNumber.toString(),
                                          flyLogs[index].accuracy.toString(),
                                          flyLogs[index].airLatitude.toString(),
                                          flyLogs[index].airLongitude.toString(),
                                          padding: const EdgeInsets.symmetric(vertical: 10),
                                          index: index,
                                        );
                                      },
                                    ),
                                  )),
                                ],
                              ),
                            )),
                      )
                    ],
                  ),
          )
        ],
      )),
    );
  }

  void itemClick(int index) {
    if (isEdit && index >= 0) {
      flyLogs[index].isSelect = !flyLogs[index].isSelect;
      setState(() {});
    }
  }

  late double elevation;
  late double deflection;
  late int starNumber;
  late double accuracy;

  late double airLatitude; // 飞机坐标
  late double airLongitude; // 飞机坐标

  Widget itemWidget(
    String mode,
    String state,
    String battery,
    String liftingSpeed,
    String speed,
    String height,
    String distance,
    String elevation,
    String deflection,
    String starNumber,
    String accuracy,
    String airLatitude,
    String airLongitude, {
    EdgeInsetsGeometry padding = EdgeInsets.zero,
    int index = -1,
  }) {
    return GestureTap(
        onTap: () => itemClick(index),
        child: Padding(
          padding: padding,
          child: Row(
            children: [
              Container(width: 100, alignment: Alignment.center, child: Text(mode, style: const TextStyle(color: Colors.white))),
              Container(width: 100, alignment: Alignment.center, child: Text(state, style: const TextStyle(color: Colors.white))),
              Container(width: 100, alignment: Alignment.center, child: Text(battery, style: const TextStyle(color: Colors.white))),
              Container(width: 100, alignment: Alignment.center, child: Text(liftingSpeed, style: const TextStyle(color: Colors.white))),
              Container(width: 100, alignment: Alignment.center, child: Text(speed, style: const TextStyle(color: Colors.white))),
              Container(width: 100, alignment: Alignment.center, child: Text(height, style: const TextStyle(color: Colors.white))),
              Container(width: 100, alignment: Alignment.center, child: Text(distance, style: const TextStyle(color: Colors.white))),
              Container(width: 100, alignment: Alignment.center, child: Text(elevation, style: const TextStyle(color: Colors.white))),
              Container(width: 100, alignment: Alignment.center, child: Text(deflection, style: const TextStyle(color: Colors.white))),
              Container(width: 100, alignment: Alignment.center, child: Text(starNumber, style: const TextStyle(color: Colors.white))),
              Container(width: 100, alignment: Alignment.center, child: Text(accuracy, style: const TextStyle(color: Colors.white))),
              Container(width: 100, alignment: Alignment.center, child: Text(airLatitude, style: const TextStyle(color: Colors.white))),
              Container(width: 100, alignment: Alignment.center, child: Text(airLongitude, style: const TextStyle(color: Colors.white)))
            ],
          ),
        ));
  }

  @override
  void initState() {
    super.initState();
    initData();
  }

  initData() async {
    String logs = await LwApi.getInstance().queryHeightData(widget.id);

    if (logs != 'error') {
      flyLogs.clear();
      List<dynamic> list = jsonDecode(logs);
      for (var element in list) {
        flyLogs.add(FlyLogBean.fromJson(element));
      }
    }
    setState(() => loadComplete = true);
  }

  @override
  void dispose() {
    super.dispose();

    controllerTime.dispose();
    controller.dispose();
  }
}
