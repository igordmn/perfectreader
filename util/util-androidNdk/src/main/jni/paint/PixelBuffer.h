#pragma once

#include <stdint.h>

namespace dmi {
    struct PixelBuffer {
        uint16_t width = 0;
        uint16_t height = 0;
        uint16_t stride = 0;   // количество пикселей с строке, >= width
        uint32_t *data = 0;    // пиксели в формате ABGR
    };

    struct AlphaBuffer {
        uint16_t width = 0;
        uint16_t height = 0;
        uint16_t stride = 0;   // количество пикселей с строке, >= width
        uint8_t *data = 0;    // пиксели в формате ABGR
    };
}