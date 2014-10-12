package com.dmi.perfectreader.book.pagebook;

import com.dmi.perfectreader.book.content.Content;
import com.dmi.perfectreader.book.pagesbuilder.PagesBuilder;
import com.dmi.perfectreader.book.position.Position;
import com.dmi.perfectreader.util.concurrent.InterruptibleRunnable;
import com.dmi.perfectreader.util.concurrent.SingleTaskExecutor;

import java.io.IOException;

import javax.annotation.concurrent.ThreadSafe;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

@ThreadSafe
public class BuildPageBook implements PageBook {
    private final PageBookView pageBookView;
    private final PagesBuilder pagesBuilder;

    private final SingleTaskExecutor singleTaskExecutor = new SingleTaskExecutor();
    private final Pages pages;
    private Position position;

    public BuildPageBook(PageBookView pageBookView, PagesBuilder pagesBuilder, int maxRelativeIndex) {
        checkArgument(maxRelativeIndex >= 1);
        this.pageBookView = pageBookView;
        this.pagesBuilder = pagesBuilder;
        pages = new Pages(maxRelativeIndex);
        singleTaskExecutor.setPriority(Thread.MIN_PRIORITY);
    }

    @Override
    public void goPosition(Position position) {
        this.position = position;
        pages.setMiddle(position);
        pageBookView.setPages(pages);
        reBuild();
    }

    @Override
    public void tryGoNext() {
        checkState(position != null, "Need go position first");
        Content nextPage = pages.get(1);
        if (nextPage != null) {
            position = nextPage.range().begin();
            pages.setMiddle(position);
            pageBookView.moveNext(pages);
            reBuild();
        }
    }

    @Override
    public void tryGoPreview() {
        checkState(position != null, "Need go position first");
        Content previewPage = pages.get(-1);
        if (previewPage != null) {
            position = previewPage.range().begin();
            pages.setMiddle(position);
            pageBookView.movePreview(pages);
            reBuild();
        }
    }

    @Override
    public Position position() {
        checkState(position != null, "Need go position first");
        return position;
    }

    private void reBuild() {
        singleTaskExecutor.execute(new InterruptibleRunnable() {
            @Override
            public void run() throws InterruptedException {
                try {
                    pagesBuilder.build(position, pages);
                    pageBookView.setPages(pages);
                } catch (IOException e) {
                    throw new RuntimeException("Ошибка при построении книги", e);
                }
            }
        });
    }
}
