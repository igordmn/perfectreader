package com.dmi.perfectreader.book.format;

import javax.annotation.concurrent.Immutable;

import static com.google.common.base.Preconditions.checkArgument;

@Immutable
public class PageConfig {
    private final float width;
    private final float height;
    private final float paddingTop;
    private final float paddingRight;
    private final float paddingBottom;
    private final float paddingLeft;

    public PageConfig(float width, float height, float paddingTop, float paddingRight, float paddingBottom, float paddingLeft) {
        checkArgument(width >= 0);
        checkArgument(height >= 0);
        checkArgument(width + paddingLeft + paddingRight >= 0);
        checkArgument(height + paddingTop + paddingRight >= 0);
        this.width = width;
        this.height = height;
        this.paddingTop = paddingTop;
        this.paddingRight = paddingRight;
        this.paddingBottom = paddingBottom;
        this.paddingLeft = paddingLeft;
    }

    public float width() {
        return width;
    }

    public float height() {
        return height;
    }

    public float paddingTop() {
        return paddingTop;
    }

    public float paddingRight() {
        return paddingRight;
    }

    public float paddingBottom() {
        return paddingBottom;
    }

    public float paddingLeft() {
        return paddingLeft;
    }
}
