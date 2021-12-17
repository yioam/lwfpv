import 'dart:math';

import 'package:flutter/material.dart';

typedef Update = void Function(double proportionX, double proportionY);

///遥感
class RemoteSensingWidget extends StatefulWidget {
  final Update onUpdate;
  final int? x;
  final int? y;
  final bool needGesture;

  const RemoteSensingWidget(
    this.onUpdate, {
    Key? key,
    this.x,
    this.y,
    this.needGesture = true,
  }) : super(key: key);

  @override
  _RemoteSensingWidgetState createState() => _RemoteSensingWidgetState();
}

class _RemoteSensingWidgetState extends State<RemoteSensingWidget> {
  double _left = 0;
  double _top = 0;

  @override
  Widget build(BuildContext context) {
    const double lineWidth = 2.0;
    const Color white = Color(0xccffffff);
    return GestureDetector(
      child: Stack(
        children: [
          ///最外层两个半圆
          const Align(
            child: CustomPaint(
              painter: Progress(
                backgroundColor: Colors.transparent,
                progressColor: white,
                startPointAngle: pi / 18,
                endPointAngle: pi * 8 / 9,
                centerClose: false,
                radius: 100.0,
                lineWidth: lineWidth,
                value: 1,
              ),
            ),
          ),
          const Align(
            child: CustomPaint(
              painter: Progress(
                backgroundColor: Colors.transparent,
                progressColor: white,
                startPointAngle: pi * 19 / 18,
                endPointAngle: pi * 8 / 9,
                centerClose: false,
                radius: 100.0,
                lineWidth: lineWidth,
                value: 1,
              ),
            ),
          ),

          ///第二层顽症的圆
          const Align(
            child: CustomPaint(
              painter: Progress(
                backgroundColor: Colors.transparent,
                progressColor: white,
                startPointAngle: 0,
                endPointAngle: pi * 2,
                centerClose: false,
                radius: 90.0,
                lineWidth: lineWidth,
                value: 1,
              ),
            ),
          ),
          Align(
            alignment: Alignment.center,
            child: Container(
              width: 178,
              height: 178,
              decoration: const BoxDecoration(color: Color(0x77000000), shape: BoxShape.circle),
            ),
          ),

          ///第三层圆
          const Align(
            child: CustomPaint(
              painter: Progress(
                backgroundColor: Colors.transparent,
                progressColor: white,
                startPointAngle: 0,
                endPointAngle: pi * 2,
                centerClose: false,
                radius: 50.0,
                lineWidth: lineWidth,
                value: 1,
              ),
            ),
          ),

          ///第四层4个圆弧
          const Align(
            child: CustomPaint(
              painter: Progress(
                backgroundColor: Colors.transparent,
                progressColor: white,
                startPointAngle: pi / 18,
                endPointAngle: pi * 7 / 18,
                centerClose: false,
                radius: 40.0,
                lineWidth: 5,
                value: 1,
              ),
            ),
          ),
          const Align(
            child: CustomPaint(
              painter: Progress(
                backgroundColor: Colors.transparent,
                progressColor: white,
                startPointAngle: pi * 10 / 18,
                endPointAngle: pi * 7 / 18,
                centerClose: false,
                radius: 40.0,
                lineWidth: 5,
                value: 1,
              ),
            ),
          ),
          const Align(
            child: CustomPaint(
              painter: Progress(
                backgroundColor: Colors.transparent,
                progressColor: white,
                startPointAngle: pi * 19 / 18,
                endPointAngle: pi * 7 / 18,
                centerClose: false,
                radius: 40.0,
                lineWidth: 5,
                value: 1,
              ),
            ),
          ),
          const Align(
            child: CustomPaint(
              painter: Progress(
                backgroundColor: Colors.transparent,
                progressColor: white,
                startPointAngle: pi * 28 / 18,
                endPointAngle: pi * 7 / 18,
                centerClose: false,
                radius: 40.0,
                lineWidth: 5,
                value: 1,
              ),
            ),
          ),

          ///中间圆
          Positioned(
              left: 75 + _left + (widget.x ?? 0) * 75 / 500,
              top: 75 + _top + (widget.y ?? 0) * 75 / 500,
              child: GestureDetector(
                child: Container(
                  width: 50,
                  height: 50,
                  decoration: const BoxDecoration(color: Colors.white, shape: BoxShape.circle),
                ),
              )),
        ],
      ),
      onLongPress: () {},
      //手指滑动时会触发此回调
      onPanUpdate: !widget.needGesture
          ? null
          : (DragUpdateDetails e) {
              //用户手指滑动时，更新偏移，重新构建
              setState(() {
                // _left += e.delta.dx;
                // _top += e.delta.dy;
                _left = e.localPosition.dx - 75 - 25;
                _top = e.localPosition.dy - 75 - 25;

                double r = sqrt(_left * _left + _top * _top);

                if (r > 75) {
                  _left = _left * 75 / r;
                  _top = _top * 75 / r;
                }
                widget.onUpdate(_left / 75, _top / 75);
              });
            },
      onPanEnd: !widget.needGesture
          ? null
          : (DragEndDetails e) {
              //打印滑动结束时在x、y轴上的速度
              // print(e.velocity);
              _left = 0;
              _top = 0;
              widget.onUpdate(0, 0);
              setState(() {});
            },
    );
  }
}

class Progress extends CustomPainter {
  final Color backgroundColor; //背景色
  final Color progressColor; //前景色
  final double startPointAngle; //Canvas绘制开始位置
  final double endPointAngle; //结束位置
  final bool centerClose; //圆弧是否闭合
  final double radius; //半径
  final double lineWidth; //所绘制线条的宽度
  final double value; //进度条的value

  const Progress({
    required this.backgroundColor,
    required this.progressColor,
    required this.startPointAngle,
    required this.endPointAngle,
    required this.centerClose,
    required this.radius,
    required this.lineWidth,
    required this.value,
  });

  @override
  void paint(Canvas canvas, Size size) {
    Paint paint = Paint() //初始化画笔
      ..color = backgroundColor //背景颜色
      ..style = PaintingStyle.stroke //画笔样式
      ..strokeCap = StrokeCap.round //画笔笔头类型
      ..strokeWidth = lineWidth; //画笔的宽度
    Rect rect = Rect.fromCircle(center: Offset(size.width / 2, size.height / 2), radius: radius);

    canvas.drawArc(rect, startPointAngle, endPointAngle, centerClose, paint);
//背景弧线绘制完成

    paint
      ..color = progressColor //前景色
      ..strokeWidth = lineWidth + 1.0 //画笔宽度，在这里我们设置得比背景的宽度稍宽些
      ..style = PaintingStyle.stroke
      ..strokeCap = StrokeCap.butt;
    canvas.drawArc(rect, startPointAngle, endPointAngle * value, centerClose, paint);
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) {
    return true;
  }
}
