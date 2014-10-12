package com.dmi.perfectreader.book.format.shape;

public class Shape {
    private final char[] chars;
    private final int offset;
    private final int length;
    private final int[] glyphs;
    private final int[] charIndices;
    private final int[] glyphIndices;
    private final float[] advanceX;
    private final float[] advanceY;
    private final float[] widths;
    private final float ascent;
    private final float descent;
    private final float linegap;

    public Shape(char[] chars, int offset, int length, int[] glyphs,
                 int[] charIndices,
                 int[] glyphIndices,
                 float[] advanceX,
                 float[] advanceY,
                 float[] widths,
                 float ascent,
                 float descent,
                 float linegap) {
        this.chars = chars;
        this.offset = offset;
        this.length = length;
        this.glyphs = glyphs;
        this.charIndices = charIndices;
        this.glyphIndices = glyphIndices;
        this.advanceX = advanceX;
        this.advanceY = advanceY;
        this.widths = widths;
        this.ascent = ascent;
        this.descent = descent;
        this.linegap = linegap;
    }

    public char[] chars() {
        return chars;
    }

    public int offset() {
        return offset;
    }

    public int length() {
        return length;
    }

    public int[] glyphs() {
        return glyphs;
    }

    public int[] charIndices() {
        return charIndices;
    }

    public int[] glyphIndices() {
        return glyphIndices;
    }

    public float[] advanceX() {
        return advanceX;
    }

    public float[] advanceY() {
        return advanceY;
    }

    public float[] widths() {
        return widths;
    }

    public float ascent() {
        return ascent;
    }

    public float descent() {
        return descent;
    }

    public float linegap() {
        return linegap;
    }

    public int size() {
        return glyphs.length;
    }
}
