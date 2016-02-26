package com.dmi.perfectreader.layout.liner;

import java.util.List;
import java.util.Locale;

public interface Liner {
    List<Line> makeLines(MeasuredText measuredText, Config config);

    interface Line {
        float left();
        float width();
        boolean hasHyphenAfter();
        boolean isLast();
        List<Token> tokens();
    }

    interface Token {
        boolean isSpace();
        int beginIndex();
        int endIndex();
    }

    interface MeasuredText {
        CharSequence plainText();
        Locale locale();
        float widthOf(int index);
        float widthOf(int beginIndex, int endIndex);
        float hyphenWidthAfter(int index);
    }

    interface Config {
        float firstLineIndent();
        float maxWidth();
        float leftHangFactor(char ch);
        float rightHangFactor(char ch);
    }
}
