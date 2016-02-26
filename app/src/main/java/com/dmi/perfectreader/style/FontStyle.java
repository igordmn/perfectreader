package com.dmi.perfectreader.style;

public class FontStyle {
    private final float size;
    private final int color;
    private final RenderParams renderParams;

    public FontStyle(float size, int color, RenderParams renderParams) {
        this.size = size;
        this.color = color;
        this.renderParams = renderParams;
    }

    public float size() {
        return size;
    }

    public int color() {
        return color;
    }

    public RenderParams renderParams() {
        return renderParams;
    }

    public static class RenderParams {
        private final boolean antialias;
        private final boolean subpixel;
        private final boolean hinting;
        private final boolean linearScaling;

        public RenderParams(boolean antialias, boolean subpixel, boolean hinting, boolean linearScaling) {
            this.antialias = antialias;
            this.subpixel = subpixel;
            this.hinting = hinting;
            this.linearScaling = linearScaling;
        }

        public boolean textAntialias() {
            return antialias;
        }

        public boolean textSubpixel() {
            return subpixel;
        }

        public boolean textHinting() {
            return hinting;
        }

        public boolean textLinearScaling() {
            return linearScaling;
        }
    }
}
