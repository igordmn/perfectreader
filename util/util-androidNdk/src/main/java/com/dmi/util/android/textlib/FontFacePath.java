package com.dmi.util.android.textlib;

import java.io.File;

public class FontFacePath {
    final long nativePtr;
    public final File file;
    public final int index;

    public FontFacePath(File file, int index) {
        this.nativePtr = nativeNewFontFace(file.getAbsolutePath(), index);
        this.file = file;
        this.index = index;
    }

    @SuppressWarnings("ThrowFromFinallyBlock")
    @Override
    protected void finalize() throws Throwable {
        try {
            nativeDestroyFontFace(nativePtr);
        } finally {
            super.finalize();
        }
    }

    private native long nativeNewFontFace(String filePath, int index);
    private native void nativeDestroyFontFace(long facePtr);
}