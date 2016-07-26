#pragma once

#include "stdint.h"
#include "FontFaceID.h"

namespace dmi {
    struct FontConfig {
        const FontFaceID *faceID;
        const float sizeX;
        const float sizeY;

        const bool hinting;
        const bool forceAutoHinting;
        const bool lightHinting;

        const float scaleX;
        const float scaleY;
        const float skewX;
        const float skewY;

        const float emboldenStrengthX;
        const float emboldenStrengthY;

        const bool strokeInside;
        const bool strokeOutside;
        const FT_Stroker_LineCap strokeLineCap;
        const FT_Stroker_LineJoin strokeLineJoin;
        const float strokeMiterLimit;
        const float strokeRadius;

        const bool antialias;
        const float blurRadius;
        const float gamma;
        const uint32_t color;

        FontConfig(
                const FontFaceID *faceID,
                float sizeX,
                float sizeY,

                bool hinting,
                bool forceAutoHinting,
                bool lightHinting,

                float scaleX,
                float scaleY,
                float skewX,
                float skewY,

                float emboldenStrengthX,
                float emboldenStrengthY,

                bool strokeInside,
                bool strokeOutside,
                FT_Stroker_LineCap strokeLineCap,
                FT_Stroker_LineJoin strokeLineJoin,
                float strokeMiterLimit,
                float strokeRadius,

                bool antialias,
                float blurRadius,
                float gamma,
                uint32_t color
        ) : faceID(faceID),
            sizeX(sizeX),
            sizeY(sizeY),

            hinting(hinting),
            forceAutoHinting(forceAutoHinting),
            lightHinting(lightHinting),

            scaleX(scaleX),
            scaleY(scaleY),
            skewX(skewX),
            skewY(skewY),

            emboldenStrengthX(emboldenStrengthX),
            emboldenStrengthY(emboldenStrengthY),

            strokeInside(strokeInside),
            strokeOutside(strokeOutside),
            strokeLineCap(strokeLineCap),
            strokeLineJoin(strokeLineJoin),
            strokeMiterLimit(strokeMiterLimit),
            strokeRadius(strokeRadius),

            antialias(antialias),
            blurRadius(blurRadius),
            gamma(gamma),
            color(color) {
        }
    };
}