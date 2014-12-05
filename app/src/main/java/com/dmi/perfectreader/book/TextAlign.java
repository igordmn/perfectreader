package com.dmi.perfectreader.book;

public enum TextAlign {
    LEFT, CENTER, RIGHT, JUSTIFY;

    String cssValue() {
        return name().toLowerCase();
    }
}
