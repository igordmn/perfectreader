package com.dmi.perfectreader.style;

public class StyleLength {
    private float value;
    private Type type;

    // См. http://www.w3schools.com/cssref/css_units.asp
    public enum Type {
        // Абсолютные
        CM, MM, IN, PX, PT, PC,

        // Относительные
        EM, EX, CH, REM, VW, VH, VMIN, VMAX,

        // Зависят от контекста
        PERCENT
    }
}
