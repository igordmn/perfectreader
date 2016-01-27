package com.dmi.perfectreader.layout.config;

public class LayoutArea {
    private final float width;
    private final float height;

    private LayoutArea(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public float width() {
        return width;
    }

    public float height() {
        return height;
    }

    public static LayoutArea unlimited() {
        return new LayoutArea(Float.MAX_VALUE, Float.MAX_VALUE);
    }

    public static LayoutArea limited(float width, float height) {
        return new LayoutArea(width, height);
    }

    public static LayoutArea widthLimited(float width) {
        return new LayoutArea(width, Float.MAX_VALUE);
    }

    public static LayoutArea heightLimited(float height) {
        return new LayoutArea(Float.MAX_VALUE, height);
    }
}
