package com.dmi.perfectreader.layout.liner.breaker

class BreakUtils {
    public static def breakIndicesOf(Breaks breaks, String text) {
        def indices = []
        for (int i = 0; i < text.length(); i++) {
            if (breaks.hasBreakBefore(i))
                indices.add(i)
        }
        return indices
    }

    public static def hyphenIndicesOf(Breaks breaks, String text) {
        def indices = []
        for (int i = 0; i < text.length(); i++) {
            if (breaks.hasHyphenBefore(i))
                indices.add(i)
        }
        return indices
    }
}
