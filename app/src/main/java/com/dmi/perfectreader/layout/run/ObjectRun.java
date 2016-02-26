package com.dmi.perfectreader.layout.run;

import com.dmi.perfectreader.layout.LayoutObject;

public class ObjectRun extends Run {
    private final LayoutObject object;

    public ObjectRun(LayoutObject object) {
        this.object = object;
    }

    public LayoutObject object() {
        return object;
    }
}
