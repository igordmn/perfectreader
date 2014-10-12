package com.dmi.perfectreader.book;

import android.content.Context;
import android.opengl.Matrix;

import com.dmi.perfectreader.book.animation.PageAnimation;
import com.dmi.perfectreader.book.content.Content;
import com.dmi.perfectreader.book.content.Text;
import com.dmi.perfectreader.book.font.FreetypeLibrary;
import com.dmi.perfectreader.book.pagebook.PageBookView;
import com.dmi.perfectreader.book.pagebook.Pages;
import com.dmi.perfectreader.graphic.AlphaColorPlane;
import com.dmi.perfectreader.graphic.GLText;
import com.dmi.perfectreader.util.collection.DuplexBuffer;
import com.dmi.perfectreader.util.opengl.DeltaTimeRenderer;
import com.dmi.perfectreader.util.opengl.RenderRequester;

import javax.annotation.concurrent.ThreadSafe;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.GL_UNPACK_ALIGNMENT;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glPixelStorei;
import static android.opengl.GLES20.glViewport;
import static java.lang.Math.abs;

@ThreadSafe
public class AndroidPageBookView implements DeltaTimeRenderer, PageBookView {
    private final Context context;
    private final FreetypeLibrary freetypeLibrary;
    private final PageAnimation pageAnimation;
    private final int maxRelativeIndex;
    private RenderRequester renderRequester;

    private final DuplexBuffer<Content> pages;
    private final PageTexts pageTexts;
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    private float screenWidth;

    public AndroidPageBookView(Context context, FreetypeLibrary freetypeLibrary,
                               PageAnimation pageAnimation, int maxRelativeIndex) {
        this.context = context;
        this.freetypeLibrary = freetypeLibrary;
        this.pageAnimation = pageAnimation;
        this.maxRelativeIndex = maxRelativeIndex;

        pages = new DuplexBuffer<>(maxRelativeIndex);
        pageTexts = new PageTexts(maxRelativeIndex);
    }

    @Override
    public void setRequester(RenderRequester renderRequester) {
        this.renderRequester = renderRequester;
    }

    @Override
    public void moveNext(Pages pages) {
        copyPages(pages);
        pageAnimation.moveNext();
        renderRequester.requestRender();
    }

    @Override
    public void movePreview(Pages pages) {
        copyPages(pages);
        pageAnimation.movePreview();
        renderRequester.requestRender();
    }

    @Override
    public void setPages(Pages pages) {
        copyPages(pages);
        renderRequester.requestRender();
    }

    private void copyPages(Pages pages) {
        synchronized (this.pages) {
            pages.get(this.pages);
        }
    }

    @Override
    public void onSurfaceCreated() {
        glClearColor(1, 1, 1, 1);

        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        pageTexts.init(context, freetypeLibrary);
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        this.screenWidth = width;
        pageAnimation.setPageWidth(width);

        pageTexts.setSize(width, height);

        glViewport(0, 0, width, height);

        Matrix.orthoM(projectionMatrix, 0, 0f, width, height, 0.0f, -1, 1);
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
    }

    @Override
    public void onDrawFrame(float dt) {
        if (pageAnimation.isPagesMoving()) {
            renderRequester.requestRender();
        }

        pageAnimation.update(dt);

        glClear(GL_COLOR_BUFFER_BIT);
        synchronized (pages) {
            pageTexts.setPages(pages);
        }
        pageAnimation.drawPages(new PageAnimation.PageDrawer() {
            @Override
            public void drawPage(int relativeIndex, float posX) {
                if (abs(relativeIndex) <= maxRelativeIndex) {
                    GLText glText = pageTexts.get(relativeIndex);
                    Matrix.translateM(viewMatrix, 0, posX, 0, 0);
                    // todo валится Fatal signal 6 (только нужно sleep здесь поставить, чтобы воспроизвести)
                    Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
                    Matrix.translateM(viewMatrix, 0, -posX, 0, 0);
                    glText.draw(viewProjectionMatrix);
                }
            }
        }, screenWidth);
    }

    private static class PageTexts {
        private final DuplexBuffer<Content> pages;
        private final DuplexBuffer<GLText> pageTexts;
        private int maxRelativeIndex;
        private AlphaColorPlane plane;

        public PageTexts(int maxRelativeIndex) {
            this.maxRelativeIndex = maxRelativeIndex;
            pages = new DuplexBuffer<>(maxRelativeIndex);
            pageTexts = new DuplexBuffer<>(maxRelativeIndex);
        }

        public void init(Context context, FreetypeLibrary freetypeLibrary) {
            plane = new AlphaColorPlane(context);
            for (int i = -maxRelativeIndex; i <= maxRelativeIndex; i++) {
                GLText glText = new GLText(freetypeLibrary, plane);
                glText.init();
                pageTexts.set(i, glText);
            }
        }

        public void setSize(int width, int height) {
            plane.setSize(width, height);
            for (int i = -maxRelativeIndex; i <= maxRelativeIndex; i++) {
                Content page = pages.get(i);
                pageTexts.get(i).setSize(width, height);
                pageTexts.get(i).updateTexture(page != null ? page.text() : Text.EMPTY);
            }
        }

        public void setPages(DuplexBuffer<Content> pages) {
            for (int i = -maxRelativeIndex; i <= maxRelativeIndex; i++) {
                Content page = pages.get(i);
                if (this.pages.get(i) != page) {
                    this.pages.set(i, page);
                    GLText glText = pageTexts.get(i);
                    glText.updateTexture(page != null ? page.text() : Text.EMPTY);
                }
            }
        }

        public GLText get(int relativeIndex) {
            return pageTexts.get(relativeIndex);
        }
    }
}
