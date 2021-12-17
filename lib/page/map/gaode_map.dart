import 'package:flutter/material.dart';

import 'package:amap_flutter_map/amap_flutter_map.dart';
import 'package:amap_flutter_base/amap_flutter_base.dart';
import 'package:lewei_pro/api/lw_api.dart';
import 'package:lewei_pro/bean/lw_marker_bean.dart';
import 'package:lewei_pro/contoller/fly_control.dart';
import 'package:get/get.dart';
import 'package:lewei_pro/util/gps_util.dart';
import 'package:lewei_pro/widget/public_dialog.dart';
import 'package:fluttertoast/fluttertoast.dart';

class GaodeMap extends StatefulWidget {
  const GaodeMap({Key? key}) : super(key: key);

  @override
  GaodeMapState createState() => GaodeMapState();
}

class GaodeMapState extends State<GaodeMap> {
  double? latitude;
  double? longitude;
  AMapController? mapController;

  // GlobalKey key = GlobalKey();
  final FlyController model = Get.find();
  List<LwMarkerBean> markers = [];

  @override
  Widget build(BuildContext context) {
    return AMapWidget(
      initialCameraPosition: CameraPosition(target: latitude == null ? const LatLng(39.909187, 116.397451) : LatLng(latitude!, longitude!), zoom: 19),
      // key: key,
      scaleEnabled: false,
      //比例尺是否显示
      apiKey: const AMapApiKey(androidKey: '9af5cadf0a9964894834872018458d28'),
      onMapCreated: onMapCreated,
      mapType: getMapType(),
      touchPoiEnabled: false,
      onTap: (LatLng latLng) {
        if (model.drawType == 1 || model.drawType == 4) {
          mapController?.addMarker(latitude ?? 39.909187, longitude ?? 116.397451, latLng.latitude, latLng.longitude, 50.0, needClear: model.drawType == 4).then(getMarkers);
        }
      },
    );
  }

  MapType getMapType() {
    switch (model.mapType) {
      case 1:
        return MapType.normal;
      case 2:
        return MapType.satellite;
      case 3:
        return MapType.night;
      default:
        return MapType.normal;
    }
  }

  ///添加玩marker返回的数据处理
  void getMarkers(String? value) {
    if (value!.contains('error')) {
      if (value.contains('error:1001')) {
        PublicDialog.showTipDialog(context, '警告', '超出航点最大半径范围');
      } else if (value.contains('error:1002')) {
        Fluttertoast.showToast(msg: '超出航点个数的最大值！');
      }

      value = value.substring(9, value.length);
    }

    List<String> splits = value.replaceAll('[', '').replaceAll(')]', '').replaceAll('lat/lng: (', '').split('),');
    markers.clear();
    markers.addAll(splits.map((e) {
      List<String> s = e.split(',');
      return LwMarkerBean(FlyController.getInstance().defaultHeight, FlyController.getInstance().time, FlyController.getInstance().speed, double.parse(s[0]), double.parse(s[1]));
    }).toList());
  }

  void onMapCreated(AMapController controller) {
    controller.setOnMarkerClickListener();
    controller.onLwMarkerTap((LatLng latLng) {
      ///右下角弹出 marker的信息提示框

      LwMarkerBean? bean;
      int index = 0;
      for (int i = 0; i < markers.length; i++) {
        if (markers[i].latitude == latLng.latitude && markers[i].longitude <= latLng.longitude + 0.00000000000010 && markers[i].longitude >= latLng.longitude - 0.00000000000010) {
          index = i;
          bean = markers[i];
          break;
        }
      }
      if (bean == null) return;

      PublicDialog.showMarkerDetailDialog(context, '航点${index + 1}', bean.height).then((value) {
        markers[index].height = value;
      });
    });

    // setState(() {
    mapController = controller;
    //   getApprovalNumber();
    // });
  }

  void getApprovalNumber() async {
    //普通地图审图号
    String? mapContentApprovalNumber = await mapController?.getMapContentApprovalNumber();
    //卫星地图审图号
    String? satelliteImageApprovalNumber = await mapController?.getSatelliteImageApprovalNumber();
  }

  moveToCurrentLocation(double lat, double lng) {
    latitude = lat;
    longitude = lng;
    mapController?.moveCamera(CameraUpdate.newLatLng(LatLng(latitude!, longitude!)));
    mapController?.addCircle(latitude!, longitude!, 50);
  }

  onPanStart(int x, int y) {
    if (model.drawType != 0) return;
    mapController?.drawTrailFromScreenLocation(latitude ?? 39.909187, longitude ?? 116.397451, x, y, 50.0, needClear: true).then(getMarkers);
  }

  onPanUpdate(int x, int y) {
    if (model.drawType != 0) return;
    mapController?.drawTrailFromScreenLocation(latitude ?? 39.909187, longitude ?? 116.397451, x, y, 50.0).then(getMarkers);
  }

  clearMarker() {
    markers.clear();
    mapController?.clearMarker();
  }

  setTrackRouteControlData() {
    mapController?.setMarkerClickToDel(false);
    if (markers.isNotEmpty) {
      LwApi.getInstance().setTrackRouteControlData(markers.map((e) => e.toJson(isGaodeLatLng: true)).toList());
    }
  }

  setFlyCircleControlData() {
    mapController?.setMarkerClickToDel(false);
    if (markers.isNotEmpty) {
      double lat = markers[0].latitude;
      double lng = markers[0].longitude;
      List<num> gps84 = GpsUtil.gcj02_To_Gps84(lat, lng);
      lat = gps84[0].toDouble();
      lng = gps84[1].toDouble();
      LwApi.getInstance().setFlyCircleControlData(lat, lng);
    }
  }

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance!.addPostFrameCallback((Duration duration) async {
      // setState(() {
      //   key = GlobalKey();
      // });
    });
  }

  @override
  void dispose() {
    super.dispose();
    mapController?.disponse();
  }
}
