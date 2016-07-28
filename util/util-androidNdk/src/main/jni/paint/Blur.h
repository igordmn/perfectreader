#pragma once

#include <stdint.h>
#include "PixelBuffer.h"

namespace dmi {
    namespace blur {
        uint16_t gaussianBlurAdditionalPixels(float radius);
        void boxBlurHorizontal(AlphaBuffer &dst, float radius);
        void boxBlurVertical(AlphaBuffer &dst, float radius);
        void boxBlur(AlphaBuffer &dst, float radius);
        void gaussianBlur(AlphaBuffer &dst, float radius);
    }
}