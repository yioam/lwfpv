part of amap_flutter_map;

final MethodChannelAMapFlutterMap _methodChannel = AMapFlutterPlatform.instance as MethodChannelAMapFlutterMap;

/// 地图通信中心
class AMapController {
  final int mapId;
  final _MapState _mapState;
  AMapController._(CameraPosition initCameraPosition, this._mapState,
      {required this.mapId}) {
    _connectStreams(mapId);
  }

  ///根据传入的id初始化[AMapController]
  /// 主要用于在[AMapWidget]初始化时在[AMapWidget.onMapCreated]中初始化controller
  static Future<AMapController> init(
    int id,
    CameraPosition initialCameration,
    _MapState mapState,
  ) async {
    await _methodChannel.init(id);
    return AMapController._(
      initialCameration,
      mapState,
      mapId: id,
    );
  }

  ///只用于测试
  ///用于与native的通信
  @visibleForTesting
  MethodChannel get channel {
    return _methodChannel.channel(mapId);
  }

  void _connectStreams(int mapId) {
    if (_mapState.widget.onLocationChanged != null) {
      _methodChannel.onLocationChanged(mapId: mapId).listen(
          (LocationChangedEvent e) =>
              _mapState.widget.onLocationChanged!(e.value));
    }

    if (_mapState.widget.onCameraMove != null) {
      _methodChannel.onCameraMove(mapId: mapId).listen(
          (CameraPositionMoveEvent e) =>
              _mapState.widget.onCameraMove!(e.value));
    }
    if (_mapState.widget.onCameraMoveEnd != null) {
      _methodChannel.onCameraMoveEnd(mapId: mapId).listen(
          (CameraPositionMoveEndEvent e) =>
              _mapState.widget.onCameraMoveEnd!(e.value));
    }
    if (_mapState.widget.onTap != null) {
      _methodChannel
          .onMapTap(mapId: mapId)
          .listen(((MapTapEvent e) => _mapState.widget.onTap!(e.value)));
    }
    if (_mapState.widget.onLongPress != null) {
      _methodChannel.onMapLongPress(mapId: mapId).listen(
          ((MapLongPressEvent e) => _mapState.widget.onLongPress!(e.value)));
    }

    if (_mapState.widget.onPoiTouched != null) {
      _methodChannel.onPoiTouched(mapId: mapId).listen(
          ((MapPoiTouchEvent e) => _mapState.widget.onPoiTouched!(e.value)));
    }

    _methodChannel
        .onMarkerTap(mapId: mapId)
        .listen((MarkerTapEvent e) => _mapState.onMarkerTap(e.value));

    _methodChannel.onMarkerDragEnd(mapId: mapId).listen(
        (MarkerDragEndEvent e) =>
            _mapState.onMarkerDragEnd(e.value, e.position));

    _methodChannel
        .onPolylineTap(mapId: mapId)
        .listen((PolylineTapEvent e) => _mapState.onPolylineTap(e.value));
  }

  void disponse() {
    _methodChannel.dispose(id: mapId);
  }

  Future<void> _updateMapOptions(Map<String, dynamic> optionsUpdate) {
    return _methodChannel.updateMapOptions(optionsUpdate, mapId: mapId);
  }

  Future<void> _updateMarkers(MarkerUpdates markerUpdates) {
    return _methodChannel.updateMarkers(markerUpdates, mapId: mapId);
  }

  Future<void> _updatePolylines(PolylineUpdates polylineUpdates) {
    return _methodChannel.updatePolylines(polylineUpdates, mapId: mapId);
  }

  Future<void> _updatePolygons(PolygonUpdates polygonUpdates) {
    return _methodChannel.updatePolygons(polygonUpdates, mapId: mapId);
  }

  ///改变地图视角
  ///
  ///通过[CameraUpdate]对象设置新的中心点、缩放比例、放大缩小、显示区域等内容
  ///
  ///（注意：iOS端设置显示区域时，不支持duration参数，动画时长使用iOS地图默认值350毫秒）
  ///
  ///可选属性[animated]用于控制是否执行动画移动
  ///
  ///可选属性[duration]用于控制执行动画的时长,默认250毫秒,单位:毫秒
  Future<void> moveCamera(CameraUpdate cameraUpdate,
      {bool animated = true, int duration = 250}) {
    return _methodChannel.moveCamera(cameraUpdate,
        mapId: mapId, animated: animated, duration: duration);
  }

  ///添加/移动到 当前位置圈圈以及中心点
  Future<void> addCircle(double latitude, double longitude, double radius) {
    return _methodChannel.addCircle(mapId, latitude, longitude, radius);
  }
  
  Future<String?> addMarker(double latitude, double longitude, double curLat, double curLng, double maxRadius,{bool needClear = false}) {
    return _methodChannel.addMarker(mapId, <String, dynamic>{
      'latitude': latitude,
      'longitude': longitude,
      'curLat': curLat,
      'curLng': curLng,
      'maxRadius': maxRadius,
      'needClear': needClear,
    });
  }

  ///清除所有marker
  Future<void> clearMarker(){
    return _methodChannel.clearMarker(mapId);
  }

  ///设置成点击marker直接删除状态
  Future<void> setMarkerClickToDel(bool isClickToDel){
    return _methodChannel.setMarkerClickToDel(mapId, isClickToDel);
  }

  ///fromScreenLocation
  Future<String?> fromScreenLocation(int x, int y) {
    return _methodChannel.fromScreenLocation(mapId, x, y);
  }

  ///
  Future<String?> setOnMarkerClickListener() {
    return _methodChannel.setOnMarkerClickListener(mapId);
  }

  
  ///Yioam marker回调
  Future<void> onLwMarkerTap(ArgumentCallback<LatLng> lwMarkerTap) {
    _methodChannel.onLwMarkerTap(mapId: mapId).listen(((MarkerEvent e) {
      lwMarkerTap(e.value);
    }));

    return Future.value();
  }

  ///航点添加多个标注
  ///isFirst 第一个点
  ///isEnd 最后一个点
  Future<String?> drawTrailFromScreenLocation(
    double latitude,
    double longitude,
    int x,
    int y,
    double maxRadius, {
    bool needClear = false,
    bool isEnd = false,
    double calibrate = 10.0,
  }) {
    return _methodChannel.drawTrailFromScreenLocation(mapId, <String, dynamic>{
      'latitude': latitude,
      'longitude': longitude,
      'x': x,
      'y': y,
      'needClear': needClear,
      'isEnd': isEnd,
      'maxRadius': maxRadius,
      'calibrate': calibrate,
    });
  }

  ///设置地图每秒渲染的帧数
  Future<void> setRenderFps(int fps) {
    return _methodChannel.setRenderFps(fps, mapId: mapId);
  }

  ///地图截屏
  Future<Uint8List?> takeSnapshot() {
    return _methodChannel.takeSnapshot(mapId: mapId);
  }

  /// 获取地图审图号（普通地图）
  ///
  /// 任何使用高德地图API调用地图服务的应用必须在其应用中对外透出审图号
  ///
  /// 如高德地图在"关于"中体现
  Future<String?> getMapContentApprovalNumber() {
    return _methodChannel.getMapContentApprovalNumber(mapId: mapId);
  }

  /// 获取地图审图号（卫星地图)
  ///
  /// 任何使用高德地图API调用地图服务的应用必须在其应用中对外透出审图号
  ///
  /// 如高德地图在"关于"中体现
  Future<String?> getSatelliteImageApprovalNumber() {
    return _methodChannel.getSatelliteImageApprovalNumber(mapId: mapId);
  }

  /// 清空缓存
  Future<void> clearDisk() {
    return _methodChannel.clearDisk(mapId: mapId);
  }
}
