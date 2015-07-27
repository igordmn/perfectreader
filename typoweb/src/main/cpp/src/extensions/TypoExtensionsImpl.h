#pragma once

#include "../extensions/TypoHangingPunctuationImpl.h"
#include "../extensions/TypoHyphenatorImpl.h"
#include "third_party/WebKit/public/platform/TypoExtensions.h"

namespace typo {

class TypoExtensionsImpl : public blink::TypoExtensions {
public:
    virtual ~TypoExtensionsImpl() {}

    virtual blink::TypoHyphenator& hyphenator() override { return hyphenator_; }
    virtual blink::TypoHangingPunctuation& hangingPunctuation() override { return hangingPunctuation_; }

private:
    TypoHyphenatorImpl hyphenator_;
    TypoHangingPunctuationImpl hangingPunctuation_;
};

}
