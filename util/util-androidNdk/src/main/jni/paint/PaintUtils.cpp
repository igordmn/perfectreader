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

void paintUtils::copyPixels(
        PixelBuffer &dst, uint8_t *src, uint16_t srcWidth, uint16_t srcHeight, uint16_t srcStride,
        int16_t x, int16_t y, uint32_t argbColor
) {
    uint32_t color = premultiplyAlpha(argb2abgr(argbColor));

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
            uint8_t textAlpha = *sr;
            uint32_t pixelColor = multiplyAlpha(color, textAlpha);
            *dr = alphaBlendPremultiplied(pixelColor, *dr);
            dr++;
            sr++;
        }
        d += dst.stride;
        s += srcStride;
    }
}