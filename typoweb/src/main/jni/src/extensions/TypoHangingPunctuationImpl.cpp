#include "TypoHangingPunctuationImpl.h"
#include "../util/Debug.h"

namespace typo {

using namespace std;
using namespace blink;

namespace {

struct JConfigMeta {
    jclass cls;
    jfieldID startChars;
    jfieldID startCharsHangFactors;
    jfieldID endChars;
    jfieldID endCharsHangFactors;
};

JConfigMeta jConfigMeta;

}

void TypoHangingPunctuationImpl::registerJni() {
    JNIScope jniScope;
    JNIEnv* env = jniScope.getEnv();
    jConfigMeta.cls = (jclass) env->NewGlobalRef(env->FindClass("com/dmi/typoweb/HangingPunctuationConfig"));
    jConfigMeta.startChars = env->GetFieldID(jConfigMeta.cls, "startChars", "[C");
    jConfigMeta.startCharsHangFactors = env->GetFieldID(jConfigMeta.cls, "startCharsHangFactors", "[F");
    jConfigMeta.endChars = env->GetFieldID(jConfigMeta.cls, "endChars", "[C");
    jConfigMeta.endCharsHangFactors = env->GetFieldID(jConfigMeta.cls, "endCharsHangFactors", "[F");
}

TypoHangingPunctuationImpl::TypoHangingPunctuationImpl() {}

TypoHangingPunctuationImpl::~TypoHangingPunctuationImpl() {}

float TypoHangingPunctuationImpl::startHangFactor(blink::WebUChar ch) {
    auto it = startHangFactors_.find(ch);
    return it != startHangFactors_.end() ? it->second : 0;
}

float TypoHangingPunctuationImpl::endHangFactor(blink::WebUChar ch) {
    auto it = endHangFactors_.find(ch);
    return it != endHangFactors_.end() ? it->second : 0;
}

void TypoHangingPunctuationImpl::setHangingPunctuationConfig(JNIEnv* env, jobject config) {
    startHangFactors_.clear();
    endHangFactors_.clear();
    if (config) {
        jcharArray jStartChars = (jcharArray) env->GetObjectField(config, jConfigMeta.startChars);
        jfloatArray jStartCharsHangFactors = (jfloatArray) env->GetObjectField(config, jConfigMeta.startCharsHangFactors);
        jcharArray jEndChars = (jcharArray) env->GetObjectField(config, jConfigMeta.endChars);
        jfloatArray jEndCharsHangFactors = (jfloatArray) env->GetObjectField(config, jConfigMeta.endCharsHangFactors);

        convertFactors(env, jStartChars, jStartCharsHangFactors, startHangFactors_);
        convertFactors(env, jEndChars, jEndCharsHangFactors, endHangFactors_);
    }
}

void TypoHangingPunctuationImpl::convertFactors(JNIEnv* env, jcharArray jChars, jfloatArray jHangFactors, std::hash_map<blink::WebUChar, float>& destination) {
    int charsCount = env->GetArrayLength(jChars);

    jchar* chars = env->GetCharArrayElements(jChars, NULL);
    jfloat* hangFactors = env->GetFloatArrayElements(jHangFactors, NULL);

    for (int i = 0; i < charsCount; i++) {
        destination[chars[i]] = hangFactors[i];
    }

    env->ReleaseCharArrayElements(jChars, chars, 0);
    env->ReleaseFloatArrayElements(jHangFactors, hangFactors, 0);
}

}



