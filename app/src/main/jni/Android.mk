INITIAL_LOCAL_PATH := $(call my-dir)
include $(INITIAL_LOCAL_PATH)/../../../../typoweb/src/main/jni/module.mk

LOCAL_PATH := $(INITIAL_LOCAL_PATH)

include $(CLEAR_VARS)
LOCAL_MODULE := libperfectreader
LOCAL_STATIC_LIBRARIES := libtypoweb
LOCAL_SRC_FILES := src/EntryPoint.cpp

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

LOCAL_LDLIBS += -lGLESv2 -lEGL -llog -lc -ldl -lm -flto

include $(BUILD_SHARED_LIBRARY)
