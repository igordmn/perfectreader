package com.dmi.typoweb;

import com.dmi.util.natv.UsedByNative;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.net.URLConnection.guessContentTypeFromName;

// Типы были взяты из <chromium_src>/net/base/mime_util.cc
@UsedByNative
abstract class WebMimeRegistryImpl {
    private static final Set<String> SUPPORTED_IMAGE_TYPES = new HashSet<>(Arrays.asList(
            "image/jpeg",
            "image/pjpeg",
            "image/jpg",
            "image/webp",
            "image/png",
            "image/gif",
            "image/bmp",
            "image/vnd.microsoft.icon",
            "image/x-icon",
            "image/x-xbitmap",
            "image/x-png"
    ));

    private static final Set<String> SUPPORTED_NON_IMAGE_TYPES = new HashSet<>(Arrays.asList(
            "image/svg+xml",
            "application/xml",
            "application/atom+xml",
            "application/rss+xml",
            "application/xhtml+xml",
            "application/json",
            "multipart/related",
            "multipart/x-mixed-replace"
    ));

    private static final Set<String> SUPPORTED_JAVASCRIPT_TYPES = new HashSet<>(Arrays.asList(
            "text/javascript",
            "text/ecmascript",
            "application/javascript",
            "application/ecmascript",
            "application/x-javascript",
            "text/javascript1.1",
            "text/javascript1.2",
            "text/javascript1.3",
            "text/jscript",
            "text/livescript"
    ));

    private static final Set<String> SUPPORTED_MEDIA_TYPES = new HashSet<>(Arrays.asList(
            "audio/ogg",
            "audio/webm",
            "audio/wav",
            "audio/x-wav",
            "audio/mp4",
            "audio/x-m4a",
            "audio/mp3",
            "audio/x-mp3",
            "audio/mpeg",
            "video/webm",
            "video/mp4",
            "video/x-m4v",
            "application/ogg"
            // "application/vnd.apple.mpegurl" not supported yet (http live stream)
            // "application/x-mpegurl"  not supported yet (http live stream)
    ));

    private static final Set<String> UNSUPPORTED_TEXT_TYPES = new HashSet<>(Arrays.asList(
            "text/calendar",
            "text/x-calendar",
            "text/x-vcalendar",
            "text/vcalendar",
            "text/vcard",
            "text/x-vcard",
            "text/directory",
            "text/ldif",
            "text/qif",
            "text/x-qif",
            "text/x-csv",
            "text/x-vcf",
            "text/rtf",
            "text/comma-separated-values",
            "text/csv",
            "text/tab-separated-values",
            "text/tsv",
            "text/ofx",
            "text/vnd.sun.j2me.app-descriptor"
    ));

    private static final Map<String , String> PRIMARY_MIME_TYPES = new HashMap<String , String>() {{
        put("html", "text/html");
        put("htm", "text/html");
        put("shtml", "text/html");
        put("shtm", "text/html");
        put("css", "text/css");
        put("xml", "text/xml");
        put("gif", "image/gif");
        put("jpeg", "image/jpeg");
        put("jpg", "image/jpeg");
        put("webp", "image/webp");
        put("png", "image/png");
        put("mp4", "video/mp4");
        put("m4v", "video/mp4");
        put("m4a", "audio/x-m4a");
        put("mp3", "audio/mp3");
        put("ogv", "video/ogg");
        put("ogm", "video/ogg");
        put("ogg", "audio/ogg");
        put("oga", "audio/ogg");
        put("opus", "audio/ogg");
        put("webm", "video/webm");
        put("wav", "audio/wav");
        put("xhtml", "application/xhtml+xml");
        put("xht", "application/xhtml+xml");
        put("xhtm", "application/xhtml+xml");
        put("crx", "application/x-chrome-extension");
        put("mhtml", "multipart/related");
        put("mht", "multipart/related");
    }};
    
    private static final Map<String , String> SECONDARY_MIME_TYPES = new HashMap<String , String>() {{
        put("exe,com,bin", "application/octet-stream");
        put("gz", "application/gzip");
        put("pdf", "application/pdf");
        put("ps", "application/postscript");
        put("eps", "application/postscript");
        put("ai", "application/postscript");
        put("js", "application/javascript");
        put("woff", "application/font-woff");
        put("bmp", "image/bmp");
        put("ico", "image/x-icon");
        put("jfif", "image/jpeg");
        put("pjpeg", "image/jpeg");
        put("pjp", "image/jpeg");
        put("tiff", "image/tiff");
        put("tif", "image/tiff");
        put("xbm", "image/x-xbitmap");
        put("svg", "image/svg+xml");
        put("svgz", "image/svg+xml");
        put("png", "image/x-png");
        put("eml", "message/rfc822");
        put("txt", "text/plain");
        put("text", "text/plain");
        put("ehtml", "text/html");
        put("rss", "application/rss+xml");
        put("rdf", "application/rdf+xml");
        put("xsl", "text/xml");
        put("xbl", "text/xml");
        put("xslt", "text/xml");
        put("xul", "application/vnd.mozilla.xul+xml");
        put("swf", "application/x-shockwave-flash");
        put("swl", "application/x-shockwave-flash");
        put("p7m", "application/pkcs7-mime");
        put("p7c", "application/pkcs7-mime");
        put("p7z", "application/pkcs7-mime");
        put("p7s", "application/pkcs7-signature");
        put("m3u8", "application/x-mpegurl");
    }};

    @UsedByNative
    private static boolean supportsMIMEType(String mimeType) {
        return mimeType.startsWith("image/") && supportsImageMIMEType(mimeType) ||
               supportsNonImageMIMEType(mimeType);
    }

    @UsedByNative
    private static boolean supportsImageMIMEType(String mimeType) {
        return SUPPORTED_IMAGE_TYPES.contains(mimeType);
    }

    @UsedByNative
    private static boolean supportsImagePrefixedMIMEType(String mimeType) {
        return supportsImageMIMEType(mimeType) ||
               mimeType.startsWith("image/") && supportsNonImageMIMEType(mimeType);
    }

    @UsedByNative
    private static boolean supportsJavaScriptMIMEType(String mimeType) {
        return SUPPORTED_JAVASCRIPT_TYPES.contains(mimeType);
    }

    @UsedByNative
    private static boolean supportsNonImageMIMEType(String mimeType) {
        return SUPPORTED_NON_IMAGE_TYPES.contains(mimeType) ||
               SUPPORTED_JAVASCRIPT_TYPES.contains(mimeType) ||
               SUPPORTED_MEDIA_TYPES.contains(mimeType) ||
               mimeType.startsWith("text/") && !UNSUPPORTED_TEXT_TYPES.contains(mimeType) ||
               mimeType.startsWith("application/") && mimeType.endsWith("json");
    }

    @UsedByNative
    private static boolean supportsMediaMIMEType(String mimeType, String codecs, String keySystem) {
        return SUPPORTED_MEDIA_TYPES.contains(mimeType);
    }

    @UsedByNative
    private static boolean supportsMediaSourceMIMEType(String mimeType, String codecs) {
        return SUPPORTED_MEDIA_TYPES.contains(mimeType);
    }

    @UsedByNative
    private static String mimeTypeForExtension(String extension) {
        String mimeType = PRIMARY_MIME_TYPES.get(extension);
        if (mimeType == null) mimeType = SECONDARY_MIME_TYPES.get(extension);
        if (mimeType == null) mimeType = guessContentTypeFromName("." + extension);
        if (mimeType == null) mimeType = "";
        return mimeType;
    }

    @UsedByNative
    private static String wellKnownMimeTypeForExtension(String extension) {
        String mimeType = PRIMARY_MIME_TYPES.get(extension);
        return mimeType != null ? mimeType : "";
    }

    @UsedByNative
    static String mimeTypeFromFile(String filePath) {
        return mimeTypeForExtension(getFileExtension(filePath));
    }

    private static String getFileExtension(String path) {
        int lastDotIndex = path.lastIndexOf(".");
        return lastDotIndex != -1 ? path.substring(lastDotIndex + 1) : "";
    }
}
