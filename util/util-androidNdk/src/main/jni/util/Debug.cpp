#include "Debug.h"

#include <android/log.h>
#include <string>
#include <cstdlib>
#include "JniUtils.h"

namespace dmi {
    void abortWithLog(const char *file, int line, int errorCode, const std::string& message, ...) {
        const char *fileName =
                strrchr(file, '/') ? strrchr(file, '/') + 1 :
                strrchr(file, '\\') ? strrchr(file, '\\') + 1 :
                file;

        dmi::JNIScope scope;
        JNIEnv *env = scope.env();

        std::string javaStackTrace = dmi::jniUtils::getJavaStackTrace(env);

        if (message != "") {
            va_list args;
            va_start(args, message);
            std::string formattedMessage = formatErrorMessage(message, args);
            va_end (args);

            __android_log_print(
                    ANDROID_LOG_ERROR, "utilAndroid", "Check failed; %s. %s:%d\n%s",
                    formattedMessage.c_str(), fileName, line, javaStackTrace.c_str()
            );
        } else if (errorCode != 0) {
            __android_log_print(
                    ANDROID_LOG_ERROR, "utilAndroid", "Check failed; error code: %d. %s:%d\n%s",
                    errorCode, fileName, line, javaStackTrace.c_str()
            );
        } else {
            __android_log_print(
                    ANDROID_LOG_ERROR, "utilAndroid", "Check failed. %s:%d\n%s",
                    fileName, line, javaStackTrace.c_str()
            );
        }

        std::abort();
    }
}