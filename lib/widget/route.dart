import 'package:flutter/material.dart';

class CustomRoute extends PageRouteBuilder {
  final Widget widget;
  CustomRoute(this.widget) :super(
      opaque:false,
      // 设置过度时间
      transitionDuration: const Duration(milliseconds: 300),
      // 构造器
      pageBuilder: (BuildContext context, Animation<double> animation1, Animation<double> animation2,){
        return widget;
      },
      transitionsBuilder: (BuildContext context, Animation<double> animation1, Animation<double> animation2, Widget child) {
        return FadeTransition(
          opacity: Tween(begin: 0.0, end: 1.0).animate(animation1),
          child: child,
        );
      }
  );
}