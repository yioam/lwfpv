import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:lewei_pro/res/text_style.dart';

class PublicDialog {
  static Future showMarkerDetailDialog(BuildContext context,
      String title,
      int height,) async {
    await showDialog(
      context: context,
      barrierColor: Colors.transparent,
      builder: (BuildContext context) {
        return StatefulBuilder(
          builder: (BuildContext context, setter) {
            return GestureDetector(
              onTap: () => Get.back(),
              child: Scaffold(
                backgroundColor: Colors.transparent,
                body: GestureDetector(
                  onTap: () {},
                  child: Align(
                    alignment: Alignment.bottomRight,
                    child: SizedBox(
                      width: 260,
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.end,
                        children: [
                          Container(
                            color: Colors.lightGreenAccent,
                            width: 260,
                            alignment: Alignment.center,
                            padding: const EdgeInsets.only(top: 10, bottom: 10),
                            child: Text(title, style: textStyle14w),
                          ),
                          Container(
                            color: Colors.black87,
                            padding: const EdgeInsets.only(top: 50, bottom: 50),
                            child: Row(
                              children: [
                                SizedBox(width: 60, child: Text('高度 $height m', style: textStyle12w)),
                                Slider(
                                  value: height.toDouble(),
                                  min: 5,
                                  max: 50,
                                  label: '高度 $height m',
                                  inactiveColor: Colors.grey,
                                  onChanged: (double v) {
                                    height = v.toInt();
                                    setter(() {});
                                  },
                                ),
                              ],
                            ),
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
      },
    );

    return height;
  }

  static bool isShowTipDialog = false;

  static Future showTipDialog(BuildContext context,
      String title,
      String content,) async {
    if (isShowTipDialog) return;
    isShowTipDialog = true;
    await showCupertinoDialog(
      context: context,
      builder: (context) {
        return CupertinoAlertDialog(
          title: Text(title),
          content: Text('\n$content'),
          actions: <Widget>[
            CupertinoDialogAction(
              child: const Text('确认'),
              onPressed: () => Get.back(),
            ),
          ],
        );
      },
    );
    isShowTipDialog = false;
    return;
  }

  //提示弹框
  static Future showBottomAlertDialog(BuildContext context,String content, {String title = '提示'}){

    return showCupertinoDialog(
        context: context,
        // backgroundColor: Colors.transparent,
        builder: (BuildContext ctx) {
          return CupertinoAlertDialog(
            title: Text(title),
            content: Text('\n$content'),
            actions: <Widget>[
              CupertinoDialogAction(
                child: const Text('确定'),
                onPressed: () => Get.back(result: '确定'),
              ),CupertinoDialogAction(
                child: const Text('取消'),
                onPressed: () => Get.back(result: '取消'),
              ),
            ],
          );
        }
    );
  }
}
