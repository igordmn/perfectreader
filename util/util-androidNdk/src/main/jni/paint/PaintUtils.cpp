#include "PaintUtils.h"

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

    inline uint32_t alphaMultiply(uint32_t color, uint32_t alpha) {
        /*
         * диапазон 0..255 преобразовывается в 1..256.
         * в результате можно поделить на 256 быстрым методом (>> 8)
         * точность меньше, но зато быстро.
         */
        alpha += 1;

        uint32_t xrxb = ((color & 0x00FF00FF) * alpha) >> 8;
        uint32_t axgx = ((color >> 8) & 0x00FF00FF) * alpha;
        return xrxb & 0x00FF00FF | axgx & 0xFF00FF00;
    }

    inline uint32_t abgrBlendAlpha(uint32_t src, uint32_t preMultipliedDst, uint32_t srcA) {
        return alphaMultiply(src, srcA) + alphaMultiply(preMultipliedDst, (uint32_t) (255 - srcA));
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