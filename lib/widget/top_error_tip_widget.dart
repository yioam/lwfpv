import 'package:flutter/material.dart';
import 'package:get/get.dart';

ValueNotifier<String> topErrorTipValue = ValueNotifier('');

///顶部提示文字
class TopErrorTipWidget extends StatefulWidget {
  const TopErrorTipWidget({Key? key}) : super(key: key);

  @override
  _TopErrorTipWidgetState createState() => _TopErrorTipWidgetState();
}

class _TopErrorTipWidgetState extends State<TopErrorTipWidget> with SingleTickerProviderStateMixin {
  Animation<double>? animation;
  AnimationController? controller;
  bool isDialogShow = false;

  @override
  Widget build(BuildContext context) {
    return FadeTransition(
      opacity: animation!,
      child: Text(topErrorTipValue.value, style: const TextStyle(fontSize: 16, color: Colors.red)),
    );
  }

  @override
  void initState() {
    super.initState();
    topErrorTipValue.addListener(showText);
    controller = AnimationController(vsync: this, duration: const Duration(milliseconds: 1000));
    animation = Tween(begin: 1.0, end: 0.0).animate(controller!);
  }

  int _id = 0;

  Future<void> showText() async {
    if (topErrorTipValue.value == '开始校准X') {
      showCalibrationDialog();
    } else if (topErrorTipValue.value == '开始校准Y') {
      showCalibrationDialog();
    } else {
      if (isDialogShow) Get.back();

      _id++;
      int currentId = _id;
      controller!.reset();
      await Future.delayed(const Duration(milliseconds: 1000));
      if (currentId != _id) return;
      controller!.forward();
    }
  }

  showCalibrationDialog() {
    if (!isDialogShow) {
      isDialogShow = true;
      showDialog(
          context: context,
          builder: (context) {
            return Align(
              child: Padding(
                  padding: const EdgeInsets.only(bottom: 30),
                  child: ValueListenableBuilder(
                    valueListenable: topErrorTipValue,
                    builder: (BuildContext context, String value, Widget? child) {
                      return Text(
                        topErrorTipValue.value,
                        style: const TextStyle(fontSize: 16, color: Colors.red, decoration: TextDecoration.none),
                      );
                    },
                  )),
            );
          }).then((value) => isDialogShow = false);
    }
  }

  @override
  void dispose() {
    super.dispose();
    topErrorTipValue.removeListener(showText);
  }
}
