#pragma once

#include <string>
#include <jni.h>

#include "Debug.h"

namespace dmi {
    class JNIScope {
    private:
        static JavaVM *javaVM;
        JNIEnv *_env;

    public:
        JNIScope() {
            CHECK(javaVM->GetEnv((void **) &_env, JNI_VERSION_1_6) == JNI_OK);
            CHECK(_env->PushLocalFrame(16) == 0);
        }

        ~JNIScope() {
            CHECK(_env->PopLocalFrame(nullptr) == 0);
        }

        JNIEnv *env() { return _env; }
    };

    class JniUtils {
    public:
        static std::string toUTF8String(JNIEnv *env, jstring jstr);
        static jstring toJavaString(JNIEnv *env, std::string utf8str);
        static jstring toJavaString(JNIEnv *env, const char *cstr);
    };
}