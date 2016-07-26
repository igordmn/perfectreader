#pragma ide diagnostic ignored "UnusedImportStatement"

#include <string>
#include <ft2build.h>
#include FT_FREETYPE_H
#include FT_CACHE_H
#include FT_OUTLINE_H
#include "../util/JniUtils.h"
#include "../paint/PixelBuffer.h"
#include "../paint/PaintUtils.h"
#include "FTErrors.h"
#include "FontFaceID.h"
#include "FontConfig.h"
#include "FontCache.h"

using namespace std;
using namespace dmi;
using namespace paintUtils;

namespace {
    class TextLibrary {
    public:
        FT_Library library;
        FontCache *fontCache;

        TextLibrary(uint16_t cacheMaxFaces, uint16_t cacheMaxSizes, uint32_t cacheMaxBytes) {
            FT_CHECK(FT_Init_FreeType(&library));
            fontCache = new FontCache(library, cacheMaxFaces, cacheMaxSizes, cacheMaxBytes);
        }

        ~TextLibrary() {
            delete fontCache;
            FT_CHECK(FT_Done_FreeType(library));
        }
    };
}

extern "C" JNIEXPORT jlong JNICALL
Java_com_dmi_util_android_graphics_TextLibrary_nativeNewTextLibrary(
        JNIEnv *, jobject, jint cacheMaxFaces, jint cacheMaxSizes, jint cacheMaxBytes
) {
    return (jlong) new TextLibrary((uint16_t) cacheMaxFaces, (uint16_t) cacheMaxSizes, (uint32_t) cacheMaxBytes);
}

extern "C" JNIEXPORT void JNICALL
Java_com_dmi_util_android_graphics_TextLibrary_nativeDestroyTextLibrary(JNIEnv *, jobject, jlong libraryPtr) {
    delete (TextLibrary *) libraryPtr;
}


extern "C" JNIEXPORT void JNICALL
Java_com_dmi_util_android_graphics_TextLibrary_nativeGetGlyphIndices(
        JNIEnv *env, jobject, jlong libraryPtr, jlong facePathPtr, jcharArray jChars, jintArray jIndices
) {
    TextLibrary &library = *((TextLibrary *) libraryPtr);
    const FontFaceID *faceID = (const FontFaceID *) facePathPtr;

    FontCache &cache = *library.fontCache;
    FT_Face face = cache.getFace(faceID);

    int32_t len = env->GetArrayLength(jChars);

    jchar *chars = env->GetCharArrayElements(jChars, nullptr);

    jint *indices = new jint[len];
    for (int32_t i = 0; i < len; i++) {
        indices[i] = FT_Get_Char_Index(face, chars[i]);
    }
    env->SetIntArrayRegion(jIndices, 0, len, indices);

    env->ReleaseCharArrayElements(jChars, chars, 0);
    delete[] indices;
}

extern "C" JNIEXPORT void JNICALL
Java_com_dmi_util_android_graphics_TextLibrary_nativeRenderGlyphs(
        JNIEnv *env, jobject,
        jlong libraryPtr, jintArray jGlyphIndices, jfloatArray jCoordinates,
        jlong fontConfigPtr, jlong pixelBufferPtr
) {
    TextLibrary &library = *((TextLibrary *) libraryPtr);
    FontConfig *fontConfig = (FontConfig *) fontConfigPtr;
    PixelBuffer &buffer = *((PixelBuffer *) pixelBufferPtr);

    jint size = env->GetArrayLength(jGlyphIndices);
    jint *glyphIndices = env->GetIntArrayElements(jGlyphIndices, nullptr);
    jfloat *coordinates = env->GetFloatArrayElements(jCoordinates, nullptr);

    FontCache &cache = *library.fontCache;

    uint32_t i = 0, c = 0;

    while (i < size) {
        float x = (float) coordinates[c++];
        float y = (float) coordinates[c++];
        uint32_t index = (uint32_t) glyphIndices[i++];

        const GlyphBitmap &glyphBitmap = cache.getGlyphBitmap(fontConfig, index, 0);
        const AlphaBuffer &glyphBuffer = glyphBitmap.buffer;
        copyPixels(buffer, glyphBuffer, (int16_t) x, (int16_t) y, fontConfig->color);
    }

    env->ReleaseIntArrayElements(jGlyphIndices, glyphIndices, 0);
    env->ReleaseFloatArrayElements(jCoordinates, coordinates, 0);
}