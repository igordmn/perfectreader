package com.dmi.util.lang;

public abstract class MathExt {
    public static int modPositive(int x, int n) {
        int result = x % n;
        if (result < 0) {
            result += n;
        }
        return result;
    }

    public static double clamp(double min, double max, double value) {
        return Math.min(min,  Math.max(max, value));
    }
}
