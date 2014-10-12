package com.dmi.perfectreader.graphic;

import android.annotation.SuppressLint;
import android.graphics.Color;

import com.dmi.perfectreader.book.content.Text;
import com.dmi.perfectreader.book.font.FontFace;
import com.dmi.perfectreader.book.font.FreetypeLibrary;

import static android.opengl.GLES20.GL_ALPHA;
import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glTexImage2D;
import static android.opengl.GLES20.glTexParameteri;
import static com.dmi.perfectreader.util.opengl.GLObjects.glGenTexture;
import static com.dmi.perfectreader.util.opengl.Graphics.floatColor;

public class GLText {
    private final FreetypeLibrary freetypeLibrary;
    private AlphaColorPlane alphaColorPlane;

    private int width;
    private int height;
    private int textureId;

    public GLText(FreetypeLibrary freetypeLibrary, AlphaColorPlane alphaColorPlane) {
        this.freetypeLibrary = freetypeLibrary;
        this.alphaColorPlane = alphaColorPlane;
    }

    private static native void drawSymbols(int width, int height,
                                           long ftLibrary,
                                           long ftcSBitCache,
                                           long ftcScaler,
                                           int[] codepoints, float[] glyphCoordinates);

    public void init() {
        textureId = glGenTexture();
        alphaColorPlane.init();
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureId);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, width, height, 0, GL_ALPHA, GL_UNSIGNED_BYTE, null);
    }

    @SuppressLint("NewApi")
    public void updateTexture(Text text) {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureId);

        FontFace fontFace = FontFace.DEFAULT;
        synchronized (freetypeLibrary.freetypeMutex) {
            drawSymbols(width, height, freetypeLibrary.ftcManager, freetypeLibrary.ftcSBitCache,
                    freetypeLibrary.ftcScaler(fontFace), text.codepoints(), text.coordinates());
        }
    }

    public void draw(float[] matrix) {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureId);
        alphaColorPlane.draw(floatColor(Color.BLACK), matrix);
    }
}
