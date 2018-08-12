#include "util/JniUtils.h"
#include <android/bitmap.h>
#include "util/Debug.h"
#include "GLES2/gl2.h"

extern "C" JNIEXPORT void JNICALL
Java_com_dmi_util_android_opengl_GLUtilsKt_texSubImage2D(
        JNIEnv *env, jclass, jint target, jint level, jint xoffset, jint yoffset,
        jobject bitmap, jint bitmapX, jint bitmapY, jint bitmapWidth, jint bitmapHeight
) {
    AndroidBitmapInfo info;
    void *pixels;
    CHECKE(AndroidBitmap_getInfo(env, bitmap, &info));
    CHECK(info.format == ANDROID_BITMAP_FORMAT_RGBA_8888)
    CHECK(bitmapX + bitmapWidth <= info.width && bitmapX >= 0 && bitmapWidth >= 0)
    CHECK(bitmapY + bitmapHeight <= info.height && bitmapY >= 0 && bitmapHeight >= 0)
    CHECKE(AndroidBitmap_lockPixels(env, bitmap, &pixels));
    uint32_t stride = info.stride / 4;

    uint32_t *subPixels = new uint32_t[bitmapWidth * bitmapHeight];
    uint32_t *p = (uint32_t *) pixels + (stride * bitmapY + bitmapX);
    uint32_t *s = subPixels;
    for (int y = 0; y < bitmapHeight; y++) {
        std::memcpy(s, p, (size_t) bitmapWidth * 4);
        p += stride;
        s += bitmapWidth;
    }

    glTexSubImage2D((GLenum) target, level, xoffset, yoffset, bitmapWidth, bitmapHeight, GL_RGBA, GL_UNSIGNED_BYTE, subPixels);

    delete [] subPixels;
    CHECKE(AndroidBitmap_unlockPixels(env, bitmap));
}