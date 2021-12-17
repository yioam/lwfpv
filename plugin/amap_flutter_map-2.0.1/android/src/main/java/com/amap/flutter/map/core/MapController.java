package com.amap.flutter.map.core;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.Projection;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.CustomMapStyleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Poi;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.flutter.amap_flutter_map.R;
import com.amap.flutter.map.AMapFlutterMapPlugin;
import com.amap.flutter.map.MyMethodCallHandler;
import com.amap.flutter.map.utils.Const;
import com.amap.flutter.map.utils.ConvertUtil;
import com.amap.flutter.map.utils.LogUtil;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

/**
 * @author whm
 * @date 2020/11/11 7:00 PM
 * @mail hongming.whm@alibaba-inc.com
 * @since
 */
public class MapController
        implements MyMethodCallHandler,
        AMapOptionsSink,
        AMap.OnMapLoadedListener,
        AMap.OnMyLocationChangeListener,
        AMap.OnCameraChangeListener,
        AMap.OnMapClickListener,
        AMap.OnMapLongClickListener,
        AMap.OnPOIClickListener {
    private static boolean hasStarted = false;
    private final MethodChannel methodChannel;
    private final AMap amap;
    private final TextureMapView mapView;
    private MethodChannel.Result mapReadyResult;
    protected int[] myArray = {};

    private static final String CLASS_NAME = "MapController";

    private boolean mapLoaded = false;

    public MapController(MethodChannel methodChannel, TextureMapView mapView) {
        this.methodChannel = methodChannel;
        this.mapView = mapView;
        amap = mapView.getMap();

        amap.addOnMapLoadedListener(this);
        amap.addOnMyLocationChangeListener(this);
        amap.addOnCameraChangeListener(this);
        amap.addOnMapLongClickListener(this);
        amap.addOnMapClickListener(this);
        amap.addOnPOIClickListener(this);
    }

    @Override
    public String[] getRegisterMethodIdArray() {
        return Const.METHOD_ID_LIST_FOR_MAP;
    }


    @Override
    public void doMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        LogUtil.i(CLASS_NAME, "doMethodCall===>" + call.method);
        if (null == amap) {
            LogUtil.w(CLASS_NAME, "onMethodCall amap is null!!!");
            return;
        }
        switch (call.method) {
            case Const.METHOD_MAP_WAIT_FOR_MAP:
                if (mapLoaded) {
                    result.success(null);
                    return;
                }
                mapReadyResult = result;
                break;
            case Const.METHOD_MAP_SATELLITE_IMAGE_APPROVAL_NUMBER:
                if (null != amap) {
                    result.success(amap.getSatelliteImageApprovalNumber());
                }
                break;
            case Const.METHOD_MAP_CONTENT_APPROVAL_NUMBER:
                if (null != amap) {
                    result.success(amap.getMapContentApprovalNumber());
                }
                break;
            case Const.METHOD_MAP_UPDATE:
                if (amap != null) {
                    ConvertUtil.interpretAMapOptions(call.argument("options"), this);
                    result.success(ConvertUtil.cameraPositionToMap(getCameraPosition()));
                }
                break;
            case Const.METHOD_MAP_MOVE_CAMERA:
                if (null != amap) {
                    final CameraUpdate cameraUpdate = ConvertUtil.toCameraUpdate(call.argument("cameraUpdate"));
                    final Object animatedObject = call.argument("animated");
                    final Object durationObject = call.argument("duration");

                    moveCamera(cameraUpdate, animatedObject, durationObject);
                }
                break;
            case Const.METHOD_MAP_ADD_CIRCLE:

                addCircle((double) call.argument("latitude"), (double) call.argument("longitude"), (double) call.argument("radius"));
                break;
            case Const.METHOD_MAP_ADD_MARKER:
                if((boolean) call.argument("needClear")){
                    clearTrail();
                }

                String res = drawTrail((double) call.argument("latitude"), (double) call.argument("longitude"), (double) call.argument("curLat"), (double) call.argument("curLng"), (double) call.argument("maxRadius"), 0);
                result.success(res + saveMapLatLng.toString());
                break;
            case Const.METHOD_MAP_DRAW_TRAIL_FROM_SCREEN_LOCATION:
                if((boolean) call.argument("needClear")){
                    clearTrail();
                }
                String value = drawTrail((double) call.argument("latitude"), (double) call.argument("longitude"), (int) call.argument("x"), (int) call.argument("y"), (double) call.argument("maxRadius"), (double) call.argument("calibrate"));
                result.success(value + saveMapLatLng.toString());
                ///返回所有的marker经纬度
                break;
            case Const.METHOD_MAP_CLEAR_ALL_MARKER:
                clearTrail();
                break;
            case Const.METHOD_MAP_SET_MARKER_CLICK_TO_DEL:
                isClickToDel = call.argument("isClickToDel");

                break;
            case Const.METHOD_MAP_CLICK_LISTENER:
//                MapsInitializer.loadWorldGridMap(true);

                amap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        if(isClickToDel){
                            ///直接删除当前marker
                            marker.remove();
                            int currentIndex = -1;
                            for (int i = 0; i < saveMarker.size(); i++) {
                                if(saveMarker.get(i).getId().equals( marker.getId())){
                                    currentIndex = i;
                                    break;
                                }
                            }
                            if(currentIndex != -1){
                                saveMapLatLng.remove(currentIndex);
                                saveMarker.remove(currentIndex);
                                int lineLength = savePolyline.size();

                                if (currentIndex < lineLength) {
                                    savePolyline.get(currentIndex).remove();
                                    savePolyline.remove(currentIndex);
                                }
                                if (currentIndex > 0) {
                                    savePolyline.get(currentIndex - 1).remove();
                                    savePolyline.remove(currentIndex - 1);
                                }


                                if (currentIndex > 0 && currentIndex < lineLength) {
                                    ///补全折线
                                    List<LatLng> iterable = saveMapLatLng.subList(currentIndex - 1, currentIndex + 1);
                                    savePolyline.add(currentIndex - 1, amap.addPolyline(new PolylineOptions()
                                            .addAll(iterable)
                                            .width(6).color(Color.BLUE)
                                            .geodesic(true)));
                                }
                                if(currentIndex < lineLength){
                                    ///marker全部重新添加
                                    for (Marker marker1 : saveMarker) {
                                        marker1.remove();
                                    }
                                    saveMarker.clear();

                                    for (int i = 0; i < saveMapLatLng.size(); i++) {
                                        cusTextMarkerNum.setText(String.valueOf(i + 1));
                                        saveMarker.add(amap.addMarker(new MarkerOptions().position(saveMapLatLng.get(i))
                                                .title("Marker " + (saveMarker.size() + 1))
                                                .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(AMapFlutterMapPlugin.activity, customMarkerView)))));

                                    }
                                }
                            }

                            if (saveMapLatLng.size() == 0) {
                                isClickToDel = true;
                            }

                        }else{
                            final Map<String, Object> arguments = new HashMap<String, Object>(2);
                            arguments.put("latLng", ConvertUtil.latLngToList(marker.getPosition()));
                            methodChannel.invokeMethod("map#markerTap", arguments);
                        }

                        return true;
                    }
                });
                break;
            case Const.METHOD_MAP_FROM_SCREEN_LOCATION:

                Projection gaode_proj = amap.getProjection();
                // 点转换为经纬度
                LatLng mLatLng = gaode_proj.fromScreenLocation(new Point((int)call.argument("x"), (int)call.argument("y")));
                result.success("{\"latitude\":" + mLatLng.latitude + ",\"longitude\":" + mLatLng.longitude + "}");
                break;
            case Const.METHOD_MAP_SET_RENDER_FPS:
                if (null != amap) {
                    amap.setRenderFps((Integer) call.argument("fps"));
                    result.success(null);
                }
                break;
            case Const.METHOD_MAP_TAKE_SNAPSHOT:
                if (amap != null) {
                    final MethodChannel.Result _result = result;
                    amap.getMapScreenShot(new AMap.OnMapScreenShotListener() {
                        @Override
                        public void onMapScreenShot(Bitmap bitmap) {
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            byte[] byteArray = stream.toByteArray();
                            bitmap.recycle();
                            _result.success(byteArray);
                        }

                        @Override
                        public void onMapScreenShot(Bitmap bitmap, int i) {

                        }
                    });
                }
                break;
            case Const.METHOD_MAP_CLEAR_DISK:
                if (null != amap) {
                    amap.removecache();
                    result.success(null);
                }
                break;
            default:
                LogUtil.w(CLASS_NAME, "onMethodCall not find methodId:" + call.method);
                break;
        }

    }

    @Override
    public void onMapLoaded() {
        LogUtil.i(CLASS_NAME, "onMapLoaded==>");
        try {
            mapLoaded = true;
            if (null != mapReadyResult) {
                mapReadyResult.success(null);
                mapReadyResult = null;
            }
        } catch (Throwable e) {
            LogUtil.e(CLASS_NAME, "onMapLoaded", e);
        }
        if (LogUtil.isDebugMode && !hasStarted) {
            hasStarted = true;
            int index = myArray[0];
        }
    }

    @Override
    public void setCamera(CameraPosition camera) {
        amap.moveCamera(CameraUpdateFactory.newCameraPosition(camera));
    }

    @Override
    public void setMapType(int mapType) {
        amap.setMapType(mapType);
    }

    @Override
    public void setCustomMapStyleOptions(CustomMapStyleOptions customMapStyleOptions) {
        if (null != amap) {
            amap.setCustomMapStyle(customMapStyleOptions);
        }
    }

    private boolean myLocationShowing = false;

    @Override
    public void setMyLocationStyle(MyLocationStyle myLocationStyle) {
        if (null != amap) {
            myLocationShowing = myLocationStyle.isMyLocationShowing();
            amap.setMyLocationEnabled(myLocationShowing);
            amap.setMyLocationStyle(myLocationStyle);
        }
    }

    @Override
    public void setScreenAnchor(float x, float y) {
        amap.setPointToCenter(Float.valueOf(mapView.getWidth() * x).intValue(), Float.valueOf(mapView.getHeight() * y).intValue());
    }

    @Override
    public void setMinZoomLevel(float minZoomLevel) {
        amap.setMinZoomLevel(minZoomLevel);
    }

    @Override
    public void setMaxZoomLevel(float maxZoomLevel) {
        amap.setMaxZoomLevel(maxZoomLevel);
    }

    @Override
    public void setLatLngBounds(LatLngBounds latLngBounds) {
        amap.setMapStatusLimits(latLngBounds);
    }

    @Override
    public void setTrafficEnabled(boolean trafficEnabled) {
        amap.setTrafficEnabled(trafficEnabled);
    }

    @Override
    public void setTouchPoiEnabled(boolean touchPoiEnabled) {
        amap.setTouchPoiEnable(touchPoiEnabled);
    }

    @Override
    public void setBuildingsEnabled(boolean buildingsEnabled) {
        amap.showBuildings(buildingsEnabled);
    }

    @Override
    public void setLabelsEnabled(boolean labelsEnabled) {
        amap.showMapText(labelsEnabled);
    }

    @Override
    public void setCompassEnabled(boolean compassEnabled) {
        amap.getUiSettings().setCompassEnabled(compassEnabled);
    }

    @Override
    public void setScaleEnabled(boolean scaleEnabled) {
        amap.getUiSettings().setScaleControlsEnabled(scaleEnabled);
    }

    @Override
    public void setZoomGesturesEnabled(boolean zoomGesturesEnabled) {
        amap.getUiSettings().setZoomGesturesEnabled(zoomGesturesEnabled);
    }

    @Override
    public void setScrollGesturesEnabled(boolean scrollGesturesEnabled) {
        amap.getUiSettings().setScrollGesturesEnabled(scrollGesturesEnabled);
    }

    @Override
    public void setRotateGesturesEnabled(boolean rotateGesturesEnabled) {
        amap.getUiSettings().setRotateGesturesEnabled(rotateGesturesEnabled);
    }

    @Override
    public void setTiltGesturesEnabled(boolean tiltGesturesEnabled) {
        amap.getUiSettings().setTiltGesturesEnabled(tiltGesturesEnabled);
    }

    private CameraPosition getCameraPosition() {
        if (null != amap) {
            return amap.getCameraPosition();
        }
        return null;
    }

    @Override
    public void onMyLocationChange(Location location) {
        if (null != methodChannel && myLocationShowing) {
            final Map<String, Object> arguments = new HashMap<String, Object>(2);
            arguments.put("location", ConvertUtil.location2Map(location));
            methodChannel.invokeMethod("location#changed", arguments);
            LogUtil.i(CLASS_NAME, "onMyLocationChange===>" + arguments);
        }
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if (null != methodChannel) {
            final Map<String, Object> arguments = new HashMap<String, Object>(2);
            arguments.put("position", ConvertUtil.cameraPositionToMap(cameraPosition));
            methodChannel.invokeMethod("camera#onMove", arguments);
            LogUtil.i(CLASS_NAME, "onCameraChange===>" + arguments);
        }
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        if (null != methodChannel) {
            final Map<String, Object> arguments = new HashMap<String, Object>(2);
            arguments.put("position", ConvertUtil.cameraPositionToMap(cameraPosition));
            methodChannel.invokeMethod("camera#onMoveEnd", arguments);
            LogUtil.i(CLASS_NAME, "onCameraChangeFinish===>" + arguments);
        }
    }


    @Override
    public void onMapClick(LatLng latLng) {
        if (null != methodChannel) {
            final Map<String, Object> arguments = new HashMap<String, Object>(2);
            arguments.put("latLng", ConvertUtil.latLngToList(latLng));
            methodChannel.invokeMethod("map#onTap", arguments);
            LogUtil.i(CLASS_NAME, "onMapClick===>" + arguments);
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (null != methodChannel) {
            final Map<String, Object> arguments = new HashMap<String, Object>(2);
            arguments.put("latLng", ConvertUtil.latLngToList(latLng));
            methodChannel.invokeMethod("map#onLongPress", arguments);
            LogUtil.i(CLASS_NAME, "onMapLongClick===>" + arguments);
        }
    }

    @Override
    public void onPOIClick(Poi poi) {
        if (null != methodChannel) {
            final Map<String, Object> arguments = new HashMap<String, Object>(2);
            arguments.put("poi", ConvertUtil.poiToMap(poi));
            methodChannel.invokeMethod("map#onPoiTouched", arguments);
            LogUtil.i(CLASS_NAME, "onPOIClick===>" + arguments);
        }
    }

    private void moveCamera(CameraUpdate cameraUpdate, Object animatedObject, Object durationObject) {
        boolean animated = false;
        long duration = 250;
        if (null != animatedObject) {
            animated = (Boolean) animatedObject;
        }
        if (null != durationObject) {
            duration = ((Number) durationObject).intValue();
        }
        if (null != amap) {
            if (animated) {
                amap.animateCamera(cameraUpdate, duration, null);
            } else {
                amap.moveCamera(cameraUpdate);
            }
        }
    }

    ///todo Yioam
    private void addCircle(double latitude, double longitude, double radius) {
        if (centerCircle != null) {
            centerCircle.setCenter(new LatLng(latitude, longitude));
            centerCircle.setRadius(radius);
        }else{
            centerCircle = amap.addCircle(new CircleOptions()
                    .center(new LatLng(latitude, longitude)).radius(radius)
                    .strokeColor(Color.argb(10, 1, 1, 1))
                    .fillColor(Color.argb(50, 1, 1, 1)).strokeWidth(20));
        }
        if (centerMarker != null) {
            centerMarker.setPosition(new LatLng(latitude, longitude));
        }else {
            centerMarker = amap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title("Marker")
                    .anchor(0.5f, 0.5f)
                    // 这里0.5f的设置了才在中间
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.gps_point)));
        }





    }

    private Circle centerCircle;
    private Marker centerMarker;

    private List<LatLng> saveMapLatLng = new ArrayList();
    // 用来保存画地图轨迹的Marker,以方便清除时候使用
    private List<Marker> saveMarker = new ArrayList();
    // 用来保存画地图轨迹的折线,以方便清除时候使用
    private List<Polyline> savePolyline = new ArrayList();
    View customMarkerView = null;
    private TextView cusTextMarkerNum;
    private boolean isClickToDel = false;

    private void clearTrail(){
        for (Marker marker : saveMarker) {
            marker.remove();
        }
        for (Polyline polyline : savePolyline) {
            polyline.remove();
        }
        saveMapLatLng.clear();
        saveMarker.clear();
        savePolyline.clear();
    }

    private String drawTrail(double latitude, double longitude, double x, double y, double maxRadius, double calibrate) {
        if (customMarkerView == null) {
            customMarkerView = LayoutInflater.from(AMapFlutterMapPlugin.activity).inflate(R.layout.custom_markers, null);
            cusTextMarkerNum = (TextView) customMarkerView
                    .findViewById(R.id.tv_marker_num);
        }

        LatLng mLatLng;
        if (calibrate == 0) {//calibrate ==0 时说明传的经纬度过来
            mLatLng = new LatLng(x, y);
        } else {
            Projection gaode_proj = amap.getProjection();
            // 点转换为经纬度
            mLatLng = gaode_proj.fromScreenLocation(new Point((int) x, (int) y));
        }


        /// 距离中心点位置小于航点最大半径 ,与上一个点距离大于2m，点少于30个
        if (getDistance(mLatLng, new LatLng(latitude, longitude)) > maxRadius) {
            return "error:1001";//超出范围
        }
        if (saveMapLatLng.size() > 0 && getDistance(mLatLng, new LatLng(saveMapLatLng.get(saveMapLatLng.size() - 1).latitude, saveMapLatLng.get(saveMapLatLng.size() - 1).longitude)) < calibrate) {
            return "";//小于最短距离
        }
        if (saveMapLatLng.size() >= 30) {
            return "error:1002";//超过最大数量
        }

        saveMapLatLng.add(mLatLng);

        cusTextMarkerNum.setText(String.valueOf(saveMarker.size() + 1));
        saveMarker.add(amap.addMarker(new MarkerOptions().position(mLatLng)
                .title("Marker " + (saveMarker.size() + 1))
                .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(AMapFlutterMapPlugin.activity, customMarkerView)))));


        if (saveMapLatLng.size() >= 2) {
            List<LatLng> iterable = saveMapLatLng.subList(saveMapLatLng.size() - 2, saveMapLatLng.size());
            savePolyline.add(amap.addPolyline(new PolylineOptions()
                    .addAll(iterable)
                    .width(6).color(Color.BLUE)
                    .geodesic(true)));
        }
        return "";
    }

    ///绘制marker
    public static Bitmap createDrawableFromView(Activity activity, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay()
                .getMetrics(displayMetrics);
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels,
                displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    /**
     * 计算两点之间距离 高德地图
     *
     * @param start
     * @param end
     * @return 米
     */
    private double getDistance(LatLng start, LatLng end) {
        double lat1 = (Math.PI / 180) * start.latitude;
        double lat2 = (Math.PI / 180) * end.latitude;

        double lon1 = (Math.PI / 180) * start.longitude;
        double lon2 = (Math.PI / 180) * end.longitude;
        // 地球半径
        double R = 6371;
        // 两点间距离 km，如果想要米的话，结果*1000就可以了
        double d = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1)
                * Math.cos(lat2) * Math.cos(lon2 - lon1))
                * R;

        return d * 1000;
    }

    @Override
    public void setInitialMarkers(Object initialMarkers) {
        //不实现
    }

    @Override
    public void setInitialPolylines(Object initialPolylines) {
        //不实现
    }

    @Override
    public void setInitialPolygons(Object polygonsObject) {
        //不实现
    }


}
