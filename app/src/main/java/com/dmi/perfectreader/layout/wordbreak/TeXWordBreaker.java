package com.dmi.perfectreader.layout.wordbreak;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import timber.log.Timber;

public class TeXWordBreaker implements WordBreaker {
    private final TeXPatternsSource patternsSource;

    private final HyphenatorCache hyphenatorCache = new HyphenatorCache();

    public TeXWordBreaker(TeXPatternsSource patternsSource) {
        this.patternsSource = patternsSource;
    }

    @Override
    public WordBreaks breakWord(CharSequence text, Locale locale, int beginIndex, int endIndex) {
        return hyphenatorCache.getFor(locale).breakWord(text, beginIndex, endIndex);
    }

    private TeXHyphenator loadHyphenatorFor(Locale locale) {
        TeXHyphenator.Builder builder = new TeXHyphenator.Builder();
        try {
            {
                InputStream is = patternsSource.readPatternsFor(locale);
                if (is != null) {
                    builder.addPatternsFrom(is);
                }
            }
            {
                InputStream is = patternsSource.readExceptionsFor(locale);
                if (is != null) {
                    builder.addExceptionsFrom(is);
                }
            }
        } catch (IOException e) {
            Timber.w(e, "Cannot load hyphenation patterns for lang: %s", locale);
        }
        return builder.build();
    }

    private class HyphenatorCache {
        private TeXHyphenator currentHyphenator = null;
        private TeXHyphenator previewHyphenator = null;
        private Locale currentLocale = null;
        private Locale previewLocale = null;

        public TeXHyphenator getFor(Locale locale) {
            if (locale == previewLocale) {
                TeXHyphenator temp = previewHyphenator;
                previewHyphenator = currentHyphenator;
                currentHyphenator = temp;
                previewLocale = currentLocale;
                currentLocale = locale;
            } else if (locale != currentLocale) {
                previewHyphenator = currentHyphenator;
                currentHyphenator = loadHyphenatorFor(locale);
                previewLocale = currentLocale;
                currentLocale = locale;
            }

            return currentHyphenator;
        }
    }
}
