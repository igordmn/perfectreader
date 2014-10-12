package com.dmi.perfectreader.util.opengl;

import android.content.res.Resources;
import android.graphics.Color;
import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Graphics {
    public static final int BYTES_PER_FLOAT = 4;

    private static final String LOG_TAG = Graphics.class.getName();

    public static FloatBuffer floatBuffer(float[] items) {
        FloatBuffer floatBuffer = ByteBuffer.allocateDirect(items.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        floatBuffer.put(items).position(0);
        return floatBuffer;
    }

    public static int vertexBuffer(FloatBuffer buffer) {
        final int buffers[] = new int[1];
        GLES20.glGenBuffers(1, buffers, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, buffer.capacity() * BYTES_PER_FLOAT,
                buffer, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        return buffers[0];
    }

    public static FloatBuffer floatColor(int color) {
        return floatBuffer(new float[]{
                Color.red(color) / 255F,
                Color.green(color) / 255F,
                Color.blue(color) / 255F,
                Color.alpha(color) / 255F
        });
    }

    public static int compileShader(String strSource, int iType) {
        Log.d(LOG_TAG, "compileShader");
        int[] compiled = new int[1];
        int iShader = GLES20.glCreateShader(iType);
        GLES20.glShaderSource(iShader, strSource);
        GLES20.glCompileShader(iShader);
        GLES20.glGetShaderiv(iShader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            Log.e("Load Shader Failed", "Compilation\n" + GLES20.glGetShaderInfoLog(iShader));
            return 0;    // todo throw exception
        }
        return iShader;
    }

    public static int createProgram(Resources resources, int vertexShaderResId, int fragmentShaderResId) {
        String vertexShader = ResourceUtils.readTextRawResource(resources, vertexShaderResId);
        String fragmentShader = ResourceUtils.readTextRawResource(resources, fragmentShaderResId);
        return createProgram(vertexShader, fragmentShader);
    }

    public static int createProgram(String vertexShader, String fragmentShader) {
        Log.d(LOG_TAG, "createProgram");
        int iVShader;
        int iFShader;
        int iProgId;
        int[] link = new int[1];
        iVShader = compileShader(vertexShader, GLES20.GL_VERTEX_SHADER);
        if (iVShader == 0) {
            Log.d("Load Program", "Vertex Shader Failed");
            return 0;    // todo throw exception
        }
        iFShader = compileShader(fragmentShader, GLES20.GL_FRAGMENT_SHADER);
        if (iFShader == 0) {
            Log.d("Load Program", "Fragment Shader Failed");
            return 0;    // todo throw exception
        }

        iProgId = GLES20.glCreateProgram();

        GLES20.glAttachShader(iProgId, iVShader);
        GLES20.glAttachShader(iProgId, iFShader);

        GLES20.glLinkProgram(iProgId);

        GLES20.glGetProgramiv(iProgId, GLES20.GL_LINK_STATUS, link, 0);
        if (link[0] <= 0) {
            Log.d("Load Program", "Linking Failed");
            return 0;   // todo throw exception
        }
        GLES20.glDeleteShader(iVShader);
        GLES20.glDeleteShader(iFShader);
        return iProgId;
    }
}
