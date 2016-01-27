package com.dmi.perfectreader.render;

import android.graphics.Canvas;
import android.text.TextPaint;

import static java.util.Collections.emptyList;

public class RenderText extends RenderObject {
    private final String text;
    private final float baseline;
    private final int color;
    private float size;

    public RenderText(float width, float height, String text, float baseline, int color, float size) {
        super(width, height, emptyList());
        this.text = text;
        this.baseline = baseline;
        this.color = color;
        this.size = size;
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
        super.paintItself(config, canvas);
        TextPaint paint = getTextPaint(config);
        paint.setColor(color);
        paint.setTextSize(size);
        canvas.drawText(text, 0, text.length(), 0F, baseline, paint);
    }
}
