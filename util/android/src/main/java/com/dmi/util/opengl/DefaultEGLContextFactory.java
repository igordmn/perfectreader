package com.dmi.util.opengl;

import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

import static com.dmi.util.opengl.Graphics.EGL_CONTEXT_CLIENT_VERSION;
import static com.dmi.util.opengl.Graphics.throwEGLException;
import static javax.microedition.khronos.egl.EGL10.EGL_NONE;

public class DefaultEGLContextFactory implements GLSurfaceView.EGLContextFactory {
    private final int eglContextClientVersion;

    public DefaultEGLContextFactory(int eglContextClientVersion) {
        this.eglContextClientVersion = eglContextClientVersion;
    }

    public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig config) {
        int[] attributes = {
                EGL_CONTEXT_CLIENT_VERSION,
                eglContextClientVersion,
                EGL_NONE
        };

        return egl.eglCreateContext(display, config, EGL10.EGL_NO_CONTEXT,
                                    eglContextClientVersion != 0 ? attributes : null);
    }

    public void destroyContext(EGL10 egl, EGLDisplay display,
                               EGLContext context) {
        if (!egl.eglDestroyContext(display, context)) {
            throwEGLException(egl, "eglDestroyContext error");
        }
    }
}