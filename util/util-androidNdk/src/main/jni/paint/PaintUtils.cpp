#include "PaintUtils.h"

#include <algorithm>
#include <cmath>

using namespace std;
using namespace dmi;
using namespace paintUtils;

namespace {
    /**
     * Умножение A B G R компонентов цвета на alpha
     * если умножаем на 255, значит компоненты не меняются.
     * если умножаем на 0, то все компоненты превращаются в 0.
     * если умножаем на 127, то все делится пополам
     */
    inline uint32_t multiplyAlpha(uint32_t color, uint32_t alpha) {
        /*
         * диапазон 0..255 преобразовывается в 1..256.
         * в результате можно поделить на 256 быстрым методом (>> 8)
         * точность меньше, но зато быстро.
         */
        alpha += 1;

        uint32_t xbxr = ((color & 0x00FF00FF) * alpha) >> 8;
        uint32_t axgx = ((color >> 8) & 0x00FF00FF) * alpha;
        return xbxr & 0x00FF00FF | axgx & 0xFF00FF00;
    }

    /**
     * Умножение B G R компонентов цвета на компонент A
     */
    inline uint32_t premultiplyAlpha(uint32_t color) {
        uint32_t alpha = color >> 24;
        return alpha << 24 | 0x00FFFFFF & multiplyAlpha(color, alpha);
    }

    /**
     * Альфа смешивание для цветов с превычесленной альфой
     * pre-multipled alpha - это когда цвет (0.5*255 0.75*255 0 0) передается в виде (0.5*255 0.375*255 0 0)
     * См https://en.wikipedia.org/wiki/Alpha_compositing по тексту "pre-multiplied alpha"
     */
    inline uint32_t alphaBlendPremultiplied(uint32_t src, uint32_t dst) {
        uint32_t srcA = src >> 24;
        return src + multiplyAlpha(dst, (uint32_t) (255 - srcA));
    }
}

uint32_t paintUtils::argb2abgr(uint32_t argb) {
    uint32_t axgx = argb & 0xFF00FF00;
    uint32_t xbxx = (argb & 0x000000FF) << 16;
    uint32_t xxxr = (argb & 0x00FF0000) >> 16;
    return axgx | xbxx | xxxr;
}

void paintUtils::copyPixelsAlphaBlend(PixelBuffer &dst, const AlphaBuffer &src, int16_t x, int16_t y, uint32_t color) {
    uint32_t premultiplyColor = premultiplyAlpha(color);

    uint16_t factDstX = min(dst.width, (uint16_t) max((int16_t) 0, x));
    uint16_t factDstY = min(dst.height, (uint16_t) max((int16_t) 0, y));
    uint16_t factSrcX = min(src.width, (uint16_t) max((int16_t) 0, (int16_t) -x));
    uint16_t factSrcY = min(src.height, (uint16_t) max((int16_t) 0, (int16_t) -y));
    uint16_t factSrcWidth = min((uint16_t) (dst.width - factDstX), (uint16_t) (src.width - factSrcX));
    uint16_t factSrcHeight = min((uint16_t) (dst.height - factDstY), (uint16_t) (src.height - factSrcY));

    uint32_t *d = dst.data;
    uint8_t *s = src.data;

    d += dst.stride * factDstY + factDstX;
    s += src.stride * factSrcY + factSrcX;

    for (uint16_t yi = 0; yi < factSrcHeight; ++yi) {
        uint32_t *dr = d;
        uint8_t *sr = s;
        for (uint16_t xi = 0; xi < factSrcWidth; ++xi) {
            uint8_t textAlpha = *sr;
            uint32_t pixelColor = multiplyAlpha(premultiplyColor, textAlpha);
            *dr = alphaBlendPremultiplied(pixelColor, *dr);
            dr++;
            sr++;
        }
        d += dst.stride;
        s += src.stride;
    }
}

void paintUtils::copyPixels(AlphaBuffer &dst, const AlphaBuffer &src, int16_t x, int16_t y) {
    uint16_t factDstX = min(dst.width, (uint16_t) max((int16_t) 0, x));
    uint16_t factDstY = min(dst.height, (uint16_t) max((int16_t) 0, y));
    uint16_t factSrcX = min(src.width, (uint16_t) max((int16_t) 0, (int16_t) -x));
    uint16_t factSrcY = min(src.height, (uint16_t) max((int16_t) 0, (int16_t) -y));
    uint16_t factSrcWidth = min((uint16_t) (dst.width - factDstX), (uint16_t) (src.width - factSrcX));
    uint16_t factSrcHeight = min((uint16_t) (dst.height - factDstY), (uint16_t) (src.height - factSrcY));

    uint8_t *d = dst.data;
    uint8_t *s = src.data;

    d += dst.stride * factDstY + factDstX;
    s += src.stride * factSrcY + factSrcX;

    for (int yi = 0; yi < factSrcHeight; yi++) {
        memcpy(d, s, factSrcWidth);
        d += dst.stride;
        s += src.stride;
    }
}

void paintUtils::clear(AlphaBuffer &src, uint8_t alpha) {
    memset(src.data, alpha, src.stride * src.height);
}