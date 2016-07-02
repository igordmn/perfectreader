#include "util/JniUtils.h"

using namespace dmi;

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    return JNI_VERSION_1_6;
}