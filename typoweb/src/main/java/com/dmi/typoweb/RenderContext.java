package com.dmi.typoweb;

import javax.annotation.concurrent.NotThreadSafe;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.glBindBuffer;
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

        // necessary because skia don't cleanup buffer binding
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public void destroy() {
        checkState(!destroyed, "Already destroyed");
        nativeDestroyRenderContext(nativeRenderContext);
        destroyed = true;
    }

    void checkCanUse() {
        checkState(!destroyed);
        checkState(nativeRenderContext != 0, "need call attachGL for renderContext in opengl thread");
    }

    private static native long nativeCreateRenderContext();
    private static native void nativeDestroyRenderContext(long nativeRenderContext);
}
