#pragma once

#include <string>

namespace dmi {
    struct FontFaceID {
        std::string filePath;
        uint16_t index;

        FontFaceID(const std::string &filePath, uint16_t index) : filePath(filePath), index(index) { }
    };
}