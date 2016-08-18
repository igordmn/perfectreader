#include "util/JniUtils.h"
#include <android/bitmap.h>
#include "util/Debug.h"
#include "GLES2/gl2.h"

extern "C" JNIEXPORT void JNICALL
Java_com_dmi_util_android_opengl_OpenGL_texSubImage2D(
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

    uint32_t *subPixels = (uint32_t *) pixels + (stride * bitmapY + bitmapX);
    for (int y = 0; y < bitmapHeight; y++) {
        glTexSubImage2D((GLenum) target, level, xoffset, yoffset + y, bitmapWidth, 1, GL_RGBA, GL_UNSIGNED_BYTE, subPixels);
        subPixels += stride;
    }

    CHECKE(AndroidBitmap_unlockPixels(env, bitmap));
}