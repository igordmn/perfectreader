package com.dmi.perfectreader.book.content;

import com.dmi.perfectreader.book.position.Position;
import com.dmi.perfectreader.book.position.Range;

import static com.google.common.base.Preconditions.checkArgument;

public class ContentBuilder {
    private Range range = Range.range(Position.BEGIN, Position.BEGIN);
    private int[] codepoints = new int[]{};
    private float[] coordinates = new float[]{};
    private Size size = Size.ZERO;

    public ContentBuilder size(Size size) {
        this.size = size;
        return this;
    }

    public ContentBuilder range(Range range) {
        this.range = range;
        return this;
    }

    public ContentBuilder appendFirst(Content content) {
        range = content.range();
        codepoints = content.text().codepoints().clone();
        coordinates = content.text().coordinates().clone();
        size = content.size();
        return this;
    }

    public ContentBuilder appendBelow(Content content) {
        Range addRange = content.range();
        int[] addCodepoints = content.text().codepoints();
        float[] addCoordinates = content.text().coordinates();
        Size addSize = content.size();

        checkArgument(addRange.begin().equals(range.end()));
        checkArgument(addSize.width() == size().width());

        int[] newCodepoints = new int[codepoints.length + addCodepoints.length];
        float[] newCoordinates = new float[coordinates.length + addCoordinates.length];

        System.arraycopy(codepoints, 0, newCodepoints, 0, codepoints.length);
        System.arraycopy(coordinates, 0, newCoordinates, 0, coordinates.length);
        System.arraycopy(addCodepoints, 0, newCodepoints, codepoints.length, addCodepoints.length);
        System.arraycopy(addCoordinates, 0, newCoordinates, coordinates.length, addCoordinates.length);

        // iterate over y index
        for (int i = coordinates.length + 1; i < newCoordinates.length; i += 2) {
            newCoordinates[i] += size.height();
        }

        range = Range.range(range.begin(), addRange.end());
        codepoints = newCodepoints;
        coordinates = newCoordinates;
        size = Size.size(size.width(), size.height() + addSize.height());

        return this;
    }

    public ContentBuilder offset(float x, float y) {
        for (int i = 0; i < coordinates.length; i += 2) {
            coordinates[i] += x;
        }
        for (int i = 1; i < coordinates.length; i += 2) {
            coordinates[i] += y;
        }
        return this;
    }

    public Content buildContent() {
        Text text = new Text(codepoints, coordinates, size);
        return new Content(range, text, size);
    }

    public Size size() {
        return size;
    }

    public Range range() {
        return range;
    }
}
