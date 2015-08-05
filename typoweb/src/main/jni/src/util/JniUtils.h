#pragma once

#include <string>
#include <jni.h>

namespace typo {

class JNIScope {
public:
    JNIScope();
    ~JNIScope();

    JNIEnv* getEnv();

private:
    JNIEnv* jniEnv_;
};

class JniUtils {
public:
    static void init(JavaVM* vm);
    static std::string toUTF8String(JNIEnv* env, jstring jstr);
    static jstring toJavaString(JNIEnv* env, std::string utf8str);
    static jstring toJavaString(JNIEnv* env, const char* cstr);

private:
    static void threadDestroyed(void* value);
};

}
