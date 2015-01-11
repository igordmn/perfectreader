package com.dmi.perfectreader.util.lang;

import java.io.Serializable;
import java.text.DecimalFormat;

import static com.google.common.base.Preconditions.checkArgument;

public abstract class IntegerPercent implements Serializable {
    public static int ZERO = 0;
    public static int HUNDRED = Integer.MAX_VALUE;

    public static String toString(int percent) {
        return new DecimalFormat("0.00").format(toDouble(percent) * 100) + "%";
    }

    public static double toDouble(int percent) {
        return (double) percent / Integer.MAX_VALUE;
    }

    public static int toIntegerPercent(double doubleValue) {
        checkArgument(doubleValue >= 0 && doubleValue <= 1.0);
        return (int) (Integer.MAX_VALUE * doubleValue);
    }

    public static int valuePercent(int value, int maxValue) {
        checkArgument(value >= 0);
        checkArgument(maxValue >= value);
        return (int) ((double) value / maxValue * Integer.MAX_VALUE);
    }

    public static double multiply(int percent, double value) {
        return toDouble(percent) * value;
    }
}
