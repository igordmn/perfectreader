#include "FontCache.h"

#include <ft2build.h>
#include FT_FREETYPE_H
#include FT_CACHE_H
#include "../paint/PixelBuffer.h"
#include "FTErrors.h"
#include "FontFaceID.h"

using namespace dmi;

namespace {
    static FT_Error FaceRequester(FTC_FaceID ftFaceID, FT_Library library, void *, FT_Face *face) {
        const FontFaceID *faceID = (const FontFaceID *) ftFaceID;
        FT_CHECKM(
                FT_New_Face(library, faceID->filePath.c_str(), faceID->index, face),
                "path: %s, index: %d", faceID->filePath.c_str(), faceID->index
        );
        return 0;
    }
}

namespace dmi {
    uint32_t FontCache::weighEntry(const GlyphCacheKey &key, void *value) {
        if (key.isBitmap) {
            const GlyphBitmap *glyphBitmap = (GlyphBitmap *) value;
            const AlphaBuffer &buffer = glyphBitmap->buffer;
            return buffer.stride * buffer.height + sizeof(buffer) + sizeof(*glyphBitmap);
        } else {
            const Glyph *glyph = (Glyph *) value;
            const FT_Glyph ftGlyph = glyph->ftGlyph;
            CHECK(ftGlyph->format == FT_GLYPH_FORMAT_OUTLINE)
            const FT_OutlineGlyph outlineGlyph = (FT_OutlineGlyph) glyph;
            return outlineGlyph->outline.n_points * (sizeof(FT_Vector) + sizeof(FT_Byte)) +
                   outlineGlyph->outline.n_contours * sizeof(FT_Short) +
                   sizeof(*outlineGlyph) + sizeof(*glyph);
        }
    }

    void FontCache::destroyEntry(const GlyphCacheKey &key, void *value) {
        if (key.isBitmap) {
            const GlyphBitmap *glyphBitmap = (GlyphBitmap *) value;
            const AlphaBuffer &buffer = glyphBitmap->buffer;
            delete[] buffer.data;
        } else {
            const Glyph *glyph = (Glyph *) value;
            const FT_Glyph ftGlyph = glyph->ftGlyph;
            FT_Done_Glyph(ftGlyph);
        }
    }

    FontCache::FontCache(FT_Library library, uint16_t cacheMaxFaces, uint16_t cacheMaxSizes, uint32_t cacheMaxBytes) :
            glyphCache(cacheMaxBytes, weighEntry, destroyEntry) {
        FT_CHECK(FTC_Manager_New(library, cacheMaxFaces, cacheMaxSizes, 0, FaceRequester, 0, &manager));
        memset(&glyphCacheKey, 0, sizeof(GlyphCacheKey));
    }

    FontCache::~FontCache() {
        FTC_Manager_Done(manager);
    }

    FT_Face FontCache::getFace(const FontFaceID *faceID) {
        FT_Face face;
        FT_CHECK(FTC_Manager_LookupFace(manager, (FTC_FaceID) faceID, &face));
        return face;
    }


    FT_Size FontCache::getSize(const FontFaceID *faceID, float sizeX, float sizeY) {
        cacheScaler.face_id = (FTC_FaceID) faceID;
        cacheScaler.width = (uint32_t) (sizeX * 64);
        cacheScaler.height = (uint32_t) (sizeY * 64);
        cacheScaler.x_res = 0;
        cacheScaler.y_res = 0;
        cacheScaler.pixel = 0;

        FT_Size size;
        FT_CHECK(FTC_Manager_LookupSize(manager, &cacheScaler, &size));
        return size;
    }

    const Glyph &FontCache::getGlyph(FontConfig *fontConfig, uint32_t index) {
        glyphCacheKey.isBitmap = false;
        glyphCacheKey.fontConfig = fontConfig;
        glyphCacheKey.index = index;
        glyphCacheKey.subpixelX = 0;

        Glyph *glyph = (Glyph *) glyphCache.get(glyphCacheKey);

        if (glyph == 0) {
            FT_Size size = getSize(fontConfig->faceID, fontConfig->sizeX, fontConfig->sizeY);
            FT_Face face = size->face;
            FT_CHECK(FT_Load_Glyph(face, index, FT_LOAD_DEFAULT));
            FT_GlyphSlot glyphSlot = face->glyph;

            glyph = new Glyph();
            FT_Get_Glyph(glyphSlot, &glyph->ftGlyph);
            glyph->metrics = glyphSlot->metrics;
            glyph->lsbDelta = glyphSlot->lsb_delta;
            glyph->rsbDelta = glyphSlot->rsb_delta;

            glyphCache.put(glyphCacheKey, glyph);
        }

        return *glyph;
    }

    const GlyphBitmap &FontCache::getGlyphBitmap(FontConfig *fontConfig, uint32_t index, uint8_t subpixelX) {
        glyphBitmapCacheKey.isBitmap = true;
        glyphBitmapCacheKey.fontConfig = fontConfig;
        glyphBitmapCacheKey.index = index;
        glyphBitmapCacheKey.subpixelX = subpixelX;

        GlyphBitmap *bitmap = (GlyphBitmap *) glyphCache.get(glyphBitmapCacheKey);

        if (bitmap == 0) {
            const Glyph &glyph = getGlyph(fontConfig, index);

            FT_Glyph ftGlyph = glyph.ftGlyph;
            FT_CHECK(FT_Glyph_To_Bitmap(&ftGlyph, FT_RENDER_MODE_NORMAL, 0, 0));
            FT_BitmapGlyph bitmapGlyph = (FT_BitmapGlyph) ftGlyph;
            FT_Bitmap &ftBitmap = bitmapGlyph->bitmap;

            bitmap = new GlyphBitmap();
            bitmap->buffer.width = (uint16_t) ftBitmap.width;
            bitmap->buffer.height = (uint16_t) ftBitmap.rows;
            bitmap->buffer.stride = (uint16_t) ftBitmap.pitch;
            bitmap->buffer.data = new uint8_t[ftBitmap.pitch * ftBitmap.rows];
            memcpy(bitmap->buffer.data, ftBitmap.buffer, ftBitmap.pitch * ftBitmap.rows);

            FT_Done_Glyph(ftGlyph);

            glyphCache.put(glyphBitmapCacheKey, bitmap);
        }

        return *bitmap;
    }
}