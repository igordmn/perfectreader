#pragma once

#include <android/log.h>
#include <string>

#define LOG(level, text, ...) __android_log_print(level, "utilAndroid", text, ## __VA_ARGS__);
#define LOGD(text, ...) __android_log_print(ANDROID_LOG_DEBUG, "utilAndroid", text, ## __VA_ARGS__);
#define LOGI(text, ...) __android_log_print(ANDROID_LOG_INFO, "utilAndroid", text, ## __VA_ARGS__);
#define LOGW(text, ...) __android_log_print(ANDROID_LOG_WARN, "utilAndroid", text, ## __VA_ARGS__);
#define LOGE(text, ...) __android_log_print(ANDROID_LOG_ERROR, "utilAndroid", text, ## __VA_ARGS__);

namespace dmi {
    void abortWithLog(const char *file, int line, int errorCode = 0, const std::string &message = "", ...);

    template<typename ... Args>
    std::string formatErrorMessage(const std::string &message, Args ... args) {
        char buffer[512];
        int size = snprintf(buffer, 512, message.c_str(), args ...);
        return std::string(buffer, buffer + size);
    }
}

#define CHECK(condition)  \
    if (!(condition)) {   \
        dmi::abortWithLog(__FILE__, __LINE__); \
    }

#define CHECKE(callAndGetErrCode)  { \
    int errCode = callAndGetErrCode; \
    if (errCode != 0) {   \
        dmi::abortWithLog(__FILE__, __LINE__, errCode); \
    } \
}

#define CHECKM(condition, message, ...)  \
    if (!(condition)) {   \
        dmi::abortWithLog(__FILE__, __LINE__, 0, message, ## __VA_ARGS__); \
    }