"use strict";

define("piece/calculator", function() {
    const ContentInfo = require("piece/content-info");

    const min = Math.min.bind(Math);

    const AVERAGE_ELEMENT_LEAF_LENGTH = 32;

    return {
        calculateEdges: function(root, beginPercent, endPercent) {
            this.calcLeafEdges(root, beginPercent, endPercent);
            this.calcParentEdges(root);
        },

        calcLeafEdges: function(root, beginPercent, endPercent) {
            let leafs = [];
            let totalLength = 0;
            collectLeafs(root);

            const distance = endPercent - beginPercent;
            let leafBegin = beginPercent;
            for (let i = 0; i < leafs.length; i++) {
                const leaf = leafs[i];
                const weight = getLength(leaf) / totalLength;
                let leafEnd = min(leafBegin + weight * distance, endPercent);
                leaf[ContentInfo.beginPercent] = leafBegin;
                leaf[ContentInfo.endPercent] = leafEnd;
                leafBegin = leafEnd;
            }

            function collectLeafs(root) {
                for (let node = root.firstChild; node; node = node.nextSibling) {
                    collectLeafs(node);
                }

                if (!root.hasChildNodes()) {
                    leafs.push(root);
                    totalLength += getLength(root);
                }
            }

            function getLength(node) {
                return node.nodeType == Node.TEXT_NODE ? node.length : AVERAGE_ELEMENT_LEAF_LENGTH;
            }
        },

        calcParentEdges: function(root) {
            for (let node = root.firstChild; node; node = node.nextSibling) {
                this.calcParentEdges(node);
            }

            if (root.hasChildNodes()) {
                root[ContentInfo.beginPercent] = root.firstChild[ContentInfo.beginPercent];
                root[ContentInfo.endPercent] = root.lastChild[ContentInfo.endPercent];
            }
        },
    };
});
