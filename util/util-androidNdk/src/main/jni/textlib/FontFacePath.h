#pragma once

#include <string>

namespace dmi {
    struct FontFacePath {
        std::string filePath;
        uint16_t index;

        FontFacePath(const std::string &filePath, uint16_t index) : filePath(filePath), index(index) { }
    };
}