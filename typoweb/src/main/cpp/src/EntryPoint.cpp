#include "TypoWeb.h"
#include "WebThreadImpl.h"
#include "WebMimeRegistryImpl.h"
#include "WebURLLoaderImpl.h"
#include "BlinkResourceLoader.h"
#include "RenderContext.h"
#include "TypoWebLibrary.h"
#include "util/JniUtils.h"
#include "third_party/skia/include/core/SkGraphics.h"

using namespace typo;

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    JniUtils::init(vm);
    TypoWeb::registerJni();
    WebThreadImpl::registerJni();
    WebMimeRegistryImpl::registerJni();
    WebURLLoaderImpl::registerJni();
    TypoWebLibrary::registerJni();
    RenderContext::registerJni();
    BlinkResourceLoader::registerJni();

    SkGraphics::SetFontCacheLimit(8 * 1024 * 1024);
    SkGraphics::SetResourceCacheSingleAllocationByteLimit(64 * 1024 * 1024);
    SkGraphics::Init();

    return JNI_VERSION_1_6;
}
