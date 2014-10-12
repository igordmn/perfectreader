#include <jni.h>
#include <assert.h>
#include <ft2build.h>
#include FT_FREETYPE_H
#include FT_GLYPH_H
#include FT_CACHE_H
#include <GLES2/gl2.h>

namespace GlText {
    unsigned char *canvas = new unsigned char[0];
    int canvasWidth = 0;
    int canvasHeight = 0;

    inline void clearCanvas() {
        for (int i = 0; i < canvasWidth * canvasHeight; i++) {
            canvas[i] = 0;
        }
    }

    inline void drawBitmap(int x, int y, int width, int height, FT_Byte *buffer) {
        int xMax = x + width;
        int yMax = y + height;

        int dx, dy, sx, sy;
        for (dx = x, sx = 0; dx < xMax; dx++, sx++) {
            for (dy = y, sy = 0; dy < yMax; dy++, sy++) {
                if (dx >= 0 && dy >= 0 && dx < canvasWidth && dy < canvasHeight) {
                    canvas[dy * canvasWidth + dx] |= buffer[sy * width + sx];
                }
            }
        }
    }

    inline void setSize(int pWidth, int pHeight) {
        if (pWidth * pHeight > canvasWidth * canvasHeight) {
            delete canvas;
            canvas = new unsigned char[pWidth * pHeight];
        }
        canvasWidth = pWidth;
        canvasHeight = pHeight;
    }
}

extern "C" JNIEXPORT void JNICALL Java_com_dmi_perfectreader_graphic_GLText_drawSymbols
        (JNIEnv *env, jclass,
         jint width, jint height,
         jlong ftcManagerRef, jlong ftcSBitCacheRef, jlong ftcScalerRef,
         jintArray jGlyphIndices, jfloatArray jGlyphCoordinates) {
    GlText::setSize(width, height);

    FTC_Manager ftcManager = (FTC_Manager) ftcManagerRef;
    FTC_SBitCache ftcSBitCache = (FTC_SBitCache) ftcSBitCacheRef;
    FTC_Scaler ftcScaler = (FTC_Scaler) ftcScalerRef;

    FTC_Node ftcNode;
    FTC_SBit ftcSBit;

    jint *glyphIndices = env->GetIntArrayElements(jGlyphIndices, 0);
    jfloat *glyphCoordinates = env->GetFloatArrayElements(jGlyphCoordinates, 0);

    GlText::clearCanvas();

    int coordIt = 0;
    for (int i = 0; i < env->GetArrayLength(jGlyphIndices); i++) {
        assert(!FTC_SBitCache_LookupScaler(ftcSBitCache, ftcScaler, FT_LOAD_DEFAULT | FT_LOAD_RENDER, glyphIndices[i], &ftcSBit, &ftcNode));
        FTC_Node_Unref(ftcNode, ftcManager);
        float x = glyphCoordinates[coordIt++];
        float y = glyphCoordinates[coordIt++];
        GlText::drawBitmap(x + ftcSBit->left, y - ftcSBit->top, ftcSBit->width, ftcSBit->height, ftcSBit->buffer);
    }

    env->ReleaseIntArrayElements(jGlyphIndices, glyphIndices, 0);
    env->ReleaseFloatArrayElements(jGlyphCoordinates, glyphCoordinates, 0);

    glTexSubImage2D(
            GL_TEXTURE_2D,
            0,
            0,
            0,
            GlText::canvasWidth,
            GlText::canvasHeight,
            GL_ALPHA,
            GL_UNSIGNED_BYTE,
            GlText::canvas
    );
}
