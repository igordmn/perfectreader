#pragma once

#include "util/JniUtils.h"
#include "third_party/WebKit/public/platform/WebMimeRegistry.h"

namespace typo {

class WebMimeRegistryImpl : public blink::WebMimeRegistry {
public:
    static void registerJni();

    virtual ~WebMimeRegistryImpl();
    virtual WebMimeRegistry::SupportsType supportsMIMEType(const blink::WebString& mimeType) override;
    virtual WebMimeRegistry::SupportsType supportsImageMIMEType(const blink::WebString& mimeType) override;
    virtual WebMimeRegistry::SupportsType supportsImagePrefixedMIMEType(const blink::WebString& mimeType) override;
    virtual WebMimeRegistry::SupportsType supportsJavaScriptMIMEType(const blink::WebString& mimeType) override;
    virtual WebMimeRegistry::SupportsType supportsNonImageMIMEType(const blink::WebString& mimeType) override;
    virtual WebMimeRegistry::SupportsType supportsMediaMIMEType(
            const blink::WebString& mimeType,
            const blink::WebString& codecs,
            const blink::WebString& keySystem) override;
    virtual bool supportsMediaSourceMIMEType(const blink::WebString& mimeType, const blink::WebString& codecs);
    virtual blink::WebString mimeTypeForExtension(const blink::WebString& extension) override;
    virtual blink::WebString wellKnownMimeTypeForExtension(const blink::WebString& extension) override;
    virtual blink::WebString mimeTypeFromFile(const blink::WebString& filePath) override;
};

}

