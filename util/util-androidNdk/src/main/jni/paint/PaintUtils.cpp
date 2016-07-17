#include "PaintUtils.h"

#include <cmath>
#include "../util/JniUtils.h"

using namespace std;
using namespace dmi;
using namespace paintUtils;

namespace {
    inline uint32_t argb2abgr(uint32_t argb) {
        uint32_t axgx = argb & 0xFF00FF00;
        uint32_t xbxx = (argb & 0x000000FF) << 16;
        uint32_t xxxr = (argb & 0x00FF0000) >> 16;
        return axgx | xbxx | xxxr;
    }

    inline uint32_t abgrBlendAlpha(uint32_t src, uint32_t dst, uint8_t srcA) {
        if (srcA == 255) {
            return src;
        } else if (srcA == 0) {
            return dst;
        } else {
            uint32_t srcB = (src >> 16) & 0xFF;
            uint32_t srcG = (src >> 8) & 0xFF;
            uint32_t srcR = src & 0xFF;

            uint32_t dstA = dst >> 24;
            uint32_t dstB = (dst >> 16) & 0xFF;
            uint32_t dstG = (dst >> 8) & 0xFF;
            uint32_t dstR = dst & 0xFF;

            uint32_t q = dstA * (255 - srcA) / 255;
            uint32_t outA = srcA + q;

            if (outA > 0) {
                uint32_t outB = (srcB * srcA + dstB * q) / outA;
                uint32_t outG = (srcG * srcA + dstG * q) / outA;
                uint32_t outR = (srcR * srcA + dstR * q) / outA;
                return outA << 24 | outB << 16 | outG << 8 | outR;
            } else {
                return 0;
            }
        }
    }
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
            *dr = abgrBlendAlpha(abgrColor, *dr, *sr);
            dr++;
            sr++;
        }
        d += dst.stride;
        s += srcStride;
    }
}