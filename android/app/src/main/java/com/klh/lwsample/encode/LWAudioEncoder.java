package com.klh.lwsample.encode;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by Andy on 2018/3/16.
 */

public class LWAudioEncoder {
    private String MIME_TYPE="audio/mp4a-latm";
    private MediaCodec mMediaCodec;
    //    File file = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "audio001.aac");
//    FileOutputStream fops=null;
    private boolean aacEnableAdts;
    /**
     * 初始化AAC编码器
     */
    public boolean Init(boolean aacEnableAdts) {
        try {
            mMediaCodec = MediaCodec.createEncoderByType(MIME_TYPE);
            MediaFormat mediaFormat = new MediaFormat();
            mediaFormat.setString(MediaFormat.KEY_MIME, MIME_TYPE);
            mediaFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);
            mediaFormat.setInteger(MediaFormat.KEY_SAMPLE_RATE, 8000);
            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 128000);
            mediaFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectMain);
            mMediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mMediaCodec.start();
            this.aacEnableAdts =aacEnableAdts;
//            try {
//                if(file.exists()){
//                    file.delete();
//                }
//                fops = new FileOutputStream(file);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
          //  Log.e("","AudioEncoder create mediaEncode suc");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
       // Log.e("","AudioEncoder create mediaEncode failed");
        return false;
    }


    public int encode(byte[] src, int srcLen, long timeus, ArrayList<AudioDataBean> audiodateList) {
        int nIndex = 0;
        try {
            ByteBuffer[] inputBuffers = mMediaCodec.getInputBuffers();
            ByteBuffer[] outputBuffers = mMediaCodec.getOutputBuffers();
            int inputBufferIndex = mMediaCodec.dequeueInputBuffer(-1);
            if (inputBufferIndex >= 0) {
                ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
                inputBuffer.clear();
                inputBuffer.put(src, 0, srcLen);
                mMediaCodec.queueInputBuffer(inputBufferIndex, 0, srcLen, timeus, 0);
            }
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            int outputBufferIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, 0);
            if (outputBufferIndex>=0) {
                while (outputBufferIndex >= 0) {
                    if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                        bufferInfo.size = 0;
                    }
                    if (bufferInfo.size != 0) {
                        int length=bufferInfo.size;
                        if(!aacEnableAdts){
                            length=length+7;
                        }
                        byte[] mFrameByte;
                        mFrameByte=new byte[length];
                        if(!aacEnableAdts){
                            addADTStoPacket(mFrameByte,length);
                        }

                        ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
                        AudioDataBean data = new AudioDataBean();
                        if(!aacEnableAdts){
                            outputBuffer.get(mFrameByte, 7, bufferInfo.size);
                        }else {
                            outputBuffer.get(mFrameByte,0, bufferInfo.size);
                        }
                        outputBuffer.clear();
                        data.nCount =length;
                        data.data = mFrameByte;
                        audiodateList.add(data);
                        nIndex =nIndex+1;
//                        try {
//                            fops.write(mFrameByte);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                    }
                    mMediaCodec.releaseOutputBuffer(outputBufferIndex, false);
                    outputBufferIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, 0);
                }
            } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                outputBuffers = mMediaCodec.getOutputBuffers();
                Log.i("","AudioEncoder: INFO_OUTPUT_BUFFERS_CHANGED = " + outputBuffers.length);
            } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                MediaFormat mMediaFormat = mMediaCodec.getOutputFormat();
                Log.i("","AudioEncoder: INFO_OUTPUT_FORMAT_CHANGED = " + mMediaFormat.toString());
            } else if (outputBufferIndex < 0) {
                Log.i("","AudioEncoder: outputBufferIndex < 0");
            } else if(outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER){
                Log.i("","AudioEncoder: INFO_TRY_AGAIN_LATER");
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage() != null) {
               Log.e("",e.getMessage());
            }
        }
        return nIndex;
    }

    private void addADTStoPacket(byte[] packet, int packetLen) {
        int profile = 1;
        int freqIdx = 11;
        int chanCfg = 1;
        packet[0] = (byte)0xFF;
        packet[1] = (byte)0xF9;
        packet[2] = (byte)(((profile)<<6) + (freqIdx<<2) +((chanCfg& 0x4)>>2));
        packet[3] = (byte)(((chanCfg & 0x3)<<6) + ((packetLen & 0x1800)>>11));
        packet[4] = (byte)((packetLen & 0x7FF) >> 3);
        packet[5] = (byte)(((packetLen & 7) << 5) + 0x1F);
        packet[6] = (byte)0xFC;

    }

    // 关闭编码器
    public void unInit() {
        synchronized (MediaCodec.class) {
            if (mMediaCodec != null) {
                mMediaCodec.stop();
                mMediaCodec.release();
//                try {
//                    fops.flush();
//                    fops.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                mMediaCodec = null;
                Log.i("","AudioEncoder 关闭AAC编码成功");
            }
        }
    }
}
