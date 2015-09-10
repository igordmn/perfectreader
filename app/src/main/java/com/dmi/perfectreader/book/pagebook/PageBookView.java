package com.dmi.perfectreader.book.pagebook;

import android.content.Context;
import android.opengl.Matrix;
import android.support.annotation.UiThread;
import android.util.AttributeSet;

import com.dmi.perfectreader.R;
import com.dmi.perfectreader.book.animation.PageAnimation;
import com.dmi.perfectreader.book.animation.PageAnimationState;
import com.dmi.util.collection.DuplexBuffer;
import com.dmi.util.opengl.DeltaTimeGLSurfaceView;

import java.nio.FloatBuffer;
import java.util.List;
import java.util.Stack;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_COLOR_ATTACHMENT0;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.GL_FRAMEBUFFER_COMPLETE;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_RGBA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.GL_UNPACK_ALIGNMENT;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glCheckFramebufferStatus;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glFramebufferTexture2D;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetError;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glPixelStorei;
import static android.opengl.GLES20.glTexImage2D;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static com.dmi.util.concurrent.Interrupts.waitTask;
import static com.dmi.util.concurrent.Threads.postUITask;
import static com.dmi.util.opengl.Graphics.createProgram;
import static com.dmi.util.opengl.Graphics.floatBuffer;
import static com.dmi.util.opengl.Graphics.glGenFramebuffer;
import static com.dmi.util.opengl.Graphics.glGenTexture;
import static java.lang.Math.abs;
import static java.lang.String.format;

public class PageBookView extends DeltaTimeGLSurfaceView {
    private static final int MAX_VISIBLE_PAGES = 32;
    private static final int MAX_VISIBLE_PAGES_WITH_CONTENT = 3;

    private Client client;
    private PageAnimation pageAnimation;
    private PageBookRenderer renderer;

    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    private final Plane plane = new Plane();

    private final DuplexBuffer<Page> visiblePages = new DuplexBuffer<>(MAX_VISIBLE_PAGES);
    private final Stack<Page> freePages = new Stack<>();
    private final List<Page> allPages = new Stack<>();

    private int currentPageRelativeIndex;
    private int animationCurrentPageRelativeIndex;

    public PageBookView(Context context) {
        super(context);
    }

    public PageBookView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @UiThread
    public void setClient(Client client) {
        this.client = client;
    }

    @UiThread
    public void setPageAnimation(PageAnimation pageAnimation) {
        this.pageAnimation = pageAnimation;
    }

    @UiThread
    public void setRenderer(PageBookRenderer renderer) {
        this.renderer = renderer;
        for (int i = 0; i < MAX_VISIBLE_PAGES_WITH_CONTENT; i++) {
            Page page = new Page();
            allPages.add(page);
            freePages.add(page);
        }
        setEGLContextClientVersion(2);
        runRender();
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        client.resize(w, h);
    }

    public int currentPageRelativeIndex() {
        return currentPageRelativeIndex;
    }

    public void refresh() {
        requestRender();
    }

    public void reset(Runnable resetter) {
        currentPageRelativeIndex = 0;
        queueEvent(() -> {
            animationCurrentPageRelativeIndex = 0;
            pageAnimation.reset();
            freeInvisiblePages();
            waitTask(postUITask(resetter::run));
        });
        requestRender();
    }

    public void goNextPage() {
        currentPageRelativeIndex--;
        queueEvent(() -> {
            animationCurrentPageRelativeIndex--;
            pageAnimation.moveNext();
            visiblePages.shiftLeft();
        });
        requestRender();
    }

    public void goPreviewPage() {
        currentPageRelativeIndex++;
        queueEvent(() -> {
            animationCurrentPageRelativeIndex++;
            pageAnimation.movePreview();
            visiblePages.shiftRight();
        });
        requestRender();
    }

    @Override
    protected void onSurfaceCreated() {
        glClearColor(1, 1, 1, 1);
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        renderer.onSurfaceCreated();
        plane.init();
        for (Page page : allPages) {
            page.init();
        }
    }

    @Override
    protected void onSurfaceChanged(int width, int height) {
        glViewport(0, 0, width, height);
        Matrix.orthoM(projectionMatrix, 0, 0f, width, height, 0.0f, -1, 1);
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        pageAnimation.setPageWidth(width);
        plane.setSize(width, height);
        for (Page page : allPages) {
            page.setSize(width, height);
        }
    }

    @Override
    protected void onFreeResources() {
        renderer.onFreeResources();
    }

    @Override
    protected void onUpdate(float dt) {
        pageAnimation.update(dt);
        if (pageAnimation.isAnimate()) {
            requestRender();
        } else {
            resetTimer();
        }
    }

    @Override
    protected void onDrawFrame() {
        synchronizeCurrentPage();
        refreshPages();
        drawPages();
    }

    private void synchronizeCurrentPage() {
        if (animationCurrentPageRelativeIndex != 0) {
            if (canSynchronizeCurrentPage()) {
                waitTask(postUITask(() -> {
                    currentPageRelativeIndex = client.synchronizeCurrentPage(currentPageRelativeIndex);
                    animationCurrentPageRelativeIndex = currentPageRelativeIndex;
                }));
            }
        }
        if (animationCurrentPageRelativeIndex != 0) {
            requestRender();
        }
    }

    private boolean canSynchronizeCurrentPage() {
        int currentIndex = animationCurrentPageRelativeIndex;
        PageAnimationState animationState = pageAnimation.state();
        boolean pageVisible = animationState.minRelativeIndex() >= currentIndex ||
                              currentIndex <= animationState.maxRelativeIndex();
        boolean pageDrawn = abs(currentIndex) > MAX_VISIBLE_PAGES ||
                            visiblePages.get(currentIndex) != null;
        return pageVisible && pageDrawn || !pageVisible;
    }

    public void refreshPages() {
        freeInvisiblePages();
        redrawCurrentPage();
    }

    private void freeInvisiblePages() {
        PageAnimationState animationState = pageAnimation.state();
        for (int i = -visiblePages.maxRelativeIndex(); i < animationState.minRelativeIndex(); i++) {
            freePage(visiblePages, i);
        }
        for (int i = visiblePages.maxRelativeIndex(); i > animationState.maxRelativeIndex(); i--) {
            freePage(visiblePages, i);
        }
    }

    private void redrawCurrentPage() {
        int currentIndex = animationCurrentPageRelativeIndex;
        if (abs(currentIndex) <= MAX_VISIBLE_PAGES && !renderer.isLoading()) {
            Page currentPage = visiblePages.get(currentIndex);
            if (currentPage == null) {
                if (!freePages.empty()) {
                    currentPage = acquirePage(visiblePages, currentIndex);
                    currentPage.refresh();
                }
            } else if (!pageAnimation.isAnimate()) {
                currentPage.refresh();
            }
        }
    }

    private void drawPages() {
        glClearColor(1.0F, 1.0F, 1.0F, 1.0F);
        glClear(GL_COLOR_BUFFER_BIT);
        PageAnimationState animationState = pageAnimation.state();
        for (int i = 0; i < animationState.pageCount(); i++) {
            int relativeIndex = animationState.pageRelativeIndex(i);
            float positionX = animationState.pagePositionX(i);
            Matrix.translateM(viewMatrix, 0, positionX, 0, 0);
            Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
            if (abs(relativeIndex) <= MAX_VISIBLE_PAGES && visiblePages.get(relativeIndex) != null) {
                visiblePages.get(relativeIndex).draw(viewProjectionMatrix);
            }
            Matrix.translateM(viewMatrix, 0, -positionX, 0, 0);
        }
    }

    private Page acquirePage(DuplexBuffer<Page> pages, int index) {
        Page page = freePages.pop();
        pages.set(index, page);
        return page;
    }

    private void freePage(DuplexBuffer<Page> pages, int index) {
        Page page = pages.get(index);
        if (page != null) {
            pages.set(index, null);
            freePages.push(page);
        }
    }

    private class Page {
        private int textureId;
        private int frameBufferId;

        public void init() {
            textureId = glGenTexture();
            frameBufferId = glGenFramebuffer();
        }

        public void setSize(int width, int height) {
            glBindTexture(GL_TEXTURE_2D, textureId);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, null);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glBindTexture(GL_TEXTURE_2D, 0);

            glBindFramebuffer(GL_FRAMEBUFFER, frameBufferId);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textureId, 0);
            if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
                throw new RuntimeException(
                        format("frame buffer init error. status: 0x%08X, error code: 0x%08X",
                               glCheckFramebufferStatus(GL_FRAMEBUFFER), glGetError())
                );
            }
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
        }

        public void refresh() {
            glBindFramebuffer(GL_FRAMEBUFFER, frameBufferId);
            glClearColor(1.0F, 1.0F, 1.0F, 1.0F);
            glClear(GL_COLOR_BUFFER_BIT);
            renderer.onDrawFrame();
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
        }

        public void draw(float[] matrix) {
            glBindTexture(GL_TEXTURE_2D, textureId);
            plane.draw(matrix);
            glBindTexture(GL_TEXTURE_2D, 0);
        }
    }

    private class Plane {
        private final static int VERTEX_COUNT = 4;

        private int programId;
        private int coordinateHandle;
        private int mvpMatrixHandle;
        private int textureHandle;

        private FloatBuffer vertexBuffer;

        public void init() {
            programId = createProgram(getResources(), R.raw.shader_page_vertex, R.raw.shader_page_fragment);
            coordinateHandle = glGetAttribLocation(programId, "coordinate");
            mvpMatrixHandle = glGetUniformLocation(programId, "mvpMatrix");
            textureHandle = glGetUniformLocation(programId, "texture");
        }

        public void setSize(int width, int height) {
            vertexBuffer = floatBuffer(new float[]{
                    0, 0, 0, 1,
                    width, 0, 1, 1,
                    0, height, 0, 0,
                    width, height, 1, 0
            });
        }

        public void draw(float[] matrix) {
            glUseProgram(programId);
            glEnableVertexAttribArray(coordinateHandle);
            glVertexAttribPointer(coordinateHandle, 4,
                                  GL_FLOAT, false,
                                  0, vertexBuffer);
            glUniformMatrix4fv(mvpMatrixHandle, 1, false, matrix, 0);
            glUniform1i(textureHandle, 0);
            glDrawArrays(GL_TRIANGLE_STRIP, 0, VERTEX_COUNT);
            glDisableVertexAttribArray(coordinateHandle);
            glUseProgram(0);
        }
    }

    public interface Client {
        void resize(int width, int height);

        int synchronizeCurrentPage(int currentPageRelativeIndex);
    }
}
