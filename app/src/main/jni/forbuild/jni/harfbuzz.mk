LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE:= libharfbuzz
LOCAL_CPP_EXTENSION := .cc
LOCAL_SRC_FILES:= \
    ../repo/harfbuzz/src/hb-blob.cc \
    ../repo/harfbuzz/src/hb-buffer-serialize.cc \
    ../repo/harfbuzz/src/hb-buffer.cc \
    ../repo/harfbuzz/src/hb-common.cc \
    ../repo/harfbuzz/src/hb-face.cc \
    ../repo/harfbuzz/src/hb-font.cc \
    ../repo/harfbuzz/src/hb-ot-tag.cc \
    ../repo/harfbuzz/src/hb-set.cc \
    ../repo/harfbuzz/src/hb-shape.cc \
    ../repo/harfbuzz/src/hb-shape-plan.cc \
    ../repo/harfbuzz/src/hb-shaper.cc \
    ../repo/harfbuzz/src/hb-unicode.cc \
    ../repo/harfbuzz/src/hb-warning.cc \
    ../repo/harfbuzz/src/hb-ot-font.cc \
    ../repo/harfbuzz/src/hb-ot-layout.cc \
    ../repo/harfbuzz/src/hb-ot-map.cc \
    ../repo/harfbuzz/src/hb-ot-shape.cc \
    ../repo/harfbuzz/src/hb-ot-shape-complex-arabic.cc \
    ../repo/harfbuzz/src/hb-ot-shape-complex-default.cc \
    ../repo/harfbuzz/src/hb-ot-shape-complex-hangul.cc \
    ../repo/harfbuzz/src/hb-ot-shape-complex-hebrew.cc \
    ../repo/harfbuzz/src/hb-ot-shape-complex-indic.cc \
    ../repo/harfbuzz/src/hb-ot-shape-complex-indic-table.cc \
    ../repo/harfbuzz/src/hb-ot-shape-complex-myanmar.cc \
    ../repo/harfbuzz/src/hb-ot-shape-complex-sea.cc \
    ../repo/harfbuzz/src/hb-ot-shape-complex-thai.cc \
    ../repo/harfbuzz/src/hb-ot-shape-complex-tibetan.cc \
    ../repo/harfbuzz/src/hb-ot-shape-normalize.cc \
    ../repo/harfbuzz/src/hb-ot-shape-fallback.cc \
    ../repo/harfbuzz/src/hb-ft.cc \
    ../repo/harfbuzz/src/hb-icu.cc \
    $(NULL)
LOCAL_C_INCLUDES += \
    $(LOCAL_PATH)/../repo/harfbuzz/src \
    $(NULL)
LOCAL_EXPORT_C_INCLUDES += \
    $(LOCAL_PATH)/../repo/harfbuzz/src \
    $(NULL)
LOCAL_ARM_MODE := arm
LOCAL_CFLAGS += -DHB_NO_MT -DHAVE_OT -DHAVE_ICU -DHAVE_ICU_BUILTIN -O2
LOCAL_SHARED_LIBRARIES := libfreetype libicu
include $(BUILD_SHARED_LIBRARY)
