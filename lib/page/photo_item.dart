import 'dart:typed_data';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:lewei_pro/widget/lru_image_cache.dart';
import 'package:photo_manager/photo_manager.dart';

class PhotoItem extends StatelessWidget {
  final AssetEntity entity;

  final Color? themeColor;

  final int size;

  final Color? imageColor;

  const PhotoItem({
    Key? key,
    required this.entity,
    this.themeColor,
    this.size = 64,
    this.imageColor,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    var thumb = LRUImageCache.getData(entity, size);
    if (thumb != null) {
      return _buildImageItem(context, thumb);
    }

    return FutureBuilder<Uint8List?>(
      future: entity.thumbDataWithSize(size, size),
      builder: (BuildContext context, AsyncSnapshot<Uint8List?> snapshot) {
        var futureData = snapshot.data;
        if (snapshot.connectionState == ConnectionState.done && futureData != null) {
          LRUImageCache.setData(entity, size, futureData);
          return _buildImageItem(context, futureData);
        }
        return Center(
            child: Container(
                width: 30.0,
                height: 30.0,
                child: const CircularProgressIndicator(
                  valueColor: AlwaysStoppedAnimation(Colors.white),
                )));
      },
    );
  }

  Widget _buildImageItem(BuildContext context, Uint8List data) {
    var image = Image.memory(
      data,
      width: 200,
      height: 200,
      fit: BoxFit.cover,
      // alignment: Alignment.center,
    );
    return image;
  }
}
