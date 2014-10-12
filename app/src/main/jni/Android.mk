LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := libfreetype
LOCAL_SRC_FILES := libs/$(TARGET_ARCH_ABI)/libfreetype.so
LOCAL_EXPORT_C_INCLUDES += \
    $(LOCAL_PATH)/include/freetype \
    $(NULL)
include $(PREBUILT_SHARED_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE := libharfbuzz
LOCAL_SRC_FILES := libs/$(TARGET_ARCH_ABI)/libharfbuzz.so
LOCAL_EXPORT_C_INCLUDES += \
    $(LOCAL_PATH)/include/harfbuzz \
    $(NULL)
include $(PREBUILT_SHARED_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE := libicu
LOCAL_SRC_FILES := libs/$(TARGET_ARCH_ABI)/libicu.so
LOCAL_EXPORT_C_INCLUDES += \
    $(LOCAL_PATH)/include/icu/common \
    $(NULL)
include $(PREBUILT_SHARED_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE := libtext
LOCAL_SRC_FILES := \
    com_dmi_perfectreader_book_font_FreetypeLibrary.cpp \
    com_dmi_perfectreader_graphic_GLText.cpp \
    com_dmi_perfectreader_book_format_shape_HarfbuzzShaper.cpp \
    $(NULL)
LOCAL_ARM_MODE := arm
LOCAL_CFLAGS += -O2
LOCAL_LDLIBS := -llog -lGLESv2 -lEGL
LOCAL_CPP_FEATURES := rtti exceptions
LOCAL_SHARED_LIBRARIES := libfreetype libharfbuzz libicu libui
include $(BUILD_SHARED_LIBRARY)
