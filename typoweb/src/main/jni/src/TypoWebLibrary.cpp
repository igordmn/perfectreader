#include "TypoWebLibrary.h"

#include "BlinkPlatformImpl.h"
#include "base/logging.h"
#include "third_party/WebKit/public/web/WebKit.h"
#include "third_party/WebKit/public/web/WebRuntimeFeatures.h"
#include "third_party/WebKit/public/web/WebCache.h"
#include "third_party/WebKit/public/web/WebImageCache.h"
#include "third_party/WebKit/public/platform/WebTraceLocation.h"
#include "third_party/skia/include/core/SkGraphics.h"
#include "third_party/icu/source/common/unicode/udata.h"
#include "third_party/icu/source/common/unicode/uclean.h"
#include "v8/include/v8.h"

using namespace blink;
using namespace std;

namespace typo {

namespace {

void clearSkiaCache() {
    size_t fontCacheLimit = SkGraphics::SetFontCacheLimit(0);
    SkGraphics::SetFontCacheLimit(fontCacheLimit);
}

}

base::ThreadTaskRunnerHandle* TypoWebLibrary::threadTaskRunnerHandle_ = 0;
BlinkPlatformImpl* TypoWebLibrary::blinkPlatform_ = 0;
void* TypoWebLibrary::icuData_ = 0;
WebThreadImpl* TypoWebLibrary::mainThread_ = 0;

void TypoWebLibrary::registerJni() {
    JNIScope jniScope;
    JNIEnv* env = jniScope.getEnv();
    jclass cls = env->FindClass("com/dmi/typoweb/TypoWebLibrary");
    static JNINativeMethod nativeMethods[] = {
        {"nativeStartMainThread", "()V", (void*) &nativeStartMainThread},
        {"nativeMainThread", "()Lcom/dmi/typoweb/WebThreadImpl;", (void*) &nativeMainThread},
        {"nativeInitBlink", "(Ljava/lang/String;)V", (void*) &nativeInitBlink},
        {"nativeInitICU", "(Ljava/nio/ByteBuffer;)V", (void*) &nativeInitICU},
        {"nativeNewDirectByteBuffer", "(I)Ljava/nio/ByteBuffer;", (void*) &nativeNewDirectByteBuffer},
        {"nativeLowMemoryNotification", "(Z)V", (void*) &nativeLowMemoryNotification},
        {"nativePause", "()V", (void*) &nativePause},
        {"nativeResume", "()V", (void*) &nativeResume},
        {"nativeSetURLHandler", "(Lcom/dmi/typoweb/URLHandler;)V", (void*) &nativeSetURLHandler},
        {"nativeSetHangingPunctuationConfig", "(Lcom/dmi/typoweb/HangingPunctuationConfig;)V", (void*) &nativeSetHangingPunctuationConfig},
        {"nativeSetHyphenationPatternsLoader", "(Lcom/dmi/typoweb/HyphenationPatternsLoader;)V", (void*) &nativeSetHyphenationPatternsLoader},
    };
    env->RegisterNatives(cls, nativeMethods, sizeof(nativeMethods) / sizeof(nativeMethods[0]));
}

WebThreadImpl* TypoWebLibrary::mainThread() {
    return mainThread_;
}

void TypoWebLibrary::nativeStartMainThread(JNIEnv* env, jclass) {
    CHECK(mainThread_ == 0);
    mainThread_ = new WebThreadImpl("Main web thread");
}

jobject TypoWebLibrary::nativeMainThread(JNIEnv* env, jclass) {
    return mainThread_ ? mainThread_->jobj_ : 0;
}

void TypoWebLibrary::nativeInitBlink(JNIEnv* env, jclass, jstring jUserAgent) {
    CHECK(blinkPlatform_ == 0);

    SkGraphics::SetFontCacheLimit(8 * 1024 * 1024);
    SkGraphics::SetResourceCacheSingleAllocationByteLimit(8 * 1024 * 1024);
    SkGraphics::SetResourceCacheTotalByteLimit(32 * 1024 * 1024);
    SkGraphics::Init();

    string flag("--harmony --expose-gc --max-executable-size=96 --max-old-space-size=128 --max-semi-space-size=1");
    v8::V8::SetFlagsFromString(flag.c_str(), static_cast<int>(flag.size()));

    threadTaskRunnerHandle_ = new base::ThreadTaskRunnerHandle(scoped_refptr<base::SingleThreadTaskRunner>()); // without this blink::initialize crashes
    blinkPlatform_ = new BlinkPlatformImpl(JniUtils::toUTF8String(env, jUserAgent));
    blink::initialize(blinkPlatform_);

    WebImageCache::setCacheLimitInBytes(8 * 1024 * 1024);
    WebCache::setCapacities(0, 4 * 1024 * 1024, 8 * 1024 * 1024);
}

void TypoWebLibrary::nativeInitICU(JNIEnv* env, jclass, jobject icuData) {
    CHECK(icuData_ == 0);

    icuData_ = env->GetDirectBufferAddress(icuData);

    UErrorCode err = U_ZERO_ERROR;
    udata_setCommonData(icuData_, &err);
    CHECK(err == U_ZERO_ERROR);
}

jobject TypoWebLibrary::nativeNewDirectByteBuffer(JNIEnv* env, jclass, jint size) {
    void* buffer = malloc(size);
    return env->NewDirectByteBuffer(buffer, size);
}

void TypoWebLibrary::nativeLowMemoryNotification(JNIEnv*, jclass, jboolean critical) {
    WebCache::pruneAll();

    if (blink::mainThreadIsolate()) {
        blink::mainThreadIsolate()->LowMemoryNotification();
    }

    if (critical) {
        WebCache::clear();
        WebImageCache::clear();
        clearSkiaCache();
    }
}

void TypoWebLibrary::nativePause(JNIEnv*, jclass) {
    blinkPlatform_->pause();
}

void TypoWebLibrary::nativeResume(JNIEnv*, jclass) {
    blinkPlatform_->resume();
}

void TypoWebLibrary::nativeSetURLHandler(JNIEnv* env, jclass, jobject urlHandler) {
    blinkPlatform_->setURLHandler(env, urlHandler);
}

void TypoWebLibrary::nativeSetHangingPunctuationConfig(JNIEnv* env, jclass, jobject config) {
    blinkPlatform_->setHangingPunctuationConfig(env, config);
}

void TypoWebLibrary::nativeSetHyphenationPatternsLoader(JNIEnv* env, jclass, jobject loader) {
    blinkPlatform_->setHyphenationPatternsLoader(env, loader);
}

}
