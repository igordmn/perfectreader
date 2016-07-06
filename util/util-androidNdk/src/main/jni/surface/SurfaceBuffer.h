#pragma once

#include <stdint.h>

namespace dmi {
    struct SurfaceBuffer {
        uint16_t width;
        uint16_t height;
        uint16_t stride;   // количество пикселей с строке, >= width
        uint32_t *data;    // пиксели в формате RGBA
    };
}