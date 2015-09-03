package com.dmi.perfectreader.manualtest.book;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.ViewGroup.LayoutParams;

import com.dmi.perfectreader.book.animation.SlidePageAnimation;
import com.dmi.perfectreader.book.pagebook.PageBookRenderer;
import com.dmi.perfectreader.book.pagebook.PageBookView;
import com.dmi.util.base.BaseActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import timber.log.Timber;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;

public class PageBookViewTestActivity extends BaseActivity {
    private TestPageBook pageBook;
    private PageBookView pageBookView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageBook = new TestPageBook();
        pageBookView = new PageBookView(this);
        pageBookView.setClient(new PageBookViewClient());
        pageBookView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        pageBookView.setPageAnimation(new SlidePageAnimation(1));
        pageBookView.setRenderer(pageBook);
        setContentView(pageBookView);
    }

    @Override
    protected void onDestroy() {
        pageBook.stop();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (pageBook.canGoPage(pageBookView.currentPageRelativeIndex() + 1)) {
                pageBookView.goNextPage();
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (pageBook.canGoPage(pageBookView.currentPageRelativeIndex() - 1)) {
                pageBookView.goPreviewPage();
            }
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

    private void afterAnimate() {
        if (pageBookView != null) {
            pageBookView.refresh();
        }
    }

    private class PageBookViewClient implements PageBookView.Client {
        @Override
        public void resize(int width, int height) {
        }

        @Override
        public int synchronizeCurrentPage(int currentPageRelativeIndex) {
            if (currentPageRelativeIndex < 0) {
                if (pageBook.canGoPage(1)) {
                    pageBook.goNextPage();
                    return currentPageRelativeIndex + 1;
                } else {
                    return 0;
                }
            } else if (currentPageRelativeIndex > 0) {
                if (pageBook.canGoPage(-1)) {
                    pageBook.goPreviewPage();
                    return currentPageRelativeIndex - 1;
                } else {
                    return 0;
                }
            }
            return currentPageRelativeIndex;
        }
    }

    private class TestPageBook implements PageBookRenderer {
        private final ExecutorService executor = Executors.newSingleThreadExecutor();

        private float color = 1.0F;
        private boolean colorDecreasing = true;
        private long previewTime = -1;
        private final int maxPages = 20;
        private int currentPage = 0;
        private volatile int loadCount = 0;
        private volatile boolean stopped = false;

        public TestPageBook() {
            executor.execute(this::updateLoop);
        }

        public void stop() {
            stopped = true;
        }

        public boolean canGoPage(int offset) {
            int targetPage = currentPage + offset;
            return targetPage >= 0 && targetPage < maxPages - 1;
        }

        public void goNextPage() {
            if (canGoPage(1)) {
                loadCount++;
                currentPage++;
                executor.execute(() -> {
                    delay();
                    loadCount--;
                });
            } else {
                Timber.w("DDD cannot go next page");
            }
        }

        public void goPreviewPage() {
            if (canGoPage(-1)) {
                loadCount++;
                currentPage--;
                executor.execute(() -> {
                    delay();
                    loadCount--;
                });
            } else {
                Timber.w("DDD cannot go preview page");
            }
        }

        private void updateLoop() {
            long nowTime = System.nanoTime();
            float dt = previewTime != -1 ? (nowTime - previewTime) / 1E9F : 0;
            previewTime = nowTime;
            update(dt);
            if (!stopped) {
                executor.execute(this::updateLoop);
            }
        }

        private void update(float dt) {
            if (colorDecreasing) {
                color -= dt * 0.3;
                if (color < 0.5F) {
                    color = 0.5F;
                    colorDecreasing = false;
                }
            } else {
                color += dt * 0.3;
                if (color > 1) {
                    color = 1;
                    colorDecreasing = true;
                }
            }
            delay();
            afterAnimate();
        }

        @Override
        public void onSurfaceCreated() {
        }

        @Override
        public void onSurfaceChanged(int width, int height) {
        }

        @Override
        public void onFreeResources() {
        }

        @Override
        public void onDrawFrame() {
            switch (currentPage % 3) {
                case 0:
                    glClearColor(color, 0.0F, 0.0F, 1.0F);
                    glClear(GL_COLOR_BUFFER_BIT);
                    break;
                case 1:
                    glClearColor(0.0F, color, 0.0F, 1.0F);
                    glClear(GL_COLOR_BUFFER_BIT);
                    break;
                case 2:
                    glClearColor(0.0F, 0.0F, color, 1.0F);
                    glClear(GL_COLOR_BUFFER_BIT);
                    break;
            }
            delay();
        }

        private void delay() {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        @Override
        public boolean isLoading() {
            return loadCount > 0;
        }
    }
}
