package com.dmi.perfectreader.util.lang;

import java.io.Serializable;
import java.text.DecimalFormat;

import static com.google.common.base.Preconditions.checkArgument;

// todo заменить везде LongPercent на long. Все операции производить с помощью static методов
public class LongPercent implements Serializable {
    public static LongPercent ZERO = new LongPercent(0);
    public static LongPercent HUNDRED = new LongPercent(Long.MAX_VALUE);

    private final long value;

    private LongPercent(long value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LongPercent that = (LongPercent) o;

        return value == that.value;
    }

    @Override
    public int hashCode() {
        return (int) (value ^ (value >>> 32));
    }

    @Override
    public String toString() {
        return new DecimalFormat("0.00").format(toDouble() * 100) + "%";
    }

    public double toDouble() {
        return (double) value / Long.MAX_VALUE;
    }

    public static LongPercent toLongPercent(double doubleValue) {
        checkArgument(doubleValue >= 0);
        return new LongPercent((long) (Long.MAX_VALUE * doubleValue));
    }

    public static LongPercent valuePercent(long value, int maxValue) {
        checkArgument(value >= 0);
        checkArgument(maxValue >= value);
        return new LongPercent((long) ((double) value / maxValue * Long.MAX_VALUE));
    }

    public double multiply(double value) {
        return toDouble() * value;
    }
}
