#include "PaintUtils.h"

#include <cmath>
#include "../util/JniUtils.h"

using namespace std;
using namespace dmi;
using namespace paintUtils;

uint32_t paintUtils::argb2abgr(uint32_t argb) {
    uint32_t axgx = argb & 0xFF00FF00;
    uint32_t xbxx = (argb & 0x000000FF) << 16;
    uint32_t xxxr = (argb & 0x00FF0000) >> 16;
    return axgx | xbxx | xxxr;
}

uint32_t paintUtils::abgrBlendAlpha(uint32_t dst, uint32_t src) {
    uint32_t sA = (uint32_t) (src >> 24);
    // todo alpha blending неправильный. см. https://en.wikipedia.org/wiki/Alpha_compositing, раздел Alpha blending
    return src;//(uint32_t) (dst * (sA / 255) + src * (1 - sA / 255));
}

void paintUtils::copyPixels(
        PaintBuffer &dst, uint8_t *src, uint16_t srcWidth, uint16_t srcHeight, uint16_t srcStride,
        int16_t x, int16_t y, uint32_t color
) {
    uint32_t abgrColor = argb2abgr(color);

    uint16_t factX = min(dst.width, (uint16_t) max((int16_t) 0, x));
    uint16_t factY = min(dst.height, (uint16_t) max((int16_t) 0, y));
    uint16_t factWidth = min((uint16_t) (dst.width - factX), srcWidth);
    uint16_t factHeight = min((uint16_t) (dst.height - factY), srcHeight);

    uint32_t *d = dst.data;
    uint8_t *s = src;

    d += dst.stride * factY + factX;

    for (uint16_t yi = 0; yi < factHeight; ++yi) {
        uint32_t *dr = d;
        uint8_t *sr = s;
        for (uint16_t xi = 0; xi < factWidth; ++xi) {
            uint32_t dPixel = *dr;
            uint8_t sPixelAlpha = *sr;

            uint32_t sPixelRed = (uint32_t) (sPixelAlpha << 24 | (0 << 16) | (0 << 8) | 255);  // todo применить abgrColor
            *dr = abgrBlendAlpha(dPixel, sPixelRed);
            dr++;
            sr++;
        }
        d += dst.stride;
        s += srcStride;
    }
}