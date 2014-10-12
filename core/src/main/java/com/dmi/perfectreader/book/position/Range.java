package com.dmi.perfectreader.book.position;


import java.io.Serializable;

import javax.annotation.concurrent.Immutable;

import static com.google.common.base.Preconditions.checkArgument;

@Immutable
public final class Range implements Serializable {
    final Position begin;
    final Position end;

    Range(Position begin, Position end) {
        checkArgument(end.moreOrEquals(begin));
        this.begin = begin;
        this.end = end;
    }

    public static Range range(Position begin, Position end) {
        return new Range(begin, end);
    }

    public Position begin() {
        return begin;
    }

    public Position end() {
        return end;
    }

    public Distance distance() {
        return new Distance(end.longPos - begin.longPos);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Range that = (Range) o;

        return begin.equals(that.begin) && end.equals(that.end);
    }

    @Override
    public String toString() {
        return String.format("[%s; %s)", begin.toString(), end.toString());
    }
}
