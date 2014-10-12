#include <jni.h>
#include <assert.h>
#include <ft2build.h>
#include FT_FREETYPE_H
#include FT_GLYPH_H
#include FT_CACHE_H
#include <hb.h>
#include <hb-ft.h>
#include <hb-icu.h>

extern "C" JNIEXPORT jlong JNICALL Java_com_dmi_perfectreader_book_format_shape_HarfbuzzShaper_nativeInitHbFont
        (JNIEnv *, jclass, jlong ftcManagerRef, jlong ftcScalerRef) {
    FTC_Manager ftcManager = (FTC_Manager) ftcManagerRef;
    FTC_Scaler ftcScaler = (FTC_Scaler) ftcScalerRef;
    FT_Size ftSize;
    assert(!FTC_Manager_LookupSize(ftcManager, ftcScaler, &ftSize));
    FT_Face ftFace = ftSize->face;

    return (jlong) hb_ft_font_create(ftFace, NULL);
}

extern "C" JNIEXPORT jlong JNICALL Java_com_dmi_perfectreader_book_format_shape_HarfbuzzShaper_nativeInitHbBuffer
        (JNIEnv *env, jclass) {
    hb_buffer_t *hbBuffer = hb_buffer_create();
    hb_buffer_set_unicode_funcs(hbBuffer, hb_icu_get_unicode_funcs());
    return (jlong) hbBuffer;
}

extern "C" JNIEXPORT void JNICALL Java_com_dmi_perfectreader_book_format_shape_HarfbuzzShaper_nativeDestroyHbFont
        (JNIEnv *env, jclass, jlong hbFontRef) {
    hb_font_t *hbFont = (hb_font_t *) hbFontRef;
    hb_font_destroy(hbFont);
}

extern "C" JNIEXPORT void JNICALL Java_com_dmi_perfectreader_book_format_shape_HarfbuzzShaper_nativeDestroyHbBuffer
        (JNIEnv *env, jclass, jlong hbFontRef) {
    hb_buffer_t *hbBuffer = (hb_buffer_t *) hbFontRef;
    hb_buffer_clear_contents(hbBuffer);
    hb_buffer_destroy(hbBuffer);
}

extern "C" JNIEXPORT void JNICALL Java_com_dmi_perfectreader_book_format_shape_HarfbuzzShaper_nativeShape
        (JNIEnv *env, jclass, jlong hbBufferRef, jlong hbFontRef, jcharArray jCharacters, jint offset, jint length) {
    jchar characters[length];
    env->GetCharArrayRegion(jCharacters, offset, length, characters);

    hb_buffer_t *hbBuffer = (hb_buffer_t *) hbBufferRef;
    hb_font_t *hbFont = (hb_font_t *) hbFontRef;
    hb_buffer_clear_contents(hbBuffer);
    hb_buffer_set_direction(hbBuffer, HB_DIRECTION_LTR);
    hb_buffer_set_script(hbBuffer, HB_SCRIPT_LATIN);
    hb_buffer_set_language(hbBuffer, hb_language_from_string("en", strlen("en")));
    hb_buffer_add_utf16(hbBuffer, characters, length, offset, length);
    hb_shape(hbFont, hbBuffer, NULL, 0);
}

extern "C" JNIEXPORT jintArray JNICALL Java_com_dmi_perfectreader_book_format_shape_HarfbuzzShaper_nativeCodepoints
        (JNIEnv *env, jclass, jlong hbBufferRef) {
    hb_buffer_t *hbBuffer = (hb_buffer_t *) hbBufferRef;

    unsigned int glyphCount;
    hb_glyph_info_t *glyphInfo = hb_buffer_get_glyph_infos(hbBuffer, &glyphCount);

    jint codepoints[glyphCount];

    for (int i = 0; i < glyphCount; i++) {
        codepoints[i] = glyphInfo[i].codepoint;
    }

    jintArray jCodepoints = env->NewIntArray(glyphCount);
    env->SetIntArrayRegion(jCodepoints, 0, glyphCount, codepoints);
    return jCodepoints;
}

extern "C" JNIEXPORT jintArray JNICALL Java_com_dmi_perfectreader_book_format_shape_HarfbuzzShaper_nativeClusters
        (JNIEnv *env, jclass, jlong hbBufferRef) {
    hb_buffer_t *hbBuffer = (hb_buffer_t *) hbBufferRef;

    unsigned int glyphCount;
    hb_glyph_info_t *glyphInfo = hb_buffer_get_glyph_infos(hbBuffer, &glyphCount);

    jint clusters[glyphCount];

    for (int i = 0; i < glyphCount; i++) {
        clusters[i] = glyphInfo[i].cluster;
    }

    jintArray jClusters = env->NewIntArray(glyphCount);
    env->SetIntArrayRegion(jClusters, 0, glyphCount, clusters);
    return jClusters;
}

extern "C" JNIEXPORT jfloatArray JNICALL Java_com_dmi_perfectreader_book_format_shape_HarfbuzzShaper_nativeAdvanceX
        (JNIEnv *env, jclass, jlong hbBufferRef) {
    hb_buffer_t *hbBuffer = (hb_buffer_t *) hbBufferRef;

    unsigned int glyphCount;
    hb_glyph_position_t *glyphPos = hb_buffer_get_glyph_positions(hbBuffer, &glyphCount);

    jfloat advances[glyphCount];

    for (int i = 0; i < glyphCount; i++) {
        advances[i] = glyphPos[i].x_advance / 64.0F;
    }

    jfloatArray jAdvances = env->NewFloatArray(glyphCount);
    env->SetFloatArrayRegion(jAdvances, 0, glyphCount, advances);
    return jAdvances;
}

extern "C" JNIEXPORT jfloatArray JNICALL Java_com_dmi_perfectreader_book_format_shape_HarfbuzzShaper_nativeAdvanceY
        (JNIEnv *env, jclass, jlong hbBufferRef) {
    hb_buffer_t *hbBuffer = (hb_buffer_t *) hbBufferRef;

    unsigned int glyphCount;
    hb_glyph_position_t *glyphPos = hb_buffer_get_glyph_positions(hbBuffer, &glyphCount);

    jfloat advances[glyphCount];

    for (int i = 0; i < glyphCount; i++) {
        advances[i] = glyphPos[i].y_advance / 64.0F;
    }

    jfloatArray jAdvances = env->NewFloatArray(glyphCount);
    env->SetFloatArrayRegion(jAdvances, 0, glyphCount, advances);
    return jAdvances;
}

extern "C" JNIEXPORT jfloatArray JNICALL Java_com_dmi_perfectreader_book_format_shape_HarfbuzzShaper_nativeWidths
        (JNIEnv *env, jclass, jlong hbBufferRef, jlong hbFontRef) {
    hb_buffer_t *hbBuffer = (hb_buffer_t *) hbBufferRef;
    hb_font_t *hbFont = (hb_font_t *) hbFontRef;

    unsigned int glyphCount;
    hb_glyph_position_t *glyphPos = hb_buffer_get_glyph_positions(hbBuffer, &glyphCount);

    jfloat widths[glyphCount];
    for (int i = 0; i < glyphCount; i++) {
        // todo заменить advance на width
        widths[i] = glyphPos[i].x_advance / 64.0F;
    }

    jfloatArray jWidths = env->NewFloatArray(glyphCount);
    env->SetFloatArrayRegion(jWidths, 0, glyphCount, widths);
    return jWidths;
}

extern "C" JNIEXPORT jfloatArray JNICALL Java_com_dmi_perfectreader_book_format_shape_HarfbuzzShaper_nativeFontMetrics
        (JNIEnv *env, jclass, jlong ftcManagerRef, jlong ftcScalerRef) {
    FTC_Manager ftcManager = (FTC_Manager) ftcManagerRef;
    FTC_Scaler ftcScaler = (FTC_Scaler) ftcScalerRef;
    FT_Size ftSize;
    assert(!FTC_Manager_LookupSize(ftcManager, ftcScaler, &ftSize));
    FT_Face ftFace = ftSize->face;

    FT_Size_Metrics scaledMetrics = ftFace->size->metrics;

    jfloat fontMetrics[3];

    fontMetrics[0] = scaledMetrics.ascender / 64.0F;
    fontMetrics[1] = scaledMetrics.descender / 64.0F;
    fontMetrics[2] = (scaledMetrics.height - scaledMetrics.ascender + scaledMetrics.descender) / 64.0F;

    jfloatArray jfontMetrics = env->NewFloatArray(3);
    env->SetFloatArrayRegion(jfontMetrics, 0, 3, fontMetrics);
    return jfontMetrics;
}
