import 'package:flutter/material.dart';
import 'package:lewei_pro/api/lw_api.dart';
import 'package:lewei_pro/base/base_state.dart';
import 'package:lewei_pro/contoller/fly_control.dart';
import 'package:get/get.dart';
import 'package:lewei_pro/page/fly_record/fly_record_page.dart';
import 'package:lewei_pro/page/test_page.dart';

///其他
class SettingOther extends StatefulWidget {
  const SettingOther({Key? key}) : super(key: key);

  @override
  _SettingOtherState createState() => _SettingOtherState();
}

class _SettingOtherState extends BaseState<SettingOther> {
  final FlyController model = Get.find();

  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      child: Column(
        children: [
          settingItem(
              '实时预览',
              Row(
                children: [
                  checkBox(true, '480', () {}),
                  Container(width: (MediaQuery.of(context).size.width - 300) / 4),
                ],
              )),
          settingItem('镜头反向', checkBox(false, '', LwApi.getInstance().setMirrorCamera)),
          settingItem(
              '飞行记录',
              checkBox(false, '', () {
                Get.back();
                Get.to(() => const FlyRecordPage());
                // Get.to(() => const TestPage());
                // Navigator.push(context,  MaterialPageRoute(builder: (context)=>const FlyRecordPage()));
              })),
          settingItem('查找飞行器', checkBox(false, '', () {})),
        ],
      ),
    );
  }
}
