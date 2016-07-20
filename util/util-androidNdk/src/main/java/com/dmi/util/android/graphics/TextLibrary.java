package com.dmi.util.android.graphics;

public class TextLibrary {
    private static int CACHE_MAX_FACES = 4;
    private static int CACHE_MAX_SIZES = 4;
    private static int CACHE_MAX_BYTES = 1024 * 1024 * 8;  // 8 MB

    private final long nativePtr = nativeNewTextLibrary(CACHE_MAX_FACES, CACHE_MAX_SIZES, CACHE_MAX_BYTES);

    public void destroy() {
        nativeDestroyTextLibrary(nativePtr);
    }

    public void getGlyphIndices(FontFaceID facePath, char[] chars, int[] indices) {
        nativeGetGlyphIndices(nativePtr, facePath.nativePtr, chars, indices);
    }

    public void renderGlyphs(TextConfig config, int[] glyphIndices, float[] coordinates, PixelBuffer pixelBuffer) {
        nativeRenderGlyphs(
                nativePtr, glyphIndices, coordinates,
                config.faceID.nativePtr, config.sizeInPixels, config.color,
                pixelBuffer.nativePtr
        );
    }

    private native long nativeNewTextLibrary(int cacheMaxFaces, int cacheMaxSizes, int cacheMaxBytes);
    private native void nativeDestroyTextLibrary(long libraryPtr);

    private native void nativeGetGlyphIndices(long libraryPtr, long faceIDPtr, char[] chars, int[] indices);
    private native void nativeRenderGlyphs(long libraryPtr,
            int[] glyphIndices, float[] coordinates,
            long facePathPtr, float sizeInPixels, int color,
            long surfaceBufferPtr
    );
}