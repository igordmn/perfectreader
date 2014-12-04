package com.dmi.perfectreader.util.opengl;

import android.content.res.Resources;
import android.graphics.Color;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static java.lang.String.format;

public class Graphics {
    public static final int BYTES_PER_FLOAT = 4;

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

    public static int compileShader(String strSource, int type) {
        int[] ids = new int[1];
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, strSource);
        GLES20.glCompileShader(shader);
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, ids, 0);
        if (ids[0] == 0) {
            throw new RuntimeException(format("Compile shader failed: %s", GLES20.glGetShaderInfoLog(shader)));
        }
        return shader;
    }

    public static int createProgram(Resources resources, int vertexShaderResId, int fragmentShaderResId) {
        String vertexShader = ResourceUtils.readTextRawResource(resources, vertexShaderResId);
        String fragmentShader = ResourceUtils.readTextRawResource(resources, fragmentShaderResId);
        return createProgram(vertexShader, fragmentShader);
    }

    public static int createProgram(String vertexShader, String fragmentShader) {
        int[] ids = new int[1];

        int vShader = compileShader(vertexShader, GLES20.GL_VERTEX_SHADER);
        if (vShader == 0) {
            throw new RuntimeException("Vertex shader failed");
        }
        int fShader = compileShader(fragmentShader, GLES20.GL_FRAGMENT_SHADER);
        if (fShader == 0) {
            throw new RuntimeException("Fragment shader failed");
        }

        int progId = GLES20.glCreateProgram();

        GLES20.glAttachShader(progId, vShader);
        GLES20.glAttachShader(progId, fShader);

        GLES20.glLinkProgram(progId);
        GLES20.glGetProgramiv(progId, GLES20.GL_LINK_STATUS, ids, 0);
        if (ids[0] <= 0) {
            throw new RuntimeException("Linking shader failed");
        }

        GLES20.glDeleteShader(vShader);
        GLES20.glDeleteShader(fShader);

        return progId;
    }
}
