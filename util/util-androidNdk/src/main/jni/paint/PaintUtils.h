#pragma once

#include <stdint.h>
#include "PixelBuffer.h"

namespace dmi {
    namespace paintUtils {
        uint32_t argb2abgr(uint32_t argb);
        void copyPixelsAlphaBlend(PixelBuffer &dst, const AlphaBuffer &src, int16_t x, int16_t y, uint32_t color);
        void copyPixels(AlphaBuffer &dst, const AlphaBuffer &src, int16_t x, int16_t y);
        void clear(AlphaBuffer &src, uint8_t alpha);
    }
}