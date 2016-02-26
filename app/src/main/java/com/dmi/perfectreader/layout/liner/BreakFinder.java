package com.dmi.perfectreader.layout.liner;

import com.dmi.util.annotation.Reusable;

import java.util.Locale;

import java8.util.function.Consumer;

public interface BreakFinder {
    void findBreaks(CharSequence text, Locale locale, Consumer<Break> consumer);

    @Reusable
    interface Break {
        int index();
        boolean hasHyphen();
        boolean isForce();
    }
}
