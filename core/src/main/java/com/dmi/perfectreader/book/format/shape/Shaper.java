package com.dmi.perfectreader.book.format.shape;

import com.dmi.perfectreader.book.font.FontFace;

public interface Shaper {
    Shape shape(FontFace fontFace, char[] chars, int offset, int length);
}
