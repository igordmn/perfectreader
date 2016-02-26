package com.dmi.perfectreader.layout.run;

import com.dmi.perfectreader.style.FontStyle;

public class TextRun extends Run {
    private final CharSequence text;
    private final FontStyle style;

    public TextRun(String text, FontStyle style) {
        this.text = text;
        this.style = style;
    }

    public CharSequence text() {
        return text;
    }

    public FontStyle style() {
        return style;
    }
}
