package com.dmi.util.android.textlib;

public class TextConfig {
    public final FontFacePath facePath;
    public final float sizeInPixels;
    public final int color;

    public TextConfig(FontFacePath facePath, int color, int sizeInPixels) {
        this.facePath = facePath;
        this.color = color;
        this.sizeInPixels = sizeInPixels;
    }
}