#pragma once

#include <stdint.h>
#include "PaintBuffer.h"

namespace dmi {
    namespace paintUtils {
        uint32_t argb2abgr(uint32_t argb);
        uint32_t abgrBlendAlpha(uint32_t dst, uint32_t src);
        void copyPixels(PaintBuffer &dst, uint8_t *src, uint16_t srcWidth, uint16_t srcHeight, uint16_t srcStride,
                        int16_t x, int16_t y, uint32_t color);
        void fillColor(PaintBuffer &dst, uint32_t color);
    }
}