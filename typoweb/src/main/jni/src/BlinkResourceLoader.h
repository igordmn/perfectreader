#pragma once

#include "util/JniUtils.h"
#include "third_party/WebKit/public/platform/WebData.h"

namespace typo {

class BlinkResourceLoader {
public:
    static void registerJni();
    static blink::WebData loadResource(const char* name);
};

}
