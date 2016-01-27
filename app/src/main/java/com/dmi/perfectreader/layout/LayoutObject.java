package com.dmi.perfectreader.layout;

import com.dmi.perfectreader.layout.config.LayoutArea;
import com.dmi.perfectreader.layout.config.LayoutConfig;
import com.dmi.perfectreader.render.RenderObject;

public abstract class LayoutObject {
    public abstract RenderObject layout(LayoutConfig config, LayoutArea layoutArea);
}
