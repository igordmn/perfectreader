package com.dmi.perfectreader.book.content;

import javax.annotation.concurrent.Immutable;

import static com.google.common.base.Preconditions.checkArgument;

@Immutable
public class Size {
    public static final Size ZERO = size(0, 0);

    private final float width;
    private final float height;

    private Size(float width, float height) {
        checkArgument(width >= 0);
        checkArgument(height >= 0);
        this.width = width;
        this.height = height;
    }

    public static Size size(float width, float height) {
        return new Size(width, height);
    }

    public float width() {
        return width;
    }

    public float height() {
        return height;
    }

    @Override
    public String toString() {
        return width + " x " + height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Size size = (Size) o;

        return Float.compare(size.height, height) == 0 &&
                Float.compare(size.width, width) == 0;

    }

    @Override
    public int hashCode() {
        int result = (width != +0.0f ? Float.floatToIntBits(width) : 0);
        result = 31 * result + (height != +0.0f ? Float.floatToIntBits(height) : 0);
        return result;
    }
}
