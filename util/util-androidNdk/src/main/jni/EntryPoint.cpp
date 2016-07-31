#include "util/JniUtils.h"

using namespace dmi;

jint JNI_OnLoad(JavaVM *vm, void *) {
    dmi::registerJniUtils(vm);
    return JNI_VERSION_1_6;
}