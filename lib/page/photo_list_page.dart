

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:lewei_pro/api/lw_api.dart';
import 'package:lewei_pro/page/photo_item.dart';
import 'package:lewei_pro/page/photo_preview.dart';
import 'package:lewei_pro/widget/route.dart';
import 'package:photo_manager/photo_manager.dart';
import 'package:get/get.dart';

class PhotoListPage extends StatefulWidget {
  const PhotoListPage({Key? key}) : super(key: key);

  @override
  PhotoListPageState createState() => PhotoListPageState();
}

class PhotoListPageState extends State<PhotoListPage> with AutomaticKeepAliveClientMixin {
  // List<FileSystemEntity> files = [];
  List<ImageBean> assets = [];
  bool loadComplete = false;

  bool isSelect = false;

  @override
  Widget build(BuildContext context) {
    return Container(
      color: Colors.black87,
      child: assets.isEmpty && !loadComplete
          ? Center(
              child: Container(
                width: 30,
                height: 30,
                child: const CircularProgressIndicator(color: Colors.white),
              ),
            )
          : GridView.builder(
              itemCount: assets.length,
              gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                crossAxisCount: MediaQuery.of(context).size.height > MediaQuery.of(context).size.width ? 3 : 6,
                crossAxisSpacing: 5,
                mainAxisSpacing: 5,
                childAspectRatio: 1,
              ),
              itemBuilder: (BuildContext context, int index) {
                return Stack(
                  children: [
                    RepaintBoundary(
                      child: MaterialButton(
                        minWidth: 0,
                        padding: EdgeInsets.zero,
                        onPressed: () {
                          Navigator.push(context, CustomRoute(PhotoPreview(assets, index)));
                        },
                        child: Hero(tag: assets[index].assetEntity.id, child: PhotoItem(entity: assets[index].assetEntity, size: 150)),
                      ),
                    ),
                    Positioned(
                      top: 0,
                      right: 0,
                      child: Visibility(
                        visible: isSelect,
                        child:CupertinoButton(
                          onPressed: (){
                            assets[index].isSelect = !assets[index].isSelect;
                            setState(() {});
                          },
                          child:  Icon(
                            assets[index].isSelect ? Icons.check_circle_sharp : CupertinoIcons.circle,
                            color: assets[index].isSelect ? Colors.blue : Colors.white,
                            size: 30,
                          ),
                        )
                      ),
                    ),
                  ],
                );

                ///
                // return MaterialButton(
                //   minWidth: 0,
                //   padding: EdgeInsets.zero,
                //   onPressed: () {
                //     Navigator.push(context, CustomRoute(PhotoPreview(files, index)));
                //   },
                //   child: Hero(tag: files[index].path, child: ExtendedImage.file(File(files[index].path), fit: BoxFit.cover, scale: 0.1, compressionRatio: 0.1,)),
                // );
              }),
    );
  }

  Future<void> imageHandler() async {
    if (isSelect) {
      await showDialog(
          context: context,
          builder: (BuildContext context) {
            return Center(
              child: Container(
                color: Colors.grey,
                padding: EdgeInsets.fromLTRB(30, 20, 30, 10),
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Text('delete', style: TextStyle(color: Colors.white)),
                    const SizedBox(height: 30),
                    Text('Do you want to delete file?', style: TextStyle(color: Colors.white)),
                    const SizedBox(height: 30),
                    Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        MaterialButton(
                            onPressed: () {
                              ///
                              assets.removeWhere((element) {
                                if (isSelect) {
                                  element.assetEntity.file.then((value) async {
                                    if (value != null && await value.exists()) {
                                      try {
                                        await value.delete();
                                        LwApi.getInstance().refreshPhoto(value.path);
                                      } catch (e) {
                                        print(e);
                                      }
                                    }
                                  });
                                }
                                return element.isSelect;
                              });
                              setState(() {});

                              Get.back();
                            },
                            child: const Text('YES', style: TextStyle(color: Colors.white))),
                        MaterialButton(
                            onPressed: () {
                              Get.back();
                            },
                            child: Text('NO', style: TextStyle(color: Colors.white))),
                      ],
                    )
                  ],
                ),
              ),
            );
          });
    }
    isSelect = !isSelect;
    setState(() {});
  }

  @override
  void initState() {
    super.initState();
    initData();
  }

  Future<void> initData() async {
    List<AssetPathEntity> list = await PhotoManager.getAssetPathList(filterOption: FilterOptionGroup(containsPathModified: true));
    List<AssetPathEntity> pathEntity = list.where((element) => element.name == 'Photos').toList();
    await Future.forEach<AssetPathEntity>(pathEntity, (element) async {
      List<AssetEntity> entities = await element.assetList;
      if (entities.isNotEmpty && entities.first.relativePath == 'LW_FPV/Photos/') {
        assets.addAll(entities.map((e) => ImageBean(e, false)).toList());
      }
    });

    ///

    print('123');
    //
    // String path = await LwApi.getInstance().getPhotoPath();
    // Directory directory = Directory(path);
    // if (!await directory.exists()) {
    //   await directory.create();
    // }
    //
    // directory.listSync().removeWhere((element) {
    //   String pathUpper = element.path.toLowerCase();
    //   return !(pathUpper.endsWith('bmp') || pathUpper.endsWith('jpg') || pathUpper.endsWith('png'));
    // });
    //
    // files.clear();
    // files.addAll(directory.listSync());
    loadComplete = true;
    if (mounted) setState(() {});
  }

  @override
  bool get wantKeepAlive => true;
}

class ImageBean {
  AssetEntity assetEntity;
  bool isSelect;

  ImageBean(this.assetEntity, this.isSelect);
}
