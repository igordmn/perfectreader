package com.dmi.typoweb;

import android.annotation.SuppressLint;
import android.content.res.Resources;

import com.dmi.util.natv.UsedByNative;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import timber.log.Timber;

import static com.dmi.typoweb.TypoWebLibrary.mainThread;
import static com.dmi.typoweb.WebMimeRegistryImpl.mimeTypeFromFile;
import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;
import static java.net.URLConnection.guessContentTypeFromStream;

@UsedByNative
class WebURLLoaderImpl {
    // WARNING!!! don't make load in another thread. if you did it, you should fix bug PR-267 (for example, disable invalidate on any url loading)

    private static final int BUFFER_SIZE = 8192;

    private long nativeWebURLLoaderImpl;
    private volatile boolean cancelled = false;
    private Runnable loadTask;

    public WebURLLoaderImpl(long nativeWebURLLoaderImpl, URLHandler urlHandler) {
        this.nativeWebURLLoaderImpl = nativeWebURLLoaderImpl;
        this.urlHandler = urlHandler;
    }

    @UsedByNative
    private void destroy() {
        mainThread().cancelTask(loadTask);
    }

    @UsedByNative
    private void cancel() {
        cancelled = true;
    }

    private final URLHandler urlHandler;

    @UsedByNative
    private void load(String url) {
        loadTask = () -> {
            String decodedURL = null;
            try {
                decodedURL = URLDecoder.decode(url, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                onFail("Failed to decode url: %s", url);
            }
            if (decodedURL != null) {
                if (cancelled) {
                    onFail("URL loading aborted: %s", decodedURL);
                } else {
                    try {
                        handleRequest(decodedURL);
                    } catch (Resources.NotFoundException | FileNotFoundException e) {
                        onFail("URL not found: %s", decodedURL);
                    } catch (SecurityException e) {
                        onFail("Access to URL denied: %s", decodedURL);
                    } catch (Exception e) {
                        Timber.e(e, "Error loading URL");
                        onFail("Error loading URL: %s", decodedURL);
                    }
                }
            }
        };
        mainThread().postTask(loadTask);
    }

    private void onFail(String messageFormat, String url) {
        String shortUrl = url.length() > 200 ? url.substring(0, 200) + "..." : url;
        String message = format(messageFormat, shortUrl);
        Timber.e(message);
        nativeDidFail(nativeWebURLLoaderImpl, message);
    }

    @SuppressLint("NewApi")
    private void handleRequest(String url) throws IOException {
        checkState(urlHandler != null, "need set url handler");
        try (InputStream stream = urlHandler.handleURL(url)) {
            int expectedContentLength = stream.available();
            String contentType = guessContentType(stream, url);
            nativeDidReceiveResponse(nativeWebURLLoaderImpl, expectedContentLength, contentType);

            int totalLength = 0;
            byte[] data = new byte[BUFFER_SIZE];
            int length;
            while ((length = stream.read(data, 0, data.length)) != -1) {
                nativeDidReceiveData(nativeWebURLLoaderImpl, data, length);
                totalLength += length;
            }

            final int totalLengthFinal = totalLength;
            nativeDidFinishLoading(nativeWebURLLoaderImpl, totalLengthFinal);
        }
    }

    private static String guessContentType(InputStream stream, String url) {
        try {
            String contentType = mimeTypeFromFile(url);
            if (contentType != null) {
                return contentType;
            }
            contentType = guessContentTypeFromStream(stream);
            if (contentType != null) {
                return contentType;
            }
        } catch (Exception ignored) {
        }
        return "";
    }

    private native void nativeDidReceiveResponse(long nativeWebURLLoaderImpl, long contentLength, String contentType);
    private native void nativeDidReceiveData(long nativeWebURLLoaderImpl, byte[] data, int dataLength);
    private native void nativeDidFinishLoading(long nativeWebURLLoaderImpl, long totalLength);
    private native void nativeDidFail(long nativeWebURLLoaderImpl, String message);
}
