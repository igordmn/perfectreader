package com.dmi.perfectreader.layout.layouter;

import com.dmi.perfectreader.layout.config.LayoutArea;

public interface Layouter<L, R> {
    R layout(L object, LayoutArea area);
}
