package com.dmi.perfectreader.manualtest;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MotionEvent;

import com.dmi.perfectreader.R;
import com.dmi.perfectreader.book.TexHyphenationPatternsLoader;
import com.dmi.typoweb.RenderContext;
import com.dmi.typoweb.TypoWeb;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;

// NOTE: pause, resume, destroy not calling because we are not test it
public class ExtBlinkTestActivity extends Activity {
    @SuppressWarnings("unused")
    private static final String HANGING_PUNCTUATION = "assets://manualtest/extBlink/hangingpunctuation.html";
    @SuppressWarnings("unused")
    private static final String HYPHENS = "assets://manualtest/extBlink/hyphens.html";
    private static final String FILE = HYPHENS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        setContentView(new TypoWebView(this));
    }

    private class TypoWebView extends GLSurfaceView implements GLSurfaceView.Renderer, TypoWeb.Client {
        private TypoWeb typoWeb;
        private RenderContext renderContext;

        public TypoWebView(Context context) {
            super(context);

            typoWeb = new TypoWeb(this, context, context.getString(R.string.app_name));
            typoWeb.setURLHandler(url -> {
                if (url.startsWith("assets://")) {
                    return getAssets().open(url.substring("assets://".length()));
                } else {
                    throw new SecurityException();
                }
            });
            typoWeb.setHyphenationPatternsLoader(new TexHyphenationPatternsLoader(context));
            typoWeb.loadUrl(FILE);

            setEGLContextClientVersion(2);
            setRenderer(this);
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            renderContext = new RenderContext();
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
        public void afterAnimate() {
        }

        @Override
        public boolean onTouchEvent(@NonNull MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                typoWeb.tap(event.getX(), event.getY(), event.getRawX(), event.getRawY(),
                            event.getTouchMajor(), event.getEventTime());
            }
            return true;
        }
    }
}
