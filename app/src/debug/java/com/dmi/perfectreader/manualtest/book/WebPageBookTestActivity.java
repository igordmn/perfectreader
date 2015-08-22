package com.dmi.perfectreader.manualtest.book;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;

import com.dmi.perfectreader.book.PageBook;
import com.dmi.perfectreader.book.WebPageBook;
import com.dmi.perfectreader.manualtest.testbook.TestBookStorage;
import com.dmi.perfectreader.manualtest.testbook.TestBooks;
import com.dmi.util.base.BaseActivity;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import timber.log.Timber;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;

public class WebPageBookTestActivity extends BaseActivity implements WebPageBook.Client {
    private static final String[] TEST_BOOK = TestBooks.Extracted.LORD_OF_THE_RINGS;

    private WebPageBook pageBook;
    private PageBookView pageBookView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageBook = new WebPageBook(this, getApplicationContext());
        pageBook.load(new TestBookStorage(this, TEST_BOOK));
        pageBook.goPercent(0);
        pageBookView = new PageBookView(this);
        setContentView(pageBookView);
    }

    @Override
    protected void onDestroy() {
        pageBook.destroy();
        pageBook = null;
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        pageBook.resume();
        pageBookView.onResume();
    }

    @Override
    protected void onPause() {
        pageBookView.onPause();
        pageBook.pause();
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            pageBookView.queueEvent(() -> {
                if (pageBook.canGoPage(1) == PageBook.CanGoResult.CAN) {
                    pageBook.goNextPage();
                } else {
                    Timber.i("DDD cannot go next page");
                }
            });
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            pageBookView.queueEvent(() -> {
                if (pageBook.canGoPage(-1) == PageBook.CanGoResult.CAN) {
                    pageBook.goPreviewPage();
                } else {
                    Timber.i("DDD cannot go preview page");
                }
            });
            return  true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_VOLUME_DOWN ||
               keyCode == KeyEvent.KEYCODE_VOLUME_UP ||
               super.onKeyUp(keyCode, event);
    }

    @Override
    public void afterAnimate() {
        if (pageBookView != null) {
            pageBookView.requestRender();
        }
    }

    @Override
    public void afterLocationChanged() {
    }

    @Override
    public void handleTap() {
    }

    private class PageBookView extends GLSurfaceView implements GLSurfaceView.Renderer {
        public PageBookView(Context context) {
            super(context);
            setEGLContextClientVersion(2);
            setRenderer(this);
            setRenderMode(RENDERMODE_WHEN_DIRTY);
        }

        @Override
        public void onPause() {
            queueEvent(pageBook::glFreeResources);
            super.onPause();
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            pageBook.glInit();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            glViewport(0, 0, width, height);
            pageBook.glSetSize(width, height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            glClearColor(1.0F, 1.0F, 1.0F, 1.0F);
            glClear(GL_COLOR_BUFFER_BIT);
            if (pageBook.glCanDraw()) {
                pageBook.glDraw();
            }
        }
    }
}
