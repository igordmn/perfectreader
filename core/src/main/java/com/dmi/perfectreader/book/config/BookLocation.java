package com.dmi.perfectreader.book.config;

import com.dmi.perfectreader.util.lang.LongPercent;

import java.io.Serializable;

public class BookLocation implements Serializable {
    private int segmentIndex;
    private LongPercent percent;

    public BookLocation(int segmentIndex, LongPercent percent) {
        this.segmentIndex = segmentIndex;
        this.percent = percent;
    }

    public int segmentIndex() {
        return segmentIndex;
    }

    public void setSegmentIndex(int segmentIndex) {
        this.segmentIndex = segmentIndex;
    }

    public LongPercent percent() {
        return percent;
    }

    public void setPercent(LongPercent percent) {
        this.percent = percent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BookLocation that = (BookLocation) o;

        return segmentIndex == that.segmentIndex && percent.equals(that.percent);
    }

    @Override
    public int hashCode() {
        int result = segmentIndex;
        result = 31 * result + percent.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format("[%s, %s]",
                segmentIndex,
                percent.toString()
        );
    }
}
