package com.lewei.lib;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.klh.lwsample.controller.ActivityLifeCycle;
import com.lewei.codec.LWH264Dec;
import com.klh.lwsample.constant.HandlerParams;
import com.klh.lwsample.controller.FlyModel;
import com.klh.lwsample.encode.FrameDataHelper;
import com.klh.lwsample.encode.LWH264Encoder;
import com.klh.lwsample.encode.LWMediaMuxer;
import com.klh.lwsample.util.DateUtils;
import com.klh.lwsample.util.LWLogUtils;
import com.klh.lwsample.util.PathConfig;
import com.klh.lwsample.view.FlutterSurfaceView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Stream93 {
    private final String TAG = "Stream93";
    private static Context context;
    private Handler handler;

    private Thread mThread93;
    public boolean isStop93 = false;
    public boolean is_recording_now = false;

    public static int count = 0;

    private H264Frame mFrame;

    private String recpath = "";

    /**
     * soft1080P_flag这个标识为，我重新定义一下， 当soft1080P_flag==0x01表示拍照插值成1080P，
     * soft1080P_flag==0x10,表示录像插值1080P， 16 soft1080P_flag==0x11表示都插值成1080P 17
     ***/
    private int soft1080P_flag = -1;

    private int getPhotoVideoTo4k_flag = -1;

    private int moduleTypeFlag = 16;// 获取模块类型

    public Stream93(Context context, Handler handler) {
        Stream93.context = context;
        this.handler = handler;

    }

    public void stopStream93() {
        isStop93 = true;
        count = 0;
        if (is_recording_now) {
            is_recording_now = false;
        }
    }

    public RecordCb mRecordCb;

    public void setRecordCb(RecordCb recordCb) {
        this.mRecordCb = recordCb;
    }

    int iFrame = 2;
    int w = 1280;
    int h = 720;
    boolean has_create_spsPps = false;
    byte[] sps = null;
    byte[] pps = null;

    public void startStream93() {

        if (mThread93 == null || !mThread93.isAlive()) {
            mThread93 = new Thread(() -> {

                isStop93 = false;
                int flag = -1;
                boolean has_create_bmp = false;
                // long time = System.currentTimeMillis();
                Bitmap bmpBitmap = null;

                while (!isStop93) {

                    flag = LeweiLib
                            .LW93StartLiveStream(1, LeweiLib.HD_flag);
                    if (flag > 0) {

                        if (LeweiLib.HD_flag != 1) {
                            w = 640;
                            h = 480;
                            // LWLogUtils.e("LeweiLib.HD_flag=640");
                        }

                        LWH264Dec.H264Open();

                        int dstYuvLength;
                        int srcYuvLength;
                        int srcArgbLength;
                        dstYuvLength = 640 * 480 * 3 / 2;

                        while (!isStop93) {
                            count++;
                            // Log.e("", "count==" + count);
                            if (count == 3000) {
                                {
                                    handler.sendEmptyMessage(HandlerParams.WiFi_disconnection);
                                }

                            }

                            // time = System.currentTimeMillis();

                            mFrame = LeweiLib.getH264Frame();

                            if (mFrame == null || FlyModel.getInstance().activityState != ActivityLifeCycle.onCreate) {
                                msleep(5);
                                continue;
                            }
                            if (!has_create_bmp) {
                                handler.sendEmptyMessage(HandlerParams.GET_FIRST_FRAME_93);
                                has_create_bmp = true;
                                iFrame = mFrame.iFrame;

                                if (mFrame.iFrame == 2) {
                                    soft1080P_flag = LeweiLib.getSoft1080pFlag();
                                    Log.e("", "soft1080P_flag=" + soft1080P_flag);

                                    bmpBitmap = BitmapFactory.decodeByteArray(mFrame.data, 0, mFrame.size);
                                    w = bmpBitmap.getWidth();
                                    h = bmpBitmap.getHeight();
                                    srcYuvLength = w * h * 3 / 2;
                                    srcArgbLength = w * h * 4;
                                    dstYuvLength = w * h * 3 / 2;
                                    LWLogUtils.e("YuvUtils.allocateMemo()");
                                    YuvUtils.allocateMemo(srcYuvLength,
                                            srcArgbLength, dstYuvLength);

                                }

                            }

                            moduleTypeFlag = LeweiLib.getModuleType();
                            // Log.e("", "moduleTypeFlag=" +
                            // moduleTypeFlag);

                            count = 0;
                            handler.sendEmptyMessage(HandlerParams.CONNECT_93WIFI);
                            FlyModel.getInstance().isConnectWifi = true;
                            // LWLogUtils.e("mFrame.iFrame="+mFrame.iFrame);
                            if (mFrame.iFrame == 2) {
                                bmpBitmap = BitmapFactory.decodeByteArray(
                                        mFrame.data, 0, mFrame.size);

                                if (bmpBitmap.getWidth() != w && bmpBitmap.getHeight() != h) {
                                    YuvUtils.releaseMemo();
                                    w = bmpBitmap.getWidth();
                                    h = bmpBitmap.getHeight();
                                    // Log.e(TAG, "w==="+w+"  hh="+h);
                                    srcYuvLength = w * h * 3 / 2;
                                    srcArgbLength = w * h * 4;
                                    dstYuvLength = w * h * 3 / 2;
                                    YuvUtils.allocateMemo(srcYuvLength, srcArgbLength, dstYuvLength);
                                }


                                byte[] dstYuv;

                                dstYuv = new byte[dstYuvLength];
                                YuvUtils.rgbToYuvBylibyuv(bmpBitmap, dstYuv);
                                byte[] yuv_after = null;
                                if (soft1080P_flag == 16 || soft1080P_flag == 17) {
                                    yuv_after = new byte[1920 * 1080 * 3 / 2];
                                    YuvUtils.I420Scale(dstYuv, w, h, yuv_after, 1920, 1080);

                                    if (FlyModel.getInstance().activityState == ActivityLifeCycle.onCreate && FlutterSurfaceView.surfaceView != null) {
                                        FlutterSurfaceView.surfaceView.copyBmpBuffer(yuv_after, 1920, 1080);
                                        handler.sendEmptyMessage(HandlerParams.SURFACE_VISIBLE);
                                    }
//									if (FlyModel.getInstance().isMapMode) {
//										// 右上角实时预览的时候
//										byte[] yuv2 = new byte[1920 * 1080 * 3 / 2];
//										System.arraycopy(yuv_after, 0,
//												yuv2, 0, yuv_after.length);
//									}
                                } else {
                                    yuv_after = new byte[1280 * 720 * 3 / 2];
                                    YuvUtils.I420Scale(dstYuv, w, h, yuv_after, 1280, 720);
                                    if (FlyModel.getInstance().activityState == ActivityLifeCycle.onCreate && FlutterSurfaceView.surfaceView != null) {
                                        FlutterSurfaceView.surfaceView.copyBmpBuffer(yuv_after, 1280, 720);
                                        handler.sendEmptyMessage(HandlerParams.SURFACE_VISIBLE);
                                    }
//									if (FlyModel.getInstance().isMapMode) {
//										// 右上角实时预览的时候
//										byte[] yuv2 = new byte[1280 * 720 * 3 / 2];
//										System.arraycopy(yuv_after, 0, yuv2, 0, yuv_after.length);
//									}
                                }

                                if (is_recording_now) {

                                    Log.e(TAG, "22--========0000");
                                    if (soft1080P_flag == 16 || soft1080P_flag == 17) {
                                        encodeH246FromIframe2(yuv_after, 1920, 1080);

                                    } else {
                                        encodeH246FromIframe2(yuv_after, 1280, 720);
                                    }

                                }

                            } else {
                                // 2560*1440(2k ) 4096*2160(4k)
                                byte[] yuv = new byte[2560 * 1440 * 3 / 2];

                                int[] framePara = new int[4];
                                int decoceSuc = LWH264Dec.H264Decode(
                                        mFrame.data, mFrame.size,
                                        framePara, yuv, mFrame.iFrame);
                                if (decoceSuc != 0) {
                                    continue;
                                }
                                if (!has_create_spsPps) {
                                    if (mFrame.iFrame == 1) {
                                        FrameDataHelper.getInstance().nalysisSpsaPps(mFrame.data);
                                        sps = FrameDataHelper.getInstance().getSps();
                                        pps = FrameDataHelper.getInstance().getPps();
                                        has_create_spsPps = true;
                                    }

                                }

                                if (FlyModel.getInstance().activityState == ActivityLifeCycle.onCreate && FlutterSurfaceView.surfaceView != null) {
                                    FlutterSurfaceView.surfaceView.copyBmpBuffer(yuv, framePara[2], framePara[3]);
                                    handler.sendEmptyMessage(HandlerParams.SURFACE_VISIBLE);
                                }

//								if (FlyModel.getInstance().isMapMode) {
//									// 右上角实时预览的时候
//									byte[] yuv2 = new byte[2560 * 1440 * 3 / 2];
//									System.arraycopy(yuv, 0, yuv2, 0, yuv.length);
//								}
                                w = framePara[2];
                                h = framePara[3];
                                Log.e(TAG, "wwww=" + w + "  hh=" + h);
                                if (is_recording_now) {

                                    if (moduleTypeFlag == 16 && w == 1280) {
                                        byte[] yuv_after;
                                        yuv_after = new byte[1920 * 1080 * 3 / 2];
                                        YuvUtils.I420Scale(yuv, w, h, yuv_after, 1920, 1080);

                                        byte[] h264;
                                        byte[] nv21;

                                        recordFramCount = recordFramCount + 1;

                                        int timeInterval = (1000 / framRate) * 1000;
                                        //
                                        h264 = new byte[1920 * 1080 * 3 / 2];
                                        // 420p杞琻v21
                                        nv21 = new byte[1920 * 1080 * 3 / 2];

                                        // YuvUtils.I420ToNV21(yuv_after,
                                        // nv21, 1920,1080, true);

                                        if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar) {
                                            LWH264Encoder.getInstance().offerEncoder(yuv_after, h264, timeInterval);
                                        } else {

                                            YuvUtils.I420ToNV21(yuv_after, nv21, 1920, 1080, true);

                                            // Log.e("111111111111","LWH264Encoder.getInstance().offerEncoder");
                                            LWH264Encoder.getInstance().offerEncoder(nv21, h264, timeInterval);

                                        }

                                        if (mRecordCb != null) {
                                            // Log.e("timeString",
                                            // "timeString=");
                                            String timeString = DateUtils.calculateRecordTime(framRate, recordFramCount);
                                            mRecordCb.RecordTime(framRate, recordFramCount, timeString);
                                        }
                                    } else if (moduleTypeFlag == 93) {
                                        byte[] yuv_after;
                                        yuv_after = new byte[2048 * 1080 * 3 / 2];
                                        YuvUtils.I420Scale(yuv, w, h,
                                                yuv_after, 2048, 1080);
                                        byte[] h264;
                                        byte[] nv21;

                                        recordFramCount = recordFramCount + 1;
                                        int timeInterval = (1000 / framRate) * 1000;
                                        h264 = new byte[2048 * 1080 * 3 / 2];
                                        nv21 = new byte[2048 * 1080 * 3 / 2];

                                        if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar) {
                                            LWH264Encoder.getInstance().offerEncoder(yuv_after, h264, timeInterval);
                                        } else {

                                            // YuvUtils.I420ToNV21(yuv_after,
                                            // nv21, 1920, 1080, true);

                                            YuvUtils.I420ToNV21(yuv_after, nv21, 2048, 1080, true);

                                            // Log.e("111111111111","LWH264Encoder.getInstance().offerEncoder");
                                            LWH264Encoder.getInstance().offerEncoder(nv21, h264, timeInterval);

                                        }

                                        if (mRecordCb != null) {
                                            // Log.e("timeString",
                                            // "timeString=");
                                            String timeString = DateUtils.calculateRecordTime(framRate, recordFramCount);
                                            mRecordCb.RecordTime(framRate, recordFramCount, timeString);
                                        }

                                    } else {
                                        recordFramCount = recordFramCount + 1;

                                        int timeInterval = (1000 / framRate) * 1000;

                                        usTime = usTime + timeInterval;


                                        if (mFrame.iFrame == 1) {
                                            ByteBuffer buffer = ByteBuffer.wrap(mFrame.data);
                                            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                                            bufferInfo.offset = 0;
                                            bufferInfo.size = mFrame.data.length;
                                            bufferInfo.presentationTimeUs = usTime;
                                            bufferInfo.flags = MediaCodec.BUFFER_FLAG_SYNC_FRAME;
                                            LWMediaMuxer.getInstance().SetVideoIdr(true);
                                            LWMediaMuxer.getInstance().onVideoTrack(buffer, bufferInfo, usTime);
                                            Log.e(TAG, "22--========11111");

                                        } else {
                                            Log.e(TAG, "22--========2222");
                                            ByteBuffer buffer = ByteBuffer.wrap(mFrame.data);
                                            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                                            bufferInfo.offset = 0;
                                            bufferInfo.size = mFrame.size;
                                            bufferInfo.presentationTimeUs = usTime;
                                            bufferInfo.flags = 0;
                                            LWMediaMuxer.getInstance().onVideoTrack(buffer, bufferInfo, usTime);
                                        }

                                        if (mRecordCb != null) {

                                            String timeString = DateUtils.calculateRecordTime(framRate, recordFramCount);
                                            mRecordCb.RecordTime(framRate, recordFramCount, timeString);
                                        }

                                    }
                                }
                            }
                        }
                    } else {
                        msleep(200);
                    }

                }
                isStop93 = true;
                if (has_create_bmp) {
                    YuvUtils.releaseMemo();
                }
                LeweiLib.LW93StopLiveStream();
                LWH264Dec.H264Close();
                if (bmpBitmap != null)
                    bmpBitmap.recycle();
            });

            mThread93.start();

        }

    }

    public void takePhoto() {
        getPhotoVideoTo4k_flag = LeweiLib.getPhotoVideoTo4kFlag();

        // Log.e(TAG, "soft1080P_flag==" + soft1080P_flag);
        Log.e(TAG, "getPhotoVideoTo4k_flag==" + getPhotoVideoTo4k_flag);
        if (soft1080P_flag == 1 || soft1080P_flag == 17) {
            LeweiLib.isNeedTakePhoto_Big = true;
            Log.e(TAG, "soft1080P_flag==111111" + soft1080P_flag);
        } else {
            LeweiLib.isNeedTakePhoto_Big = false;
        }
        // 拍照插值4k
        LeweiLib.needTakePhoto_4k = getPhotoVideoTo4k_flag == 1 || getPhotoVideoTo4k_flag == 3;
        LeweiLib.isNeedTakePhoto = true;
    }

    public boolean isRecording() {
        return is_recording_now;
    }

    long usTime;
    int colorFormat;
    int framRate = 20;
    int recordFramCount = 0;

    public void takeRecord() {
        if (iFrame == 2) {
            usTime = System.nanoTime() / 1000;

            if (!is_recording_now) {
                recordFramCount = 0;
                recpath = PathConfig.getVideoPath();
                framRate = 30;
                int w_after = 1280;
                int h_after = 720;
                if (soft1080P_flag == 16 || soft1080P_flag == 17) {
                    w_after = 1920;
                    h_after = 1080;
                }

                LWMediaMuxer.getInstance().setRecordPath(recpath);
                colorFormat = LWH264Encoder.getInstance().init(w_after,
                        h_after, framRate, 2500000, FlyModel.getInstance().isNeedAudio);
                // LWMediaMuxer.getInstance().RecordPcm();
                LWH264Encoder.getInstance().setTimeUs(0);
                is_recording_now = true;
                handler.sendEmptyMessage(HandlerParams.RECORD_START_OK);

            } else {
                // mLib23.stopRecord();
                LWH264Encoder.getInstance().close();
                LWMediaMuxer.getInstance().UnInit();
                handler.sendEmptyMessage(HandlerParams.RECORD_STOP);
                is_recording_now = false;
                updatePhotoToAlbum(recpath);
            }

        } else if (moduleTypeFlag == 16 && w == 1280) {

            usTime = System.nanoTime() / 1000;

            if (!is_recording_now) {
                recordFramCount = 0;
                if (w == 640) {
                    framRate = LeweiLib.getVgaFps();
                } else if (w == 1280) {
                    framRate = LeweiLib.get720PFps();
                } else if (w == 1920) {
                    framRate = LeweiLib.get1080PFps();
                }

                if (framRate < 1) {
                    framRate = 20;
                }
                framRate = 20;
                recpath = PathConfig.getVideoPath();

                int w_after = 1920;
                int h_after = 1080;

                LWMediaMuxer.getInstance().setRecordPath(recpath);
                colorFormat = LWH264Encoder.getInstance().init(w_after,
                        h_after, framRate, 2500000, FlyModel.getInstance().isNeedAudio);
                // LWMediaMuxer.getInstance().RecordPcm();
                LWH264Encoder.getInstance().setTimeUs(0);
                is_recording_now = true;
                handler.sendEmptyMessage(HandlerParams.RECORD_START_OK);

            } else {
                // mLib23.stopRecord();
                LWH264Encoder.getInstance().close();
                LWMediaMuxer.getInstance().UnInit();
                handler.sendEmptyMessage(HandlerParams.RECORD_STOP);
                is_recording_now = false;
                updatePhotoToAlbum(recpath);
            }
        } else if (moduleTypeFlag == 93) {
            usTime = System.nanoTime() / 1000;

            if (!is_recording_now) {
                recordFramCount = 0;
                framRate = 20;
                recpath = PathConfig.getVideoPath();
                int w_after = 2048;
                int h_after = 1080;
                LWMediaMuxer.getInstance().setRecordPath(recpath);
                colorFormat = LWH264Encoder.getInstance().init(w_after,
                        h_after, framRate, 2500000, FlyModel.getInstance().isNeedAudio);
                LWH264Encoder.getInstance().setTimeUs(0);
                is_recording_now = true;
                handler.sendEmptyMessage(HandlerParams.RECORD_START_OK);

            } else {
                // mLib23.stopRecord();
                LWH264Encoder.getInstance().close();
                LWMediaMuxer.getInstance().UnInit();
                handler.sendEmptyMessage(HandlerParams.RECORD_STOP);
                is_recording_now = false;
                updatePhotoToAlbum(recpath);
            }
        } else {
            usTime = System.nanoTime() / 1000;
            recordFramCount = 0;
            if (LeweiLib.HD_flag != 1) {
                framRate = 15;
            }

            if (!is_recording_now) {
                recpath = PathConfig.getVideoPath();

                if (w == 640) {
                    framRate = LeweiLib.getVgaFps();
                } else if (w == 1280) {
                    framRate = LeweiLib.get720PFps();
                } else if (w == 1920) {
                    framRate = LeweiLib.get1080PFps();
                }

                if (framRate < 1) {
                    framRate = 20;
                }

                if (FlyModel.getInstance().isNeedAudio) {
                    LWMediaMuxer.getInstance().setRecordPath(recpath);
                    colorFormat = LWMediaMuxer.getInstance().Init(w, h,
                            framRate, true, sps, pps);
                    // ret = LWMediaMuxer.getInstance().Init(w, h,
                    // true,framRate);
                    if (colorFormat > 0) {
                        LWMediaMuxer.getInstance().RecordPcm();
                    }
                } else {
                    LWMediaMuxer.getInstance().setRecordPath(recpath);
                    colorFormat = LWMediaMuxer.getInstance().Init(w, h,
                            framRate, false, sps, pps);
                    // ret = LWMediaMuxer.getInstance().Init(w, h,
                    // false,framRate);
                }
                if (colorFormat > 0) {
                    is_recording_now = true;
                    handler.sendEmptyMessage(HandlerParams.RECORD_START_OK);
                } else {
                    handler.sendEmptyMessage(HandlerParams.RECORD_START_FAIL);
                }

            } else {
                LWMediaMuxer.getInstance().UnInit();
                handler.sendEmptyMessage(HandlerParams.RECORD_STOP);
                is_recording_now = false;
                usTime = 0;
                updatePhotoToAlbum(recpath);
            }
        }


    }

    /**
     * Bitmap缩放 interpolationSize_w需要插值照片的宽 interpolationSize_h需要插值照片的高
     * original_w原来照片的宽 original_h原来照片的高
     */
    public static Bitmap interpolation_Photo_to4k(Bitmap bitmap,
                                                  float interpolationSize_w, float interpolationSize_h,
                                                  int original_w, int original_h) {

        BigDecimal bigW = new BigDecimal(interpolationSize_w / original_w);
        BigDecimal bigH = new BigDecimal(interpolationSize_h / original_h);
        float getW = bigW.setScale(4, BigDecimal.ROUND_HALF_UP)// 保留四位小数
                .floatValue();
        float getH = bigH.setScale(4, BigDecimal.ROUND_HALF_UP)// 保留四位小数
                .floatValue();

        Matrix matrix = new Matrix();
        // matrix.postScale(1.0f, 0.9926f); // 放大倍数4096×2160;3840*2160
        matrix.postScale(getW, getH); // 放大倍数4096×2160;3840*2160
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
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

    private void msleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * save photos use bitmap
     *
     * @param bmp
     * @param file_name bitmap data
     */
    public static void savePhoto(Bitmap bmp, String file_name) {
        String sdCardDir = PathConfig.getRootPath();
        if (sdCardDir != null) {
            try {
                SimpleDateFormat format = new SimpleDateFormat(
                        "yyyy-MM-dd HH.mm.ss", Locale.getDefault());
                long time = System.currentTimeMillis();
                Date curDate = new Date(time);
                String timeString = format.format(curDate);

                File savePhoto = new File(file_name);
                if (!savePhoto.exists()) {
                    savePhoto.createNewFile();
                }
                String absolutePath = savePhoto.getAbsolutePath();
                Log.e("path", absolutePath);

                FileOutputStream fout;

                fout = new FileOutputStream(absolutePath);

                bmp.compress(CompressFormat.JPEG, 80, fout);

                fout.close();
                // 推送到相册吧
                Intent intent = new Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri uri = PathConfig.path2uri(context,
                        Uri.fromFile(new File(absolutePath)));
                Log.e("Display Activity", "uri  " + uri.toString());
                intent.setData(uri);
                context.sendBroadcast(intent);

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
    }

    /**
     * 这个方法是编码H264，I帧是类型2的用到
     */
    public void encodeH246FromIframe2(byte[] yuv, int ww, int hh) {
        byte[] h264 = new byte[ww * hh * 3 / 2];
        // 420p杞琻v21
        byte[] nv21 = new byte[ww * hh * 3 / 2];
        // YuvUtils.I420ToNV21(yuv_after,
        // nv21, 1920,1080, true);

        if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar) {
            recordFramCount = recordFramCount + 1;
            int timeInterval = (1000 / framRate) * 1000;
            LWH264Encoder.getInstance().offerEncoder(yuv, h264, timeInterval);
            if (mRecordCb != null) {
                String timeString = DateUtils.calculateRecordTime(framRate,
                        recordFramCount);
                mRecordCb.RecordTime(framRate, recordFramCount, timeString);
            }
            recordFramCount = recordFramCount + 1;
            LWH264Encoder.getInstance().offerEncoder(yuv, h264, timeInterval);
            if (mRecordCb != null) {
                String timeString = DateUtils.calculateRecordTime(framRate,
                        recordFramCount);
                mRecordCb.RecordTime(framRate, recordFramCount, timeString);
            }
        } else {

            YuvUtils.I420ToNV21(yuv, nv21, ww, hh, true);
            // Log.e("111111111111","LWH264Encoder.getInstance().offerEncoder");
            recordFramCount = recordFramCount + 1;
            int timeInterval = (1000 / framRate) * 1000;
            LWH264Encoder.getInstance().offerEncoder(nv21, h264, timeInterval);
            if (mRecordCb != null) {
                String timeString = DateUtils.calculateRecordTime(framRate,
                        recordFramCount);
                mRecordCb.RecordTime(framRate, recordFramCount, timeString);
            }
            recordFramCount = recordFramCount + 1;
            LWH264Encoder.getInstance().offerEncoder(nv21, h264, timeInterval);
            if (mRecordCb != null) {
                String timeString = DateUtils.calculateRecordTime(framRate,
                        recordFramCount);
                mRecordCb.RecordTime(framRate, recordFramCount, timeString);
            }
        }
    }

    public interface RecordCb {
        void RecordTime(int framRate, int framCount, String timeString);
    }

}
