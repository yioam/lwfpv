package com.klh.lwsample.view;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public abstract class BaseGLView extends GLSurfaceView implements
        GLSurfaceView.Renderer{

    protected static final String TAG = "BaseGLView";

    protected Context mContext;

    protected int mViewWidth;
    protected int mViewHeight;

//    protected int mPreviewWidth;
//    protected int mPreviewHeight;

    protected ByteBuffer mFullViewVertices;
    protected ByteBuffer mFullTexVertices;

    protected boolean mRendering = false;

    public BaseGLView(Context context) {
        this(context,null);
    }

    public BaseGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
        //setLayoutParams(new RelativeLayout.LayoutParams(1080,1920));
        setPreserveEGLContextOnPause(true);
        setEGLContextClientVersion(2);

    }

    /**
     * used for subClass to  init their shaders
     */
    protected abstract  void whenSurfaceCreate();

    protected abstract void callWhenSurfaceChanged(int width,int height);

    protected abstract void showOwnFrame();

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        final byte FULL_QUAD_COORDS[] = {-1, 1, -1, -1, 1, 1, 1, -1};
        mFullViewVertices = ByteBuffer.allocateDirect(FULL_QUAD_COORDS.length).order(ByteOrder.nativeOrder());
        mFullViewVertices.put(FULL_QUAD_COORDS).position(0);

        final byte[] FULL_TEXTURE_COORDS={
                0,0,
                1,0,
                0,1,
                1,1
        };
        mFullTexVertices= ByteBuffer.allocateDirect(FULL_TEXTURE_COORDS.length).order(ByteOrder.nativeOrder());
        mFullTexVertices.put(FULL_TEXTURE_COORDS).position(0);

        whenSurfaceCreate();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mViewWidth=width;
        mViewHeight=height;
        GLES20.glViewport(0, 0, mViewWidth, mViewHeight);

//        mPreviewWidth=CameraInstance.getInstance().getPreviewWidth();
//        mPreviewHeight=CameraInstance.getInstance().getPreviewHeight();

        callWhenSurfaceChanged(width,height);


        Log.e("ZL","mViewHeight="+mViewHeight+" mViewWidth="+mViewWidth);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        showOwnFrame();
    }
}
