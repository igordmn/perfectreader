#pragma once

#include "../util/JniUtils.h"
#include "third_party/WebKit/public/platform/TypoExtensions.h"
#include <hash_map>

namespace typo {

class BlinkPlatformImpl;

class TypoHangingPunctuationImpl : public blink::TypoHangingPunctuation {
public:
    static void registerJni();

    TypoHangingPunctuationImpl();
    virtual ~TypoHangingPunctuationImpl();

    virtual float startHangFactor(blink::WebUChar ch) override;
    virtual float endHangFactor(blink::WebUChar ch) override;

private:
    void setHangingPunctuationConfig(jobject config);
    void convertFactors(JNIEnv* env, jcharArray jChars, jfloatArray jHangFactors, std::hash_map<blink::WebUChar, float>& destination);

    std::hash_map<blink::WebUChar, float> startHangFactors_;
    std::hash_map<blink::WebUChar, float> endHangFactors_;

    friend BlinkPlatformImpl;
};

}
