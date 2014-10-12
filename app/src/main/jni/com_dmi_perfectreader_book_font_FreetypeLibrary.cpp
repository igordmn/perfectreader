#include <jni.h>
#include <string>
#include <assert.h>
#include <ft2build.h>
#include FT_FREETYPE_H
#include FT_GLYPH_H
#include FT_CACHE_H

using namespace std;

namespace FreetypeLibrary {
    struct MyFaceId {
        string name;
    };

    FT_Error FtcFaceRequester(FTC_FaceID faceID, FT_Library lib, void *reqData, FT_Face *face)  {
        MyFaceId *myFaceId = (MyFaceId *) faceID;
        string fontsPath((char *) reqData);
        string fullPath = fontsPath + "/" + myFaceId->name + ".ttf";
        assert(!FT_New_Face(lib, fullPath.c_str(), 0, face));
        return 0;
    }
}

extern "C" JNIEXPORT jlong JNICALL Java_com_dmi_perfectreader_book_font_FreetypeLibrary_nativeInitFtLibrary
        (JNIEnv *, jclass) {
    FT_Library ftLibrary;
    assert(!FT_Init_FreeType(&ftLibrary));
    return (jlong) ftLibrary;
}

extern "C" JNIEXPORT void JNICALL Java_com_dmi_perfectreader_book_font_FreetypeLibrary_nativeDoneFtLibrary
        (JNIEnv *env, jclass, jlong ftLibraryRef) {
    FT_Library ftLibrary = (FT_Library) ftLibraryRef;
    FT_Done_FreeType(ftLibrary);
}

extern "C" JNIEXPORT jlong JNICALL Java_com_dmi_perfectreader_book_font_FreetypeLibrary_nativeInitFtcManager
        (JNIEnv *env, jclass, jlong ftLibraryRef, jlong fontsPathRef) {
    FT_Library ftLibrary = (FT_Library) ftLibraryRef;
    char *fontsPath = (char *) fontsPathRef;

    FTC_Manager ftcManager;
    assert(!FTC_Manager_New(ftLibrary, 32, 32, 2 * 1024 * 1024, FreetypeLibrary::FtcFaceRequester, fontsPath, &ftcManager));
    return (jlong) ftcManager;
}

extern "C" JNIEXPORT void JNICALL Java_com_dmi_perfectreader_book_font_FreetypeLibrary_nativeDoneFtcManager
        (JNIEnv *env, jclass, jlong ftcManagerRef) {
    FTC_Manager ftcManager = (FTC_Manager) ftcManagerRef;
    FTC_Manager_Done(ftcManager);
}

extern "C" JNIEXPORT jlong JNICALL Java_com_dmi_perfectreader_book_font_FreetypeLibrary_nativeInitSbitCache
        (JNIEnv *env, jclass, jlong ftcManagerRef) {
    FTC_Manager ftcManager = (FTC_Manager) ftcManagerRef;
    FTC_SBitCache ftcSBitCache;
    assert(!FTC_SBitCache_New(ftcManager, &ftcSBitCache));
    return (jlong) ftcSBitCache;
}

extern "C" JNIEXPORT jlong JNICALL Java_com_dmi_perfectreader_book_font_FreetypeLibrary_nativeNewCText
        (JNIEnv *env, jclass, jstring jText) {
    char *cText = (char*) env->GetStringUTFChars(jText, 0);
    return (jlong) cText;
}

extern "C" JNIEXPORT void JNICALL Java_com_dmi_perfectreader_book_font_FreetypeLibrary_nativeDeleteCText
        (JNIEnv *env, jclass, jstring jText, jlong cTextRef) {
    char *cText = (char *) cTextRef;
    env->ReleaseStringUTFChars(jText, cText);
}

extern "C" JNIEXPORT jlong JNICALL Java_com_dmi_perfectreader_book_font_FreetypeLibrary_nativeCreateScaler
        (JNIEnv *env, jclass, jstring jName, jfloat width, jfloat height, jint hDpi, jint vDpi) {
    const char *cName = env->GetStringUTFChars(jName, 0);

    FreetypeLibrary::MyFaceId *myFaceId = new FreetypeLibrary::MyFaceId();
    myFaceId->name = string(cName);

    FTC_Scaler ftcScaler = new FTC_ScalerRec();
    ftcScaler->face_id = myFaceId;
    ftcScaler->width = 64 * width;
    ftcScaler->height = 64 * height;
    ftcScaler->x_res = hDpi;
    ftcScaler->y_res = vDpi;
    ftcScaler->pixel = 0;

    env->ReleaseStringUTFChars(jName, cName);

    return (jlong) ftcScaler;
}

extern "C" JNIEXPORT void JNICALL Java_com_dmi_perfectreader_book_font_FreetypeLibrary_nativeDestroyScaler
        (JNIEnv *, jclass, jlong ftcScalerRef) {
    FTC_Scaler ftcScaler = (FTC_Scaler) ftcScalerRef;
    delete (FreetypeLibrary::MyFaceId*) ftcScaler->face_id;
    delete ftcScaler;
}