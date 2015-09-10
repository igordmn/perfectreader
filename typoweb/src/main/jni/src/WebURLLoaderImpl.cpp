#include "WebURLLoaderImpl.h"

#include "WebThreadImpl.h"
#include "DataURL.h"
#include "TypoWebLibrary.h"
#include "base/logging.h"
#include "util/StringUtils.h"
#include <string>
#include "third_party/WebKit/public/platform/WebString.h"
#include "third_party/WebKit/public/platform/WebURLError.h"
#include "third_party/WebKit/public/platform/WebURLRequest.h"
#include "third_party/WebKit/public/platform/WebURLResponse.h"
#include "third_party/WebKit/public/platform/WebURLLoader.h"
#include "third_party/WebKit/public/platform/WebURLLoaderClient.h"
#include "third_party/WebKit/public/platform/WebTraceLocation.h"

using namespace std;
using namespace blink;

namespace {

struct JMethods {
    jclass cls;
    jmethodID constructor;
    jmethodID load;
    jmethodID cancel;
};

JMethods jmethods;

}

namespace typo {

void WebURLLoaderImpl::registerJni() {
    JNIScope jniScope;
    JNIEnv* env = jniScope.getEnv();

    jmethods.cls = (jclass) env->NewGlobalRef(env->FindClass("com/dmi/typoweb/WebURLLoaderImpl"));
    jmethods.constructor = env->GetMethodID(jmethods.cls, "<init>", "(JLcom/dmi/typoweb/URLHandler;)V");
    jmethods.load = env->GetMethodID(jmethods.cls, "load", "(Ljava/lang/String;)V");
    jmethods.cancel = env->GetMethodID(jmethods.cls, "cancel", "()V");

    static JNINativeMethod nativeMethods[] = {
        {"nativeDidReceiveResponse", "(JJLjava/lang/String;)V", (void*) &nativeDidReceiveResponse},
        {"nativeDidReceiveData", "(J[BI)V", (void*) &nativeDidReceiveData},
        {"nativeDidFinishLoading", "(JJ)V", (void*) &nativeDidFinishLoading},
        {"nativeDidFail", "(JLjava/lang/String;)V", (void*) &nativeDidFail},
    };
    env->RegisterNatives(jmethods.cls, nativeMethods, sizeof(nativeMethods) / sizeof(nativeMethods[0]));
}

class WebURLLoaderImpl::ProcessDataURLTask : public WebThreadImpl::Task {
private:
    WebURLLoaderImpl* loader;
    string url;

public:
    ProcessDataURLTask(WebURLLoaderImpl* loader, string url) :
        loader(loader), url(url)
    { }
    virtual ~ProcessDataURLTask() { }

    virtual void run() override {
        loader->processDataURL(url);
    }
};

WebURLLoaderImpl::WebURLLoaderImpl(jobject urlHandler) : processDataURLTask_(0) {
    JNIScope jniScope;
    JNIEnv* env = jniScope.getEnv();
    jobj_ = env->NewGlobalRef(env->NewObject(jmethods.cls, jmethods.constructor, (jlong) this, urlHandler));
}

WebURLLoaderImpl::~WebURLLoaderImpl() {
    JNIScope jniScope;
    JNIEnv* env = jniScope.getEnv();
    env->DeleteGlobalRef(jobj_);
}

void WebURLLoaderImpl::loadSynchronously(
        const WebURLRequest& request,
        WebURLResponse& response,
        WebURLError& error,
        WebData& data) {
    LOG(WARNING) << "WebURLLoaderImpl::loadSynchronously not implemented";
}

void WebURLLoaderImpl::loadAsynchronously(
        const WebURLRequest& request,
        WebURLLoaderClient* client) {
    CHECK(!client_);
    client_ = client;

    string urlStr = request.url().string().utf8();
    if (urlStr.compare(0, 5, "data:") == 0) {
        processDataURLTask_ = new ProcessDataURLTask(this, urlStr);
        TypoWebLibrary::mainThread()->postTask(WebTraceLocation(), processDataURLTask_);
    } else {
        JNIScope jniScope;
        JNIEnv* env = jniScope.getEnv();
        jstring jurl = JniUtils::toJavaString(env, urlStr);
        env->CallVoidMethod(jobj_, jmethods.load, jurl);
    }

}

void WebURLLoaderImpl::processDataURL(string url) {
    string mimeType, charset, data;
    typo::DataURL::parse(url, &mimeType, &charset, &data);
    if (data.size() > 0) {
        didReceiveResponse(data.size(), mimeType, charset);
        didReceiveData(data.c_str(), data.size());
        didFinishLoading(data.size());
    } else {
        didFail("Cannot load data url");
    }
}

void WebURLLoaderImpl::cancel() {
    if (processDataURLTask_) {
        TypoWebLibrary::mainThread()->cancelTask(processDataURLTask_);
        processDataURLTask_ = 0;
    }
    JNIScope jniScope;
    JNIEnv* env = jniScope.getEnv();
    env->CallVoidMethod(jobj_, jmethods.cancel);
}

void WebURLLoaderImpl::setDefersLoading(bool value) {
    LOG(WARNING) << "WebURLLoaderImpl::setDefersLoading not implemented";
}

void WebURLLoaderImpl::didReceiveResponse(long long contentLength, string contentType, string charset) {
    CHECK(client_);
    WebString webContentType = WebString::fromUTF8(contentType);
    WebString webContentLengthStr = WebString::fromUTF8(StringUtils::toString(contentLength));
    string contentTypeHeader = contentType + (charset != "" ? "; charset=" + charset: "");
    WebString webContentTypeHeader = WebString::fromUTF8(contentTypeHeader);

    WebURLResponse response;
    response.initialize();
    response.setHTTPVersion(WebURLResponse::HTTP_1_1);
    response.setHTTPStatusCode(200);
    response.setHTTPStatusText("OK");
    response.setMIMEType(webContentType);
    response.setExpectedContentLength(contentLength);
    response.addHTTPHeaderField("Content-Type", webContentTypeHeader);
    response.addHTTPHeaderField("Content-Length", webContentLengthStr);
    client_->didReceiveResponse(this, response);
}

void WebURLLoaderImpl::didReceiveData(const char* data, int dataLength) {
    CHECK(client_);
    client_->didReceiveData(this, data, dataLength, dataLength);
}

void WebURLLoaderImpl::didFinishLoading(long long totalLength) {
    CHECK(client_);
    client_->didFinishLoading(this, 0, totalLength);
}

void WebURLLoaderImpl::didFail(string message) {
    CHECK(client_);
    WebURLError error;
    error.reason = -2; // A generic failure occurred.
    error.localizedDescription =  WebString::fromUTF8(message);
    client_->didFail(this, error);
}

void WebURLLoaderImpl::nativeDidReceiveResponse(JNIEnv* env,
        jobject, jlong nativeWebURLLoaderImpl, jlong contentLength, jstring jContentType) {
    typo::WebURLLoaderImpl* loader = (typo::WebURLLoaderImpl*) nativeWebURLLoaderImpl;
    loader->didReceiveResponse(contentLength, typo::JniUtils::toUTF8String(env, jContentType), "");
}

void WebURLLoaderImpl::nativeDidReceiveData(JNIEnv* env,
        jobject, jlong nativeWebURLLoaderImpl, jbyteArray jData, jint dataLength) {
    typo::WebURLLoaderImpl* loader = (typo::WebURLLoaderImpl*) nativeWebURLLoaderImpl;
    char data[dataLength];
    env->GetByteArrayRegion(jData, 0, dataLength, (jbyte*) data);
    loader->didReceiveData(data, dataLength);
}

void WebURLLoaderImpl::nativeDidFinishLoading(JNIEnv* env, jobject, jlong nativeWebURLLoaderImpl, jlong totalLength) {
    typo::WebURLLoaderImpl* loader = (typo::WebURLLoaderImpl*) nativeWebURLLoaderImpl;
    loader->didFinishLoading(totalLength);
}

void WebURLLoaderImpl::nativeDidFail(JNIEnv* env, jobject, jlong nativeWebURLLoaderImpl, jstring jMessage) {
    typo::WebURLLoaderImpl* loader = (typo::WebURLLoaderImpl*) nativeWebURLLoaderImpl;
    loader->didFail(typo::JniUtils::toUTF8String(env, jMessage));
}

}
