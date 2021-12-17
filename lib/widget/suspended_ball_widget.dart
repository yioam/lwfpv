import 'package:flutter/material.dart';
import 'dart:math';

///悬浮小球
class SuspendedBallWidget extends StatefulWidget {
  const SuspendedBallWidget({Key? key}) : super(key: key);

  @override
  _SuspendedBallWidgetState createState() => _SuspendedBallWidgetState();
}

class _SuspendedBallWidgetState extends State<SuspendedBallWidget> {
  double _left = 0;
  double _top = 0;

  @override
  Widget build(BuildContext context) {
    return Positioned(
        left: 75 + _left,
        top: 75 + _top,
        child: GestureDetector(
          onLongPress: () {},
          //手指滑动时会触发此回调
          onPanUpdate: (DragUpdateDetails e) {
            //用户手指滑动时，更新偏移，重新构建
            setState(() {
              // _left += e.delta.dx;
              // _top += e.delta.dy;
              _left = e.localPosition.dx - 25;
              _top = e.localPosition.dy - 25;

              double r = sqrt(_left * _left + _top * _top);

              if (r > 75) {
                _left = _left * 75 / r;
                _top = _top * 75 / r;
              }
            });
          },
          onPanEnd: (DragEndDetails e) {
            //打印滑动结束时在x、y轴上的速度
            // print(e.velocity);
            _left = 0;
            _top = 0;
            setState(() {});
          },
          child: Container(
            width: 50,
            height: 50,
            decoration: const BoxDecoration(
                color: Colors.white, shape: BoxShape.circle),
          ),
        ));
  }
}
