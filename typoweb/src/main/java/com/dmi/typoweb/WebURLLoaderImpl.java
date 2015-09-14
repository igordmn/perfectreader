package com.dmi.typoweb;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.util.Log;

import com.dmi.util.natv.UsedByNative;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import timber.log.Timber;

import static com.dmi.typoweb.TypoWebLibrary.mainThread;
import static com.dmi.typoweb.WebMimeRegistryImpl.mimeTypeFromFile;
import static com.dmi.util.concurrent.Threads.postIOTask;
import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;
import static java.net.URLConnection.guessContentTypeFromStream;

@UsedByNative
class WebURLLoaderImpl {
    // WARNING!!! don't make load in another thread. if you did it, you should fix bug PR-267 (for example, disable invalidate on any url loading)

    private static final int BUFFER_SIZE = 8192;

    private long nativeWebURLLoaderImpl;

    public WebURLLoaderImpl(long nativeWebURLLoaderImpl, URLHandler urlHandler) {
        this.nativeWebURLLoaderImpl = nativeWebURLLoaderImpl;
        this.urlHandler = urlHandler;
    }

    private final URLHandler urlHandler;

    @UsedByNative
    private void load(String url) {
        mainThread().postTask(() -> {
             try {
                 handleRequest(url);
             } catch (InterruptedException e) {
                 Thread.currentThread().interrupt();
             } catch (Resources.NotFoundException | FileNotFoundException e) {
                 onFail("URL not found: %s", url);
             } catch (SecurityException e) {
                 onFail("Access to URL denied: %s", url);
             } catch (Exception e) {
                 Timber.e(e, "Error loading URL");
                 onFail("Error loading URL: %s", url);
             }
         });
    }

    private void onFail(String messageFormat, String url) {
        String shortUrl = url.length() > 200 ? url.substring(0, 200) : url;
        String message = format(messageFormat, shortUrl);
        Timber.e(message);
        nativeDidFail(nativeWebURLLoaderImpl, message);
    }

    @UsedByNative
    private void cancel() {
    }

    @SuppressLint("NewApi")
    private void handleRequest(String url) throws IOException, InterruptedException {
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
