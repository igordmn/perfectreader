package com.dmi.util.android.textlib;

import com.dmi.util.android.paint.PaintBuffer;

public class TextLibrary {
    private static int CACHE_MAX_FACES = 4;
    private static int CACHE_MAX_SIZES = 4;
    private static int CACHE_MAX_BYTES = 1024 * 1024 * 8;  // 8 MB

    private final long nativePtr = nativeNewTextLibrary(CACHE_MAX_FACES, CACHE_MAX_SIZES, CACHE_MAX_BYTES);

    public void destroy() {
        nativeDestroyTextLibrary(nativePtr);
    }

    public void getGlyphIndices(FontFacePath facePath, char[] chars, int[] indices) {
        nativeGetGlyphIndices(nativePtr, facePath.nativePtr, chars, indices);
    }

    public void renderGlyphs(FontFacePath facePath, int[] glyphIndices, float[] coordinates, TextConfig config, PaintBuffer paintBuffer) {
        nativeRenderGlyphs(
                nativePtr, glyphIndices, coordinates,
                facePath.nativePtr, config.sizeInPixels, config.color,
                paintBuffer.nativePtr
        );
    }

    private native long nativeNewTextLibrary(int cacheMaxFaces, int cacheMaxSizes, int cacheMaxBytes);
    private native void nativeDestroyTextLibrary(long libraryPtr);

    private native void nativeGetGlyphIndices(long libraryPtr, long facePathPtr, char[] chars, int[] indices);
    private native void nativeRenderGlyphs(long libraryPtr,
            int[] glyphIndices, float[] coordinates,
            long facePathPtr, float sizeInPixels, int color,
            long surfaceBufferPtr
    );
}