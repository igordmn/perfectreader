package com.dmi.perfectreader.render;

public class RenderChild {
    public final float x;
    public final float y;
    public final RenderObject object;

    public RenderChild(float x, float y, RenderObject object) {
        this.x = x;
        this.y = y;
        this.object = object;
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    public RenderObject object() {
        return object;
    }
}
