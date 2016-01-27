package com.dmi.perfectreader.layout.config;

import android.graphics.Paint;
import android.text.TextPaint;

public class PaintTextMetrics implements TextMetrics {
    private static final TextPaint textPaint = new TextPaint();
    private static final Paint.FontMetrics fontMetrics = new Paint.FontMetrics();

    @Override
    public void getTextWidths(String text, float size, float[] widths) {
        textPaint.setTextSize(size);
        textPaint.getTextWidths(text, widths);
    }

    @Override
    public void getVerticalMetrics(float size, VerticalMetrics verticalMetrics) {
        textPaint.setTextSize(size);
        textPaint.getFontMetrics(fontMetrics);
        verticalMetrics.ascent = fontMetrics.ascent;
        verticalMetrics.descent = fontMetrics.descent;
        verticalMetrics.leading = fontMetrics.leading;
    }
}
