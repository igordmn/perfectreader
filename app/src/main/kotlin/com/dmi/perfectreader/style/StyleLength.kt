package com.dmi.perfectreader.style

class StyleLength(val value: Float, val type: Type) {
    // См. http://www.w3schools.com/cssref/css_units.asp
    enum class Type {
        // Абсолютные
        CM,
        MM, IN, PX, PT, PC,

        // Относительные
        EM,
        EX, CH, REM, VW, VH, VMIN, VMAX,

        // Зависят от контекста
        PERCENT
    }
}
