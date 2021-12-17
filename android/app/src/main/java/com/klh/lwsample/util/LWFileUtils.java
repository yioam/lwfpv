package com.klh.lwsample.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Andy on 2018/3/26.
 * Email: 963069079@qq.com
 */

public class LWFileUtils {
    /**
     * 鑾峰彇瑙嗛鏃堕暱
     *
     * @param filePath
     * @return Created by Andy
     */
    public static String getVideoTime(String filePath) {
        String timeLeng = "";
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            timeLeng = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION); // 鎾斁鏃堕暱鍗曚綅涓烘绉�
            int a = Integer.parseInt(timeLeng);
            int b = a / 1000;
            int c = b / 60;
            if (c >= 10) {
                if (b % 60 < 10) {
                    timeLeng = c + ":" + ":0" + b % 60;
                } else {
                    timeLeng = c + ":" + b % 60;
                }

            } else {
                if (b % 60 < 10) {
                    timeLeng = "0" + c + ":0" + b % 60;
                } else {
                    timeLeng = "0" + c + ":" + b % 60;
                }
            }

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return timeLeng;
    }


    /**
     * 淇濆瓨1080P鐨勫浘鐗囷紝鍘熸潵宸茬粡瀛樺湪浜�720P鐨勫浘鐗囦簡锛岄渶瑕佸浘鐗囪浆鍖栨垚1080P
     * */
    public static void save1080Pic(final String pathString){

        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap=raedPic(pathString);
                bitmap=zoom720to1080(bitmap);
                saveBitmap(pathString,bitmap);
            }
        }).start();

    }

    /**
     * 璇诲彇涓�寮犵収鐗� 杩斿洖涓�涓� Bitmap bitmap
     * */
    public static Bitmap raedPic(String pathString){
        Bitmap bitmap=null;
        bitmap= BitmapFactory.decodeFile(pathString);
        return bitmap;
    }

    /** Bitmap鏀惧ぇ鐨勬柟娉� */
    public static Bitmap zoom720to1080(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(1.5f, 1.5f); // 闀垮拰瀹芥斁澶х缉灏忕殑姣斾緥
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }

    public static boolean saveBitmap(String pathString,Bitmap bitmap){
//        String path = "/sdcard/" + "img-" + imagename + ".jpg";
        File fileP = new File(pathString);
        if(fileP.exists()){
            fileP.delete();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(pathString);
            if (fos != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.close();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }



}
