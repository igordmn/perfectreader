package com.dmi.perfectreader.render;

import android.graphics.Canvas;

import java.util.List;

public class RenderBox extends RenderObject {
    public RenderBox(float width, float height, List<RenderChild> children) {
        super(width, height, children);
    }

    @Override
    public boolean canPartiallyPainted() {
        return true;
    }

    @Override
    public void paintItself(RenderConfig config, Canvas canvas) {
    }
}
