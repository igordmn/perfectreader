APP_STL := stlport_static
APP_PLATFORM := android-15
APP_CFLAGS := -fno-strict-aliasing \
    -Wno-unused-parameter -Wno-missing-field-initializers \
    -fvisibility=hidden -pipe -fPIC -Wno-unused-local-typedefs -Wno-format \
    -ffunction-sections -funwind-tables -g \
    -fno-short-enums -finline-limit=64 \
    -Os -fno-ident -fdata-sections -ffunction-sections
APP_CPPFLAGS := -fno-exceptions -fno-rtti -fno-threadsafe-statics \
    -fvisibility-inlines-hidden -Wno-deprecated -std=gnu++11 \
    -Wno-narrowing -Wno-literal-suffix