package com.dmi.perfectreader.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;

import com.gc.materialdesign.views.ButtonFlat;

public class ButtonFlatExt extends ButtonFlat {
    public ButtonFlatExt(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void setAttributes(AttributeSet attrs) {
        super.setAttributes(attrs);
        getTextView().setGravity(Gravity.CENTER);
    }
}
