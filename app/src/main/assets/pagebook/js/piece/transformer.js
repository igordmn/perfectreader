"use strict";

define("piece/transformer", function() {
    const StyleUtils = require("utils/style");
    const ContentInfo = require("piece/content-info");

    return {
        cleanNode: function(root) {
            const toRemove = [];

            for (let node = root.firstChild; node; node = node.nextSibling) {
                if (node.nodeType == Node.ELEMENT_NODE) {
                    const style = node[ContentInfo.computedStyle];
                    if (!style.display.startsWith("inline")) {
                        let previewNode = node.previousSibling;
                        let nextNode = node.nextSibling;
                        if (previewNode && previewNode.nodeType == Node.TEXT_NODE && isOnlySpaces(previewNode.nodeValue)) {
                            toRemove.push(previewNode);
                        }
                        if (nextNode && nextNode.nodeType == Node.TEXT_NODE && isOnlySpaces(nextNode.nodeValue)) {
                            toRemove.push(nextNode);
                        }
                    }
                    if (style.display != "none") {
                        this.cleanNode(node);
                    } else {
                        toRemove.push(node);
                    }
                } else if (node.nodeType != Node.TEXT_NODE) {
                    toRemove.push(node);
                }
            }

            for (let i = 0; i < toRemove.length; i++) {
                const node = toRemove[i];
                node.parentNode && node.parentNode.removeChild(node);
            }
        }
    };

    function isOnlySpaces(str) {
        return /^\s*$/.test(str);
    }
});
