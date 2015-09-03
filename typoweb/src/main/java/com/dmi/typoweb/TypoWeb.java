package com.dmi.typoweb;

import android.annotation.SuppressLint;
import android.content.Context;

import com.dmi.util.natv.UsedByNative;
import com.google.common.base.Joiner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import java8.util.J8Arrays;
import timber.log.Timber;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.glBindBuffer;
import static com.dmi.typoweb.TypoWebLibrary.mainThread;
import static com.dmi.util.TypeConverters.stringToType;
import static com.dmi.util.TypeConverters.typeToString;
import static com.dmi.util.opengl.Graphics.getFrameBufferBinding;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.lang.Math.max;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;

@NotThreadSafe
public class TypoWeb {
    private static final double FPS = 60;

    private static boolean instanceCreated = false;

    private final Client client;
    long nativeTypoWeb = 0;

    private boolean loading = false;
    private boolean destroyed = false;
    private final List<String> delayedScripts = new ArrayList<>();

    private final Invalidator invalidator = new Invalidator();
    private final Picture picture = new Picture();

    public TypoWeb(Client client, Context context, String userAgent) {
        checkState(!instanceCreated, "only single instance of TypoWeb allowed");
        instanceCreated = true;

        this.client = client;

        TypoWebLibrary.checkInit(context, userAgent);
        mainThread().postTask(
                () -> nativeTypoWeb = nativeCreateTypoWeb(this, context.getResources().getDisplayMetrics().density)
        );
    }

    public void destroy() {
        checkNotDestroyed();
        destroyed = true;
        picture.recycle();
        invalidator.destroy();
        mainThread().postTask(() -> {
            nativeDestroyTypoWeb(nativeTypoWeb);
            nativeTypoWeb = 0;
        });
        TypoWebLibrary.setURLHandler(null);
        TypoWebLibrary.setHyphenationPatternsLoader(null);
        TypoWebLibrary.setHangingPunctuationConfig(null);
        instanceCreated = false;
    }

    public void pause() {
        checkNotDestroyed();
        invalidator.pause();
        TypoWebLibrary.pause();
    }

    public void resume() {
        checkNotDestroyed();
        TypoWebLibrary.resume();
        invalidator.resume();
    }

    public void setURLHandler(URLHandler urlHandler) {
        checkNotDestroyed();
        TypoWebLibrary.setURLHandler(urlHandler);
    }

    public void setHyphenationPatternsLoader(HyphenationPatternsLoader loader) {
        checkNotDestroyed();
        TypoWebLibrary.setHyphenationPatternsLoader(loader);
    }

    public void setHangingPunctuationConfig(HangingPunctuationConfig config) {
        checkNotDestroyed();
        TypoWebLibrary.setHangingPunctuationConfig(config);
    }

    public void resize(float width, float height) {
        checkNotDestroyed();
        mainThread().postTask(() -> nativeResize(nativeTypoWeb, width, height));
    }

    public void loadUrl(String url) {
        checkNotDestroyed();
        mainThread().postTask(() -> {
            delayedScripts.clear();
            loading = true;
            nativeLoadUrl(nativeTypoWeb, url);
        });
    }

    public void execJavaScript(String javascript) {
        checkNotDestroyed();
        mainThread().postTask(() -> {
            if (loading) {
                delayedScripts.add(javascript);
            } else {
                nativeLoadUrl(nativeTypoWeb, "javascript:" + javascript);
            }
        });
    }

    public void tap(float x, float y, float rawX, float rawY, float tapDiameter, float eventTimeMillis) {
        checkNotDestroyed();
        mainThread().postTask(
                () -> nativeTap(nativeTypoWeb, x, y, rawX, rawY, tapDiameter, eventTimeMillis / 1000F)
        );
    }

    public void setSelection(float x1, float y1, float x2, float y2) {
        checkNotDestroyed();
        // todo реализовать. см WebFrame.h
        throw new UnsupportedOperationException();
    }

    public void start() {
        checkNotDestroyed();
    }

    public void stop() {
        checkNotDestroyed();
    }

    /**
     * Should called before loadUrl. Otherwise interface won't be visible
     */
    public void addJavascriptInterface(final String name, final Object object) {
        checkNotDestroyed();
        mainThread().postTask(new Runnable() {
            @Override
            public void run() {
                Method[] methods = getAnnotatedMethods(object, JavascriptInterface.class);
                String[] functionNames = getMethodNames(methods);
                nativeAddJavascriptInterface(nativeTypoWeb, name, functionNames, object, methods);
            }

            private Method[] getAnnotatedMethods(Object object, Class<? extends Annotation> annotation) {
                return J8Arrays.stream(object.getClass().getDeclaredMethods())
                               .filter(method -> method.isAnnotationPresent(annotation))
                               .toArray(Method[]::new);
            }

            private String[] getMethodNames(Method[] methods) {
                return J8Arrays.stream(methods)
                               .map(Method::getName)
                               .toArray(String[]::new);
            }
        });
    }

    /**
     * Should called from GL thread
     */
    void draw(RenderContext renderContext) {
        checkState(WebThreadImpl.current() != mainThread(), "Draw shouldn't called from main web thread");
        picture.draw(renderContext);
    }

    @UsedByNative
    private void scheduleAnimate() {
        invalidator.scheduleInvalidate();
    }

    @UsedByNative
    private void didFinishLoad() {
        loading = false;
        for (String delayedScript : delayedScripts) {
            nativeLoadUrl(nativeTypoWeb, "javascript:" + delayedScript);
        }
        delayedScripts.clear();
    }

    @UsedByNative
    @SuppressLint("NewApi")
    private static String callObjectMethod(Object object, Method method, String[] strArguments) {
        try {
            Class<?>[] parameterTypes = method.getParameterTypes();
            Class<?> resultType = method.getReturnType();
            checkArgument(strArguments.length == parameterTypes.length);
            Object[] arguments = new Object[strArguments.length];
            for (int i = 0; i < strArguments.length; i++) {
                arguments[i] = stringToType(strArguments[i], parameterTypes[i]);
            }
            Object result = method.invoke(object, arguments);
            return typeToString(result, resultType);
        } catch (Exception e) {
            Timber.e(e, "Error call method. method name %s; arguments: %s", method.getName(), formatArguments(strArguments));
            return null;
        }
    }

    private static String formatArguments(String[] arguments) {
        String[] formattedArguments = new String[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            formattedArguments[i] = arguments[i] == null ? "null" : format("'%s'", arguments[i]);
        }
        return Joiner.on(", ").join(formattedArguments);
    }

    private void checkNotDestroyed() {
        checkState(!destroyed, "Already destroyed");
    }

    private native static long nativeCreateTypoWeb(TypoWeb typoWeb, float deviceDensity);
    private native static void nativeDestroyTypoWeb(long nativeTypoWeb);
    private native static void nativeResize(long nativeTypoWeb, float width, float height);
    private native static void nativeLayout(long nativeTypoWeb);
    private native static void nativeBeginFrame(long nativeTypoWeb, double frameTimeSeconds, double deadline, double interval);
    private native static void nativeLoadUrl(long nativeTypoWeb, String url);
    private native static void nativeTap(long nativeTypoWeb, float x, float y, float rawX, float rawY,
                                         float tapDiameter, float eventTimeSeconds);
    private native static long nativeRecordPicture(long nativeTypoWeb);
    private native static void nativeDestroyPicture(long nativePicture);
    private native static void nativeDrawPicture(long nativeRenderContext,
                                                 int frameBufferId, long nativeCurrentPicture);
    private native static void nativeAddJavascriptInterface(long nativeTypoWeb,
                                                            String objectName, String[] functionNames,
                                                            Object javaObject, Method[] javaMethods);

    private class Invalidator {
        private boolean destroyed = false;
        private boolean paused = false;

        private long lastInvalidateTime = -1;
        private boolean invalidateScheduled = false;
        private final Runnable invalidateTask = this::invalidate;

        public synchronized void pause() {
            paused = true;
        }

        public synchronized void resume() {
            paused = false;
            scheduleInvalidate();
        }

        public synchronized void destroy() {
            destroyed = true;
        }

        public synchronized void scheduleInvalidate() {
            if (!invalidateScheduled && !paused) {
                invalidateScheduled = true;
                long lastInvalidateInterval = currentTimeMillis() - lastInvalidateTime;
                long frameInterval = (long) (1000 / FPS);
                long invalidateDelay = max(frameInterval - lastInvalidateInterval, 0);
                mainThread().postDelayedTask(invalidateTask, invalidateDelay);
            }
        }

        private synchronized void invalidate() {
            if (!destroyed) {
                invalidateScheduled = false;
                lastInvalidateTime = currentTimeMillis();
                picture.invalidate();
                client.afterAnimate();
            }
        }
    }

    private class Picture {
        private long nativeCurrentPicture = 0;

        // called from client thread (usually it is android UI thread)
        public synchronized void recycle(){
            if (nativeCurrentPicture != 0) {
                nativeDestroyPicture(nativeCurrentPicture);
                nativeCurrentPicture = 0;
            }
        }

        // called from GL thread
        public synchronized void draw(RenderContext renderContext) {
            renderContext.checkCanUse();
            if (nativeCurrentPicture != 0) {
                nativeDrawPicture(renderContext.nativeRenderContext, getFrameBufferBinding(), nativeCurrentPicture);
            }

            // necessary because skia don't cleanup buffer binding
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        }

        // called from main web thread
        public synchronized void invalidate() {
            nativeBeginFrame(nativeTypoWeb, currentTimeMillis() / 1000.0, 0, 1 / FPS);
            nativeLayout(nativeTypoWeb);
            recycle();
            nativeCurrentPicture = nativeRecordPicture(nativeTypoWeb);
        }
    }

    public interface Client {
        /**
         * Called from another thread (not thread in which client call public methods of typoweb).
         * Should execute as quickly as possible
         */
        void afterAnimate();
    }
}
