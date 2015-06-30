#include "TypoWebLibrary.h"

#include "BlinkPlatformImpl.h"
#include "util/Debug.h"
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

namespace typo {

namespace {

void clearSkiaCache() {
    size_t fontCacheLimit = SkGraphics::SetFontCacheLimit(0);
    SkGraphics::SetFontCacheLimit(fontCacheLimit);
}

}

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

    WebRuntimeFeatures::enableExperimentalFeatures(true);
    WebRuntimeFeatures::enableApplicationCache(false);
    WebRuntimeFeatures::enableDatabase(false);
    WebRuntimeFeatures::enableCompositedSelectionUpdate(false);
    WebRuntimeFeatures::enableDecodeToYUV(false);
    WebRuntimeFeatures::enableDisplayList2dCanvas(false);
    WebRuntimeFeatures::enableEncryptedMedia(false);
    WebRuntimeFeatures::enablePrefixedEncryptedMedia(false);
    WebRuntimeFeatures::enableBleedingEdgeFastPaths(false);
    WebRuntimeFeatures::enableExperimentalCanvasFeatures(false);
    WebRuntimeFeatures::enableFastMobileScrolling(false);
    WebRuntimeFeatures::enableFileSystem(false);
    WebRuntimeFeatures::enableLocalStorage(false);
    WebRuntimeFeatures::enableMediaPlayer(false);
    WebRuntimeFeatures::enableMediaCapture(false);
    WebRuntimeFeatures::enableMediaSource(false);
    WebRuntimeFeatures::enableNotifications(false);
    WebRuntimeFeatures::enableNavigatorContentUtils(false);
    WebRuntimeFeatures::enableNavigationTransitions(false);
    WebRuntimeFeatures::enableNetworkInformation(false);
    WebRuntimeFeatures::enableOrientationEvent(false);
    WebRuntimeFeatures::enablePagePopup(false);
    WebRuntimeFeatures::enablePeerConnection(false);
    WebRuntimeFeatures::enableRequestAutocomplete(false);
    WebRuntimeFeatures::enableScreenOrientation(false);
    WebRuntimeFeatures::enableScriptedSpeech(false);
    WebRuntimeFeatures::enableServiceWorker(false);
    WebRuntimeFeatures::enableSessionStorage(false);
    WebRuntimeFeatures::enableTouch(true);
    WebRuntimeFeatures::enableTouchIconLoading(false);
    WebRuntimeFeatures::enableWebAudio(false);
    WebRuntimeFeatures::enableWebGLDraftExtensions(false);
    WebRuntimeFeatures::enableWebGLImageChromium(false);
    WebRuntimeFeatures::enableWebMIDI(false);
    WebRuntimeFeatures::enableXSLT(false);
    WebRuntimeFeatures::enableOverlayScrollbars(false);
    WebRuntimeFeatures::enableOverlayFullscreenVideo(false);
    WebRuntimeFeatures::enableSharedWorker(false);
    WebRuntimeFeatures::enablePreciseMemoryInfo(false);
    WebRuntimeFeatures::enableLayerSquashing(true);
    WebRuntimeFeatures::enableShowModalDialog(false);
    blinkPlatform_ = new BlinkPlatformImpl(JniUtils::toUTF8String(env, jUserAgent));
    blink::initialize(blinkPlatform_);
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

}
