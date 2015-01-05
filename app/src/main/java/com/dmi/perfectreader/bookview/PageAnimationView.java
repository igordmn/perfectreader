package com.dmi.perfectreader.bookview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.SurfaceTexture;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.view.Surface;

import com.dmi.perfectreader.R;
import com.dmi.perfectreader.book.animation.PageAnimation;
import com.dmi.perfectreader.book.config.BookLocation;
import com.dmi.perfectreader.util.collection.DuplexBuffer;
import com.dmi.perfectreader.util.concurrent.Waiter;
import com.dmi.perfectreader.util.opengl.DeltaTimeSurfaceView;
import com.dmi.perfectreader.util.opengl.Graphics;

import java.nio.FloatBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

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
import static com.dmi.perfectreader.util.opengl.GLObjects.glGenTexture;
import static com.dmi.perfectreader.util.opengl.Graphics.floatBuffer;
import static com.google.common.base.Preconditions.checkState;
import static java.lang.Math.abs;

public class PageAnimationView extends DeltaTimeSurfaceView {
    private final static int MAX_DISTANCE_IN_PAGES = 4;

    private PageAnimation pageAnimation;
    private PagesDrawer pagesDrawer;

    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    private float screenWidth;

    private BookLocation currentLocation = null;
    private final DuplexBuffer<Page> pages = new DuplexBuffer<>(MAX_DISTANCE_IN_PAGES);
    private final DuplexBuffer<Page> pagesForDraw = new DuplexBuffer<>(MAX_DISTANCE_IN_PAGES);

    private final RefreshService refreshService = new RefreshService();

    public PageAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
        initRender();
        for (int i = -MAX_DISTANCE_IN_PAGES; i <= MAX_DISTANCE_IN_PAGES; i++) {
            pages.set(i, new Page());
        }
    }

    public void setPageAnimation(PageAnimation pageAnimation) {
        this.pageAnimation = pageAnimation;
    }

    public void setPagesDrawer(PagesDrawer pagesDrawer) {
        this.pagesDrawer = pagesDrawer;
    }

    public void moveLocation(BookLocation currentLocation) {
        synchronized (pages) {
            this.currentLocation = currentLocation;
            for (int i = -pages.maxRelativeIndex(); i <= pages.maxRelativeIndex() - 1; i++) {
                pages.get(i).clear();
            }
            pageAnimation.reset();
        }
        resumeDrawing();
    }

    public void moveNext(BookLocation currentLocation) {
        synchronized (pages) {
            this.currentLocation = currentLocation;
            Page firstPage = pages.get(-pages.maxRelativeIndex());
            for (int i = -pages.maxRelativeIndex(); i <= pages.maxRelativeIndex() - 1; i++) {
                pages.set(i, pages.get(i + 1));
            }
            pages.set(pages.maxRelativeIndex(), firstPage);
            pages.get(pages.maxRelativeIndex()).clear();
            pageAnimation.moveNext();
        }
        resumeDrawing();
    }

    public void movePreview(BookLocation currentLocation) {
        synchronized (pages) {
            this.currentLocation = currentLocation;
            Page lastPage = pages.get(pages.maxRelativeIndex());
            for (int i = pages.maxRelativeIndex(); i >= -pages.maxRelativeIndex() + 1; i--) {
                pages.set(i, pages.get(i - 1));
            }
            pages.set(-pages.maxRelativeIndex(), lastPage);
            pages.get(-pages.maxRelativeIndex()).clear();
            pageAnimation.movePreview();
        }
        resumeDrawing();
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

        for (int i = -MAX_DISTANCE_IN_PAGES; i <= MAX_DISTANCE_IN_PAGES; i++) {
            Page page = pages.get(i);
            page.init();
        }
    }

    @Override
    protected void onSurfaceChanged(int width, int height) {
        screenWidth = width;
        pageAnimation.setPageWidth(width);

        glViewport(0, 0, width, height);

        Matrix.orthoM(projectionMatrix, 0, 0f, width, height, 0.0f, -1, 1);
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        for (int i = -MAX_DISTANCE_IN_PAGES; i <= MAX_DISTANCE_IN_PAGES; i++) {
            Page page = pages.get(i);
            page.setSize(width, height);
        }
    }

    @Override
    protected void onDrawFrame(float dt) {
        pageAnimation.update(dt);

        glClear(GL_COLOR_BUFFER_BIT);

        synchronized (pages) {
            for (int i = -MAX_DISTANCE_IN_PAGES; i <= MAX_DISTANCE_IN_PAGES; i++) {
                pagesForDraw.set(i, pages.get(i));
            }
        }

        pageAnimation.drawPages(new PageAnimation.PageDrawer() {
            @Override
            public void drawPage(int relativeIndex, float posX) {
                if (abs(relativeIndex) <= MAX_DISTANCE_IN_PAGES) {
                    Matrix.translateM(viewMatrix, 0, posX, 0, 0);
                    Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
                    pagesForDraw.get(relativeIndex).draw();
                    Matrix.translateM(viewMatrix, 0, -posX, 0, 0);
                }
            }
        }, screenWidth);

        if (!pageAnimation.isPagesMoving()) {
            pauseDrawing();
        }
    }

    private class Page {
        private final Plane plane = new Plane();
        private int textureId = -1;
        private Surface surface = null;
        private SurfaceTexture surfaceTexture = null;
        private final Object drawMutex = new Object();
        private final AtomicBoolean isBlank = new AtomicBoolean(true);

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

            if (!isBlank.get()) {
                glActiveTexture(GL_TEXTURE0);
                glBindTexture(GL_TEXTURE_EXTERNAL_OES, textureId);
                plane.draw(viewProjectionMatrix);
                glBindTexture(GL_TEXTURE_EXTERNAL_OES, 0);
            }
        }

        public void refreshByPage(BookLocation location, int relativeIndex) {
            synchronized (drawMutex) {
                Canvas canvas = surface.lockCanvas(null);
                try {
                    pagesDrawer.drawPage(location, relativeIndex, canvas);
                } finally {
                    surface.unlockCanvasAndPost(canvas);
                }
            }
            isBlank.set(false);
            resumeDrawing();
        }

        public void clear() {
            isBlank.set(true);
            resumeDrawing();
        }
    }

    private class Plane {
        private final static int VERTEX_COUNT = 4;

        private int programId;
        private int coordinateHandle;
        private int mvpMatrixHandle;
        private int textureHandle;
        private FloatBuffer vertexBuffer;

        // todo вынести programId в верхний класс
        public void init() {
            programId = Graphics.createProgram(
                    getResources(),
                    R.raw.shader_webview_snapshot_vertex,
                    R.raw.shader_webview_snapshot_fragment);

            coordinateHandle = glGetAttribLocation(programId, "coordinate");
            mvpMatrixHandle = glGetUniformLocation(programId, "mvpMatrix");
            textureHandle = glGetUniformLocation(programId, "texture");
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
            glUseProgram(programId);

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
        private Waiter refreshWaiter = new Waiter();

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
                try {
                    while (!Thread.interrupted()) {
                        refreshWaiter.waitRequest();

                        if (currentLocation != null) {
                            Page currentPage;
                            Page nextPage;
                            Page previewPage;
                            BookLocation refreshLocation;

                            synchronized (pages) {
                                refreshLocation = currentLocation;
                                currentPage = pages.get(0);
                                nextPage = pages.get(1);
                                previewPage = pages.get(-1);
                            }

                            currentPage.refreshByPage(refreshLocation, 0);
                            nextPage.refreshByPage(refreshLocation, 1);
                            previewPage.refreshByPage(refreshLocation, -1);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
