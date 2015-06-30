#pragma once

#include "util/JniUtils.h"
#include "third_party/skia/include/gpu/GrContext.h"
#include "third_party/skia/include/gpu/gl/GrGLInterface.h"
#include "third_party/skia/include/core/SkSurface.h"
#include "third_party/skia/include/core/SkCanvas.h"

namespace typo {

class RenderContext {
public:
    static void registerJni();

    static jlong nativeCreateRenderContext(JNIEnv*, jclass);
    static void nativeDestroyRenderContext(JNIEnv*, jclass, jlong nativeRenderContext);

    RenderContext();
    virtual ~RenderContext();

    GrRenderTarget* wrapBackendRenderTarget(const GrBackendRenderTargetDesc& desc);
    void resetContext(uint32_t state = kAll_GrBackendState);
    void flush();

private:
    const GrGLInterface* glInterface_;
    GrContext* context_;
};

}
