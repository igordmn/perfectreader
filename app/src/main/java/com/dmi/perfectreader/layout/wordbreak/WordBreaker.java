package com.dmi.perfectreader.layout.wordbreak;

import com.dmi.util.annotation.Reusable;

import java.util.Locale;

public interface WordBreaker {
    WordBreaks breakWord(CharSequence text, Locale locale, int beginIndex, int endIndex);

    @Reusable
    interface WordBreaks {
        boolean canBreakBefore(int index);
    }
}
