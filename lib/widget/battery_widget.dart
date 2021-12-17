import 'package:flutter/material.dart';

///电量图标
class BatteryPaint extends CustomPainter {
  num percent;
  Color color;
  double padding;
  double frameWidth;
  double batteryHeadHeight; //电池头的空心高度

  BatteryPaint(this.percent, this.color, {this.padding = 1.0, this.frameWidth = 1.0, this.batteryHeadHeight = 3});

  @override
  void paint(Canvas canvas, Size size) {
    double batteryWidget = size.width - batteryHeadHeight - frameWidth;

    ///外边框
    Paint framePaint = Paint();
    framePaint.strokeWidth = frameWidth;
    framePaint.color = color;
    framePaint.style = PaintingStyle.stroke;
    canvas.drawRect(Rect.fromLTRB(frameWidth / 2, frameWidth / 2, batteryWidget - frameWidth / 2, size.height - frameWidth / 2), framePaint);

    ///电池百分比
    double width = batteryWidget - padding * 2 - frameWidth * 2;
    double height = size.height - padding * 2 - frameWidth * 2;
    Paint mPaint = Paint();
    mPaint.strokeWidth = height;
    mPaint.color = color;
    mPaint.style = PaintingStyle.fill;
    canvas.drawLine(Offset(padding + frameWidth, size.height / 2), Offset(percent * width / 100 + padding + frameWidth, size.height / 2), mPaint);

    ///电池头
    Paint headPaint = Paint();
    headPaint.strokeWidth = frameWidth;
    headPaint.color = color;
    headPaint.style = PaintingStyle.stroke;
    canvas.drawRect(Rect.fromLTWH(batteryWidget - frameWidth / 2, (size.height - frameWidth - batteryHeadHeight) / 2, frameWidth + batteryHeadHeight, frameWidth + batteryHeadHeight), framePaint);
  }

  @override
  bool shouldRepaint(BatteryPaint oldDelegate) {
    return oldDelegate.percent != percent;
  }
}
