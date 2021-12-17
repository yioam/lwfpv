import 'package:flutter/material.dart';

///手势控件
class GestureTap extends StatefulWidget {
  final Widget child;
  final Function onTap;
  final bool needGreyBg;
  final EdgeInsetsGeometry margin;

  const GestureTap({Key? key, required this.child, required this.onTap, this.margin = const EdgeInsets.only(top: 0), this.needGreyBg = true}) : super(key: key);

  @override
  GestureTapState createState() {
    return GestureTapState();
  }
}

class GestureTapState extends State<GestureTap> with SingleTickerProviderStateMixin {
  AnimationController? _controller;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(vsync: this, duration: const Duration(milliseconds: 50));
  }

  @override
  void dispose() {
    _controller!.stop();
    _controller!.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      child: AnimatedBuilder(
        animation: _controller!,
        builder: (BuildContext context, Widget? child) {
          return Container(
            margin: widget.margin,
            foregroundDecoration: widget.needGreyBg
                ? BoxDecoration(
                    color: Colors.grey.withOpacity(0.2 * _controller!.value),
                  )
                : null,
            child: widget.child,
          );
        },
      ),
      onTap: () {
        Future.delayed(const Duration(milliseconds: 50), () {
          widget.onTap();
        });
      },
      onTapDown: (d) {
        _controller?.forward();
      },
      onTapUp: (d) {
        prepareToIdle();
      },
      onTapCancel: () {
        prepareToIdle();
      },
    );
  }

  void prepareToIdle() {
    AnimationStatusListener? listener;
    listener = (AnimationStatus statue) {
      if (statue == AnimationStatus.completed) {
        _controller!.removeStatusListener(listener!);
        toStart();
      }
    };
    _controller!.addStatusListener(listener);
    if (!_controller!.isAnimating) {
      _controller!.removeStatusListener(listener);
      toStart();
    }
  }

  void toStart() {
    _controller!.stop();
    _controller!.reverse();
  }

  onAnimation() {
    _controller!.forward();
    Future.delayed(const Duration(milliseconds: 500)).then((value) => prepareToIdle());
  }
}
