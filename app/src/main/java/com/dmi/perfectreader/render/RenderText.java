package com.dmi.perfectreader.render;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;

import com.dmi.perfectreader.style.FontStyle;

import java.util.Locale;

import static java.util.Collections.emptyList;

public class RenderText extends RenderObject {
    private final CharSequence text;
    private final Locale locale;
    private final float baseline;
    private final FontStyle style;

    public RenderText(float width, float height, CharSequence text, Locale locale, float baseline, FontStyle style) {
        super(width, height, emptyList());
        this.text = text;
        this.locale = locale;
        this.baseline = baseline;
        this.style = style;
    }

    public CharSequence text() {
        return text;
    }

    public Locale locale() {
        return locale;
    }

    public float baseline() {
        return baseline;
    }

    public FontStyle style() {
        return style;
    }

    @Override
    public boolean canPartiallyPainted() {
        return false;
    }

    @Override
    public void paintItself(Canvas canvas) {
        super.paintItself(canvas);
        Paint paint = PaintCache.forStyle(style);
        canvas.drawText(text, 0, text.length(), 0F, baseline, paint);
    }

    private static class PaintCache {
        private static TextPaint paint = new TextPaint();

        private static FontStyle lastStyle;

        public static TextPaint forStyle(FontStyle style) {
            if (lastStyle != style) {
                paint.setAntiAlias(style.renderParams().textAntialias());
                paint.setSubpixelText(style.renderParams().textSubpixel());
                paint.setHinting(style.renderParams().textHinting() ? Paint.HINTING_ON : Paint.HINTING_OFF);
                paint.setLinearText(style.renderParams().textLinearScaling());
                paint.setColor(style.color());
                paint.setTextSize(style.size());
                lastStyle = style;
            }
            return paint;
        }
    }
}
