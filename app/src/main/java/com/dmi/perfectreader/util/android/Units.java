package com.dmi.perfectreader.util.android;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class Units {
    private final DisplayMetrics displayMetrics;

    public Units(Context context) {
        displayMetrics = context.getResources().getDisplayMetrics();
    }

    public float dipToPx(float dip) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, displayMetrics);
    }

    public int displayDpi() {
        return displayMetrics.densityDpi;
    }
}