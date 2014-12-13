package com.dmi.perfectreader.book.config;

public enum TextAlign {
    LEFT, CENTER, RIGHT, JUSTIFY;

    public String cssValue() {
        return name().toLowerCase();
    }
}
