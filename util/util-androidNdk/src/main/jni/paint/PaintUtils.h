#pragma once

#include <stdint.h>
#include "PixelBuffer.h"

namespace dmi {
    namespace paintUtils {
        uint32_t argb2abgr(uint32_t argb);
        void copyPixels(PixelBuffer &dst, const AlphaBuffer &src, int16_t x, int16_t y, uint32_t color);
    }
}