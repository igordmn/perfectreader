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
    src/EntryPoint.cpp \
    src/RenderContext.cpp \
    src/TypoWeb.cpp \
    src/TypoWebLibrary.cpp \
    src/WebMimeRegistryImpl.cpp \
    src/WebSchedulerImpl.cpp \
    src/WebThreadImpl.cpp \
    src/WebURLLoaderImpl.cpp \
    $(NULL)

LOCAL_CFLAGS += -DV8_DEPRECATION_WARNINGS -D_FILE_OFFSET_BITS=64 -DNO_TCMALLOC \
    -DDISABLE_NACL -DCHROMIUM_BUILD -DUSE_LIBJPEG_TURBO=1 -DENABLE_WEBRTC=1 \
    -DUSE_PROPRIETARY_CODECS -DENABLE_BROWSER_CDMS \
    -DENABLE_CONFIGURATION_POLICY -DENABLE_NOTIFICATIONS \
    -DDISCARDABLE_MEMORY_ALWAYS_SUPPORTED_NATIVELY \
    -DSYSTEM_NATIVELY_SIGNALS_MEMORY_PRESSURE -DDONT_EMBED_BUILD_METADATA \
    -DENABLE_AUTOFILL_DIALOG=1 -DCLD_VERSION=1 -DENABLE_SUPERVISED_USERS=1 \
    -DVIDEO_HOLE=1 -DU_USING_ICU_NAMESPACE=0 -DU_ENABLE_DYLOAD=0 \
    -DU_STATIC_IMPLEMENTATION -DCHROME_PNG_WRITE_SUPPORT -DPNG_USER_CONFIG \
    -DCHROME_PNG_READ_PACK_SUPPORT -DSK_SUPPORT_GPU=1 \
    -DSK_LEGACY_DRAWPICTURECALLBACK -DSK_SUPPORT_LEGACY_GET_PIXELS_ENUM \
    -DSK_BUILD_FOR_ANDROID -DUSE_LIBPCI=1 -DUSE_OPENSSL=1 \
    -DUSE_OPENSSL_CERTS=1 -DANDROID -D__GNU_SOURCE=1 -DUSE_STLPORT=1 \
    -D_STLP_USE_PTR_SPECIALIZATIONS=1 '-DCHROME_BUILD_ID=""' \
    -DHAVE_SYS_UIO_H -DNDEBUG -DNO_UNWIND_TABLES -DNVALGRIND \
    -DDYNAMIC_ANNOTATIONS_ENABLED=0

LOCAL_CFLAGS += -fno-strict-aliasing \
    -Wno-unused-parameter -Wno-missing-field-initializers \
    -fvisibility=hidden -pipe -fPIC -Wno-unused-local-typedefs -Wno-format \
    -ffunction-sections -funwind-tables -g \
    -fno-short-enums -finline-limit=64 \
    -Os -fno-ident -fdata-sections -ffunction-sections

LOCAL_CPPFLAGS += -fno-exceptions -fno-rtti -fno-threadsafe-statics \
    -fvisibility-inlines-hidden -Wno-deprecated -std=gnu++11 \
    -Wno-narrowing -Wno-literal-suffix

LOCAL_LDFLAGS += -Wl,-z,now -Wl,-z,relro -Wl,--fatal-warnings -Wl,-z,defs \
    -Wl,-z,noexecstack -fPIC -Wl,--no-fatal-warnings \
    -Wl,--no-undefined \
    -Wl,-shared,-Bsymbolic \
    -Wl,-O1 -Wl,--as-needed -Wl,--gc-sections -Wl,--warn-shared-textrel

ifeq ($(TARGET_ARCH_ABI), armeabi-v7a)
    LOCAL_LDFLAGS += -Wl,--icf=all
endif
ifeq ($(TARGET_ARCH_ABI), x86)
    LOCAL_LDFLAGS += -Wl,--icf=all
endif
ifeq ($(TARGET_ARCH_ABI), x86_64)
    LOCAL_LDFLAGS += -Wl,--icf=all
endif

LOCAL_LDLIBS += -lGLESv2 -lEGL -llog -lc -ldl -lm
LOCAL_STATIC_LIBRARIES := libchromiumtypo
include $(BUILD_SHARED_LIBRARY)
