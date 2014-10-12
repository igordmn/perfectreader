package com.dmi.perfectreader.util.opengl;

import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES20.glGenFramebuffers;
import static android.opengl.GLES20.glGenTextures;

public abstract class GLObjects {
    public static int glGenTexture() {
        final int[] ids = new int[1];
        glGenTextures(1, ids, 0);
        return ids[0];
    }

    public static int glGenBuffer() {
        final int[] ids = new int[1];
        glGenBuffers(1, ids, 0);
        return ids[0];
    }

    public static int glGenFramebuffer() {
        final int[] ids = new int[1];
        glGenFramebuffers(1, ids, 0);
        return ids[0];
    }
}
