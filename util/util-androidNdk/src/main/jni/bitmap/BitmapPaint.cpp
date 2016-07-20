#include "../paint/PixelBuffer.h"

#include "../util/JniUtils.h"
#include "../util/Debug.h"
#include <android/bitmap.h>

using namespace dmi;

extern "C" JNIEXPORT jlong JNICALL
Java_com_dmi_util_android_graphics_BitmapPaint_nativeLockBuffer(JNIEnv *env, jclass, jobject jBitmap) {
    AndroidBitmapInfo info;
    void *pixels;

    CHECKE(AndroidBitmap_getInfo(env, jBitmap, &info));
    CHECKM(info.format == ANDROID_BITMAP_FORMAT_RGBA_8888, "Bitmap must be RGBA_8888")
    CHECKE(AndroidBitmap_lockPixels(env, jBitmap, &pixels));

    PixelBuffer *pixelBuffer = new PixelBuffer();
    pixelBuffer->width = (uint16_t) info.width;
    pixelBuffer->height = (uint16_t) info.height;
    pixelBuffer->stride = (uint16_t) (info.stride / 4);
    pixelBuffer->data = (uint32_t *) pixels;

    return (jlong) pixelBuffer;
}

extern "C" JNIEXPORT void JNICALL
Java_com_dmi_util_android_graphics_BitmapPaint_nativeUnlockBufferAndPost(JNIEnv *env, jclass, jobject jBitmap, jlong pixelBufferPtr) {
    delete (PixelBuffer *) pixelBufferPtr;
    CHECKE(AndroidBitmap_unlockPixels(env, jBitmap));
}