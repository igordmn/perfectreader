package com.dmi.perfectreader.layout;

import com.dmi.perfectreader.layout.config.LayoutArea;
import com.dmi.perfectreader.layout.config.LayoutConfig;
import com.dmi.perfectreader.layout.line.Inline;
import com.dmi.perfectreader.layout.line.LinesBuilder;
import com.dmi.perfectreader.render.RenderBox;
import com.dmi.perfectreader.render.RenderChild;
import com.dmi.perfectreader.render.RenderLine;
import com.dmi.perfectreader.render.RenderObject;

import java.util.ArrayList;
import java.util.List;

public class LayoutText extends LayoutObject {
    private final List<Inline> inlines;

    public LayoutText(List<Inline> inlines) {
        this.inlines = inlines;
    }

    public List<Inline> inlines() {
        return inlines;
    }

    @Override
    public RenderObject layout(LayoutConfig config, LayoutArea layoutArea) {
        LinesBuilder linesBuilder = new LinesBuilder(layoutArea.width());
        for (Inline inline : inlines) {
            inline.layout(config, linesBuilder);
        }
        List<RenderLine> lines = linesBuilder.build();

        ArrayList<RenderChild> children = new ArrayList<>();
        float width = 0;
        float height = 0;
        for (RenderLine line : lines) {
            children.add(new RenderChild(0, height, line));
            height += line.height();
            if (line.width() > width) {
                width = line.width();
            }
        }

        return new RenderBox(width, height, children);
    }
}
