package com.dmi.perfectreader.bookview;

import com.dmi.perfectreader.book.config.TextAlign;

import static com.dmi.perfectreader.util.js.JavaScript.jsValue;
import static java.lang.String.format;

public class BookConfigurator {
    private PageBookView pageBookView;
    private final StringBuilder fullJs = new StringBuilder();

    BookConfigurator(PageBookView pageBookView) {
        this.pageBookView = pageBookView;
    }

    public BookConfigurator setPagePadding(int pageTopPaddingInPixels,
                                           int pageRightPaddingInPixels,
                                           int pageBottomPaddingInPixels,
                                           int pageLeftPaddingInPixels) {
        appendJs(
                format("reader.setPagePadding(%s, %s, %s, %s);",
                        jsValue(pageTopPaddingInPixels),
                        jsValue(pageRightPaddingInPixels),
                        jsValue(pageBottomPaddingInPixels),
                        jsValue(pageLeftPaddingInPixels)
                )
        );
        return this;
    }

    public BookConfigurator setTextAlign(TextAlign textAlign) {
        appendJs(
                format("reader.setTextAlign(%s);",
                        jsValue(textAlign.cssValue())
                )
        );
        return this;
    }

    public BookConfigurator setFontSize(int fontSizeInPercents) {
        appendJs(
                format("reader.setFontSize(%s);",
                        jsValue(fontSizeInPercents)
                )
        );
        return this;
    }

    public BookConfigurator setLineHeight(int lineHeightInPercents) {
        appendJs(
                format("reader.setLineHeight(%s);",
                        jsValue(lineHeightInPercents)
                )
        );
        return this;
    }

    public void commit() {
        pageBookView.resetCanGoPages();
        pageBookView.execJs(fullJs.toString());
    }

    private void appendJs(String js) {
        fullJs.append(js).append('\n');
    }
}
