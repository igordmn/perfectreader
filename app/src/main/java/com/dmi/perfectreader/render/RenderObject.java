package com.dmi.perfectreader.render;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;

import java.util.List;

public abstract class RenderObject {
    private static final Paint paint = new Paint();
    private static final TextPaint textPaint = new TextPaint();

    private final float width;
    private final float height;
    private final List<RenderChild> children;

    public RenderObject(float width, float height, List<RenderChild> children) {
        this.width = width;
        this.height = height;
        this.children = children;
    }

    public float width() {
        return width;
    }

    public float height() {
        return height;
    }

    public List<RenderChild> children() {
        return children;
    }

    public RenderChild child(int index) {
        return children.get(index);
    }

    public RenderObject childObject(int index) {
        return children.get(index).object();
    }

    /**
     * Необходимо для механизма разделения на страницы.
     *   true - для текстовых строк, изображений, и всех тех объектов, которые необходимо рисовать на экране целиком
     *   false - для контейнеров, таблиц, и всех тех объектов, которые можно рисовать частично (обрезав, например верхнюю границу)
     */
    public abstract boolean canPartiallyPainted();

    /**
     * Нарисовать только содержимое данного объекта, без рисования дочерних объектов
     */
    public void paintItself(RenderConfig config, Canvas canvas) {
    }

    protected static Paint getPaint(RenderConfig config) {
        configurePaint(textPaint, config);
        return paint;
    }

    protected static TextPaint getTextPaint(RenderConfig config) {
        configurePaint(textPaint, config);
        return textPaint;
    }

    protected static void configurePaint(Paint paint, RenderConfig config) {
        paint.setAntiAlias(config.textAntialias());
        paint.setSubpixelText(config.textSubpixel());
        paint.setHinting(config.textHinting() ? Paint.HINTING_ON : Paint.HINTING_OFF);
        paint.setLinearText(config.textLinearScaling());
        paint.setFilterBitmap(config.bitmapBilinearSampling());
        paint.setDither(config.dither());
    }

    public void paintRecursive(RenderConfig config, Canvas canvas) {
        paintItself(config, canvas);
        for (RenderChild child : children) {
            canvas.translate(child.x(), child.y());
            child.object().paintRecursive(config, canvas);
            canvas.translate(-child.x(), -child.y());
        }
    }

    public boolean click(float x, float y) {
        return false;
    }
}
