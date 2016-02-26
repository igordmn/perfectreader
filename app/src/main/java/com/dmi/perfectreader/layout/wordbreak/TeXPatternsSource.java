package com.dmi.perfectreader.layout.wordbreak;

import android.content.Context;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static java.lang.String.format;

public class TeXPatternsSource {
    private static final String patternFormat = "hyphenation/hyph-%s.pat.txt";
    private static final String exceptionFormat = "hyphenation/hyph-%s.hyp.txt";

    private static final Map<String, String> languageAliases = new HashMap<String, String>() {{
        put("de", "de-1996");
        put("el", "el-monoton");
        put("en", "en-us");
        put("la", "la-x-classic");
        put("mn", "mn-cyrl");
        put("sr", "sh-latn");
    }};

    private final Context context;

    public TeXPatternsSource(Context context) {
        this.context = context;
    }

    public InputStream readPatternsFor(Locale locale) throws IOException {
        return readTeXFile(locale, patternFormat);
    }

    public InputStream readExceptionsFor(Locale locale) throws IOException {
        return readTeXFile(locale, exceptionFormat);
    }

    private InputStream readTeXFile(Locale locale, String format) throws IOException {
        try {
            String language = aliasOrLanguage(locale.getLanguage());
            return context.getAssets().open(format(format, language));
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    private String aliasOrLanguage(String language) {
        String alias = languageAliases.get(language);
        return alias != null ? alias : language;
    }
}
