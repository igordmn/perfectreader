"use strict";

define("piece-view", function() {
    const PromiseUtils = require("utils/promise");
    const ElementUtils = require("utils/element");

    const min = Math.min.bind(Math);
    const max = Math.max.bind(Math);
    const async = PromiseUtils.async.bind(PromiseUtils);
    const documentReady = ElementUtils.documentReady.bind(ElementUtils);

    class Box {
        constructor(rootTop, rect, isLeaf) {
            this.top = rect.top - rootTop;
            this.bottom = rect.bottom - rootTop;
            this.isLeaf = isLeaf;
            this.isVisible = true;
        }
    }

    class BlockBox extends Box {
        constructor(element, rootTop, rect, isLeaf) {
            super(rootTop, rect, isLeaf);
            this.element = element;
            this.elementOriginalVisibility = element.style.visibility;
            this.lineChildren = [];
            this.children = [];
        }
    }

    return {
        /**
         * pageHeight - for elements with percent height
         */
        insert: function(pieceFragment, container, index, pageHeight) {
            return async(function*() {
                const contentContainer = document.createElement("div");
                contentContainer.appendChild(pieceFragment);

                const pageHeightContainer = document.createElement("div");
                pageHeightContainer.style.height = pageHeight + "px";
                pageHeightContainer.appendChild(contentContainer);

                const boundingContainer = document.createElement("div");
                boundingContainer.style.position = "relative";
                boundingContainer.style.top = "0px";
                boundingContainer.style.clear = "both";
                boundingContainer.appendChild(pageHeightContainer);

                if (index < container.childNodes.length) {
                    container.insertBefore(boundingContainer, container.childNodes[index]);
                } else {
                    container.appendChild(boundingContainer);
                }

                boundingContainer.style.height = contentContainer.clientHeight + "px";

                yield documentReady(document, 10000);

                let prepared = false;
                let rootBox, rows;
                let beginIndex, endIndex;

                return {
                    get length() { checkPrepared(); return rows.length },
                    get beginIndex() { checkPrepared(); return beginIndex },
                    get endIndex() { checkPrepared(); return endIndex },
                    get isBlank() { return this.boundingClientRect.height == 0 },
                    get isCollapsed() { checkPrepared(); return beginIndex >= endIndex },
                    get boundingClientRect() { return boundingContainer.getBoundingClientRect(); },

                    set beginIndex(value) {
                        checkPrepared();
                        assert(value >= 0 && value <= rows.length, "wrong index");
                        beginIndex = value;
                        refresh();
                    },

                    set endIndex(value) {
                        checkPrepared();
                        assert(value >= 0 && value <= rows.length, "wrong index");
                        endIndex = value;
                        refresh();
                    },

                    rowTop: function(index) {
                        checkPrepared();
                        assert(index >= 0 && index < rows.length, "wrong index");
                        return rows[index].top;
                    },

                    rowBottom: function(index) {
                        checkPrepared();
                        assert(index >= 0 && index < rows.length, "wrong index");
                        return rows[index].bottom;
                    },
                };

                function checkPrepared() {
                    if (!prepared) {
                        rootBox = analyzeBoxTree(contentContainer, boundingContainer.getBoundingClientRect().top);
                        rows = analyzeRows(rootBox);

                        beginIndex = 0;
                        endIndex = rows.length;

                        prepared = true;
                    }
                }

                function refresh() {
                    for (let i = 0; i < rows.length; i++) {
                        const row = rows[i];
                        for (let j = 0; j < row.boxes.length; j++) {
                            const box = row.boxes[j];
                            box.isVisible = i >= beginIndex && i < endIndex;
                        }
                    }

                    if (rows.length > 0) {
                        const top = beginIndex < rows.length ? rows[beginIndex].top : rows[rows.length - 1].bottom;
                        const bottom = endIndex > beginIndex ? rows[endIndex - 1].bottom : top;

                        refreshVisibility(rootBox);
                        refreshOffset(top, bottom);
                    }

                    function refreshVisibility(root) {
                        walkTree(root, function(box) {
                            if (box instanceof BlockBox) {
                                const style = box.element.style;

                                if (box.isVisible) {
                                    style.visibility = box.elementOriginalVisibility;
                                } else {
                                    style.visibility = "hidden";
                                }

                                const lineCount = box.lineChildren.length;
                                if (lineCount > 0) {
                                    let hiddenLinesTop = lineCount;
                                    let hiddenLinesBottom = lineCount;
                                    for (let i = 0; i < lineCount; i++) {
                                        const lineBox = box.lineChildren[i];
                                        if (lineBox.isVisible && i < hiddenLinesTop) {
                                            hiddenLinesTop = i;
                                        }
                                        if (lineBox.isVisible && lineCount - i - 1 < hiddenLinesBottom) {
                                            hiddenLinesBottom = lineCount - i - 1;
                                        }
                                    }
                                    style.typoHiddenLinesTop = hiddenLinesTop;
                                    style.typoHiddenLinesBottom = hiddenLinesBottom;
                                } else {
                                    style.typoHiddenLinesTop = "";
                                    style.typoHiddenLinesBottom = "";
                                }
                            }
                        });
                    }

                    function refreshOffset(top, bottom) {
                        boundingContainer.style.top = -top + "px";
                        boundingContainer.style.height = (bottom - top) + "px";
                    }
                }
            });
        },
    }

    function analyzeBoxTree(rootBlock, rootTop, csTime) {
        const box = new BlockBox(rootBlock, rootTop, rootBlock.getBoundingClientRect(), false);
        addChildLines(rootBlock, box);
        addChildBlocks(rootBlock, box);
        return box;

        function createBoxFor(node, parentBox) {
            if (node.nodeType == Node.ELEMENT_NODE) {
                const rect = node.getBoundingClientRect();
                const style = getComputedStyle(node);
                if (isVisualBlock(style, rect)) {
                    if (canContainChildren(node, style)) {
                        const box = new BlockBox(node, rootTop, rect, false);
                        addChildLines(node, box);
                        addChildBlocks(node, box);
                        parentBox.children.push(box);
                    } else {
                        parentBox.children.push(new BlockBox(node, rootTop, rect, true));
                    }
                } else if (style.display == "inline") {
                    if (canContainChildren(node, style)) {
                        addChildBlocks(node, parentBox);
                    }
                }
            }
        }

        function addChildLines(element, parentBox) {
            const lineRects = element.typoGetLineRects();
            for (let i = 0; i < lineRects.length; i++) {
                const lineChild = new Box(rootTop, lineRects[i], true);
                parentBox.lineChildren.push(lineChild);
                parentBox.children.push(lineChild);
            }
        }

        function addChildBlocks(element, parentBox) {
            const childNodes = element.childNodes;
            for (let i = 0; i < childNodes.length; i++) {
                createBoxFor(childNodes[i], parentBox);
            }
        }

        function isVisualBlock(style, rect) {
            const display = style.display;

            const isRectVisible = rect.width > 0 || rect.height > 0;;
            const allowedDisplay =
                    display == "block" || display == "list-item" ||
                    display == "flex" || display == "grid" ||
                    display == "table" || display == "table-caption" ||
                    display == "table-cell" || display == "table-row" ||
                    display == "table-footer-group" || display == "table-header-group" ||
                    display == "table-row-group";

            return isRectVisible && allowedDisplay;
        }

        function canContainChildren(element, style) {
            const nodeName = element.nodeName.toLowerCase();
            const allowedName = nodeName != "svg" &&
                      nodeName != "video" &&
                      nodeName != "img" &&
                      nodeName != "br";
            const allowedStyle =
                    style.transform == "none" && style.webkitColumns == "auto auto" &&
                    (style.writingMode == "lr-tb" || style.writingMode == "horizontal-tb");
            return allowedName && allowedStyle;
        }
    }

    function analyzeRows(root) {
        const leafs = [], containers = [], rows = [];
        collectLeafsAndContainers(root, leafs, containers);
        combineIntoRows(rows, leafs);
        expandToContainers(rows, containers);
        return rows;

        function collectLeafsAndContainers(root, leafs, containers) {
            const initLeafs = [], initContainers = [];
            walkTree(root, function(box) {
                if (box.isLeaf) {
                    initLeafs.push(box);
                } else {
                    initContainers.push(box);
                }
            });
            moveWidowContainersToLeafs(initLeafs, initContainers, leafs, containers);
        }

        /* Widow container - container that intersects no leaf and hasn't children */
        function moveWidowContainersToLeafs(initLeafs, initContainers, leafs, containers) {
            initLeafs.sort(function(a, b) { return a.top - b.top || a.bottom - b.bottom });
            initContainers.sort(function(a, b) { return a.top - b.top || a.bottom - b.bottom });

            Array.prototype.push.apply(leafs, initLeafs);

            let l = 0;
            for (let i = 0; i < initContainers.length; i++) {
                const container = initContainers[i];
                if (container.children.length == 0) {
                    l = itemWithBottomBelow(initLeafs, l, container.top);
                    const isIntersectsLeafs = l < initLeafs.length && container.bottom > initLeafs[l].top;
                    if (isIntersectsLeafs) {
                        containers.push(container);
                    } else {
                        leafs.push(container);
                    }
                } else {
                    containers.push(container);
                }
            }
        }

        // See {@link doc-files/combineIntoRows.png}
        function combineIntoRows(rows, leafs) {
            leafs.sort(function(a, b) { return a.top - b.top || b.bottom - a.bottom });

            let rowBoxes = [];
            for (let i = 0; i < leafs.length; i++) {
                const box = leafs[i];
                if (rowBoxes.length > 0 && box.bottom > rowBoxes[0].bottom) {
                    rows.push(createRow(rowBoxes));
                    rowBoxes = [];
                }
                rowBoxes.push(box);
            }
            if (rowBoxes.length > 0) rows.push(createRow(rowBoxes));

            assert(isRowsValid(rows), "Row sorting is wrong");
        }

        /**
         * For every container.top, find closest greater row.top, and if row intersects container - expand row.top to container.top
         * For every container.bottom, find closest less row.bottom, and if row intersects container - expand row.bottom to container.bottom
         */
        function expandToContainers(rows, containers) {
            containers.sort(function(a, b) { return a.top - b.top });
            let r = 0;
            for (let i = 0; i < containers.length; i++) {
                const container = containers[i];
                r = itemWithBottomBelow(rows, r, container.top);
                const row = r < rows.length ? rows[r] : undefined;
                if (row && row.top > container.top && row.top < container.bottom) {
                    row.top = container.top;
                }
            }

            containers.sort(function(a, b) { return b.bottom - a.bottom });
            r = rows.length - 1;
            for (let i = 0; i < containers.length; i++) {
                const container = containers[i];
                r = itemWithTopAbove(rows, r, container.bottom);
                const row = r >= 0 ? rows[r] : undefined;
                if (row && row.bottom > container.top && row.bottom < container.bottom) {
                    row.bottom = container.bottom;
                }
            }

            assert(isRowsValid(rows), "Row sorting is wrong");
        }

        function itemWithBottomBelow(items, minIndex, offset) {
            for (let i = minIndex; i < items.length; i++) {
                if (items[i].bottom > offset) return i;
            }
            return items.length;
        }

        function itemWithTopAbove(items, maxIndex, offset) {
            for (let i = maxIndex; i >= 0; i--) {
                if (items[i].top < offset) return i;
            }
            return -1;
        }
    }

    function isRowsValid(rows) {
        for (let i = 1; i < rows.length; i++) {
            if (rows[i].top < rows[i-1].top || rows[i].bottom < rows[i-1].bottom) {
                return false;
            }
        }
        return true;
    }

    function walkTree(root, callback) {
        callback(root);
        if (root instanceof BlockBox) {
            for (let i = 0; i < root.children.length; i++) {
                const child = root.children[i];
                walkTree(child, callback);
            }
        }
    }

    function createRow(boxes) {
        assert(boxes.length > 0);

        let minTop = Number.MAX_VALUE;
        let maxBottom = -Number.MAX_VALUE;
        for (let i = 0; i < boxes.length; i++) {
            const box = boxes[i];
            if (box.top < minTop) minTop = box.top;
            if (box.bottom > maxBottom) maxBottom = box.bottom;
        }

        return {
            boxes: boxes,
            top: minTop,
            bottom: maxBottom,
        };
    }
});
