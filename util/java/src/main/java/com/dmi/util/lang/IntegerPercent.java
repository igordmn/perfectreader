package com.dmi.util.lang;

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
        long longValue = (long) value;
        long longMaxValue = (long) maxValue;
        long longHundred = (long) HUNDRED;
        return longMaxValue != 0 ? (int) (longValue * longHundred / longMaxValue) : 0;
    }

    public static int multiply(int percent, int value) {
        checkArgument(percent >= 0);
        long longPercent = (long) percent;
        long longValue = (long) value;
        long longHundred = (long) HUNDRED;
        return (int) ((longPercent * longValue + longHundred - 1) / longHundred);
    }
}
