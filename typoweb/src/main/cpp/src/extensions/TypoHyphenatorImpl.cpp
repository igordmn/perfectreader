#include "TypoHyphenatorImpl.h"

namespace typo {

using namespace std;
using namespace blink;

namespace {

struct JMethods {
    jclass cls;
    jmethodID constructor;
    jmethodID loadPatterns;
};

JMethods jmethods;

}

void TypoHyphenatorImpl::registerJni() {
    JNIScope jniScope;
    JNIEnv* env = jniScope.getEnv();
    jmethods.cls = (jclass) env->NewGlobalRef(env->FindClass("com/dmi/typoweb/TypoHyphenatorImpl"));
    jmethods.constructor = env->GetMethodID(jmethods.cls, "<init>", "()V");
    jmethods.loadPatterns = env->GetMethodID(jmethods.cls, "loadPatterns", "(JLjava/lang/String;)Z");
    static JNINativeMethod nativeMethods[] = {
        {"nativeAddPattern", "(JLjava/lang/String;)V", (void*) &nativeAddPattern},
    };
    env->RegisterNatives(jmethods.cls, nativeMethods, sizeof(nativeMethods) / sizeof(nativeMethods[0]));
}

TypoHyphenatorImpl::TypoHyphenatorImpl() {
    JNIScope jniScope;
    JNIEnv* env = jniScope.getEnv();
    jobj_ = env->NewGlobalRef(env->NewObject(jmethods.cls, jmethods.constructor));
}

TypoHyphenatorImpl::~TypoHyphenatorImpl() {
    if (currentHyphenator_) {
        delete currentHyphenator_;
    }
    if (previewHyphenator_) {
        delete previewHyphenator_;
    }
    JNIScope jniScope;
    JNIEnv* env = jniScope.getEnv();
    env->DeleteGlobalRef(jobj_);
}

int TypoHyphenatorImpl::hyphenateText(const blink::WebString& locale,
        const blink::WebString& text, unsigned int start, unsigned int end,
        unsigned int minBreakIndex, unsigned int maxBreakIndex)
{
    WordHyphenator* wordHyphenator = hyphenatorForLocale(locale);

    if (wordHyphenator) {
        if (start < 0) start = 0;
        if (end > text.length()) end = text.length();

        // уберем символы слева, которые не могут обрабатываться нашим алгоритмом (пробелы, знаки пунктуации)
        for (unsigned int i = start; i < end; i++) {
            WebUChar ch = text.at(i);
            if (!wordHyphenator->alphabetContains(ch)) {
                start = i + 1;
            } else {
                break;
            }
        }

        // сделаем то же самое справа
        for (unsigned int i = end ; i > 0; i--) {
            WebUChar ch = text.at(i - 1);
            if (!wordHyphenator->alphabetContains(ch)) {
                end = i - 1;
            } else {
                break;
            }
        }
        return wordHyphenator->hyphenateWord(text, start, end, minBreakIndex, maxBreakIndex);
    } else {
        return -1;
    }
}

WordHyphenator* TypoHyphenatorImpl::hyphenatorForLocale(const blink::WebString& locale) {
    if (locale == currentLocale_) {
        // nothing. leave as is
    } else if (locale == previewLocale_) {
        WordHyphenator* previewHyphenator = previewHyphenator_;
        previewHyphenator_ = currentHyphenator_;
        currentHyphenator_ = previewHyphenator;
        previewLocale_ = currentLocale_;
        currentLocale_ = locale;
    } else {
        if (previewHyphenator_) {
            delete previewHyphenator_;
        }
        previewHyphenator_ = currentHyphenator_;
        currentHyphenator_ = loadHyphenator(locale);
        previewLocale_ = currentLocale_;
        currentLocale_ = locale;
    }
    return currentHyphenator_;
}

WordHyphenator* TypoHyphenatorImpl::loadHyphenator(const blink::WebString& locale) {
    JNIScope jniScope;
    JNIEnv* env = jniScope.getEnv();
    jstring jLocale = JniUtils::toJavaString(env, locale.utf8());

    WordHyphenator::Builder hyphenatorBuilder;
    jboolean patternsLoaded = env->CallBooleanMethod(jobj_, jmethods.loadPatterns, (jlong) &hyphenatorBuilder, jLocale);
    if (patternsLoaded) {
        return hyphenatorBuilder.build();
    } else {
        return 0;
    }
}

void TypoHyphenatorImpl::nativeAddPattern(JNIEnv*, jobject, jlong nativeHyphenatorBuilder, jstring jPattern) {
    JNIScope jniScope;
    JNIEnv* env = jniScope.getEnv();
    const jchar* patternChars = env->GetStringChars(jPattern, JNI_FALSE);
    unsigned int patternLength = env->GetStringLength(jPattern);

    WordHyphenator::Builder* builder = (WordHyphenator::Builder*) nativeHyphenatorBuilder;
    builder->addPattern(patternChars, 0, patternLength);

    env->ReleaseStringChars(jPattern, patternChars);
}

}
