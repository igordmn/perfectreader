package com.dmi.perfectreader.book.pagebook;

import com.dmi.typoweb.TypoWebRenderer;
import com.dmi.util.opengl.GLRendererDelegate;

public class WebPageBookRenderer extends GLRendererDelegate implements PageBookRenderer {
    private final WebPageBook pageBook;

    public WebPageBookRenderer(WebPageBook pageBook) {
        super(new TypoWebRenderer(pageBook.typoWeb));
        this.pageBook = pageBook;
    }

    @Override
    public boolean isLoading() {
        return pageBook.isLoading();
    }
}