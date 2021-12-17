import 'dart:io';
import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'package:lewei_pro/api/lw_api.dart';
import 'package:lewei_pro/page/video_player.dart';
import 'package:video_thumbnail/video_thumbnail.dart';

class VideoListPage extends StatefulWidget {
  const VideoListPage({Key? key}) : super(key: key);

  @override
  _VideoListPageState createState() => _VideoListPageState();
}

class _VideoListPageState extends State<VideoListPage> with AutomaticKeepAliveClientMixin {
  List<VideoBean> videos = [];
  bool loadComplete = false;

  @override
  Widget build(BuildContext context) {
    return Container(
      color: Colors.black87,
      child: videos.isEmpty && !loadComplete
          ? Center(
              child: Container(
                width: 30,
                height: 30,
                child: const CircularProgressIndicator(color: Colors.white),
              ),
            )
          : GridView.builder(
              itemCount: videos.length,
              gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                crossAxisCount: MediaQuery.of(context).size.height > MediaQuery.of(context).size.width ? 3 : 6,
                crossAxisSpacing: 5,
                mainAxisSpacing: 5,
                childAspectRatio: 1,
              ),
              itemBuilder: (BuildContext context, int index) {
                return GestureDetector(
                  onTap: () {
                    Navigator.push(context, MaterialPageRoute(builder: (BuildContext context) {
                      return VideoPlayer(videos[index].path);
                    }));
                  },
                  child: Stack(
                    children: [
                      Image.memory(videos[index].uint8list, fit: BoxFit.cover, width: 200, height: 200),
                      const Center(child: Icon(Icons.play_arrow, color: Colors.white, size: 50)),
                    ],
                  ),
                );
              }),
    );
  }

  @override
  void initState() {
    super.initState();
    initData();

    WidgetsBinding.instance!.addPostFrameCallback((Duration duration) async {
      // showDialog(context: context, builder: (BuildContext context){
      //   return ;
      // });-
    });
  }

  Future<void> initData() async {
    String path = await LwApi.getInstance().getPhVideoPath();
    Directory directory = Directory(path);
    if (!await directory.exists()) {
      await directory.create();
    }

    directory.listSync().removeWhere((element) {
      String pathUpper = element.path.toLowerCase();
      return !(pathUpper.endsWith('.avi') || pathUpper.endsWith('.3gp') || pathUpper.endsWith('.mp4'));
    });
    videos.clear();

    await Future.forEach<FileSystemEntity>(directory.listSync(), (element) async {
      try {
        Uint8List? uint8list = await VideoThumbnail.thumbnailData(
          video: element.path,
          imageFormat: ImageFormat.JPEG,
          maxWidth: 500, // specify the width of the thumbnail, let the height auto-scaled to keep the source aspect ratio
          quality: 50,
        );
        if (uint8list != null) {
          videos.add(VideoBean(uint8list, element.path));
        }
      } catch (e) {}
    });

    loadComplete = true;
    if (mounted) setState(() {});
  }

  @override
  bool get wantKeepAlive => true;
}

class VideoBean {
  Uint8List uint8list;
  String path;

  VideoBean(this.uint8list, this.path);
}
