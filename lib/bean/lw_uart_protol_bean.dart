import 'package:get/get.dart';

class LwUartProtolBean extends GetxController {
  LwUartProtol? lwUartProtol;
  var test = 0.obs;

  LwUartProtolBean({this.lwUartProtol});

  LwUartProtolBean.fromJson(Map<String, dynamic> json) {
    lwUartProtol = json['lwUartProtol'] != null ? LwUartProtol.fromJson(json['lwUartProtol']) : null;
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    if (lwUartProtol != null) {
      data['lwUartProtol'] = lwUartProtol!.toJson();
    }
    return data;
  }
}

class LwUartProtol {

  MControlPara? mControlPara;
  MFlyInfo? mFlyInfo;
  MFlyTestInfo? mFlyTestInfo;

  LwUartProtol({this.mControlPara, this.mFlyInfo, this.mFlyTestInfo});

  LwUartProtol.fromJson(Map<String, dynamic> json) {
    mControlPara = json['mControlPara'] != null ? MControlPara.fromJson(json['mControlPara']) : null;
    mFlyInfo = json['mFlyInfo'] != null ? MFlyInfo.fromJson(json['mFlyInfo']) : null;
    mFlyTestInfo = json['mFlyTestInfo'] != null ? MFlyTestInfo.fromJson(json['mFlyTestInfo']) : null;
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    if (mControlPara != null) {
      data['mControlPara'] = mControlPara!.toJson();
    }
    if (mFlyInfo != null) {
      data['mFlyInfo'] = mFlyInfo!.toJson();
    }
    if (mFlyTestInfo != null) {
      data['mFlyTestInfo'] = mFlyTestInfo!.toJson();
    }
    return data;
  }
}

class MControlPara {
  num? gPSAuthorizationStatusDenied;
  num? gpsMode;
  num? gpsSpeed;
  num? mFTest;
  num? pointSendFlag;
  num? seqNum;
  num? wSControlDataheartTime;
  num? wSGetDataHeartTime;
  num? accCalibrate;
  ActionPara? actionPara;
  num? altHold;
  num? autoLanding;
  num? autoTakeoff;
  num? batLowBalGet;
  num? batLowBalSet;
  num? beginnerMode;
  num? circleFly;
  CirclePara? circlePara;
  num? circleSendFlag;
  num? commantCount;
  String? ctlmode;
  num? droneLed;
  num? enumType;
  FlyParaInfo? flyParaInfo;
  num? followFly;
  num? followMode;
  num? followSendFlag;
  num? followTime;
  num? followType;
  num? geoCalibrate;
  num? getFlyInfo;
  num? handTack;
  num? header;
  num? headless;
  num? heartTime;
  num? homeward;
  num? horizontalAccuracy;
  num? hover;
  num? indoorMode;
  num? joystickOn;
  JoystickPara? joystickPara;
  num? multiProtolType;
  num? photo;
  num? pointClear;
  num? pointCout;
  num? pointFly;
  num? pointNum;
  num? pointget;
  num? pointset;
  num? recordCircle;
  num? recording;
  num? roll;
  num? saveCommandTime;
  num? saveSendTwoSecondTime;
  num? shoot;
  num? shortVideoTypeInt;
  num? snap;
  num? speed;
  num? startFlag;
  num? stop;
  String? supportGLGPSType;
  num? test;
  String? uartProtol;
  num? unlock;
  Center? userCoordinate;
  num? video;
  VisionPara? visionPara;

  MControlPara(
      {this.gPSAuthorizationStatusDenied,
      this.gpsMode,
      this.gpsSpeed,
      this.mFTest,
      this.pointSendFlag,
      this.seqNum,
      this.wSControlDataheartTime,
      this.wSGetDataHeartTime,
      this.accCalibrate,
      this.actionPara,
      this.altHold,
      this.autoLanding,
      this.autoTakeoff,
      this.batLowBalGet,
      this.batLowBalSet,
      this.beginnerMode,
      this.circleFly,
      this.circlePara,
      this.circleSendFlag,
      this.commantCount,
      this.ctlmode,
      this.droneLed,
      this.enumType,
      this.flyParaInfo,
      this.followFly,
      this.followMode,
      this.followSendFlag,
      this.followTime,
      this.followType,
      this.geoCalibrate,
      this.getFlyInfo,
      this.handTack,
      this.header,
      this.headless,
      this.heartTime,
      this.homeward,
      this.horizontalAccuracy,
      this.hover,
      this.indoorMode,
      this.joystickOn,
      this.joystickPara,
      this.multiProtolType,
      this.photo,
      this.pointClear,
      this.pointCout,
      this.pointFly,
      this.pointNum,
      this.pointget,
      this.pointset,
      this.recordCircle,
      this.recording,
      this.roll,
      this.saveCommandTime,
      this.saveSendTwoSecondTime,
      this.shoot,
      this.shortVideoTypeInt,
      this.snap,
      this.speed,
      this.startFlag,
      this.stop,
      this.supportGLGPSType,
      this.test,
      this.uartProtol,
      this.unlock,
      this.userCoordinate,
      this.video,
      this.visionPara});

  MControlPara.fromJson(Map<String, dynamic> json) {
    gPSAuthorizationStatusDenied = json['GPSAuthorizationStatusDenied'];
    gpsMode = json['GpsMode'];
    gpsSpeed = json['GpsSpeed'];
    mFTest = json['MFTest'];
    pointSendFlag = json['PointSendFlag'];
    seqNum = json['SeqNum'];
    wSControlDataheartTime = json['WSControlDataheartTime'];
    wSGetDataHeartTime = json['WSGetDataHeartTime'];
    accCalibrate = json['accCalibrate'];
    actionPara = json['actionPara'] != null ? ActionPara.fromJson(json['actionPara']) : null;
    altHold = json['altHold'];
    autoLanding = json['autoLanding'];
    autoTakeoff = json['autoTakeoff'];
    batLowBalGet = json['batLowBalGet'];
    batLowBalSet = json['batLowBalSet'];
    beginnerMode = json['beginnerMode'];
    circleFly = json['circleFly'];
    circlePara = json['circlePara'] != null ? CirclePara.fromJson(json['circlePara']) : null;
    circleSendFlag = json['circleSendFlag'];
    commantCount = json['commantCount'];
    ctlmode = json['ctlmode'];
    droneLed = json['droneLed'];
    enumType = json['enumType'];
    flyParaInfo = json['flyParaInfo'] != null ? FlyParaInfo.fromJson(json['flyParaInfo']) : null;
    followFly = json['followFly'];
    followMode = json['followMode'];
    followSendFlag = json['followSendFlag'];
    followTime = json['followTime'];
    followType = json['followType'];
    geoCalibrate = json['geoCalibrate'];
    getFlyInfo = json['getFlyInfo'];
    handTack = json['handTack'];
    header = json['header'];
    headless = json['headless'];
    heartTime = json['heartTime'];
    homeward = json['homeward'];
    horizontalAccuracy = json['horizontalAccuracy'];
    hover = json['hover'];
    indoorMode = json['indoorMode'];
    joystickOn = json['joystickOn'];
    joystickPara = json['joystickPara'] != null ? JoystickPara.fromJson(json['joystickPara']) : null;
    multiProtolType = json['multiProtolType'];
    photo = json['photo'];
    pointClear = json['pointClear'];
    pointCout = json['pointCout'];
    pointFly = json['pointFly'];
    pointNum = json['pointNum'];
    pointget = json['pointget'];
    pointset = json['pointset'];
    recordCircle = json['recordCircle'];
    recording = json['recording'];
    roll = json['roll'];
    saveCommandTime = json['saveCommandTime'];
    saveSendTwoSecondTime = json['saveSendTwoSecondTime'];
    shoot = json['shoot'];
    shortVideoTypeInt = json['shortVideoType_int'];
    snap = json['snap'];
    speed = json['speed'];
    startFlag = json['startFlag'];
    stop = json['stop'];
    supportGLGPSType = json['supportGLGPSType'];
    test = json['test'];
    uartProtol = json['uartProtol'];
    unlock = json['unlock'];
    userCoordinate = json['userCoordinate'] != null ? Center.fromJson(json['userCoordinate']) : null;
    video = json['video'];
    visionPara = json['visionPara'] != null ? VisionPara.fromJson(json['visionPara']) : null;
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['GPSAuthorizationStatusDenied'] = gPSAuthorizationStatusDenied;
    data['GpsMode'] = gpsMode;
    data['GpsSpeed'] = gpsSpeed;
    data['MFTest'] = mFTest;
    data['PointSendFlag'] = pointSendFlag;
    data['SeqNum'] = seqNum;
    data['WSControlDataheartTime'] = wSControlDataheartTime;
    data['WSGetDataHeartTime'] = wSGetDataHeartTime;
    data['accCalibrate'] = accCalibrate;
    if (actionPara != null) {
      data['actionPara'] = actionPara!.toJson();
    }
    data['altHold'] = altHold;
    data['autoLanding'] = autoLanding;
    data['autoTakeoff'] = autoTakeoff;
    data['batLowBalGet'] = batLowBalGet;
    data['batLowBalSet'] = batLowBalSet;
    data['beginnerMode'] = beginnerMode;
    data['circleFly'] = circleFly;
    if (circlePara != null) {
      data['circlePara'] = circlePara!.toJson();
    }
    data['circleSendFlag'] = circleSendFlag;
    data['commantCount'] = commantCount;
    data['ctlmode'] = ctlmode;
    data['droneLed'] = droneLed;
    data['enumType'] = enumType;
    if (flyParaInfo != null) {
      data['flyParaInfo'] = flyParaInfo!.toJson();
    }
    data['followFly'] = followFly;
    data['followMode'] = followMode;
    data['followSendFlag'] = followSendFlag;
    data['followTime'] = followTime;
    data['followType'] = followType;
    data['geoCalibrate'] = geoCalibrate;
    data['getFlyInfo'] = getFlyInfo;
    data['handTack'] = handTack;
    data['header'] = header;
    data['headless'] = headless;
    data['heartTime'] = heartTime;
    data['homeward'] = homeward;
    data['horizontalAccuracy'] = horizontalAccuracy;
    data['hover'] = hover;
    data['indoorMode'] = indoorMode;
    data['joystickOn'] = joystickOn;
    if (joystickPara != null) {
      data['joystickPara'] = joystickPara!.toJson();
    }
    data['multiProtolType'] = multiProtolType;
    data['photo'] = photo;
    data['pointClear'] = pointClear;
    data['pointCout'] = pointCout;
    data['pointFly'] = pointFly;
    data['pointNum'] = pointNum;
    data['pointget'] = pointget;
    data['pointset'] = pointset;
    data['recordCircle'] = recordCircle;
    data['recording'] = recording;
    data['roll'] = roll;
    data['saveCommandTime'] = saveCommandTime;
    data['saveSendTwoSecondTime'] = saveSendTwoSecondTime;
    data['shoot'] = shoot;
    data['shortVideoType_int'] = shortVideoTypeInt;
    data['snap'] = snap;
    data['speed'] = speed;
    data['startFlag'] = startFlag;
    data['stop'] = stop;
    data['supportGLGPSType'] = supportGLGPSType;
    data['test'] = test;
    data['uartProtol'] = uartProtol;
    data['unlock'] = unlock;
    if (userCoordinate != null) {
      data['userCoordinate'] = userCoordinate!.toJson();
    }
    data['video'] = video;
    if (visionPara != null) {
      data['visionPara'] = visionPara!.toJson();
    }
    return data;
  }
}

class ActionPara {
  num? actionid;
  num? parameter1;
  num? parameter2;
  num? sn;
  num? style;

  ActionPara({this.actionid, this.parameter1, this.parameter2, this.sn, this.style});

  ActionPara.fromJson(Map<String, dynamic> json) {
    actionid = json['actionid'];
    parameter1 = json['parameter1'];
    parameter2 = json['parameter2'];
    sn = json['sn'];
    style = json['style'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['actionid'] = actionid;
    data['parameter1'] = parameter1;
    data['parameter2'] = parameter2;
    data['sn'] = sn;
    data['style'] = style;
    return data;
  }
}

class CirclePara {
  Center? center;
  num? circleNum;
  num? height;
  num? radius;
  num? speed;

  CirclePara({this.center, this.circleNum, this.height, this.radius, this.speed});

  CirclePara.fromJson(Map<String, dynamic> json) {
    center = json['center'] != null ? Center.fromJson(json['center']) : null;
    circleNum = json['circleNum'];
    height = json['height'];
    radius = json['radius'];
    speed = json['speed'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    if (center != null) {
      data['center'] = center!.toJson();
    }
    data['circleNum'] = circleNum;
    data['height'] = height;
    data['radius'] = radius;
    data['speed'] = speed;
    return data;
  }
}

class Center {
  num? latitude;
  num? longitude;

  Center({this.latitude, this.longitude});

  Center.fromJson(Map<String, dynamic> json) {
    latitude = json['latitude'];
    longitude = json['longitude'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['latitude'] = latitude;
    data['longitude'] = longitude;
    return data;
  }
}

class FlyParaInfo {
  num? batLowVal1;
  num? batLowVal2;
  num? batLowVal3;
  num? maxPoint;
  num? pointspeed;
  num? pointtime;
  num? acstatus;
  num? altitude;
  num? circleHeight;
  num? circleRadius;
  num? flyinfo;
  num? followDistance;
  num? followHeight;
  num? homewardHeight;
  num? limitedHeight;
  num? limitedRadius;
  num? live;
  String? name;
  num? photo;
  num? product;
  num? reserve;
  num? reserve1;
  num? reserve2;
  num? sn;
  num? takeoffHeight;
  String? version;
  num? video;
  num? voltage;

  FlyParaInfo(
      {this.batLowVal1,
      this.batLowVal2,
      this.batLowVal3,
      this.maxPoint,
      this.pointspeed,
      this.pointtime,
      this.acstatus,
      this.altitude,
      this.circleHeight,
      this.circleRadius,
      this.flyinfo,
      this.followDistance,
      this.followHeight,
      this.homewardHeight,
      this.limitedHeight,
      this.limitedRadius,
      this.live,
      this.name,
      this.photo,
      this.product,
      this.reserve,
      this.reserve1,
      this.reserve2,
      this.sn,
      this.takeoffHeight,
      this.version,
      this.video,
      this.voltage});

  FlyParaInfo.fromJson(Map<String, dynamic> json) {
    batLowVal1 = json['BatLowVal1'];
    batLowVal2 = json['BatLowVal2'];
    batLowVal3 = json['BatLowVal3'];
    maxPoint = json['MaxPoint'];
    pointspeed = json['Pointspeed'];
    pointtime = json['Pointtime'];
    acstatus = json['acstatus'];
    altitude = json['altitude'];
    circleHeight = json['circleHeight'];
    circleRadius = json['circleRadius'];
    flyinfo = json['flyinfo'];
    followDistance = json['followDistance'];
    followHeight = json['followHeight'];
    homewardHeight = json['homewardHeight'];
    limitedHeight = json['limitedHeight'];
    limitedRadius = json['limitedRadius'];
    live = json['live'];
    name = json['name'];
    photo = json['photo'];
    product = json['product'];
    reserve = json['reserve'];
    reserve1 = json['reserve1'];
    reserve2 = json['reserve2'];
    sn = json['sn'];
    takeoffHeight = json['takeoffHeight'];
    version = json['version'];
    video = json['video'];
    voltage = json['voltage'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['BatLowVal1'] = batLowVal1;
    data['BatLowVal2'] = batLowVal2;
    data['BatLowVal3'] = batLowVal3;
    data['MaxPoint'] = maxPoint;
    data['Pointspeed'] = pointspeed;
    data['Pointtime'] = pointtime;
    data['acstatus'] = acstatus;
    data['altitude'] = altitude;
    data['circleHeight'] = circleHeight;
    data['circleRadius'] = circleRadius;
    data['flyinfo'] = flyinfo;
    data['followDistance'] = followDistance;
    data['followHeight'] = followHeight;
    data['homewardHeight'] = homewardHeight;
    data['limitedHeight'] = limitedHeight;
    data['limitedRadius'] = limitedRadius;
    data['live'] = live;
    data['name'] = name;
    data['photo'] = photo;
    data['product'] = product;
    data['reserve'] = reserve;
    data['reserve1'] = reserve1;
    data['reserve2'] = reserve2;
    data['sn'] = sn;
    data['takeoffHeight'] = takeoffHeight;
    data['version'] = version;
    data['video'] = video;
    data['voltage'] = voltage;
    return data;
  }
}

class JoystickPara {
  num? aileron;
  num? aileronTrim;
  num? elvator;
  num? elvatorTrim;
  num? ptzH;
  num? ptzV;
  num? rudder;
  num? rudderTrim;
  num? throttle;

  JoystickPara({this.aileron, this.aileronTrim, this.elvator, this.elvatorTrim, this.ptzH, this.ptzV, this.rudder, this.rudderTrim, this.throttle});

  JoystickPara.fromJson(Map<String, dynamic> json) {
    aileron = json['aileron'];
    aileronTrim = json['aileronTrim'];
    elvator = json['elvator'];
    elvatorTrim = json['elvatorTrim'];
    ptzH = json['ptz_h'];
    ptzV = json['ptz_v'];
    rudder = json['rudder'];
    rudderTrim = json['rudderTrim'];
    throttle = json['throttle'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['aileron'] = aileron;
    data['aileronTrim'] = aileronTrim;
    data['elvator'] = elvator;
    data['elvatorTrim'] = elvatorTrim;
    data['ptz_h'] = ptzH;
    data['ptz_v'] = ptzV;
    data['rudder'] = rudder;
    data['rudderTrim'] = rudderTrim;
    data['throttle'] = throttle;
    return data;
  }
}

class VisionPara {
  num? autoMode;
  num? bmpHeight;
  num? bmpWidth;
  num? disElvatorOffset;
  num? elvatorXmax;
  num? elvatorXmin;
  num? elvatorYcenter;
  num? elvatorYmax;
  num? elvatorYmin;
  num? limitedMaxH;
  num? limitedMinH;
  Rect? rect;
  num? resultTime;
  num? reverseInterval;
  num? rudderOffset;
  num? rudderSearch;
  num? searchRudderDuration;
  num? searchStartTime;
  num? start;
  num? supportSearch;
  num? throttleOffset;
  num? throttleSeatch;
  num? trackElvatorSpeed;
  num? trackFollowState;
  num? trackRudderSpeed;

  VisionPara(
      {this.autoMode,
      this.bmpHeight,
      this.bmpWidth,
      this.disElvatorOffset,
      this.elvatorXmax,
      this.elvatorXmin,
      this.elvatorYcenter,
      this.elvatorYmax,
      this.elvatorYmin,
      this.limitedMaxH,
      this.limitedMinH,
      this.rect,
      this.resultTime,
      this.reverseInterval,
      this.rudderOffset,
      this.rudderSearch,
      this.searchRudderDuration,
      this.searchStartTime,
      this.start,
      this.supportSearch,
      this.throttleOffset,
      this.throttleSeatch,
      this.trackElvatorSpeed,
      this.trackFollowState,
      this.trackRudderSpeed});

  VisionPara.fromJson(Map<String, dynamic> json) {
    autoMode = json['autoMode'];
    bmpHeight = json['bmpHeight'];
    bmpWidth = json['bmpWidth'];
    disElvatorOffset = json['disElvatorOffset'];
    elvatorXmax = json['elvatorXmax'];
    elvatorXmin = json['elvatorXmin'];
    elvatorYcenter = json['elvatorYcenter'];
    elvatorYmax = json['elvatorYmax'];
    elvatorYmin = json['elvatorYmin'];
    limitedMaxH = json['limitedMaxH'];
    limitedMinH = json['limitedMinH'];
    rect = json['rect'] != null ? Rect.fromJson(json['rect']) : null;
    resultTime = json['resultTime'];
    reverseInterval = json['reverseInterval'];
    rudderOffset = json['rudderOffset'];
    rudderSearch = json['rudderSearch'];
    searchRudderDuration = json['searchRudderDuration'];
    searchStartTime = json['searchStartTime'];
    start = json['start'];
    supportSearch = json['supportSearch'];
    throttleOffset = json['throttleOffset'];
    throttleSeatch = json['throttleSeatch'];
    trackElvatorSpeed = json['trackElvatorSpeed'];
    trackFollowState = json['trackFollowState'];
    trackRudderSpeed = json['trackRudderSpeed'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['autoMode'] = autoMode;
    data['bmpHeight'] = bmpHeight;
    data['bmpWidth'] = bmpWidth;
    data['disElvatorOffset'] = disElvatorOffset;
    data['elvatorXmax'] = elvatorXmax;
    data['elvatorXmin'] = elvatorXmin;
    data['elvatorYcenter'] = elvatorYcenter;
    data['elvatorYmax'] = elvatorYmax;
    data['elvatorYmin'] = elvatorYmin;
    data['limitedMaxH'] = limitedMaxH;
    data['limitedMinH'] = limitedMinH;
    if (rect != null) {
      data['rect'] = rect!.toJson();
    }
    data['resultTime'] = resultTime;
    data['reverseInterval'] = reverseInterval;
    data['rudderOffset'] = rudderOffset;
    data['rudderSearch'] = rudderSearch;
    data['searchRudderDuration'] = searchRudderDuration;
    data['searchStartTime'] = searchStartTime;
    data['start'] = start;
    data['supportSearch'] = supportSearch;
    data['throttleOffset'] = throttleOffset;
    data['throttleSeatch'] = throttleSeatch;
    data['trackElvatorSpeed'] = trackElvatorSpeed;
    data['trackFollowState'] = trackFollowState;
    data['trackRudderSpeed'] = trackRudderSpeed;
    return data;
  }
}

class Rect {
  num? objectHeight;
  num? objectWidth;
  num? objectX;
  num? objectY;

  Rect({this.objectHeight, this.objectWidth, this.objectX, this.objectY});

  Rect.fromJson(Map<String, dynamic> json) {
    objectHeight = json['objectHeight'];
    objectWidth = json['objectWidth'];
    objectX = json['objectX'];
    objectY = json['objectY'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['objectHeight'] = objectHeight;
    data['objectWidth'] = objectWidth;
    data['objectX'] = objectX;
    data['objectY'] = objectY;
    return data;
  }
}

class MFlyInfo {
  num? accCalib;
  num? autoLand;
  num? baroInitOk;
  num? batVal;
  num? batVal1;
  num? batVal2;
  num? calibProgress;
  num? currOver;
  num? flowInitOk;
  num? gPSAccuracy;
  num? geoCalib;
  num? gpsFine;
  num? gpsInitOk;
  num? gpsNum;
  num? hFParaInfoType;
  num? indoorMode;
  num? insInitOk;
  num? lowBat;
  num? magInitOk;
  num? magNoise;
  num? magXYCalib;
  num? magZCalib;
  num? rFFollow;
  num? rcFastMode;
  num? tempOver;
  num? videoOn;
  Attitude? attitude;
  num? circleFlyOk;
  Center? coordinate;
  num? distant;
  num? droneLedStatus;
  num? droneVersionSync;
  num? fire;
  num? flyBeginnerMode;
  GlFlightRcord? glFlightRcord;
  num? headless;
  num? height;
  num? mFlyMode;
  num? mFlySate;
  num? modeGps;
  FlyParaInfo? paraInfo;
  num? paraInfoSetOK;
  num? paraInfoSync;
  num? pointFlyOk;
  num? preBatteryValue;
  num? rockerBal;
  num? rockerLink;
  num? rockerLowBat;
  num? rockerSignal;
  num? shockproofness;
  num? speed;
  num? unLowHomeward;
  num? updateTime;
  num? velocity;
  num? versionSyncOK;
  num? versionSyncProgress;

  MFlyInfo(
      {this.accCalib,
      this.autoLand,
      this.baroInitOk,
      this.batVal,
      this.batVal1,
      this.batVal2,
      this.calibProgress,
      this.currOver,
      this.flowInitOk,
      this.gPSAccuracy,
      this.geoCalib,
      this.gpsFine,
      this.gpsInitOk,
      this.gpsNum,
      this.hFParaInfoType,
      this.indoorMode,
      this.insInitOk,
      this.lowBat,
      this.magInitOk,
      this.magNoise,
      this.magXYCalib,
      this.magZCalib,
      this.rFFollow,
      this.rcFastMode,
      this.tempOver,
      this.videoOn,
      this.attitude,
      this.circleFlyOk,
      this.coordinate,
      this.distant,
      this.droneLedStatus,
      this.droneVersionSync,
      this.fire,
      this.flyBeginnerMode,
      this.glFlightRcord,
      this.headless,
      this.height,
      this.mFlyMode,
      this.mFlySate,
      this.modeGps,
      this.paraInfo,
      this.paraInfoSetOK,
      this.paraInfoSync,
      this.pointFlyOk,
      this.preBatteryValue,
      this.rockerBal,
      this.rockerLink,
      this.rockerLowBat,
      this.rockerSignal,
      this.shockproofness,
      this.speed,
      this.unLowHomeward,
      this.updateTime,
      this.velocity,
      this.versionSyncOK,
      this.versionSyncProgress});

  MFlyInfo.fromJson(Map<String, dynamic> json) {
    accCalib = json['AccCalib'];
    autoLand = json['AutoLand'];
    baroInitOk = json['BaroInitOk'];
    batVal = json['BatVal'];
    batVal1 = json['BatVal1'];
    batVal2 = json['BatVal2'];
    calibProgress = json['CalibProgress'];
    currOver = json['CurrOver'];
    flowInitOk = json['FlowInitOk'];
    gPSAccuracy = json['GPSAccuracy'];
    geoCalib = json['GeoCalib'];
    gpsFine = json['GpsFine'];
    gpsInitOk = json['GpsInitOk'];
    gpsNum = json['GpsNum'];
    hFParaInfoType = json['HFParaInfoType'];
    indoorMode = json['IndoorMode'];
    insInitOk = json['InsInitOk'];
    lowBat = json['LowBat'];
    magInitOk = json['MagInitOk'];
    magNoise = json['MagNoise'];
    magXYCalib = json['MagXYCalib'];
    magZCalib = json['MagZCalib'];
    rFFollow = json['RF_follow'];
    rcFastMode = json['RcFastMode'];
    tempOver = json['TempOver'];
    videoOn = json['VideoOn'];
    attitude = json['attitude'] != null ? Attitude.fromJson(json['attitude']) : null;
    circleFlyOk = json['circleFlyOk'];
    coordinate = json['coordinate'] != null ? Center.fromJson(json['coordinate']) : null;
    distant = json['distant'];
    droneLedStatus = json['droneLedStatus'];
    droneVersionSync = json['droneVersionSync'];
    fire = json['fire'];
    flyBeginnerMode = json['flyBeginnerMode'];
    glFlightRcord = json['glFlightRcord'] != null ? GlFlightRcord.fromJson(json['glFlightRcord']) : null;
    headless = json['headless'];
    height = json['height'];
    mFlyMode = json['mFlyMode'];
    mFlySate = json['mFlySate'];
    modeGps = json['mode_gps'];
    paraInfo = json['paraInfo'] != null ? FlyParaInfo.fromJson(json['paraInfo']) : null;
    paraInfoSetOK = json['paraInfoSetOK'];
    paraInfoSync = json['paraInfoSync'];
    pointFlyOk = json['pointFlyOk'];
    preBatteryValue = json['preBatteryValue'];
    rockerBal = json['rockerBal'];
    rockerLink = json['rockerLink'];
    rockerLowBat = json['rockerLowBat'];
    rockerSignal = json['rockerSignal'];
    shockproofness = json['shockproofness'];
    speed = json['speed'];
    unLowHomeward = json['unLowHomeward'];
    updateTime = json['update_time'];
    velocity = json['velocity'];
    versionSyncOK = json['versionSyncOK'];
    versionSyncProgress = json['versionSyncProgress'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['AccCalib'] = accCalib;
    data['AutoLand'] = autoLand;
    data['BaroInitOk'] = baroInitOk;
    data['BatVal'] = batVal;
    data['BatVal1'] = batVal1;
    data['BatVal2'] = batVal2;
    data['CalibProgress'] = calibProgress;
    data['CurrOver'] = currOver;
    data['FlowInitOk'] = flowInitOk;
    data['GPSAccuracy'] = gPSAccuracy;
    data['GeoCalib'] = geoCalib;
    data['GpsFine'] = gpsFine;
    data['GpsInitOk'] = gpsInitOk;
    data['GpsNum'] = gpsNum;
    data['HFParaInfoType'] = hFParaInfoType;
    data['IndoorMode'] = indoorMode;
    data['InsInitOk'] = insInitOk;
    data['LowBat'] = lowBat;
    data['MagInitOk'] = magInitOk;
    data['MagNoise'] = magNoise;
    data['MagXYCalib'] = magXYCalib;
    data['MagZCalib'] = magZCalib;
    data['RF_follow'] = rFFollow;
    data['RcFastMode'] = rcFastMode;
    data['TempOver'] = tempOver;
    data['VideoOn'] = videoOn;
    if (attitude != null) {
      data['attitude'] = attitude!.toJson();
    }
    data['circleFlyOk'] = circleFlyOk;
    if (coordinate != null) {
      data['coordinate'] = coordinate!.toJson();
    }
    data['distant'] = distant;
    data['droneLedStatus'] = droneLedStatus;
    data['droneVersionSync'] = droneVersionSync;
    data['fire'] = fire;
    data['flyBeginnerMode'] = flyBeginnerMode;
    if (glFlightRcord != null) {
      data['glFlightRcord'] = glFlightRcord!.toJson();
    }
    data['headless'] = headless;
    data['height'] = height;
    data['mFlyMode'] = mFlyMode;
    data['mFlySate'] = mFlySate;
    data['mode_gps'] = modeGps;
    if (paraInfo != null) {
      data['paraInfo'] = paraInfo!.toJson();
    }
    data['paraInfoSetOK'] = paraInfoSetOK;
    data['paraInfoSync'] = paraInfoSync;
    data['pointFlyOk'] = pointFlyOk;
    data['preBatteryValue'] = preBatteryValue;
    data['rockerBal'] = rockerBal;
    data['rockerLink'] = rockerLink;
    data['rockerLowBat'] = rockerLowBat;
    data['rockerSignal'] = rockerSignal;
    data['shockproofness'] = shockproofness;
    data['speed'] = speed;
    data['unLowHomeward'] = unLowHomeward;
    data['update_time'] = updateTime;
    data['velocity'] = velocity;
    data['versionSyncOK'] = versionSyncOK;
    data['versionSyncProgress'] = versionSyncProgress;
    return data;
  }
}

class GlFlightRcord {
  num? appStopt;
  num? armed;
  num? gyroErrLock;
  num? landLock;
  num? pwmMot0;
  num? pwmMot1;
  num? pwmMot2;
  num? pwmMot3;
  num? rcStopt;

  GlFlightRcord({this.appStopt, this.armed, this.gyroErrLock, this.landLock, this.pwmMot0, this.pwmMot1, this.pwmMot2, this.pwmMot3, this.rcStopt});

  GlFlightRcord.fromJson(Map<String, dynamic> json) {
    appStopt = json['AppStopt'];
    armed = json['Armed'];
    gyroErrLock = json['GyroErrLock'];
    landLock = json['LandLock'];
    pwmMot0 = json['PwmMot0'];
    pwmMot1 = json['PwmMot1'];
    pwmMot2 = json['PwmMot2'];
    pwmMot3 = json['PwmMot3'];
    rcStopt = json['RcStopt'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['AppStopt'] = appStopt;
    data['Armed'] = armed;
    data['GyroErrLock'] = gyroErrLock;
    data['LandLock'] = landLock;
    data['PwmMot0'] = pwmMot0;
    data['PwmMot1'] = pwmMot1;
    data['PwmMot2'] = pwmMot2;
    data['PwmMot3'] = pwmMot3;
    data['RcStopt'] = rcStopt;
    return data;
  }
}

class MFlyTestInfo {
  num? baroInitOk;
  num? batVal;
  num? flowInitOk;
  num? gpsFine;
  num? gpsInitOk;
  num? gpsNum;
  num? gpsQuality;
  num? insInitOk;
  num? magInitOk;
  num? accX;
  num? accY;
  num? accZ;
  Attitude? attitude;
  num? baroAlt;
  Center? coordinate;
  num? current1;
  num? current2;
  num? gyroX;
  num? gyroY;
  num? gyroZ;
  num? magX;
  num? magY;
  num? magZ;
  num? temperature;

  MFlyTestInfo(
      {this.baroInitOk,
      this.batVal,
      this.flowInitOk,
      this.gpsFine,
      this.gpsInitOk,
      this.gpsNum,
      this.gpsQuality,
      this.insInitOk,
      this.magInitOk,
      this.accX,
      this.accY,
      this.accZ,
      this.attitude,
      this.baroAlt,
      this.coordinate,
      this.current1,
      this.current2,
      this.gyroX,
      this.gyroY,
      this.gyroZ,
      this.magX,
      this.magY,
      this.magZ,
      this.temperature});

  MFlyTestInfo.fromJson(Map<String, dynamic> json) {
    baroInitOk = json['BaroInitOk'];
    batVal = json['BatVal'];
    flowInitOk = json['FlowInitOk'];
    gpsFine = json['GpsFine'];
    gpsInitOk = json['GpsInitOk'];
    gpsNum = json['GpsNum'];
    gpsQuality = json['GpsQuality'];
    insInitOk = json['InsInitOk'];
    magInitOk = json['MagInitOk'];
    accX = json['acc_x'];
    accY = json['acc_y'];
    accZ = json['acc_z'];
    attitude = json['attitude'] != null ? Attitude.fromJson(json['attitude']) : null;
    baroAlt = json['baro_alt'];
    coordinate = json['coordinate'] != null ? Center.fromJson(json['coordinate']) : null;
    current1 = json['current1'];
    current2 = json['current2'];
    gyroX = json['gyro_x'];
    gyroY = json['gyro_y'];
    gyroZ = json['gyro_z'];
    magX = json['mag_x'];
    magY = json['mag_y'];
    magZ = json['mag_z'];
    temperature = json['temperature'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['BaroInitOk'] = baroInitOk;
    data['BatVal'] = batVal;
    data['FlowInitOk'] = flowInitOk;
    data['GpsFine'] = gpsFine;
    data['GpsInitOk'] = gpsInitOk;
    data['GpsNum'] = gpsNum;
    data['GpsQuality'] = gpsQuality;
    data['InsInitOk'] = insInitOk;
    data['MagInitOk'] = magInitOk;
    data['acc_x'] = accX;
    data['acc_y'] = accY;
    data['acc_z'] = accZ;
    if (attitude != null) {
      data['attitude'] = attitude!.toJson();
    }
    data['baro_alt'] = baroAlt;
    if (coordinate != null) {
      data['coordinate'] = coordinate!.toJson();
    }
    data['current1'] = current1;
    data['current2'] = current2;
    data['gyro_x'] = gyroX;
    data['gyro_y'] = gyroY;
    data['gyro_z'] = gyroZ;
    data['mag_x'] = magX;
    data['mag_y'] = magY;
    data['mag_z'] = magZ;
    data['temperature'] = temperature;
    return data;
  }
}

class Attitude {
  num? pitch;
  num? roll;
  num? yaw;

  Attitude({this.pitch, this.roll, this.yaw});

  Attitude.fromJson(Map<String, dynamic> json) {
    pitch = json['pitch'];
    roll = json['roll'];
    yaw = json['yaw'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['pitch'] = pitch;
    data['roll'] = roll;
    data['yaw'] = yaw;
    return data;
  }
}
