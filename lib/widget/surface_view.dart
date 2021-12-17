import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
Map<int,MethodChannel> surfaceViewMap = {};

///视频渲染控件
class SurfaceView extends StatelessWidget {
  final Map map;

  const SurfaceView({Key? key, required this.map}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return AndroidView(
      viewType: 'com.lewei.flutter.surfaceView',
      creationParams: map,
      creationParamsCodec: const StandardMessageCodec(),
      onPlatformViewCreated: (int _id) {
        surfaceViewMap[map['id']] = MethodChannel('com.flutter/surfaceView${map['id']}');
      },
    );
  }

}
