LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE:= libfreetype
LOCAL_SRC_FILES := \
    ../repo/freetype/src/base/ftbase.c \
    ../repo/freetype/src/base/ftsystem.c \
    ../repo/freetype/src/base/ftinit.c \
    ../repo/freetype/src/base/ftbitmap.c \
    ../repo/freetype/src/base/ftglyph.c \
    ../repo/freetype/src/autofit/autofit.c \
    ../repo/freetype/src/smooth/smooth.c \
    ../repo/freetype/src/raster/raster.c \
    ../repo/freetype/src/truetype/truetype.c \
    ../repo/freetype/src/type1/type1.c \
    ../repo/freetype/src/type42/type42.c \
    ../repo/freetype/src/pfr/pfr.c \
    ../repo/freetype/src/cid/type1cid.c \
    ../repo/freetype/src/cff/cff.c \
    ../repo/freetype/src/pcf/pcf.c \
    ../repo/freetype/src/bdf/bdf.c \
    ../repo/freetype/src/sfnt/sfnt.c \
    ../repo/freetype/src/winfonts/winfnt.c \
    ../repo/freetype/src/gzip/ftgzip.c \
    ../repo/freetype/src/lzw/ftlzw.c \
    ../repo/freetype/src/psnames/psnames.c \
    ../repo/freetype/src/pshinter/pshinter.c \
    ../repo/freetype/src/pfr/pfrload.c \
    ../repo/freetype/src/psaux/psaux.c \
    ../repo/freetype/src/cache/ftcache.c \
    $(NULL)
LOCAL_C_INCLUDES += \
    $(LOCAL_PATH)/../repo/freetype/include \
    $(NULL)
LOCAL_EXPORT_C_INCLUDES += \
    $(LOCAL_PATH)/../repo/freetype/include \
    $(NULL)
LOCAL_ARM_MODE := arm
LOCAL_CFLAGS += -W -Wall -fPIC -DPIC -DDARWIN_NO_CARBON -DFT2_BUILD_LIBRARY -O2
LOCAL_SHARED_LIBRARIES += libpng libz
include $(BUILD_SHARED_LIBRARY)
