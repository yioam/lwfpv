import 'dart:io';
import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'package:lewei_pro/api/lw_api.dart';
import 'package:lewei_pro/page/photo_list_page.dart';
import 'package:photo_view/photo_view_gallery.dart';
import 'package:photo_view/photo_view.dart';
import 'package:photo_manager/photo_manager.dart';
import 'package:get/get.dart';
// import 'package:share_plus/share_plus.dart';

class PhotoPreview extends StatefulWidget {
  final List<ImageBean> files;
  final int initIndex;

  const PhotoPreview(this.files, this.initIndex, {Key? key}) : super(key: key);

  @override
  _PhotoPreviewState createState() => _PhotoPreviewState();
}

class _PhotoPreviewState extends State<PhotoPreview> {
  PageController? controller;
  int currentIndex = 0;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black,
      body: SafeArea(
        child: GestureDetector(
          onTap: () {
            Navigator.pop(context);
          },
          child: Stack(
            children: [
              PhotoViewGallery.builder(
                pageController: controller,
                itemCount: widget.files.length,
                builder: _buildItem,
                onPageChanged: (int index) {
                  currentIndex = index;
                  setState(() {});
                },
              ),
              Align(
                alignment: Alignment.topCenter,
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    MaterialButton(
                      onPressed: () {
                        Navigator.pop(context);
                      },
                      padding: EdgeInsets.zero,
                      minWidth: 0,
                      child: Image.asset('images/map_arrow_pre.png', width: 30),
                    ),
                    Text('${currentIndex + 1}/${widget.files.length}', style: const TextStyle(color: Colors.white)),
                    MaterialButton(
                      onPressed: () {
                        showMenu();
                      },
                      padding: EdgeInsets.zero,
                      minWidth: 0,
                      child: const Icon(
                        Icons.more_horiz,
                        color: Colors.white,
                        size: 30,
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  void showMenu() {
    showModalBottomSheet(
        backgroundColor: Colors.transparent,
        context: context,
        isScrollControlled: true,
        useRootNavigator: false,
        builder: (BuildContext context) {
          return GestureDetector(
            onTap: () => Navigator.pop(context),
            child: Scaffold(
              backgroundColor: Colors.transparent,
              body: Container(
                alignment: Alignment.bottomCenter,
                child: GestureDetector(
                  onTap: () {},
                  child: Container(
                    width: MediaQuery.of(context).size.width,
                    decoration: const BoxDecoration(color: Colors.white, borderRadius: BorderRadius.only(topLeft: Radius.circular(20), topRight: Radius.circular(20))),
                    padding: const EdgeInsets.fromLTRB(0, 10, 0, 0),
                    child: Column(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        // MaterialButton(
                        //   onPressed: () => Navigator.pop(context),
                        //   child: Container(
                        //     padding: EdgeInsets.symmetric(vertical: 10),
                        //     alignment: Alignment.center,
                        //     decoration: BoxDecoration(color: Colors.white12, borderRadius: BorderRadius.circular(50)),
                        //     child: Text('删除'),
                        //   ),
                        // ),
                        MaterialButton(
                          onPressed: () async {
                            Get.back();

                            File? f = await widget.files[currentIndex].assetEntity.file;
                            if (f != null) {
                              // Share.share('check out my website https://example.com');
                              // Share.shareFiles([f.path],text: '123');

                              LwApi.getInstance().shareImage(f.path);
                            } else {
                              //
                            }
                          },
                          child: Container(
                            alignment: Alignment.center,
                            decoration: BoxDecoration(color: Colors.white12, borderRadius: BorderRadius.circular(50)),
                            child: Text('分享'),
                          ),
                        ),
                        MaterialButton(
                          onPressed: () => Navigator.pop(context),
                          child: Container(
                            alignment: Alignment.center,
                            decoration: BoxDecoration(color: Colors.white12, borderRadius: BorderRadius.circular(50)),
                            child: Text('取消'),
                          ),
                        )
                      ],
                    ),
                  ),
                ),
              ),
            ),
          );
        });
  }

  PhotoViewGalleryPageOptions _buildItem(BuildContext context, int index) {
    AssetEntity assetEntity = widget.files[index].assetEntity;
    var width = MediaQuery.of(context).size.width;
    var height = MediaQuery.of(context).size.height;

    return PhotoViewGalleryPageOptions.customChild(
      // imageProvider: FileImage(File(widget.files[index].path), scale: 0.2),
      child: FutureBuilder(
        future: assetEntity.thumbDataWithSize(width.floor(), height.floor()),
        builder: (BuildContext context, AsyncSnapshot<Uint8List?> snapshot) {
          var file = snapshot.data;
          if (snapshot.connectionState == ConnectionState.done && file != null) {
            return Image.memory(
              file,
              fit: BoxFit.contain,
              width: double.infinity,
              height: double.infinity,
            );
          } else {
            return Center(
                child: Container(
                    width: 50.0,
                    height: 50.0,
                    child: const CircularProgressIndicator(
                      valueColor: AlwaysStoppedAnimation(Colors.white),
                    )));
          }
        },
      ),
      initialScale: PhotoViewComputedScale.contained,
      minScale: PhotoViewComputedScale.contained * 0.4,
      maxScale: PhotoViewComputedScale.covered * 1.2,
//      heroTag: msg.nonceStr,
      heroAttributes: PhotoViewHeroAttributes(
          tag: widget.files[index].assetEntity.id,
          flightShuttleBuilder: (BuildContext f, Animation<double> a, HeroFlightDirection direction, BuildContext fromHeroContext, BuildContext toHeroContext) {
            Hero toHero;
            if (direction == HeroFlightDirection.push) {
              toHero = toHeroContext.widget as Hero;
            } else {
              toHero = fromHeroContext.widget as Hero;
            }
            return toHero.child;
          }),
    );
  }

  @override
  void initState() {
    super.initState();
    controller = PageController(initialPage: widget.initIndex);
    currentIndex = widget.initIndex;
  }
}
