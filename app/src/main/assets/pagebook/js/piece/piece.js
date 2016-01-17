"use strict";

define("piece/piece", function() {
    const ContentInfo = require("piece/content-info");

    const JOIN_TO_FLOAT_COUNT = 8;

    const firstChildSym = Symbol();
    const lastChildSym = Symbol();
    const isSlicedFirstSym = Symbol();
    const isSlicedLastSym = Symbol();

    return {
        createPieceByNodes: function(rootNode, firstNode, lastNode) {
            assert(lastNode[ContentInfo.endPercent] > firstNode[ContentInfo.beginPercent], "wrong piece percents");
            return {
                beginPercent: firstNode[ContentInfo.beginPercent],
                endPercent: lastNode[ContentInfo.endPercent],

                createFragment: function() {
                    markParents(firstNode, firstChildSym, isSlicedFirstSym, function(parent) { return parent.firstChild });
                    markParents(lastNode, lastChildSym, isSlicedLastSym, function(parent) { return parent.lastChild });
                    return extractClone(rootNode);

                    function markParents(node, childSym, isSlicedSym, getChild) {
                        let isSliced = false;
                        while (node != rootNode) {
                            const parent = node.parentNode;
                            if (getChild(parent) != node) isSliced = true;
                            parent[childSym] = node;
                            parent[isSlicedSym] = isSliced;
                            node = parent;
                        }
                    }

                    function extractClone(node) {
                        let clone = node == rootNode ? document.createDocumentFragment() : node.cloneNode();

                        if (node != rootNode && node.nodeType == Node.ELEMENT_NODE) {
                            const isInline = node[ContentInfo.computedStyle].display == "inline";
                            if (node[isSlicedFirstSym]) setSliced(clone, isInline, true);
                            if (node[isSlicedLastSym]) setSliced(clone, isInline, false);
                        }

                        if (node.hasChildNodes()) {
                            const firstChild = node[firstChildSym] || node.firstChild;
                            const lastChild = node[lastChildSym] || node.lastChild;
                            const endChild = lastChild.nextSibling;
                            for (let child = firstChild; child != endChild; child = child.nextSibling) {
                                clone.appendChild(extractClone(child));
                            }
                        }

                        if (clone.hasChildNodes()) {
                            clone[ContentInfo.beginPercent] = clone.firstChild[ContentInfo.beginPercent];
                            clone[ContentInfo.endPercent] = clone.lastChild[ContentInfo.endPercent];
                        } else {
                            clone[ContentInfo.beginPercent] = node[ContentInfo.beginPercent];
                            clone[ContentInfo.endPercent] = node[ContentInfo.endPercent];
                        }

                        clone[ContentInfo.computedStyle] = node[ContentInfo.computedStyle];

                        node[firstChildSym] = undefined;
                        node[lastChildSym] = undefined;
                        node[isSlicedFirstSym] = undefined;
                        node[isSlicedLastSym] = undefined;

                        return clone;
                    }
                }
            };
        },

        createEmptyPiece: function(beginPercent, endPercent) {
            assert(endPercent > beginPercent, "wrong piece percents");
            return {
                beginPercent: beginPercent,
                endPercent: endPercent,

                createFragment: function() {
                    return document.createDocumentFragment();
                },
            };
        },
    };

    function setSliced(element, isInline, atStart) {
        if (isInline) {
            if (atStart) {
                element.style.marginLeft = "0px";
                element.style.paddingLeft = "0px";
                element.style.borderLeft = "0px";
            } else {
                element.style.marginRight = "0px";
                element.style.paddingRight = "0px";
                element.style.borderRight = "0px";
            }
        } else {
            if (atStart) {
                element.style.marginTop = "0px";
                element.style.paddingTop = "0px";
                element.style.borderTop = "0px";
                element.style.borderTopRightRadius = "0px";
                element.style.borderTopLeftRadius = "0px";
            } else {
                element.style.marginBottom = "0px";
                element.style.paddingBottom = "0px";
                element.style.borderBottom = "0px";
                element.style.borderBottomRightRadius = "0px";
                element.style.borderBottomLeftRadius = "0px";
            }
        }
    }
});
