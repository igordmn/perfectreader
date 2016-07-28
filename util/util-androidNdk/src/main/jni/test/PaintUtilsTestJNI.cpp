#include "../paint/PaintUtils.h"
#include "../paint/Blur.h"

#include "../util/JniUtils.h"

using namespace std;
using namespace dmi;
using namespace paintUtils;

extern "C" JNIEXPORT jint JNICALL
Java_com_dmi_util_android_test_PaintUtilsTestJNI_argb2abgr(JNIEnv *, jclass, jint argb) {
    return paintUtils::argb2abgr((uint32_t) argb);
}

extern "C" JNIEXPORT void JNICALL
Java_com_dmi_util_android_test_PaintUtilsTestJNI_copyPixelsAlphaBlend(
        JNIEnv *env, jclass,
        jint dstWidth, jint dstHeight, jint dstStride, jintArray jDstData,
        jint srcWidth, jint srcHeight, jint srcStride, jbyteArray jSrcData,
        jint x, jint y, jint color
) {
    IntArray dstData(env, jDstData);
    ByteArray srcData(env, jSrcData);

    PixelBuffer dst;
    dst.width = (uint16_t) dstWidth;
    dst.height = (uint16_t) dstHeight;
    dst.stride = (uint16_t) dstStride;
    dst.data = (uint32_t *) dstData.array;

    AlphaBuffer src;
    src.width = (uint16_t) srcWidth;
    src.height = (uint16_t) srcHeight;
    src.stride = (uint16_t) srcStride;
    src.data = (uint8_t *) srcData.array;

    paintUtils::copyPixelsAlphaBlend(dst, src, (int16_t) x, (int16_t) y, (uint32_t) color);
}

extern "C" JNIEXPORT void JNICALL
Java_com_dmi_util_android_test_PaintUtilsTestJNI_copyPixels(
        JNIEnv *env, jclass,
        jint dstWidth, jint dstHeight, jint dstStride, jbyteArray jDstData,
        jint srcWidth, jint srcHeight, jint srcStride, jbyteArray jSrcData,
        jint x, jint y
) {
    ByteArray dstData(env, jDstData);
    ByteArray srcData(env, jSrcData);

    AlphaBuffer dst;
    dst.width = (uint16_t) dstWidth;
    dst.height = (uint16_t) dstHeight;
    dst.stride = (uint16_t) dstStride;
    dst.data = (uint8_t *) dstData.array;

    AlphaBuffer src;
    src.width = (uint16_t) srcWidth;
    src.height = (uint16_t) srcHeight;
    src.stride = (uint16_t) srcStride;
    src.data = (uint8_t *) srcData.array;

    paintUtils::copyPixels(dst, src, (int16_t) x, (int16_t) y);
}

extern "C" JNIEXPORT void JNICALL
Java_com_dmi_util_android_test_PaintUtilsTestJNI_clear(
        JNIEnv *env, jclass,
        jint srcWidth, jint srcHeight, jint srcStride, jbyteArray jSrcData,
        jbyte alpha
) {
    ByteArray srcData(env, jSrcData);

    AlphaBuffer src;
    src.width = (uint16_t) srcWidth;
    src.height = (uint16_t) srcHeight;
    src.stride = (uint16_t) srcStride;
    src.data = (uint8_t *) srcData.array;

    paintUtils::clear(src, (uint8_t) alpha);
}

extern "C" JNIEXPORT jint JNICALL
Java_com_dmi_util_android_test_PaintUtilsTestJNI_gaussianBlurAdditionalPixels(
        JNIEnv *, jclass,
        jfloat radius
) {
    return blur::gaussianBlurAdditionalPixels(radius);
}

extern "C" JNIEXPORT void JNICALL
Java_com_dmi_util_android_test_PaintUtilsTestJNI_boxBlurHorizontal(
        JNIEnv *env, jclass,
        jint dstWidth, jint dstHeight, jint dstStride, jbyteArray jDstData,
        jfloat radius
) {
    ByteArray srcData(env, jDstData);

    AlphaBuffer dst;
    dst.width = (uint16_t) dstWidth;
    dst.height = (uint16_t) dstHeight;
    dst.stride = (uint16_t) dstStride;
    dst.data = (uint8_t *) srcData.array;

    blur::boxBlurHorizontal(dst, radius);
}

extern "C" JNIEXPORT void JNICALL
Java_com_dmi_util_android_test_PaintUtilsTestJNI_boxBlurVertical(
        JNIEnv *env, jclass,
        jint dstWidth, jint dstHeight, jint dstStride, jbyteArray jDstData,
        jfloat radius
) {
    ByteArray srcData(env, jDstData);

    AlphaBuffer dst;
    dst.width = (uint16_t) dstWidth;
    dst.height = (uint16_t) dstHeight;
    dst.stride = (uint16_t) dstStride;
    dst.data = (uint8_t *) srcData.array;

    blur::boxBlurVertical(dst, radius);
}