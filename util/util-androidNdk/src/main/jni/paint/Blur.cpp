#include "Blur.h"

#include "../util/JniUtils.h"
#include <cmath>

using namespace std;
using namespace dmi;

namespace {
    inline void copyRow(AlphaBuffer &from, uint8_t *to, int rowIndex) {
        memcpy(to, from.data + rowIndex * from.stride, from.width);
    }

    inline void copyRow(uint8_t *from, AlphaBuffer &to, int rowIndex) {
        memcpy(to.data + rowIndex * to.stride, from, to.width);
    }

    inline void copyColumn(AlphaBuffer &from, uint8_t *to, int columnIndex) {
        uint8_t *f = from.data + columnIndex;
        uint8_t *t = to;
        for (uint16_t i = 0; i < from.height; i++) {
            *t = *f;
            f += from.stride;
            t++;
        }
    }

    inline void copyColumn(uint8_t *from, AlphaBuffer &to, int columnIndex) {
        uint8_t *f = from;
        uint8_t *t = to.data + columnIndex;
        for (uint16_t i = 0; i < to.height; i++) {
            *t = *f;
            f++;
            t += to.stride;
        }
    }

    /**
     * Пиксели за границами, считаем нулевыми, т.к. обычно для глифов так и есть (мы используем blur только для глифов).
     */
    inline uint8_t pixelValue(uint8_t *src, uint32_t size, int32_t index) {
        return index >= 0 && index < size ? src[index] : (uint8_t) 0;
    }

    inline void boxBlur1D(uint8_t *src, uint8_t *dst, uint16_t size, float radius) {
        /**
         * Формула horizontal box blur для нецелого радиуса:
         *   r = m + a. m - целая часть, a - дробная
         *   pixel(x) = (a * пиксель_minus_m_minus_1 + сумма_всех_пикселей_в_радиусе_m + a * пиксель_m_plus_1) / (2 * r + 1)
         * для оптимизации, можно вычислять вначале первый пискель, а потом все последующие по предыдущим
         */

        if (size > 0) {
            uint32_t m = (uint32_t) radius;   // целая часть
            float a = radius - m;             // дробная часть

            float msum = 0;
            float multiplier = 1 / (2 * radius + 1);

            for (int i = 0; i <= m; i++) {
                msum += pixelValue(src, size, i);
            }
            float sum = msum + a * (pixelValue(src, size, 0 - m - 1) + pixelValue(src, size, 0 + m + 1));
            dst[0] = (uint8_t) (sum * multiplier);

            for (int i = 1; i < size; i++) {
                msum = msum - pixelValue(src, size, i - m - 1) + pixelValue(src, size, i + m);
                sum = msum + a * (pixelValue(src, size, i - m - 1) + pixelValue(src, size, i + m + 1));
                dst[i] = (uint8_t) (sum * multiplier);
            }
        }
    }
}

uint16_t blur::gaussianBlurAdditionalPixels(float radius) {
    return (uint16_t) (3 * ceil(radius));
}

void blur::boxBlurHorizontal(AlphaBuffer &dst, float radius) {
    uint8_t srcRow[dst.width];
    uint8_t dstRow[dst.width];

    for (uint16_t i = 0; i < dst.height; i++) {
        copyRow(dst, srcRow, i);
        boxBlur1D(srcRow, dstRow, dst.width, radius);
        copyRow(dstRow, dst, i);
    }
}

void blur::boxBlurVertical(AlphaBuffer &dst, float radius) {
    uint8_t srcColumn[dst.height];
    uint8_t dstColumn[dst.height];

    for (uint16_t i = 0; i < dst.width; i++) {
        copyColumn(dst, srcColumn, i);
        boxBlur1D(srcColumn, dstColumn, dst.height, radius);
        copyColumn(dstColumn, dst, i);
    }
}

void blur::boxBlur(AlphaBuffer &dst, float radius) {
    boxBlurHorizontal(dst, radius);
    boxBlurVertical(dst, radius);
}

/**
 * Применить gaussian blur.
 * Изображение размоется на gaussianBlurAdditionalPixels(radius) пикселей.
 */
void blur::gaussianBlur(AlphaBuffer &dst, float radius) {
    /* Gaussian blur аппроксимируется тремя box blur с тем же радиусом */
    boxBlur(dst, radius);
    boxBlur(dst, radius);
    boxBlur(dst, radius);
}