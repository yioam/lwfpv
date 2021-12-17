package com.klh.lwsample.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.klh.lwsample.MyApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class Utils {

    public static final String OBJECT_TRACK_MODEL = "M_track_dcfnet_64_16.model";
//    public static final String OBJECT_DETECT_MODEL = "M_detect_res18_1.4.2.model";
    public static final String BODY_OBJECT_DETECT_MODEL="M_Detect_Body_Hunter_1.4.0.model";

    public static String OBJECT_TRACK_MODEL_PATH = "";
//    public static String OBJECT_DETECT_MODEL_PATH  = "";
    public static String BODY_OBJECT_DETECT_PATH= "";

    private static final Context sContext= MyApplication.context;

    private static final String TAG = Utils.class.getSimpleName();

    public static void copyModelFile(Context context) {
        OBJECT_TRACK_MODEL_PATH = copyFileIfNeed(context, OBJECT_TRACK_MODEL);
//        OBJECT_DETECT_MODEL_PATH = copyFileIfNeed(context, OBJECT_DETECT_MODEL);
        BODY_OBJECT_DETECT_PATH=copyFileIfNeed(context,BODY_OBJECT_DETECT_MODEL);
    }

    /**
     * 閹风柉绀塧sset娑撳娈戝Ο鈥崇�烽弬鍥︽閸掔櫛ontext.getFilesDir()閻╊喖缍嶆稉锟�
     */
    public static String copyFileIfNeed(Context context, String modelName) {
        try {
            File modelFile = new File(context.getFilesDir(), modelName); // 姒涙顓荤�涙ê鍋嶉崷鈺燼ta/data/<application
            // name>/file閻╊喖缍嶆稉锟�
            InputStream is = context.getAssets().open(modelName);
            if (modelFile.length() == is.available()) {
                return modelFile.getAbsolutePath();
            }
            OutputStream os = new FileOutputStream(modelFile);
            byte[] buffer = new byte[1024];
            int length = is.read(buffer);
            while (length > 0) {
                os.write(buffer, 0, length);
                length = is.read(buffer);
            }
            os.flush();
            os.close();
            is.close();
            return modelFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getAssertData(Context context, String path) {
        try {
            InputStream stream = context.getAssets().open(path);
            int length = stream.available();
            byte[] data = new byte[length];
            stream.read(data);
            stream.close();
            return new String(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("NewApi") public static Point getScreenWidthAndHeight(){
        WindowManager wm = (WindowManager) sContext.getSystemService(Context.WINDOW_SERVICE);
        Point point=new Point();
        wm.getDefaultDisplay().getRealSize(point);
        return point;
    }

    public static float getDensityFloat(){
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) sContext.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metric);
        return metric.density;
    }


    /**
     * 閼惧嘲褰囬悩鑸碉拷浣圭埉閻ㄥ嫰鐝惔锟�
     * 瑜版捁绻栨稉鐙猟娑撳秴鐡ㄩ崷銊︽,閸婇棿璐熼梿锟�
     * */
    public static int  getStatusBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height","dimen","android");
        if(resourceId!=0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    /**
     * 閼惧嘲褰囩�佃壈鍩呴弽蹇曟畱妤傛ê瀹�
     * 瑜版捁绻栨稉鐙猟娑撳秴鐡ㄩ崷銊︽,閸婇棿璐熼梿锟�
     * */
    public static int getNavigationHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("navigation_bar_height","dimen","android");
        if(resourceId!=0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }


    /**
     * 1.閸掋倖鏌囩�佃壈鍩呴弽蹇旀Ц閸氾箑鐡ㄩ崷锟�
     * 2.閸掋倖鏌囩�佃壈鍩呴弽蹇旀Ц閸氾箑鍙ч梻锟�
     * */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static boolean navigationBarExist(Activity activity) {
        WindowManager windowManager = activity.getWindowManager();
        Display d = windowManager.getDefaultDisplay();

        //鐠佹儳顦惇鐔风杽鐎逛粙鐝�
        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        d.getRealMetrics(realDisplayMetrics);
        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        //閸愬懎顔愰崠鍝勭厵閻喎鐤勭�逛粙鐝�
        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);
        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }



    /**
     * 鐏忓摳ssets閻╊喖缍嶆稉瀣畱閺屾劒閲滈弬鍥︽閹存牞锟藉懏鏋冩禒璺恒仚闁帒缍婃穱婵嗙摠閸掔増婀伴崷鏉垮瘶閸氬秳绗呴惃鍒les閻╊喖缍嶆稉锟�
     *
     * @param ctxDealFile
     *            缁嬪绨稉濠佺瑓閺傦拷
     * @param path
     *            assets閻╊喖缍嶆稉顓犳畱閺屾劒閲滈惄顔肩秿閹存牗鏋冩禒璺烘倳
     * @return 婵″倹鐏夋径宥呭煑閹存劕濮涢崚娆掔箲閸ョ�焤ue閿涘苯銇戠拹銉ュ灟鏉╂柨娲杅alse
     */
    public static void deepFile(Context ctxDealFile, String path) {
        try {
            String str[] = ctxDealFile.getAssets().list(path);
            if (str.length > 0) {// 婵″倹鐏夐弰顖滄窗瑜帮拷
                File file = new File(ctxDealFile.getFilesDir() + "/" + path);
                if (!file.exists()) {
                    file.mkdirs();
                }
                for (String string : str) {
                    path = path + "/" + string;
                    deepFile(ctxDealFile, path);
                    path = path.substring(0, path.lastIndexOf('/'));
                }
            } else {// 婵″倹鐏夐弰顖涙瀮娴狅拷
                InputStream is = ctxDealFile.getAssets().open(path);
                File file = new File(ctxDealFile.getFilesDir() + "/" + path);
                if (file.exists()) {
                    return;
                }
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                while (true) {
                    int len = is.read(buffer);
                    if (len == -1) {
                        break;
                    }
                    fos.write(buffer, 0, len);
                }
                is.close();
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /** 鏉烆剙瀵瞲閸ф劖鐖�,opengl閸ф劖鐖ｇ化濠氬櫡闂堛垻娈戦崸鎰垼**/
    public static float normalizeXCoor(float x,int portViewWidth){
        x=(x-portViewWidth/2)/(portViewWidth/2);
        return x;
    }

    /** 鏉烆剙瀵瞴閸ф劖鐖�,opengl閸ф劖鐖ｇ化濠氬櫡闂堛垻娈戦崸鎰垼**/
    public static float normalizeYCoor(float y,int portViewHeight){
        y=(portViewHeight/2-y)/(portViewHeight/2);
        return y;
    }


    /**
     * 閼惧嘲褰囬幗鍕剼閺堣櫣娈戦懕姘卞妽閸栧搫鐓� (閻㈠娼伴弰顖滅彨閻╁娈戦幆鍛枌閿涳拷
     * @param  x                 閼辨氨鍔嶉崠鍝勭厵閻ㄥ嫬涔忔稉濠咁潡x鏉炴潙娼楅弽锟�
     * @param  y                 閼辨氨鍔嶉崠鍝勭厵閻ㄥ嫬涔忔稉濠咁潡y鏉炴潙娼楅弽锟�
     * @param  width             閼辨氨鍔嶉崠鍝勭厵閻ㄥ嫬顔旀惔锟�
     * @param  height            閼辨氨鍔嶉崠鍝勭厵閻ㄥ嫰鏆辨惔锟�
     * @param  viewWith          缂佹ê鍩楅悽濠氭桨閻ㄥ垍iew閻ㄥ嫬顔旀惔锟�(婵′總urfaceView)
     * @param  viewHeight        缂佹ê鍩楅悽濠氭桨閻ㄥ垍iew閻ㄥ嫰鐝惔锟�(婵′總urfaceView)
     * @return 鏉╂柨娲栭惌鈺佽埌閸栧搫鐓欓惃鍑磂ct
     */
    public static Rect calculateTapArea2(int x, int y, int width, int height, int viewWith, int viewHeight){
        //Log.e("ZL","鐠佸墽鐤嗛懕姘卞妽==========>("+x+","+y+")"+" width="+width+" height="+height+" viewWidth="+viewWith+" viewheight="+viewHeight);
        Rect rect = new Rect(x,y,x+width,y+height);
        int left = ((int)(rect.left * 2000.0f / viewWith) - 1000);
        int top = ((int)(rect.top * 2000.0f/ viewHeight )- 1000);
        int right = ((int)(rect.right * 2000.0f / viewWith) - 1000);
        int bottom = ((int)(rect.bottom * 2000.0f / viewHeight) - 1000);
        // 婵″倹鐏夌搾鍛毉娴滐拷(-1000,1000)閸掞拷(1000, 1000)閻ㄥ嫯瀵栭崶杈剧礉閸掓瑤绱扮�佃壈鍤ч惄鍛婃簚瀹曗晜绨�
        left = left < -1000 ? -1000 : left;
        top = top < -1000 ? -1000 : top;
        right = right > 1000 ? 1000 : right;
        bottom = bottom > 1000 ? 1000 : bottom;
        //Log.e("ZL","left=="+left+" right="+right+" top="+top+" bottom="+bottom);
        if(left<right&&top<bottom) {  //闂冨弶顒泃op >1000閻ㄥ嫭鍎忛崘锟�
            return new Rect(left, top, right, bottom);
        }else {
            return null;
        }
    }


    /**
     * 鐏忓棙鎲氶崓蹇旀簚妫板嫯顫嶉崚鍡氶哺閻滃洣鑵戦惃鍕潒閸ユ儳娼楅弽鍥ㄥ床缁犳鍨氱憴鍡楁禈闁插矂娼伴惃鍕綏閺嶏拷
     * @param   mPreviewCoordinate  妫板嫯顫嶉崸鎰垼缁鍣烽棃銏犳綏閺嶏拷
     * @param   viewRadio           閹广垻鐣婚幋鎰潒閸ユ儳娼楅弽鍥╅兇閹碉拷娑旀ü浜掗惃鍕槷閻滐拷
     * @return  鏉╂柨娲栫拋锛勭暬閹碉拷瀵版娈戠憴鍡楁禈閸ф劖鐖ｇ化鑽ゆ畱閸ф劖鐖�
     */
    public static int prewiewToViewCoordinate(float mPreviewCoordinate,float viewRadio){
        return  (int)(mPreviewCoordinate*viewRadio);
    }



}
