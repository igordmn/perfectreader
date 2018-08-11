package com.dmi.util.lang;

public class Enums {
    @SuppressWarnings("unchecked")
    public static Object unsafeValueOf(Class<? extends Enum> v, String text) {
        return java.lang.Enum.valueOf(v, text);
    }
}
