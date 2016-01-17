#pragma once

#include "util/JniUtils.h"
#include <set>
#include "third_party/WebKit/public/platform/WebURLLoader.h"

namespace typo {

class WebURLLoaderImpl : public blink::WebURLLoader {
public:
    static void registerJni();

    WebURLLoaderImpl(jobject urlHandler);
    virtual ~WebURLLoaderImpl();

    // implements blink::WebURLLoader
    virtual void loadSynchronously(
            const blink::WebURLRequest& request,
            blink::WebURLResponse& response,
            blink::WebURLError& error,
            blink::WebData& data) override;
    virtual void loadAsynchronously(
            const blink::WebURLRequest& request,
            blink::WebURLLoaderClient* client) override;
    virtual void cancel() override;
    virtual void setDefersLoading(bool value) override;

    void didReceiveResponse(long long contentLength, std::string contentType, std::string charset);
    void didReceiveData(const char* data, int dataLength);
    void didFinishLoading(long long totalLength);
    void didFail(std::string message);

    // used by java
    static void nativeDidReceiveResponse(JNIEnv* env, jclass,
            jlong nativeWebURLLoaderImpl, jlong contentLength, jstring jContentType);
    static void nativeDidReceiveData(JNIEnv* env, jclass, jlong nativeWebURLLoaderImpl, jobject jData, jint dataLength);
    static void nativeDidFinishLoading(JNIEnv* env, jclass, jlong nativeWebURLLoaderImpl, jlong totalLength);
    static void nativeDidFail(JNIEnv* env, jclass, jlong nativeWebURLLoaderImpl, jstring jMessage);
    static jobject nativeCreateBuffer(JNIEnv* env, jclass, jlong size);
    static void nativeDeleteBuffer(JNIEnv* env, jclass, jobject jBuffer);

private:
    class ProcessDataURLTask;

    jobject jobj_;
    blink::WebURLLoaderClient* client_ = 0;
    ProcessDataURLTask* processDataURLTask_ = 0;

    void processDataURL(std::string url);
};

}

