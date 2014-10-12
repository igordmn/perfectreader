package com.dmi.perfectreader.book.content;

import com.dmi.perfectreader.book.position.Position;
import com.dmi.perfectreader.book.position.Range;

import javax.annotation.concurrent.Immutable;

import static com.google.common.base.Preconditions.checkArgument;

@Immutable
public final class Content {
    private final Range range;
    private final Text text;
    private final Size size;

    public Content(Range range, Text text, Size size) {
        checkArgument(range.end().more(range.begin()));
        this.size = size;
        this.range = range;
        this.text = text;
    }

    public static Content emptyContent(Position begin, Position end) {
        return new Content(Range.range(begin, end), Text.EMPTY, Size.ZERO);
    }

    public Text text() {
        return text;
    }

    public Range range() {
        return range;
    }

    public Size size() {
        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Content content = (Content) o;

        return range.equals(content.range) &&
                size.equals(content.size) &&
                text.equals(content.text);
    }

    @Override
    public int hashCode() {
        int result = range.hashCode();
        result = 31 * result + text.hashCode();
        result = 31 * result + size.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Content{" +
                "range=" + range +
                ", text=" + text +
                ", size=" + size +
                '}';
    }
}
