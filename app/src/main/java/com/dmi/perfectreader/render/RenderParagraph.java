package com.dmi.perfectreader.render;

import java.util.List;

public class RenderParagraph extends RenderObject {
    public RenderParagraph(float width, float height, List<RenderChild> children) {
        super(width, height, children);
    }

    @Override
    public boolean canPartiallyPainted() {
        return true;
    }
}
