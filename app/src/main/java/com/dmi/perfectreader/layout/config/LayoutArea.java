package com.dmi.perfectreader.layout.config;

public class LayoutArea {
    private final float width;
    private final float height;

    public LayoutArea(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public float width() {
        return width;
    }

    public float height() {
        return height;
    }
}
