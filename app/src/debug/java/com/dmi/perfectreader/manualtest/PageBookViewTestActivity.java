package com.dmi.perfectreader.manualtest;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup.LayoutParams;

import com.dmi.perfectreader.book.PageBook;
import com.dmi.perfectreader.book.PageBookView;
import com.dmi.perfectreader.book.animation.SlidePageAnimation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static com.google.common.base.Preconditions.checkState;
import static java.lang.Math.min;

public class PageBookViewTestActivity extends Activity {
    private PageBookImpl pageBook;
    private PageBookView pageBookView;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageBook = new PageBookImpl();
        pageBookView = new PageBookView(this);
        pageBookView.setPageAnimation(new SlidePageAnimation(1));
        pageBookView.setPageBook(pageBook);
        pageBookView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
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
            pageBookView.goNextPage();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            pageBookView.goPreviewPage();
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

    private class PageBookImpl implements PageBook {
        private float color = 1.0F;
        private boolean colorDecreasing = true;
        private long previewTime = -1;
        private final int maxPages = 20;
        private int currentPage = 0;
        private final LoadingState canDrawState = new LoadingState();
        private boolean stopped = false;

        public PageBookImpl() {
            executor.execute(this::updateLoop);
        }

        public void stop() {
            stopped = true;
        }

        @Override
        public CanGoResult canGoPage(int offset) {
            int targetPage = currentPage + offset;
            return targetPage >= 0 && targetPage < maxPages - 1 ? CanGoResult.CAN : CanGoResult.CANNOT;
        }

        @Override
        public boolean glCanDraw() {
            return canDrawState.canDraw();
        }

        @Override
        public void goPercent(int integerPercent) {
            canDrawState.beforePageGo();
            executor.execute(() -> {
                currentPage = min(maxPages * integerPercent, maxPages - 1);
                delay();
                canDrawState.afterPageGo();
            });
        }

        @Override
        public void goNextPage() {
            if (canGoPage(1) == CanGoResult.CAN) {
                canDrawState.beforePageGo();
                executor.execute(() -> {
                    currentPage++;
                    delay();
                    canDrawState.afterPageGo();
                });
            } else {
                Log.w("DDD", "cannot go next page");
            }
        }

        @Override
        public void goPreviewPage() {
            if (canGoPage(-1) == CanGoResult.CAN) {
                canDrawState.beforePageGo();
                executor.execute(() -> {
                    currentPage--;
                    delay();
                    canDrawState.afterPageGo();
                });
            } else {
                Log.w("DDD", "cannot go preview page");
            }
        }

        private void updateLoop() {
            long nowTime = System.currentTimeMillis();
            float dt = previewTime != -1 ? (nowTime - previewTime) / 1000F : 0;
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
            canDrawState.afterAnimate();
            afterAnimate();
        }

        @Override
        public void glDraw() {
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
    }

    private class LoadingState {
        private int scheduledGoes = 0;
        private boolean canDraw = true;

        public synchronized void beforePageGo() {
            canDraw = false;
            scheduledGoes++;
            checkState(scheduledGoes >= 0);
        }

        public synchronized void afterPageGo() {
            scheduledGoes--;
            checkState(scheduledGoes >= 0);
        }

        public synchronized void afterAnimate() {
            if (scheduledGoes == 0) {
                canDraw = true;
            }
        }

        public synchronized boolean canDraw() {
            return canDraw;
        }
    }
}
