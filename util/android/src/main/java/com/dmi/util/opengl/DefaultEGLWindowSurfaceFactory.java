package com.dmi.util.opengl;

import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

import static com.dmi.util.opengl.Graphics.throwEGLException;

public class DefaultEGLWindowSurfaceFactory implements GLSurfaceView.EGLWindowSurfaceFactory {
    public EGLSurface createWindowSurface(EGL10 egl, EGLDisplay display,
                                          EGLConfig config, Object nativeWindow) {
        return egl.eglCreateWindowSurface(display, config, nativeWindow, null);
    }

    public void destroySurface(EGL10 egl, EGLDisplay display,
                               EGLSurface surface) {
        if (!egl.eglDestroySurface(display, surface)) {
            throwEGLException(egl, "eglDestroySurface error");
        }
    }
}