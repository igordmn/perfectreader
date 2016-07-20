#pragma once

#include <stdint.h>
#include "PixelBuffer.h"

namespace dmi {
    namespace paintUtils {
        void copyPixels(PixelBuffer &dst, uint8_t *src, uint16_t srcWidth, uint16_t srcHeight, uint16_t srcStride,
                        int16_t x, int16_t y, uint32_t argbColor);
    }
}