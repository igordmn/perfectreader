package com.dmi.perfectreader.render;

import java.util.List;

public class RenderLine extends RenderObject {
    public RenderLine(float width, float height, List<RenderChild> children) {
        super(width, height, children);
    }

    @Override
    public boolean canPartiallyPainted() {
        return false;
    }
}
