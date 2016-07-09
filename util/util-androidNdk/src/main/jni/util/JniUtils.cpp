#include "JniUtils.h"
#include "Debug.h"

namespace {
    JavaVM *javaVM = 0;

    jclass jniUtilsCls;
    jmethodID getCurrentStackTraceMethod;
}


namespace dmi {
    void registerJniUtils(JavaVM* vm) {
        javaVM = vm;

        JNIScope scope;
        JNIEnv *env = scope.env();

        jniUtilsCls = (jclass) env->NewGlobalRef(env->FindClass("com/dmi/util/android/jni/JniUtils"));
        getCurrentStackTraceMethod = env->GetStaticMethodID(jniUtilsCls, "getCurrentStackTrace", "()Ljava/lang/String;");
    }

    JNIScope::JNIScope() {
        CHECK(javaVM->GetEnv((void **) &_env, JNI_VERSION_1_6) == JNI_OK);
        CHECK(_env->PushLocalFrame(16) == 0);
    }

    JNIScope::~JNIScope() {
        CHECK(_env->PopLocalFrame(nullptr) == 0);
    }

    namespace jniUtils {
        std::string toUTF8String(JNIEnv *env, jstring jstr) {
            const char *chars = env->GetStringUTFChars(jstr, nullptr);
            std::string result(chars);
            env->ReleaseStringUTFChars(jstr, chars);
            return result;
        }

        jstring toJavaString(JNIEnv *env, std::string utf8str) {
            return toJavaString(env, utf8str.c_str());
        }

        jstring toJavaString(JNIEnv *env, const char *cstr) {
            return env->NewStringUTF(cstr);
        }

        std::string getJavaStackTrace(JNIEnv *env) {
            return toUTF8String(env, (jstring) env->CallStaticObjectMethod(jniUtilsCls, getCurrentStackTraceMethod));
        }
    }
}