package com.dmi.perfectreader.render;

import android.graphics.Canvas;

import java.util.List;

public class RenderLine extends RenderObject {
    public RenderLine(float width, float height, List<RenderChild> children) {
        super(width, height, children);
    }

    @Override
    public boolean canPartiallyPainted() {
        return false;
    }

    @Override
    public void paintItself(RenderConfig config, Canvas canvas) {
    }
}
