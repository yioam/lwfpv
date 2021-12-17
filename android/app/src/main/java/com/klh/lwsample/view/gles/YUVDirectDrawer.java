package com.klh.lwsample.view.gles;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Andy on 2018/3/21.
 */

public class YUVDirectDrawer {

    private static String TAG = "YUVDirectDrawer";
    // Shader.vert
    public static String VERTEX_SHADER =
            "uniform mat4 uMVPMatrix;    \n"
            +"attribute vec4 vPosition;    \n"
            + "attribute vec2 a_texCoord;	\n"
            + "varying vec2 tc;		\n"
            + "void main()                  \n"
            + "{                            \n"
            + "   gl_Position =uMVPMatrix *  vPosition;  \n"
            + "	  tc = a_texCoord;	\n" + "}                            \n";

    public static String FRAG_SHADER = "precision mediump float;\n"
            + "uniform sampler2D SamplerY;                 \n"
            + "uniform sampler2D SamplerU;                 \n"
            + "uniform sampler2D SamplerV;                 \n"
            + "varying vec2 tc;                         \n"
            + "void main()                                  \n"
            + "{                                            \n"
            + "  vec4 c = vec4((texture2D(SamplerY, tc).r - 16./255.) * 1.164);\n"
            + "  vec4 U = vec4(texture2D(SamplerU, tc).r - 128./255.);\n"
            + "  vec4 V = vec4(texture2D(SamplerV, tc).r - 128./255.);\n"
            + "  c += V * vec4(1.596, -0.813, 0, 0);\n"
            + "  c += U * vec4(0, -0.392, 2.017, 0);\n"
            + "  c.a = 1.0;\n"
            + "  gl_FragColor = c;\n" + "}                                            \n";

    private int mProgramObject;

    private int mPositionLoc;
    private int mTexCoordLoc;
    private int yTexture;
    private int uTexture;
    private int vTexture;

    private int[] yTextureNames;
    private int[] uTextureNames;
    private int[] vTextureNames;

    private static FloatBuffer vertexBuffer;
    private static ShortBuffer drawListBuffer;

    private final float[] mVerticesData = {-1.f, 1.f, 0.0f, // Position 0
            0.0f, 0.0f, // TexCoord 0
            -1.f, -1.f, 0.0f, // Position 1
            0.0f, 1.0f, // TexCoord 1
            1.f, -1.f, 0.0f, // Position 2
            1.0f, 1.0f, // TexCoord 2
            1.f, 1.f, 0.0f, // Position 3
            1.0f, 0.0f // TexCoord 3
    };

    private final short[] drawOrder = {0, 1, 2, 0, 2, 3};
    public float[] mMVP = new float[16];
    private int mMVPMatrixHandle;
    public YUVDirectDrawer(int texture){


        if (vertexBuffer == null) {
            vertexBuffer = ByteBuffer.allocateDirect(mVerticesData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
            vertexBuffer.put(mVerticesData).position(0);

        }
        if (drawListBuffer == null) {
            drawListBuffer = ByteBuffer.allocateDirect(drawOrder.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
            drawListBuffer.put(drawOrder).position(0);

        }
        mProgramObject = loadProgram(VERTEX_SHADER, FRAG_SHADER);

        // Get the attribute locations
        mPositionLoc = GLES20.glGetAttribLocation(mProgramObject, "vPosition");
        mTexCoordLoc = GLES20.glGetAttribLocation(mProgramObject, "a_texCoord");
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramObject, "uMVPMatrix");

        // GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        yTexture = GLES20.glGetUniformLocation(mProgramObject, "SamplerY");
        yTextureNames = new int[1];
        GLES20.glGenTextures(1, yTextureNames, 0);

        // GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        uTexture = GLES20.glGetUniformLocation(mProgramObject, "SamplerU");
        uTextureNames = new int[1];
        GLES20.glGenTextures(1, uTextureNames, 0);

        // GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        vTexture = GLES20.glGetUniformLocation(mProgramObject, "SamplerV");
        vTextureNames = new int[1];
        GLES20.glGenTextures(1, vTextureNames, 0);

        // GLES20.glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
//        GLES20.glClearColor(0f, 0f, 0f, 0f);

//        IntBuffer ib = IntBuffer.allocate(1);
//        GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, ib);

        mat4f_LoadOrtho(-1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f, mMVP);
//        MAX_TEXTURE_SIZE = ib.get(0) - 100;
    }



    public void draw(int g_width, int g_height, ByteBuffer yBuffer, ByteBuffer uBuffer, ByteBuffer vBuffer){
//        mMVP[5] = -Math.abs(mMVP[5]);
            // Use the program object
            GLES20.glUseProgram(mProgramObject);
            // Load the vertex position
            vertexBuffer.position(0);
            GLES20.glVertexAttribPointer(mPositionLoc, 3, GLES20.GL_FLOAT, false, 5 * 4, vertexBuffer);
            // GLES20.glVertexAttribPointer(mPositionLoc, 2, GLES20.GL_FLOAT, false,
            // 5*4, squareBuffer);
            // Load the texture coordinate
            vertexBuffer.position(3);
            GLES20.glVertexAttribPointer(mTexCoordLoc, 2, GLES20.GL_FLOAT, false, 5 * 4, vertexBuffer);
            // GLES20.glVertexAttribPointer(mTexCoordLoc, 2, GLES20.GL_FLOAT, false,
            // 5*4, coordBuffer);

            GLES20.glEnableVertexAttribArray(mPositionLoc);
            GLES20.glEnableVertexAttribArray(mTexCoordLoc);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, yTextureNames[0]);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, g_width, g_height, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, yBuffer);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            // GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
            // GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, yTextureNames[0]);
            GLES20.glUniform1i(yTexture, 0);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, uTextureNames[0]);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, g_width / 2, g_height / 2, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, uBuffer);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            // GLES20.glActiveTexture(GLES20.GL_TEXTURE1+1);
            // GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, uTextureNames[0]);
            GLES20.glUniform1i(uTexture, 1);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, vTextureNames[0]);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, g_width / 2, g_height / 2, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, vBuffer);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            // GLES20.glActiveTexture(GLES20.GL_TEXTURE1+2);
            // GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, vTextureNames[0]);
            GLES20.glUniform1i(vTexture, 2);


        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVP, 0);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
            // GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);


    }

    public void resetMatrix() {
        mat4f_LoadOrtho(-1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f, mMVP);
    }
    public void setLeftMatrix() {
        Matrix.setIdentityM(mMVP, 0);
        float scaleX = 1f / 2f;
        float scaleY = 1f / 2f;

        Matrix.scaleM(mMVP, 0, scaleX, scaleY, 0);
        Matrix.translateM(mMVP, 0, -1f , 1f / 8f, 0f);
    }
    public void setRightMatrix(){
        Matrix.setIdentityM(mMVP, 0);
        float scaleX = 1f / 2f;
        float scaleY = 1f / 2f;

        Matrix.scaleM(mMVP, 0, scaleX, scaleY, 0);
        Matrix.translateM(mMVP, 0, 1f , 1f / 8f, 0f);
//        mat4f_LoadOrtho(-0.5f, 0.5f, -0.5f, 0.5f, -0.5f, 0.5f, mMVP);

    }


    public static void mat4f_LoadOrtho(float left, float right, float bottom, float top, float near, float far, float[] mout) {
        float r_l = right - left;
        float t_b = top - bottom;
        float f_n = far - near;
        float tx = -(right + left) / (right - left);
        float ty = -(top + bottom) / (top - bottom);
        float tz = -(far + near) / (far - near);

        mout[0] = 2.0f / r_l;
        mout[1] = 0.0f;
        mout[2] = 0.0f;
        mout[3] = 0.0f;

        mout[4] = 0.0f;
        mout[5] = 2.0f / t_b;
        mout[6] = 0.0f;
        mout[7] = 0.0f;

        mout[8] = 0.0f;
        mout[9] = 0.0f;
        mout[10] = -2.0f / f_n;
        mout[11] = 0.0f;

        mout[12] = tx;
        mout[13] = ty;
        mout[14] = tz;
        mout[15] = 1.0f;
    }




    public static int loadProgram(String vertShaderSrc, String fragShaderSrc) {

        int vertexShader;
        int fragmentShader;
        int programObject;
        int[] linked = new int[1];

        // Load the vertex/fragment shaders
        vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertShaderSrc);
        if (vertexShader == 0) {
            return 0;
        }

        fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragShaderSrc);
        if (fragmentShader == 0) {
            GLES20.glDeleteShader(vertexShader);
            return 0;
        }

        // Create the program object
        programObject = GLES20.glCreateProgram();

        if (programObject == 0) {
            return 0;
        }

        GLES20.glAttachShader(programObject, vertexShader);
        GLES20.glAttachShader(programObject, fragmentShader);

        // Link the program
        GLES20.glLinkProgram(programObject);

        // Check the link status
        GLES20.glGetProgramiv(programObject, GLES20.GL_LINK_STATUS, linked, 0);

        if (linked[0] == 0) {
            Log.e(TAG, "Error linking program:");
            Log.e(TAG, GLES20.glGetProgramInfoLog(programObject));
            GLES20.glDeleteProgram(programObject);
            return 0;
        }

        // Free up no longer needed shader resources
        GLES20.glDeleteShader(vertexShader);
        GLES20.glDeleteShader(fragmentShader);

        return programObject;
    }

    public static int loadShader(int type, String shaderSrc) {

        int shader;
        int[] compiled = new int[1];

        // Create the shader object
        shader = GLES20.glCreateShader(type);
        if (shader == 0) {
            return 0;
        }
        // Load the shader source
        GLES20.glShaderSource(shader, shaderSrc);
        // Compile the shader
        GLES20.glCompileShader(shader);
        // Check the compile status
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);

        if (compiled[0] == 0) {
            Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            return 0;
        }
        return shader;
    }
}
