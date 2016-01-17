"use strict";

define("utils/element", function() {
    return {
        documentReady: function(document, timeout) {
            return new Promise(function(onResolve) {
                const fontsReady = new Promise(function(onResolve) {
                    let allFontsLoaded = true;
                    document.fonts.forEach(function(fontFace) {
                        const status = fontFace.status;
                        if (status == "unloaded") {
                            fontFace.load();
                        }

                        if (status != "loaded" && status != "error") {
                            allFontsLoaded = false;
                        }
                    });

                    if (allFontsLoaded) { // check for faster resolve (document.fonts.ready is slow)
                        onResolve();
                    } else {
                        document.fonts.ready.then(function() {
                            onResolve();
                        });
                    }
                });
                const imagesReady = new Promise(function(onResolve) {
                    let loadingImages = 0;
                    for (let i = 0; i < document.images.length; i++) {
                        const image = document.images[i];
                        if (image.src != "" && !image.complete) {
                            loadingImages++;

                            const onComplete = function() {
                                --loadingImages == 0 && onResolve();
                            }

                            const originalOnLoad = image.onload && image.onload.bind(image);
                            image.onload = function() {
                                image.onload = originalOnLoad;
                                originalOnLoad && originalOnLoad(arguments);
                                onComplete();
                            }

                            const originalOnError = image.onerror && image.onerror.bind(image);
                            image.onerror = function() {
                                image.onerror = originalOnError;
                                originalOnError && originalOnError(arguments);
                                onComplete();
                            }
                        }
                    }
                    loadingImages == 0 && onResolve();
                });

                const readyPromise = Promise.all([fontsReady, imagesReady]);

                const timeoutPromise = new Promise(function(onResolve) {
                    timeout !== undefined && setTimeout(onResolve, timeout);
                });

                Promise.race([readyPromise, timeoutPromise]).then(function() {
                    onResolve(document);
                });
            });
        },
    };
});
