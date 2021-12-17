import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:lewei_pro/api/lw_api.dart';
import 'package:lewei_pro/bean/fly_mileage_bean.dart';
import 'package:get/get.dart';
import 'package:lewei_pro/page/fly_record/fly_log_page.dart';
import 'package:lewei_pro/widget/gesture_tap.dart';

class FlyRecordPage extends StatefulWidget {
  const FlyRecordPage({Key? key}) : super(key: key);

  @override
  _FlyRecordPageState createState() => _FlyRecordPageState();
}

class _FlyRecordPageState extends State<FlyRecordPage> {
  List<FlyMileage> flyMileages = [];
  bool loadComplete = false;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: Column(
          children: [
            Container(
              color: const Color(0xff283138),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  IconButton(onPressed: () => Get.back(), icon: const Icon(Icons.keyboard_return, color: Colors.white)),
                  const Text('飞行数据中心', style: TextStyle(color: Colors.white)),
                  IconButton(onPressed: () {}, icon: const Icon(Icons.backup_outlined, color: Colors.white))
                ],
              ),
            ),
            Expanded(
              child: flyMileages.isEmpty && !loadComplete
                  ? const Center(
                      child: SizedBox(
                        width: 30,
                        height: 30,
                        child: CircularProgressIndicator(color: Colors.grey),
                      ),
                    )
                  : ListView.builder(
                      itemCount: flyMileages.length,
                      itemBuilder: (BuildContext context, int index) {
                        return Column(
                          children: [
                            Visibility(child: itemWidget('日期', '里程', '高度', '时长'), visible: index == 0),
                            GestureTap(
                              child: Container(
                                child: itemWidget(flyMileages[index].mileageTime, '${flyMileages[index].mileage}m', '${flyMileages[index].height}m', flyMileages[index].duration),
                                padding: const EdgeInsets.symmetric(vertical: 15),
                                decoration: BoxDecoration(border: Border(bottom: BorderSide(color: Colors.grey.shade300, width: 1))),
                              ),
                              onTap: () => Get.to(() => FlyLog(id: flyMileages[index].flyNumberId, date: flyMileages[index].mileageTime)),
                            ),
                          ],
                        );
                      },
                    ),
            )
          ],
        ),
      ),
    );
  }

  Widget itemWidget(String date, String mill, String height, String duration) {
    return Row(
      children: [
        Expanded(child: Center(child: Text(date))),
        Expanded(child: Center(child: Text(mill))),
        Expanded(child: Center(child: Text(height))),
        Expanded(child: Center(child: Text(duration))),
      ],
    );
  }

  @override
  void initState() {
    super.initState();
    initData();
  }

  initData() async {
    String mileages = await LwApi.getInstance().queryMileageList();
    if (mileages != 'error') {
      flyMileages.clear();
      List<dynamic> list = jsonDecode(mileages);

      for (var element in list) {
        flyMileages.add(FlyMileage.fromJson(element));
      }
    }
    setState(() => loadComplete = true);
  }
}
