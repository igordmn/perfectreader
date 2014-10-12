package com.dmi.perfectreader.book.format.shape;

import com.dmi.perfectreader.book.font.FontFace;
import com.dmi.perfectreader.book.font.FreetypeLibrary;

import java.util.Arrays;

public class HarfbuzzShaper implements Shaper {
    private FreetypeLibrary freetypeLibrary;

    public HarfbuzzShaper(FreetypeLibrary freetypeLibrary) {
        this.freetypeLibrary = freetypeLibrary;
    }

    private static native long nativeInitHbFont(long ftcManager, long ftcScaler);

    private static native long nativeInitHbBuffer();

    private static native void nativeDestroyHbFont(long hbFont);

    private static native void nativeDestroyHbBuffer(long hbBuffer);

    private static native void nativeShape(long hbBuffer, long hbFont,
                                           char[] chars, int offset, int length);

    private static native int[] nativeCodepoints(long hbBuffer);

    private static native int[] nativeClusters(long hbBuffer);

    private static native float[] nativeAdvanceX(long hbBuffer);

    private static native float[] nativeAdvanceY(long nativeHbBuffer);

    private static native float[] nativeWidths(long hbBuffer, long hbFont);

    private static native float[] nativeFontMetrics(long ftcManager, long ftcScaler);

    @Override
    public Shape shape(FontFace fontFace, char[] chars, int offset, int length) {
        synchronized (freetypeLibrary.freetypeMutex) {
            long nativeHbBuffer = nativeInitHbBuffer();
            long ftcScaler = freetypeLibrary.ftcScaler(fontFace);
            long nativeHbFont = nativeInitHbFont(freetypeLibrary.ftcManager, ftcScaler);
            nativeShape(nativeHbBuffer, nativeHbFont, chars, offset, length);
            int[] glyphs = nativeCodepoints(nativeHbBuffer);
            int[] charIndices = nativeClusters(nativeHbBuffer);
            int[] glyphIndices = glyphIndices(charIndices, offset, length);
            float[] advanceX = nativeAdvanceX(nativeHbBuffer);
            float[] advanceY = nativeAdvanceY(nativeHbBuffer);
            float[] widths = nativeWidths(nativeHbBuffer, nativeHbFont);
            float[] fontMetrics = nativeFontMetrics(freetypeLibrary.ftcManager, ftcScaler);
            float ascent = fontMetrics[0];
            float descent = fontMetrics[1];
            float linegap = fontMetrics[2];
            nativeDestroyHbFont(nativeHbFont);
            nativeDestroyHbBuffer(nativeHbBuffer);
            return new Shape(chars, offset, length, glyphs,
                    charIndices, glyphIndices,
                    advanceX, advanceY, widths,
                    ascent, descent, linegap);
        }
    }

    private int[] glyphIndices(int[] charIndices, int offset, int length) {
        int[] glyphIndices = new int[length];
        Arrays.fill(glyphIndices, -1);
        for (int i = charIndices.length - 1; i >= 0; i--) {
            glyphIndices[charIndices[i] - offset] = i;
        }
        for (int i = 1; i < length; i++) {
            if (glyphIndices[i] == -1) {
                glyphIndices[i] = glyphIndices[i - 1];
            }
        }
        return glyphIndices;
    }
}
