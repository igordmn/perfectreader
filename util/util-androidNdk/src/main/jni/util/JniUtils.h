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
        static std::string toUTF8String(JNIEnv *env, jstring jstr) {
            const char *chars = env->GetStringUTFChars(jstr, nullptr);
            std::string result(chars);
            env->ReleaseStringUTFChars(jstr, chars);
            return result;
        }

        static jstring toJavaString(JNIEnv *env, std::string utf8str) {
            return toJavaString(env, utf8str.c_str());
        }

        static jstring toJavaString(JNIEnv *env, const char *cstr) {
            return env->NewStringUTF(cstr);
        }
    };

    JavaVM *JNIScope::javaVM = 0;
}