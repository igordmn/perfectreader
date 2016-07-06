#include "SurfaceBuffer.h"

#include "../util/JniUtils.h"
#include <android/native_window.h>
#include <android/native_window_jni.h>

using namespace dmi;

extern "C" JNIEXPORT void JNICALL
Java_com_dmi_util_android_surface_SurfaceUtils_nativeSetBuffersGeometry(
        JNIEnv *env, jclass, jobject surface, jint width, jint height
) {
    ANativeWindow *window = ANativeWindow_fromSurface(env, surface);
    ANativeWindow_setBuffersGeometry(window, width, height, WINDOW_FORMAT_RGBA_8888);
    ANativeWindow_release(window);
}

extern "C" JNIEXPORT jlong JNICALL
Java_com_dmi_util_android_surface_SurfaceUtils_nativeLockBuffer(JNIEnv *env, jclass, jobject surface) {
    ANativeWindow *window = ANativeWindow_fromSurface(env, surface);
    ANativeWindow_Buffer buffer;
    CHECK(ANativeWindow_lock(window, &buffer, nullptr) == 0);
    CHECK(buffer.format == WINDOW_FORMAT_RGBA_8888);
    ANativeWindow_release(window);

    SurfaceBuffer *surfaceBuffer = new SurfaceBuffer();
    surfaceBuffer->width = (uint16_t) buffer.width;
    surfaceBuffer->height = (uint16_t) buffer.height;
    surfaceBuffer->stride = (uint16_t) buffer.stride;
    surfaceBuffer->data = (uint32_t *) buffer.bits;

    return (jlong) surfaceBuffer;
}

extern "C" JNIEXPORT void JNICALL
Java_com_dmi_util_android_surface_SurfaceUtils_nativeUnlockBufferAndPost(JNIEnv *env, jclass, jobject surface, jlong surfaceBufferPtr) {
    delete (SurfaceBuffer *) surfaceBufferPtr;

    ANativeWindow *window = ANativeWindow_fromSurface(env, surface);
    ANativeWindow_unlockAndPost(window);
    ANativeWindow_release(window);
}