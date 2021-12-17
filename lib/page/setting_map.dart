import 'package:flutter/material.dart';
import 'package:lewei_pro/base/base_state.dart';
import 'package:get/get.dart';
import 'package:lewei_pro/contoller/fly_control.dart';

///参数
class SettingMap extends StatefulWidget {
  const SettingMap({Key? key}) : super(key: key);

  @override
  _SettingMapState createState() => _SettingMapState();
}

class _SettingMapState extends BaseState<SettingMap> {
  final FlyController controlModel = Get.find();



  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      child: Column(
        children: [

          settingItem(
              '飞机显示地图中心',
              Row(
                children: [
                  checkBox(controlModel.mapCenter, '是', () => setState(() => controlModel.mapCenter = true)),
                  checkBox(controlModel.mapCenter, '否', () => setState(() => controlModel.mapCenter = false)),
                ],
              )),

          settingItem(
              '坐标自动纠正（中国大陆地区使用）',
              Row(
                children: [
                  checkBox(controlModel.autoCorrect, '打开', () => setState(() => controlModel.autoCorrect = true)),
                  checkBox(!controlModel.autoCorrect, '关闭', () => setState(() => controlModel.autoCorrect = false)),
                ],
              )),

          settingItem(
              '地图类型',
              Row(
                children: [
                  checkBox(controlModel.mapType == 1, '标准', () => setState(() => controlModel.mapType = 1), topWidget: Image.asset('images/map_standard.png', width: 50)),
                  checkBox(controlModel.mapType == 2, '卫星', () => setState(() => controlModel.mapType = 2), topWidget: Image.asset('images/map_satellite.png', width: 50)),
                  checkBox(controlModel.mapType ==3, '夜视', () => setState(() => controlModel.mapType = 3), topWidget: Image.asset('images/map_hybrid.png', width: 50)),
                ],
              )),
        ],
      ),
    );
  }

  Widget checkBox(bool isCheck, String text, GestureTapCallback onTap, {Widget? topWidget}) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        color: Colors.transparent,
        width: (MediaQuery.of(context).size.width - 300) / 4,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            topWidget??Container(),
            Row(
              children: [
                Icon(isCheck ? Icons.radio_button_checked : Icons.radio_button_unchecked, color: Colors.white),
                Text(text, style: const TextStyle(color: Colors.white)),
              ],
            ),
          ],
        )
      ),
    );
  }
}
