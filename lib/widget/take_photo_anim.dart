import 'dart:math';

import 'package:flutter/material.dart';

///拍照动画
class TakePhotoAnim extends StatefulWidget {
  const TakePhotoAnim({Key? key}) : super(key: key);

  @override
  TakePhotoAnimState createState() => TakePhotoAnimState();
}

class TakePhotoAnimState extends State<TakePhotoAnim> with SingleTickerProviderStateMixin {
  Animation<int>? animation;
  AnimationController? controller;
  bool visible = false;

  //素材列表
  List<String> images = [
    'images/camerairs6.png',
    'images/camerairs9.png',
    'images/camerairs10.png',
    'images/camerairs11.png',
    'images/camerairs12.png',
    'images/camerairs13.png',
    'images/camerairs14.png',
    'images/camerairs15.png',
    'images/camerairs16.png',
  ];

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
        animation: animation!,
        builder: (BuildContext context, Widget? child) {
          return Visibility(
            visible: visible,
            child: Image.asset(
              images[animation!.value],
              fit: BoxFit.fill,
              gaplessPlayback: true,
              width: MediaQuery.of(context).size.width,
              height: MediaQuery.of(context).size.height,
            ),
          );
        });
  }

  void showAnimal() {
    if (visible) return;
    controller?.reset();
    visible = true;
    controller?.forward();
    animation?.addStatusListener((status) {
      if (status == AnimationStatus.completed) {
        visible = false;
      }
    });
  }

  @override
  void initState() {
    super.initState();

    controller = AnimationController(vsync: this, duration: const Duration(milliseconds: 700));
    animation = IntTween(begin: 0, end: images.length - 1).animate(controller!);
  }

  @override
  void dispose() {
    super.dispose();
    controller!.dispose();
  }
}
