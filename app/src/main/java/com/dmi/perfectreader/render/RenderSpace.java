package com.dmi.perfectreader.render;

import android.graphics.Canvas;

import com.dmi.perfectreader.style.FontStyle;

import java.util.Locale;

public class RenderSpace extends RenderText {
    private float scaleX;

    public RenderSpace(float width, float height, CharSequence text, Locale locale, float baseline, float scaleX, FontStyle style) {
        super(width, height, text, locale, baseline, style);
        this.scaleX = scaleX;
    }

    public float scaleX() {
        return scaleX;
    }

    @Override
    public void paintItself(Canvas canvas) {
    }
}
