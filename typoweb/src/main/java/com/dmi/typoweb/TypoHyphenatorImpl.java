package com.dmi.typoweb;

import android.annotation.SuppressLint;
import android.util.Log;

import com.dmi.util.natv.UsedByNative;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import timber.log.Timber;

@UsedByNative
class TypoHyphenatorImpl {
    private HyphenationPatternsLoader patternsLoader;

    @UsedByNative
    public TypoHyphenatorImpl() {
    }

    @UsedByNative
    public void setPatternsLoader(HyphenationPatternsLoader patternsLoader) {
        this.patternsLoader = patternsLoader;
    }

    @UsedByNative
    public boolean loadPatterns(long nativeHyphenatorBuilder, String locale) {
        try {
            InputStream inputStream = openStream(locale);
            if (inputStream != null) {
                loadPatterns(nativeHyphenatorBuilder, inputStream);
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            Timber.w(e, "Cannot load hyphenation patterns for lang: %s", locale);
            return false;
        }
    }

    private InputStream openStream(String locale) throws IOException {
        int dashIndex = locale.indexOf("-");
        String language = dashIndex >= 0 ? locale.substring(0, dashIndex) : locale;
        return patternsLoader != null ? patternsLoader.loadPatterns(language) : null;
    }

    @SuppressLint("NewApi")
    private void loadPatterns(long nativeHyphenatorBuilder, InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String pattern;
            while ((pattern = reader.readLine()) != null) {
                if (pattern.length() > 0) {
                    nativeAddPattern(nativeHyphenatorBuilder, pattern);
                }
            }
        }
    }

    private static native void nativeAddPattern(long nativeHyphenatorBuilder, String pattern);
}
