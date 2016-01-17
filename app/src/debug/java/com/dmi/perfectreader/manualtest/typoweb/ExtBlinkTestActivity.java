package com.dmi.perfectreader.manualtest.typoweb;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MotionEvent;

import com.dmi.perfectreader.R;
import com.dmi.perfectreader.book.TexHyphenationPatternsLoader;
import com.dmi.typoweb.HangingPunctuationConfig;
import com.dmi.typoweb.TypoWeb;
import com.dmi.typoweb.TypoWebRenderer;
import com.dmi.util.base.BaseActivity;
import com.dmi.util.opengl.GLRendererDelegate;
import com.dmi.util.opengl.GLSurfaceViewExt;

import java.io.FileInputStream;
import java.net.URL;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;

// NOTE: pause, resume, destroy not calling because we are not test it
public class ExtBlinkTestActivity extends BaseActivity {
    private static final String FILE = "assets://manualtest/pagebook/paginator-test.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        setContentView(new TypoWebView(this));
    }

    private class TypoWebView extends GLSurfaceViewExt implements TypoWeb.Client {
        private TypoWeb typoWeb;

        public TypoWebView(Context context) {
            super(context);

            typoWeb = new TypoWeb(this, context, context.getString(R.string.app_name));
            typoWeb.setURLHandler(url -> {
                if (url.startsWith("http://")) {
                    return new URL(url).openStream();
                } else if (url.startsWith("assets://")) {
                    return getAssets().open(url.substring("assets://".length()));
                } else if (url.startsWith("file://")) {
                    return new FileInputStream(url.substring("file://".length()));
                } else {
                    throw new SecurityException();
                }
            });
            typoWeb.setHangingPunctuationConfig(
                    HangingPunctuationConfig.builder()
                                            .startChar('(', 1.0F)
                                            .startChar('\"', 1.0F)
                                            .endChar(')', 1.0F)
                                            .endChar('\'', 1.0F)
                                            .endChar(',', 1.0F)
                                            .endChar('.', 1.0F)
                                            .endChar('-', 1.0F)
                                            .build()
            );
            typoWeb.setHyphenationPatternsLoader(new TexHyphenationPatternsLoader(context));
            typoWeb.loadUrl(FILE);

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
        }
    }
}
