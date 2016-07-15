#include "Debug.h"

#include <android/log.h>
#include <string>
#include "JniUtils.h"

namespace dmi {
    void abortWithLog(const char *file, int line, const char *message, int errorCode) {
        const char *fileName =
                strrchr(file, '/') ? strrchr(file, '/') + 1 :
                strrchr(file, '\\') ? strrchr(file, '\\') + 1 :
                file;

        dmi::JNIScope scope;
        JNIEnv *env = scope.env();

        std::string javaStackTrace = dmi::jniUtils::getJavaStackTrace(env);

        if (message != 0) {
            __android_log_print(
                    ANDROID_LOG_ERROR, "utilAndroid", "Check failed: %s. %s:%d\n%s",
                    message, fileName, line, javaStackTrace.c_str());
        } else if (errorCode != 0) {
            __android_log_print(
                    ANDROID_LOG_ERROR, "utilAndroid", "Check failed. Error code: %d. %s:%d\n%s",
                    errorCode, fileName, line, javaStackTrace.c_str());
        } else {
            __android_log_print(
                    ANDROID_LOG_ERROR, "utilAndroid", "Check failed. %s:%d\n%s",
                    fileName, line, javaStackTrace.c_str()
            );
        }

        abort();
    }
}