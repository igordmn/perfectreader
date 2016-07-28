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

    class ByteArray {
    private:
        JNIEnv *env;
        jbyteArray jArray;

    public:
        jbyte *array;

        ByteArray(JNIEnv *env, jbyteArray jArray) : env(env), jArray(jArray) {
            array = env->GetByteArrayElements(jArray, NULL);
        }

        ~ByteArray() {
            int length = env->GetArrayLength(jArray);
            env->SetByteArrayRegion(jArray, 0, length, array);
            env->ReleaseByteArrayElements(jArray, array, 0);
        }
    };

    class IntArray {
    private:
        JNIEnv *env;
        jintArray jArray;

    public:
        jint *array;

        IntArray(JNIEnv *env, jintArray jArray) : env(env), jArray(jArray) {
            array = env->GetIntArrayElements(jArray, NULL);
        }

        ~IntArray() {
            int length = env->GetArrayLength(jArray);
            env->SetIntArrayRegion(jArray, 0, length, array);
            env->ReleaseIntArrayElements(jArray, array, 0);
        }
    };

    namespace jniUtils {
        std::string toUTF8String(JNIEnv *env, jstring jstr);
        jstring toJavaString(JNIEnv *env, std::string utf8str);
        jstring toJavaString(JNIEnv *env, const char *cstr);
        std::string getJavaStackTrace(JNIEnv *env);
    };
}