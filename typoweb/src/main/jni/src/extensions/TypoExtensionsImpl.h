#pragma once

#include "../extensions/TypoHangingPunctuationImpl.h"
#include "../extensions/TypoHyphenatorImpl.h"
#include "third_party/WebKit/public/platform/TypoExtensions.h"

namespace typo {

class TypoExtensionsImpl : public blink::TypoExtensions {
public:
    virtual ~TypoExtensionsImpl() {}

    virtual TypoHyphenatorImpl& hyphenator() override { return hyphenator_; }
    virtual TypoHangingPunctuationImpl& hangingPunctuation() override { return hangingPunctuation_; }

private:
    TypoHyphenatorImpl hyphenator_;
    TypoHangingPunctuationImpl hangingPunctuation_;
};

}
