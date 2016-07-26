package com.dmi.util.android.graphics;

import com.dmi.util.graphic.Color;

public class FontConfig {
    final long nativePtr;

    /**
     * Не удалять из этого класса, т.к. FontConfig должен содержать ссылку на FontFaceID в Java, иначе FontFaceID может удалиться до того, как удалиться FontConfig
     */
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final FontFaceID faceID;

    public FontConfig(
            FontFaceID faceID,
            float sizeX,  // Ширина шрифта в пикселях
            float sizeY,  // Высота шрифта в пикселях

            boolean hinting,
            boolean forceAutoHinting,
            boolean lightHinting,

            float scaleX,
            float scaleY,
            float skewX,
            float skewY,

            boolean embolden,
            float emboldenStrengthX,
            float emboldenStrengthY,

            boolean strokeInside,
            boolean strokeOutside,
            StrokeLineCap strokeLineCap,
            StrokeLineJoin strokeLineJoin,
            float strokeMiterLimit,
            float strokeRadius,

            boolean antialias,
            float gamma,
            float blurRadius,
            Color color
    ) {
        this.faceID = faceID;

        this.nativePtr = nativeNewFontConfig(
                faceID.nativePtr,
                sizeX,
                sizeY,

                hinting,
                forceAutoHinting,
                lightHinting,

                scaleX,
                scaleY,
                skewX,
                skewY,

                embolden,
                emboldenStrengthX,
                emboldenStrengthY,

                strokeInside,
                strokeOutside,
                strokeLineCap.ordinal(),
                strokeLineJoin.ordinal(),
                strokeMiterLimit,
                strokeRadius,

                antialias,
                gamma,
                blurRadius,
                color.getValue()
        );
    }

    @SuppressWarnings("ThrowFromFinallyBlock")
    @Override
    protected void finalize() throws Throwable {
        try {
            nativeDestroyFontConfig(nativePtr);
        } finally {
            super.finalize();
        }
    }

    private native long nativeNewFontConfig(
            long faceIDPtr,
            float sizeX,
            float sizeY,

            boolean hinting,
            boolean forceAutoHinting,
            boolean lightHinting,

            float scaleX,
            float scaleY,
            float skewX,
            float skewY,

            boolean embolden,
            float emboldenStrengthX,
            float emboldenStrengthY,

            boolean strokeInside,
            boolean strokeOutside,
            int strokeLineCapOrdinal,
            int strokeLineJoinOrdinal,
            float strokeMiterLimit,
            float strokeRadius,

            boolean antialias,
            float gamma,
            float blurRadius,
            int colorARGB
    );

    private native void nativeDestroyFontConfig(long ptr);

    public enum StrokeLineCap {BUTT, ROUND, SQUARE}

    public enum StrokeLineJoin {ROUND, BEVEL, MITER_VARIABLE, MITER_FIXED}
}
