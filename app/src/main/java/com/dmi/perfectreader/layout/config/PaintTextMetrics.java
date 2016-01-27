package com.dmi.perfectreader.layout.config;

import android.graphics.Paint;

public class PaintTextMetrics implements TextMetrics {
    private static final Paint paint = new Paint();
    private static final Paint.FontMetrics fontMetrics = new Paint.FontMetrics();

    @Override
    public void getTextWidths(String text, float[] widths) {
        paint.getTextWidths(text, widths);
    }

    @Override
    public void getVerticalMetrics(VerticalMetrics verticalMetrics) {
        paint.getFontMetrics(fontMetrics);
        verticalMetrics.ascent = fontMetrics.ascent;
        verticalMetrics.descent = fontMetrics.descent;
        verticalMetrics.leading = fontMetrics.leading;
    }
}
