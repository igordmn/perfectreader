package com.dmi.perfectreader.manualtest.typoweb;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MotionEvent;

import com.dmi.perfectreader.R;
import com.dmi.typoweb.JavascriptInterface;
import com.dmi.typoweb.TypoWeb;
import com.dmi.typoweb.TypoWebRenderer;
import com.dmi.util.base.BaseActivity;
import com.dmi.util.opengl.GLRendererDelegate;
import com.dmi.util.opengl.GLSurfaceViewExt;

import timber.log.Timber;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;

public class TypoWebTestActivity extends BaseActivity {
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

    private class TypoWebView extends GLSurfaceViewExt implements TypoWeb.Client {
        private TypoWeb typoWeb;

        public TypoWebView(Context context) {
            super(context);

            Timber.d("DDD start init");

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
            setRenderer(new GLRendererDelegate(new TypoWebRenderer(typoWeb)) {
                @Override
                public void onSurfaceChanged(int width, int height) {
                    glViewport(0, 0, width, height);
                    super.onSurfaceChanged(width, height);
                }

                @Override
                public void onDrawFrame() {
                    glClearColor(1.0F, 1.0F, 1.0F, 1.0F);
                    glClear(GL_COLOR_BUFFER_BIT);
                    super.onDrawFrame();
                }
            });
            setRenderMode(RENDERMODE_WHEN_DIRTY);
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
            super.onPause();
            typoWeb.pause();
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            typoWeb.resize(w, h);
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
            requestRender();
        }
    }

    private class JavaBridge {
        @JavascriptInterface
        public String valueFromJava(String value) {
            return value + " йцукен";
        }

        @JavascriptInterface
        public void voidTest(String value1, String value2) {
            Timber.i("DDD " + value1 + " " + value2);
        }
    }
}
