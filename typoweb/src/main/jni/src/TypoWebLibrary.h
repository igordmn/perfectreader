#pragma once

#include "WebThreadImpl.h"
#include "BlinkPlatformImpl.h"
#include "util/JniUtils.h"
#include "base/thread_task_runner_handle.h"
#include "base/single_thread_task_runner.h"

namespace typo {

class TypoWebLibrary {
public:
    static void registerJni();

    static WebThreadImpl* mainThread();

private:
    // used by java
    static void nativeStartMainThread(JNIEnv* env, jclass);
    static jobject nativeMainThread(JNIEnv* env, jclass);
    static void nativeInitBlink(JNIEnv* env, jclass, jstring jUserAgent);
    static void nativeInitICU(JNIEnv* env, jclass, jobject icuData);
    static jobject nativeNewDirectByteBuffer(JNIEnv* env, jclass, jint size);
    static void nativeLowMemoryNotification(JNIEnv*, jclass, jboolean critical);
    static void nativePause(JNIEnv*, jclass);
    static void nativeResume(JNIEnv*, jclass);
    static void nativeSetURLHandler(JNIEnv*, jclass, jobject urlHandler);
    static void nativeSetHangingPunctuationConfig(JNIEnv*, jclass, jobject config);
    static void nativeSetHyphenationPatternsLoader(JNIEnv*, jclass, jobject loader);

    static BlinkPlatformImpl* blinkPlatform_;
    static void* icuData_;
    static WebThreadImpl* mainThread_;
    static base::ThreadTaskRunnerHandle* threadTaskRunnerHandle_;
};

}
