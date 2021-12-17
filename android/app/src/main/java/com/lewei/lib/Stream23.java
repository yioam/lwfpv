package com.lewei.lib;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaCodecInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.klh.lwsample.constant.HandlerParams;
import com.klh.lwsample.controller.ActivityLifeCycle;
import com.klh.lwsample.controller.FlyCtrl;
import com.klh.lwsample.controller.FlyModel;
import com.klh.lwsample.encode.LWH264Encoder;
import com.klh.lwsample.encode.LWMediaMuxer;
import com.klh.lwsample.util.DateUtils;
import com.klh.lwsample.util.PathConfig;
import com.klh.lwsample.view.FlutterSurfaceView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Stream23 {
    private String TAG = "Stream23";

    private Context context;
    private Handler handler;

    private Thread mThread23;
    private boolean isStop23 = false;
    public boolean is_recording_now = false;

    private H264Frame mFrame;
    private KeyValue mKeyValue = new KeyValue();
    private HeartBitParams mHeartBitParams = new HeartBitParams();

    private LeweiLib23 mLib23;
    private boolean isNeedTakePhoto = false;
    public static int count = 0;
    private String recpath = "";


    public Stream23(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;


        mLib23 = new LeweiLib23();
    }


    public RecordCb mRecordCb;

    public void setRecordCb(RecordCb recordCb) {
        this.mRecordCb = recordCb;
    }

    // 接收数据的回调
    private LeweiLib23.OnLib23CallBack mOnLib23CallBack = new LeweiLib23.OnLib23CallBack() {

        @Override
        public void onHeartBit(String ssid, int channel, int baudrate,
                               int cameraValue, int droneType) {
            // TODO Auto-generated method stub
            Log.d("", "ssid:" + ssid);
        }

        @Override
        public void onUdpRecv(byte[] recvBufFlyState, int size) {
            if (recvBufFlyState == null) {
                return;
            }
            FlyCtrl.interpretingData(recvBufFlyState);
        }

        // 手柄拍照和录像
        @Override
        public void onKeyRecv(int key_id, int key_value) {
            // TODO Auto-generated method stub
            Log.d("", "key id:" + key_id + "  key value:" + key_value);
            if (key_id == 16) {
                if (key_value == 1) {
                    handler.sendEmptyMessage(HandlerParams.SET_TAKEPHOTO);
                } else if (key_value == 2) {
                    takeRecord();
                }
            }
        }
    };

    public void stopStream23() {
        isStop23 = true;
        count = 0;
    }

    int get360P = 0;

    public void startStream23() {

        if (mThread23 == null || !mThread23.isAlive()) {
            mThread23 = new Thread(() -> {
                isStop23 = false;
                boolean flag = false;
                boolean has_create_bmp = false;
                Bitmap bmpBitmap = null;

                int dstYuvLength;
                int srcYuvLength;
                int srcArgbLength;
                srcYuvLength = 640 * 480 * 3 / 2;
                srcArgbLength = 640 * 480 * 4;
                dstYuvLength = 640 * 480 * 3 / 2;
                while (!isStop23) {
                    flag = mLib23.initStream(mOnLib23CallBack,
                            LeweiLib.PORT_COMMON);
                    if (flag) {
                        while (!isStop23) {
                            // Log.e(Tag, "22222222222");
                            // 这个从控制界面退出到home界面，然后在进入控制界面，
                            // 有时mFrame获得的是null导致一直运行以下的这个代码，从而导致没有图像
                            mFrame = mLib23.getH264Frame();
                            if (mFrame == null) {
                                msleep(5);
                                count++;
                                if (count == 2000) {
                                    FlyModel.getInstance().isConnectWifi = false;
                                    handler.sendEmptyMessage(HandlerParams.WiFi_disconnection);
                                    Log.e(TAG, "wifi 已经断开连接==" + count);

                                }
                                continue;
                            }
                            // Log.e(Tag, "444444444444");
                            get360P = LeweiLib23.get360P();
                            // Log.e(Tag, "LeweiLib23.get360P()="+get360P);
                            if (!has_create_bmp) {
                                handler.sendEmptyMessage(HandlerParams.GET_FIRST_FRAME_23);
                                has_create_bmp = true;
                                // Log.e(Tag, "55555555555");
                                if (get360P == 1) {///todo yioam
                                    srcYuvLength = 640 * 360 * 3 / 2;
                                    srcArgbLength = 640 * 360 * 4;
                                    dstYuvLength = 640 * 360 * 3 / 2;
                                } else {
                                    srcYuvLength = 640 * 480 * 3 / 2;
                                    srcArgbLength = 640 * 480 * 4;
                                    dstYuvLength = 640 * 480 * 3 / 2;
                                }
                                YuvUtils.allocateMemo(srcYuvLength, srcArgbLength, dstYuvLength);
                            }
                            //Log.e(TAG, "66666666666=" + mFrame.size);
                            // 连上wifi时候一直刷
                            handler.sendEmptyMessage(HandlerParams.CONNECT_63WIFI);
                            count = 0;
                            FlyModel.getInstance().isConnectWifi = true;

                            bmpBitmap = BitmapFactory.decodeByteArray(
                                    mFrame.data, 0, mFrame.size);

                            byte[] dstYuv;

                            dstYuv = new byte[dstYuvLength];
                            Log.e(TAG, "88888888888888");
                            YuvUtils.rgbToYuvBylibyuv(bmpBitmap, dstYuv);
                            Log.e(TAG, "99999999999999999");
                            byte[] yuv720 = new byte[1280 * 720 * 3 / 2];
                            Log.e("Stream23", "I420Scale start");
                            if (get360P == 1) {
                                yuv720 = new byte[1280 * 720 * 3 / 2];
                                YuvUtils.I420Scale(dstYuv, 640, 360, yuv720, 1280, 720);
                                if (FlyModel.getInstance().activityState == ActivityLifeCycle.onCreate && FlutterSurfaceView.surfaceView != null) {
                                    FlutterSurfaceView.surfaceView.copyBmpBuffer(yuv720, 1280, 720);
                                    handler.sendEmptyMessage(HandlerParams.SURFACE_VISIBLE);
                                }

                            } else {
                                yuv720 = new byte[640 * 480 * 3 / 2];
                                YuvUtils.I420Scale(dstYuv, 640, 480, yuv720, 640, 480);
                                if (FlyModel.getInstance().activityState == ActivityLifeCycle.onCreate && FlutterSurfaceView.surfaceView != null) {
                                    FlutterSurfaceView.surfaceView.copyBmpBuffer(yuv720, 640, 480);
                                    handler.sendEmptyMessage(HandlerParams.SURFACE_VISIBLE);
                                }
                            }

                            if (is_recording_now) {
                                byte[] h264 = null;
                                byte[] nv21 = null;
                                if (get360P == 1) {
                                    h264 = new byte[1280 * 720 * 3 / 2];
                                    nv21 = new byte[1280 * 720 * 3 / 2];

                                } else {
                                    h264 = new byte[640 * 480 * 3 / 2];
                                    // 420p转nv21
                                    nv21 = new byte[640 * 480 * 3 / 2];

                                }
                                if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar) {

                                    recordFramCount = recordFramCount + 1;

                                    int timeInterval = (1000 / framRate) * 1000;
                                    LWH264Encoder.getInstance().offerEncoder(yuv720, h264, timeInterval);
                                    if (mRecordCb != null) {
//											Log.e("timeString", "timeString=");
                                        String timeString = DateUtils.calculateRecordTime(framRate, recordFramCount);
                                        mRecordCb.RecordTime(framRate, recordFramCount, timeString);
                                    }
                                    recordFramCount = recordFramCount + 1;
                                    LWH264Encoder.getInstance().offerEncoder(yuv720, h264, timeInterval);
                                    if (mRecordCb != null) {
//											Log.e("timeString", "timeString=");
                                        String timeString = DateUtils.calculateRecordTime(framRate, recordFramCount);
                                        mRecordCb.RecordTime(framRate, recordFramCount, timeString);
                                    }
                                } else {
                                    if (get360P == 1) {
                                        YuvUtils.I420ToNV21(yuv720, nv21, 1280,
                                                720, true);
                                    } else {
                                        YuvUtils.I420ToNV21(yuv720, nv21, 640,
                                                480, true);
                                    }

                                    recordFramCount = recordFramCount + 1;
                                    int timeInterval = (1000 / framRate) * 1000;

                                    int ret = LWH264Encoder.getInstance().offerEncoder(nv21, h264, timeInterval);
                                    if (mRecordCb != null) {
                                        String timeString = DateUtils.calculateRecordTime(framRate, recordFramCount);
                                        mRecordCb.RecordTime(framRate, recordFramCount, timeString);
                                    }
                                    recordFramCount = recordFramCount + 1;
                                    LWH264Encoder.getInstance().offerEncoder(
                                            nv21, h264, timeInterval);
                                    if (mRecordCb != null) {
//											Log.e("timeString", "timeString=");
                                        String timeString = DateUtils.calculateRecordTime(framRate, recordFramCount);
                                        mRecordCb.RecordTime(framRate, recordFramCount, timeString);
                                    }
                                }


                                //旧的方法
                                //LeweiLib23.nativeRecAddData(bmpBitmap);
                            }


                            if (isNeedTakePhoto) {
                                SimpleDateFormat format = new SimpleDateFormat(
                                        "yyyyMMddHHmmss", Locale
                                        .getDefault());
                                long time = System.currentTimeMillis();
                                Date curDate = new Date(time);
                                String timeString = format.format(curDate);

                                String photoName = timeString + ".jpg";
                                PathConfig.savePhoto(context, PathConfig.PHOTOS_PATH, photoName, mFrame.data);

                                isNeedTakePhoto = false;
                            }

                        }
                    } else {
                        msleep(200);
                    }
                }

                isStop23 = true;
                if (has_create_bmp) {
                    YuvUtils.releaseMemo();
                }
                mLib23.deinitStream();
                if (bmpBitmap != null)
                    bmpBitmap.recycle();
            });
            mThread23.start();
        }

    }

    public boolean isRecording() {
        return is_recording_now;
    }

    /**
     * 拍照
     */
    public void takePhoto() {
        isNeedTakePhoto = true;

    }

    long usTime;
    int colorFormat;

    int framRate = 30;
    int recordFramCount = 0;


    public void takeRecord() {
        usTime = System.nanoTime() / 1000;

        if (!is_recording_now) {
            recpath = PathConfig.getVideoPath();
            recordFramCount = 0;
            int w_after = 640;
            int h_after = 480;
            if (get360P == 1) {
                w_after = 1280;
                h_after = 720;

            }
            LWMediaMuxer.getInstance().setRecordPath(recpath);
            colorFormat = LWH264Encoder.getInstance().init(w_after, h_after, framRate, 2500000, FlyModel.getInstance().isNeedAudio);
            // LWMediaMuxer.getInstance().RecordPcm();
            LWH264Encoder.getInstance().setTimeUs(0);
            handler.sendEmptyMessage(HandlerParams.RECORD_START_OK);
            is_recording_now = true;
            Log.e("", "takeRec ture");

        } else {
            // mLib23.stopRecord();
            LWH264Encoder.getInstance().close();
            LWMediaMuxer.getInstance().UnInit();
            handler.sendEmptyMessage(HandlerParams.RECORD_STOP);
            is_recording_now = false;
            updatePhotoToAlbum(recpath);

        }
    }

    public void setMirror() {
        mLib23.setMirror();
    }

    private void msleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public class KeyValue {
        public int key_id;
        public int key_value;
    }

    public class HeartBitParams {
        public String ssid = "";
        public String pass = "";
        public int channel = 2;
        public int cameraValue = 0;
        public int qpValue = 30;
    }

    private void sendHandlerMessage(int type, Object obj) {
        if (handler != null) {
            Message message = Message.obtain();
            message.what = type;
            message.obj = obj;
            handler.sendMessage(message);
        }
    }

    /**
     * 将录像成功的视频提交到相册
     *
     * @param path
     */
    private void updatePhotoToAlbum(String path) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(path));
        Log.e("Display Activity", "uri  " + uri.toString());
        intent.setData(uri);
        context.getApplicationContext().sendBroadcast(intent);
    }

    // /**
    // * Bitmap放大的方法
    // *
    // * @param bitmap
    // * @return
    // */
    // private static Bitmap scaleBigBitmap(Bitmap bitmap) {
    // Matrix matrix = new Matrix();
    // matrix.postScale(1.5f, 1.5f); // 长和宽放大缩小的比例
    // Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
    // bitmap.getHeight(), matrix, true);
    // return resizeBmp;
    // }

    /**
     * Bitmap → byte[]
     *
     * @param bm
     * @return
     */
    private byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 0, baos);
        return baos.toByteArray();
    }

    public interface RecordCb {
        void RecordTime(int framRate, int framCount, String timeString);
    }

}
