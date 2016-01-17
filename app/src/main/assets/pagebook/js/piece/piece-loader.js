"use strict";

define("piece/piece-loader", function() {
    const LOAD_TIMEOUT = 8000;

    return {
        createPieceLoader: function() {
            const PromiseUtils = require("utils/promise");
            const ContentInfo = require("piece/content-info");
            const Importer = require("piece/importer");
            const Calculator = require("piece/calculator");
            const Transformer = require("piece/transformer");
            const Splitter = require("piece/splitter");

            const async = PromiseUtils.async.bind(PromiseUtils);
            const importContent = Importer.importContent.bind(Importer);
            const calculateEdges = Calculator.calculateEdges.bind(Calculator);
            const cleanNode = Transformer.cleanNode.bind(Transformer);
            const splitIntoPieces = Splitter.splitIntoPieces.bind(Splitter);

            return {
                loadPieces: function(src, beginPercent, endPercent) {
                    return new Promise(function(resolve) {
                        const frame = document.createElement("iframe");
                        frame.style.display = "none";
                        document.body.appendChild(frame);

                        let isLoaded = false;
                        let isError = false;

                        frame.onload = function() {
                            if (!isError) {
                                const body = importContent(frame.contentWindow.document);

                                calculateEdges(body, beginPercent, endPercent);
                                cleanNode(body);
                                resolve(splitIntoPieces(body));

                                document.body.removeChild(frame);
                                isLoaded = true;
                            }
                        };
                        frame.src = src;

                        setTimeout(function() {
                            if (!isLoaded) {
                                log("pieces not loaded. url: " + src);

                                resolve([]);

                                document.body.removeChild(frame);
                                isError = true;
                            }
                        }, LOAD_TIMEOUT);
                    });
                },
            };
        },
    }
});
