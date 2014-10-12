package com.dmi.perfectreader.book.content;

import java.util.Arrays;

import javax.annotation.concurrent.Immutable;

import static com.google.common.base.Preconditions.checkArgument;

@Immutable
public final class Text {
    public static final Text EMPTY = new Text(new int[]{}, new float[]{}, Size.ZERO);

    private final int[] codepoints;
    private final float[] coordinates;
    private final Size size;

    public Text(int[] codepoints, float[] coordinates, Size size) {
        checkArgument(coordinates.length == 2 * codepoints.length);
        this.codepoints = codepoints;
        this.coordinates = coordinates;
        this.size = size;
    }

    public int[] codepoints() {
        return codepoints;
    }

    public float[] coordinates() {
        return coordinates;
    }

    public Size size() {
        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Text text = (Text) o;

        return Arrays.equals(codepoints, text.codepoints) &&
                Arrays.equals(coordinates, text.coordinates) &&
                size.equals(text.size);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(codepoints);
        result = 31 * result + Arrays.hashCode(coordinates);
        result = 31 * result + size.hashCode();
        return result;
    }
}
