import 'package:flutter/cupertino.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:lewei_pro/page/file_list_page.dart';
import 'package:lewei_pro/page/photo_list_page.dart';
import 'package:lewei_pro/page/video_list_page.dart';

class PhotoPage extends StatelessWidget {
  PageController controller = PageController();
  ValueNotifier<int> pageValue = ValueNotifier(0);

  GlobalKey<PhotoListPageState> photoListKey = GlobalKey<PhotoListPageState>();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: Column(
          children: [
            Container(
                color: const Color(0xff999999),
                child: ValueListenableBuilder(
                    valueListenable: pageValue,
                    builder: (BuildContext context, int index, Widget? child) {
                      return Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          actionItem('images/map_arrow_pre.png', () {
                            Navigator.pop(context);
                          }),
                          actionItem(index == 0 ? 'images/playback_title_photo_h.png' : 'images/playback_title_photo.png', () {
                            controller.animateToPage(0, duration: const Duration(milliseconds: 500), curve: Curves.ease);
                          }),
                          actionItem(index == 1 ? 'images/playback_title_video_h.png' : 'images/playback_title_video.png', () {
                            controller.animateToPage(1, duration: const Duration(milliseconds: 500), curve: Curves.ease);
                          }),
                          actionItem(index == 2 ? 'images/playback_title_video_h.png' : 'images/playback_title_video.png', () {
                            controller.animateToPage(2, duration: const Duration(milliseconds: 500), curve: Curves.ease);
                          }),
                          actionItem('images/gallery_delete.png', () {

                            photoListKey.currentState!.imageHandler();
                          }),
                        ],
                      );
                    })),
            Expanded(
                child: PageView(
              controller: controller,
              onPageChanged: (int index) {
                pageValue.value = index;
              },
              children: [
                PhotoListPage(key: photoListKey),
                const VideoListPage(),
                const FileListPage(),
              ],
            )),
          ],
        ),
      ),
    );
  }

  Widget actionItem(String img, VoidCallback onTap) {
    return MaterialButton(
      onPressed: onTap,
      padding: EdgeInsets.zero,
      minWidth: 0,
      child: Image.asset(
        img,
        width: 30,
      ),
    );
  }
}
