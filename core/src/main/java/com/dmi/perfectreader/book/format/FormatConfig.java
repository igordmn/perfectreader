package com.dmi.perfectreader.book.format;

public class FormatConfig {
    private final float paragraphIndent;
    private final float paragraphTopMargin;

    public FormatConfig(float paragraphIndent, float paragraphTopMargin) {
        this.paragraphIndent = paragraphIndent;
        this.paragraphTopMargin = paragraphTopMargin;
    }

    public float paragraphIndent() {
        return paragraphIndent;
    }

    public float paragraphTopMargin() {
        return paragraphTopMargin;
    }
}
