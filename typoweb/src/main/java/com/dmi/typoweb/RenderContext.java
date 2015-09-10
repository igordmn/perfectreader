package com.dmi.typoweb;

import android.opengl.GLES20;

import javax.annotation.concurrent.NotThreadSafe;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_RENDERBUFFER;
import static android.opengl.GLES20.GL_SCISSOR_TEST;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBindRenderbuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDisable;
import static com.google.common.base.Preconditions.checkState;

/**
 * Should created and destroyed in Open GL thread
 */
@NotThreadSafe
class RenderContext {
    final long nativeRenderContext;
    boolean destroyed = false;

    public RenderContext() {
        nativeRenderContext = nativeCreateRenderContext();
        resetGLState();
    }

    public void destroy() {
        checkState(!destroyed, "Already destroyed");
        nativeDestroyRenderContext(nativeRenderContext);
        destroyed = true;
    }

    public void resetGLState() {
        // necessary because skia don't cleanup buffer binding
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindRenderbuffer(GL_RENDERBUFFER, 0);
        glBindTexture(GL_TEXTURE_2D, 0);
        glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
        glDisable(GL_SCISSOR_TEST);
    }

    void checkCanUse() {
        checkState(!destroyed);
        checkState(nativeRenderContext != 0, "need call attachGL for renderContext in opengl thread");
    }

    private static native long nativeCreateRenderContext();
    private static native void nativeDestroyRenderContext(long nativeRenderContext);
}
