#include "TypoWeb.h"
#include "WebThreadImpl.h"
#include "WebMimeRegistryImpl.h"
#include "WebURLLoaderImpl.h"
#include "BlinkResourceLoader.h"
#include "RenderContext.h"
#include "TypoWebLibrary.h"
#include "extensions/TypoHyphenatorImpl.h"
#include "extensions/TypoHangingPunctuationImpl.h"
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
    TypoHyphenatorImpl::registerJni();
    TypoHangingPunctuationImpl::registerJni();
    return JNI_VERSION_1_6;
}
