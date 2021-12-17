import 'package:flutter/material.dart';
import 'package:lewei_pro/base/base_state.dart';
import 'package:lewei_pro/contoller/fly_control.dart';
import 'package:get/get.dart';

///参数
class SettingParameter extends StatefulWidget {
  const SettingParameter({Key? key}) : super(key: key);

  @override
  _SettingParameterState createState() => _SettingParameterState();
}

class _SettingParameterState extends BaseState<SettingParameter> {
  final FlyController model = Get.find();

  TextEditingController defaultHeightController = TextEditingController(text: '20'); //默认航点高度
  TextEditingController maxHeightController = TextEditingController(text: '50'); //最大航点高度
  TextEditingController speedController = TextEditingController(text: '1'); //航点速度
  TextEditingController timeController = TextEditingController(text: '0'); //航点停留时间
  TextEditingController radiusController = TextEditingController(text: '50'); //航点最大半径
  TextEditingController minHeightController = TextEditingController(text: '10'); //航点最低高度

  FocusNode defaultHeightFocusNode = FocusNode();
  FocusNode maxHeightFocusNode = FocusNode();
  FocusNode speedFocusNode = FocusNode();
  FocusNode timeFocusNode = FocusNode();
  FocusNode radiusFocusNode = FocusNode();
  FocusNode minHeightFocusNode = FocusNode();

  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      child: Column(
        children: [
          settingItem(
              '默认航点高度',
              Row(
                children: [
                  const Text('(5-50m)', style: TextStyle(color: Colors.white)),
                  const SizedBox(width: 10),
                  editText(defaultHeightController, defaultHeightFocusNode),
                ],
              )),
          settingItem(
              '最大航点高度',
              Row(
                children: [
                  const Text('(5-50m)', style: TextStyle(color: Colors.white)),
                  const SizedBox(width: 10),
                  editText(maxHeightController, maxHeightFocusNode),
                ],
              )),
          settingItem(
              '航点速度',
              Row(
                children: [
                  const Text('(1-6m/s)', style: TextStyle(color: Colors.white)),
                  const SizedBox(width: 10),
                  editText(speedController, speedFocusNode),
                ],
              )),
          settingItem(
              '航点停留时间',
              Row(
                children: [
                  const Text('(0-60s)', style: TextStyle(color: Colors.white)),
                  const SizedBox(width: 10),
                  editText(timeController, timeFocusNode),
                ],
              )),
          settingItem(
              '航点最大半径',
              Row(
                children: [
                  const Text('(0-50m)', style: TextStyle(color: Colors.white)),
                  const SizedBox(width: 10),
                  editText(radiusController, radiusFocusNode),
                ],
              )),
          settingItem(
              '航点最低高度',
              Row(
                children: [
                  const Text('(10-30m)', style: TextStyle(color: Colors.white)),
                  const SizedBox(width: 10),
                  editText(minHeightController, minHeightFocusNode),
                ],
              )),
        ],
      ),
    );
  }

  Widget editText(TextEditingController controller, FocusNode focusNode) {
    InputBorder inputBorder = const OutlineInputBorder(borderSide: BorderSide(color: Colors.white, width: 1));

    return SizedBox(
        width: (MediaQuery
            .of(context)
            .size
            .width - 300) / 4,
        height: 30,
        child: TextField(
          cursorColor: Colors.white,
          decoration: InputDecoration(
            border: inputBorder,
            disabledBorder: inputBorder,
            enabledBorder: inputBorder,
            focusedBorder: inputBorder,
          ),
          focusNode: focusNode,
          style: const TextStyle(color: Colors.white),
          controller: controller,
          keyboardType: TextInputType.number,
        ));
  }

  @override
  void initState() {
    super.initState();

    defaultHeightFocusNode.addListener(defaultHeight);
    maxHeightFocusNode.addListener(maxHeight);
    speedFocusNode.addListener(speed);
    timeFocusNode.addListener(time);
    radiusFocusNode.addListener(radius);
    minHeightFocusNode.addListener(minHeight);
  }

  void defaultHeight() {
    if (defaultHeightFocusNode.hasFocus) return;
    try {
      int value = int.parse(defaultHeightController.text);
      if (value > 50) {
        defaultHeightController.text = '50';
        model.defaultHeight = 50;
      } else if (value < 5) {
        defaultHeightController.text = '5';
        model.defaultHeight = 5;
      } else {
        model.defaultHeight = value;
      }
    } catch (e) {
      defaultHeightController.text = model.defaultHeight.toString();
    }
  }

  void maxHeight() {
    if (maxHeightFocusNode.hasFocus) return;
    try {
      int value = int.parse(maxHeightController.text);
      if (value > 50) {
        maxHeightController.text = '50';
        model.maxHeight = 50;
      } else if (value < 5) {
        maxHeightController.text = '5';
        model.maxHeight = 5;
      } else {
        model.maxHeight = value;
      }
    } catch (e) {
      maxHeightController.text = model.maxHeight.toString();
    }
  }

  void speed() {
    if (speedFocusNode.hasFocus) return;
    try {
      int value = int.parse(speedController.text);
      if (value > 6) {
        speedController.text = '6';
        model.speed = 6;
      } else if (value < 1) {
        speedController.text = '1';
        model.speed = 1;
      } else {
        model.speed = value;
      }
    } catch (e) {
      speedController.text = model.speed.toString();
    }
  }

  void time() {
    if (timeFocusNode.hasFocus) return;
    try {
      int value = int.parse(timeController.text);
      if (value > 60) {
        timeController.text = '60';
        model.time = 60;
      } else if (value < 0) {
        timeController.text = '0';
        model.time = 0;
      } else {
        model.time = value;
      }
    } catch (e) {
      timeController.text = model.time.toString();
    }
  }

  void radius() {
    if (radiusFocusNode.hasFocus) return;
    try {
      int value = int.parse(radiusController.text);
      if (value > 50) {
        radiusController.text = '50';
        model.radius = 50;
      } else if (value < 0) {
        radiusController.text = '0';
        model.radius = 0;
      } else {
        model.radius = value;
      }
    } catch (e) {
      radiusController.text = model.radius.toString();
    }
  }

  void minHeight() {
    if (minHeightFocusNode.hasFocus) return;
    try {
      int value = int.parse(minHeightController.text);
      if (value > 30) {
        minHeightController.text = '30';
        model.minHeight = 30;
      } else if (value < 10) {
        minHeightController.text = '10';
        model.minHeight = 10;
      } else {
        model.minHeight = value;
      }
    } catch (e) {
      minHeightController.text = model.minHeight.toString();
    }
  }

  @override
  void dispose() {
    super.dispose();

    defaultHeightFocusNode.removeListener(defaultHeight);
    maxHeightFocusNode.removeListener(maxHeight);
    speedFocusNode.removeListener(speed);
    timeFocusNode.removeListener(time);
    radiusFocusNode.removeListener(radius);
    minHeightFocusNode.removeListener(minHeight);

    defaultHeightFocusNode.dispose();
    maxHeightFocusNode.dispose();
    speedFocusNode.dispose();
    timeFocusNode.dispose();
    radiusFocusNode.dispose();
    minHeightFocusNode.dispose();

    defaultHeightController.dispose();
    maxHeightController.dispose;
    speedController.dispose;
    timeController.dispose;
    radiusController.dispose;
    minHeightController.dispose;
  }
}
