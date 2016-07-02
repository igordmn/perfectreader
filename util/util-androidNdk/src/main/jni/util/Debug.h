#pragma once

#include <android/log.h>

#define LOG(level, text, ...) __android_log_print(level, "typoweb", text, ## __VA_ARGS__);
#define LOGD(text, ...) __android_log_print(ANDROID_LOG_DEBUG, "typoweb", text, ## __VA_ARGS__);
#define LOGI(text, ...) __android_log_print(ANDROID_LOG_INFO, "typoweb", text, ## __VA_ARGS__);
#define LOGW(text, ...) __android_log_print(ANDROID_LOG_WARN, "typoweb", text, ## __VA_ARGS__);
#define LOGE(text, ...) __android_log_print(ANDROID_LOG_ERROR, "typoweb", text, ## __VA_ARGS__);

#define CHECK(condition)  \
    if (!(condition)) {   \
        __android_log_print(ANDROID_LOG_ERROR, "typoweb", "Check failed. %s:%d ", __FILE__, __LINE__);   \
    }