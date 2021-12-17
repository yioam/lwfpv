package com.klh.lwsample.encode;

/**
 * Created by Andy on 2018/3/14.
 */

import android.annotation.SuppressLint;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.util.Log;

import com.klh.lwsample.util.LWLogUtils;

import java.io.IOException;
import java.nio.ByteBuffer;

public class LWH264Encoder {
    private MediaCodec mediaCodec;
    int m_width;
    int m_height;
    byte[] m_info = null;
    int m_framerate;

    private static LWH264Encoder mInstance;

    public synchronized static LWH264Encoder getInstance() {
        if (mInstance == null) {
            mInstance = new LWH264Encoder();

        }
        return mInstance;
    }
    /**
     * Returns a color format that is supported by the codec and by this test code. If no
     * match is found, this throws a test failure -- the set of formats known to the test
     * should be expanded for new platforms.
     */
    public static int selectColorFormat(MediaCodecInfo codecInfo, String mimeType) {
        MediaCodecInfo.CodecCapabilities capabilities = codecInfo.getCapabilitiesForType(mimeType);
        for (int i = 0; i < capabilities.colorFormats.length; i++) {
            int colorFormat = capabilities.colorFormats[i];
            LWLogUtils.e("supports colorFormat = "+colorFormat);
            if (isRecognizedFormat(colorFormat)) {
                return colorFormat;
            }
        }
        LWLogUtils.e("couldn't find a good color format for " + codecInfo.getName() + " / " + mimeType);
        return 0; // not reached
    }
    /**
     * Returns true if this is a color format that this test code understands (i.e. we know how
     * to read and generate frames in this format).
     */
    private static boolean isRecognizedFormat(int colorFormat) {
        switch (colorFormat) {
            // these are the formats we know how to handle for this test
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar://is supported I420
            //  case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedPlanar:
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar:
                //   case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedSemiPlanar:
                //   case MediaCodecInfo.CodecCapabilities.COLOR_TI_FormatYUV420PackedSemiPlanar:
//            case COLOR_FormatYUV420Flexible:
                return true;
            default:
                return false;
        }
    }
    /**
     * Returns the first codec capable of encoding the specified MIME type, or null if no
     * match was found.
     */
    public static MediaCodecInfo selectCodec(String mimeType) {
        int numCodecs = MediaCodecList.getCodecCount();
        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            if (!codecInfo.isEncoder()) {
                continue;
            }
            String[] types = codecInfo.getSupportedTypes();
            for (int j = 0; j < types.length; j++) {
                if (types[j].equalsIgnoreCase(mimeType)) {
                    return codecInfo;
                }
            }
        }
        return null;
    }

    private boolean mSound;

//    private byte[] yuv420 = null;
    @SuppressLint("NewApi")
    public int init(int width, int height, int framerate, int bitrate,boolean bSound) {
//         close();
        m_width  = width;
        m_height = height;
//        yuv420 = new byte[width*height*3/2];
        m_framerate=framerate;
        this.mSound=bSound;
//        mFrameHelper=new FrameHelper();

        try {
            mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        try {
//        if(file.exists()){
//            file.delete();
//        }
//        fops = new FileOutputStream(file);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

//        MediaFormat mediaFormat = new MediaFormat();
//        mediaFormat.setString(MediaFormat.KEY_MIME, "video/avc");
//        mediaFormat.setInteger(MediaFormat.KEY_WIDTH, width);
//        mediaFormat.setInteger(MediaFormat.KEY_HEIGHT, height);

        MediaFormat mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height);
//        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, width*height*5);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 2 * width*height * framerate / 20);

        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, framerate);
//        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar);
        MediaCodecInfo mediaCodecInfo = selectCodec(MediaFormat.MIMETYPE_VIDEO_AVC);
        int colorFormat =selectColorFormat(mediaCodecInfo,MediaFormat.MIMETYPE_VIDEO_AVC);
        LWLogUtils.e("encoder color format = "+colorFormat);
        if(colorFormat==MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar){
            mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar);
        }else {
            mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar);
        }
//        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar);
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1); //闁稿繑濞婇弫顓犳暜瑜斿Λ鍧楁⒕閺冿拷濡炲倿姊婚敓?闁告娲戠紞鍗?
//        mediaFormat.setInteger(MediaFormat.KEY_ROTATION, 1);

//        MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/avc", 1280, 720);
//        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 125000);
//        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 20);
//        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar);
//        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 2);
        setTimeUs(0);

        mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mediaCodec.start();
        return colorFormat;


    }

    @SuppressLint("NewApi")
    public void close() {
        try {

            mediaCodec.stop();
            mediaCodec.release();
            mediaCodec=null;
            usTime=0;
        } catch (Exception e){
            e.printStackTrace();
        }
    }
//    private int TIMEOUT_USEC = 12000;
    /**
     * Generates the presentation time for frame N, in microseconds.
     */
    private long computePresentationTime(long frameIndex) {
        return 132 + frameIndex * 1000000 / m_framerate;
//        return 132l + frameIndex * 1000000l / (long) m_framerate;
    }
    public static long usTime;
    public void setTimeUs(long time){
//        if(time>0){
//            usTime=time;
//        }else {
//            usTime=System.nanoTime()/1000;
//        }
        usTime=System.nanoTime()/1000;
        generateIndex = 0;
        pts =  0;
        timeT=System.currentTimeMillis();
    }
    public long getUsTime(){
        return usTime;
    }
    long generateIndex = 0;
    long pts =  0;
    public byte[] configbyte;
    // 鐠侊紕鐣婚弮鍫曟？閹?
    long lasttime = 0;
    long currenttime = 0;
    long timeStamp = 0;

    long timeT;  //閺冨爼妫块幋?
    int time_interval=(int)(1000/15);

    private boolean firstSps=true;
    @SuppressLint("NewApi")
    public int offerEncoder(byte[] input, byte[] output,int timeInterval)
    {
        int pos = 0;
        if(mediaCodec==null){
            return pos;
        }
        if (input != null) {
            try {
                long startMs = System.currentTimeMillis();
               
                ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
                ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();
                int inputBufferIndex = mediaCodec.dequeueInputBuffer(-1);
                if (inputBufferIndex >= 0) {
                    pts = computePresentationTime(generateIndex);
                    ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
                    inputBuffer.clear();
                    inputBuffer.put(input);
                    mediaCodec.queueInputBuffer(inputBufferIndex, 0, input.length, pts, 0);
                    generateIndex = generateIndex+1;
                }

                Log.e("111111111111","H264 BBBBB333333");
                MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 500);
                while (outputBufferIndex >= 0) {
                    Log.e("111111111111","H264 BBBBB44444");
//                    if(m_framerate==20){
//                        usTime=usTime+50000;
//                    }else {
//                        usTime=usTime+66666;
//                    }

//                    usTime=System.nanoTime()/1000;
                    usTime=usTime+timeInterval;

                    //Log.i("Av2cEncoder", "Get H264 Buffer Success! flag = "+bufferInfo.flags+",pts = "+bufferInfo.presentationTimeUs+"");
                    ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
                    byte[] outData = new byte[bufferInfo.size];
                    outputBuffer.get(outData);
                    if(bufferInfo.flags == MediaCodec.BUFFER_FLAG_CODEC_CONFIG){
                        configbyte = new byte[bufferInfo.size];
                        configbyte = outData;
                        FrameDataHelper.getInstance().nalysisSpsaPps(configbyte);
                        byte[] sps=FrameDataHelper.getInstance().getSps();
                        byte[] pps=FrameDataHelper.getInstance().getPps();
                       // Log.e("111111111111","H264 BBBBB22222");
                        LWMediaMuxer.getInstance().Init(m_width,m_height,m_framerate,mSound,sps,pps);
                        if(mSound){
                        	  LWMediaMuxer.getInstance().RecordPcm();
                        }

                    }else if(bufferInfo.flags == MediaCodec.BUFFER_FLAG_SYNC_FRAME ){
                        byte[] keyframe=null;

                            firstSps=false;
                            keyframe = new byte[bufferInfo.size + configbyte.length];
                            System.arraycopy(configbyte, 0, keyframe, 0, configbyte.length);
                            System.arraycopy(outData, 0, keyframe, configbyte.length, outData.length);

                        MediaCodec.BufferInfo bufferInfo2 = new MediaCodec.BufferInfo();
                        bufferInfo2.offset = 0;
                        bufferInfo2.size = keyframe.length;
                        bufferInfo2.flags = MediaCodec.BUFFER_FLAG_SYNC_FRAME ;
                        bufferInfo2.presentationTimeUs = usTime;
                        LWMediaMuxer.getInstance().SetVideoIdr(true);

//                        byte[] sps = new byte[]{0x67, 0x42, (byte) 0x00, 0x28, (byte) 0xE9, 0x00, (byte) 0xA0, 0x0B, (byte) 0x75, (byte) 0xC4, (byte) 0x80, 0x03, 0x6E, (byte) 0xE8, 0x00, (byte) 0xCD, (byte) 0xFE,
//                                0x60, 0x0D, (byte) 0x88, 0x10, (byte) 0x94};
//                        byte[] pps = new byte[]{0x68, (byte) 0xCE, 0x31, (byte) 0x52};
//
//
//                        byte[] keyframe2=new byte[keyframe.length-21 + sps.length+pps.length];
//
//                        System.arraycopy(sps, 0,  keyframe2, 0, sps.length);
//                        System.arraycopy(pps, 0,  keyframe2, sps.length, pps.length);
//                        System.arraycopy(keyframe, 21,  keyframe2, sps.length+pps.length, keyframe.length-21);

                        ByteBuffer buffer = ByteBuffer.wrap(keyframe);
                        LWMediaMuxer.getInstance().onVideoTrack(buffer, bufferInfo2,usTime);
//                        outputStream.write(keyframe, 0, keyframe.length);
                    }else{
                        MediaCodec.BufferInfo bufferInfo2 = new MediaCodec.BufferInfo();
                        bufferInfo2.offset = 0;
                        bufferInfo2.size = outData.length;
                        bufferInfo2.presentationTimeUs = usTime;
                        bufferInfo2.flags = 0 ;
//                        LWMediaMuxer.getInstance().SetVideoIdr(true);

//
//                        byte[] outData2 = new byte[outData.length-21];
//                        System.arraycopy(outData, 21,  outData2, 0, outData2.length);

                        ByteBuffer buffer = ByteBuffer.wrap(outData);
                        LWMediaMuxer.getInstance().onVideoTrack(buffer, bufferInfo2,usTime);
//                        System.arraycopy(outData, 0,  output, pos, outData.length);
//                        pos=pos+outData.length;

                       // Log.e("111111111111","H264 BBBBB");
//                        outputStream.write(outData, 0, outData.length);
                    }

                    mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
                    outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 500);
                }

            } catch (Throwable t) {
                t.printStackTrace();
                return -1;
            }
        }

        return pos;
    }

//
//    @SuppressLint("NewApi")
//    public byte[] offerEncoder(byte[] input) {
//        int count = 0;
//        byte[] output = null;
//        int pos = 0;
//// swapYV12toI420(input, yuv420, m_width, m_height);
//        try {
//            ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
//            Log.i("inputBufferssize", "" + inputBuffers.length);
//            ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();
//            int inputBufferIndex = mediaCodec.dequeueInputBuffer(0);
//
//            Log.i("inputBufferIndex", inputBufferIndex + "");
//            if (inputBufferIndex >= 0) {
//                ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
//                inputBuffer.clear();
//                inputBuffer.put(input);
//                mediaCodec.queueInputBuffer(inputBufferIndex, 0, input.length,
//                        0, 0);
//            }
//
//            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
//
//            int outputBufferIndex = 0;
//            while (count <= 10) {
//                outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo,
//                        10000);
//                Log.i("outputBufferIndex", outputBufferIndex + "");
//                if (outputBufferIndex == -1) {
//                    count++;
//                } else {
//                    break;
//                }
//            }
//            if (outputBufferIndex >= 0) {
//                ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
//                byte[] outData = new byte[bufferInfo.size];
//// Log.i("h264.size", outData.length + "");
//                output = new byte[outData.length];
//                outputBuffer.get(outData);
//                if (m_info != null) {
//                    System.arraycopy(outData, 0, output, pos, outData.length);
//                    pos += outData.length;
//                } else {
//                    ByteBuffer spsPpsBuffer = ByteBuffer.wrap(outData);
//                    if (spsPpsBuffer.getInt() == 0x00000001) {
//                        m_info = new byte[outData.length];// 缂佹鍏涚粩瀵告暜瑜庨弳鐔煎箲閿?
//                        System.arraycopy(outData, 0, m_info, 0, outData.length);
//                    }
//                }
//
//                mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
//                mediaCodec.flush();
//            }
//            int key = output[4] & 0x1F;
//            if (key == 5) // key frame2
//            {
//                System.arraycopy(output, 0, input, 0, pos);
//
//                output = new byte[output.length + m_info.length];
//
//                System.arraycopy(m_info, 0, output, 0, m_info.length);
//                System.arraycopy(input, 0, output, m_info.length, pos);
//                pos += m_info.length;
//            }
//
//        } catch (Throwable t) {
//            t.printStackTrace();
//        }
//
//           if(output!=null){
//               usTime=System.nanoTime()/1000;
//               MediaCodec.BufferInfo bufferInfo2 = new MediaCodec.BufferInfo();
//               bufferInfo2.offset = 0;
//               bufferInfo2.size = output.length;
//               bufferInfo2.flags = MediaCodec.BUFFER_FLAG_SYNC_FRAME ;
//               bufferInfo2.presentationTimeUs = usTime;
//               LWMediaMuxer.getInstance().SetVideoIdr(true);
//               ByteBuffer buffer = ByteBuffer.wrap(output);
//               LWMediaMuxer.getInstance().onVideoTrack(buffer, bufferInfo2,usTime);
//           }
//
//
//        return output;
//    }



}

