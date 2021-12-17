class FlyLogBean {
  late String heightTime; // 记录时间
  late int flyMode;
  late int flyState;
  late int flyBattery;
  late double liftingSpeed;
  late double speed;

  late double height;
  late double distance;

  late double elevation;
  late double deflection;
  late int starNumber;
  late double accuracy;

  late double airLatitude; // 飞机坐标
  late double airLongitude; // 飞机坐标

  bool isSelect = false;

  FlyLogBean.fromJson(Map<String, dynamic> json) {
    heightTime = json['height_time'];
    flyMode = json['fly_mode'];
    flyState = json['fly_state'];
    flyBattery = json['fly_bettery'];
    liftingSpeed = json['lifting_speed'];
    speed = json['speed'];
    height = json['height'];
    distance = json['distance'];
    elevation = json['elevation'];
    deflection = json['deflection'];
    starNumber = json['star_number'];
    accuracy = json['accuracy'];
    airLatitude = json['air_latitude'];
    airLongitude = json['air_longitude'];
  }
}
