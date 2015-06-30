#include "WebMimeRegistryImpl.h"

#include <string>
#include "third_party/WebKit/public/platform/WebString.h"

using namespace std;
using namespace blink;

namespace {

struct JMethods {
    jclass cls;
    jmethodID supportsMIMEType;
    jmethodID supportsImageMIMEType;
    jmethodID supportsImagePrefixedMIMEType;
    jmethodID supportsJavaScriptMIMEType;
    jmethodID supportsNonImageMIMEType;
    jmethodID supportsMediaMIMEType;
    jmethodID supportsMediaSourceMIMEType;
    jmethodID mimeTypeForExtension;
    jmethodID wellKnownMimeTypeForExtension;
    jmethodID mimeTypeFromFile;
};

JMethods jmethods;

}

namespace typo {

void WebMimeRegistryImpl::registerJni() {
    JNIScope jniScope;
    JNIEnv* env = jniScope.getEnv();
    jmethods.cls = (jclass) env->NewGlobalRef(env->FindClass("com/dmi/typoweb/WebMimeRegistryImpl"));
    jmethods.supportsMIMEType = env->GetStaticMethodID(jmethods.cls,
            "supportsMIMEType", "(Ljava/lang/String;)Z");
    jmethods.supportsImageMIMEType = env->GetStaticMethodID(jmethods.cls,
            "supportsImageMIMEType", "(Ljava/lang/String;)Z");
    jmethods.supportsImagePrefixedMIMEType = env->GetStaticMethodID(jmethods.cls,
            "supportsImagePrefixedMIMEType", "(Ljava/lang/String;)Z");
    jmethods.supportsJavaScriptMIMEType = env->GetStaticMethodID(jmethods.cls,
            "supportsJavaScriptMIMEType", "(Ljava/lang/String;)Z");
    jmethods.supportsNonImageMIMEType = env->GetStaticMethodID(jmethods.cls,
            "supportsNonImageMIMEType", "(Ljava/lang/String;)Z");
    jmethods.supportsMediaMIMEType = env->GetStaticMethodID(jmethods.cls,
            "supportsMediaMIMEType", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Z");
    jmethods.supportsMediaSourceMIMEType = env->GetStaticMethodID(jmethods.cls,
            "supportsMediaSourceMIMEType", "(Ljava/lang/String;Ljava/lang/String;)Z");
    jmethods.mimeTypeForExtension = env->GetStaticMethodID(jmethods.cls,
            "mimeTypeForExtension", "(Ljava/lang/String;)Ljava/lang/String;");
    jmethods.wellKnownMimeTypeForExtension = env->GetStaticMethodID(jmethods.cls,
            "wellKnownMimeTypeForExtension", "(Ljava/lang/String;)Ljava/lang/String;");
    jmethods.mimeTypeFromFile = env->GetStaticMethodID(jmethods.cls,
            "mimeTypeFromFile", "(Ljava/lang/String;)Ljava/lang/String;");
}

WebMimeRegistryImpl::~WebMimeRegistryImpl() {}

WebMimeRegistry::SupportsType WebMimeRegistryImpl::supportsMIMEType(const WebString& mimeType) {
    JNIScope jniScope;
    JNIEnv* env = jniScope.getEnv();
    jstring jMimeType = JniUtils::toJavaString(env, mimeType.utf8());
    bool supports = env->CallStaticBooleanMethod(jmethods.cls, jmethods.supportsMIMEType, jMimeType);
    return supports ? WebMimeRegistry::IsSupported : WebMimeRegistry::IsNotSupported;
}

WebMimeRegistry::SupportsType WebMimeRegistryImpl::supportsImageMIMEType(const WebString& mimeType) {
    JNIScope jniScope;
    JNIEnv* env = jniScope.getEnv();
    jstring jMimeType = JniUtils::toJavaString(env, mimeType.utf8());
    bool supports = env->CallStaticBooleanMethod(jmethods.cls, jmethods.supportsImageMIMEType, jMimeType);
    return supports ? WebMimeRegistry::IsSupported : WebMimeRegistry::IsNotSupported;
}

WebMimeRegistry::SupportsType WebMimeRegistryImpl::supportsImagePrefixedMIMEType(const WebString& mimeType) {
    JNIScope jniScope;
    JNIEnv* env = jniScope.getEnv();
    jstring jMimeType = JniUtils::toJavaString(env, mimeType.utf8());
    bool supports = env->CallStaticBooleanMethod(jmethods.cls, jmethods.supportsImagePrefixedMIMEType, jMimeType);
    return supports ? WebMimeRegistry::IsSupported : WebMimeRegistry::IsNotSupported;
}

WebMimeRegistry::SupportsType WebMimeRegistryImpl::supportsJavaScriptMIMEType(const WebString& mimeType) {
    JNIScope jniScope;
    JNIEnv* env = jniScope.getEnv();
    jstring jMimeType = JniUtils::toJavaString(env, mimeType.utf8());
    bool supports = env->CallStaticBooleanMethod(jmethods.cls, jmethods.supportsJavaScriptMIMEType, jMimeType);
    return supports ? WebMimeRegistry::IsSupported : WebMimeRegistry::IsNotSupported;
}

WebMimeRegistry::SupportsType WebMimeRegistryImpl::supportsNonImageMIMEType(const WebString& mimeType) {
    JNIScope jniScope;
    JNIEnv* env = jniScope.getEnv();
    jstring jMimeType = JniUtils::toJavaString(env, mimeType.utf8());
    bool supports = env->CallStaticBooleanMethod(jmethods.cls, jmethods.supportsNonImageMIMEType, jMimeType);
    return supports ? WebMimeRegistry::IsSupported : WebMimeRegistry::IsNotSupported;
}

WebMimeRegistry::SupportsType WebMimeRegistryImpl::supportsMediaMIMEType(
        const WebString& mimeType,
        const WebString& codecs,
        const WebString& keySystem)
{
    JNIScope jniScope;
    JNIEnv* env = jniScope.getEnv();
    jstring jMimeType = JniUtils::toJavaString(env, mimeType.utf8());
    jstring jCodecs = JniUtils::toJavaString(env, codecs.utf8());
    jstring jKeySystem = JniUtils::toJavaString(env, keySystem.utf8());
    bool supports = env->CallStaticBooleanMethod(jmethods.cls, jmethods.supportsMediaMIMEType, jMimeType, jCodecs, jKeySystem);
    return supports ? WebMimeRegistry::IsSupported : WebMimeRegistry::IsNotSupported;
}

bool WebMimeRegistryImpl::supportsMediaSourceMIMEType(const WebString& mimeType, const WebString& codecs) {
    JNIScope jniScope;
    JNIEnv* env = jniScope.getEnv();
    jstring jMimeType = JniUtils::toJavaString(env, mimeType.utf8());
    jstring jCodecs = JniUtils::toJavaString(env, codecs.utf8());
    return env->CallStaticBooleanMethod(jmethods.cls, jmethods.supportsMediaSourceMIMEType, jMimeType, jCodecs);
}

WebString WebMimeRegistryImpl::mimeTypeForExtension(const WebString& extension) {
    JNIScope jniScope;
    JNIEnv* env = jniScope.getEnv();
    jstring jExtension = JniUtils::toJavaString(env, extension.utf8());
    jstring jMimeType = (jstring) env->CallStaticObjectMethod(jmethods.cls, jmethods.mimeTypeForExtension, jExtension);
    return WebString::fromUTF8(JniUtils::toUTF8String(env, jMimeType));
}

WebString WebMimeRegistryImpl::wellKnownMimeTypeForExtension(const WebString& extension) {
    JNIScope jniScope;
    JNIEnv* env = jniScope.getEnv();
    jstring jExtension = JniUtils::toJavaString(env, extension.utf8());
    jstring jMimeType = (jstring) env->CallStaticObjectMethod(jmethods.cls, jmethods.wellKnownMimeTypeForExtension, jExtension);
    return WebString::fromUTF8(JniUtils::toUTF8String(env, jMimeType));
}

WebString WebMimeRegistryImpl::mimeTypeFromFile(const WebString& filePath) {
    JNIScope jniScope;
    JNIEnv* env = jniScope.getEnv();
    jstring jFilePath = JniUtils::toJavaString(env, filePath.utf8());
    jstring jMimeType = (jstring) env->CallStaticObjectMethod(jmethods.cls, jmethods.mimeTypeFromFile, jFilePath);
    return WebString::fromUTF8(JniUtils::toUTF8String(env, jMimeType));
}

}

