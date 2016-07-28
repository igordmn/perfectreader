#pragma once

#include <ft2build.h>
#include FT_FREETYPE_H
#include FT_STROKER_H
#include FT_CACHE_H
#include <stdint.h>
#include "../paint/PixelBuffer.h"
#include "../util/LRUCache.hpp"
#include "FontFaceID.h"
#include "FontConfig.h"

namespace dmi {
    struct Glyph {
        FT_Glyph ftGlyph;
        FT_Glyph_Metrics metrics;
        FT_Pos lsbDelta;
        FT_Pos rsbDelta;
    };

    struct GlyphBitmap {
        int16_t left;
        int16_t top;
        AlphaBuffer buffer;
    };

    class FontCache {
    private:
        struct GlyphCacheKey {
            bool isBitmap;            // если false, то ищем Glyph, если true, то GlyphBitmap
            FontConfig *fontConfig;   // для Glyph и GlyphBitmap
            uint32_t index;           // для Glyph и GlyphBitmap
            uint8_t subpixelX;        // только для GlyphBitmap
        };

        FTC_Manager manager;
        LRUCache<GlyphCacheKey, void> glyphCache;
        GlyphCacheKey glyphCacheKey;
        GlyphCacheKey glyphBitmapCacheKey;
        FTC_ScalerRec cacheScaler;

        static uint32_t weighEntry(const GlyphCacheKey &key, void *value);
        static void destroyEntry(const GlyphCacheKey &, void *);

    public:
        static const uint8_t SUBPIXEL_COUNT = 4;

        FontCache(FT_Library library, uint16_t cacheMaxFaces, uint16_t cacheMaxSizes, uint32_t cacheMaxBytes);
        ~FontCache();

        FT_Face getFace(const FontFaceID *faceID);
        FT_Size getSize(const FontFaceID *faceID, float sizeX, float sizeY);
        const Glyph &getGlyph(FontConfig *fontConfig, uint32_t index);
        const GlyphBitmap &getGlyphBitmap(FontConfig *fontConfig, uint32_t index, uint8_t subpixelX);

    private:
        FontCache(const FontCache &cache) = delete;
    };
}