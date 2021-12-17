import 'package:flutter/material.dart';
import 'package:lewei_pro/api/lw_api.dart';
import 'package:lewei_pro/base/base_state.dart';
import 'package:lewei_pro/contoller/fly_control.dart';
import 'package:get/get.dart';

///遥控器
class RemoteControl extends StatefulWidget {
  const RemoteControl({Key? key}) : super(key: key);

  @override
  _RemoteControlState createState() => _RemoteControlState();
}

class _RemoteControlState extends BaseState<RemoteControl> {
  final FlyController controlModel = Get.find();

  @override
  Widget build(BuildContext context) {
    return ListView(
      children: [
        settingItem(
            '遥控模式',
            Row(
              children: [
                checkBox(controlModel.remoteControlMode == 1, '日本手', () => setState(() => controlModel.remoteControlMode = 1)),
                checkBox(controlModel.remoteControlMode == 0, '美国手', () => setState(() => controlModel.remoteControlMode = 0)),
              ],
            )),
        settingItem(
            '重力模式',
            Row(
              children: [
                checkBox(!controlModel.gravityMode, '否', () => setState(() => controlModel.gravityMode = false)),
                checkBox(controlModel.gravityMode, '是', () => setState(() => controlModel.gravityMode = true)),
              ],
            )),
        settingItem(
            'GPS信号良好才能起飞',
            Row(
              children: [
                checkBox(controlModel.nedGPSGood, '是', () => setState(() => controlModel.nedGPSGood = true)),
                checkBox(!controlModel.nedGPSGood, '否', () => setState(() => controlModel.nedGPSGood = false)),
              ],
            )),
        settingItem(
            '速度',
            Row(
              children: [
                checkBox(controlModel.highSpeed, '高速', () => setState(() => controlModel.highSpeed = true)),
                checkBox(!controlModel.highSpeed, '低速', () => setState(() => controlModel.highSpeed = false)),
              ],
            )),
        settingItem('校准加速度计', checkBox(false, '', LwApi.getInstance().accCalibrate)),
        settingItem('校准地磁', checkBox(false, '', LwApi.getInstance().geoCalibrate)),
      ],
    );
  }
}
