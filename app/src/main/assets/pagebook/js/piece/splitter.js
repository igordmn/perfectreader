"use strict";

define("piece/splitter", function() {
    const Piece = require("piece/piece");
    const ContentInfo = require("piece/content-info");

    const createPieceByNodes = Piece.createPieceByNodes.bind(Piece);

    const JOIN_TO_FLOAT_COUNT = 4;

    return {
        splitIntoPieces: function(rootBlock) {
            const pieces = [];
            const blocks = [];
            collectChildrenBlocks(rootBlock);
            appendPieces();
            return pieces;

            function collectChildrenBlocks(element) {
                for (let child = element.firstChild; child; child = child.nextSibling) {
                    collectLeafBlocks(child);
                }
            }

            function collectLeafBlocks(root) {
                if (root.nodeType == Node.ELEMENT_NODE) {
                    const style = root[ContentInfo.computedStyle];

                    let hasChildren = false;
                    if (canSplit(root, style)) {
                        const oldCount = blocks.length;
                        collectChildrenBlocks(root);
                        hasChildren = blocks.length != oldCount;
                    }

                    if (isBlock(root, style) && !hasChildren) {
                        blocks.push(root);
                    }
                }
            }

            function appendPieces() {
                let needJoinCount = 0;
                let joinFirstNode = undefined;

                let firstNode = rootBlock.firstChild;
                for (let i = 0; i < blocks.length; i++) {
                    const block = blocks[i];

                    if (!isSameBegin(firstNode, block)) append(firstNode, getPreviewNode(block));
                    append(block, block);

                    const float = block[ContentInfo.computedStyle].float;
                    if (float == "left" || float == "right") {
                        if (!joinFirstNode) joinFirstNode = firstNode;
                        needJoinCount = JOIN_TO_FLOAT_COUNT;
                    }

                    firstNode = getNextNode(block);
                }
                if (firstNode) append(firstNode, rootBlock.lastChild);

                function append(firstNode, lastNode) {
                    if (needJoinCount > 0) {
                        pieces[pieces.length - 1] = createPieceByNodes(rootBlock, joinFirstNode, lastNode);
                        needJoinCount--;
                    } else {
                        joinFirstNode = undefined;
                        pieces.push(createPieceByNodes(rootBlock, firstNode, lastNode));
                    }
                }
            }

            function getNextNode(node) {
                return node.nextSibling || (node.parentNode ? getNextNode(node.parentNode) : undefined);
            }

            function getPreviewNode(node) {
                return node.previousSibling || (node.parentNode ? getPreviewNode(node.parentNode) : undefined);
            }

            function isSameBegin(node1, node2) {
                if (node1 == node2) {
                    return true;
                } else if (node1 == undefined || node2 == undefined) {
                    return false;
                } else {
                    return isFirstChild(node1, node2) || isFirstChild(node2, node1);
                }
            }

            function isFirstChild(parent, node) {
                let nd = node;
                while (nd && nd.parentNode && nd != parent) {
                    const pr = nd.parentNode;
                    const firstChild = pr.firstChild;
                    if (pr == parent && firstChild == nd) {
                        return true;
                    } else if (firstChild != nd) {
                        return false;
                    }
                    nd = pr;
                }
                return false;
            }
        },
    };

    function isBlock(element, style) {
        return !style.display.startsWith("inline");
    }

    function canSplit(element, style) {
        const nodeName = element.nodeName.toLowerCase();
        const allowedName = nodeName != "svg" && nodeName != "video";
        const allowedDisplay = style.display == "block" || style.display == "inline";
        const allowedPosition = style.position == "static" || (style.position == "relative" && isZeroPosition(style));
        const allowedStyle =
                        style.float == "none" &&
                        style.transform == "none" &&
                        style.columns == "auto auto" &&
                        !style.whiteSpace.startsWith("pre") &&
                        (style.writingMode == "lr-tb" || style.writingMode == "horizontal-tb");
        return allowedName && allowedDisplay && allowedPosition && allowedStyle;
    }

    function isZeroPosition(style) {
        return (style.left == "auto" || style.left == "0px") && (style.top == "auto" || style.top == "0px");
    }
});
