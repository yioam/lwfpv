import 'dart:io';

import 'package:flutter/material.dart';
import 'package:chewie/chewie.dart';
import 'package:video_player/video_player.dart';

class VideoPlayer extends StatefulWidget {
  final String path;

  const VideoPlayer(this.path, {Key? key}) : super(key: key);

  @override
  _VideoPlayerState createState() => _VideoPlayerState();
}

class _VideoPlayerState extends State<VideoPlayer> {
  late ChewieController _chewieController;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Stack(
        children: [
          Center(
            child: Chewie(
              controller: _chewieController,
            ),
          ),
        ],
      ),
    );
  }

  @override
  void initState() {
    super.initState();
    VideoPlayerController _videoPlayerController1 = VideoPlayerController.file(File(widget.path));
    _chewieController = ChewieController(
      videoPlayerController: _videoPlayerController1,
      aspectRatio: 3/2,
      autoPlay: true,
//      looping: true,
//        fullScreenByDefault:true,
      allowFullScreen: false,
    );
  }
}
