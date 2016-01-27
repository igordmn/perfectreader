package com.dmi.perfectreader.render;

import android.graphics.Canvas;

import static java.util.Collections.emptyList;

public class RenderText extends RenderObject {
    private final String text;
    private final float baseline;
    private final int color;

    public RenderText(float width, float height, String text, float baseline, int color) {
        super(width, height, emptyList());
        this.text = text;
        this.baseline = baseline;
        this.color = color;
    }

    public String text() {
        return text;
    }

    @Override
    public boolean canPartiallyPainted() {
        return false;
    }

    @Override
    public void paintItself(RenderConfig config, Canvas canvas) {
        textPaint.setColor(color);
        canvas.drawText(text, 0, text.length(), 0F, baseline, textPaint);
    }
}
