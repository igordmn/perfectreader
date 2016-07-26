#include "FontCache.h"

#include <ft2build.h>
#include FT_FREETYPE_H
#include FT_CACHE_H
#include FT_STROKER_H
#include FT_OUTLINE_H
#include "../paint/PixelBuffer.h"
#include "../paint/PaintUtils.h"
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

    signed long freetypeToF26Dot6(float value) {
        return (signed long) (value * 64);
    }

    unsigned long freetypeToUF26Dot6(float value) {
        return (unsigned long) (value * 64);
    }

    signed long freetypeToF16Dot16(float value) {
        return (signed long) (value * 0x10000L);
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
            delete glyphBitmap;
        } else {
            const Glyph *glyph = (Glyph *) value;
            const FT_Glyph ftGlyph = glyph->ftGlyph;
            FT_Done_Glyph(ftGlyph);
            delete glyph;
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
        cacheScaler.width = freetypeToUF26Dot6(sizeX);
        cacheScaler.height = freetypeToUF26Dot6(sizeY);
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

            FT_Int32 flags = FT_LOAD_DEFAULT | FT_LOAD_NO_BITMAP;
            if (fontConfig->hinting) {
                if (fontConfig->forceAutoHinting)
                    flags |= FT_LOAD_FORCE_AUTOHINT;
                if (!fontConfig->antialias) {
                    flags |= FT_LOAD_TARGET_MONO;
                } else if (fontConfig->lightHinting) {
                    flags |= FT_LOAD_TARGET_LIGHT;
                } else {
                    flags |= FT_LOAD_TARGET_NORMAL;
                }
            } else {
                flags |= FT_LOAD_NO_HINTING;
            }

            FT_CHECK(FT_Load_Glyph(face, index, flags));
            FT_GlyphSlot glyphSlot = face->glyph;
            FT_Glyph ftGlyph;
            FT_Get_Glyph(glyphSlot, &ftGlyph);

            CHECK(ftGlyph->format == FT_GLYPH_FORMAT_OUTLINE)
            FT_OutlineGlyph outlineGlyph = (FT_OutlineGlyph) ftGlyph;
            FT_Outline &outline = outlineGlyph->outline;

            FT_Matrix matrix;
            matrix.xx = freetypeToF16Dot16(fontConfig->scaleX);
            matrix.xy = freetypeToF16Dot16(fontConfig->skewX);
            matrix.yx = freetypeToF16Dot16(fontConfig->skewY);
            matrix.yy = freetypeToF16Dot16(fontConfig->scaleY);

            FT_Outline_Transform(&outline, &matrix);
            FT_Vector_Transform(&ftGlyph->advance, &matrix);

            if (fontConfig->emboldenStrengthX != 0 || fontConfig->emboldenStrengthY != 0) {
                FT_Outline_EmboldenXY(
                        &outline,
                        freetypeToF26Dot6(fontConfig->emboldenStrengthX),
                        freetypeToF26Dot6(fontConfig->emboldenStrengthY)
                );
            }

            if (fontConfig->strokeInside || fontConfig->strokeOutside) {
                FT_Glyph strokeGlyph = ftGlyph;
                FT_Stroker stroker;
                FT_Stroker_New(ftGlyph->library, &stroker);
                FT_Stroker_Set(stroker,
                               freetypeToF26Dot6(fontConfig->strokeRadius),
                               fontConfig->strokeLineCap,
                               fontConfig->strokeLineJoin,
                               freetypeToF16Dot16(fontConfig->strokeMiterLimit)
                );

                if (fontConfig->strokeInside && fontConfig->strokeOutside) {
                    FT_Glyph_Stroke(&strokeGlyph, stroker, 1);
                } else if (fontConfig->strokeInside) {
                    FT_Glyph_StrokeBorder(&strokeGlyph, stroker, 1, 1);
                } else {
                    FT_Glyph_StrokeBorder(&strokeGlyph, stroker, 0, 1);
                }

                FT_Stroker_Done(stroker);
                ftGlyph = strokeGlyph;
            }

            glyph = new Glyph();
            glyph->ftGlyph = ftGlyph;
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
            FT_Render_Mode_ renderMode = fontConfig->antialias ? FT_RENDER_MODE_NORMAL : FT_RENDER_MODE_MONO;
            FT_CHECK(FT_Glyph_To_Bitmap(&ftGlyph, renderMode, 0, 0));
            FT_BitmapGlyph bitmapGlyph = (FT_BitmapGlyph) ftGlyph;
            FT_Bitmap &ftBitmap = bitmapGlyph->bitmap;

            bitmap = new GlyphBitmap();

            if (fontConfig->blurRadius == 0) {
                bitmap->top = (int16_t) -bitmapGlyph->top;
                bitmap->left = (int16_t) bitmapGlyph->left;
                bitmap->buffer.width = (uint16_t) ftBitmap.width;
                bitmap->buffer.height = (uint16_t) ftBitmap.rows;
                bitmap->buffer.stride = (uint16_t) ftBitmap.pitch;
                bitmap->buffer.data = new uint8_t[ftBitmap.pitch * ftBitmap.rows];
                memcpy(bitmap->buffer.data, ftBitmap.buffer, ftBitmap.pitch * ftBitmap.rows);
            } else {
                AlphaBuffer original;
                original.width = (uint16_t) ftBitmap.width;
                original.height = (uint16_t) ftBitmap.rows;
                original.stride = (uint16_t) ftBitmap.pitch;
                original.data = ftBitmap.buffer;

                uint16_t additionalPixels = (uint16_t) (fontConfig->blurRadius + 1);

                AlphaBuffer expanded;
                expanded.width = (uint16_t) (ftBitmap.width + 2 * additionalPixels);
                expanded.height = (uint16_t) (ftBitmap.rows + 2 * additionalPixels);
                expanded.stride = (uint16_t) ftBitmap.pitch;
                expanded.data = new uint8_t[expanded.stride * expanded.height];
                paintUtils::clear(expanded, 0);
                paintUtils::copyPixels(expanded, original, additionalPixels, additionalPixels);

                AlphaBuffer blurred;
                blurred.width = expanded.width;
                blurred.height = expanded.height;
                blurred.stride = expanded.stride;
                blurred.data = new uint8_t[expanded.stride * expanded.height];

                paintUtils::blur(blurred, expanded, fontConfig->blurRadius);

                delete[] expanded.data;

                bitmap->top = (int16_t) -bitmapGlyph->top - additionalPixels;
                bitmap->left = (int16_t) bitmapGlyph->left - additionalPixels;
                bitmap->buffer = blurred;
            }

            // ftGlyph - это уже bitmapGlyph, а не glyph.ftGlyph (после вызова FT_Glyph_To_Bitmap указатель поменялся). Его нужно удалить
            FT_Done_Glyph(ftGlyph);

            glyphCache.put(glyphBitmapCacheKey, bitmap);
        }

        return *bitmap;
    }
}