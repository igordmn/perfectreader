#pragma once

#include "WebMimeRegistryImpl.h"
#include "extensions/TypoExtensionsImpl.h"
#include "util/JniUtils.h"
#include <string>
#include "third_party/WebKit/public/platform/Platform.h"
#include "third_party/WebKit/public/platform/WebThemeEngine.h"

namespace typo {

class TypoWebLibrary;

class BlinkPlatformImpl : public blink::Platform {
public:
    BlinkPlatformImpl(std::string userAgent);
    virtual ~BlinkPlatformImpl();

    void pause();
    void resume();
    void setURLHandler(JNIEnv* env, jobject urlHandler);
    void setHangingPunctuationConfig(JNIEnv* env, jobject config);
    void setHyphenationPatternsLoader(JNIEnv* env, jobject patternsLoader);

    virtual blink::WebURLLoader* createURLLoader() override;
    virtual blink::WebString userAgent() override;
    virtual blink::WebThread* createThread(const char* name) override;
    virtual blink::WebThread* currentThread() override;
    virtual void yieldCurrentThread() override;
    virtual const unsigned char* getTraceCategoryEnabledFlag(const char*) override;
    virtual double currentTime() override;
    virtual double monotonicallyIncreasingTime() override;
    virtual void setSharedTimerFiredFunction(void (*func)()) override;
    virtual void setSharedTimerFireInterval(double intervalSeconds) override;
    virtual void stopSharedTimer() override;
    virtual blink::WebThemeEngine* themeEngine() override;
    virtual size_t maxDecodedImageBytes() override;
    virtual blink::WebData loadResource(const char* name) override;
    virtual blink::WebData parseDataURL(const blink::WebURL& url, blink::WebString& mimetype, blink::WebString& charset) override;
    virtual blink::WebMimeRegistry* mimeRegistry() override;
    virtual blink::WebString defaultLocale() override;
    virtual bool canAccelerate2dCanvas() override;
    virtual void cryptographicallyRandomValues(unsigned char* buffer, size_t length) override {};
    virtual blink::TypoExtensions* typoExtensions() override { return &typoExtensionsImpl_; }

private:
    class SharedTimerTask;
    class CallContextFuncTask;

    std::string userAgent_;

    bool paused_ = false;

    SharedTimerTask* sharedTimerTask_ = 0;
    void (*sharedTimerFunc_)();
    long long sharedTimerIntervalMilliseconds_ = 0;
    bool startSharedTimerOnResume_ = false;

    blink::WebThemeEngine webThemeEngine_;
    WebMimeRegistryImpl mimeRegistry_;
    TypoExtensionsImpl typoExtensionsImpl_;
    jobject urlHandler_ = 0;
};

}
