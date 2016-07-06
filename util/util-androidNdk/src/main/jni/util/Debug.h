#pragma once

#include <android/log.h>

#define LOG(level, text, ...) __android_log_print(level, "typoweb", text, ## __VA_ARGS__);
#define LOGD(text, ...) __android_log_print(ANDROID_LOG_DEBUG, "typoweb", text, ## __VA_ARGS__);
#define LOGI(text, ...) __android_log_print(ANDROID_LOG_INFO, "typoweb", text, ## __VA_ARGS__);
#define LOGW(text, ...) __android_log_print(ANDROID_LOG_WARN, "typoweb", text, ## __VA_ARGS__);
#define LOGE(text, ...) __android_log_print(ANDROID_LOG_ERROR, "typoweb", text, ## __VA_ARGS__);

#define __FILENAME__ (strrchr(__FILE__, '/') ? strrchr(__FILE__, '/') + 1 : strrchr(__FILE__, '\\') ? strrchr(__FILE__, '\\') + 1 : __FILE__)

#define CHECK(condition)  \
    if (!(condition)) {   \
        __android_log_print(ANDROID_LOG_ERROR, "utilAndroid", "Check failed. %s:%d ", __FILENAME__, __LINE__);   \
        abort(); \
    }

#define CHECKM(condition, message)  \
    if (!(condition)) {   \
        __android_log_print(ANDROID_LOG_ERROR, "utilAndroid", "Check failed: %s. %s:%d ", message, __FILENAME__, __LINE__);   \
        abort(); \
    }