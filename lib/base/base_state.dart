import 'package:flutter/material.dart';

abstract class BaseState<T> extends State {
  Widget settingItem(String text,Widget rightWidget) {
    return Container(
      margin: const EdgeInsets.only(left: 10, right: 10, top: 10),
      padding: const EdgeInsets.only(bottom: 10),
      decoration: const BoxDecoration(
        border: Border(bottom: BorderSide(color: Color(0x77ffffff)))
      ),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.end,
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(text, style: const TextStyle(color: Colors.white)),
          rightWidget,
        ],
      ),
    );
  }

  Widget checkBox(bool isCheck, String text, GestureTapCallback onTap, {Widget? topWidget}) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
          color: Colors.transparent,
          width: (MediaQuery.of(context).size.width - 300) / 4,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              topWidget ?? Container(),
              Row(
                children: [
                  Icon(isCheck ? Icons.radio_button_checked : Icons.radio_button_unchecked, color: Colors.white),
                  Text(text, style: const TextStyle(color: Colors.white)),
                ],
              ),
            ],
          )),
    );
  }
}
