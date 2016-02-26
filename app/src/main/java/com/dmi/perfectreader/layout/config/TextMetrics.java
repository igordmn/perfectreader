package com.dmi.perfectreader.layout.config;

import com.dmi.perfectreader.style.FontStyle;
import com.dmi.util.annotation.Reusable;

public interface TextMetrics {
    @Reusable
    float[] charWidths(CharSequence text, FontStyle style);

    VerticalMetrics verticalMetrics(FontStyle style);

    @Reusable
    class VerticalMetrics {
        protected float ascent;
        protected float descent;
        protected float leading;

        public float ascent() {
            return ascent;
        }

        public float descent() {
            return descent;
        }

        public float leading() {
            return leading;
        }
    }
}
