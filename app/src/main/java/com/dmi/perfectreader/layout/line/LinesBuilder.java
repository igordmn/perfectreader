package com.dmi.perfectreader.layout.line;

import com.dmi.perfectreader.render.RenderChild;
import com.dmi.perfectreader.render.RenderLine;
import com.dmi.perfectreader.render.RenderObject;

import java.util.ArrayList;
import java.util.List;

import gnu.trove.list.array.TFloatArrayList;


public class LinesBuilder {
    private final float maxWidth;

    private List<RenderLine> lines = new ArrayList<>();
    private List<RenderObject> currentObjects = new ArrayList<>();
    private TFloatArrayList baselines = new TFloatArrayList();
    private float currentWidth;

    public LinesBuilder(float maxWidth) {
        this.maxWidth = maxWidth;
    }

    public void finishLine() {
        if (currentObjects.size() > 0) {
            float lineWidth = 0;
            float lineHeight = 0;
            float lineBaseline = 0;
            ArrayList<RenderChild> children = new ArrayList<>();

            for (int i = 0; i < currentObjects.size(); i++) {
                RenderObject object = currentObjects.get(i);
                float baseline = baselines.get(i);
                if (object.height() > lineHeight) {
                    lineHeight = object.height();
                }
                if (baseline > lineBaseline) {
                    lineBaseline = baseline;
                }
            }

            for (int i = 0; i < currentObjects.size(); i++) {
                RenderObject object = currentObjects.get(i);
                float baseline = baselines.get(i);
                float x = lineWidth;
                float y = lineBaseline - baseline;
                children.add(new RenderChild(x, y, object));
                lineWidth += object.width();
            }

            lines.add(new RenderLine(lineWidth, lineHeight, children));
        }
        currentObjects.clear();
        currentWidth = 0;
        baselines.clear();
    }

    public void appendObject(RenderObject object, float baseline) {
        currentObjects.add(object);
        currentWidth += object.width();
        baselines.add(baseline);
    }

    public  boolean isFirstLine() {
        return lines.size() == 0;
    }

    public boolean isLineEmpty() {
        return currentObjects.size() == 0;
    }

    public float availableWidth() {
        return maxWidth - currentWidth;
    }

    public List<RenderLine> build() {
        finishLine();
        return lines;
    }
}
