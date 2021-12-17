import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:lewei_pro/bean/lw_uart_protol_bean.dart';
import 'package:lewei_pro/contoller/fly_control.dart';
import 'package:lewei_pro/generated/l10n.dart';
import 'package:lewei_pro/page/control_page.dart';
import 'package:lewei_pro/util/flutter_screenutil.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:orientation/orientation.dart';
import 'package:get/get.dart';
import 'package:flutter_localizations/flutter_localizations.dart';

void main() {
  // AMapFlutterLocation.setApiKey("anroid ApiKey", "ios ApiKey");
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return GetMaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      localizationsDelegates: const [
        S.delegate,
        GlobalMaterialLocalizations.delegate,
        GlobalCupertinoLocalizations.delegate,
        GlobalWidgetsLocalizations.delegate,
      ],
      supportedLocales: S.delegate.supportedLocales,
      localeListResolutionCallback: (locales, supportedLocales) {
        print(locales);
        return;
      },
      home: const MyHomePage(),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({Key? key}) : super(key: key);

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  final FlyController model = Get.put(FlyController.getInstance());
  final LwUartProtolBean lwUartProtolBean = Get.put(LwUartProtolBean());

  @override
  Widget build(BuildContext context) {
    ScreenUtil.getInstance().init(context);
    return Scaffold(
      body: Stack(
        children: [
          Image.asset(
            'images/home_bg.png',
            fit: BoxFit.fill,
            gaplessPlayback: true,
            height: MediaQuery.of(context).size.height,
            width: MediaQuery.of(context).size.width,
          ),
          Positioned(
            bottom: 80,
            left: 50,
            right: 50,
            child: GestureDetector(
              onTap: () async {
                if (!await Permission.location.isGranted) {
                  model.hasPermission = await Permission.location.request().isGranted;
                } else {
                  model.hasPermission = true;
                }
                OrientationPlugin.forceOrientation(DeviceOrientation.landscapeRight);
                ///TODO android屏幕旋转时间是400ms
                await Future.delayed(const Duration(milliseconds: 400));
                Get.to(() => const ControlPage(), duration: Duration.zero);
                return;
              },
              child: Container(
                height: 30,
                color: const Color(0x77ffffff),
                alignment: Alignment.center,
                child: const Text('进入控制', style: TextStyle(fontSize: 16)),
              ),
            ),
          ),
        ],
      ),
    );
  }

  @override
  void initState() {
    super.initState();

    OrientationPlugin.forceOrientation(DeviceOrientation.portraitUp);
  }
}
