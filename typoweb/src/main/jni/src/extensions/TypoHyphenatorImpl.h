#pragma once

#include "WordHyphenator.h"
#include "../util/JniUtils.h"
#include "third_party/WebKit/public/platform/TypoExtensions.h"
#include "third_party/WebKit/public/platform/WebString.h"

namespace typo {

class BlinkPlatformImpl;

class TypoHyphenatorImpl : public blink::TypoHyphenator {
public:
    static void registerJni();

    TypoHyphenatorImpl();
    virtual ~TypoHyphenatorImpl();

    virtual int hyphenateText(const blink::WebString& locale,
            const blink::WebString& text, unsigned int start, unsigned int end,
            unsigned int minBreakIndex, unsigned int maxBreakIndex) override;
private:
    void setHyphenationPatternsLoader(JNIEnv* env, jobject patternsLoader);

    WordHyphenator* hyphenatorForLocale(const blink::WebString& locale);
    WordHyphenator* loadHyphenator(const blink::WebString& locale);

    static void nativeAddPattern(JNIEnv*, jobject, jlong nativeHyphenatorBuilder, jstring jPattern);

    jobject jobj_;

    WordHyphenator* currentHyphenator_ = 0;
    WordHyphenator* previewHyphenator_ = 0;
    blink::WebString currentLocale_;
    blink::WebString previewLocale_;

    friend BlinkPlatformImpl;
};

}
