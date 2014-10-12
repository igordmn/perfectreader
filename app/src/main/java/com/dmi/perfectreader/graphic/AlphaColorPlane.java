package com.dmi.perfectreader.graphic;

import android.content.Context;

import com.dmi.perfectreader.R;
import com.dmi.perfectreader.util.opengl.Graphics;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniform4fv;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static com.dmi.perfectreader.util.opengl.Graphics.floatBuffer;

public class AlphaColorPlane {
    private final static int VERTEX_COUNT = 4;

    private final Context context;

    private int programId;
    private int coordVarHandle;
    private int mvpMatrixVarHandle;
    private int colorVarHandle;
    private int texVarHandle;
    private FloatBuffer vertexBuffer;

    public AlphaColorPlane(Context context) {
        this.context = context;
    }

    public void init() {
        programId = Graphics.createProgram(
                context.getResources(),
                R.raw.shader_text_vertex,
                R.raw.shader_text_fragment);

        coordVarHandle = glGetAttribLocation(programId, "coord");
        mvpMatrixVarHandle = glGetUniformLocation(programId, "u_MVPMatrix");
        colorVarHandle = glGetUniformLocation(programId, "color");
        texVarHandle = glGetUniformLocation(programId, "tex");
    }

    public void setSize(int width, int height) {
        vertexBuffer = floatBuffer(new float[]{
                0, 0, 0, 0,
                width, 0, 1, 0,
                0, height, 0, 1,
                width, height, 1, 1
        });
    }

    public void draw(FloatBuffer colorBuffer, float[] matrix) {
        glUseProgram(programId);

        glEnableVertexAttribArray(coordVarHandle);
        glVertexAttribPointer(coordVarHandle, 4,
                GL_FLOAT, false,
                0, vertexBuffer);
        glUniformMatrix4fv(mvpMatrixVarHandle, 1, false, matrix, 0);
        glUniform4fv(colorVarHandle, 1, colorBuffer);
        glUniform1i(texVarHandle, 0);

        glDrawArrays(GL_TRIANGLE_STRIP, 0, VERTEX_COUNT);
    }
}
