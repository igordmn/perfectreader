package com.dmi.util.android.graphics;

public class TextConfig {
    public FontFacePath facePath;
    public float sizeInPixels;
    public int color;

    public TextConfig() {
    }

    public TextConfig(FontFacePath facePath, float sizeInPixels, int color) {
        this.facePath = facePath;
        this.color = color;
        this.sizeInPixels = sizeInPixels;
    }
}