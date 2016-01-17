package com.dmi.typoweb;

import android.annotation.SuppressLint;
import android.content.res.Resources;

import com.dmi.util.concurrent.Threads;
import com.dmi.util.natv.UsedByNative;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.util.concurrent.Future;

import timber.log.Timber;

import static com.dmi.typoweb.TypoWebLibrary.mainThread;
import static com.dmi.typoweb.WebMimeRegistryImpl.mimeTypeFromFile;
import static com.dmi.util.concurrent.Interrupts.checkThreadInterrupted;
import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;
import static java.net.URLConnection.guessContentTypeFromStream;

@UsedByNative
class WebURLLoaderImpl {
    private static final int INITIAL_DATA_CAPACITY = 32 * 1024;
    private static final int BUFFER_SIZE = 8 * 1024;

    private long nativeWebURLLoaderImpl;
    private ByteBuffer data = nativeCreateBuffer(INITIAL_DATA_CAPACITY);
    private boolean cancelled = false;
    private boolean destroyed = false;

    public WebURLLoaderImpl(long nativeWebURLLoaderImpl, URLHandler urlHandler) {
        this.nativeWebURLLoaderImpl = nativeWebURLLoaderImpl;
        this.urlHandler = urlHandler;
    }

    @UsedByNative
    private void destroy() {
        nativeDeleteBuffer(data);
        destroyed = true;
    }

    private void checkDataCapacity(int desiredLength) {
        if (desiredLength > data.capacity()) {
            ByteBuffer newData = nativeCreateBuffer(data.capacity() * 2);
            data.position(0);
            newData.put(data);
            nativeDeleteBuffer(data);
            data = newData;
        }
    }

    @UsedByNative
    private void cancel() {
        cancelled = true;
    }

    private final URLHandler urlHandler;

    @UsedByNative
    private void load(String url) {
        Threads.postIOTask(() -> {
            String decodedURL = null;
            try {
                decodedURL = URLDecoder.decode(url, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                didFail("Failed to decode url: %s", url);
            }
            if (decodedURL != null) {
                try {
                    handleRequest(decodedURL);
                } catch (Resources.NotFoundException | FileNotFoundException e) {
                    didFail("URL not found: %s", decodedURL);
                } catch (SecurityException e) {
                    didFail("Access to URL denied: %s", decodedURL);
                } catch (InterruptedException e) {
                    didFail("URL loading interrupted", decodedURL);
                } catch (Exception e) {
                    Timber.e(e, "Error loading URL");
                    didFail("Error loading URL: %s", decodedURL);
                }
            }
        });
    }

    @SuppressLint("NewApi")
    private void handleRequest(String url) throws IOException, InterruptedException {
        checkState(urlHandler != null, "need set url handler");
        checkState(data.position() == 0, "handleRequest called twice");

        try (InputStream stream = urlHandler.handleURL(url)) {
            checkCancelled();

            int expectedContentLength = stream.available();
            String contentType = guessContentType(stream, url);
            didReceiveResponse(expectedContentLength, contentType);

            byte[] buffer = new byte[BUFFER_SIZE];
            int length;
            while ((length = stream.read(buffer, 0, buffer.length)) != -1) {
                checkDataCapacity(data.position() + length);
                data.put(buffer, 0, length);
                checkCancelled();
            }

            didReceiveData(data, data.position());
            didFinishLoading(data.position());
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
    
    private void didReceiveResponse(long contentLength, String contentType) {
        postMainThread(() -> nativeDidReceiveResponse(nativeWebURLLoaderImpl, contentLength, contentType));
    }

    // WARNING!!! call this method only once, at the end of url loading
    // if you call it multiple times, you should fix PR-267 (for example, disable invalidate document on any url loading)
    private void didReceiveData(ByteBuffer data, int dataLength) {
        postMainThread(() -> nativeDidReceiveData(nativeWebURLLoaderImpl, data, dataLength));
    }

    private void didFinishLoading(long totalLength) {
        postMainThread(() -> nativeDidFinishLoading(nativeWebURLLoaderImpl, totalLength));
    }

    private void didFail(String messageFormat, String url) {
        postMainThread(() -> {
            String shortUrl = url.length() > 200 ? url.substring(0, 200) + "..." : url;
            String message = format(messageFormat, shortUrl);
            Timber.e(message);
            nativeDidFail(nativeWebURLLoaderImpl, message);
        });
    }

    private void postMainThread(Runnable runnable) {
        mainThread().postTask(() -> {
            if (!destroyed) {
                runnable.run();
            }
        });
    }

    private void checkCancelled() throws InterruptedException {
        if (cancelled) {
            throw new InterruptedException();
        }
    }

    private static native void nativeDidReceiveResponse(long nativeWebURLLoaderImpl, long contentLength, String contentType);
    private static native void nativeDidReceiveData(long nativeWebURLLoaderImpl, ByteBuffer data, int dataLength);
    private static native void nativeDidFinishLoading(long nativeWebURLLoaderImpl, long totalLength);
    private static native void nativeDidFail(long nativeWebURLLoaderImpl, String message);

    private static native ByteBuffer nativeCreateBuffer(long size);
    private static native void nativeDeleteBuffer(ByteBuffer buffer);
}
