#pragma ide diagnostic ignored "UnusedImportStatement"

#include <string>
#include <ft2build.h>
#include FT_FREETYPE_H
#include FT_CACHE_H
#include "../util/JniUtils.h"
#include "../paint/PaintBuffer.h"
#include "../paint/PaintUtils.h"
#include "FTErrors.h"
#include "FontFaceID.h"

using namespace std;
using namespace dmi;
using namespace paintUtils;

namespace {
    class TextLibrary {
    public:
        FT_Library library;
        FTC_Manager manager;
        FTC_ImageCache imageCache;
        FTC_SBitCache sBitCache;

        static FT_Error FaceRequester(FTC_FaceID ftFaceID, FT_Library library, void *reqData, FT_Face *face) {
            FontFaceID *faceID = (FontFaceID *) ftFaceID;
            FT_CHECK(FT_New_Face(library, faceID->filePath.c_str(), 0, face));
            return 0;
        }

        TextLibrary(uint16_t cacheMaxFaces, uint16_t cacheMaxSizes, uint16_t cacheMaxBytes) {
            FT_CHECK(FT_Init_FreeType(&library));
            FT_CHECK(FTC_Manager_New(library, cacheMaxFaces, cacheMaxSizes, cacheMaxBytes, FaceRequester, 0, &manager));
            FT_CHECK(FTC_ImageCache_New(manager, &imageCache));
            FT_CHECK(FTC_SBitCache_New(manager, &sBitCache));
        }

        ~TextLibrary() {
            FTC_Manager_Done(manager);
            FT_CHECK(FT_Done_FreeType(library));
        }
    };
}

extern "C" JNIEXPORT jlong JNICALL
Java_com_dmi_util_android_graphics_TextLibrary_nativeNewTextLibrary(
        JNIEnv *, jobject, jint cacheMaxFaces, jint cacheMaxSizes, jint cacheMaxBytes
) {
    return (jlong) new TextLibrary((uint16_t) cacheMaxFaces, (uint16_t) cacheMaxSizes, (uint16_t) cacheMaxBytes);
}

extern "C" JNIEXPORT void JNICALL
Java_com_dmi_util_android_graphics_TextLibrary_nativeDestroyTextLibrary(JNIEnv *env, jobject instance, jlong libraryPtr) {
    delete (TextLibrary *) libraryPtr;
}


extern "C" JNIEXPORT void JNICALL
Java_com_dmi_util_android_graphics_TextLibrary_nativeGetGlyphIndices(
        JNIEnv *env, jobject, jlong libraryPtr, jlong facePathPtr, jcharArray jChars, jintArray jIndices
) {
    TextLibrary &library = *((TextLibrary *) libraryPtr);
    FontFaceID *facePath = (FontFaceID *) facePathPtr;

    FTC_Manager manager = library.manager;
    FT_Face face;
    FT_CHECK(FTC_Manager_LookupFace(manager, facePath, &face));

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
        jlong faceIDPtr, jfloat sizeInPixels, jint color, jlong paintBufferPtr
) {
    TextLibrary &library = *((TextLibrary *) libraryPtr);
    FontFaceID *faceID = (FontFaceID *) faceIDPtr;
    PaintBuffer &buffer = *((PaintBuffer *) paintBufferPtr);

    FTC_SBitCache sBitCache = library.sBitCache;

    jint size = env->GetArrayLength(jGlyphIndices);
    jint *glyphIndices = env->GetIntArrayElements(jGlyphIndices, nullptr);
    jfloat *coordinates = env->GetFloatArrayElements(jCoordinates, nullptr);

    FTC_ScalerRec scaler;
    scaler.face_id = faceID;
    scaler.pixel = 0;
    scaler.width = (uint32_t) (sizeInPixels * 64);
    scaler.height = (uint32_t) (sizeInPixels * 64);
    scaler.x_res = 0;
    scaler.y_res = 0;

    uint32_t i = 0, c = 0;

    FTC_SBit sBit;
    while (i < size) {
        uint16_t glyphIndex = (uint16_t) glyphIndices[i++];
        float x = (float) coordinates[c++];
        float y = (float) coordinates[c++];
        FT_CHECK(FTC_SBitCache_LookupScaler(sBitCache, &scaler, FT_LOAD_DEFAULT, glyphIndex, &sBit, nullptr));
        copyPixels(buffer, sBit->buffer, sBit->width, sBit->height, (uint16_t) sBit->pitch, (int16_t) x, (int16_t) y, (uint32_t) color);
    }

    env->ReleaseIntArrayElements(jGlyphIndices, glyphIndices, 0);
    env->ReleaseFloatArrayElements(jCoordinates, coordinates, 0);
}