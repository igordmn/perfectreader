#include "JniUtils.h"

namespace dmi {
    JavaVM *JNIScope::javaVM = 0;

    std::string JniUtils::toUTF8String(JNIEnv *env, jstring jstr) {
        const char *chars = env->GetStringUTFChars(jstr, nullptr);
        std::string result(chars);
        env->ReleaseStringUTFChars(jstr, chars);
        return result;
    }

    jstring JniUtils::toJavaString(JNIEnv *env, std::string utf8str) {
        return toJavaString(env, utf8str.c_str());
    }

    jstring JniUtils::toJavaString(JNIEnv *env, const char *cstr) {
        return env->NewStringUTF(cstr);
    }
}