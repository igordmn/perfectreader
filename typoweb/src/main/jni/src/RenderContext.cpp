#include "RenderContext.h"

#include "skia/GrGLCreateNativeInterface_android.h"
#include <sys/time.h>
#include "base/logging.h"

namespace typo {

void RenderContext::registerJni() {
    JNIScope jniScope;
    JNIEnv* env = jniScope.getEnv();
    jclass cls = env->FindClass("com/dmi/typoweb/RenderContext");
    static JNINativeMethod nativeMethods[] = {
        {"nativeCreateRenderContext", "()J", (void*) &nativeCreateRenderContext},
        {"nativeDestroyRenderContext", "(J)V", (void*) &nativeDestroyRenderContext},
    };
    env->RegisterNatives(cls, nativeMethods, sizeof(nativeMethods) / sizeof(nativeMethods[0]));
}

jlong RenderContext::nativeCreateRenderContext(JNIEnv*, jclass) {
    return (jlong) new RenderContext();
}

void RenderContext::nativeDestroyRenderContext(JNIEnv*, jclass, jlong nativeRenderSurface) {
    delete (RenderContext*) nativeRenderSurface;
}

RenderContext::RenderContext() {
    glInterface_ = GrGLCreateAndroidNativeInterface();
    context_ = GrContext::Create(kOpenGL_GrBackend, (GrBackendContext) glInterface_);
    CHECK(glInterface_);
    CHECK(context_);
}

RenderContext::~RenderContext() {
    SkSafeUnref(context_);
    SkSafeUnref(glInterface_);
}

GrRenderTarget* RenderContext::wrapBackendRenderTarget(const GrBackendRenderTargetDesc& desc) {
    return context_->textureProvider()->wrapBackendRenderTarget(desc);
}

void RenderContext::resetContext(uint32_t state) {
    context_->resetContext(state);
}

void RenderContext::flush() {
    context_->flush();
}

}
