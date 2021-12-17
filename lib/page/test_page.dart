import 'dart:async';

import 'package:flutter/material.dart';

class TestPage extends StatefulWidget {
  const TestPage({Key? key}) : super(key: key);

  @override
  _TestPageState createState() => _TestPageState();
}

class _TestPageState extends State<TestPage> {
  final ScrollController controller = ScrollController();
  final GlobalKey widgetKey = GlobalKey();

  /* widgetKey is for widget in buildHeaderRow() */
  StreamController<bool> _streamController = StreamController<bool>();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('123'),
        centerTitle: true,
      ),
      body: getBody(),
    );
  }

  Widget getBody() {
    controller.addListener(() {
      if (widgetKey.currentContext != null) {
        double height = widgetKey.currentContext?.size?.height ?? 0;
        _streamController.add(controller.offset >= height);
      }
    });
    return ListView(
      controller: controller,
      children: <Widget>[ buildPagerRow()],
    );
  }

  Widget buildPagerRow() => _EventSpeakerPager(scrollCallback, _streamController.stream);

  scrollCallback(double position) => controller.position.jumpTo(controller.position.pixels - position);

  @override
  void dispose() {
    controller.dispose();
    super.dispose();
  }
}

typedef ScrollCallback = void Function(double position);

class _EventSpeakerPager extends StatefulWidget {
  _EventSpeakerPager(this.callback, this.stream);

  final ScrollCallback callback;
  final Stream<bool> stream;

  @override
  State<StatefulWidget> createState() => _EventSpeakerPagerState();
}

class _EventSpeakerPagerState extends State<_EventSpeakerPager> {
  final GlobalKey tabKey = GlobalKey();
  bool isChildScrollEnabled = false;

  ScrollController controller = ScrollController();

  List<String> data = [
    '1',
    '2',
    '3',
    '4',
    '5',
    '6',
    '7',
    '8',
    '9',
    '10',
  ];

  @override
  void initState() {
    super.initState();
    widget.stream.distinct().listen((bool data) {
      setState(() {
        isChildScrollEnabled = data;
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    ListView eventList = ListView.builder(
      physics: isChildScrollEnabled ? AlwaysScrollableScrollPhysics() : NeverScrollableScrollPhysics(),
      controller: controller,
      itemBuilder: (buildContext, position) {
        // if (position.isOdd) return CommonDivider();

        return Text(data[position~/1]);
        // return buildEventRow(getEventList()[position ~/ 2], false, null);
      },
      itemCount: data.length,
    );
    return Listener(
      onPointerMove: (event) {
        double pixels = controller.position.pixels;
        if (event.delta.dy > 0.0 && pixels == 0.0) widget.callback(event.delta.dy);
      },
      child: eventList,
    );
  }
}
