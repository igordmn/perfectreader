package com.dmi.perfectreader.layout.config;

import android.graphics.Paint;
import android.text.TextPaint;

import com.dmi.perfectreader.style.FontStyle;
import com.dmi.util.annotation.Reusable;
import com.dmi.util.cache.ReuseCache.Reuser;

import java.util.concurrent.atomic.AtomicReference;

import static com.dmi.util.cache.ReuseCache.reuseFloatArray;
import static com.dmi.util.cache.ReuseCache.reuser;

public class PaintTextMetrics implements TextMetrics {
    @Reusable
    @Override
    public float[] charWidths(CharSequence text, FontStyle style) {
        TextPaint textPaint = Reusables.textPaint();
        float[] charWidths = Reusables.charWidths(text.length());

        textPaint.setAntiAlias(style.renderParams().textAntialias());
        textPaint.setSubpixelText(style.renderParams().textSubpixel());
        textPaint.setHinting(style.renderParams().textHinting() ? Paint.HINTING_ON : Paint.HINTING_OFF);
        textPaint.setLinearText(style.renderParams().textLinearScaling());
        textPaint.setTextSize(style.size());
        textPaint.getTextWidths(text, 0, text.length(), charWidths);

        return charWidths;
    }

    @Reusable
    @Override
    public VerticalMetrics verticalMetrics(FontStyle style) {
        TextPaint textPaint = Reusables.textPaint();
        VerticalMetrics verticalMetrics = Reusables.verticalMetrics();
        Paint.FontMetrics paintFontMetrics = Reusables.paintFontMetrics();

        textPaint.setTextSize(style.size());
        textPaint.getFontMetrics(paintFontMetrics);
        verticalMetrics.ascent = paintFontMetrics.ascent;
        verticalMetrics.descent = paintFontMetrics.descent;
        verticalMetrics.leading = paintFontMetrics.leading;

        return verticalMetrics;
    }

    private static class Reusables {
        private static final TextPaint textPaint = new TextPaint();
        private static final Paint.FontMetrics paintFontMetrics = new Paint.FontMetrics();
        private static final TextMetrics.VerticalMetrics verticalMetrics = new VerticalMetrics();
        private static final Reuser<AtomicReference<float[]>> charWidths = reuser(() -> new AtomicReference<>(new float[8000]));

        @Reusable
        public static TextPaint textPaint() {
            return textPaint;
        }

        @Reusable
        public static Paint.FontMetrics paintFontMetrics() {
            return paintFontMetrics;
        }

        @Reusable
        public static TextMetrics.VerticalMetrics verticalMetrics() {
            return verticalMetrics;
        }

        @Reusable
        public static float[] charWidths(int capacity) {
            return reuseFloatArray(charWidths, capacity);
        }
    }
}
