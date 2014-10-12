package com.dmi.perfectreader.book.module;

import com.dmi.perfectreader.book.data.TxtBookData;
import com.dmi.perfectreader.book.font.FreetypeLibrary;
import com.dmi.perfectreader.book.format.FormatConfig;
import com.dmi.perfectreader.book.format.LineFormatter;
import com.dmi.perfectreader.book.format.PageConfig;
import com.dmi.perfectreader.book.format.PageFormatter;
import com.dmi.perfectreader.book.format.line.BreakConfig;
import com.dmi.perfectreader.book.format.line.ShapeLiner;
import com.dmi.perfectreader.book.format.line.SpaceTextBreaker;
import com.dmi.perfectreader.book.format.shape.HarfbuzzShaper;
import com.dmi.perfectreader.book.pagebook.BuildPageBook;
import com.dmi.perfectreader.book.pagebook.PageBook;
import com.dmi.perfectreader.book.pagebook.PageBookView;
import com.dmi.perfectreader.book.pageprovider.BuildPageProvider;
import com.dmi.perfectreader.book.pagesbuilder.LazyPagesBuilder;

import java.io.File;

public class PageBookModule {
    private static final BreakConfig breakConfig = new BreakConfig(new char[]{' ', '\n'}, '-');

    private File bookFile;
    private FormatConfig formatConfig;
    private PageConfig pageConfig;
    private FreetypeLibrary freetypeLibrary;
    private int maxRelativeIndex;

    private BuildPageBook pageBook;
    private PageBookView bookView;

    public PageBookModule bookFile(File bookFile) {
        this.bookFile = bookFile;
        return this;
    }


    public PageBookModule formatConfig(FormatConfig formatConfig) {
        this.formatConfig = formatConfig;
        return this;
    }

    public PageBookModule pageConfig(PageConfig pageConfig) {
        this.pageConfig = pageConfig;
        return this;
    }

    public PageBookModule freetypeLibrary(FreetypeLibrary freetypeLibrary) {
        this.freetypeLibrary = freetypeLibrary;
        return this;
    }

    public PageBookModule maxRelativeIndex(int maxRelativeIndex) {
        this.maxRelativeIndex = maxRelativeIndex;
        return this;
    }

    public PageBookModule build() {
        TxtBookData bookData = new TxtBookData(bookFile);
        SpaceTextBreaker textBreaker = new SpaceTextBreaker();
        HarfbuzzShaper shaper = new HarfbuzzShaper(freetypeLibrary);
        ShapeLiner liner = new ShapeLiner(breakConfig, shaper, textBreaker);
        LineFormatter lineFormatter = new LineFormatter(pageConfig, formatConfig, liner);
        PageFormatter pageFormatter = new PageFormatter(pageConfig, lineFormatter);
        BuildPageProvider pageProvider = new BuildPageProvider(bookData, pageFormatter);
        LazyPagesBuilder pagesBuilder = new LazyPagesBuilder(pageProvider);
        pageBook = new BuildPageBook(bookView, pagesBuilder, maxRelativeIndex);
        return this;
    }

    public PageBook pageBook() {
        return pageBook;
    }

    public PageBookModule bookView(PageBookView bookView) {
        this.bookView = bookView;
        return this;
    }
}
