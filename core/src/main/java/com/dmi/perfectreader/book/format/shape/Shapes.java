package com.dmi.perfectreader.book.format.shape;

public abstract class Shapes {
    public static int firstGlyphIndex(Shape shape, int charIndex) {
        return shape.glyphIndices()[charIndex];
    }

    public static int firstCharIndex(Shape shape, int glyphIndex) {
        return shape.charIndices()[glyphIndex] + shape.offset();
    }

    public static boolean isFirstGlyphOfChar(Shape shape, int glyphIndex) {
        int[] charIndices = shape.charIndices();
        return glyphIndex == 0 || charIndices[glyphIndex] > charIndices[glyphIndex - 1];
    }

    public static float glyphsWidth(Shape shape, int begin, int end) {
        float[] advanceX = shape.advanceX();
        float width = 0;
        for (int i = begin; i < end - 1; i++) {
            width += advanceX[i];
        }
        if (end > 0) {
            width += shape.widths()[end - 1];
        }
        return width;
    }

    public static float glyphsHeight(Shape shape) {
        return shape.ascent() - shape.descent() + shape.linegap();
    }
}
