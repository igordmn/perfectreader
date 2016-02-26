package com.dmi.perfectreader.layout;

import com.dmi.perfectreader.layout.config.HangingConfig;
import com.dmi.perfectreader.layout.run.Run;
import com.dmi.perfectreader.style.TextAlign;

import java.util.List;
import java.util.Locale;

public class LayoutParagraph extends LayoutObject {
    private final boolean fillAreaWidth;
    private final Locale locale;
    private final List<Run> runs;
    private final float firstLineIndent;
    private final TextAlign textAlign;
    private final HangingConfig hangingConfig;

    public LayoutParagraph(
            boolean fillAreaWidth,
            Locale locale,
            List<Run> runs,
            float firstLineIndent,
            TextAlign textAlign,
            HangingConfig hangingConfig
    ) {
        this.fillAreaWidth = fillAreaWidth;
        this.locale = locale;
        this.runs = runs;
        this.firstLineIndent = firstLineIndent;
        this.textAlign = textAlign;
        this.hangingConfig = hangingConfig;
    }

    public boolean fillAreaWidth() {
        return fillAreaWidth;
    }

    public Locale locale() {
        return locale;
    }

    public List<Run> runs() {
        return runs;
    }

    public float firstLineIndent() {
        return firstLineIndent;
    }

    public TextAlign textAlign() {
        return textAlign;
    }

    public HangingConfig hangingConfig() {
        return hangingConfig;
    }
}
