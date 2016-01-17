"use strict";

define("paginator", function() {
    const PromiseUtils = require("utils/promise");
    const ElementUtils = require("utils/element");
    const PieceView = require("piece-view");

    const max = Math.max.bind(Math);
    const min = Math.min.bind(Math);
    const async = PromiseUtils.async;
    const documentReady = ElementUtils.documentReady;
    const insertPieceView = PieceView.insert;

    const pieceSym = Symbol();
    const piecesSym = Symbol();

    return {
        createPaginator: function(bookContent) {
            const getNextPiece = bookContent.getNextPiece.bind(bookContent);
            const getPreviousPiece = bookContent.getPreviousPiece.bind(bookContent);
            const hasNextPiece = bookContent.hasNextPiece.bind(bookContent);
            const hasPreviousPiece = bookContent.hasPreviousPiece.bind(bookContent);

            return {
                makePage: function(percent, container) {
                    return async(function*() {
                        const pageBuilder = createPageBuilder(container);
                        yield pageBuilder.init(percent);
                        yield pageBuilder.fillPageBottom();
                        yield pageBuilder.fillPageTop();
                        return pageBuilder.build();
                    });
                },

                makeNextPage: function(page, container) {
                    return async(function*() {
                        const pageBuilder = createPageBuilder(container);
                        yield pageBuilder.init(page.endPercent);
                        yield pageBuilder.fillPageBottom();
                        return pageBuilder.build();
                    });
                },

                makePreviousPage: function(page, container) {
                    return async(function*() {
                        const pageBuilder = createPageBuilder(container);
                        yield pageBuilder.init(page.beginPercent);
                        yield pageBuilder.fillPageTop();
                        yield pageBuilder.fillPageBottom();
                        return pageBuilder.build();
                    });
                },

                hasNextPage: function(page) {
                    return page.endPercent < 1.0;
                },

                hasPreviousPage: function(page) {
                    return page.beginPercent > 0.0;
                },

                releasePage: function(page) {
                    for (let piece of page[piecesSym]) {
                        bookContent.releasePiece(piece);
                    }
                },
            };

            function createPageBuilder(container) {
                assert(container.childNodes.length == 0, "page container shouldn't contain children");

                const topDiv = document.createElement("div");
                topDiv.style.webkitMarginBottomCollapse = "discard";

                const bottomDiv = document.createElement("div");
                bottomDiv.style.webkitMarginTopCollapse = "discard";

                const contentElement = document.createElement("div");

                const clipElement = document.createElement("div");
                clipElement.style.overflow = "hidden";
                clipElement.appendChild(topDiv);
                clipElement.appendChild(contentElement);
                clipElement.appendChild(bottomDiv);

                const pageElement = document.createElement("div");
                pageElement.style.width = "100%";
                pageElement.style.height = "100%";
                pageElement.style.overflow = "hidden";
                pageElement.appendChild(clipElement);

                container.appendChild(pageElement);

                const height = pageElement.clientHeight;
                const content = createContent(contentElement, height);

                assert(height > 0, "wrong height");

                function getContentRect() { return contentElement.getBoundingClientRect(); }
                function isPageOverfilled() { return getContentRect().height >= height && !content.isBlank }

                return {
                    init: function(percent) {
                        return async(function*() {
                            pageElement.classList.add("invisible-page");

                            const piece = yield bookContent.getPieceAt(percent);
                            yield content.addBottom(piece);

                            const rowIndex = bookPercentToRowIndex(content.bottom, percent);
                            content.bottom.beginIndex = rowIndex;
                            content.bottom.endIndex = rowIndex;
                        });
                    },

                    fillPageBottom: function() {
                        return async(function*() {
                            content.bottom.endIndex = content.bottom.length;

                            let piece = content.bottom[pieceSym];
                            while (!isPageOverfilled() && hasNextPiece(piece)) {
                                piece = yield getNextPiece(piece);
                                yield content.addBottom(piece);
                            }

                            const pieceView = content.bottom;
                            pieceView.endIndex = rowIndexAbove(pieceView, getContentRect().top + height);
                            if (content.isBlank) {
                                pieceView.endIndex = min(pieceView.beginIndex + 1, pieceView.length);
                            }
                        });
                    },

                    fillPageTop: function() {
                        return async(function*() {
                            content.top.beginIndex = 0;

                            let piece = content.top[pieceSym];
                            while (!isPageOverfilled() && hasPreviousPiece(piece)) {
                                piece = yield getPreviousPiece(piece);
                                yield content.addTop(piece);
                            }

                            const pieceView = content.top;
                            pieceView.beginIndex = rowIndexBelow(pieceView, getContentRect().bottom - height);
                            if (content.isBlank) {
                                pieceView.beginIndex = max(pieceView.beginIndex - 1, 0);
                            }
                        });
                    },

                    build: function() {
                        content.top && content.top.isCollapsed && content.removeTop();
                        content.bottom && content.bottom.isCollapsed && content.removeBottom();

                        pageElement.classList.remove("invisible-page");

                        const page = {
                            beginPercent: rowIndexToBookPercent(content.top, content.top.beginIndex),
                            endPercent: rowIndexToBookPercent(content.bottom, content.bottom.endIndex),
                        };
                        page[piecesSym] = content.pieces;

                        return page;
                    },
                };
            }

            function createContent(element, pageHeight) {
                const nextSym = Symbol();
                const previousSym = Symbol();

                let topView;
                let bottomView;
                let length = 0;

                return {
                    get top() { return topView },
                    get bottom() { return bottomView },
                    get length() { return length },
                    get isBlank() {
                        for (let view = topView; view; view = view[nextSym]) {
                            if (!view.isBlank) {
                                return false;
                            }
                        }
                        return true;
                    },

                    get pieces() {
                        const pieces = [];
                        for (let view = topView; view; view = view[nextSym]) {
                            pieces.push(view[pieceSym]);
                        }
                        return pieces;
                    },

                    addTop: function(piece) {
                        return async(function*() {
                            const pieceView = yield insertPieceView(piece.createFragment(), element, 0);
                            if (topView) topView[previousSym] = pieceView;
                            pieceView[nextSym] = topView;
                            pieceView[pieceSym] = piece;
                            topView = pieceView;
                            if (length == 0) bottomView = pieceView;
                            length++;
                        });
                    },

                    addBottom: function(piece) {
                        return async(function*() {
                            const pieceView = yield insertPieceView(piece.createFragment(), element, element.childNodes.length, pageHeight);
                            if (bottomView) bottomView[nextSym] = pieceView;
                            pieceView[previousSym] = bottomView;
                            pieceView[pieceSym] = piece;
                            bottomView = pieceView;
                            if (length == 0) topView = pieceView;
                            length++;
                        });
                    },

                    removeTop: function() {
                        assert(length > 0, "Should contains piece views");
                        element.removeChild(element.firstChild);
                        topView = topView[nextSym];
                        if (topView) topView[previousSym] = undefined;
                        length--;
                        if (length == 0) bottomView = undefined;
                    },

                    removeBottom: function() {
                        assert(length > 0, "Should contains piece views");
                        element.removeChild(element.lastChild);
                        bottomView = bottomView[previousSym];
                        if (bottomView) bottomView[nextSym] = undefined;
                        length--;
                        if (length == 0) topView = undefined;
                    },
                };
            }

            function bookPercentToRowIndex(pieceView, bookPercent) {
                const piece = pieceView[pieceSym];
                assert(bookPercent >= piece.beginPercent && bookPercent <= piece.endPercent, "wrong pieceView percents");
                if (piece.endPercent > piece.beginPercent) {
                    const piecePercent = (bookPercent - piece.beginPercent) / (piece.endPercent - piece.beginPercent);
                    return Math.round(pieceView.length * piecePercent);
                } else {
                    return 0;
                }
            }

            function rowIndexToBookPercent(pieceView, rowIndex) {
                const piece = pieceView[pieceSym];
                const piecePercent = pieceView.length > 0 ? rowIndex / pieceView.length : 0.0;
                return piece.beginPercent + (piece.endPercent - piece.beginPercent) * piecePercent;
            }

            function rowIndexAbove(pieceView, screenBottom) {
                const pieceScreenTop = pieceView.boundingClientRect.top;
                for (let i = 0; i < pieceView.length; i++) {
                    const rowScreenBottom = pieceScreenTop + pieceView.rowBottom(i);
                    if (rowScreenBottom > screenBottom) {
                        return i;
                    }
                }
                return pieceView.length;
            }

            function rowIndexBelow(pieceView, screenTop) {
                const pieceScreenTop = pieceView.boundingClientRect.top;
                for (let i = pieceView.length - 1; i >=0; i--) {
                    const rowScreenTop = pieceScreenTop + pieceView.rowTop(i);
                    if (rowScreenTop < screenTop) {
                        return i + 1;
                    }
                }
                return 0;
            }
        },
    };
});
