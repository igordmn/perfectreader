#include "BlinkPlatformImpl.h"

#include "DataURL.h"
#include "WebURLLoaderImpl.h"
#include "WebThreadImpl.h"
#include "BlinkResourceLoader.h"
#include "TypoWebLibrary.h"
#include "util/Debug.h"
#include <string>
#include <sys/time.h>
#include "third_party/WebKit/public/platform/WebTraceLocation.h"

using namespace std;
using namespace blink;

namespace typo {

class BlinkPlatformImpl::SharedTimerTask : public blink::WebThread::Task {
private:
    void (*func_)();

public:
    SharedTimerTask(void (*func)()) : func_(func) { }
    virtual ~SharedTimerTask() { }

    virtual void run() override {
        if (func_) {
            func_();
        }
    }
};

class BlinkPlatformImpl::CallContextFuncTask : public blink::WebThread::Task {
private:
    void (*func_)(void*);
    void* context_;

public:
    CallContextFuncTask(void (*func)(void*), void* context) : func_(func), context_(context) { }
    virtual ~CallContextFuncTask() { }

    virtual void run() override {
        if (func_) {
            func_(context_);
        }
    }
};

BlinkPlatformImpl::BlinkPlatformImpl(string userAgent) :
        sharedTimerFunc_(0),
        userAgent_(userAgent)
{}

BlinkPlatformImpl::~BlinkPlatformImpl() {}

void BlinkPlatformImpl::pause() {
    paused_ = true;
}

void BlinkPlatformImpl::resume() {
    paused_ = false;
    if (startSharedTimerOnResume_) {
        sharedTimerTask_ = new SharedTimerTask(sharedTimerFunc_);
        TypoWebLibrary::mainThread()->postDelayedTask(blink::WebTraceLocation(), sharedTimerTask_, sharedTimerIntervalMilliseconds_);
        startSharedTimerOnResume_ = false;
    }
}

void BlinkPlatformImpl::setHangingPunctuationConfig(jobject config) {
    typoExtensionsImpl_.hangingPunctuation().setHangingPunctuationConfig(config);
}

WebURLLoader* BlinkPlatformImpl::createURLLoader() {
    return new WebURLLoaderImpl();
}

WebString BlinkPlatformImpl::userAgent() {
    return WebString::fromUTF8(userAgent_);
}

WebThread* BlinkPlatformImpl::createThread(const char* name) {
    return new WebThreadImpl(name);
}

WebThread* BlinkPlatformImpl::currentThread() {
    return WebThreadImpl::current();
}

void BlinkPlatformImpl::yieldCurrentThread() {
    WebThreadImpl::yieldCurrent();
}

const unsigned char* BlinkPlatformImpl::getTraceCategoryEnabledFlag(const char*) {
    return (const unsigned char*) "";
}

double BlinkPlatformImpl::currentTime() {
    struct timeval tv;
    CHECK(gettimeofday(&tv, NULL) == 0);
    return tv.tv_sec + tv.tv_usec / 1.0E6;
}

double BlinkPlatformImpl::monotonicallyIncreasingTime() {
    struct timespec ts;
    CHECK(clock_gettime(CLOCK_MONOTONIC, &ts) == 0);
    return ts.tv_sec + ts.tv_nsec / 1.0E9;
}

void BlinkPlatformImpl::setSharedTimerFiredFunction(void (*func)()) {
    sharedTimerFunc_ = func;
}

void BlinkPlatformImpl::setSharedTimerFireInterval(double intervalSeconds) {
    TypoWebLibrary::mainThread()->cancelTask(sharedTimerTask_);
    sharedTimerIntervalMilliseconds_ = ceil(intervalSeconds * 1000);
    if (paused_) {
        startSharedTimerOnResume_ = true;
    } else {
        sharedTimerTask_ = new SharedTimerTask(sharedTimerFunc_);
        TypoWebLibrary::mainThread()->postDelayedTask(blink::WebTraceLocation(), sharedTimerTask_, sharedTimerIntervalMilliseconds_);
    }
}

void BlinkPlatformImpl::stopSharedTimer() {
    TypoWebLibrary::mainThread()->cancelTask(sharedTimerTask_);
}

void BlinkPlatformImpl::callOnMainThread(void (*func)(void*), void* context) {
    TypoWebLibrary::mainThread()->postTask(blink::WebTraceLocation(), new CallContextFuncTask(func, context));
}

WebScheduler* BlinkPlatformImpl::scheduler() {
    return &webScheduler_;
}

WebThemeEngine* BlinkPlatformImpl::themeEngine() {
    return &webThemeEngine_;
}

size_t BlinkPlatformImpl::maxDecodedImageBytes() {
    return 3 * 1024 * 1024 * 4;
}

WebData BlinkPlatformImpl::loadResource(const char* name) {
    return BlinkResourceLoader::loadResource(name);
}

WebData BlinkPlatformImpl::parseDataURL(const WebURL& url, WebString& mimetypeOut, WebString& charsetOut) {
    string mimeType, charset, data;
    DataURL::parse(url.string().utf8(), &mimeType, &charset, &data);
    mimetypeOut = WebString::fromUTF8(mimeType);
    charsetOut = WebString::fromUTF8(charset);
    return data;
}

WebMimeRegistry* BlinkPlatformImpl::mimeRegistry() {
    return &mimeRegistry_;
}

WebString BlinkPlatformImpl::defaultLocale() {
    return WebString("en-US");
}

bool BlinkPlatformImpl::canAccelerate2dCanvas() {
    return true;
}

}
