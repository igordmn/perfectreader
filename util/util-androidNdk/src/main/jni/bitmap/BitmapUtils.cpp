#include "../paint/PaintBuffer.h"

#include "../util/JniUtils.h"
#include "../util/Debug.h"
#include <android/bitmap.h>

using namespace dmi;

extern "C" JNIEXPORT jlong JNICALL
Java_com_dmi_util_android_bitmap_BitmapUtils_nativeLockBuffer(JNIEnv *env, jclass, jobject jBitmap) {
    AndroidBitmapInfo info;
    void *pixels;

    CHECKE(AndroidBitmap_getInfo(env, jBitmap, &info));
    CHECKM(info.format == ANDROID_BITMAP_FORMAT_RGBA_8888, "Bitmap must be RGBA_8888")
    CHECKE(AndroidBitmap_lockPixels(env, jBitmap, &pixels));

    PaintBuffer *paintBuffer = new PaintBuffer();
    paintBuffer->width = (uint16_t) info.width;
    paintBuffer->height = (uint16_t) info.height;
    paintBuffer->stride = (uint16_t) (info.stride / 4);
    paintBuffer->data = (uint32_t *) pixels;

    return (jlong) paintBuffer;
}

extern "C" JNIEXPORT void JNICALL
Java_com_dmi_util_android_bitmap_BitmapUtils_nativeUnlockBufferAndPost(JNIEnv *env, jclass, jobject jBitmap, jlong paintBufferPtr) {
    delete (PaintBuffer *) paintBufferPtr;
    CHECKE(AndroidBitmap_unlockPixels(env, jBitmap));
}