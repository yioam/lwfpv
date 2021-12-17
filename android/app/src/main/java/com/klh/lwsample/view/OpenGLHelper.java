package com.klh.lwsample.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.klh.lwsample.R;
import com.klh.lwsample.util.Utils;
import com.klh.lwsample.view.gles.YUVDirectDrawer;
import com.sensetime.trackapi.model.ObjectInfo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class OpenGLHelper {
    private int mCameraTextureID;
    private int mHighLightTextureID;


//    private final Shader mCameraShader = new Shader();

    private final Shader mPointShader=new Shader();
    private final Shader mHighLightShader=new Shader();

    private float[] mFaceRects;
    private FloatBuffer mFaceRectsBuffer;
    private FloatBuffer mCenterPointBuffer;

    private float[] mCenterPoint=new float[2];


    protected Context mContext;
    private int mLineLength=40;

//    YUVDirectDrawer mYUVDirectDrawer;
private List<YUVDirectDrawer> mYUVDirectDrawers;
    YUVDirectDrawer mYUVDirectDrawer2;
    YUVDirectDrawer mYUVDirectDrawer;
    /** opengl 閻ㄥ嫬鍨垫慨瀣瀹搞儰缍�**/
    public void OpenGLInit(Context context)throws Exception {
        mContext=context;
        mYUVDirectDrawers = new ArrayList<YUVDirectDrawer>();
        if(mYUVDirectDrawer==null){
            mYUVDirectDrawer=new YUVDirectDrawer(1);
            mYUVDirectDrawer2 =new YUVDirectDrawer(2);

            mYUVDirectDrawers.add(mYUVDirectDrawer);
        }


//        mCameraShader.setProgram(R.raw.camera_vshader, R.raw.camera_fshader, mContext);
        mPointShader.setProgram(R.raw.point_vshader, R.raw.point_fshader,mContext);
        mHighLightShader.setProgram(R.raw.highlight_vshader, R.raw.highlight_fshader,mContext);
        mFaceRects=new float[16*2];
        mFaceRectsBuffer= ByteBuffer.allocateDirect(mFaceRects.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCenterPointBuffer= ByteBuffer.allocateDirect(2*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        initCameraTexture();
        initBackgroundTexure();
    }

    public int getCameraTextureID(){
        return mCameraTextureID;
    }

    private void initCameraTexture() {
    }

    private void initBackgroundTexure(){
        if(mHighLightTextureID!=0)return;

        final BitmapFactory.Options options=new BitmapFactory.Options();
        options.inScaled=true;
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.highlight, options);
        int[] mTextureHandles = new int[1];
        GLES20.glGenTextures(1, mTextureHandles, 0);
        mHighLightTextureID = mTextureHandles[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mHighLightTextureID);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,0,bitmap,0);
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        bitmap.recycle();
    }

    public void setVRView(){
//        YUVDirectDrawer mYUVDirectDrawer2=new YUVDirectDrawer(2);
//        mYUVDirectDrawer2.setFromCamera(false);
        mYUVDirectDrawers.add(mYUVDirectDrawer2);

    }

    public void setPlaneView(){
        mYUVDirectDrawers.remove(1);
    }

    /** 缂佹ê鍩楅幗鍕剼婢跺瓨鏆熼幑锟�**/
    public void drawCameraFrameView(int g_width, int g_height, ByteBuffer yBuffer, ByteBuffer uBuffer, ByteBuffer vBuffer){
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT| GLES20.GL_DEPTH_BUFFER_BIT);
        for (int i = 0; i < mYUVDirectDrawers.size(); i++) {
            YUVDirectDrawer YUVDirectDrawer = mYUVDirectDrawers.get(i);
            if(mYUVDirectDrawers.size()==1){
                YUVDirectDrawer.resetMatrix();
            }else if (i == 0) {
                YUVDirectDrawer.setLeftMatrix();
            } else {
                YUVDirectDrawer.setRightMatrix();
            }
            YUVDirectDrawer.draw(g_width,g_height,yBuffer,uBuffer,vBuffer);
        }
//        mYUVDirectDrawer.draw(g_width,g_height,yBuffer,uBuffer,vBuffer);
    }
//    /** 缂佹ê鍩楅幗鍕剼婢跺瓨鏆熼幑锟�**/
//    public void drawCameraFrameView(ByteBuffer fullViewVertices,ByteBuffer fullTexVertices){
//        mCameraShader.useProgram();
//        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mCameraTextureID);
//        int aPosition= mCameraShader.getHandle("aPosition");
//        GLES20.glVertexAttribPointer(aPosition, 2, GLES20.GL_BYTE, false, 0,fullViewVertices);
//        int aTextureCoord= mCameraShader.getHandle("aTextureCoord");
//        GLES20.glVertexAttribPointer(aTextureCoord, 2, GLES20.GL_BYTE, false, 0,fullTexVertices);
//        GLES20.glEnableVertexAttribArray(aPosition);
//        GLES20.glEnableVertexAttribArray(aTextureCoord);
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
//        GLES20.glDisableVertexAttribArray(aPosition);
//        GLES20.glDisableVertexAttribArray(aTextureCoord);
//        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,0);
//        GLES20.glUseProgram(0);
//    }


    /** 缂佹ê鍩楅惌鈺佽埌濡楋拷 **/
    public void drawRect(ObjectInfo src, int width, int height){

        mFaceRects[0] = Utils.normalizeXCoor(src.objectRect.left, width);
        mFaceRects[1] = Utils.normalizeYCoor(src.objectRect.top, height);

        mFaceRects[2] = Utils.normalizeXCoor((src.objectRect.left + mLineLength), width);
        mFaceRects[3] = Utils.normalizeYCoor(src.objectRect.top, height);

        mFaceRects[4] = Utils.normalizeXCoor(src.objectRect.left, width);
        mFaceRects[5] = Utils.normalizeYCoor(src.objectRect.top, height);

        mFaceRects[6] = Utils.normalizeXCoor(src.objectRect.left, width);
        mFaceRects[7] = Utils.normalizeYCoor(src.objectRect.top + mLineLength, height);

        //the second point
        mFaceRects[8] = Utils.normalizeXCoor(src.objectRect.left + src.objectRect.width(), width);
        mFaceRects[9] = Utils.normalizeYCoor(src.objectRect.top, height);

        mFaceRects[10] = Utils.normalizeXCoor(src.objectRect.left + src.objectRect.width() - mLineLength, width);
        mFaceRects[11] = Utils.normalizeYCoor(src.objectRect.top, height);

        mFaceRects[12] = Utils.normalizeXCoor(src.objectRect.left + src.objectRect.width(), width);
        mFaceRects[13] = Utils.normalizeYCoor(src.objectRect.top, height);

        mFaceRects[14] = Utils.normalizeXCoor(src.objectRect.left + src.objectRect.width(), width);
        mFaceRects[15] = Utils.normalizeYCoor(src.objectRect.top + mLineLength, height);

        //the third point
        mFaceRects[16] = Utils.normalizeXCoor(src.objectRect.left + src.objectRect.width(), width);
        mFaceRects[17] = Utils.normalizeYCoor(src.objectRect.top + src.objectRect.height(), height);

        mFaceRects[18] = Utils.normalizeXCoor(src.objectRect.left + src.objectRect.width(), width);
        mFaceRects[19] = Utils.normalizeYCoor(src.objectRect.top + src.objectRect.height() - mLineLength, height);

        mFaceRects[20] = Utils.normalizeXCoor(src.objectRect.left + src.objectRect.width(), width);
        mFaceRects[21] = Utils.normalizeYCoor(src.objectRect.top + src.objectRect.height(), height);

        mFaceRects[22] = Utils.normalizeXCoor(src.objectRect.left + src.objectRect.width() - mLineLength, width);
        mFaceRects[23] = Utils.normalizeYCoor(src.objectRect.top + src.objectRect.height(), height);

        //the fouth point
        mFaceRects[24] = Utils.normalizeXCoor(src.objectRect.left, width);
        mFaceRects[25] = Utils.normalizeYCoor(src.objectRect.top + src.objectRect.height(), height);

        mFaceRects[26] = Utils.normalizeXCoor(src.objectRect.left, width);
        mFaceRects[27] = Utils.normalizeYCoor(src.objectRect.top + src.objectRect.height() - mLineLength, height);

        mFaceRects[28] = Utils.normalizeXCoor(src.objectRect.left, width);
        mFaceRects[29] = Utils.normalizeYCoor(src.objectRect.top + src.objectRect.height(), height);

        mFaceRects[30] = Utils.normalizeXCoor(src.objectRect.left + mLineLength, width);
        mFaceRects[31] = Utils.normalizeYCoor(src.objectRect.top + src.objectRect.height(), height);

        mFaceRectsBuffer.put(mFaceRects).position(0);
        mPointShader.useProgram();
        int pointSize = mPointShader.getHandle("pointSize");
        GLES20.glUniform1f(pointSize,1);
        int pointPosition=mPointShader.getHandle("pointPosition");
        GLES20.glVertexAttribPointer(pointPosition,2, GLES20.GL_FLOAT,false,0,mFaceRectsBuffer);
        GLES20.glLineWidth(5); //缁捐法娈戠�硅棄瀹�
        GLES20.glEnableVertexAttribArray(pointPosition);
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, mFaceRects.length/2);
        GLES20.glDisableVertexAttribArray(pointPosition);
        GLES20.glUseProgram(0);
    }

    /** 缂佹ê鍩楅惌鈺佽埌娑擃厼绺鹃悙锟�**/
    public void drawCenterPoint(float x,float y,int viewWidth,int viewHeight){

        mCenterPoint[0]=Utils.normalizeXCoor(x,viewWidth);
        mCenterPoint[1]=Utils.normalizeYCoor(y,viewHeight);


        mCenterPointBuffer.put(mCenterPoint).position(0);

        mPointShader.useProgram();
        int pointSize = mPointShader.getHandle("pointSize");
        GLES20.glUniform1f(pointSize,20);

        int pointPosition=mPointShader.getHandle("pointPosition");
        GLES20.glVertexAttribPointer(pointPosition,2, GLES20.GL_FLOAT,false,0,mCenterPointBuffer);
        GLES20.glEnableVertexAttribArray(pointPosition);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, mCenterPoint.length/2);
        GLES20.glDisableVertexAttribArray(pointPosition);
        GLES20.glUseProgram(0);

    }
}
