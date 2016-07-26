#include "FontConfig.h"

#include "../util/JniUtils.h"
#include "../paint/PaintUtils.h"

using namespace dmi;

extern "C" JNIEXPORT jlong JNICALL
Java_com_dmi_util_android_graphics_FontConfig_nativeNewFontConfig(
        JNIEnv *, jobject,

        jlong faceIDPtr,
        jfloat sizeX,
        jfloat sizeY,

        jboolean hinting,
        jboolean forceAutoHinting,
        jboolean lightHinting,

        jfloat scaleX,
        jfloat scaleY,
        jfloat skewX,
        jfloat skewY,

        jboolean embolden,
        jfloat emboldenStrengthX,
        jfloat emboldenStrengthY,

        jboolean strokeInside,
        jboolean strokeOutside,
        jint strokeLineCapOrdinal,
        jint strokeLineJoinOrdinal,
        jfloat strokeMiterLimit,
        jfloat strokeRadius,

        jboolean antialias,
        jfloat gamma,
        jfloat blurRadius,
        jint colorARGB
) {
    FontFaceID *faceID = (FontFaceID *) faceIDPtr;

    FT_Stroker_LineCap strokeLineCap =
            strokeLineCapOrdinal == 0 ? FT_STROKER_LINECAP_BUTT :
            strokeLineCapOrdinal == 1 ? FT_STROKER_LINECAP_ROUND :
            FT_STROKER_LINECAP_SQUARE;

    FT_Stroker_LineJoin strokeLineJoin =
            strokeLineJoinOrdinal == 0 ? FT_STROKER_LINEJOIN_ROUND :
            strokeLineJoinOrdinal == 1 ? FT_STROKER_LINEJOIN_BEVEL :
            strokeLineJoinOrdinal == 2 ? FT_STROKER_LINEJOIN_MITER_VARIABLE :
            FT_STROKER_LINEJOIN_MITER_FIXED;

    uint32_t color = paintUtils::argb2abgr((uint32_t) colorARGB);

    return (jlong) new FontConfig(
            faceID,
            sizeX,
            sizeY,

            hinting,
            forceAutoHinting,
            lightHinting,

            scaleX,
            scaleY,
            skewX,
            skewY,

            embolden,
            emboldenStrengthX,
            emboldenStrengthY,

            strokeInside,
            strokeOutside,
            strokeLineCap,
            strokeLineJoin,
            strokeMiterLimit,
            strokeRadius,

            antialias,
            gamma,
            blurRadius,
            color
    );
}

extern "C" JNIEXPORT void JNICALL
Java_com_dmi_util_android_graphics_FontConfig_nativeDestroyFontConfig(JNIEnv *env, jobject instance, jlong ptr) {
    delete (FontConfig *) ptr;
}