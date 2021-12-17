import 'package:lewei_pro/util/gps_util.dart';

class LwMarkerBean {
  int height;
  int time;
  int speed;
  double latitude;
  double longitude;

  // LatLng latLng;

  LwMarkerBean(this.height, this.time, this.speed, this.latitude, this.longitude);

  Map<String, dynamic> toJson({bool isGaodeLatLng = true}) {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['height'] = height;
    data['time'] = time;
    data['speed'] = speed;
    if(!isGaodeLatLng){
      data['latitude'] = latitude;
      data['longitude'] = longitude;
    }else{
      List<num> gps84 = GpsUtil.gcj02_To_Gps84(latitude, longitude);
      data['latitude'] = gps84[0];
      data['longitude'] = gps84[1];
    }

    return data;
  }
}
