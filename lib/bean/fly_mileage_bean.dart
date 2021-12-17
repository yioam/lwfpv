class FlyMileage {
  late int flyNumberId; //
  late String mileageTime; // 记录时间
  late double mileage; // 里程
  late double height; // 高度
  late String duration; // 时长
  late double airLatitude;
  late double airLongitude;


  FlyMileage.fromJson(Map<String, dynamic> json) {
    flyNumberId = json['fly_number_id'];
    mileageTime = json['mileage_time'];
    mileage = json['mileage'];
    height = json['height'];
    duration = json['duration'];
    airLatitude = json['air_latitude'];
    airLongitude = json['air_longitude'];
  }

}