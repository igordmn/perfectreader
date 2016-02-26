package com.dmi.perfectreader.render;

import android.graphics.Canvas;

import java.util.List;

public abstract class RenderObject {
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

    /**
     * Необходимо для механизма разделения на страницы.
     *   true - для текстовых строк, изображений, и всех тех объектов, которые необходимо рисовать на экране целиком
     *   false - для контейнеров, таблиц, и всех тех объектов, которые можно рисовать частично (обрезав, например верхнюю границу)
     */
    public abstract boolean canPartiallyPainted();

    /**
     * Нарисовать только содержимое данного объекта, без рисования дочерних объектов
     */
    public void paintItself(Canvas canvas) {
    }

    public void paintRecursive(Canvas canvas) {
        paintItself(canvas);
        // не использовать for-each! снижается производительность
        for (int i = 0; i < children.size(); i++) {
            RenderChild child = children.get(i);
            canvas.translate(child.x(), child.y());
            child.object().paintRecursive(canvas);
            canvas.translate(-child.x(), -child.y());
        }
    }

    public boolean click(float x, float y) {
        return false;
    }
}
