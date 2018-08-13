#include "util/JniUtils.h"
#include "util/Debug.h"
#include <android/bitmap.h>
#include <cmath>
#include <CAIR.h>

namespace {
    void pixelsToCML(uint32_t *pixels, uint32_t stride, CML_color &dest) {
        uint32_t *p = pixels;
        for (int y = 0; y < dest.Height(); y++) {
            uint32_t *r = p;
            for (int x = 0; x < dest.Width(); x++) {
                uint32_t color = *r;
                uint8_t alpha = (uint8_t) (color >> 24);
                uint8_t blue = (uint8_t) ((color >> 16) & 0xFF);
                uint8_t green = (uint8_t) ((color >> 8) & 0xFF);
                uint8_t red = (uint8_t) (color & 0xFF);

                CML_RGBA &destPixel = dest(x, y);
                destPixel.alpha = alpha;
                destPixel.red = red;
                destPixel.green = green;
                destPixel.blue = blue;

                r++;
            }
            p += stride;
        }
    }

    void CMLToPixels(CML_color &src, uint32_t *pixels, uint32_t stride) {
        uint32_t *p = pixels;
        for (int y = 0; y < src.Height(); y++) {
            uint32_t *r = p;
            for (int x = 0; x < src.Width(); x++) {
                CML_RGBA &srcPixel = src(x, y);
                *r = (srcPixel.alpha << 24) | (srcPixel.blue << 16) | (srcPixel.green << 8) | srcPixel.red;
                r++;
            }
            p += stride;
        }
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_dmi_util_android_graphics_ResizesKt_resizeSeamCarvingTo(
        JNIEnv *env, jclass, jobject jSource, jobject jDestination
) {
    AndroidBitmapInfo srcInfo;
    AndroidBitmapInfo dstInfo;
    void *srcPixels;
    void *dstPixels;

    CHECKE(AndroidBitmap_getInfo(env, jSource, &srcInfo));
    CHECKE(AndroidBitmap_getInfo(env, jDestination, &dstInfo));
    CHECKE(AndroidBitmap_lockPixels(env, jSource, &srcPixels));
    CHECKE(AndroidBitmap_lockPixels(env, jDestination, &dstPixels));

    CML_color source(srcInfo.width, srcInfo.height);
    CML_int weights(srcInfo.width, srcInfo.height);
    weights.Fill(0);
    pixelsToCML((uint32_t *) srcPixels, srcInfo.stride / 4, source);

    CML_color dst(dstInfo.width, dstInfo.height);
    CML_int dstWeights(1, 1);

    CAIR(&source, &weights, dstInfo.width, dstInfo.height, SOBEL, BACKWARD, &dstWeights, &dst, NULL);
    CMLToPixels(dst, (uint32_t *) dstPixels, dstInfo.stride / 4);

    CHECKE(AndroidBitmap_unlockPixels(env, jDestination));
    CHECKE(AndroidBitmap_unlockPixels(env, jSource));
}
