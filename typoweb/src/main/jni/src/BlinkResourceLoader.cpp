#include "BlinkResourceLoader.h"

#include <string>

using namespace std;
using namespace blink;

namespace {

struct JMethods {
    jclass cls;
    jmethodID loadResource;
};

JMethods jmethods;

}

namespace typo {

void BlinkResourceLoader::registerJni() {
    JNIScope jniScope;
    JNIEnv* env = jniScope.getEnv();
    jmethods.cls = (jclass) env->NewGlobalRef(env->FindClass("com/dmi/typoweb/BlinkResourceLoader"));
    jmethods.loadResource = env->GetStaticMethodID(jmethods.cls, "loadResource", "(Ljava/lang/String;)[B");
}

WebData BlinkResourceLoader::loadResource(const char* name) {
    JNIScope jniScope;
    JNIEnv* env = jniScope.getEnv();
    jstring jName = JniUtils::toJavaString(env, name);
    jbyteArray jData = (jbyteArray) env->CallStaticObjectMethod(jmethods.cls, jmethods.loadResource, jName);
    int dataLength = env->GetArrayLength(jData);
    char* data = new char[dataLength];
    env->GetByteArrayRegion(jData, 0, dataLength, (jbyte*) data);
    return WebData(data, dataLength);
}

}
