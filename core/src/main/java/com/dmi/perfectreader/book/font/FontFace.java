package com.dmi.perfectreader.book.font;

public class FontFace {
    public static final FontFace DEFAULT = new FontFace();

    public String name = "Arial";

    public float size = 6;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FontFace fontFace = (FontFace) o;

        return Float.compare(fontFace.size, size) == 0 && name.equals(fontFace.name);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (size != +0.0f ? Float.floatToIntBits(size) : 0);
        return result;
    }
}
