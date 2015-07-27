package com.dmi.perfectreader.book;

import android.content.Context;

import com.dmi.typoweb.HyphenationPatternsLoader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

public class TexHyphenationPatternsLoader implements HyphenationPatternsLoader {
    private static final String fileFormat = "hyphenation/hyph-%s.pat.txt";

    private static final Map<String, String> languageAliases = new HashMap<String, String>() {{
        put("de", "de-1996");
        put("el", "el-monoton");
        put("en", "en-us");
        put("la", "la-x-classic");
        put("mn", "mn-cyrl");
        put("sr", "sh-latn");
    }};

    private final Context context;

    public TexHyphenationPatternsLoader(Context context) {
        this.context = context;
    }

    @Override
    public InputStream loadPatterns(String language) throws IOException {
        for (String languageVariant : languageVariants(language)) {
            try {
                return context.getAssets().open(format(fileFormat, languageVariant));
            } catch (FileNotFoundException e) {
                // ignoring
            }
        }
        return null;
    }

    private List<String> languageVariants(String language) {
        List<String> variants = new ArrayList<>(3);
        variants.add(aliasOrLanguage(language));

        int indexOfDash = language.indexOf('-');
        if (indexOfDash >= 0) {
            String shortLanguage = language.substring(0, indexOfDash);
            variants.add(aliasOrLanguage(shortLanguage));
        }

        return variants;
    }

    private String aliasOrLanguage(String language) {
        String alias = languageAliases.get(language);
        return alias != null ? alias : language;
    }
}
