package com.klh.lwsample.view.gles;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Andy on 2018/3/15.
 */

public class YUVGLRender implements GLSurfaceView.Renderer {

    private static String TAG = "YUVGLRender";

    private int U_INDEX = 0;
    private int V_INDEX = 0;
    private int LENGTH = 0;
    private int LENGTH_4 = 0;

    private int g_width = 0;
    private int g_height = 0;

    private int mViewWidth, mViewHight;



    private ByteBuffer yBuffer;
    private ByteBuffer uBuffer;
    private ByteBuffer vBuffer;


    private  byte[] glYdata, glUdata, glVdata;//Y U V数据

    public YUVGLRender() {
    }
    
    public void onRelease() {
		// TODO Auto-generated method stub
    	  glYdata = new byte[1];
          glUdata = new byte[1];
          glVdata = new byte[1];
          if(mYUVDirectDrawer!=null){
        	  mYUVDirectDrawers.remove(mYUVDirectDrawer);
              mYUVDirectDrawer=null;
              mYUVDirectDrawer2=null;
              mYUVDirectDrawers=null;
          }
          if(yBuffer!=null){
        	  yBuffer.clear();
              uBuffer.clear();
               vBuffer.clear();
               yBuffer=null;
               uBuffer=null;
                vBuffer=null; 
          }
         
         
	}



    private List<YUVDirectDrawer> mYUVDirectDrawers;
    YUVDirectDrawer mYUVDirectDrawer2;
    YUVDirectDrawer mYUVDirectDrawer;
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mViewWidth = width;
        mViewHight = height;
        if(mYUVDirectDrawer==null){
            mYUVDirectDrawer=new YUVDirectDrawer(1);
            mYUVDirectDrawer2 =new YUVDirectDrawer(2);

            mYUVDirectDrawers.add(mYUVDirectDrawer);
        }

        GLES20.glViewport(0, 0, width, height);
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mYUVDirectDrawers = new ArrayList<YUVDirectDrawer>();
    }
    public void setVRView(){
//        YUVDirectDrawer mYUVDirectDrawer2=new YUVDirectDrawer(2);
//        mYUVDirectDrawer2.setFromCamera(false);
        mYUVDirectDrawers.add(mYUVDirectDrawer2);

    }

    public void setPlaneView(){
        mYUVDirectDrawers.remove(1);
    }

    @Override
    public final void onDrawFrame(GL10 gl) {
        if (g_width == 0 || g_height == 0)
            return;
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT| GLES20.GL_DEPTH_BUFFER_BIT);
        if(mYUVDirectDrawers!=null){
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
        }
       
    }

    public void setVideoBuffer(byte[] buffer, int buffersize, int width, int height) {
    	  
    	if(glYdata==null){
    		glYdata = new byte[4096*2160];
            glUdata = new byte[4096*2160 / 4];
            glVdata = new byte[4096*2160 / 4];
    	}
        if (g_width != width || g_height != height) {
            g_width = width;
            g_height = height;

            U_INDEX = width * height;
            V_INDEX = width * height * 5 / 4;
            LENGTH = width * height;
            LENGTH_4 = width * height / 4;
            yBuffer = ByteBuffer.wrap(glYdata, 0, LENGTH);
            uBuffer = ByteBuffer.wrap(glUdata, 0, LENGTH_4);
            vBuffer = ByteBuffer.wrap( glVdata , 0, LENGTH_4);
        }
        System.arraycopy(buffer, 0, glYdata, 0, LENGTH);
        System.arraycopy(buffer, U_INDEX,glUdata, 0, LENGTH_4);
        System.arraycopy(buffer, V_INDEX,  glVdata , 0, LENGTH_4);
    }


}
