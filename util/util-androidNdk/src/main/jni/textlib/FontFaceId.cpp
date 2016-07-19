#include "FontFaceID.h"

#include "../util/JniUtils.h"

using namespace dmi;

extern "C" JNIEXPORT jlong JNICALL
Java_com_dmi_util_android_graphics_FontFaceID_nativeNewFontFace(JNIEnv *env, jobject, jstring jFilePath, jint index) {
    std::string filePath = jniUtils::toUTF8String(env, jFilePath);
    return (jlong) new FontFaceID(filePath, (uint16_t) index);
}

extern "C" JNIEXPORT void JNICALL
Java_com_dmi_util_android_graphics_FontFaceID_nativeDestroyFontFace(JNIEnv *, jobject, jlong facePtr) {
    delete (FontFaceID *) facePtr;
}