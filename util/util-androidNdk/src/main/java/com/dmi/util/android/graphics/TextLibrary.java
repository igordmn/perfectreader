package com.dmi.util.android.graphics;

public class TextLibrary {
    private static int CACHE_MAX_FACES = 4;
    private static int CACHE_MAX_SIZES = 4;
    private static int CACHE_MAX_BYTES = 1024 * 1024 * 6;  // 8 MB

    private final long nativePtr = nativeNewTextLibrary(CACHE_MAX_FACES, CACHE_MAX_SIZES, CACHE_MAX_BYTES);

    public void destroy() {
        nativeDestroyTextLibrary(nativePtr);
    }

    public void getGlyphIndices(FontFaceID faceID, char[] chars, int[] indices) {
        nativeGetGlyphIndices(nativePtr, faceID.nativePtr, chars, indices);
    }

    /**
     * @param fontConfig не создавайте каждый раз fontConfig заново, а кэшируйте. т.к. TextLibrary хранит кэш глифов, привязанный к ссылке на fontConfig
     */
    public void renderGlyphs(FontConfig fontConfig, int[] glyphIndices, float[] coordinates, PixelBuffer pixelBuffer) {
        nativeRenderGlyphs(
                nativePtr, glyphIndices, coordinates,
                fontConfig.nativePtr,
                pixelBuffer.nativePtr
        );
    }

    private native long nativeNewTextLibrary(int cacheMaxFaces, int cacheMaxSizes, int cacheMaxBytes);
    private native void nativeDestroyTextLibrary(long libraryPtr);

    private native void nativeGetGlyphIndices(long libraryPtr, long faceIDPtr, char[] chars, int[] indices);
    private native void nativeRenderGlyphs(long libraryPtr,
            int[] glyphIndices, float[] coordinates,
            long fontConfigPtr,
            long surfaceBufferPtr
    );
}