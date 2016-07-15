#pragma once

#include <string>
#include <jni.h>

namespace dmi {
    void registerJniUtils(JavaVM* vm);

    class JNIScope {
    private:
        JNIEnv *_env;

    public:
        JNIScope();
        ~JNIScope();

        JNIEnv *env() { return _env; }
    };

    namespace jniUtils {
        std::string toUTF8String(JNIEnv *env, jstring jstr);
        jstring toJavaString(JNIEnv *env, std::string utf8str);
        jstring toJavaString(JNIEnv *env, const char *cstr);
        std::string getJavaStackTrace(JNIEnv *env);
    };
}