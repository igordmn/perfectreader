#pragma once

#include <string>

#include <ft2build.h>
#include FT_STROKER_H

namespace dmi {
    struct FontFaceID {
        const std::string filePath;
        const uint16_t index;

        FontFaceID(const std::string &filePath, uint16_t index) : filePath(filePath), index(index) { }
    };
}