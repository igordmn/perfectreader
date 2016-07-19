package com.dmi.util.android.graphics;

public class TextConfig {
    public FontFaceID faceID;
    public float sizeInPixels;
    public int color;

    public TextConfig() {
    }

    public TextConfig(FontFaceID faceID, float sizeInPixels, int color) {
        this.faceID = faceID;
        this.color = color;
        this.sizeInPixels = sizeInPixels;
    }
}