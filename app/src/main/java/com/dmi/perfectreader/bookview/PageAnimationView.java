package com.dmi.perfectreader.bookview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.SurfaceTexture;
import android.opengl.Matrix;
import android.util.Log;
import android.view.Surface;

import com.dmi.perfectreader.R;
import com.dmi.perfectreader.book.animation.PageAnimation;
import com.dmi.perfectreader.book.animation.PageAnimationState;
import com.dmi.perfectreader.util.collection.DuplexBuffer;
import com.dmi.perfectreader.util.concurrent.Waiter;
import com.dmi.perfectreader.util.lang.Pool;
import com.dmi.perfectreader.util.opengl.DeltaTimeSurfaceView;
import com.dmi.perfectreader.util.opengl.Graphics;

import java.nio.FloatBuffer;
import java.util.concurrent.atomic.AtomicInteger;

import static android.opengl.GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_RGB;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.GL_UNPACK_ALIGNMENT;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glPixelStorei;
import static android.opengl.GLES20.glTexImage2D;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static android.util.Log.getStackTraceString;
import static com.dmi.perfectreader.util.opengl.GLObjects.glGenTexture;
import static com.dmi.perfectreader.util.opengl.Graphics.floatBuffer;
import static com.google.common.base.Preconditions.checkState;
import static java.lang.Math.abs;

public class PageAnimationView extends DeltaTimeSurfaceView {
    private static final String LOG_TAG = PageBookView.class.getSimpleName();

    private final static int MAX_DISTANCE_IN_PAGES = 2;
    private final static int BACK_BUFFER_PAGE_COUNT = 3;
    private final static int DRAWING_PAGE_COUNT = 2 * MAX_DISTANCE_IN_PAGES + 1;
    private final static int PAGE_POOL_SIZE = DRAWING_PAGE_COUNT + BACK_BUFFER_PAGE_COUNT;

    private PageAnimation pageAnimation;
    private PagesDrawer pagesDrawer;
    private Listener listener;

    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    private int planeProgramId;

    private final DuplexBuffer<PageSlot> pageSlots = new DuplexBuffer<>(MAX_DISTANCE_IN_PAGES);
    private final DuplexBuffer<Page> drawingPages = new DuplexBuffer<>(MAX_DISTANCE_IN_PAGES);
    private final Pool<Page> pagePool;
    private AtomicInteger currentPageIndex = new AtomicInteger(0);
    private final Object animationStateMutex = new Object();

    private final RefreshService refreshService = new RefreshService();

    public PageAnimationView(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        initRender();
        for (int i = -MAX_DISTANCE_IN_PAGES; i <= MAX_DISTANCE_IN_PAGES; i++) {
            pageSlots.set(i, new PageSlot());
        }
        pagePool = createPagePool();
    }

    private Pool<Page> createPagePool() {
        Page[] precreatedPages = new Page[PAGE_POOL_SIZE];
        for (int i = 0; i < PAGE_POOL_SIZE; i++) {
            precreatedPages[i] = new Page();
        }
        return new Pool<>(precreatedPages);
    }

    public void setPageAnimation(PageAnimation pageAnimation) {
        this.pageAnimation = pageAnimation;
    }

    public void setPagesDrawer(PagesDrawer pagesDrawer) {
        this.pagesDrawer = pagesDrawer;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public boolean canMoveNext() {
        synchronized (animationStateMutex) {
            PageAnimationState animationState = pageAnimation.currentState();
            int animationPageCount = animationState.pageCount();
            return animationPageCount > 0 && animationState.pageRelativeIndex(0) > -MAX_DISTANCE_IN_PAGES;
        }
    }

    public boolean canMovePreview() {
        synchronized (animationStateMutex) {
            PageAnimationState animationState = pageAnimation.currentState();
            int animationPageCount = animationState.pageCount();
            return animationPageCount > 0 &&
                   animationState.pageRelativeIndex(animationPageCount - 1) < MAX_DISTANCE_IN_PAGES;
        }
    }

    public void reset() {
        synchronized (pageSlots) {
            for (int i = -pageSlots.maxRelativeIndex(); i <= pageSlots.maxRelativeIndex() - 1; i++) {
                replaceSlotPage(i, null);
            }
            pageAnimation.reset();
        }
        resumeDrawing();
    }

    public void moveNext() {
        synchronized (pageSlots) {
            Page firstPage = pageSlots.get(-pageSlots.maxRelativeIndex()).getPage();
            for (int i = -pageSlots.maxRelativeIndex(); i <= pageSlots.maxRelativeIndex() - 1; i++) {
                pageSlots.get(i).setPage(pageSlots.get(i + 1).getPage());
            }
            pageSlots.get(pageSlots.maxRelativeIndex()).setPage(null);
            if (firstPage != null) {
                pagePool.release(firstPage);
            }
            pageAnimation.moveNext();
            currentPageIndex.incrementAndGet();
        }
        resumeDrawing();
        listener.onStartAnimation();
    }

    public void movePreview() {
        synchronized (pageSlots) {
            Page lastPage = pageSlots.get(pageSlots.maxRelativeIndex()).getPage();
            for (int i = pageSlots.maxRelativeIndex(); i >= -pageSlots.maxRelativeIndex() + 1; i--) {
                pageSlots.get(i).setPage(pageSlots.get(i - 1).getPage());
            }
            pageSlots.get(-pageSlots.maxRelativeIndex()).setPage(null);
            if (lastPage != null) {
                pagePool.release(lastPage);
            }
            pageAnimation.movePreview();
            currentPageIndex.decrementAndGet();
        }
        resumeDrawing();
        listener.onStartAnimation();
    }

    public void postRefresh() {
        refreshService.postRefresh();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        refreshService.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        refreshService.stop();
    }

    @Override
    protected void onSurfaceCreated() {
        glClearColor(1, 1, 1, 1);

        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        planeProgramId = Graphics.createProgram(
                getResources(),
                R.raw.shader_webview_snapshot_vertex,
                R.raw.shader_webview_snapshot_fragment);

        for (Page page : pagePool) {
            page.init();
        }
    }

    @Override
    protected void onSurfaceChanged(int width, int height) {
        pageAnimation.setPageWidth(width);

        glViewport(0, 0, width, height);

        Matrix.orthoM(projectionMatrix, 0, 0f, width, height, 0.0f, -1, 1);
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        for (Page page : pagePool) {
            page.setSize(width, height);
        }
    }

    @Override
    protected void onDrawFrame(float dt) {
        glClear(GL_COLOR_BUFFER_BIT);

        synchronized (pageSlots) {
            synchronized (drawingPages) {
                for (int i = -MAX_DISTANCE_IN_PAGES; i <= MAX_DISTANCE_IN_PAGES; i++) {
                    drawingPages.set(i, pageSlots.get(i).getPage());
                }
            }
        }

        boolean wasMoving = pageAnimation.isPagesMoving();

        synchronized (animationStateMutex) {
            pageAnimation.update(dt);
        }
        PageAnimationState animationState = pageAnimation.currentState();
        for (int i = 0; i < animationState.pageCount(); i++) {
            int relativeIndex = animationState.pageRelativeIndex(i);
            float positionX = animationState.pagePositionX(i);
            if (abs(relativeIndex) <= MAX_DISTANCE_IN_PAGES && drawingPages.get(relativeIndex) != null) {
                Matrix.translateM(viewMatrix, 0, positionX, 0, 0);
                Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
                drawingPages.get(relativeIndex).draw();
                Matrix.translateM(viewMatrix, 0, -positionX, 0, 0);
            }
        }

        synchronized (drawingPages) {
            drawingPages.clear();
        }

        if (!pageAnimation.isPagesMoving()) {
            pauseDrawing();

            if (wasMoving) {
                listener.onEndAnimation();
            }
        }
    }

    private void replaceSlotPage(int slotIndex, Page page) {
        PageSlot pageSlot = pageSlots.get(slotIndex);
        if (pageSlot.getPage() != null) {
            pagePool.release(pageSlot.getPage());
        }
        pageSlot.setPage(page);
    }

    private boolean isPageDrawing(Page page) {
        for (int i = -MAX_DISTANCE_IN_PAGES; i <= MAX_DISTANCE_IN_PAGES; i++) {
            if (drawingPages.get(i) == page) {
                return true;
            }
        }
        return false;
    }

    private class PageSlot {
        private Page page;

        public Page getPage() {
            return page;
        }

        public void setPage(Page page) {
            this.page = page;
        }
    }

    private class Page {
        private final Plane plane = new Plane();
        private int textureId = -1;
        private Surface surface = null;
        private SurfaceTexture surfaceTexture = null;
        private final Object drawMutex = new Object();

        public void init() {
            plane.init();
            textureId = glGenTexture();
            surfaceTexture = new SurfaceTexture(textureId);
            surface = new Surface(surfaceTexture);
        }

        public void setSize(int width, int height) {
            plane.setSize(width, height);
            surfaceTexture.setDefaultBufferSize(width, height);

            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_EXTERNAL_OES, textureId);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, null);
            glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glBindTexture(GL_TEXTURE_EXTERNAL_OES, 0);
        }

        public void draw() {
            synchronized (drawMutex) {
                surfaceTexture.updateTexImage();
            }

            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_EXTERNAL_OES, textureId);
            plane.draw(viewProjectionMatrix);
            glBindTexture(GL_TEXTURE_EXTERNAL_OES, 0);
        }

        public void refreshByPage(int relativeIndex) {
            synchronized (drawMutex) {
                Canvas canvas = surface.lockCanvas(null);
                try {
                    pagesDrawer.drawPage(relativeIndex, canvas);
                } finally {
                    surface.unlockCanvasAndPost(canvas);
                }
            }
            resumeDrawing();
        }
    }

    private class Plane {
        private final static int VERTEX_COUNT = 4;
        private int coordinateHandle;
        private int mvpMatrixHandle;
        private int textureHandle;
        private FloatBuffer vertexBuffer;

        public void init() {
            coordinateHandle = glGetAttribLocation(planeProgramId, "coordinate");
            mvpMatrixHandle = glGetUniformLocation(planeProgramId, "mvpMatrix");
            textureHandle = glGetUniformLocation(planeProgramId, "texture");
        }

        public void setSize(int width, int height) {
            vertexBuffer = floatBuffer(new float[]{
                    0, 0, 0, 0,
                    width, 0, 1, 0,
                    0, height, 0, 1,
                    width, height, 1, 1
            });
        }

        public void draw(float[] matrix) {
            glUseProgram(planeProgramId);

            glEnableVertexAttribArray(coordinateHandle);
            glVertexAttribPointer(coordinateHandle, 4,
                    GL_FLOAT, false,
                    0, vertexBuffer);
            glUniformMatrix4fv(mvpMatrixHandle, 1, false, matrix, 0);
            glUniform1i(textureHandle, 0);

            glDrawArrays(GL_TRIANGLE_STRIP, 0, VERTEX_COUNT);
        }
    }

    private class RefreshService {
        private Thread thread = null;
        private final Waiter refreshWaiter = new Waiter();

        public void postRefresh() {
            refreshWaiter.request();
        }

        public void start() {
            checkState(thread == null);
            thread = new Thread(new RefreshRunnable());
            thread.setPriority(Thread.MAX_PRIORITY);
            thread.start();
        }

        public void stop() {
            checkState(thread != null);
            thread.interrupt();
            thread = null;
        }

        private class RefreshRunnable implements Runnable {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    refreshWaiter.waitRequest();

                    int currentPageIndex = PageAnimationView.this.currentPageIndex.get();

                    Page currentPage = pagePool.acquire();
                    Page nextPage = pagePool.acquire();
                    Page previewPage = pagePool.acquire();

                    // Нужно подождать пока рисуются на экране эти страницы,
                    // иначе на экране может отобразиться не то
                    waitPageDrawing(currentPage, nextPage, previewPage);

                    PagesDrawer.BatchDraw batchDraw = pagesDrawer.batchDraw();
                    boolean drawWithoutError = true;
                    try {
                        currentPage.refreshByPage(0);
                        nextPage.refreshByPage(1);
                        previewPage.refreshByPage(-1);
                    } catch (Exception e) {
                        Log.w(LOG_TAG, getStackTraceString(e));
                        drawWithoutError = false;
                    }
                    boolean correctlyDrawn = drawWithoutError && batchDraw.endDraw();

                    if (correctlyDrawn) {
                        synchronized (pageSlots) {
                            int newPageIndex = PageAnimationView.this.currentPageIndex.get();
                            int pageOffset = currentPageIndex - newPageIndex;
                            replaceSlotPage(pageOffset, currentPage);
                            replaceSlotPage(pageOffset + 1, nextPage);
                            replaceSlotPage(pageOffset - 1, previewPage);
                        }
                        resumeDrawing();
                    } else {
                        pagePool.release(currentPage);
                        pagePool.release(nextPage);
                        pagePool.release(previewPage);
                        refreshWaiter.request();
                    }
                }
            }

            @SuppressWarnings("StatementWithEmptyBody")
            private void waitPageDrawing(Page currentPage, Page nextPage, Page previewPage) {
                while (isPageDrawing(currentPage) && isPageDrawing(nextPage) && isPageDrawing(previewPage)) {
                    // wait
                }
            }
        }
    }

    public static interface Listener {
        void onStartAnimation();

        void onEndAnimation();
    }
}
