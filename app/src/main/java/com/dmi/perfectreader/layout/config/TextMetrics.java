package com.dmi.perfectreader.layout.config;

public interface TextMetrics {
    void getTextWidths(String text, float size, float[] widths);

    void getVerticalMetrics(float size, VerticalMetrics verticalMetrics);

    class VerticalMetrics {
        public float ascent;
        public float descent;
        public float leading;
    }
}
