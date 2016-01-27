package com.dmi.perfectreader.render;

import java.util.List;

public class RenderBox extends RenderObject {
    public RenderBox(float width, float height, List<RenderChild> children) {
        super(width, height, children);
    }

    @Override
    public boolean canPartiallyPainted() {
        return true;
    }
}
