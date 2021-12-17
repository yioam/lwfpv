import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:lewei_pro/api/lw_api.dart';

import 'package:lewei_pro/bean/lw_marker_bean.dart';
import 'package:lewei_pro/contoller/fly_control.dart';
import 'package:lewei_pro/util/gps_util.dart';
import 'package:lewei_pro/widget/public_dialog.dart';
import 'package:get/get.dart';
import 'package:fluttertoast/fluttertoast.dart';

class GMap extends StatefulWidget {
  const GMap({Key? key}) : super(key: key);

  @override
  GMapState createState() => GMapState();
}

class GMapState extends State<GMap> {
  double? latitude;
  double? longitude;
  List<LwMarkerBean> markers = [];
  final FlyController model = Get.find();

  GoogleMapController? mapController;

  @override
  Widget build(BuildContext context) {
    return GoogleMap(
      initialCameraPosition: CameraPosition(
        target: latitude == null ? const LatLng(39.909187, 116.397451) : LatLng(latitude!, longitude!),
        zoom: 18,
      ),
      onMapCreated: onMapCreated,
      compassEnabled: false,
      mapType: getMapType(),
      zoomControlsEnabled: false,


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
        return MapType.normal;
      default:
        return MapType.normal;
    }
  }

  void onMapCreated(GoogleMapController controller) {
    mapController = controller;
    mapController?.addCircle(latitude ?? 39.909187, longitude ?? 116.397451, 50);
    mapController?.setOnMarkerClickListener();
    mapController?.onLwMarkerTap((LatLng latLng) {
      ///右下角弹出 marker的信息提示框

      LwMarkerBean? bean;
      int index = 0;
      for (int i = 0; i < markers.length; i++) {///longitude最后以为精度丢失了。。
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

    setState(() {
      mapController = controller;
    });
  }

  moveToCurrentLocation(double lat, double lng){
    List<num> gps84 = GpsUtil.gcj02_To_Gps84(lat, lng);

    latitude = gps84[0].toDouble();
    longitude = gps84[1].toDouble();
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

  clearMarker(){
    markers.clear();
    mapController?.clearMarker();
  }

  setTrackRouteControlData(){
    mapController?.setMarkerClickToDel(false);
    if (markers.isNotEmpty) {
      LwApi.getInstance().setTrackRouteControlData(markers.map((e) => e.toJson(isGaodeLatLng: false)).toList());
    }
  }

  setFlyCircleControlData(){
    mapController?.setMarkerClickToDel(false);
    if (markers.isNotEmpty) {
      double lat = markers[0].latitude;
      double lng = markers[0].longitude;
      LwApi.getInstance().setFlyCircleControlData(lat, lng);
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
}
