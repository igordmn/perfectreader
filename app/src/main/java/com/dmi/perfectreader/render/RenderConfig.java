package com.dmi.perfectreader.render;

public class RenderConfig {
    private final boolean textAntialias;
    private final boolean textSubpixel;
    private final boolean textHinting;
    private final boolean textLinearScaling;
    private final boolean bitmapBilinearSampling;
    private final boolean dither;

    public RenderConfig(boolean textAntialias,
                        boolean textSubpixel,
                        boolean textHinting,
                        boolean textLinearScaling,
                        boolean bitmapBilinearSampling,
                        boolean dither) {
        this.textAntialias = textAntialias;
        this.textSubpixel = textSubpixel;
        this.textHinting = textHinting;
        this.textLinearScaling = textLinearScaling;
        this.bitmapBilinearSampling = bitmapBilinearSampling;
        this.dither = dither;
    }

    public boolean textAntialias() {
        return textAntialias;
    }

    public boolean textSubpixel() {
        return textSubpixel;
    }

    public boolean textHinting() {
        return textHinting;
    }

    public boolean textLinearScaling() {
        return textLinearScaling;
    }

    public boolean bitmapBilinearSampling() {
        return bitmapBilinearSampling;
    }

    public boolean dither() {
        return dither;
    }
}
