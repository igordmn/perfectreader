#pragma once

#include "RenderContext.h"
#include "util/JniUtils.h"
#include "third_party/WebKit/public/web/WebView.h"
#include "third_party/WebKit/public/web/WebViewClient.h"
#include "third_party/WebKit/public/web/WebFrameClient.h"
#include "third_party/skia/include/gpu/GrContext.h"
#include "third_party/skia/include/gpu/gl/GrGLInterface.h"
#include "third_party/skia/include/core/SkSurface.h"
#include "third_party/skia/include/core/SkCanvas.h"
#include "third_party/skia/include/core/SkPicture.h"
#include <string>
#include <vector>
#include <map>

namespace typo {

class TypoWeb : public blink::WebViewClient, public blink::WebFrameClient {
public:
    struct JavascriptInterface {
        JavascriptInterface(const std::string& objectName, const std::vector<std::string>& functionNames,
                            const jobject javaObject, const std::vector<jobject> javaMethods);
        JavascriptInterface(const JavascriptInterface& other);
        ~JavascriptInterface();

        std::string objectName_;
        std::vector<std::string> functionNames_;
        jobject javaObject_;
        std::vector<jobject> javaMethods_;
    };

    static void registerJni();

    static jlong nativeCreateTypoWeb(JNIEnv*, jclass, jobject jobj, jfloat deviceDensity);
    static void nativeDestroyTypoWeb(JNIEnv*, jclass, jlong nativeTypoWeb);
    static void nativeResize(JNIEnv*, jclass, jlong nativeTypoWeb, jfloat width, jfloat height);
    static void nativeBeginFrame(JNIEnv*, jclass, jlong nativeTypoWeb, jdouble frameTimeSeconds, jdouble deadline, jdouble interval);
    static void nativeLayout(JNIEnv*, jclass, jlong nativeTypoWeb);
    static jlong nativeRecordPicture(JNIEnv*, jclass, jlong nativeTypoWeb);
    static void nativeDestroyPicture(JNIEnv*, jclass, jlong nativePicture);
    static void nativeDrawPicture(JNIEnv*, jclass, jlong nativeRenderContext, jint frameBufferId, jlong nativeCurrentPicture);
    static void nativeLoadUrl(JNIEnv*, jclass, jlong nativeTypoWeb, jstring jurl);
    static void nativeTap(JNIEnv* env, jclass, jlong nativeTypoWeb, jfloat x, jfloat y,
            jfloat rawX, jfloat rawY, jfloat tapDiameter, jlong eventTimeSeconds);
    static void nativeAddJavascriptInterface(JNIEnv* env, jclass, jlong nativeTypoWeb,
                                          jstring jobjectName, jobjectArray jfunctionNames,
                                          jobject javaObject, jobjectArray javaMethods);
    TypoWeb(jobject jobj, float deviceDensity);
    virtual ~TypoWeb();

    void resize(float width, float height);
    void beginFrame(const blink::WebBeginFrameArgs& frameTime);
    void layout();
    SkPicture* recordPicture();
    void loadUrl(std::string url);
    void tap(float x, float y, float rawX, float rawY, float tapDiameter, float eventTimeSeconds);
    void addJavascriptInterface(const JavascriptInterface& interface);

protected:
    void checkContentsSize();

    // WebViewClient implementation
    virtual void scheduleAnimation() override;
    virtual void didFinishLoad(blink::WebLocalFrame* frame) override;

    // WebFrameClient implementation
    virtual blink::WebFrame* createChildFrame(
            blink::WebLocalFrame* parent,
            const blink::WebString& name,
            blink::WebSandboxFlags) override;
    virtual void frameDetached(blink::WebFrame* frame) override;
    virtual void didAddMessageToConsole(
            const blink::WebConsoleMessage& message,
            const blink::WebString& sourceName,
            unsigned sourceLine,
            const blink::WebString& stackTrace) override;
    virtual void didCreateScriptContext(
            blink::WebLocalFrame* frame,
            v8::Handle<v8::Context> context,
            int extensionGroup, int worldId) override;

private:
    jobject jobj_;
    std::vector<JavascriptInterface> javascriptInterfaces_;
    std::map<blink::WebFrame*, blink::WebSize> frameContentsSizes_;
    blink::WebView* webView_;
};

}
