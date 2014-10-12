package com.dmi.perfectreader.book.position;

import java.io.Serializable;
import java.text.DecimalFormat;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class Distance implements Serializable {
    final long longDistance;

    Distance(long longDistance) {
        this.longDistance = longDistance;
    }

    public static Distance distance(Position a, Position b) {
        return new Distance(Math.abs(a.longPos - b.longPos));
    }

    public Distance multiple(double coefficient) {
        long newLongDistance = Long.MAX_VALUE / coefficient > longDistance
                ? (long) (longDistance * coefficient) :
                Long.MAX_VALUE;
        return new Distance(newLongDistance);
    }

    public boolean isNil() {
        return longDistance == 0;
    }

    @Override
    public String toString() {
        return new DecimalFormat("0.00").format((double) longDistance / Long.MAX_VALUE * 100) + "%";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Distance that = (Distance) o;

        return longDistance == that.longDistance;
    }

    @Override
    public int hashCode() {
        return (int) (longDistance ^ (longDistance >>> 32));
    }
}
