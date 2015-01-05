package com.dmi.perfectreader.book.config;

import com.dmi.perfectreader.util.lang.IntegerPercent;

import java.io.Serializable;

public class BookLocation implements Serializable {
    private final int segmentIndex;
    private final int percent;

    public BookLocation(int segmentIndex, int percent) {
        this.segmentIndex = segmentIndex;
        this.percent = percent;
    }

    public int segmentIndex() {
        return segmentIndex;
    }

    public int percent() {
        return percent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BookLocation that = (BookLocation) o;

        return segmentIndex == that.segmentIndex && percent == that.percent;
    }

    @Override
    public int hashCode() {
        int result = segmentIndex;
        result = 31 * result + percent;
        return result;
    }

    @Override
    public String toString() {
        return String.format("[%s, %s]",
                segmentIndex,
                IntegerPercent.toString(percent)
        );
    }
}
