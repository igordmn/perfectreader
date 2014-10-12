package com.dmi.perfectreader.book.position;

import java.io.Serializable;
import java.text.DecimalFormat;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class Position implements Comparable<Position>, Serializable {
    public static final Position BEGIN = new Position(0);
    public static final Position END = new Position(Long.MAX_VALUE);

    final long longPos;

    Position(long longPos) {
        this.longPos = longPos;
    }

    public static Position toPosition(long localPosition, long maxPosition) {
        long longPos = Math.round(((double) localPosition / maxPosition) * Long.MAX_VALUE);
        return new Position(longPos);
    }

    public static Position percentToPosition(double percent) {
        return new Position(Math.round(Long.MAX_VALUE * percent));
    }

    public static Position min(Position p1, Position p2) {
        return p1.less(p2) ? p1 : p2;
    }

    public static Position max(Position p1, Position p2) {
        return p1.moreOrEquals(p2) ? p1 : p2;
    }

    public long toLocalPosition(long maxPosition) {
        return Math.round(maxPosition * percent());
    }

    public double percent() {
        return (double) longPos / Long.MAX_VALUE;
    }

    public Position plus(Distance distance) {
        long newLongPos = Long.MAX_VALUE - distance.longDistance >= longPos
                ? longPos + distance.longDistance
                : Long.MAX_VALUE;
        return new Position(newLongPos);
    }

    public Position minus(Distance distance) {
        long newLongPos = longPos >= distance.longDistance
                ? longPos - distance.longDistance
                : 0;
        return new Position(newLongPos);
    }

    public boolean inRange(Position begin, Position end) {
        return longPos >= begin.longPos && longPos < end.longPos;
    }

    public boolean inRange(Range range) {
        return inRange(range.begin(), range.end());
    }

    public boolean moreOrEquals(Position begin) {
        return longPos >= begin.longPos;
    }

    public boolean lessOrEquals(Position begin) {
        return longPos <= begin.longPos;
    }

    public boolean more(Position end) {
        return longPos > end.longPos;
    }

    public boolean less(Position end) {
        return longPos < end.longPos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Position that = (Position) o;

        return longPos == that.longPos;
    }

    @Override
    public int hashCode() {
        return (int) (longPos ^ (longPos >>> 32));
    }

    @Override
    public int compareTo(@Nonnull Position position) {
        return longPos < position.longPos ? -1 :
                longPos > position.longPos ? 1 :
                        0;
    }

    @Override
    public String toString() {
        return new DecimalFormat("0.00").format(percent() * 100) + "%";
    }
}
