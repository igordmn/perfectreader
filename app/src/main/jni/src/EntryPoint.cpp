#include "util/JniUtils.h"
#include "TypoWebRegisterJni.h"

using namespace typo;

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    JniUtils::init(vm);
    typoWebRegisterJni();
    return JNI_VERSION_1_6;
}
