package com.dmi.perfectreader.layout;

import com.dmi.perfectreader.layout.config.LayoutArea;
import com.dmi.perfectreader.layout.config.TextMetrics;
import com.dmi.perfectreader.layout.layouter.Layouter;
import com.dmi.perfectreader.layout.layouter.ParagraphLayouter;
import com.dmi.perfectreader.layout.liner.Liner;
import com.dmi.perfectreader.render.RenderObject;

public class ObjectLayouter implements Layouter<LayoutObject, RenderObject> {
    private final ParagraphLayouter paragraphLayouter;

    public ObjectLayouter(TextMetrics textMetrics, Liner liner) {
        this.paragraphLayouter = new ParagraphLayouter(this, textMetrics, liner);
    }
    
    @Override
    public RenderObject layout(LayoutObject object, LayoutArea area) {
        if (object instanceof LayoutParagraph) {
            return paragraphLayouter.layout((LayoutParagraph) object, area);
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
