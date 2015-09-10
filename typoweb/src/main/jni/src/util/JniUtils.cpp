#include "JniUtils.h"

#include "base/logging.h"
#include <pthread.h>
#include <stdlib.h>

using std::string;
using std::wstring;

namespace {

JavaVM* javaVM = 0;
pthread_key_t envThreadKey;

}

namespace typo {

JNIScope::JNIScope() {
    jniEnv_ = (JNIEnv*) pthread_getspecific(envThreadKey);
    if (jniEnv_ == nullptr) {
        CHECK(javaVM->AttachCurrentThread(&jniEnv_, nullptr) == 0);
        pthread_setspecific(envThreadKey, jniEnv_);
    }
    CHECK(jniEnv_->PushLocalFrame(16) == 0);
}

JNIScope::~JNIScope() {
    CHECK(jniEnv_->PopLocalFrame(NULL) == 0);
}

JNIEnv* JNIScope::getEnv() {
    return jniEnv_;
}

void JniUtils::init(JavaVM* vm) {
    javaVM = vm;
    assert(pthread_key_create(&envThreadKey, threadDestroyed) == 0);
}

void JniUtils::threadDestroyed(void* value) {
    JNIEnv* env = (JNIEnv*) value;
    if (env != nullptr) {
        javaVM->DetachCurrentThread();
        pthread_setspecific(envThreadKey, nullptr);
    }
}

string JniUtils::toUTF8String(JNIEnv* env, jstring jstr) {
    const char* chars = env->GetStringUTFChars(jstr, nullptr);
    std::string result(chars);
    env->ReleaseStringUTFChars(jstr, chars);
    return result;
}

jstring JniUtils::toJavaString(JNIEnv* env, string utf8str) {
    return toJavaString(env, utf8str.c_str());
}

jstring JniUtils::toJavaString(JNIEnv* env, const char* cstr) {
    return env->NewStringUTF(cstr);
}

}
