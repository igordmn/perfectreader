package com.dmi.perfectreader.book.pagesbuilder;

import com.dmi.perfectreader.book.content.Content;
import com.dmi.perfectreader.book.pagebook.Pages;
import com.dmi.perfectreader.book.pageprovider.PageProvider;
import com.dmi.perfectreader.book.position.Distance;
import com.dmi.perfectreader.book.position.Position;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

import static com.dmi.perfectreader.book.position.Distance.distance;
import static com.dmi.perfectreader.util.concurrent.Interrupts.checkThreadInterrupted;

@ThreadSafe
public class LazyPagesBuilder implements PagesBuilder {
    private final PageProvider pageProvider;

    public LazyPagesBuilder(PageProvider pageProvider) {
        this.pageProvider = pageProvider;
    }

    @Override
    public synchronized void build(Position position, Pages pages) throws InterruptedException, IOException {
        List<Content> frontPages = new ArrayList<>();

        try (PageProvider.Iterator iterator = pageProvider.iterator(position)) {
            Content page = iterator.next();
            int i = 0;
            while (page != null && i <= pages.maxRelativeIndex()) {
                checkThreadInterrupted();
                frontPages.add(page);
                pages.put(page);
                page = iterator.next();
                i++;
            }
        }

        Position frontBegin = frontPages.get(0).range().begin();
        Position frontEnd = frontPages.get(frontPages.size() - 1).range().end();
        Distance frontDistance = distance(frontBegin, frontEnd);

        List<Content> backPages = new ArrayList<>();
        float backCoefficient = 1.0F;
        Position backBegin = frontBegin;
        while (backPages.size() < pages.maxRelativeIndex() && backBegin.more(Position.BEGIN)) {
            backBegin = frontBegin.minus(frontDistance.multiple(backCoefficient));
            backPages = buildPages(backBegin, frontBegin);
            backCoefficient *= 2;
        }

        for (int i = backPages.size() - 1; i >= 0; i--) {
            checkThreadInterrupted();
            pages.put(backPages.get(i));
        }
    }

    private List<Content> buildPages(Position begin, Position end) throws InterruptedException, IOException {
        List<Content> pages = new ArrayList<>();
        try (PageProvider.Iterator iterator = pageProvider.iterator(begin)) {
            Content page = iterator.next();
            while (page != null && page.range().begin().less(end)) {
                checkThreadInterrupted();
                pages.add(page);
                page = iterator.next();
            }
        }
        return pages;
    }
}
