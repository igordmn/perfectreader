LOCAL_PATH := $(call my-dir)


include $(CLEAR_VARS)
LOCAL_MODULE := libchromiumtypo
LOCAL_SRC_FILES := $(LOCAL_PATH)/../../../third_party/chromium-typo/lib/$(TARGET_ARCH_ABI)/libchromiumtypo.a
LOCAL_EXPORT_C_INCLUDES += \
    $(LOCAL_PATH)/../../../third_party/chromium-typo/include \
    $(LOCAL_PATH)/../../../third_party/chromium-typo/include/skia/config \
    $(LOCAL_PATH)/../../../third_party/chromium-typo/include/v8/include \
    $(LOCAL_PATH)/../../../third_party/chromium-typo/include/third_party/icu/source/common \
    $(LOCAL_PATH)/../../../third_party/chromium-typo/include/third_party/WebKit \
    $(LOCAL_PATH)/../../../third_party/chromium-typo/include/third_party/skia/include/core \
    $(LOCAL_PATH)/../../../third_party/chromium-typo/include/third_party/skia/include/gpu \
    $(LOCAL_PATH)/../../../third_party/chromium-typo/include/third_party/skia/include/utils \
    $(LOCAL_PATH)/../../../third_party/chromium-typo/include/third_party/skia/src/gpu \
    $(NULL)
include $(PREBUILT_STATIC_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE := libtypoweb
LOCAL_SRC_FILES := \
    src/extensions/TypoHangingPunctuationImpl.cpp \
    src/extensions/TypoHyphenatorImpl.cpp \
    src/extensions/WordHyphenator.cpp \
    src/skia/GrGLCreateNativeInterface_android.cpp \
    src/util/JniUtils.cpp \
    src/util/StringUtils.cpp \
    src/util/UriUtils.cpp \
    src/BlinkPlatformImpl.cpp \
    src/BlinkResourceLoader.cpp \
    src/DataURL.cpp \
    src/RenderContext.cpp \
    src/TypoWeb.cpp \
    src/TypoWebLibrary.cpp \
    src/WebMimeRegistryImpl.cpp \
    src/WebSchedulerImpl.cpp \
    src/WebThreadImpl.cpp \
    src/WebURLLoaderImpl.cpp \
    src/TypoWebRegisterJni.cpp \
    $(NULL)
LOCAL_CFLAGS += \
    -DU_USING_ICU_NAMESPACE=0 -DU_ENABLE_DYLOAD=0 -DU_STATIC_IMPLEMENTATION \
    -DSK_SUPPORT_GPU=1 -DSK_LEGACY_DRAWPICTURECALLBACK -DSK_SUPPORT_LEGACY_GET_PIXELS_ENUM \
    -DSK_BUILD_FOR_ANDROID -DANDROID -DNDEBUG \
    $(NULL)
LOCAL_STATIC_LIBRARIES := libchromiumtypo
LOCAL_EXPORT_C_INCLUDES += \
    $(LOCAL_PATH)/src \
    $(NULL)
include $(BUILD_STATIC_LIBRARY)
