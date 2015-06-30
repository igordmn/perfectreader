package com.dmi.perfectreader.manualtest;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;

import com.dmi.perfectreader.R;
import com.dmi.typoweb.JavascriptInterface;
import com.dmi.typoweb.RenderContext;
import com.dmi.typoweb.TypoWeb;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;

public class TypoWebTestActivity extends Activity {
    private static final String LOG_TAG = TypoWebTestActivity.class.getName();
    private TypoWebView typoWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        typoWebView = new TypoWebView(this);
        setContentView(typoWebView);
    }

    @Override
    protected void onDestroy() {
        typoWebView.destroy();
        typoWebView = null;
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        typoWebView.onResume();
    }

    @Override
    protected void onPause() {
        typoWebView.onPause();
        super.onPause();
    }

    private class TypoWebView extends GLSurfaceView implements GLSurfaceView.Renderer, TypoWeb.Client {
        private TypoWeb typoWeb;
        private RenderContext renderContext;
        private boolean renderStarted = false;

        public TypoWebView(Context context) {
            super(context);

            Log.d(LOG_TAG, "DDD start init");

            typoWeb = new TypoWeb(this, context, context.getString(R.string.app_name));
            typoWeb.setURLHandler(url -> {
                if (url.startsWith("assets://")) {
                    return getAssets().open(url.substring("assets://".length()));
                } else {
                    throw new SecurityException();
                }
            });
            typoWeb.addJavascriptInterface("javaBridge", new JavaBridge());
            typoWeb.loadUrl("assets://manualtest/typoweb/main.html");

            typoWeb.execJavaScript("calledFromJava()");

            setEGLContextClientVersion(2);
            setRenderer(this);
            setRenderMode(RENDERMODE_WHEN_DIRTY);
            renderStarted = true;
        }

        public void destroy() {
            typoWeb.destroy();
            typoWeb = null;
        }

        @Override
        public void onResume() {
            typoWeb.resume();
            super.onResume();
        }

        @Override
        public void onPause() {
            queueEvent(() -> {
                renderContext.destroy();
                renderContext = null;
            });
            super.onPause();
            typoWeb.pause();
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            if (renderContext == null) {
                renderContext = new RenderContext();
            }
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            glViewport(0, 0, width, height);
            typoWeb.setSize(width, height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            glClearColor(1.0F, 1.0F, 1.0F, 1.0F);
            glClear(GL_COLOR_BUFFER_BIT);
            typoWeb.draw(renderContext);
        }

        @Override
        public boolean onTouchEvent(@NonNull MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                typoWeb.tap(event.getX(), event.getY(), event.getRawX(), event.getRawY(),
                            event.getTouchMajor(), event.getEventTime());
            }
            return true;
        }

        @Override
        public void afterAnimate() {
            if (renderStarted) {
                requestRender();
            }
        }
    }

    private class JavaBridge {
        @JavascriptInterface
        public String valueFromJava(String value) {
            return value + " йцукен";
        }

        @JavascriptInterface
        public void voidTest(String value1, String value2) {
            Log.i("typoweb", "DDD " + value1 + " " + value2);
        }
    }
}
