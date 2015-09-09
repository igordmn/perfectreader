#include "TypoWeb.h"

#include "BlinkPlatformImpl.h"
#include "WebThreadImpl.h"
#include "util/JniUtils.h"
#include "util/Debug.h"
#include <string>
#include "third_party/WebKit/public/web/WebKit.h"
#include "third_party/WebKit/public/web/WebView.h"
#include "third_party/WebKit/public/web/WebLocalFrame.h"
#include "third_party/WebKit/public/web/WebSettings.h"
#include "third_party/WebKit/public/web/WebInputEvent.h"
#include "third_party/WebKit/public/web/WebConsoleMessage.h"
#include "third_party/WebKit/public/web/WebLocalFrame.h"
#include "third_party/WebKit/public/web/WebDocument.h"
#include "third_party/WebKit/public/web/WebDOMCustomEvent.h"
#include "third_party/WebKit/public/web/WebSerializedScriptValue.h"
#include "third_party/WebKit/public/platform/WebString.h"
#include "third_party/WebKit/public/platform/WebURLRequest.h"
#include "third_party/WebKit/public/platform/WebTraceLocation.h"
#include "third_party/skia/include/core/SkPictureRecorder.h"
#include "v8/include/v8.h"

using namespace std;
using namespace blink;
using namespace v8;

namespace {

struct JMethods {
    jclass cls;
    jclass stringCls;
    jmethodID scheduleAnimate;
    jmethodID didFinishLoad;
    jmethodID callObjectMethod;
};

JMethods jmethods;

}

namespace typo {

namespace {

void callJavaObjectMethod(const FunctionCallbackInfo<Value>& info) {
    JNIScope jniScope;
    JNIEnv* jniEnv = jniScope.getEnv();

    Isolate* isolate = info.GetIsolate();

    Local<Object> data = info.Data()->ToObject();
    Local<Value> v8javaObject = data->Get(String::NewFromUtf8(isolate, "javaObject"));
    Local<Value> v8javaMethod = data->Get(String::NewFromUtf8(isolate, "javaMethod"));
    jobject javaObject = (jobject) Local<External>::Cast(v8javaObject)->Value();
    jobject javaMethod = (jobject) Local<External>::Cast(v8javaMethod)->Value();

    jobjectArray jArguments = jniEnv->NewObjectArray(info.Length(), jmethods.stringCls, nullptr);
    for (int i = 0; i < info.Length(); i++) {
        Local<Value> v8argument = info[i];
        jstring jArgument = nullptr;
        if (!v8argument->IsNull()) {
            Local<String> v8str = v8argument->ToString(isolate);
            char str[v8str->Length() * 4];
            v8str->WriteUtf8(str);
            jArgument = jniEnv->NewStringUTF(str);
        }
        jniEnv->SetObjectArrayElement(jArguments, i, jArgument);
    }

    jstring jresult = (jstring) jniEnv->CallStaticObjectMethod(
            jmethods.cls, jmethods.callObjectMethod,
            javaObject, javaMethod, jArguments);

    if (jresult) {
        const char* result = jniEnv->GetStringUTFChars(jresult, nullptr);
        Local<String> v8result = String::NewFromUtf8(isolate, result);
        info.GetReturnValue().Set(v8result);
        jniEnv->ReleaseStringUTFChars(jresult, result);
    }
}

}

TypoWeb::JavascriptInterface::JavascriptInterface(
        const string& objectName, const vector<string>& functionNames,
        const jobject javaObject, const vector<jobject> javaMethods) {
    JNIScope jniScope;
    JNIEnv* env = jniScope.getEnv();

    objectName_ = objectName;
    functionNames_ = functionNames;
    javaObject_ = env->NewGlobalRef(javaObject);
    for (jobject javaMethod : javaMethods) {
        javaMethods_.push_back(env->NewGlobalRef(javaMethod));
    }
}

TypoWeb::JavascriptInterface::JavascriptInterface(const JavascriptInterface& other) {
    JNIScope jniScope;
    JNIEnv* env = jniScope.getEnv();

    objectName_ = other.objectName_;
    functionNames_ = other.functionNames_;
    javaObject_ = env->NewGlobalRef(other.javaObject_);
    for (jobject javaMethod : other.javaMethods_) {
        javaMethods_.push_back(env->NewGlobalRef(javaMethod));
    }
}

TypoWeb::JavascriptInterface::~JavascriptInterface() {
    JNIScope jniScope;
    JNIEnv* env = jniScope.getEnv();
    env->DeleteGlobalRef(javaObject_);
    for (jobject javaMethod : javaMethods_) {
        env->DeleteGlobalRef(javaMethod);
    }
}

void TypoWeb::registerJni() {
    JNIScope jniScope;
    JNIEnv* env = jniScope.getEnv();

    jmethods.cls = (jclass) env->NewGlobalRef(env->FindClass("com/dmi/typoweb/TypoWeb"));
    jmethods.stringCls = (jclass) env->NewGlobalRef(env->FindClass("java/lang/String"));
    jmethods.scheduleAnimate = env->GetMethodID(jmethods.cls, "scheduleAnimate", "()V");
    jmethods.didFinishLoad = env->GetMethodID(jmethods.cls, "didFinishLoad", "()V");
    jmethods.callObjectMethod = env->GetStaticMethodID(jmethods.cls, "callObjectMethod",
            "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/String;)Ljava/lang/String;");

    static JNINativeMethod nativeMethods[] = {
        {"nativeCreateTypoWeb", "(Lcom/dmi/typoweb/TypoWeb;F)J", (void*) &nativeCreateTypoWeb},
        {"nativeDestroyTypoWeb", "(J)V", (void*) &nativeDestroyTypoWeb},
        {"nativeResize", "(JFF)V", (void*) &nativeResize},
        {"nativeBeginFrame", "(JDDD)V", (void*) &nativeBeginFrame},
        {"nativeLayout", "(J)V", (void*) &nativeLayout},
        {"nativeRecordPicture", "(J)J", (void*) &nativeRecordPicture},
        {"nativeDestroyPicture", "(J)V", (void*) &nativeDestroyPicture},
        {"nativeDrawPicture", "(JIJ)V", (void*) &nativeDrawPicture},
        {"nativeLoadUrl", "(JLjava/lang/String;)V", (void*) &nativeLoadUrl},
        {"nativeTap", "(JFFFFFF)V", (void*) &nativeTap},
        {"nativeAddJavascriptInterface", "(JLjava/lang/String;[Ljava/lang/String;Ljava/lang/Object;[Ljava/lang/reflect/Method;)V", (void*) &nativeAddJavascriptInterface},
    };
    env->RegisterNatives(jmethods.cls, nativeMethods, sizeof(nativeMethods) / sizeof(nativeMethods[0]));
}

jlong TypoWeb::nativeCreateTypoWeb(JNIEnv*, jclass, jobject jobj, jfloat deviceDensity) {
    return (jlong) new TypoWeb(jobj, deviceDensity);
}

void TypoWeb::nativeDestroyTypoWeb(JNIEnv*, jclass, jlong nativeTypoWeb) {
    TypoWeb* typoWeb = (TypoWeb*) nativeTypoWeb;
    delete typoWeb;
}

void TypoWeb::nativeResize(JNIEnv*, jclass, jlong nativeTypoWeb, jfloat width, jfloat height) {
    TypoWeb* typoWeb = (TypoWeb*) nativeTypoWeb;
    typoWeb->resize(width, height);
}

void TypoWeb::nativeBeginFrame(JNIEnv*, jclass, jlong nativeTypoWeb, jdouble frameTimeSeconds, jdouble deadline, jdouble interval) {
    TypoWeb* typoWeb = (TypoWeb*) nativeTypoWeb;
    typoWeb->beginFrame(WebBeginFrameArgs(frameTimeSeconds, deadline, interval));
}

void TypoWeb::nativeLayout(JNIEnv*, jclass, jlong nativeTypoWeb) {
    TypoWeb* typoWeb = (TypoWeb*) nativeTypoWeb;
    typoWeb->layout();
}

jlong TypoWeb::nativeRecordPicture(JNIEnv*, jclass, jlong nativeTypoWeb) {
    TypoWeb* typoWeb = (TypoWeb*) nativeTypoWeb;
    return (jlong) typoWeb->recordPicture();
}

void TypoWeb::nativeDestroyPicture(JNIEnv*, jclass, jlong nativePicture) {
    SkPicture* picture = (SkPicture*) nativePicture;
    SkSafeUnref(picture);
}

void TypoWeb::nativeDrawPicture(JNIEnv*, jclass, jlong nativeRenderContext, jint frameBufferId, jlong nativeCurrentPicture) {
    RenderContext* renderContext = (RenderContext*) nativeRenderContext;
    SkPicture* picture = (SkPicture*) nativeCurrentPicture;
    SkRect cullRect = picture->cullRect();

    GrBackendRenderTargetDesc desc;
    desc.fWidth = SkScalarRoundToInt(cullRect.fRight - cullRect.fLeft);
    desc.fHeight = SkScalarRoundToInt(cullRect.fBottom - cullRect.fTop);
    desc.fConfig = kSkia8888_GrPixelConfig;
    desc.fOrigin = kBottomLeft_GrSurfaceOrigin;
    desc.fSampleCnt = 0;
    desc.fStencilBits = 8;
    desc.fRenderTargetHandle = frameBufferId;
    GrRenderTarget* renderTarget = renderContext->wrapBackendRenderTarget(desc);
    SkSurface* surface = SkSurface::NewRenderTargetDirect(renderTarget);

    SkCanvas* canvas = surface->getCanvas();
    canvas->drawPicture(picture);
    renderContext->resetContext();
    renderContext->flush();

    SkSafeUnref(surface);
    SkSafeUnref(renderTarget);
}

void TypoWeb::nativeLoadUrl(JNIEnv* env, jclass, jlong nativeTypoWeb, jstring jurl) {
    TypoWeb* typoWeb = (TypoWeb*) nativeTypoWeb;
    string url = JniUtils::toUTF8String(env, jurl);
    typoWeb->loadUrl(url);
}

void TypoWeb::nativeTap(JNIEnv* env, jclass, jlong nativeTypoWeb, jfloat x, jfloat y,
                        jfloat rawX, jfloat rawY, jfloat tapDiameter, jlong eventTimeSeconds) {
    TypoWeb* typoWeb = (TypoWeb*) nativeTypoWeb;
    typoWeb->tap(x, y, rawX, rawY, tapDiameter, eventTimeSeconds);
}

void TypoWeb::nativeAddJavascriptInterface(JNIEnv* env, jclass, jlong nativeTypoWeb,
                                           jstring jobjectName, jobjectArray jfunctionNames,
                                           jobject javaObject, jobjectArray javaMethods) {
    TypoWeb* typoWeb = (TypoWeb*) nativeTypoWeb;
    string objectName = JniUtils::toUTF8String(env, jobjectName);

    vector<string> functionNames;
    int functionNameCount = env->GetArrayLength(jfunctionNames);
    for (int i = 0; i< functionNameCount; i++) {
        jstring jfunctionName = (jstring) env->GetObjectArrayElement(jfunctionNames, i);
        string functionName = JniUtils::toUTF8String(env, jfunctionName);
        functionNames.push_back(functionName);
    }

    vector<jobject> javaMethodsVector;
    int methodCount = env->GetArrayLength(javaMethods);
    for (int i = 0; i< methodCount; i++) {
        jobject javaMethod = env->GetObjectArrayElement(javaMethods, i);
        javaMethodsVector.push_back(javaMethod);
    }

    CHECK(functionNameCount == methodCount);

    typoWeb->addJavascriptInterface(TypoWeb::JavascriptInterface(objectName, functionNames, javaObject, javaMethodsVector));
}

TypoWeb::TypoWeb(jobject jobj, float deviceDensity) {
    JNIScope jniScope;
    JNIEnv* env = jniScope.getEnv();

    jobj_ = env->NewGlobalRef(jobj);
    webView_ = WebView::create(this);

    WebSettings* settings = webView_->settings();
    settings->setStandardFontFamily(WebString::fromUTF8("Times New Roman"), USCRIPT_COMMON);
    settings->setFixedFontFamily(WebString::fromUTF8("Courier New"), USCRIPT_COMMON);
    settings->setSerifFontFamily(WebString::fromUTF8("Times New Roman"), USCRIPT_COMMON);
    settings->setSansSerifFontFamily(WebString::fromUTF8("Arial"), USCRIPT_COMMON);
    settings->setCursiveFontFamily(WebString::fromUTF8("Script"), USCRIPT_COMMON);
    settings->setFantasyFontFamily(WebString::fromUTF8("Impact"), USCRIPT_COMMON);
    settings->setPictographFontFamily(WebString::fromUTF8("Times New Roman"), USCRIPT_COMMON);
    settings->setDefaultFontSize(16);
    settings->setDefaultFixedFontSize(13);
    settings->setMinimumFontSize(0);
    settings->setMinimumLogicalFontSize(6);
    settings->setDefaultTextEncodingName(WebString::fromUTF8("UTF-8"));
    settings->setJavaScriptEnabled(true);
    settings->setWebSecurityEnabled(false);
    settings->setJavaScriptCanOpenWindowsAutomatically(false);
    settings->setLoadsImagesAutomatically(true);
    settings->setImagesEnabled(true);
    settings->setPluginsEnabled(false);
    settings->setDOMPasteAllowed(false);
    settings->setUsesEncodingDetector(false);
    settings->setTextAreasAreResizable(false);
    settings->setAllowScriptsToCloseWindows(false);
    settings->setDownloadableBinaryFontsEnabled(true);
    settings->setJavaScriptCanAccessClipboard(false);
    settings->setXSSAuditorEnabled(false);
    settings->setDNSPrefetchingEnabled(true);
    settings->setLocalStorageEnabled(false);
    settings->setSyncXHRInDocumentsEnabled(true);
    settings->setOfflineWebApplicationCacheEnabled(false);
    settings->setCaretBrowsingEnabled(false);
    settings->setHyperlinkAuditingEnabled(false);
    settings->setCookieEnabled(false);
    settings->setNavigateOnDragDrop(false);
    settings->setJavaEnabled(false);
    settings->setAllowUniversalAccessFromFileURLs(false);
    settings->setAllowFileAccessFromFileURLs(true);
    settings->setWebAudioEnabled(false);
    settings->setExperimentalWebGLEnabled(false);
    settings->setOpenGLMultisamplingEnabled(true);
    settings->setWebGLErrorsToConsoleEnabled(true);
    settings->setMockScrollbarsEnabled(false);
    settings->setLayerSquashingEnabled(true);
    settings->setAccelerated2dCanvasEnabled(true);
    settings->setMinimumAccelerated2dCanvasSize(257 * 256);
    settings->setAntialiased2dCanvasEnabled(true);
    settings->setAccelerated2dCanvasMSAASampleCount(0);
    settings->setAsynchronousSpellCheckingEnabled(false);
    settings->setUnifiedTextCheckerEnabled(false);
    settings->setAllowDisplayOfInsecureContent(true);
    settings->setAllowRunningOfInsecureContent(true);
    settings->setPasswordEchoEnabled(false);
    settings->setShouldPrintBackgrounds(false);
    settings->setShouldClearDocumentBackground(true);
    settings->setEnableScrollAnimator(false);
    settings->setMaxTouchPoints(1);
    settings->setDeviceSupportsTouch(true);
    settings->setDeviceSupportsMouse(false);
    settings->setEnableTouchAdjustment(true);
    settings->setShouldRespectImageOrientation(false);
    settings->setUnsafePluginPastingEnabled(false);
    settings->setEditingBehavior(WebSettings::EditingBehaviorAndroid);
    settings->setSupportsMultipleWindows(false);
    settings->setViewportEnabled(false);
    settings->setLoadWithOverviewMode(false);
    settings->setViewportMetaEnabled(false);
    settings->setMainFrameResizesAreOrientationChanges(false);
    settings->setSmartInsertDeleteEnabled(false);
    settings->setSpatialNavigationEnabled(false);
    settings->setSelectionIncludesAltImageText(true);
    settings->setV8CacheOptions(WebSettings::V8CacheOptionsDefault);
    settings->setMainFrameClipsContent(false);
    settings->setAllowCustomScrollbarInMainFrame(false);
    settings->setTextAutosizingEnabled(true);
    settings->setAccessibilityFontScaleFactor(1.0f);
    settings->setDeviceScaleAdjustment(1.0f);
    settings->setFullscreenSupported(false);
    settings->setAutoZoomFocusedNodeToLegibleScale(false);
    settings->setDoubleTapToZoomEnabled(false);
    settings->setMediaControlsOverlayPlayButtonEnabled(true);
    settings->setMediaPlaybackRequiresUserGesture(true);
    settings->setSupportDeprecatedTargetDensityDPI(false);
    settings->setUseLegacyBackgroundSizeShorthandBehavior(true);
    settings->setWideViewportQuirkEnabled(false);
    settings->setUseWideViewport(false);
    settings->setForceZeroLayoutHeight(false);
    settings->setViewportMetaLayoutSizeQuirk(false);
    settings->setViewportMetaMergeContentQuirk(false);
    settings->setViewportMetaNonUserScalableQuirk(false);
    settings->setViewportMetaZeroValuesQuirk(false);
    settings->setClobberUserAgentInitialScaleQuirk(false);
    settings->setIgnoreMainFrameOverflowHiddenQuirk(false);
    settings->setReportScreenSizeInPhysicalPixelsQuirk(false);
    settings->setShrinksViewportContentToFit(false);
    settings->setPinchOverlayScrollbarThickness(false);
    settings->setUseSolidColorScrollbars(false);
    settings->setPreferCompositingToLCDTextEnabled(true);
    settings->setAcceleratedCompositingForTransitionEnabled(true);
    settings->setThreadedScrollingEnabled(false);
    settings->setAccessibilityEnabled(false);
    settings->setTouchDragDropEnabled(false);
    settings->setTouchEditingEnabled(false);

    WebLocalFrame* webFrame = WebLocalFrame::create(this);
    webView_->setMainFrame(webFrame);
    webView_->setDeviceScaleFactor(deviceDensity);
    webView_->setFocus(true);
    webView_->setIsTransparent(true);
}

TypoWeb::~TypoWeb() {
    JNIScope jniScope;
    JNIEnv* env = jniScope.getEnv();

    webView_->close();
    env->DeleteGlobalRef(jobj_);
}

void TypoWeb::resize(float width, float height) {
    float deviceDensity = webView_->deviceScaleFactor();
    webView_->resize(WebSize(width / deviceDensity, height / deviceDensity));
}

void TypoWeb::beginFrame(const WebBeginFrameArgs& frameTime) {
    webView_->beginFrame(frameTime);
}

void TypoWeb::layout() {
    webView_->layout();
    checkContentsSize();
}

SkPicture* TypoWeb::recordPicture() {
    WebSize webSize = webView_->size();
    float deviceDensity = webView_->deviceScaleFactor();
    SkPictureRecorder recorder;
    SkCanvas* canvas = recorder.beginRecording(
            webSize.width * deviceDensity,
            webSize.height * deviceDensity,
            nullptr, 0
    );
    webView_->paint(canvas, WebRect(0, 0, webSize.width, webSize.height));
    return recorder.endRecordingAsPicture();
}

void TypoWeb::loadUrl(std::string url) {
    WebURLRequest request(GURL(url.c_str()));
    webView_->mainFrame()->loadRequest(request);
}

void TypoWeb::tap(float x, float y, float rawX, float rawY, float tapDiameter, float eventTimeSeconds) {
    float deviceDensity = webView_->deviceScaleFactor();
    WebGestureEvent gestureEvent;
    gestureEvent.x = x / deviceDensity;
    gestureEvent.y = y / deviceDensity;
    gestureEvent.globalX = rawX / deviceDensity;
    gestureEvent.globalY = rawY / deviceDensity;
    gestureEvent.timeStampSeconds = eventTimeSeconds;
    gestureEvent.sourceDevice = blink::WebGestureDeviceTouchscreen;
    gestureEvent.type = WebInputEvent::GestureTap;
    gestureEvent.data.tap.tapCount = 1;
    gestureEvent.data.tap.width = tapDiameter / deviceDensity;
    gestureEvent.data.tap.height = tapDiameter / deviceDensity;
    webView_->handleInputEvent(gestureEvent);
}

void TypoWeb::addJavascriptInterface(const JavascriptInterface& interface) {
    javascriptInterfaces_.push_back(interface);
}

void TypoWeb::checkContentsSize() {
    for (auto& entry : frameContentsSizes_) {
        WebFrame* frame = entry.first;
        WebSize size = entry.second;
        WebSize currentSize = frame->contentsSize();
        if (size != currentSize) {
            WebDocument document = frame->document();
            WebDOMCustomEvent event = document.createEvent("CustomEvent").to<WebDOMCustomEvent>();
            event.initCustomEvent("typoContentSizeChange", false, false, WebSerializedScriptValue());
            document.dispatchEvent(event);
            entry.second = currentSize;
        }
    }
}

void TypoWeb::scheduleAnimation() {
    JNIScope jniScope;
    JNIEnv* env = jniScope.getEnv();
    env->CallVoidMethod(jobj_, jmethods.scheduleAnimate);
}

void TypoWeb::didFinishLoad(WebLocalFrame* frame) {
    if (frame == webView_->mainFrame()) {
        JNIScope jniScope;
        JNIEnv* env = jniScope.getEnv();
        env->CallVoidMethod(jobj_, jmethods.didFinishLoad);
    }
}

WebFrame* TypoWeb::createChildFrame(WebLocalFrame* parent, const WebString& name, WebSandboxFlags) {
    WebLocalFrame* child = WebLocalFrame::create(this);
    parent->appendChild(child);
    frameContentsSizes_[child] = child->contentsSize();
    return child;
}

void TypoWeb::frameDetached(WebFrame* frame) {
    if (frame->parent())
        frame->parent()->removeChild(frame);
    frameContentsSizes_.erase(frame);
    frame->close();
}

void TypoWeb::didAddMessageToConsole(
        const WebConsoleMessage& webMessage,
        const WebString& webSourceName,
        unsigned sourceLine,
        const WebString& stackTrace) {
    int level = INFO;
    switch (webMessage.level) {
       case WebConsoleMessage::LevelDebug:
           level = DEBUG;
           break;
       case WebConsoleMessage::LevelLog:
       case WebConsoleMessage::LevelInfo:
           level = INFO;
           break;
       case WebConsoleMessage::LevelWarning:
           level = WARNING;
           break;
       case WebConsoleMessage::LevelError:
           level = ERROR;
           break;
    }

    LOG(level, "[%s:%d] %s", webSourceName.utf8().c_str(), sourceLine, webMessage.text.utf8().c_str());
}

void TypoWeb::didCreateScriptContext(WebLocalFrame* frame, v8::Handle<v8::Context> context, int extension_group, int world_id) {
    Isolate* isolate = Isolate::GetCurrent();
    for (const JavascriptInterface& javascriptInterface : javascriptInterfaces_) {
        const jobject javaObject = javascriptInterface.javaObject_;
        Local<Object> v8obj = Object::New(isolate);
        for (int i = 0; i < javascriptInterface.functionNames_.size(); i++) {
            const string& functionName = javascriptInterface.functionNames_[i];
            const jobject javaMethod = javascriptInterface.javaMethods_[i];

            Local<Object> v8data = Object::New(isolate);
            v8data->Set(String::NewFromUtf8(isolate, "javaObject"), External::New(isolate, javaObject));
            v8data->Set(String::NewFromUtf8(isolate, "javaMethod"), External::New(isolate, javaMethod));

            Local<String> v8functionName = String::NewFromUtf8(isolate, functionName.c_str());
            v8obj->Set(v8functionName, Function::New(isolate, &callJavaObjectMethod, v8data));
        }
        Local<String> v8objectName = String::NewFromUtf8(isolate, javascriptInterface.objectName_.c_str());
        context->Global()->Set(v8objectName, v8obj);
    }
}

}
