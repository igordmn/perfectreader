#include "util/JniUtils.h"
#include "util/Debug.h"
#include <android/bitmap.h>

#include <cmath>

namespace {
    float computedGamma = -1;

    uint8_t alphaToCorrectedAlpha[256];
    uint8_t alphaAndColorToCorrectedColor[256][256];

    void setGamma(float gamma) {
        if (computedGamma != gamma) {
            computedGamma = gamma;
            for (int a = 0; a < 256; a++) {
                double alpha = a / 255.0;
                double correctedAlpha = pow(alpha, computedGamma);
                alphaToCorrectedAlpha[a] = (uint8_t) (correctedAlpha * 255);
                for (int c = 0; c < 256; c++) {
                    alphaAndColorToCorrectedColor[a][c] = (uint8_t) (c / alpha * correctedAlpha);
                }
            }
        }
    }

    inline uint32_t correctAlphaGamma(uint32_t color) {
        uint8_t a = (uint8_t) (color >> 24);
        uint8_t b = (uint8_t) ((color >> 16) & 0xFF);
        uint8_t g = (uint8_t) ((color >> 8) & 0xFF);
        uint8_t r = (uint8_t) (color & 0xFF);

        uint8_t *colorToCorrectedColor = alphaAndColorToCorrectedColor[a];
        uint8_t ac = alphaToCorrectedAlpha[a];
        uint8_t bc = colorToCorrectedColor[b];
        uint8_t gc = colorToCorrectedColor[g];
        uint8_t rc = colorToCorrectedColor[r];

        return (ac << 24) | (bc << 16) | (gc << 8) | rc;
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_dmi_util_android_graphics_CorrectGammaKt_correctAlphaGamma(
        JNIEnv *env, jclass, jobject jBitmap, jint x, jint y, jint width, jint height, float gamma
) {
    AndroidBitmapInfo info;

    void *pixels;
    CHECKE(AndroidBitmap_getInfo(env, jBitmap, &info));
    CHECKE(AndroidBitmap_lockPixels(env, jBitmap, &pixels));
    CHECK(x >= 0 && width >= 0);
    CHECK(y >= 0 && height >= 0);
    CHECK(x + width <= info.width);
    CHECK(y + height <= info.height);
    uint32_t stride = info.stride / 4;

    setGamma(gamma);

    uint32_t *p = (uint32_t *) pixels + (width * y + x);
    for (int yi = 0; yi < height; yi++) {
        uint32_t *r = p;
        for (int xi = 0; xi < width; xi++) {
            *r = correctAlphaGamma(*r);
            r++;
        }
        p += stride;
    }

    CHECKE(AndroidBitmap_unlockPixels(env, jBitmap));
}
