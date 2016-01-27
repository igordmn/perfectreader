package com.dmi.perfectreader.layout.config;

public class LayoutConfig {
    private final TextMetrics textMetrics;

    public LayoutConfig(TextMetrics textMetrics) {
        this.textMetrics = textMetrics;
    }

    public TextMetrics textMetrics() {
        return textMetrics;
    }
}
