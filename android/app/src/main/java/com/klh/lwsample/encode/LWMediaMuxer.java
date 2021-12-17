package com.klh.lwsample.encode;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.media.MediaRecorder;
import android.text.TextUtils;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by Andy on 2018/3/16.
 */

public class LWMediaMuxer {
    private static LWMediaMuxer mInstance;

    // 录像文件路径
    private volatile String mRecordPath = "";
    // 录像合成器对??
    private volatile MediaMuxer mMediaMuxer = null;
    // 合成器音视频Track
    public volatile int mVideoTrack = -1;
    public volatile int mAudioTrack = -1;

    private volatile LWAudioEncoder mLWAudioEncoder;
    // Track写入数据标记
    private volatile boolean mAudioWrite = false;
    private volatile boolean mVideoWrite = false;
    // 是否写入I帧标??
    private volatile boolean mVideoIdr = false;

    private ArrayList<AudioDataBean> mAudioDataList =new ArrayList<AudioDataBean>();

    // 设置录像保存路径
    public void setRecordPath(String path) {
        mRecordPath = path;
    }


    public synchronized static LWMediaMuxer getInstance() {
        if (mInstance == null) {
            mInstance = new LWMediaMuxer();

        }
        return mInstance;
    }
    public void setSpsAndPps(int framerate,byte[] mSps,byte[] mPps){

        LWMediaMuxer.getInstance().Init(1280, 720, framerate,false,null,null);

    }

    
    public int Init(int nWidth, int nHeight,int framerate, boolean bSound, byte[] header_sps,byte[] header_pps){
    	 int colorFormat=-1;
    	UnInit();
        // 创建新对??
        synchronized (MediaMuxer.class) {
            if (mMediaMuxer == null && !TextUtils.isEmpty(mRecordPath)) {
                try {
                    mMediaMuxer = new MediaMuxer(mRecordPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
                    if (mMediaMuxer != null) {


                        MediaFormat videoFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, nWidth, nHeight);


                        videoFormat.setByteBuffer("csd-0", ByteBuffer.wrap(header_sps));
                        videoFormat.setByteBuffer("csd-1", ByteBuffer.wrap(header_pps));
                        MediaCodecInfo mediaCodecInfo = LWH264Encoder.selectCodec(MediaFormat.MIMETYPE_VIDEO_AVC);
                        
                        colorFormat =LWH264Encoder.selectColorFormat(mediaCodecInfo,MediaFormat.MIMETYPE_VIDEO_AVC);
//                      LWLogUtils.e("encoder color format = "+colorFormat);
	                    if(colorFormat==MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar){
	                      videoFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar);
	                    }else {
	                   	  videoFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar);
	                    }
//                         videoFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1); //关键帧间隔时间 单位s
                        mVideoTrack = mMediaMuxer.addTrack(videoFormat);
//                        mVideoTrack = mMediaMuxer.addTrack(mMediaCodec.getOutputFormat());

//                        MediaFormat bb=mMediaCodec.getOutputFormat();
//                        LWLogUtils.e("bb");

//                        mVideoTrack = mMediaMuxer.addTrack(mMediaCodec.getOutputFormat());

                        // 启动AAC编码
                        if (bSound) {
                        	// 计算csd0,从编码数据后得到
                            byte[] config = new byte[2];
                            config[0] = 21;
                            config[1] = -120;
                        	   mLWAudioEncoder=new LWAudioEncoder();
                               mLWAudioEncoder.Init(true);
                            MediaFormat audioFormat = MediaFormat.createAudioFormat("audio/mp4a-latm", 8000, 1);
                            audioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectMain);
                            audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, 128000);
                            audioFormat.setByteBuffer("csd-0", ByteBuffer.wrap(config));
                            mAudioTrack = mMediaMuxer.addTrack(audioFormat);
                        }
                        // 判断是否加载Track成功
                        if (mAudioTrack != -1 || mVideoTrack != -1) {
                            mMediaMuxer.start();
                            Log.i("","MediaMuxer 初始化合成器成功");
                            return colorFormat;
                        }
                    }
                } catch (Exception e) {
                    Log.i("","MediaMuxer 初始化合成器失败");
                    e.printStackTrace();
                    if (e.getMessage() != null) {
                        Log.e("",e.getMessage());
                    }
                }
            }
        }
        Log.i("","MediaMuxer 初始化合成器失败失败");
        return  colorFormat;
    }

    public void UnInit(){
        synchronized (MediaMuxer.class) {
            // 关闭合成??
            if (mMediaMuxer != null) {
                try {
                    if(mLWAudioEncoder!=null){
                        mLWAudioEncoder.unInit();
                    }
                    isRunFlag=false;
                    mAudioRecordThread=null;
                    mAudioTrack = -1;
                    mVideoTrack = -1;
                    mAudioWrite = false;
                    mVideoWrite = false;
                    mVideoIdr = false;
                    // 关闭合成??
                    mMediaMuxer.stop();
                    mMediaMuxer.release();
                    mMediaMuxer = null;

                    Log.i("","MediaMuxer 关闭合成器成??");
                } catch (Exception e) {
                    e.printStackTrace();
                    if (e.getMessage() != null) {
                        Log.e("","MediaMuxer e.getMessage()"+e.getMessage());
                    }
                    mMediaMuxer = null;
                }
            }
            mAudioTrack = -1;
            mVideoTrack = -1;
            mAudioWrite = false;
            mVideoWrite = false;
            mVideoIdr = false;
        }
    }
    // 写音频帧
    public void onAudioTrack(byte[] src, int length,long usTime2) {
        synchronized (MediaMuxer.class) {
            if (mMediaMuxer != null && mAudioTrack != -1) {
              //  Log.e("","MediaMuxer onAudioTrack");
                mAudioDataList.clear();
                int nIndex = mLWAudioEncoder.encode(src, length, usTime2, mAudioDataList);
//                int nIndex = mLWAudioEncoder.encode(src, length, VideoUTime, mAudioDataList);
                if(nIndex>0){
                    int nAudioIndex = 0;
                    int nAudioDataLen = 0;
                    for (int i = 0; i < nIndex; i++) {
                        nAudioDataLen += mAudioDataList.get(i).nCount;
                    }
                    byte[] dst = new byte[nAudioDataLen];
                    for (int i = 0; i < nIndex; i++) {
                        System.arraycopy(mAudioDataList.get(i).data, 0, dst, nAudioIndex, mAudioDataList.get(i).nCount);
                        nAudioIndex += mAudioDataList.get(i).nCount;
                    }
//                    LWH264Encoder.usTime=LWH264Encoder.usTime+66666;
                    ByteBuffer buffer = ByteBuffer.wrap(dst);
                    MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                    bufferInfo.offset = 0;
                    bufferInfo.size = nAudioDataLen;
                    bufferInfo.presentationTimeUs = LWH264Encoder.usTime;
//                    bufferInfo.presentationTimeUs = VideoUTime;
                    bufferInfo.flags = 0 ;
                    if(mVideoIdr){
                      //  Log.e("11111","rrrrrrrr oooooooo VideoUTime="+LWH264Encoder.usTime);
                        mMediaMuxer.writeSampleData(mAudioTrack, buffer, bufferInfo);
                    }

                    mAudioWrite = true;
                }

            }
        }
    }

    long VideoUTime;
    // 写视频帧
    public void onVideoTrack(ByteBuffer byteBuf, MediaCodec.BufferInfo bufferInfo, long usTime) {
        synchronized (MediaMuxer.class) {
            if (mMediaMuxer != null  && mVideoTrack != -1 && mVideoIdr) {
                VideoUTime=usTime;
              //  Log.e("11111","rrrrrrrr vvvvvvvv VideoUTime="+VideoUTime);
                mMediaMuxer.writeSampleData(mVideoTrack, byteBuf, bufferInfo);
                mVideoWrite = true;
               // Log.e("","MediaMuxer  onVideoTrack writeSampleData");
            }
        }
    }

    // 设置关键帧写入标??
    public void SetVideoIdr(boolean bIdr) {
        synchronized (MediaMuxer.class) {
            if (mMediaMuxer != null  && mVideoTrack != -1) {
                mVideoIdr = bIdr;
            }
        }
    }


    int nMinBufSize = 0;
    private static final int SAMPLE_RATE_IN_HZ = 8000;
    protected boolean isRunFlag = false;
    AudioRecordThread mAudioRecordThread;
    //TODO  这里??启线程录??
    public void RecordPcm(){
        nMinBufSize = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        isRunFlag=true;
        mAudioRecordThread=new AudioRecordThread();
        mAudioRecordThread.start();
    }
    public class AudioRecordThread extends Thread {
        @Override
        public void run() {
            super.run();
            AudioRecord recorder = null;

            recorder = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION, SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, nMinBufSize);
            recorder.startRecording();
            byte[] pcmInBuf = new byte[320];
            int nReadBytes = 0;
            int accPos = 0;
            byte[] AacInBuf = new byte[2048];
            while (isRunFlag) {
                Log.e("11111","rrrrrrrr");
                nReadBytes = recorder.read(pcmInBuf, 0, pcmInBuf.length);
                if (nReadBytes > 0) {
                    if ((accPos + 320) < 2048) {
                        System.arraycopy(pcmInBuf, 0, AacInBuf, accPos, 320);
                        accPos = accPos + 320;
                    } else {
                        System.arraycopy(pcmInBuf, 0, AacInBuf, accPos, 2048 - accPos);

                        //todo 添加编码
                        if(mMediaMuxer != null){
//                            onAudioTrack(pcmInBuf,2048,System.nanoTime()/1000);

                            onAudioTrack(AacInBuf,2048,VideoUTime);

                        }

                        accPos = (accPos + 320) - 2048;
                        System.arraycopy(pcmInBuf, 0, AacInBuf, 0, accPos);
                    }
                }
            }


        }
    }


//    Thread  RecordPcmThread=new Thread(new Runnable() {
//        @Override
//        public void run() {
//            AudioRecord recorder = null;
//
//            recorder = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION, SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, nMinBufSize);
//            recorder.startRecording();
//            byte[] pcmInBuf = new byte[320];
//            int nReadBytes = 0;
//            int accPos = 0;
//            byte[] AacInBuf = new byte[2048];
//            while (isRunFlag) {
//                nReadBytes = recorder.read(pcmInBuf, 0, pcmInBuf.length);
//                if (nReadBytes > 0) {
//                    if ((accPos + 320) < 2048) {
//                        System.arraycopy(pcmInBuf, 0, AacInBuf, accPos, 320);
//                        accPos = accPos + 320;
//                    } else {
//                        System.arraycopy(pcmInBuf, 0, AacInBuf, accPos, 2048 - accPos);
//
//                        //todo 添加编码
//                        if(mMediaMuxer != null){
////                            onAudioTrack(pcmInBuf,2048,System.nanoTime()/1000);
//                            onAudioTrack(AacInBuf,2048,VideoUTime);
//                        }
//
//
//                        accPos = (accPos + 320) - 2048;
//                        System.arraycopy(pcmInBuf, 0, AacInBuf, 0, accPos);
//                    }
//                }
//            }
//
//        }
//    });



}
