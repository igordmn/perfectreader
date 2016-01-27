package com.dmi.perfectreader.render;

import static java.util.Collections.emptyList;

public class RenderBreak extends RenderObject {
    public RenderBreak(float width, float height) {
        super(width, height, emptyList());
    }

    @Override
    public boolean canPartiallyPainted() {
        return true;
    }
}
