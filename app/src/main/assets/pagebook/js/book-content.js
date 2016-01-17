"use strict";

define("book-content", function() {
    const SearchUtils = require("utils/search");
    const PromiseUtils = require("utils/promise");
    const Piece = require("piece/piece");
    const PieceLoader = require("piece/piece-loader");

    const binarySearch = SearchUtils.binarySearch.bind(SearchUtils);
    const isLowerOrEquals = SearchUtils.isLowerOrEquals.bind(SearchUtils);
    const async = PromiseUtils.async.bind(PromiseUtils);
    const createEmptyPiece = Piece.createEmptyPiece.bind(Piece);
    const createPieceLoader = PieceLoader.createPieceLoader.bind(PieceLoader);

    const segmentIndexSym = Symbol();
    const piecesLengthSym = Symbol();
    const pieceIndexSym = Symbol();

    return {
        createBookContent: function(bookSource) {
            assert(bookSource.segments.length > 0, "bookSource.segments.length is wrong");

            const pieceLoader = createPieceLoader();
            const segments = createSegments(bookSource);

            return {
                getPieceAt: function(percent) {
                    return async(function*() {
                        const segmentIndex = indexOfPercent(segments, percent);
                        const segment = segments[segmentIndex];

                        const pieces = yield segment.pieces.acquireRef();
                        const pieceIndex = indexOfPercent(pieces, percent);
                        return pieces[pieceIndex];
                    });
                },

                getNextPiece: function(piece) {
                    return async(function*() {
                        const segmentIndex = piece[segmentIndexSym];
                        const pieceIndex = piece[pieceIndexSym];
                        const segment = segments[segmentIndex];
                        const piecesLength = piece[piecesLengthSym];

                        if (pieceIndex < piecesLength - 1) {
                            const pieces = yield segment.pieces.acquireRef();
                            return pieces[pieceIndex + 1];
                        } else {
                            const nextSegment = segments[segmentIndex + 1];
                            const pieces = yield nextSegment.pieces.acquireRef();
                            return pieces[0];
                        }
                    });
                },

                getPreviousPiece: function(piece) {
                    return async(function*() {
                        const segmentIndex = piece[segmentIndexSym];
                        const pieceIndex = piece[pieceIndexSym];
                        const segment = segments[segmentIndex];

                        if (pieceIndex > 0) {
                            const pieces = yield segment.pieces.acquireRef();
                            return pieces[pieceIndex - 1];
                        } else {
                            const previousSegment = segments[segmentIndex - 1];
                            const pieces = yield previousSegment.pieces.acquireRef();
                            return pieces[pieces.length - 1];
                        }
                    });
                },

                hasNextPiece: function(piece) {
                    const segmentIndex = piece[segmentIndexSym];
                    const pieceIndex = piece[pieceIndexSym];
                    const segment = segments[segmentIndex];
                    const piecesLength = piece[piecesLengthSym];
                    return pieceIndex < piecesLength - 1 || segmentIndex < segments.length - 1;
                },

                hasPreviousPiece: function(piece) {
                    const segmentIndex = piece[segmentIndexSym];
                    const pieceIndex = piece[pieceIndexSym];
                    return pieceIndex > 0 || segmentIndex > 0;
                },

                releasePiece: function(piece) {
                    const segment = segments[piece[segmentIndexSym]];
                    segment.pieces.releaseRef();
                },

                isSegmentLoaded: function(segmentIndex) {
                    return segments[segmentIndex].pieces.isLoaded();
                }
            };

            function createSegments(bookSource) {
                const segments = [];

                let totalSize = 0;
                for (let info of bookSource.segments) {
                    totalSize += info.sizeInBytes;
                }

                let beginPos = 0, endPos = 0;
                for (let i = 0; i < bookSource.segments.length; i++) {
                    const info = bookSource.segments[i];
                    if (info.sizeInBytes > 0) {
                        let endPos = beginPos + info.sizeInBytes;
                        const beginPercent = beginPos / totalSize;
                        const endPercent = endPos / totalSize;
                        segments[i] = {
                            beginPercent: beginPercent,
                            endPercent: endPercent,
                            pieces: createCacheValue(loadSegmentPieces.bind(null, info.src, beginPercent, endPercent, i)),
                        };
                        beginPos = endPos;
                    }
                }

                // it's needed because percents are float numbers and values may be not accurate
                segments[0].beginPercent = 0.0;
                segments[segments.length - 1].endPercent = 1.0;

                return segments;
            }

            function loadSegmentPieces(src, beginPercent, endPercent, segmentIndex) {
                return async(function*() {
                    const pieces = yield pieceLoader.loadPieces(src, beginPercent, endPercent);

                    if (pieces.length == 0) {
                        pieces.push(createEmptyPiece(beginPercent, endPercent));
                    }

                    // it's needed because whitespaces can be reduced from pieces therefore pieces can be started not from segment begin
                    pieces[0].beginPercent = beginPercent;
                    pieces[pieces.length - 1].endPercent = endPercent;
                    for (let i = 1; i < pieces.length; i++) {
                        pieces[i].beginPercent = pieces[i - 1].endPercent;
                        assert(pieces[i].beginPercent >= pieces[i - 1].beginPercent, "Wrong piece order");
                    }

                    for (let i = 0; i < pieces.length; i++) {
                        pieces[i][segmentIndexSym] = segmentIndex;
                        pieces[i][pieceIndexSym] = i;
                        pieces[i][piecesLengthSym] = pieces.length;
                    }

                    return pieces;
                });
            }

            function createCacheValue(loadValue) {
                let refCount = 0;
                let valueReady = undefined;

                return {
                    acquireRef: function() {
                        refCount++;
                        if (!valueReady) {
                            valueReady = loadValue();
                        }
                        return valueReady;
                    },

                    releaseRef: function() {
                        refCount--;
                        assert(refCount >= 0, "Wrong refCount");

                        if (refCount == 0) {
                            valueReady = undefined;
                            window.gc && window.gc();
                        }
                    },

                    isLoaded: function() {
                        return valueReady != undefined;
                    },
                };
            }

            function indexOfPercent(items, percent) {
                return binarySearch(0, items.length - 1, isLowerOrEquals, percent, function(i) { return items[i].beginPercent });
            }
        },
    };
});
