#pragma once

#include "third_party/WebKit/public/platform/TypoExtensions.h"

namespace typo {

class TypoHangingPunctuationImpl : public blink::TypoHangingPunctuation {
public:
    TypoHangingPunctuationImpl() {}
    virtual ~TypoHangingPunctuationImpl() {}
};

}
