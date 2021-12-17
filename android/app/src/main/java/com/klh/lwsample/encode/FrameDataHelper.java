package com.klh.lwsample.encode;

/**
 * Created by Andy on 2019/6/13.
 * Email: 963069079@qq.com
 */

public class FrameDataHelper {
    // Coded slice of a non-IDR picture slice_layer_without_partitioning_rbsp( )
    public final static int NonIDR = 1;
    // Coded slice of an IDR picture slice_layer_without_partitioning_rbsp( )
    public final static int IDR = 5;
    // Supplemental enhancement information (SEI) sei_rbsp( )
    public final static int SEI = 6;
    // Sequence parameter set seq_parameter_set_rbsp( )
    public final static int SPS = 7;
    // Picture parameter set pic_parameter_set_rbsp( )
    public final static int PPS = 8;
    // Access unit delimiter access_unit_delimiter_rbsp( )
    public final static int AccessUnitDelimiter = 9;


    private static volatile FrameDataHelper mInstance = null;
    public static FrameDataHelper getInstance() {
        if (mInstance == null) {
            synchronized (FrameDataHelper.class) {
                mInstance = new FrameDataHelper();
            }
        }
        return mInstance;
    }

    private FrameDataHelper() {

    }



    byte[] sps=null;
     byte[] frame=null;
    private boolean hasSps=false;

     byte[] pps=null;
     private  boolean hasPps=false;
     private int position=0;


     public byte[] getSps(){
         return this.sps;
     }

     public byte[] getPps(){
         return this.pps;
     }


    public  boolean nalysisSpsaPps(byte[] byt){

        if(byt==null){
            return false;
        }
        int count=0;
        int frameHeadCount=0;
        for(int i=0;i<byt.length;i++){
            byte buf=byt[i];
            if(byteToInt(buf)==0){
                count=count+1;
                if(count==4){
                	count=count-1;
                }
            }else {
                if(count==3){
                    if(byteToInt(buf)==1){  //todo 这里找到一帧帧头了 00 00 00 01
                        count=0;
                        frameHeadCount=frameHeadCount+1;
                        if(frameHeadCount==2){  //这里代表找到了两个帧头了
                            frame=new byte[i-3];
                            System.arraycopy(byt,0,frame,0,i-3);
                            position=i-3;
                            if(isSps(frame)) {
                                sps = frame;
                                hasSps=true;
                                continue;
                            }
                            if(isPps(frame)){
                                pps = frame;
                                hasPps=true;
                                continue;
                            }
                        }

                        if(frameHeadCount==3){ //这里找到第三个帧头
                            frame=new byte[i-3-position];
                            System.arraycopy(byt,position,frame,0,i-3-position);
                            position=i-3;
                            if(isSps(frame)) {
                                sps = frame;
                                hasSps=true;
                                break;
                            }
                            if(isPps(frame)){
                                pps = frame;
                                hasPps=true;
                                break;
                            }
                        }

                    }
                }else if(i==byt.length-1){
                    count=0;
                    //已经有一个了，剩下那个到结尾都是另外一个了
                    if(hasSps){
                        frame=new byte[i-position+1];
                        System.arraycopy(byt,position,frame,0,i-position+1);
                        pps = frame;
                        hasPps=true;
                    }else {
                        frame=new byte[i-position+1];
                        System.arraycopy(byt,position,frame,0,i-position+1);
                        sps = frame;
                        hasSps=true;
                    }

                }else{
                	 count=0;
                }
            }
        }

        if(hasSps&&hasPps){
            return true;
        }
        return false;
    }



    private static boolean isSps(byte[] frame) {
        if (frame.length < 1) {
            return false;
        }
        // 5bits, 7.3.1 NAL unit syntax,
        // H.264-AVC-ISO_IEC_14496-10.pdf, page 44.
        //  7: SPS, 8: PPS, 5: I Frame, 1: P Frame
        int nal_unit_type = (frame[4] & 0x1f);
        return nal_unit_type == SPS;
    }

    private static boolean isPps(byte[] frame) {
        if (frame.length < 1) {
            return false;
        }
        // 5bits, 7.3.1 NAL unit syntax,
        // H.264-AVC-ISO_IEC_14496-10.pdf, page 44.
        //  7: SPS, 8: PPS, 5: I Frame, 1: P Frame
        int nal_unit_type = (frame[4] & 0x1f);
        return nal_unit_type == PPS;
    }

    //byte 与 int 的相互转换
    public static byte intToByte(int x) {
        return (byte) x;
    }

    public static int byteToInt(byte b) {
        //Java 总是把 byte 当做有符处理；我们可以通过将其和 0xFF 进行二进制与得到它的无符值
        return b & 0xFF;
    }

    public static final int byteArrayToInt_Little(byte byt[]) {
        if (byt.length == 1)
            return 0xff & byt[0];
        else if (byt.length == 2)
            return (0xff & byt[0]) | ((0xff & byt[1]) << 8);
        else if (byt.length == 4)
            return (0xff & byt[0]) | (0xff & byt[1]) << 8 | (0xff & byt[2]) << 16 | (0xff & byt[3]) << 24;
        else
            return 0;
    }
}
