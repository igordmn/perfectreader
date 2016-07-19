#include "FontFacePath.h"

#include "../util/JniUtils.h"

using namespace dmi;

extern "C" JNIEXPORT jlong JNICALL
Java_com_dmi_util_android_graphics_FontFacePath_nativeNewFontFace(JNIEnv *env, jobject, jstring jFilePath, jint index) {
    std::string filePath = jniUtils::toUTF8String(env, jFilePath);
    return (jlong) new FontFacePath(filePath, (uint16_t) index);
}

extern "C" JNIEXPORT void JNICALL
Java_com_dmi_util_android_graphics_FontFacePath_nativeDestroyFontFace(JNIEnv *, jobject, jlong facePtr) {
    delete (FontFacePath *) facePtr;
}