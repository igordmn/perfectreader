#pragma once

#include <android/log.h>

#define LOG(level, text, ...) __android_log_print(level, "utilAndroid", text, ## __VA_ARGS__);
#define LOGD(text, ...) __android_log_print(ANDROID_LOG_DEBUG, "utilAndroid", text, ## __VA_ARGS__);
#define LOGI(text, ...) __android_log_print(ANDROID_LOG_INFO, "utilAndroid", text, ## __VA_ARGS__);
#define LOGW(text, ...) __android_log_print(ANDROID_LOG_WARN, "utilAndroid", text, ## __VA_ARGS__);
#define LOGE(text, ...) __android_log_print(ANDROID_LOG_ERROR, "utilAndroid", text, ## __VA_ARGS__);

namespace dmi {
    void abortWithLog(const char *file, int line, const char *message = 0, int errorCode = 0);
}

#define CHECK(condition)  \
    if (!(condition)) {   \
        dmi::abortWithLog(__FILE__, __LINE__); \
    }

#define CHECKE(callAndGetErrCode)  { \
    int errCode = callAndGetErrCode; \
    if (errCode != 0) {   \
        dmi::abortWithLog(__FILE__, __LINE__, 0, errCode); \
    } \
};